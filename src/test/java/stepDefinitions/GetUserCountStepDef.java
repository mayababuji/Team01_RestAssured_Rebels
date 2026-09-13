package stepDefinitions;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;

public class GetUserCountStepDef {

    private RequestSpecification request;
    private Response response;
    private Map<String, String> testData;

    private final ScenarioContext scenarioContext;

    public GetUserCountStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    // ============================================================
    // POSITIVE
    // ============================================================

    @Given("Admin create GET request with valid data for user count scenario {string} from excel sheet")
    public void admin_create_get_request_with_valid_data_for_user_count_scenario(
            String scenarioName) throws IOException {

        System.out.println("==================================================");
        System.out.println("Scenario: " + scenarioName);

        // Read data from Excel
        testData = ExcelReader.readExcelData("User", scenarioName);

        // Get endpoint
        String endpoint = testData.get("EndPoint");

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = testData.get("Endpoint");
        }

        Assert.assertNotNull(endpoint, "Endpoint not found in Excel.");
        Assert.assertFalse(endpoint.trim().isEmpty(), "Endpoint is empty in Excel.");

        System.out.println("Endpoint from Excel: " + endpoint);

        // Get Role ID from Excel
       String roleId = testData.get("RoleID");

if (roleId == null || roleId.trim().isEmpty()) {
    roleId = testData.get("RoleId");
}

if (roleId == null || roleId.trim().isEmpty()) {
    roleId = testData.get("Role ID");
}

// If Role ID is not present in Excel, extract it from scenario name
if (roleId == null || roleId.trim().isEmpty()) {

    if (scenarioName.endsWith("_R01")) {
        roleId = "R01";
    } else if (scenarioName.endsWith("_R02")) {
        roleId = "R02";
    } else if (scenarioName.endsWith("_R03")) {
        roleId = "R03";
    }
}

if (roleId != null) {
    roleId = roleId.trim();
}

System.out.println("Role ID from Excel/Scenario: " + roleId);

        if (roleId != null) {
            roleId = roleId.trim();
        }

        System.out.println("Role ID from Excel: " + roleId);

        // Ensure authentication token is available
        if (SharedTestData.token == null
                || SharedTestData.token.trim().isEmpty()) {

            Map<String, String> loginData =
                    ExcelReader.readExcelData("Login", "Valid credential");

            String loginEndpoint = loginData.get("Endpoint");
            String loginBody = loginData.get("Body");

            Assert.assertNotNull(
                    loginEndpoint,
                    "Login endpoint not found in Excel."
            );

            Assert.assertNotNull(
                    loginBody,
                    "Login request body not found in Excel."
            );

            Response loginResponse = RestAssured.given()
                    .spec(RequestSpec.getRequestSpecWithoutAuth())
                    .body(loginBody)
                    .when()
                    .post(loginEndpoint);

            System.out.println(
                    "Login Status: " + loginResponse.getStatusCode()
            );

            Assert.assertEquals(
                    loginResponse.getStatusCode(),
                    200,
                    "Login failed. Unable to get authentication token."
            );

            String freshToken =
                    loginResponse.jsonPath().getString("token");

            Assert.assertNotNull(
                    freshToken,
                    "Authentication token was not returned from login."
            );

            Assert.assertFalse(
                    freshToken.trim().isEmpty(),
                    "Authentication token is empty."
            );

            SharedTestData.token = freshToken;

            System.out.println("Login successful. Token captured.");
        }

        // Create authenticated GET request
        request = given()
                .spec(RequestSpec.getRequestSpec());

        // ALL = no query parameter
        // R01/R02/R03 = ?id=R01/R02/R03
        if (roleId != null
                && !roleId.isEmpty()
                && !roleId.equalsIgnoreCase("All")) {

            request.queryParam("id", roleId);

            System.out.println(
                    "Final Request: "
                            + endpoint
                            + "?id="
                            + roleId
            );

        } else {

            System.out.println(
                    "Final Request: " + endpoint
            );
        }

        // Store request/data for ScenarioContext
        scenarioContext.setContext(
                "GET_USER_COUNT_REQUEST",
                request
        );

        scenarioContext.setContext(
                "GET_USER_COUNT_TEST_DATA",
                testData
        );
    }

    // ============================================================
    // NEGATIVE - INVALID ROLE ID
    // ============================================================

    @Given("Admin create GET request with invalid role id for {string} from excel sheet")
    public void admin_create_get_request_with_invalid_role_id(
            String scenarioName) throws IOException {

        System.out.println("==================================================");
        System.out.println("Scenario: " + scenarioName);

        // Read data from Excel
        testData = ExcelReader.readExcelData("User", scenarioName);

        // Get endpoint
        String endpoint = testData.get("EndPoint");

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = testData.get("Endpoint");
        }

        Assert.assertNotNull(endpoint, "Endpoint not found in Excel.");
        Assert.assertFalse(endpoint.trim().isEmpty(), "Endpoint is empty in Excel.");

        System.out.println("Endpoint from Excel: " + endpoint);

        // Generate dynamic invalid Role ID
        Random random = new Random();
        String roleId;

        do {
            roleId = String.format(
                    "R%02d",
                    random.nextInt(99) + 1
            );
        } while (
                "R01".equals(roleId)
                        || "R02".equals(roleId)
                        || "R03".equals(roleId)
        );

        System.out.println("Generated Invalid Role ID: " + roleId);

        // Verify generated ID is invalid
        String[] validRoleIds = {
                "R01",
                "R02",
                "R03"
        };

        boolean validRoleId = false;

        for (String validId : validRoleIds) {
            if (validId.equals(roleId)) {
                validRoleId = true;
                break;
            }
        }

        System.out.println("Is Role ID valid? " + validRoleId);

        Assert.assertFalse(
                validRoleId,
                "Generated Role ID should be invalid."
        );

        // Ensure authentication token is available
        if (SharedTestData.token == null
                || SharedTestData.token.trim().isEmpty()) {

            Map<String, String> loginData =
                    ExcelReader.readExcelData(
                            "Login",
                            "Valid credential"
                    );

            String loginEndpoint = loginData.get("Endpoint");
            String loginBody = loginData.get("Body");

            Assert.assertNotNull(
                    loginEndpoint,
                    "Login endpoint not found in Excel."
            );

            Assert.assertNotNull(
                    loginBody,
                    "Login request body not found in Excel."
            );

            Response loginResponse = RestAssured.given()
                    .spec(RequestSpec.getRequestSpecWithoutAuth())
                    .body(loginBody)
                    .when()
                    .post(loginEndpoint);

            System.out.println(
                    "Login Status: " + loginResponse.getStatusCode()
            );

            Assert.assertEquals(
                    loginResponse.getStatusCode(),
                    200,
                    "Login failed. Unable to get authentication token."
            );

            String freshToken =
                    loginResponse.jsonPath().getString("token");

            Assert.assertNotNull(
                    freshToken,
                    "Authentication token was not returned from login."
            );

            Assert.assertFalse(
                    freshToken.trim().isEmpty(),
                    "Authentication token is empty."
            );

            SharedTestData.token = freshToken;

            System.out.println(
                    "Fresh login successful. Token captured."
            );
        }

        // Create authenticated GET request
        request = given()
                .spec(RequestSpec.getRequestSpec())
                .queryParam("id", roleId);

        System.out.println(
                "Final Request: "
                        + endpoint
                        + "?id="
                        + roleId
        );

        // Store request/data for ScenarioContext
        scenarioContext.setContext(
                "GET_USER_COUNT_REQUEST",
                request
        );

        scenarioContext.setContext(
                "GET_USER_COUNT_TEST_DATA",
                testData
        );
    }

    // ============================================================
    // NEGATIVE - WITHOUT AUTHORIZATION
    // ============================================================

    @Given("Admin create GET request without authorization for {string} from excel sheet")
    public void admin_create_get_request_without_authorization(
            String scenarioName) throws IOException {

        RequestSpec.logScenarioName(scenarioName);

        testData = ExcelReader.readExcelData(
                "User",
                scenarioName
        );

        String endpoint = testData.get("EndPoint");

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = testData.get("Endpoint");
        }

        Assert.assertNotNull(
                endpoint,
                "Endpoint not found in Excel."
        );

        Assert.assertFalse(
                endpoint.trim().isEmpty(),
                "Endpoint is empty in Excel."
        );

        // IMPORTANT: no authorization
        request = RestAssured.given()
                .spec(RequestSpec.getRequestSpecWithoutAuth());

        System.out.println(
                "Scenario: " + scenarioName
        );

        System.out.println(
                "Endpoint from Excel: " + endpoint
        );

        // Store in this class and ScenarioContext
        scenarioContext.setContext(
                "GET_USER_COUNT_REQUEST",
                request
        );

        scenarioContext.setContext(
                "GET_USER_COUNT_TEST_DATA",
                testData
        );
    }

    // ============================================================
    // COMMON WHEN
    // ============================================================

    @When("Admin sends GET request to retrieve active and inactive user count")
    public void admin_sends_get_request_to_retrieve_active_and_inactive_user_count() {

        RequestSpecification scenarioRequest =
                (RequestSpecification) scenarioContext
                        .getContext("GET_USER_COUNT_REQUEST");

        @SuppressWarnings("unchecked")
        Map<String, String> scenarioTestData =
                (Map<String, String>) scenarioContext
                        .getContext("GET_USER_COUNT_TEST_DATA");

        // Use ScenarioContext first, then class fields
        if (scenarioRequest == null) {
            scenarioRequest = request;
        }

        if (scenarioTestData == null) {
            scenarioTestData = testData;
        }

        Assert.assertNotNull(
                scenarioRequest,
                "GET User Count request is null."
        );

        Assert.assertNotNull(
                scenarioTestData,
                "GET User Count test data is null."
        );

        // Get endpoint
        String endpoint = scenarioTestData.get("EndPoint");

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = scenarioTestData.get("Endpoint");
        }

        Assert.assertNotNull(
                endpoint,
                "Endpoint not found in Excel."
        );

        Assert.assertFalse(
                endpoint.trim().isEmpty(),
                "Endpoint is empty in Excel."
        );

        // Send GET request
        response = scenarioRequest
                .when()
                .get(endpoint);

        // Store response
        scenarioContext.setContext(
                "GET_USER_COUNT_RESPONSE",
                response
        );

        System.out.println(
                "Response Status: " + response.getStatusCode()
        );

        System.out.println(
                "Response Body: " + response.getBody().asString()
        );
    }

    // ============================================================
    // POSITIVE THEN
    // ============================================================

    @Then("Admin receives 200 OK status with response body for user count")
    public void admin_receives_200_ok_status_with_response_body_for_user_count() {

        int expectedStatusCode =
                Integer.parseInt(
                        testData.get("Response Code").trim()
                );

        int actualStatusCode =
                response.getStatusCode();

        System.out.println(
                "Expected Status: "
                        + expectedStatusCode
                        + " | Actual Status: "
                        + actualStatusCode
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "Unexpected HTTP status code."
        );

        String responseBody =
                response.getBody().asString();

        Assert.assertNotNull(
                responseBody,
                "Response body is null."
        );

        Assert.assertFalse(
                responseBody.trim().isEmpty(),
                "Response body is empty."
        );

        Assert.assertTrue(
                responseBody.trim().startsWith("["),
                "Response body should be a JSON array."
        );

        System.out.println(
                "Positive user count validation passed."
        );
    }

    // ============================================================
    // NEGATIVE THEN - INVALID ROLE ID
    // ============================================================

    @Then("Admin receives expected status code for invalid role id")
    public void admin_receives_expected_status_code_for_invalid_role_id() {

        int expectedStatusCode =
                Integer.parseInt(
                        testData.get("Response Code").trim()
                );

        int actualStatusCode =
                response.getStatusCode();

        System.out.println(
                "Expected Status: "
                        + expectedStatusCode
                        + " | Actual Status: "
                        + actualStatusCode
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "Invalid Role ID returned unexpected status code."
        );

        String responseBody =
                response.getBody().asString();

        System.out.println(
                "Invalid Role ID Response Body: "
                        + responseBody
        );

        Assert.assertTrue(
                responseBody.contains("RoleID"),
                "Response should contain RoleID."
        );

        Assert.assertTrue(
                responseBody.contains("not found"),
                "Response should contain 'not found'."
        );

        System.out.println(
                "Negative invalid Role ID validation passed."
        );
    }

    // ============================================================
    // NEGATIVE THEN - INVALID ENDPOINT
    // ============================================================

    @Then("Admin receives expected status code for invalid endpoint for user count")
    public void admin_receives_expected_status_code_for_invalid_endpoint_for_user_count() {

        @SuppressWarnings("unchecked")
        Map<String, String> scenarioTestData =
                (Map<String, String>) scenarioContext
                        .getContext("GET_USER_COUNT_TEST_DATA");

        Assert.assertNotNull(
                scenarioTestData,
                "GET User Count test data is null."
        );

        Response scenarioResponse =
                (Response) scenarioContext
                        .getContext("GET_USER_COUNT_RESPONSE");

        Assert.assertNotNull(
                scenarioResponse,
                "GET User Count response is null."
        );

        String expectedStatus =
                scenarioTestData.get("Response Code");

        Assert.assertNotNull(
                expectedStatus,
                "Response Code not found in Excel."
        );

        int expectedStatusCode =
                Integer.parseInt(expectedStatus.trim());

        int actualStatusCode =
                scenarioResponse.getStatusCode();

        System.out.println(
                "Expected Status: "
                        + expectedStatusCode
                        + " | Actual Status: "
                        + actualStatusCode
        );

        System.out.println(
                "Response: "
                        + scenarioResponse.getBody().asString()
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "Invalid endpoint returned unexpected status code."
        );

        System.out.println(
                "User Count invalid endpoint validation passed."
        );
    }

    // ============================================================
    // NEGATIVE THEN - WITHOUT AUTHORIZATION
    // ============================================================

    // ============================================================
// WITHOUT AUTHORIZATION THEN
// ============================================================

@Then("Admin receives expected status code for user without authorization")
public void admin_receives_expected_status_code_for_user_without_authorization() {

    // Get test data from ScenarioContext if this scenario was
    // executed by another Step Definition class.
    @SuppressWarnings("unchecked")
    Map<String, String> scenarioTestData =
            (Map<String, String>) scenarioContext
                    .getContext("GET_USER_COUNT_TEST_DATA");

    if (scenarioTestData == null) {
        scenarioTestData = testData;
    }

    Assert.assertNotNull(
            scenarioTestData,
            "GET User Count test data is null."
    );

    // The Get All Users When step stores its response here.
    Response scenarioResponse =
            (Response) scenarioContext
                    .getContext("GET_ALL_USERS_RESPONSE");

    // For Get User Count scenarios, the response is stored here.
    if (scenarioResponse == null) {
        scenarioResponse =
                (Response) scenarioContext
                        .getContext("GET_USER_COUNT_RESPONSE");
    }

    // Finally fall back to this class's response.
    if (scenarioResponse == null) {
        scenarioResponse = response;
    }

    Assert.assertNotNull(
            scenarioResponse,
            "GET User Count response is null."
    );

    String expectedStatus =
            scenarioTestData.get("Response Code");

    Assert.assertNotNull(
            expectedStatus,
            "Response Code not found in Excel."
    );

    int expectedStatusCode =
            Integer.parseInt(expectedStatus.trim());

    int actualStatusCode =
            scenarioResponse.getStatusCode();

    System.out.println(
            "Expected Status: "
                    + expectedStatusCode
                    + " | Actual Status: "
                    + actualStatusCode
    );

    System.out.println(
            "Response: "
                    + scenarioResponse.getBody().asString()
    );

    Assert.assertEquals(
            actualStatusCode,
            expectedStatusCode,
            "Without authorization returned unexpected status code."
    );

    System.out.println(
            "Get All Users without authorization validation passed."
    );
}
}