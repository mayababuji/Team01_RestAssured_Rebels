package stepDefinitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import pojo.CreateProgramRequest;
import pojo.CreateProgramResponse;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ResponseValidator;
import utils.ScenarioContext;
import utils.SharedTestData;
import utils.TestDataUtil;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ProgramStepDef extends SharedTestData {

    private Map<String, String> data;
    private Response response;
    private CreateProgramRequest programInput;

    private final ScenarioContext scenarioContext;

    public ProgramStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    // =========================================================================
    // CREATE PROGRAM - POST
    // =========================================================================

    @When("Admin sends POST request to create program with different payload for {string} from dataSheet")
    public void adminSendsPostRequest(String scenarioNameFeature)
            throws IOException {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("SCENARIO: " + scenarioNameFeature);
        System.out.println("==========================================");

        data = ExcelReader.readExcelData(
                "Program",
                scenarioNameFeature
        );

        if (data == null || data.isEmpty()) {
            throw new IllegalStateException(
                    "Test data not found in Excel for: "
                            + scenarioNameFeature
            );
        }

        String excelScenarioName = data.get("ScenarioName");

        if (excelScenarioName == null
                || !scenarioNameFeature.equalsIgnoreCase(
                excelScenarioName.trim()
        )) {

            throw new IllegalStateException(
                    "ScenarioName mismatch. Feature=["
                            + scenarioNameFeature
                            + "] Excel=["
                            + excelScenarioName
                            + "]"
            );
        }

        int expectedStatus = Integer.parseInt(
                data.get("ExpectedStatusCode").trim()
        );

        String body = data.get("Body");

        if (body == null || body.trim().isEmpty()) {
            programInput = null;
        } else {
            try {
                programInput = RequestSpec.parseBody(
                        body.trim(),
                        CreateProgramRequest.class
                );
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException(
                        "Failed to parse Program request body for scenario: "
                                + scenarioNameFeature,
                        exception
                );
            }
        }

        String dataStrategy = data.get("Data Strategy");

        if (dataStrategy == null || dataStrategy.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Data Strategy is missing in Excel for: "
                            + scenarioNameFeature
            );
        }

        applyCreateProgramDataStrategy(dataStrategy);

        RequestSpecification requestSpec =
                scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException(
                    "requestSpec is null. "
                            + "Did the authorization Given step run?"
            );
        }

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for: "
                            + scenarioNameFeature
            );
        }

        endpoint = endpoint.trim();

        if (endpoint.contains("{programId}")) {
            if (SharedTestData.programId <= 0) {
                throw new IllegalStateException(
                        "SharedTestData.programId is not available."
                );
            }

            endpoint = endpoint.replace(
                    "{programId}",
                    String.valueOf(SharedTestData.programId)
            );
        }

        String method = data.get("Method");

        if (method == null || method.trim().isEmpty()) {
            throw new IllegalStateException(
                    "HTTP Method is missing in Excel for: "
                            + scenarioNameFeature
            );
        }

        String contentType = data.get("Content-type");

        RequestSpecification requestBuilder =
                given().spec(requestSpec);

        if (contentType != null && !contentType.trim().isEmpty()) {
            requestBuilder.contentType(contentType.trim());
        }

        if (programInput != null) {
            requestBuilder.body(programInput);
        }

        System.out.println("METHOD          : " + method.trim());
        System.out.println("ENDPOINT        : " + endpoint);
        System.out.println("EXPECTED STATUS : " + expectedStatus);

        response = requestBuilder
                .log()
                .all()
                .when()
                .request(method.trim(), endpoint)
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

    private void applyCreateProgramDataStrategy(String dataStrategy) {

        switch (dataStrategy.trim().toUpperCase()) {

            case "UNIQUE":

                if (programInput == null) {
                    throw new IllegalStateException(
                            "Request body is empty for UNIQUE strategy."
                    );
                }

                String uniqueProgramName =
                        TestDataUtil.generateUniqueProgramName();

                programInput.setProgramName(uniqueProgramName);

                SharedTestData.programName = uniqueProgramName;

                System.out.println("Data Strategy : UNIQUE");
                System.out.println(
                        "Program Name  : " + uniqueProgramName
                );
                break;

            case "EXCEL":

                System.out.println("Data Strategy : EXCEL");

                if (programInput != null) {
                    System.out.println(
                            "Program Name  : "
                                    + programInput.getProgramName()
                    );
                }
                break;

            case "EXISTING":

                if (programInput == null) {
                    throw new IllegalStateException(
                            "Request body is empty for EXISTING strategy."
                    );
                }

                if (SharedTestData.programName == null
                        || SharedTestData.programName.isBlank()) {

                    throw new IllegalStateException(
                            "No existing Program Name is available."
                    );
                }

                programInput.setProgramName(
                        SharedTestData.programName
                );

                System.out.println("Data Strategy : EXISTING");
                System.out.println(
                        "Program Name  : "
                                + SharedTestData.programName
                );
                break;

            default:
                throw new IllegalStateException(
                        "Invalid Data Strategy '"
                                + dataStrategy
                                + "' in Excel."
                );
        }
    }

    // =========================================================================
    // CREATE PROGRAM - POST RESPONSE VALIDATION
    // =========================================================================

    @Then("Admin verifies the response payload with expected output from the data sheet")
    public void adminVerifiesResponse() {

        if (response == null) {
            throw new IllegalStateException(
                    "Response is null. API request was not executed."
            );
        }

        if (data == null || data.isEmpty()) {
            throw new IllegalStateException(
                    "Excel data is null or empty."
            );
        }

        int expectedStatus = Integer.parseInt(
                data.get("ExpectedStatusCode").trim()
        );

        if (expectedStatus != 201) {
            System.out.println("Validating INVALID response...");
            ResponseValidator.validateErrorResponse(response, data);
            return;
        }

        System.out.println("Validating SUCCESS response...");

        Assert.assertEquals(
                response.getStatusCode(),
                201,
                "Create Program should return HTTP 201"
        );

        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(
                        "schemas/Program/CreateProgramSchema.json"
                ));

        CreateProgramResponse actualResponse =
                response.as(CreateProgramResponse.class);

        Assert.assertTrue(
                actualResponse.getProgramId() > 0,
                "ProgramId should be greater than 0"
        );

        Assert.assertNotNull(
                actualResponse.getProgramName(),
                "ProgramName should not be null"
        );

        Assert.assertFalse(
                actualResponse.getProgramName().isBlank(),
                "ProgramName should not be blank"
        );

        Assert.assertEquals(
                actualResponse.getProgramDescription(),
                programInput.getProgramDescription(),
                "ProgramDescription mismatch"
        );

        Assert.assertEquals(
                actualResponse.getProgramName(),
                programInput.getProgramName(),
                "ProgramName mismatch"
        );

        SharedTestData.programId =
                actualResponse.getProgramId();

        SharedTestData.programName =
                actualResponse.getProgramName();

        if (!SharedTestData.programIdList.contains(
                SharedTestData.programId
        )) {
            SharedTestData.programIdList.add(
                    SharedTestData.programId
            );
        }

        if (!SharedTestData.programNameList.contains(
                SharedTestData.programName
        )) {
            SharedTestData.programNameList.add(
                    SharedTestData.programName
            );
        }

        System.out.println();
        System.out.println("==========================================");
        System.out.println("PROGRAM CREATED SUCCESSFULLY");
        System.out.println(
                "Program ID   : " + SharedTestData.programId
        );
        System.out.println(
                "Program Name : " + SharedTestData.programName
        );
        System.out.println("==========================================");
    }

    // =========================================================================
    // GET PROGRAM REQUEST
    // =========================================================================

    @Given("Admin creates GET request with {string} for Program")
    public void adminCreatesGetRequestForProgram(String scenarioName)
            throws IOException {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("PROGRAM GET REQUEST");
        System.out.println("==========================================");
        System.out.println("Scenario : " + scenarioName);

        data = ExcelReader.readExcelData("Program", scenarioName);

        if (data == null || data.isEmpty()) {
            throw new IllegalStateException(
                    "Program Excel data was not found for scenario: "
                            + scenarioName
            );
        }

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: "
                            + scenarioName
            );
        }

        endpoint = endpoint.trim();

        String method = data.get("Method");

        if (method == null || method.isBlank()) {
            throw new IllegalStateException(
                    "Method is missing in Excel for scenario: "
                            + scenarioName
            );
        }

        method = method.trim().toUpperCase();

        boolean noAuth = isNoAuthScenario(scenarioName);

        RequestSpecification requestSpec = getRequestSpec(
                noAuth
        );

        endpoint = replaceEndpointPlaceholders(
                endpoint,
                scenarioName
        );

        String contentType = data.get("Content-type");

        if (contentType != null && !contentType.isBlank()) {
            requestSpec.contentType(contentType.trim());
        }

        String body = data.get("Body");

        if (body != null && !body.isBlank()) {
            requestSpec.body(body.trim());
        }

        validateEndpointResolved(endpoint, scenarioName);

        requestSpec.basePath(endpoint);

        scenarioContext.setRequestSpec(requestSpec);
        scenarioContext.setContext("PROGRAM_SCENARIO", scenarioName);
        scenarioContext.setContext("PROGRAM_METHOD", method);
        scenarioContext.setContext("PROGRAM_ENDPOINT", endpoint);

        System.out.println("Method   : " + method);
        System.out.println("Endpoint : " + endpoint);
        System.out.println("No Auth  : " + noAuth);
        System.out.println("==========================================");
    }

    // =========================================================================
    // UPDATE PROGRAM - PUT REQUEST
    // =========================================================================

    @Given("Admin creates PUT request with {string} for Program")
    public void adminCreatesPutRequestForProgram(String scenarioName)
            throws IOException {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("PROGRAM PUT REQUEST");
        System.out.println("==========================================");
        System.out.println("Scenario : " + scenarioName);
        System.out.println(
                "Shared Program ID : " + SharedTestData.programId
        );
        System.out.println(
                "Shared Program Name : "
                        + SharedTestData.programName
        );

        data = ExcelReader.readExcelData("Program", scenarioName);

        if (data == null || data.isEmpty()) {
            throw new IllegalStateException(
                    "Program Excel data was not found for scenario: "
                            + scenarioName
            );
        }

        String endpoint = data.get("Endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException(
                    "Endpoint is missing in Excel for scenario: "
                            + scenarioName
            );
        }

        endpoint = endpoint.trim();

        String method = data.get("Method");

        if (method == null || method.isBlank()) {
            throw new IllegalStateException(
                    "Method is missing in Excel for scenario: "
                            + scenarioName
            );
        }

        method = method.trim().toUpperCase();

        boolean noAuth = isNoAuthScenario(scenarioName);

        String contentType = data.get("Content-type");
        String body = data.get("Body");

        if (contentType != null) {
            contentType = contentType.trim();
        }

        if (body != null) {
            body = body.trim();
        }

        /*
         * Both positive update scenarios use a newly generated valid name.
         * Excel can contain "programName": "" for these rows.
         */
        // Resolve the URL first, using the existing saved program name.
        endpoint = replaceEndpointPlaceholders(
                endpoint,
                scenarioName
        );

// Only after the endpoint is resolved, generate a fresh name for the
// request body. This preserves the existing name in /program/{programName}.
        if (isValidUpdateScenario(scenarioName)) {
            body = generateUniqueProgramUpdateBody(body);
        }

        if (scenarioName.toLowerCase().contains(
                "invalid method"
        )) {
            method = "PATCH";
            endpoint = "/putprogram/" + SharedTestData.programId;
        }

        if (scenarioName.toLowerCase().contains(
                "invalid baseuri"
        )) {
            endpoint = "/invalid-putprogram/"
                    + SharedTestData.programId;
        }

        validateEndpointResolved(endpoint, scenarioName);

        RequestSpecification requestSpec = getRequestSpec(
                noAuth
        );

        if (contentType != null && !contentType.isBlank()) {
            requestSpec.contentType(contentType);
        }

        if (body != null && !body.isBlank()) {
            requestSpec.body(body);
        }

        requestSpec.basePath(endpoint);

        scenarioContext.setRequestSpec(requestSpec);
        scenarioContext.setContext("PROGRAM_SCENARIO", scenarioName);
        scenarioContext.setContext("PROGRAM_METHOD", method);
        scenarioContext.setContext("PROGRAM_ENDPOINT", endpoint);

        System.out.println("Method   : " + method);
        System.out.println("Endpoint : " + endpoint);
        System.out.println("No Auth  : " + noAuth);

        if (isValidUpdateScenario(scenarioName)) {
            System.out.println(
                    "Generated Program Name : "
                            + SharedTestData.programName
            );
        }

        System.out.println("==========================================");
    }

    // =========================================================================
    // UPDATE PROGRAM - SEND REQUEST
    // =========================================================================

    @When("Admin sends request to update Program")
    public void adminSendsRequestToUpdateProgram() {

        RequestSpecification requestSpec =
                scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException(
                    "Request specification is null. "
                            + "Ensure the PUT Given step executed first."
            );
        }

        String method = (String) scenarioContext.getContext(
                "PROGRAM_METHOD"
        );

        if (method == null || method.isBlank()) {
            throw new IllegalStateException(
                    "HTTP Method is missing from scenario context."
            );
        }

        method = method.trim().toUpperCase();

        System.out.println();
        System.out.println("==========================================");
        System.out.println("SENDING PROGRAM UPDATE REQUEST");
        System.out.println("METHOD : " + method);
        System.out.println("==========================================");

        response = sendProgramRequest(requestSpec, method);

        scenarioContext.setContext("PROGRAM_RESPONSE", response);
    }

    // =========================================================================
    // COMMON REQUEST SENDER
    // =========================================================================

    private Response sendProgramRequest(
            RequestSpecification requestSpec,
            String method) {

        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException(
                    "HTTP method cannot be null or blank."
            );
        }

        String httpMethod = method.trim().toUpperCase();

        System.out.println(
                "Executing HTTP Method: " + httpMethod
        );

        switch (httpMethod) {
            case "GET":
                return requestSpec.when().log().all().get();

            case "POST":
                return requestSpec.when().log().all().post();

            case "PUT":
                return requestSpec.when().log().all().put();

            case "DELETE":
                return requestSpec.when().log().all().delete();

            case "PATCH":
                return requestSpec.when().log().all().patch();

            case "HEAD":
                return requestSpec.when().log().all().head();

            case "OPTIONS":
                return requestSpec.when().log().all().options();

            default:
                return requestSpec.when()
                        .log()
                        .all()
                        .request(httpMethod);
        }
    }

    // =========================================================================
    // PROGRAM RESPONSE VALIDATION
    // =========================================================================

    @Then("Admin receives expected Program response for {string}")
    public void adminReceivesExpectedProgramResponse(String scenarioName) {

        if (response == null) {
            response = (Response) scenarioContext.getContext(
                    "PROGRAM_RESPONSE"
            );
        }

        if (response == null) {
            throw new IllegalStateException(
                    "Program API response is null for scenario: "
                            + scenarioName
            );
        }

        if (data == null || data.isEmpty()) {
            throw new IllegalStateException(
                    "Program Excel data is missing for scenario: "
                            + scenarioName
            );
        }

        System.out.println();
        System.out.println("==========================================");
        System.out.println("PROGRAM RESPONSE VALIDATION");
        System.out.println("==========================================");
        System.out.println("Scenario        : " + scenarioName);
        System.out.println(
                "Expected Status : "
                        + data.get("ExpectedStatusCode")
        );
        System.out.println(
                "Actual Status   : " + response.getStatusCode()
        );
        System.out.println(
                "Response Body   : " + response.asString()
        );
        System.out.println("==========================================");

        ResponseValidator.validateErrorResponse(response, data);
    }
    @When("Admin sends request to get Programs")
    public void adminSendsRequestToGetPrograms() {

        RequestSpecification requestSpec =
                scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException(
                    "Request specification is null. "
                            + "Ensure the GET Given step executed first."
            );
        }

        String method = (String) scenarioContext.getContext(
                "PROGRAM_METHOD"
        );

        if (method == null || method.isBlank()) {
            throw new IllegalStateException(
                    "HTTP Method is missing from scenario context."
            );
        }

        method = method.trim().toUpperCase();

        System.out.println();
        System.out.println("==========================================");
        System.out.println("SENDING PROGRAM GET REQUEST");
        System.out.println("METHOD : " + method);
        System.out.println("==========================================");

        response = sendProgramRequest(requestSpec, method);

        scenarioContext.setContext(
                "PROGRAM_RESPONSE",
                response
        );
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private RequestSpecification getRequestSpec(boolean noAuth) {

        if (noAuth) {
            return RestAssured.given().spec(
                    RequestSpec.getRequestSpecWithoutAuth()
            );
        }

        return RestAssured.given().spec(
                RequestSpec.getRequestSpec()
        );
    }

    private String replaceEndpointPlaceholders(
            String endpoint,
            String scenarioName) {

        String resolvedEndpoint = endpoint;

        if (resolvedEndpoint.contains("{programId}")) {

            if (SharedTestData.programId <= 0) {
                throw new IllegalStateException(
                        "SharedTestData.programId is not available for "
                                + "scenario: " + scenarioName
                );
            }

            resolvedEndpoint = resolvedEndpoint.replace(
                    "{programId}",
                    String.valueOf(SharedTestData.programId)
            );
        }

        if (resolvedEndpoint.contains("{programName}")) {

            String excelProgramName = data.get("ProgramName");

            String programName = null;

            if (excelProgramName != null
                    && !excelProgramName.isBlank()) {

                programName = excelProgramName.trim();

            } else if (SharedTestData.programName != null
                    && !SharedTestData.programName.isBlank()) {

                programName = SharedTestData.programName.trim();
            }

            if (programName == null || programName.isBlank()) {
                throw new IllegalStateException(
                        "ProgramName is not available for scenario: "
                                + scenarioName
                );
            }

            resolvedEndpoint = resolvedEndpoint.replace(
                    "{programName}",
                    programName
            );
        }

        return resolvedEndpoint;
    }

    private void validateEndpointResolved(
            String endpoint,
            String scenarioName) {

        if (endpoint.contains("{") || endpoint.contains("}")) {
            throw new IllegalStateException(
                    "Endpoint contains an unresolved placeholder for "
                            + "scenario: " + scenarioName
                            + ". Endpoint: " + endpoint
            );
        }
    }

    private boolean isValidUpdateScenario(String scenarioName) {

        if (scenarioName == null) {
            return false;
        }

        String scenario = scenarioName.trim();

        return "Update valid programId".equalsIgnoreCase(scenario)
                || "Update valid programName".equalsIgnoreCase(scenario);
    }

    private boolean isNoAuthScenario(String scenarioName) {

        if (scenarioName == null) {
            return false;
        }

        String scenario = scenarioName.trim().toLowerCase();

        return scenario.contains("noauth")
                || scenario.contains("no_auth")
                || scenario.contains("no auth");
    }

    private String generateUniqueProgramUpdateBody(String body) {

        if (body == null || body.isBlank()) {
            throw new IllegalStateException(
                    "Program update request body is null or blank."
            );
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode parsedNode = objectMapper.readTree(body);

            if (!parsedNode.isObject()) {
                throw new IllegalStateException(
                        "Program update request body must be a JSON object."
                );
            }

            ObjectNode bodyNode = (ObjectNode) parsedNode;

            String generatedProgramName =
                    TestDataUtil.generateUniqueProgramName();

            bodyNode.put("programName", generatedProgramName);

            SharedTestData.programName = generatedProgramName;

            System.out.println(
                    "Generated Program Name : "
                            + generatedProgramName
            );

            return objectMapper.writeValueAsString(bodyNode);

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Program update body is not valid JSON: " + body,
                    exception
            );
        }
    }
}