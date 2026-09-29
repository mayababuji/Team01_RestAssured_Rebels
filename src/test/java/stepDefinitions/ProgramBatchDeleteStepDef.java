package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.ApiExecutor;
import utils.ExcelReader;
import utils.RequestBuilder;
import utils.ResponseValidator;
import utils.ScenarioContext;
import utils.SharedTestData;

import java.io.IOException;
import java.util.Map;

public class ProgramBatchDeleteStepDef extends SharedTestData {

    private final ScenarioContext scenarioContext;

    public ProgramBatchDeleteStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    // ==========================================================
    // DELETE BATCH WITH VALID BATCH ID
    // ==========================================================

    @Given("Admin create DELETE request with valid batchId")
    public void admin_create_delete_request_with_valid_batch_id()
            throws IOException {

        if (SharedTestData.batchId <= 0) {
            throw new IllegalStateException(
                    "batchId is invalid. Create a Batch and capture its batchId "
                            + "before running the valid DELETE scenario."
            );
        }

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "DeleteBatchById_Valid_BatchId"
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "DeleteBatchById_Valid_BatchId"
        );

        requireBatchIdPlaceholder(endpoint);

        RequestSpecification requestSpec = RequestBuilder.byBatchId(
                endpoint,
                SharedTestData.batchId,
                false
        );

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("===== VALID DELETE BATCH REQUEST =====");
        System.out.println("Endpoint: " + endpoint);
        System.out.println("batchId: " + SharedTestData.batchId);
        System.out.println("Uses auth: true");
        System.out.println("======================================");
    }

    // ==========================================================
    // DELETE BATCH WITH INVALID INPUT / INVALID ENDPOINT / NO AUTH
    // ==========================================================

    @Given("Admin create DELETE request by BatchId with invalid input for scenario {string} from excel sheet")
    public void admin_create_delete_request_by_batch_id_with_invalid_input_for_scenario_from_excel_sheet(
            String scenario) throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                scenario
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(data, scenario);

        boolean noAuth = hasNoAuthScenario(scenario);
        boolean endpointNeedsBatchId = endpoint.contains("{batchId}");

        String batchIdForRequest = null;

        if (endpointNeedsBatchId) {

            if (scenario.contains("Invalid_BatchId")) {
                batchIdForRequest = getExcelOrDefault(
                        data,
                        "InvalidBatchId",
                        "99999"
                );

            } else {

                if (SharedTestData.batchId <= 0) {
                    throw new IllegalStateException(
                            "batchId is invalid. Create a Batch and capture its batchId "
                                    + "before running this DELETE scenario."
                    );
                }

                batchIdForRequest = String.valueOf(
                        SharedTestData.batchId
                );
            }
        }

        RequestSpecification requestSpec;

        if (endpointNeedsBatchId) {
            requestSpec = RequestBuilder.withPathParam(
                    endpoint,
                    "batchId",
                    batchIdForRequest,
                    noAuth
            );
        } else {
            requestSpec = noAuth
                    ? RequestBuilder.withoutAuth(endpoint)
                    : RequestBuilder.authorized(endpoint);
        }

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("===== INVALID DELETE BATCH REQUEST =====");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains {batchId}: " + endpointNeedsBatchId);

        if (batchIdForRequest != null) {
            System.out.println("Batch ID for request: " + batchIdForRequest);
        }

        System.out.println("========================================");
    }

    // ==========================================================
    // EXECUTE DELETE
    // ==========================================================

    @When("Admin sends DELETE request to delete the batch")
    public void admin_sends_delete_request_to_delete_the_batch() {

        scenarioContext.setResponse(
                ApiExecutor.delete(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    // ==========================================================
    // VALIDATE DELETE RESPONSE
    // ==========================================================

    @Then("Admin receives success code with deleted message")
    public void admin_receives_success_code_with_deleted_message() {

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

        ResponseValidator.validateStatus(
                response,
                data.get("ExpectedStatusCode")
        );

        ResponseValidator.validateResponseMessage(
                response,
                data.get("ExpectedMessage")
        );
    }

    // ==========================================================
    // PRIVATE HELPER METHODS
    // ==========================================================

    private String requireEndpoint(
            Map<String, String> excelData,
            String scenario) {

        if (excelData == null || excelData.isEmpty()) {
            throw new IllegalStateException(
                    "Batch data was not found in Excel for scenario: " + scenario
            );
        }

        String endpoint = excelData.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: " + scenario
            );
        }

        return endpoint.trim();
    }

    private void requireBatchIdPlaceholder(String endpoint) {

        if (!endpoint.contains("{batchId}")) {
            throw new IllegalStateException(
                    "DELETE endpoint must contain {batchId}. Actual endpoint: "
                            + endpoint
            );
        }
    }

    private boolean hasNoAuthScenario(String scenario) {

        return scenario.contains("NoAuth")
                || scenario.contains("No_Auth");
    }

    private String getExcelOrDefault(
            Map<String, String> excelData,
            String columnName,
            String defaultValue) {

        String value = excelData.get(columnName);

        return value == null || value.isBlank()
                ? defaultValue
                : value.trim();
    }
}