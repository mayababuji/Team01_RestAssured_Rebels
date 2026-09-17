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
import utils.ApiExecutor;
import utils.ExcelReader;
import utils.RequestBuilder;
import utils.ResponseValidator;
import utils.ScenarioContext;
import utils.SharedTestData;
import utils.TestDataUtil;

import java.io.IOException;
import java.util.Map;

public class ProgramBatchCreateStepDef extends SharedTestData {

    private final ScenarioContext scenarioContext;

    public ProgramBatchCreateStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("Admin create POST request with valid data for {string} from excel sheet")
    public void admin_create_post_request_with_valid_data_for_from_excel_sheet(
            String scenario) throws IOException {

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is invalid. Create a valid program and capture its "
                            + "programId before creating a batch."
            );
        }

        if (SharedTestData.programName == null
                || SharedTestData.programName.isBlank()) {
            throw new IllegalStateException(
                    "programName is null or blank. Create a valid program and "
                            + "capture its programName before creating a batch."
            );
        }

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                scenario
        );

        scenarioContext.setExcelData(data);

        if (data == null || data.isEmpty()) {
            throw new IllegalStateException(
                    "Batch data was not found in Excel for scenario: " + scenario
            );
        }

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: " + scenario
            );
        }

        String requestBody = data.get("Body");

        if (requestBody == null || requestBody.isBlank()) {
            throw new IllegalStateException(
                    "Body is missing in Excel for scenario: " + scenario
            );
        }

        ObjectMapper mapper = new ObjectMapper();

        CreateBatchRequest batchData = mapper.readValue(
                requestBody,
                CreateBatchRequest.class
        );

        batchData.setProgramId(SharedTestData.programId);

        String generatedBatchName = SharedTestData.programName
                + "_"
                + TestDataUtil.randomNumericSuffix(3);

        batchData.setBatchName(generatedBatchName);

        SharedTestData.batchName = generatedBatchName;

        RequestSpecification requestSpec = RequestBuilder.authorized(
                endpoint
        ).body(batchData);

        scenarioContext.setRequestSpec(requestSpec);
        scenarioContext.setContext("BATCH_NAME", generatedBatchName);

        System.out.println("===== CREATE BATCH REQUEST =====");
        System.out.println("Scenario: " + scenario);
        System.out.println("programId: " + SharedTestData.programId);
        System.out.println("programName: " + SharedTestData.programName);
        System.out.println("batchName: " + generatedBatchName);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("================================");
    }

    @When("Admin sends POST request to create program batch")
    public void admin_sends_post_request_to_create_program_batch() {

        scenarioContext.setResponse(
                ApiExecutor.post(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    @Then("Admin receives created status with response body")
    public void admin_receives_created_status_with_response_body() {

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

        ResponseValidator.validateStatusAndSchema(
                response,
                data.get("ExpectedStatusCode"),
                "schemas/batch/CreateBatchResponseSchema.json"
        );

        CreateBatchResponse batchResponse =
                response.as(CreateBatchResponse.class);

        Assert.assertNotNull(
                batchResponse,
                "Create Batch response could not be converted to CreateBatchResponse."
        );

        Assert.assertTrue(
                batchResponse.getBatchId() > 0,
                "Batch creation response did not return a valid batchId."
        );

        String expectedBatchName = (String) scenarioContext.getContext(
                "BATCH_NAME"
        );

        Assert.assertNotNull(
                expectedBatchName,
                "Expected Batch name is missing from ScenarioContext."
        );

        Assert.assertEquals(
                batchResponse.getBatchName(),
                expectedBatchName,
                "Batch name does not match in create response."
        );

        SharedTestData.batchId = batchResponse.getBatchId();
        SharedTestData.batchName = batchResponse.getBatchName();

        if (!SharedTestData.batchIds.contains(SharedTestData.batchId)) {
            SharedTestData.batchIds.add(SharedTestData.batchId);
        }

        System.out.println("===== BATCH DATA CAPTURED =====");
        System.out.println("batchId: " + SharedTestData.batchId);
        System.out.println("batchName: " + SharedTestData.batchName);
        System.out.println("================================");
    }

    @Given("Admin create POST request with invalid input for {string} from excel sheet")
    public void admin_create_post_request_with_invalid_input_for_from_excel_sheet(
            String scenario) throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                scenario
        );

        scenarioContext.setExcelData(data);

        if (data == null || data.isEmpty()) {
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

        boolean noAuth = scenario.contains("NoAuth")
                || scenario.contains("No_Auth");

        RequestSpecification requestSpec = RequestBuilder.withBody(
                endpoint,
                requestBody,
                noAuth
        );

        scenarioContext.setRequestSpec(requestSpec);
    }

    @Then("Admin receives expected status code with error message")
    public void admin_receives_expected_status_code_with_error_message() {

        ResponseValidator.validateErrorResponse(
                scenarioContext.getResponse(),
                scenarioContext.getExcelData()
        );
    }
}