package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.hamcrest.core.Every;
import org.testng.Assert;
import utils.ApiExecutor;
import utils.BatchResponseValidator;
import utils.ExcelReader;
import utils.RequestBuilder;
import utils.ResponseValidator;
import utils.ScenarioContext;
import utils.SharedTestData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProgramBatchGetStepDef extends SharedTestData {

    private final ScenarioContext scenarioContext;

    public ProgramBatchGetStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("Admin create GET request with valid endpoint")
    public void admin_create_get_request_with_valid_endpoint() throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "GetAll_Valid_Batches"
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "GetAll_Valid_Batches"
        );

        scenarioContext.setRequestSpec(
                RequestBuilder.authorized(endpoint)
        );
    }

    @Given("Admin create GET request with invalid endpoint")
    public void admin_create_get_request_with_invalid_endpoint()
            throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "GetAll_Batches_InvalidEndpoint"

        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "GetAll_Batches_Invalid_Endpoint"
        );

        scenarioContext.setRequestSpec(
                RequestBuilder.authorized(endpoint)
        );
    }

    @Given("Admin create GET request with no authentication")
    public void admin_create_get_request_with_no_authentication()
            throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "GetAll_Batches_With_NoAuth"
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "GetAll_Batches_With_NoAuth"
        );

        scenarioContext.setRequestSpec(
                RequestBuilder.withoutAuth(endpoint)
        );
    }

    @When("Admin sends GET request to retrieve all batches")
    public void admin_sends_get_request_to_retrieve_all_batches() {

        scenarioContext.setResponse(
                ApiExecutor.get(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    @Then("Admin receives success code with response body")
    public void admin_receives_success_code_with_response_body() {

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

        ResponseValidator.validateStatusAndSchema(
                response,
                data.get("ExpectedStatusCode"),
                "schemas/batch/GetAllBatchesResponseSchema.json"
        );

        response.then()
                .body("", Matchers.instanceOf(List.class))
                .body("size()", Matchers.greaterThan(0));

        List<Map<String, Object>> batches =
                response.jsonPath().getList("");

        Assert.assertNotNull(
                batches,
                "GET-all Batch response should be a JSON list."
        );

        for (Integer expectedBatchId : SharedTestData.batchIds) {
            boolean batchExists = batches.stream()
                    .map(batch -> ((Number) batch.get("batchId")).intValue())
                    .anyMatch(actualBatchId -> actualBatchId.equals(expectedBatchId));

            Assert.assertTrue(
                    batchExists,
                    "Created batch ID was not found in GET-all response: "
                            + expectedBatchId
            );
        }
    }

    @Given("Admin create GET request with valid batchId")
    public void admin_create_get_request_with_valid_batch_id()
            throws IOException {

        if (SharedTestData.batchId <= 0) {
            throw new IllegalStateException(
                    "batchId is invalid. Create a Batch and capture its batchId "
                            + "before GET by batchId."
            );
        }

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "GetAll_BatchById_Valid_BatchId"
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "GetAll_BatchById_Valid_BatchId"
        );

        requirePathParameter(endpoint, "{batchId}");

        scenarioContext.setRequestSpec(
                RequestBuilder.byBatchId(
                        endpoint,
                        SharedTestData.batchId,
                        false
                )
        );
    }

    @When("Admin sends GET request to retrieve the batch")
    public void admin_sends_get_request_to_retrieve_the_batch() {

        scenarioContext.setResponse(
                ApiExecutor.get(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    @Then("Admin receives success code with GET response body")
    public void admin_receives_success_code_with_get_response_body() {

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

        ResponseValidator.validateStatusAndSchema(
                response,
                data.get("ExpectedStatusCode"),
                "schemas/batch/GetBatchByIdResponseSchema.json"
        );

        JsonPath json = response.jsonPath();

        Assert.assertEquals(
                json.getInt("batchId"),
                SharedTestData.batchId,
                "Batch ID does not match."
        );
    }

    @Given("Admin create GET request by BatchId with invalid input for {string} from excel sheet")
    public void admin_create_get_request_by_batch_id_with_invalid_input_for_from_excel_sheet(
            String scenario) throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                scenario
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(data, scenario);

        boolean noAuth = hasNoAuthScenario(scenario);
        boolean endpointNeedsBatchId = endpoint.contains("{batchId}");

        String testBatchId = getExcelOrDefault(
                data,
                "InvalidBatchId",
                "9999"
        );

        RequestSpecification requestSpec;

        if (endpointNeedsBatchId) {
            requestSpec = RequestBuilder.withPathParam(
                    endpoint,
                    "batchId",
                    testBatchId,
                    noAuth
            );
        } else {
            requestSpec = buildRequestWithoutPathParameter(
                    endpoint,
                    noAuth
            );
        }

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("===== INVALID GET-BY-ID REQUEST =====");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains {batchId}: " + endpointNeedsBatchId);
        System.out.println("Test batchId: " + testBatchId);
        System.out.println("======================================");
    }

    @Given("Admin create GET request to retrieve batch with valid batch name")
    public void admin_create_get_request_to_retrieve_batch_with_valid_batch_name()
            throws IOException {

        if (SharedTestData.batchName == null
                || SharedTestData.batchName.isBlank()) {
            throw new IllegalStateException(
                    "batchName is null or blank. Create a Batch and capture its "
                            + "batchName before GET by batch name."
            );
        }

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "GetBatchByName_Valid_BatchName"
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "GetBatchByName_Valid_BatchName"
        );

        requirePathParameter(endpoint, "{batchName}");

        scenarioContext.setRequestSpec(
                RequestBuilder.byBatchName(
                        endpoint,
                        SharedTestData.batchName,
                        false
                )
        );
    }

    @Then("Admin receives success code with GET response body having given batch name")
    public void admin_receives_success_code_with_get_response_body_having_given_batch_name() {

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

        ResponseValidator.validateStatusAndSchema(
                response,
                data.get("ExpectedStatusCode"),
                "schemas/batch/GetBatchByNameResponseSchema.json"
        );

        response.then()
                .body("", Matchers.instanceOf(List.class))
                .body("size()", Matchers.greaterThan(0))
                .body(
                        "batchName",
                        Matchers.hasItem(SharedTestData.batchName)
                );
    }

    @Given("Admin create GET request by BatchName with invalid input for {string} from excel sheet")
    public void admin_create_get_request_by_batch_name_with_invalid_input_for_from_excel_sheet(
            String scenario) throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                scenario
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(data, scenario);

        boolean noAuth = hasNoAuthScenario(scenario);
        boolean endpointNeedsBatchName = endpoint.contains("{batchName}");

        String testBatchName = getExcelOrDefault(
                data,
                "InvalidBatchName",
                "InvalidBatchName9999"
        );

        RequestSpecification requestSpec;

        if (endpointNeedsBatchName) {
            requestSpec = RequestBuilder.withPathParam(
                    endpoint,
                    "batchName",
                    testBatchName,
                    noAuth
            );
        } else {
            requestSpec = buildRequestWithoutPathParameter(
                    endpoint,
                    noAuth
            );
        }

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("===== INVALID GET-BY-NAME REQUEST =====");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains {batchName}: " + endpointNeedsBatchName);
        System.out.println("Test batchName: " + testBatchName);
        System.out.println("========================================");
    }

    @Given("Admin create GET request to retrieve batch with valid programId")
    public void admin_create_get_request_to_retrieve_batch_with_valid_program_id()
            throws IOException {

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is invalid. Create a valid Program and capture its "
                            + "programId before retrieving Batches by programId."
            );
        }

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "GetBatchByProgram_Valid_ProgramId"
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "GetBatchByProgram_Valid_ProgramId"
        );

        requirePathParameter(endpoint, "{programId}");

        scenarioContext.setRequestSpec(
                RequestBuilder.byProgramId(
                        endpoint,
                        SharedTestData.programId,
                        false
                )
        );
    }

    @Then("Admin receives success code with GET response body having given programId")
    public void admin_receives_success_code_with_get_response_body_having_given_program_id() {

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is invalid. A valid Program must be created before "
                            + "validating GET Batches by programId."
            );
        }

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

        ResponseValidator.validateStatusAndSchema(
                response,
                data.get("ExpectedStatusCode"),
                "schemas/batch/GetBatchByProgramResponseSchema.json"
        );

        response.then()
                .body("", Matchers.instanceOf(List.class))
                .body("size()", Matchers.greaterThan(0))
                .body(
                        "programId",
                        Every.everyItem(
                                Matchers.equalTo(SharedTestData.programId)
                        )
                );
    }

    @Given("Admin create GET request by programId with invalid input for scenario {string} from excel sheet")
    public void admin_create_get_request_by_program_id_with_invalid_input_for_scenario_from_excel_sheet(
            String scenario) throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                scenario
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(data, scenario);

        boolean noAuth = hasNoAuthScenario(scenario);
        boolean endpointNeedsProgramId = endpoint.contains("{programId}");

        String testProgramId = null;

        if (endpointNeedsProgramId) {
            testProgramId = noAuth
                    ? getValidProgramIdForNoAuthScenario()
                    : getExcelOrDefault(
                    data,
                    "InvalidProgramId",
                    "9999"
            );
        }

        RequestSpecification requestSpec;

        if (endpointNeedsProgramId) {
            requestSpec = RequestBuilder.withPathParam(
                    endpoint,
                    "programId",
                    testProgramId,
                    noAuth
            );
        } else {
            requestSpec = buildRequestWithoutPathParameter(
                    endpoint,
                    noAuth
            );
        }

        scenarioContext.setRequestSpec(requestSpec);

        System.out.println("===== INVALID GET-BY-PROGRAM-ID REQUEST =====");
        System.out.println("Scenario: " + scenario);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Uses auth: " + !noAuth);
        System.out.println("Endpoint contains {programId}: " + endpointNeedsProgramId);

        if (testProgramId != null) {
            System.out.println("Test programId: " + testProgramId);
        }

        System.out.println("==============================================");
    }

    @When("Admin sends GET request to retrieve batches by programId")
    public void admin_sends_get_request_to_retrieve_batches_by_program_id() {

        scenarioContext.setResponse(
                ApiExecutor.get(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    @When("Admin sends GET request to retrieve deleted batch with Id")
    public void admin_sends_get_request_to_retrieve_deleted_batch_with_id()
            throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "GetBatchById_Deleted_BatchId"
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "GetBatchById_Deleted_BatchId"
        );

        if (SharedTestData.batchId <= 0) {
            throw new IllegalStateException(
                    "batchId is invalid. Create and capture a Batch before "
                            + "retrieving a deleted batch."
            );
        }

        RequestSpecification requestSpec = RequestBuilder.byBatchId(
                endpoint,
                SharedTestData.batchId,
                false
        );

        scenarioContext.setRequestSpec(requestSpec);

        scenarioContext.setResponse(
                ApiExecutor.get(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    @Then("Admin receives success code with GET response body for deleted batch")
    public void admin_receives_success_code_with_get_response_body_for_deleted_batch() {

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

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
    public void admin_sends_get_request_to_retrieve_deleted_batch_with_name()
            throws IOException {

        Map<String, String> data = ExcelReader.readExcelData(
                "Batch",
                "GetBatchByName_Deleted_BatchId"
        );

        scenarioContext.setExcelData(data);

        String endpoint = requireEndpoint(
                data,
                "GetBatchByName_Deleted_BatchId"
        );

        if (SharedTestData.batchName == null
                || SharedTestData.batchName.isBlank()) {
            throw new IllegalStateException(
                    "batchName is null or blank. Create and capture a Batch "
                            + "before this step."
            );
        }

        RequestSpecification requestSpec = RequestBuilder.byBatchName(
                endpoint,
                SharedTestData.batchName,
                false
        );

        scenarioContext.setRequestSpec(requestSpec);

        scenarioContext.setResponse(
                ApiExecutor.get(
                        scenarioContext.getRequestSpec()
                )
        );
    }

    @Then("Admin receives success code with GET response body for deleted batch by name")
    public void admin_receives_success_code_with_get_response_body_for_deleted_batch_by_name() {

        Response response = scenarioContext.getResponse();
        Map<String, String> data = scenarioContext.getExcelData();

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

    private void requirePathParameter(
            String endpoint,
            String placeholder) {

        if (!endpoint.contains(placeholder)) {
            throw new IllegalStateException(
                    "Endpoint must contain " + placeholder
                            + ". Actual endpoint: " + endpoint
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

    private RequestSpecification buildRequestWithoutPathParameter(
            String endpoint,
            boolean noAuth) {

        return noAuth
                ? RequestBuilder.withoutAuth(endpoint)
                : RequestBuilder.authorized(endpoint);
    }

    private String getValidProgramIdForNoAuthScenario() {

        if (SharedTestData.programId <= 0) {
            throw new IllegalStateException(
                    "programId is invalid. Create and capture a valid Program "
                            + "before running a NoAuth programId scenario."
            );
        }

        return String.valueOf(SharedTestData.programId);
    }
}