package stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import pojo.CreateBatchRequest;
import pojo.CreateBatchResponse;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;
import utils.TestDataUtil;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ProgramBatchStepDef extends SharedTestData {

    private Response response;
    private Map<String, String> data;
    private final ScenarioContext scenarioContext;

    public ProgramBatchStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("Admin sets authorization to Bearer Token")
    public void admin_sets_authorization_to_bearer_token() {
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

        scenarioContext.setRequestSpec(requestSpec);
    }

    @Then("Admin receives expected status code with error message")
    public void admin_receives_expected_status_code_with_error_message() {
        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        String expectedMessage = data.get("ExpectedMessage");

        response.then().log().all().statusCode(expectedStatus);

        // Use your existing ResponseSpec helper to extract the message
        String message = specBuilder.ResponseSpec.getResponseMessage(response);

        if (message != null && !message.isBlank() && expectedMessage != null && !expectedMessage.isBlank()) {
            Assert.assertTrue(message.contains(expectedMessage), "Error message doesn't match");
        }
    }
}