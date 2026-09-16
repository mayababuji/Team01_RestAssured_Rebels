package stepDefinitions;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.hamcrest.Matchers;
import org.hamcrest.core.Every;
import org.testng.Assert;

import pojo.CreateBatchRequest;
import pojo.CreateBatchResponse;
import pojo.PutBatchRequest;
import pojo.PutBatchResponse;
import specBuilder.RequestSpec;
import utils.*;

public class ProgramBatchStepDef extends SharedTestData {

    private Response response;
    private Map<String, String> data;
    private final ScenarioContext scenarioContext;

    public ProgramBatchStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("Admin sets authorization to Bearer Token")
    public void admin_sets_authorization_to_bearer_token() {
        Boolean skipAuth = (Boolean) scenarioContext.getContext("SKIP_AUTH");

        if (Boolean.TRUE.equals(skipAuth)) {
            return;
        }

        scenarioContext.setRequestSpec(RequestSpec.getRequestSpec());
    }

    // ---------------------------------------------------------
    // CREATE BATCH
    // ---------------------------------------------------------

    @Given("Admin create POST request with valid data for {string} from excel sheet")
    public void admin_create_post_request_with_valid_data_for_from_excel_sheet(String scenario)
            throws IOException {

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is 0. Create a valid program and capture its programId before creating a batch."
            );
        }

        if (SharedTestData.programName == null
                || SharedTestData.programName.isBlank()) {
            throw new IllegalStateException(
                    "programName is null or blank. Create a valid program and capture its programName before creating a batch."
            );
        }

        data = ExcelReader.readExcelData("Batch", scenario);

        ObjectMapper mapper = new ObjectMapper();

        CreateBatchRequest batchData = mapper.readValue(
                data.get("Body"),
                CreateBatchRequest.class
        );

        batchData.setProgramId(SharedTestData.programId);

        String generatedBatchName = SharedTestData.programName
                + "_"
                + TestDataUtil.randomNumericSuffix(3);

        batchData.setBatchName(generatedBatchName);

        // Do not call batchData.setProgramName(...).
        // The CreateBatchRequest POJO has no programName property/setter.

        SharedTestData.batchName = generatedBatchName;

        System.out.println("===== BATCH TEST DATA =====");
        System.out.println("programId: " + SharedTestData.programId);
        System.out.println("programName: " + SharedTestData.programName);
        System.out.println("batchName: " + SharedTestData.batchName);
        System.out.println("===========================");

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(data.get("Endpoint"))
                .body(batchData);

        scenarioContext.setRequestSpec(requestSpec);
        scenarioContext.setContext("BATCH_NAME", generatedBatchName);
    }

    @When("Admin sends POST request to create program batch")
    public void admin_sends_post_request_to_create_program_batch() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException(
                    "requestSpec is null. Ensure the batch POST Given step ran before this When step."
            );
        }

        response = requestSpec
                .when()
                .log()
                .all()
                .post();
    }

    @Then("Admin receives created status with response body")
    public void admin_receives_created_status_with_response_body() {
        response.then()
                .log()
                .all()
                .statusCode(Integer.parseInt(data.get("ExpectedStatusCode")))
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/CreateBatchResponseSchema.json"
                ));

        CreateBatchResponse batchResponse = response.as(CreateBatchResponse.class);

        if (batchResponse.getBatchId() <= 0) {
            throw new IllegalStateException(
                    "Batch creation response did not return a valid batchId."
            );
        }

        SharedTestData.batchId = batchResponse.getBatchId();
        SharedTestData.batchName = batchResponse.getBatchName();

        if (!SharedTestData.batchIds.contains(batchResponse.getBatchId())) {
            SharedTestData.batchIds.add(batchResponse.getBatchId());
        }

        String expectedBatchName =
                (String) scenarioContext.getContext("BATCH_NAME");

        Assert.assertEquals(
                batchResponse.getBatchName(),
                expectedBatchName,
                "Batch Name doesn't match in response"
        );

        System.out.println("===== BATCH DATA CAPTURED =====");
        System.out.println("batchId: " + SharedTestData.batchId);
        System.out.println("batchName: " + SharedTestData.batchName);
        System.out.println("================================");
    }

    @Given("Admin create POST request with invalid input for {string} from excel sheet")
    public void admin_create_post_request_with_invalid_input_for_from_excel_sheet(
            String scenario) throws IOException {

        data = ExcelReader.readExcelData("Batch", scenario);

        RequestSpecification requestSpec;

        if (scenario.contains("NoAuth")) {
            requestSpec = given()
                    .spec(RequestSpec.getRequestSpecWithoutAuth())
                    .basePath(data.get("Endpoint"))
                    .body(data.get("Body"));
        } else {
            requestSpec = given()
                    .spec(RequestSpec.getRequestSpec())
                    .basePath(data.get("Endpoint"))
                    .body(data.get("Body"));
        }

        scenarioContext.setRequestSpec(requestSpec);
    }

    // ---------------------------------------------------------
    // COMMON ERROR RESPONSE VALIDATION
    // ---------------------------------------------------------

    @Then("Admin receives expected status code with error message")
    public void admin_receives_expected_status_code_with_error_message() {

        if (response == null) {
            throw new IllegalStateException(
                    "response is null. Ensure the request When step ran first."
            );
        }

        int expectedStatus =
                Integer.parseInt(data.get("ExpectedStatusCode"));

        String expectedMessage =
                data.get("ExpectedMessage");

        response.then()
                .log()
                .all()
                .statusCode(expectedStatus);


        if (expectedMessage == null || expectedMessage.isBlank()) {
            System.out.println(
                    "ExpectedMessage is blank; status-code validation is complete."
            );
            return;
        }

        String body = response.asString();

        if (body == null || body.isBlank()) {
            Assert.fail(
                    "Expected error message: '" + expectedMessage
                            + "', but API returned an empty response body."
            );
        }

        String actualMessage =
                ResponseValidator.extractErrorMessage(response);

        System.out.println("Expected message: " + expectedMessage);
        System.out.println("Actual message: " + actualMessage);

        Assert.assertNotNull(
                actualMessage,
                "Expected error message: '" + expectedMessage
                        + "', but no message could be extracted from the response."
        );

        Assert.assertTrue(
                actualMessage.contains(expectedMessage),
                "Error message doesn't match. Expected text: '"
                        + expectedMessage
                        + "', actual message: '"
                        + actualMessage
                        + "'"
        );
    }
    // ---------------------------------------------------------
    // GET ALL BATCHES
    // ---------------------------------------------------------

    @Given("Admin create GET request with valid endpoint")
    public void admin_create_get_request_with_valid_endpoint() throws IOException {
        data = ExcelReader.readExcelData("Batch", "GetAll_Valid_Batches");

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(data.get("Endpoint"));

        scenarioContext.setRequestSpec(requestSpec);
    }

    @When("Admin sends GET request to retrieve all batches")
    public void admin_sends_get_request_to_retrieve_all_batches() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException(
                    "requestSpec is null. Ensure the GET-all Given step ran first."
            );
        }

        response = requestSpec
                .when()
                .log()
                .all()
                .get();
    }

    @Then("Admin receives success code with response body")
    public void admin_receives_success_code_with_response_body() {
        int expectedStatusCode =
                Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/GetAllBatchesResponseSchema.json"
                ))
                .body("", Matchers.instanceOf(List.class))
                .body("size()", Matchers.greaterThan(0));

        JsonPath json = response.jsonPath();

        List<Map<String, Object>> batches = json.getList("$");

        for (Integer createdBatchId : SharedTestData.batchIds) {
            boolean batchExists = batches.stream()
                    .map(batch -> ((Number) batch.get("batchId")).intValue())
                    .anyMatch(responseBatchId ->
                            responseBatchId.equals(createdBatchId)
                    );

            Assert.assertTrue(
                    batchExists,
                    "Created batch ID was not found in GET-all response: "
                            + createdBatchId
            );
        }
    }

    @Given("Admin create GET request with invalid endpoint")
    public void admin_create_get_request_with_invalid_endpoint() throws IOException {
        data = ExcelReader.readExcelData(
                "Batch",
                "GetAll_Batches_InvalidEndpoint"
        );

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(data.get("Endpoint"));

        scenarioContext.setRequestSpec(requestSpec);
    }

    @Given("Admin create GET request with no authentication")
    public void admin_create_get_request_with_no_authentication() throws IOException {
        data = ExcelReader.readExcelData(
                "Batch",
                "GetAll_Batches_With_NoAuth"
        );

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpecWithoutAuth())
                .basePath(data.get("Endpoint"));

        scenarioContext.setRequestSpec(requestSpec);
    }

    // ---------------------------------------------------------
    // GET BATCH BY VALID BATCH ID
    // ---------------------------------------------------------

    @Given("Admin create GET request with valid batchId")
    public void admin_create_get_request_with_valid_batch_id() throws IOException {
        if (SharedTestData.batchId <= 0) {
            throw new IllegalStateException(
                    "batchId is 0. Create a batch and capture its batchId before GET by batchId."
            );
        }

        data = ExcelReader.readExcelData(
                "Batch",
                "GetAll_BatchById_Valid_BatchId"
        );

        String endpoint = data.get("Endpoint");

        if (!endpoint.contains("{batchId}")) {
            throw new IllegalStateException(
                    "The valid GET-by-batchId Excel endpoint must contain {batchId}. Actual endpoint: "
                            + endpoint
            );
        }

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("batchId", SharedTestData.batchId)
                .basePath(endpoint);

        scenarioContext.setRequestSpec(requestSpec);
    }

    @When("Admin sends GET request to retrieve the batch")
    public void admin_sends_get_request_to_retrieve_the_batch() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException(
                    "requestSpec is null. Ensure the GET-by-batchId Given step ran first."
            );
        }

        response = requestSpec
                .when()
                .log()
                .all()
                .get();
    }

    @Then("Admin receives success code with GET response body")
    public void admin_receives_success_code_with_get_response_body() {
        int expectedStatusCode =
                Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/GetBatchByIdResponseSchema.json"
                ));

        JsonPath json = response.jsonPath();

        Assert.assertEquals(
                json.getInt("batchId"),
                SharedTestData.batchId,
                "Batch Id doesn't match"
        );
    }

    // ---------------------------------------------------------
    // GET BATCH BY INVALID BATCH ID / INVALID ENDPOINT / NO AUTH
    // ---------------------------------------------------------

    @Given("Admin create GET request by BatchId with invalid input for {string} from excel sheet")
    public void admin_create_get_request_by_batch_id_with_invalid_input_for_from_excel_sheet(
            String scenario) throws IOException {

        data = ExcelReader.readExcelData("Batch", scenario);

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: " + scenario
            );
        }

        boolean noAuth = scenario.contains("NoAuth");
        boolean endpointNeedsBatchId = endpoint.contains("{batchId}");

        RequestSpecification requestSpec = given()
                .spec(noAuth
                        ? RequestSpec.getRequestSpecWithoutAuth()
                        : RequestSpec.getRequestSpec());


        if (endpointNeedsBatchId) {
            String testBatchId = data.get("InvalidBatchId");

            if (testBatchId == null || testBatchId.isBlank()) {

                testBatchId = "9999";
            }

            requestSpec = requestSpec
                    .pathParam("batchId", testBatchId);
        }

        requestSpec = requestSpec.basePath(endpoint);

        System.out.println("===== INVALID GET-BY-ID REQUEST =====");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains {batchId}: " + endpointNeedsBatchId);
        System.out.println("======================================");

        scenarioContext.setRequestSpec(requestSpec);
    }

    // ---------------------------------------------------------
// GET BATCH BY VALID BATCH NAME
// ---------------------------------------------------------

    @Given("Admin create GET request to retrieve batch with valid batch name")
    public void admin_create_get_request_to_retrieve_batch_with_valid_batch_name()
            throws IOException {

        if (SharedTestData.batchName == null
                || SharedTestData.batchName.isBlank()) {
            throw new IllegalStateException(
                    "batchName is null or blank. Create a batch and capture its batchName "
                            + "before GET by batch name."
            );
        }

        data = ExcelReader.readExcelData(
                "Batch",
                "GetBatchByName_Valid_BatchName"
        );

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing for GetBatchByName_Valid_BatchName in Excel."
            );
        }

        if (!endpoint.contains("{batchName}")) {
            throw new IllegalStateException(
                    "The valid GET-by-batchName Excel endpoint must contain {batchName}. "
                            + "Actual endpoint: " + endpoint
            );
        }

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("batchName", SharedTestData.batchName)
                .basePath(endpoint);

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("===== GET BATCH BY NAME REQUEST =====");
        System.out.println("batchName: " + SharedTestData.batchName);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("=====================================");
    }

    @Then("Admin receives success code with GET response body having given batch name")
    public void admin_receives_success_code_with_get_response_body_having_given_batch_name() {
        int expectedStatusCode =
                Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/GetBatchByNameResponseSchema.json"
                ))
                .body("", Matchers.instanceOf(List.class))
                .body("size()", Matchers.greaterThan(0))
                .body("batchName", Matchers.hasItem(SharedTestData.batchName));
    }

    // ---------------------------------------------------------
// GET BATCH BY INVALID BATCH NAME / INVALID ENDPOINT / NO AUTH
// ---------------------------------------------------------

    @Given("Admin create GET request by BatchName with invalid input for {string} from excel sheet")
    public void admin_create_get_request_by_batch_name_with_invalid_input_for_from_excel_sheet(
            String scenario) throws IOException {

        data = ExcelReader.readExcelData("Batch", scenario);

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: " + scenario
            );
        }

        boolean noAuth = scenario.contains("NoAuth");
        boolean endpointNeedsBatchName = endpoint.contains("{batchName}");

        RequestSpecification requestSpec = given()
                .spec(noAuth
                        ? RequestSpec.getRequestSpecWithoutAuth()
                        : RequestSpec.getRequestSpec());


        if (endpointNeedsBatchName) {
            String testBatchName = data.get("InvalidBatchName");

            if (testBatchName == null || testBatchName.isBlank()) {
                testBatchName = "InvalidBatchName9999";
            }

            requestSpec = requestSpec
                    .pathParam("batchName", testBatchName);
        }

        requestSpec = requestSpec.basePath(endpoint);

        System.out.println("===== INVALID GET-BY-NAME REQUEST =====");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains {batchName}: " + endpointNeedsBatchName);
        System.out.println("========================================");

        scenarioContext.setRequestSpec(requestSpec);
    }
    // ---------------------------------------------------------
// GET BATCH BY VALID PROGRAM ID
// ---------------------------------------------------------

    @Given("Admin create GET request to retrieve batch with valid programId")
    public void admin_create_get_request_to_retrieve_batch_with_valid_program_id()
            throws IOException {

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is 0. Create a valid program and capture its programId "
                            + "before retrieving batches by programId."
            );
        }

        data = ExcelReader.readExcelData(
                "Batch",
                "GetBatchByProgram_Valid_ProgramId"
        );

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing for GetBatchByProgram_Valid_ProgramId in Excel."
            );
        }

        if (!endpoint.contains("{programId}")) {
            throw new IllegalStateException(
                    "The GET-by-programId Excel endpoint must contain {programId}. "
                            + "Actual endpoint: " + endpoint
            );
        }

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("programId", SharedTestData.programId)
                .basePath(endpoint);

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("===== GET BATCH BY PROGRAM ID REQUEST =====");
        System.out.println("programId: " + SharedTestData.programId);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("===========================================");
    }
    @Then("Admin receives success code with GET response body having given programId")
    public void admin_receives_success_code_with_get_response_body_having_given_program_id() {
        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is 0. A valid program must be created and its programId "
                            + "captured before validating GET batches by programId."
            );
        }

        int expectedStatusCode =
                Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/GetBatchByProgramResponseSchema.json"
                ))
                .body("", Matchers.instanceOf(List.class))
                .body("size()", Matchers.greaterThan(0))
                .body(
                        "programId",
                        //every value in a returned list must match the condition.
                        Every.everyItem(
                                Matchers.equalTo(SharedTestData.programId)
                        )
                );
    }

    // ---------------------------------------------------------
// GET BATCH BY INVALID PROGRAM ID / INVALID ENDPOINT / NO AUTH
// ---------------------------------------------------------

    @Given("Admin create GET request by programId with invalid input for scenario {string} from excel sheet")
    public void admin_create_get_request_by_program_id_with_invalid_input_for_scenario_from_excel_sheet(
            String scenario) throws IOException {

        data = ExcelReader.readExcelData("Batch", scenario);

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: " + scenario
            );
        }

        boolean noAuth = scenario.contains("NoAuth")
                || scenario.contains("No_Auth");

        boolean endpointNeedsProgramId = endpoint.contains("{programId}");

        RequestSpecification requestSpec = given()
                .spec(noAuth
                        ? RequestSpec.getRequestSpecWithoutAuth()
                        : RequestSpec.getRequestSpec());



        if (endpointNeedsProgramId) {
            String testProgramId;


            if (noAuth) {
                if (SharedTestData.programId <= 0) {
                    throw new IllegalStateException(
                            "programId is 0. Create and capture a valid program "
                                    + "before running a GetBatchByProgramId NoAuth scenario."
                    );
                }

                testProgramId = String.valueOf(SharedTestData.programId);

            } else {

                testProgramId = data.get("InvalidProgramId");

                if (testProgramId == null || testProgramId.isBlank()) {
                    testProgramId = "9999";
                }
            }

            requestSpec = requestSpec
                    .pathParam("programId", testProgramId);
        }

        requestSpec = requestSpec.basePath(endpoint);

        System.out.println("===== INVALID GET-BY-PROGRAM-ID REQUEST =====");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains {programId}: " + endpointNeedsProgramId);
        System.out.println("==============================================");

        scenarioContext.setRequestSpec(requestSpec);
    }
    @When("Admin sends GET request to retrieve batches by programId")
    public void admin_sends_get_request_to_retrieve_batches_by_program_id() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException(
                    "requestSpec is null. Ensure the GET-by-programId Given step ran first."
            );
        }

        response = requestSpec
                .when()
                .log()
                .all()
                .get();
    }


// ---------------------------------------------------------
// PUT REQUEST TO UPDATE BATCH
// ---------------------------------------------------------

    @Given("Admin create PUT request to update batch with valid batchId for scenario {string}")
    public void admin_create_put_request_to_update_batch_with_valid_batch_id_for_scenario(
            String scenario) throws IOException {

        data = ExcelReader.readExcelData("Batch", scenario);

        if (data == null) {
            throw new IllegalStateException(
                    "Batch Excel data was not found for scenario: " + scenario
            );
        }

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: " + scenario
            );
        }

        if (!endpoint.contains("{batchId}")) {
            throw new IllegalStateException(
                    "PUT endpoint must contain {batchId}. Actual endpoint: "
                            + endpoint
            );
        }

        if (SharedTestData.batchId <= 0) {
            throw new IllegalStateException(
                    "batchId is invalid: " + SharedTestData.batchId
                            + ". Create a batch before executing the PUT scenario."
            );
        }

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is invalid: " + SharedTestData.programId
                            + ". Create a program before executing the PUT scenario."
            );
        }

        if (SharedTestData.programName == null
                || SharedTestData.programName.isBlank()) {
            throw new IllegalStateException(
                    "programName is null or blank. The Create Program scenario "
                            + "must run successfully before executing the PUT scenario."
            );
        }

        ObjectMapper mapper = new ObjectMapper();

        PutBatchRequest batchData = mapper.readValue(
                data.get("Body"),
                PutBatchRequest.class
        );


        if (scenario.contains("UpdateBatchName")) {

            String numericSuffix = TestDataUtil.randomNumericSuffix(4);

            String updatedBatchName = SharedTestData.programName
                    + "_"
                    + numericSuffix;

            batchData.setBatchName(updatedBatchName);

        } else {

            batchData.setBatchName(SharedTestData.batchName);
        }


        batchData.setProgramId(SharedTestData.programId);
        batchData.setProgramName(SharedTestData.programName);


        batchData.setBatchId(0);

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("batchId", SharedTestData.batchId)
                .basePath(endpoint)
                .body(batchData);

        scenarioContext.setRequestSpec(requestSpec);


        scenarioContext.setContext("BATCH_NAME", batchData.getBatchName());
        scenarioContext.setContext("BATCH_STATUS", batchData.getBatchStatus());
        scenarioContext.setContext(
                "BATCH_NOOFCLASSES",
                batchData.getBatchNoOfClasses()
        );
        scenarioContext.setContext("PROGRAM_ID", batchData.getProgramId());

        System.out.println("============= PUT BATCH PAYLOAD =============");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Path batchId: " + SharedTestData.batchId);
        System.out.println("Batch name: " + batchData.getBatchName());
        System.out.println("Batch description: " + batchData.getBatchDescription());
        System.out.println("Batch status: " + batchData.getBatchStatus());
        System.out.println("Batch number of classes: " + batchData.getBatchNoOfClasses());
        System.out.println("Program ID: " + batchData.getProgramId());
        System.out.println("Program name: " + batchData.getProgramName());
        System.out.println("=============================================");
    }
    @When("Admin sends PUT request to update the batch")
    public void admin_sends_put_request_to_update_the_batch() {

        RequestSpecification requestSpec = scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException(
                    "requestSpec is null. Ensure the PUT Given step ran first."
            );
        }

        response = requestSpec
                .when()
                .log()
                .all()
                .put();
    }
    @Then("Admin received success code with updated ProgramId in response")
    public void admin_received_success_code_with_updated_program_id_in_response() {

        if (response == null) {
            throw new IllegalStateException(
                    "response is null. Ensure the PUT When step ran first."
            );
        }

        int expectedStatusCode =
                Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/PutBatchByIdResponseSchema.json"
                ));

        PutBatchResponse batchResponse =
                response.getBody().as(PutBatchResponse.class);

        int expectedProgramId =
                (int) scenarioContext.getContext("PROGRAM_ID");

        Assert.assertEquals(
                batchResponse.getProgramId(),
                expectedProgramId,
                "Updated programId in response should match the expected programId"
        );
    }

    @Then("Admin received success code with updated batchName in response")
    public void admin_received_success_code_with_updated_batch_name_in_response() {

        if (response == null) {
            throw new IllegalStateException(
                    "response is null. Ensure the PUT When step ran first."
            );
        }

        int expectedStatusCode =
                Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/PutBatchByIdResponseSchema.json"
                ));

        PutBatchResponse batchResponse =
                response.getBody().as(PutBatchResponse.class);

        String expectedBatchName =
                (String) scenarioContext.getContext("BATCH_NAME");

        Assert.assertNotNull(
                expectedBatchName,
                "Expected batchName is missing from ScenarioContext."
        );

        Assert.assertEquals(
                batchResponse.getBatchName(),
                expectedBatchName,
                "Updated batchName in response should match the batchName sent in the PUT payload."
        );
    }

    @Then("Admin received success code with updated batchStatus in response")
    public void admin_received_success_code_with_updated_batch_status_in_response() {
        ResponseValidator.validateBatchPutResponse(
                response,
                data.get("ExpectedStatusCode")
        );

        PutBatchResponse batchResponse =
                response.as(PutBatchResponse.class);

        String expectedStatus =
                (String) scenarioContext.getContext("BATCH_STATUS");

        Assert.assertEquals(
                batchResponse.getBatchStatus(),
                expectedStatus,
                "Updated batchStatus does not match"
        );
    }

    @Then("Admin received success code with updated batchNoOfClasses in response")
    public void admin_received_success_code_with_updated_batch_no_of_classes_in_response() {

        if (response == null) {
            throw new IllegalStateException(
                    "response is null. Ensure the PUT When step ran first."
            );
        }

        int expectedStatusCode =
                Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/PutBatchByIdResponseSchema.json"
                ));

        PutBatchResponse batchResponse =
                response.getBody().as(PutBatchResponse.class);

        Object storedClassCount =
                scenarioContext.getContext("BATCH_NOOFCLASSES");

        if (storedClassCount == null) {
            throw new IllegalStateException(
                    "Expected batchNoOfClasses is missing from ScenarioContext."
            );
        }

        int expectedBatchNoOfClasses;

        if (storedClassCount instanceof Integer) {
            expectedBatchNoOfClasses = (Integer) storedClassCount;

        } else if (storedClassCount instanceof Number) {
            expectedBatchNoOfClasses =
                    ((Number) storedClassCount).intValue();

        } else {
            try {
                expectedBatchNoOfClasses =
                        Integer.parseInt(storedClassCount.toString());
            } catch (NumberFormatException e) {
                throw new IllegalStateException(
                        "BATCH_NOOFCLASSES is not a valid integer. Actual value: "
                                + storedClassCount,
                        e
                );
            }
        }

        Assert.assertEquals(
                batchResponse.getBatchNoOfClasses(),
                expectedBatchNoOfClasses,
                "Updated batchNoOfClasses in response should match the value sent in the PUT payload."
        );
    }
    @Given("Admin create PUT request with invalid input for each {string} from excel sheet")
    public void admin_create_put_request_with_invalid_input_for_each_from_excel_sheet(
            String scenario) throws IOException {

        data = ExcelReader.readExcelData("Batch", scenario);

        if (data == null) {
            throw new IllegalStateException(
                    "Batch data was not found in Excel for scenario: " + scenario
            );
        }

        String endpoint = data.get("Endpoint");
        String requestBody = data.get("Body");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: " + scenario
            );
        }

        if (requestBody == null || requestBody.isBlank()) {
            throw new IllegalStateException(
                    "Body is missing in Excel for scenario: " + scenario
            );
        }

        boolean noAuth = scenario.contains("NoAuth")
                || scenario.contains("No_Auth");

        boolean endpointNeedsBatchId = endpoint.contains("{batchId}");

        Object requestBodyForRequest = requestBody;

        if (noAuth) {
            requestBodyForRequest =
                    BatchRequestUtil.createValidBatchJson(requestBody);


        } else if (scenario.contains("Missing_Mandatory_Fields")) {
            requestBodyForRequest =
                    BatchRequestUtil.createValidBatchJsonWithOverrides(
                            requestBody,
                            null,
                            "Active"
                    );


        } else if (scenario.contains("Invalid_BatchStatus")) {
            requestBodyForRequest =
                    BatchRequestUtil.createValidBatchJsonWithOverrides(
                            requestBody,
                            1,
                            "act"
                    );


        } else if (scenario.contains("Invalid_BatchId")) {
            requestBodyForRequest =
                    BatchRequestUtil.createValidBatchJson(requestBody);


        } else if (scenario.contains("Invalid_ProgramId")) {
            requestBodyForRequest =
                    BatchRequestUtil.createBatchJsonForSpecificProgram(
                            requestBody,
                            9999,
                            "Invalid"
                    );

        } else if (scenario.contains("Deleted_programId")) {
            requestBodyForRequest =
                    BatchRequestUtil.createBatchJsonForSpecificProgram(
                            requestBody,
                            70,
                            "pithongjagijjjngRe"
                    );


        } else if (scenario.contains("Invalid_NoOfClasses")
                || scenario.contains("Invalid_BatchName")
                || scenario.contains("Invalid_BatchNameLength")
                || scenario.contains("Invalid_Endpoint")) {
            requestBodyForRequest = requestBody;
        }

        RequestSpecification requestSpec = given()
                .spec(noAuth
                        ? RequestSpec.getRequestSpecWithoutAuth()
                        : RequestSpec.getRequestSpec())
                .basePath(endpoint)
                .body(requestBodyForRequest);


        if (endpointNeedsBatchId) {

            if (SharedTestData.batchId <= 0) {
                throw new IllegalStateException(
                        "SharedTestData.batchId is invalid: "
                                + SharedTestData.batchId
                                + ". Create a batch before running this PUT scenario."
                );
            }

            requestSpec = requestSpec.pathParam(
                    "batchId",
                    SharedTestData.batchId
            );
        }

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("========= INVALID PUT BATCH REQUEST =========");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains {batchId}: " + endpointNeedsBatchId);
        System.out.println("=============================================");
    }
    @Given("Admin create DELETE request by BatchId with invalid input for scenario {string} from excel sheet")
    public void adminCreateDeleteRequestByBatchIdWithInvalidInputForScenarioFromExcelSheet(
            String scenario) throws IOException {

        data = ExcelReader.readExcelData("Batch", scenario);

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: " + scenario);
        }

        boolean noAuth = scenario.contains("NoAuth");
        boolean endpointNeedsBatchId = endpoint.contains("{batchId}");

        RequestSpecification requestSpec = given()
                .spec(noAuth
                        ? RequestSpec.getRequestSpecWithoutAuth()
                        : RequestSpec.getRequestSpec())
                .basePath(endpoint);

        if (endpointNeedsBatchId) {
            String batchIdForRequest;

            if (scenario.contains("Invalid_BatchId")) {
                batchIdForRequest = data.get("InvalidBatchId");

                if (batchIdForRequest == null || batchIdForRequest.isBlank()) {
                    batchIdForRequest = "99999";
                }
            } else {
                if (SharedTestData.batchId == 0) {
                    throw new IllegalStateException(
                            "batchId is invalid. Create a batch before running this scenario.");
                }

                batchIdForRequest = String.valueOf(SharedTestData.batchId);
            }

            requestSpec = requestSpec.pathParam("batchId", batchIdForRequest);
        }

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("DELETE BATCH REQUEST");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains batchId: " + endpointNeedsBatchId);
    }
    @When("Admin sends DELETE request to delete the batch")
    public void admin_sends_delete_request_to_delete_the_batch() {
        RequestSpecification request =
                scenarioContext.getRequestSpec();

        if (request == null) {
            throw new IllegalStateException(
                    "DELETE request was not prepared"
            );
        }

        response = ApiExecutor.delete(request);
    }

    @Given("Admin create DELETE request with valid batchId")
    public void admin_create_delete_request_with_valid_batch_id()
            throws IOException {

        data = ExcelReader.readExcelData(
                "Batch",
                "DeleteBatchById_Valid_BatchId"
        );

        RequestSpecification request = RequestBuilder.byBatchId(
                data.get("Endpoint"),
                SharedTestData.batchId,
                false
        );

        scenarioContext.setRequestSpec(request);
    }

    @Then("Admin receives success code with deleted message")
    public void admin_receives_success_code_with_deleted_message() {
        String expectedMessage = data.get("ExpectedMessage");

        if (expectedMessage == null || expectedMessage.isBlank()) {
            System.out.println("ExpectedMessage is blank; only status code will be validated.");
            return;
        }

        String body = response.asString();

        if (body == null || body.isBlank()) {
            Assert.fail(
                    "Expected message: " + expectedMessage + ", but API returned an empty response body."
            );
        }

        Assert.assertTrue(
                body.contains(expectedMessage),
                "Expected message doesn't match. Expected text: " + expectedMessage + ", actual body: " + body
        );
    }

    @When("Admin sends GET request to retrieve deleted batch with Id")
    public void admin_sends_get_request_to_retrieve_deleted_batch_with_id() throws IOException {
        data = ExcelReader.readExcelData("Batch", "GetBatchById_Deleted_BatchId");

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: GetBatchById_Deleted_BatchId"
            );
        }

        if (SharedTestData.batchId <= 0) {
            throw new IllegalStateException(
                    "batchId is 0. Create a batch and capture its batchId before retrieving a deleted batch."
            );
        }

        response = given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("batchId", SharedTestData.batchId)
                .basePath(endpoint)
                .when()
                .log()
                .all()
                .get();
    }
    @Then("Admin receives success code with GET response body for deleted batch")
    public void admin_receives_success_code_with_get_response_body_for_deleted_batch() {
        ResponseValidator.validateStatus(
                response,
                data.get("ExpectedStatusCode")
        );

        BatchResponseValidator.validateInactiveBatch(
                response.jsonPath(),
                SharedTestData.batchId,
                SharedTestData.batchName
        );
    }

    @When("Admin sends GET request to retrieve deleted batch with name")
    public void admin_sends_get_request_to_retrieve_deleted_batch_with_name() throws IOException {
        data = ExcelReader.readExcelData("Batch", "GetBatchByName_Deleted_BatchId");

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: GetBatchByName_Deleted_BatchId"
            );
        }

        if (SharedTestData.batchName == null || SharedTestData.batchName.isBlank()) {
            throw new IllegalStateException(
                    "batchName is null or blank. Create a batch and capture its batchName before this step."
            );
        }

        response = given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("batchName", SharedTestData.batchName)
                .basePath(endpoint)
                .when()
                .log()
                .all()
                .get();
    }

    @Then("Admin receives success code with GET response body for deleted batch by name")
    public void admin_receives_success_code_with_get_response_body_for_deleted_batch_by_name() {
        int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode);

        JsonPath json = response.jsonPath();

        List<Map<String, Object>> batches = json.getList("$");

        Assert.assertFalse(
                batches.isEmpty(),
                "Expected at least one batch in the response, but list is empty."
        );

        Map<String, Object> batch = batches.get(0);

        Assert.assertEquals(
                ((Number) batch.get("batchId")).intValue(),
                SharedTestData.batchId,
                "Batch Id doesn't match"
        );

        Assert.assertEquals(
                (String) batch.get("batchName"),
                SharedTestData.batchName,
                "Batch Name doesn't match"
        );

        Assert.assertEquals(
                (String) batch.get("batchStatus"),
                "Inactive",
                "Batch Status should be Inactive"
        );
    }

    @When("Admin sends PUT request to update deleted batch status")
    public void admin_sends_put_request_to_update_deleted_batch_status() throws IOException {
        data = ExcelReader.readExcelData("Batch", "PutBatchById_Deleted_BatchId");

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: PutBatchById_Deleted_BatchId"
            );
        }

        if (SharedTestData.batchId <= 0) {
            throw new IllegalStateException(
                    "batchId is 0. Create a batch and capture its batchId before this step."
            );
        }

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is 0. Create a program and capture its programId before this step."
            );
        }

        if (SharedTestData.programName == null || SharedTestData.programName.isBlank()) {
            throw new IllegalStateException(
                    "programName is null or blank. Create a program and capture its programName before this step."
            );
        }

        ObjectMapper mapper = new ObjectMapper();
        PutBatchRequest batchData = mapper.readValue(data.get("Body"), PutBatchRequest.class);

        // Use the current batch name from SharedTestData
        batchData.setBatchName(SharedTestData.batchName);
        batchData.setProgramId(SharedTestData.programId);
        batchData.setProgramName(SharedTestData.programName);

        response = given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("batchId", SharedTestData.batchId)
                .basePath(endpoint)
                .body(batchData)
                .when()
                .log()
                .all()
                .put();
    }
    @Then("Admin receives success code with Active batch status in the response body")
    public void admin_receives_success_code_with_active_batch_status_in_the_response_body() {
        int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath("schemas/batch/PutBatchByIdResponseSchema.json"));

        PutBatchResponse batchResponse = response.getBody().as(PutBatchResponse.class);

        Assert.assertEquals(
                batchResponse.getBatchStatus(),
                "Active",
                "Batch status should be Active in the response"
        );
    }

}