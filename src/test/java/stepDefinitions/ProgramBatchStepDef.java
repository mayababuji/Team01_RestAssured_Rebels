package stepDefinitions;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.CreateBatchRequest;
import pojo.CreateBatchResponse;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;

public class ProgramBatchStepDef extends SharedTestData  {
    private RequestSpecification requestSpec;
    private Response response;
    private Map<String, String> data;
    private final ScenarioContext scenarioContext;

    public ProgramBatchStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }
    @Given("Admin sets authorization to Bearer Token")
    public void admin_sets_authorization_to_bearer_token() {
        requestSpec = RequestSpec.getRequestSpec();
    }
    // Create Batch
    @Given("Admin create POST request with valid data for {string} from excel sheet")
    public void admin_create_post_request_with_valid_data_for_from_excel_sheet(String scenario) throws IOException {
        data = ExcelReader.readExcelData("Batch", scenario);
        ObjectMapper mapper = new ObjectMapper();
        CreateBatchRequest batchData = mapper.readValue(data.get("Body"), CreateBatchRequest.class);

        batchData.setProgramId(SharedTestData.programId);

        String batchName = SharedTestData.programName + "_01";
        batchData.setBatchName(batchName);
        SharedTestData.batchName = batchName;
        requestSpec = given().spec(RequestSpec.getRequestSpec()).basePath(data.get("Endpoint")).body(batchData);
        scenarioContext.setContext("BATCH_NAME", batchData.getBatchName());
    }
    @When("Admin sends POST request to create program batch")
    public void admin_sends_post_request_to_create_program_batch() {
        response = requestSpec.when().log().all().post();
    }
    @Then("Admin receives created status with response body")
    public void admin_receives_created_status_with_response_body() {
        response.then().log().all().statusCode(Integer.parseInt(data.get("ExpectedStatusCode")))
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

}
