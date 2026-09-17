package stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import pojo.PutBatchRequest;
import pojo.PutBatchResponse;
import utils.ApiExecutor;
import utils.BatchRequestUtil;
import utils.ExcelReader;
import utils.RequestBuilder;
import utils.ResponseValidator;
import utils.ScenarioContext;
import utils.SharedTestData;
import utils.TestDataUtil;

import java.io.IOException;
import java.util.Map;

public class ProgramBatchUpdateStepDef extends SharedTestData {

    private final ScenarioContext scenarioContext;

    public ProgramBatchUpdateStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("Admin create PUT request to update batch with valid batchId for scenario {string}")
    public void admin_create_put_request_to_update_batch_with_valid_batch_id_for_scenario(
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

        validateSharedBatchAndProgramData();

        String requestBody = data.get("Body");

        if (requestBody == null || requestBody.isBlank()) {
            throw new IllegalStateException(
                    "Body is missing in Excel for scenario: " + scenario
            );
        }

        ObjectMapper mapper = new ObjectMapper();

        PutBatchRequest batchData = mapper.readValue(
                requestBody,
                PutBatchRequest.class
        );

        if (scenario.contains("UpdateBatchName")) {
            String updatedBatchName = SharedTestData.programName
                    + "_"
                    + TestDataUtil.randomNumericSuffix(4);

            batchData.setBatchName(updatedBatchName);
        } else {
            batchData.setBatchName(SharedTestData.batchName);
        }

        batchData.setProgramId(SharedTestData.programId);
        batchData.setProgramName(SharedTestData.programName);

        // Batch ID is passed through the URL path parameter.
        batchData.setBatchId(0);

        RequestSpecification requestSpec = RequestBuilder.putByBatchId(
                endpoint,
                SharedTestData.batchId,
                batchData,
                false
        );

        scenarioContext.setRequestSpec(requestSpec);
        storeExpectedPutValues(batchData);

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

        scenarioContext.setResponse(
                ApiExecutor.put(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    @Then("Admin received success code with updated ProgramId in response")
    public void admin_received_success_code_with_updated_program_id_in_response() {

        PutBatchResponse batchResponse = validatePutResponse();

        Object storedProgramId = scenarioContext.getContext("PROGRAM_ID");

        if (!(storedProgramId instanceof Number)) {
            throw new IllegalStateException(
                    "PROGRAM_ID is missing or invalid in ScenarioContext."
            );
        }

        int expectedProgramId = ((Number) storedProgramId).intValue();

        Assert.assertEquals(
                batchResponse.getProgramId(),
                expectedProgramId,
                "Updated programId in response should match the programId "
                        + "sent in the PUT payload."
        );
    }

    @Then("Admin received success code with updated batchName in response")
    public void admin_received_success_code_with_updated_batch_name_in_response() {

        PutBatchResponse batchResponse = validatePutResponse();

        String expectedBatchName = (String) scenarioContext.getContext(
                "BATCH_NAME"
        );

        Assert.assertNotNull(
                expectedBatchName,
                "Expected batchName is missing from ScenarioContext."
        );

        Assert.assertFalse(
                expectedBatchName.isBlank(),
                "Expected batchName is blank in ScenarioContext."
        );

        Assert.assertEquals(
                batchResponse.getBatchName(),
                expectedBatchName,
                "Updated batchName in response should match the batchName "
                        + "sent in the PUT payload."
        );
    }

    @Then("Admin received success code with updated batchStatus in response")
    public void admin_received_success_code_with_updated_batch_status_in_response() {

        PutBatchResponse batchResponse = validatePutResponse();

        String expectedBatchStatus = (String) scenarioContext.getContext(
                "BATCH_STATUS"
        );

        Assert.assertNotNull(
                expectedBatchStatus,
                "Expected batchStatus is missing from ScenarioContext."
        );

        Assert.assertFalse(
                expectedBatchStatus.isBlank(),
                "Expected batchStatus is blank in ScenarioContext."
        );

        Assert.assertEquals(
                batchResponse.getBatchStatus(),
                expectedBatchStatus,
                "Updated batchStatus in response should match the batchStatus "
                        + "sent in the PUT payload."
        );
    }

    @Then("Admin received success code with updated batchNoOfClasses in response")
    public void admin_received_success_code_with_updated_batch_no_of_classes_in_response() {

        PutBatchResponse batchResponse = validatePutResponse();

        Object storedClassCount = scenarioContext.getContext(
                "BATCH_NOOFCLASSES"
        );

        int expectedBatchNoOfClasses = getIntegerContextValue(
                storedClassCount,
                "BATCH_NOOFCLASSES"
        );

        Assert.assertEquals(
                batchResponse.getBatchNoOfClasses(),
                expectedBatchNoOfClasses,
                "Updated batchNoOfClasses in response should match the value "
                        + "sent in the PUT payload."
        );
    }

    @Given("Admin create PUT request with invalid input for each {string} from excel sheet")
    public void admin_create_put_request_with_invalid_input_for_each_from_excel_sheet(
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

        if (requestBody == null || requestBody.isBlank()) {
            throw new IllegalStateException(
                    "Body is missing in Excel for scenario: " + scenario
            );
        }

        boolean noAuth = scenario.contains("NoAuth")
                || scenario.contains("No_Auth");

        boolean endpointNeedsBatchId = endpoint.contains("{batchId}");

        Object requestBodyForRequest = createInvalidPutPayload(
                scenario,
                requestBody,
                noAuth
        );

        RequestSpecification requestSpec;

        if (endpointNeedsBatchId) {

            if (SharedTestData.batchId <= 0) {
                throw new IllegalStateException(
                        "SharedTestData.batchId is invalid: "
                                + SharedTestData.batchId
                                + ". Create a batch before running this PUT scenario."
                );
            }

            requestSpec = RequestBuilder.withBodyAndBatchId(
                    endpoint,
                    SharedTestData.batchId,
                    requestBodyForRequest,
                    noAuth
            );
        } else {
            requestSpec = RequestBuilder.withBody(
                    endpoint,
                    requestBodyForRequest,
                    noAuth
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

    @When("Admin sends PUT request to update deleted batch status")
    public void admin_sends_put_request_to_update_deleted_batch_status()
            throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "PutBatchById_Deleted_BatchId"
        );

        scenarioContext.setExcelData(data);

        if (data == null || data.isEmpty()) {
            throw new IllegalStateException(
                    "Batch data was not found for PutBatchById_Deleted_BatchId."
            );
        }

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing for PutBatchById_Deleted_BatchId."
            );
        }

        validateSharedBatchAndProgramData();

        String requestBody = data.get("Body");

        if (requestBody == null || requestBody.isBlank()) {
            throw new IllegalStateException(
                    "Body is missing for PutBatchById_Deleted_BatchId."
            );
        }

        ObjectMapper mapper = new ObjectMapper();

        PutBatchRequest batchData = mapper.readValue(
                requestBody,
                PutBatchRequest.class
        );

        batchData.setBatchName(SharedTestData.batchName);
        batchData.setProgramId(SharedTestData.programId);
        batchData.setProgramName(SharedTestData.programName);

        RequestSpecification requestSpec = RequestBuilder.putByBatchId(
                endpoint,
                SharedTestData.batchId,
                batchData,
                false
        );

        scenarioContext.setRequestSpec(requestSpec);

        scenarioContext.setResponse(
                ApiExecutor.put(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    @Then("Admin receives success code with Active batch status in the response body")
    public void admin_receives_success_code_with_active_batch_status_in_the_response_body() {

        PutBatchResponse batchResponse = validatePutResponse();

        Assert.assertEquals(
                batchResponse.getBatchStatus(),
                "Active",
                "Batch status should be Active in the response."
        );
    }

    private PutBatchResponse validatePutResponse() {

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

        ResponseValidator.validateBatchPutResponse(
                response,
                data.get("ExpectedStatusCode")
        );

        PutBatchResponse batchResponse =
                response.as(PutBatchResponse.class);

        Assert.assertNotNull(
                batchResponse,
                "PUT Batch response could not be converted to PutBatchResponse."
        );

        return batchResponse;
    }

    private void validateSharedBatchAndProgramData() {

        if (SharedTestData.batchId <= 0) {
            throw new IllegalStateException(
                    "batchId is invalid. Create and capture a valid Batch before this step."
            );
        }

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is invalid. Create and capture a valid Program before this step."
            );
        }

        if (SharedTestData.programName == null
                || SharedTestData.programName.isBlank()) {
            throw new IllegalStateException(
                    "programName is null or blank. Create and capture a valid "
                            + "Program before this step."
            );
        }
    }

    private void storeExpectedPutValues(PutBatchRequest batchData) {

        scenarioContext.setContext("BATCH_NAME", batchData.getBatchName());
        scenarioContext.setContext("BATCH_STATUS", batchData.getBatchStatus());
        scenarioContext.setContext(
                "BATCH_NOOFCLASSES",
                batchData.getBatchNoOfClasses()
        );
        scenarioContext.setContext("PROGRAM_ID", batchData.getProgramId());
    }

    private int getIntegerContextValue(
            Object storedValue,
            String contextKey) {

        if (storedValue == null) {
            throw new IllegalStateException(
                    contextKey + " is missing from ScenarioContext."
            );
        }

        if (storedValue instanceof Number) {
            return ((Number) storedValue).intValue();
        }

        try {
            return Integer.parseInt(
                    storedValue.toString().trim()
            );
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    contextKey + " is not a valid integer. Actual value: "
                            + storedValue,
                    exception
            );
        }
    }

    private Object createInvalidPutPayload(
            String scenario,
            String requestBody,
            boolean noAuth) throws IOException {

        if (noAuth) {
            return BatchRequestUtil.createValidBatchJson(requestBody);
        }

        if (scenario.contains("Missing_Mandatory_Fields")) {
            return BatchRequestUtil.createValidBatchJsonWithOverrides(
                    requestBody,
                    null,
                    "Active"
            );
        }

        if (scenario.contains("Invalid_BatchStatus")) {
            return BatchRequestUtil.createValidBatchJsonWithOverrides(
                    requestBody,
                    1,
                    "act"
            );
        }

        if (scenario.contains("Invalid_BatchId")) {
            return BatchRequestUtil.createValidBatchJson(requestBody);
        }

        if (scenario.contains("Invalid_ProgramId")) {
            return BatchRequestUtil.createBatchJsonForSpecificProgram(
                    requestBody,
                    9999,
                    "Invalid"
            );
        }

        if (scenario.contains("Deleted_programId")) {
            return BatchRequestUtil.createBatchJsonForSpecificProgram(
                    requestBody,
                    70,
                    "pithongjagijjjngRe"
            );
        }

        return requestBody;
    }
}