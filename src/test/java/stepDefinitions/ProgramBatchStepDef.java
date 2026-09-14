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
import org.testng.Assert;

import pojo.CreateBatchRequest;
import pojo.CreateBatchResponse;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ProgramResponseValidator;
import utils.ScenarioContext;
import utils.SharedTestData;
import utils.TestDataUtil;

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
        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        String expectedMessage = data.get("ExpectedMessage");

        response.then()
                .log()
                .all()
                .statusCode(expectedStatus);

        String body = response.asString();

        if (body == null || body.isBlank()) {
            System.out.println(
                    "No response body available to validate the error message for status: "
                            + expectedStatus
            );
            return;
        }

        String actualMessage =
                ProgramResponseValidator.extractErrorMessage(response);

        System.out.println("Expected message: " + expectedMessage);
        System.out.println("Actual message: " + actualMessage);

        if (expectedMessage != null
                && !expectedMessage.isBlank()
                && actualMessage != null
                && !actualMessage.isBlank()) {

            Assert.assertTrue(
                    actualMessage.contains(expectedMessage),
                    "Error message doesn't match"
            );
        }
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

        /*
         * Concrete endpoint examples:
         * /batches/batchId/9999
         * /batches/batchId/a999
         * /batches/BATCH/9999
         *
         * Do NOT pass a path parameter for those.
         */
        if (endpointNeedsBatchId) {
            String testBatchId = data.get("InvalidBatchId");

            if (testBatchId == null || testBatchId.isBlank()) {
                /*
                 * Use a non-existent numeric ID as a fallback.
                 * Do not use SharedTestData.batchId here because it is valid.
                 */
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
}