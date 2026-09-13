package stepDefinitions;

import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import utils.SharedTestData;
import utils.ExcelReader;
import utils.ScenarioContext;
import specBuilder.RequestSpec;

import java.io.IOException;
import java.util.Map;

public class GetAllUsersStepDef {

    private RequestSpecification request;
    private Response response;
    private Map<String, String> testData;

    private final ScenarioContext scenarioContext;

    public GetAllUsersStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    // ============================================================
    // GET ALL USERS - POSITIVE
    // ============================================================

    @Given("Admin create GET request with valid data for {string} from excel sheet")
    public void admin_create_get_request_with_valid_data_from_excel(
            String scenarioName) throws IOException {

        RequestSpec.logScenarioName(scenarioName);

        // Login first if token is not available
        if (SharedTestData.token == null
                || SharedTestData.token.trim().isEmpty()) {

            Map<String, String> loginData =
                    ExcelReader.readExcelData("Login", "Valid credential");

            String loginEndpoint = loginData.get("Endpoint");
            String loginBody = loginData.get("Body");

            Response loginResponse = RestAssured.given()
                    .spec(RequestSpec.getRequestSpecWithoutAuth())
                    .body(loginBody)
                    .when()
                    .post(loginEndpoint);

            Assert.assertEquals(
                    loginResponse.getStatusCode(),
                    200,
                    "Login failed. Unable to get authentication token."
            );

            SharedTestData.token =
                    loginResponse.jsonPath().getString("token");

            Assert.assertNotNull(
                    SharedTestData.token,
                    "Authentication token was not returned from login."
            );

            System.out.println("Login successful. Token captured.");
        }

        // Read Get All Users test data
        testData = ExcelReader.readExcelData("User", scenarioName);

        // Create authenticated GET request
        request = RestAssured.given()
                .spec(RequestSpec.getRequestSpec());

    }

    // ============================================================
    // GET ALL USERS - COMMON GET
    // ============================================================

    @When("Admin sends GET request to retrieve all users")
    public void admin_sends_get_request_to_retrieve_all_users() {

        /*
         * First try ScenarioContext.
         *
         * This is important for:
         * GetAllUsers_Without_Authorization
         *
         * because its Given step is defined in GetUserCountStepDef.
         */

        @SuppressWarnings("unchecked")
        Map<String, String> scenarioTestData =
                (Map<String, String>) scenarioContext
                        .getContext("GET_USER_COUNT_TEST_DATA");

        RequestSpecification scenarioRequest =
                (RequestSpecification) scenarioContext
                        .getContext("GET_USER_COUNT_REQUEST");

        // Use ScenarioContext values when available
        if (scenarioTestData != null) {
            testData = scenarioTestData;
        }

        if (scenarioRequest != null) {
            request = scenarioRequest;
        }

        // Verify test data exists
        Assert.assertNotNull(
                testData,
                "Get All Users test data is null."
        );

        // Verify request exists
        Assert.assertNotNull(
                request,
                "Get All Users request is null."
        );

        // Get endpoint
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

        System.out.println("Endpoint from Excel: " + endpoint);

        // Send GET request
        response = request
                .when()
                .get(endpoint);

        System.out.println(
                "Response Status: " + response.getStatusCode()
        );

        System.out.println(
                "Response Body: " + response.getBody().asString()
        );

        // Store response
        scenarioContext.setContext(
                "GET_ALL_USERS_RESPONSE",
                response
        );
    }

    // ============================================================
    // GET ALL USERS - POSITIVE THEN
    // ============================================================

    @Then("Admin receives 200 OK status with response body")
    public void admin_receives_200_ok_status_with_response_body() {

        Assert.assertNotNull(
                testData,
                "Get All Users test data is null."
        );

        Assert.assertNotNull(
                response,
                "Get All Users response is null."
        );

        int expectedStatusCode =
                Integer.parseInt(
                        testData.get("Response Code").trim()
                );

        int actualStatusCode =
                response.getStatusCode();

        System.out.println(
                "Expected Status: " + expectedStatusCode
                        + " | Actual Status: " + actualStatusCode
        );

        System.out.println(
                "Response: " + response.getBody().asString()
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "Status Code Mismatch!"
        );

        Assert.assertNotNull(
                response.getBody(),
                "Response body should not be null"
        );
    }

    // ============================================================
    // GET ALL USERS - INVALID ENDPOINT
    // ============================================================

    @Given("Admin create GET request with invalid input for {string} from excel sheet")
    public void admin_create_get_request_with_invalid_input_from_excel(
            String scenarioName) throws IOException {

        RequestSpec.logScenarioName(scenarioName);

        // Login first if token is not available
        if (SharedTestData.token == null
                || SharedTestData.token.trim().isEmpty()) {

            Map<String, String> loginData =
                    ExcelReader.readExcelData("Login", "Valid credential");

            String loginEndpoint = loginData.get("Endpoint");
            String loginBody = loginData.get("Body");

            Response loginResponse = RestAssured.given()
                    .spec(RequestSpec.getRequestSpecWithoutAuth())
                    .body(loginBody)
                    .when()
                    .post(loginEndpoint);

            Assert.assertEquals(
                    loginResponse.getStatusCode(),
                    200,
                    "Login failed. Unable to get authentication token."
            );

            SharedTestData.token =
                    loginResponse.jsonPath().getString("token");

            Assert.assertNotNull(
                    SharedTestData.token,
                    "Authentication token was not returned from login."
            );

            System.out.println("Login successful. Token captured.");
        }

        testData =
                ExcelReader.readExcelData("User", scenarioName);

        request = RestAssured.given()
                .spec(RequestSpec.getRequestSpec());
    }

    // ============================================================
    // GET ALL USERS - INVALID METHOD
    // ============================================================

    @Given("Admin create invalid request for {string} from excel sheet")
    public void admin_create_invalid_request_from_excel(
            String scenarioName) throws IOException {

        RequestSpec.logScenarioName(scenarioName);

        // Login first if token is not available
        if (SharedTestData.token == null
                || SharedTestData.token.trim().isEmpty()) {

            Map<String, String> loginData =
                    ExcelReader.readExcelData("Login", "Valid credential");

            String loginEndpoint = loginData.get("Endpoint");
            String loginBody = loginData.get("Body");

            Response loginResponse = RestAssured.given()
                    .spec(RequestSpec.getRequestSpecWithoutAuth())
                    .body(loginBody)
                    .when()
                    .post(loginEndpoint);

            Assert.assertEquals(
                    loginResponse.getStatusCode(),
                    200,
                    "Login failed. Unable to get authentication token."
            );

            SharedTestData.token =
                    loginResponse.jsonPath().getString("token");

            Assert.assertNotNull(
                    SharedTestData.token,
                    "Authentication token was not returned from login."
            );

            System.out.println("Login successful. Token captured.");
        }

        testData =
                ExcelReader.readExcelData("User", scenarioName);

        request = RestAssured.given()
                .spec(RequestSpec.getRequestSpec());
    }

    @When("Admin sends invalid method request to retrieve all users")
    public void admin_sends_invalid_method_request_to_retrieve_all_users() {

        Assert.assertNotNull(
                testData,
                "Get All Users test data is null."
        );

        Assert.assertNotNull(
                request,
                "Get All Users request is null."
        );

        String endpoint = testData.get("EndPoint");

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = testData.get("Endpoint");
        }

        Assert.assertNotNull(
                endpoint,
                "Endpoint not found in Excel."
        );

        // Send POST to GET-only endpoint
        response = request
                .when()
                .post(endpoint);

        System.out.println("Invalid Method: POST");
        System.out.println("Final Request: " + endpoint);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
    }

    // ============================================================
    // INVALID ENDPOINT THEN
    // ============================================================

    @Then("Admin receives expected status code for invalid endpoint")
    public void admin_receives_expected_status_code_for_invalid_endpoint() {

        Assert.assertNotNull(
                testData,
                "Get All Users test data is null."
        );

        Assert.assertNotNull(
                response,
                "Get All Users response is null."
        );

        int expectedStatusCode =
                Integer.parseInt(
                        testData.get("Response Code").trim()
                );

        int actualStatusCode =
                response.getStatusCode();

        System.out.println(
                "Expected Status: " + expectedStatusCode
                        + " | Actual Status: " + actualStatusCode
        );

        System.out.println(
                "Response: " + response.getBody().asString()
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "Status Code Mismatch!"
        );
    }

    // ============================================================
    // INVALID METHOD THEN
    // ============================================================

    @Then("Admin receives expected status code for invalid method")
    public void admin_receives_expected_status_code_for_invalid_method() {

        Assert.assertNotNull(
                testData,
                "Get All Users test data is null."
        );

        Assert.assertNotNull(
                response,
                "Get All Users response is null."
        );

        int expectedStatusCode =
                Integer.parseInt(
                        testData.get("Response Code").trim()
                );

        int actualStatusCode =
                response.getStatusCode();

        System.out.println(
                "Expected Status: " + expectedStatusCode
                        + " | Actual Status: " + actualStatusCode
        );

        System.out.println(
                "Response: " + response.getBody().asString()
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "Status Code Mismatch!"
        );
    }

    // ============================================================
    // GET USER COUNT - INVALID METHOD
    // ============================================================

    @When("Admin sends invalid method request to retrieve active and inactive user count")
    public void admin_sends_invalid_method_request_to_retrieve_active_and_inactive_user_count() {

        Assert.assertNotNull(
                testData,
                "Get User Count test data is null."
        );

        Assert.assertNotNull(
                request,
                "Get User Count request is null."
        );

        String endpoint = testData.get("EndPoint");

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = testData.get("Endpoint");
        }

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = "users/byStatus";
        }

        // POST is invalid for GET endpoint
        response = request
                .when()
                .post(endpoint);

        System.out.println("Invalid Method: POST");
        System.out.println("Final Request: " + endpoint);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
    }
}