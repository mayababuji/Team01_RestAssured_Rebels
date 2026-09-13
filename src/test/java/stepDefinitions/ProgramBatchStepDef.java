package stepDefinitions;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.CreateBatchRequest;
import pojo.CreateBatchResponse;
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
        // If you have access to scenario tags via a hook, you could store a flag in ScenarioContext
        // For example, a Before hook sets scenarioContext.setSkipAuth(true) for @NoAuth scenarios.
        Boolean skipAuth = (Boolean) scenarioContext.getContext("SKIP_AUTH");
        if (Boolean.TRUE.equals(skipAuth)) {
            return; // do nothing, no auth header
        }

        RequestSpecification spec = RequestSpec.getRequestSpec();
        scenarioContext.setRequestSpec(spec);
    }

    // Create Batch
    @Given("Admin create POST request with valid data for {string} from excel sheet")
    public void admin_create_post_request_with_valid_data_for_from_excel_sheet(String scenario) throws IOException {
        data = ExcelReader.readExcelData("Batch", scenario);
        ObjectMapper mapper = new ObjectMapper();
        CreateBatchRequest batchData = mapper.readValue(data.get("Body"), CreateBatchRequest.class);

        batchData.setProgramId(SharedTestData.programId);

        //String batchName = SharedTestData.programName + "_01";
        String batchName = SharedTestData.programName + "_" + TestDataUtil.randomNumericSuffix(3);
        batchData.setBatchName(batchName);
        SharedTestData.batchName = batchName;

        // Build request spec and store it in ScenarioContext
        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(data.get("Endpoint"))
                .body(batchData);

        scenarioContext.setRequestSpec(requestSpec);
        scenarioContext.setContext("BATCH_NAME", batchData.getBatchName());
    }

    @When("Admin sends POST request to create program batch")
    public void admin_sends_post_request_to_create_program_batch() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();
        if (requestSpec == null) {
            throw new IllegalStateException("requestSpec is null – ensure the Given step that creates the request ran first.");
        }
        response = requestSpec.when().log().all().post();
    }

    @Then("Admin receives created status with response body")
    public void admin_receives_created_status_with_response_body() {
        response.then().log().all()
                .statusCode(Integer.parseInt(data.get("ExpectedStatusCode")))
                .body(matchesJsonSchemaInClasspath("schemas/batch/CreateBatchResponseSchema.json"));

        CreateBatchResponse batchResponse = response.as(CreateBatchResponse.class);
        if (batchIds.size() == 0) {
            batchId = batchResponse.getBatchId();
            batchName = batchResponse.getBatchName();
        }
        batchIds.add(batchResponse.getBatchId());
        String expectedBatchName = (String) scenarioContext.getContext("BATCH_NAME");
        Assert.assertEquals(batchResponse.getBatchName(), expectedBatchName, "Batch Name doesn't match in response");
    }

    @Given("Admin create POST request with invalid input for {string} from excel sheet")
    public void admin_create_post_request_with_invalid_input_for_from_excel_sheet(String scenario) throws IOException {
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
        System.out.println("The reuest for batch creation is "+requestSpec);

        scenarioContext.setRequestSpec(requestSpec);
    }

    @Then("Admin receives expected status code with error message")
    public void admin_receives_expected_status_code_with_error_message() {
        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        String expectedMessage = data.get("ExpectedMessage");

        response.then().log().all().statusCode(expectedStatus);

        // For 401 (and any other status with empty body), skip message check
        String body = response.asString();
        if (body == null || body.isBlank()) {
            System.out.println("No response body to validate message for status " + expectedStatus);
            return;
        }

        String message = ProgramResponseValidator.extractErrorMessage(response);

        System.out.println("expectedMessage Message " + expectedMessage);
        System.out.println("Actual Message " + message);

        if (message != null && !message.isBlank() && expectedMessage != null && !expectedMessage.isBlank()) {
            Assert.assertTrue(message.contains(expectedMessage), "Error message doesn't match");
        }
    }

    // Get ALL Batches
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
                    "requestSpec is null  the Given step that builds the request should be executed prior."
            );
        }
        response = requestSpec.when().log().all().get();
    }

    @Then("Admin receives success code with response body")
    public void admin_receives_success_code_with_response_body() {
        int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode"));
        int batchIdCount = 0;
        response.then().log().all().statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath("schemas/batch/GetAllBatchesResponseSchema.json"))
                .body("", Matchers.instanceOf(List.class)).body("size()", Matchers.greaterThan(0));
        JsonPath json = response.jsonPath();
        List<Map<String, Object>> array = json.getList("$");
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> element = array.get(i);
            int resBatch = (Integer) element.get("batchId");
            if (batchIds.contains(resBatch)) {
                batchIdCount++;
            }
        }
        Assert.assertEquals(batchIdCount, batchIds.size());
    }

    @Given("Admin create GET request with invalid endpoint")
    public void admin_create_get_request_with_invalid_endpoint() throws IOException {
        data = ExcelReader.readExcelData("Batch", "GetAll_Batches_InvalidEndpoint");

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(data.get("Endpoint"));
        scenarioContext.setRequestSpec(requestSpec);

    }

    @Given("Admin create GET request with no authentication")
    public void admin_create_get_request_with_no_authentication() throws IOException {
        data = ExcelReader.readExcelData("Batch", "GetAll_Batches_With_NoAuth");

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpecWithoutAuth())
                .basePath(data.get("Endpoint"));

        scenarioContext.setRequestSpec(requestSpec);
    }

    @Given("Admin create GET request with valid batchId")
    public void admin_create_get_request_with_valid_batch_id() throws IOException {
        data = ExcelReader.readExcelData("Batch", "GetAll_BatchById_Valid_BatchId");

        RequestSpecification requestSpec = given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("batchId", batchId)
                .basePath(data.get("Endpoint"));

        scenarioContext.setRequestSpec(requestSpec);
    }

    @When("Admin sends GET request to retrieve the batch")
    public void admin_sends_get_request_to_retrieve_the_batch() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();
        if (requestSpec == null) {
            throw new IllegalStateException(
                    "requestSpec is null  ensure the Given step that builds the request ran first."
            );
        }
        response = requestSpec.when().log().all().get();
    }

    @Then("Admin receives success code with GET response body")
    public void admin_receives_success_code_with_get_response_body() {
        int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode"));
        response.then().log().all().statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath("schemas/batch/GetBatchByIdResponseSchema.json"));
        JsonPath json = response.jsonPath();
        Assert.assertEquals(json.getInt("batchId"), batchId, "Batch Id doesn't match");
    }

    @Given("Admin create GET request by BatchId with invalid input for {string} from excel sheet")
    public void admin_create_get_request_by_batch_id_with_invalid_input_for_from_excel_sheet(String scenario)
            throws IOException {
        data = ExcelReader.readExcelData("Batch", scenario);

        RequestSpecification requestSpec;

        if (scenario.contains("NoAuth")) {
            requestSpec = given()
                    .spec(RequestSpec.getRequestSpecWithoutAuth())
                    .pathParam("batchId", batchId)
                    .basePath(data.get("Endpoint"));
        } else if (scenario.contains("invalidBatchId")) {
            // For invalid batchId scenarios, do NOT set the pathParam; let the endpoint itself be invalid/complete
            requestSpec = given()
                    .spec(RequestSpec.getRequestSpec())
                    .basePath(data.get("Endpoint"));
        } else {
            requestSpec = given()
                    .spec(RequestSpec.getRequestSpec())

                    .basePath(data.get("Endpoint"));
        }

        scenarioContext.setRequestSpec(requestSpec);
    }
}