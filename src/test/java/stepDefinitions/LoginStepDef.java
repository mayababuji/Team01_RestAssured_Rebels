package stepDefinitions;

import configReader.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import specBuilder.ResponseSpec;
import utils.ExcelReader;
import utils.SharedTestData;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;

 // A single shared admin (reset.email) is used across Login positive, Forgot Password,
 // Reset Password and the Logout background. A separate account (inactive.email) is used
 // only for the inactive-user scenario.
 
public class LoginStepDef extends SharedTestData {

    private RequestSpecification requestSpec;
    private Response response;
    private Map<String, String> data;
    private String ScenarioName;

    // Token used only by the Reset Password and Logout scenarios. It is deliberately NOT the shared
    // SharedTestData.token, which the other modules use after a successful Sign In.
    private String scenarioToken;

    // Placeholders used inside the Excel "Body" column -> config key.
    // Any placeholder ending in _PASS is masked in the console log.
    private static final String[][] PLACEHOLDERS = {
            {"VALID_EMAIL", "reset.email"},
            {"VALID_PASS", "reset.password"},
            {"RESET_EMAIL", "reset.email"},
            {"RESET_PASS", "reset.password"},
            {"INACTIVE_EMAIL", "inactive.email"},
            {"INACTIVE_PASS", "inactive.password"}
    };

    // =====================================================================
    // PRECONDITIONS - fresh tokens (used in Background)
    // =====================================================================

    @Given("Admin has a fresh login token")
    public void admin_has_a_fresh_login_token() {
        // Logout uses the same shared admin as the rest of the Login module.
        scenarioToken = fetchLoginToken(requireConfig("reset.email"), requireConfig("reset.password"));
    }

    @Given("Admin has a fresh reset token from forgot password")
    public void admin_has_a_fresh_reset_token_from_forgot_password() {
        Response r = baseSpec()
                .body("{\"userLoginEmailId\":\"" + jsonEscape(requireConfig("reset.email")) + "\"}")
                .when().post(requireConfig("login.forgotPassword.endpoint"));
        Assert.assertEquals(r.getStatusCode(), 200,
                "Could not get a reset token from forgot password: " + r.getBody().asString());
        String resetToken = r.jsonPath().getString("token");
        Assert.assertNotNull(resetToken, "Forgot password response did not contain a token");
        scenarioToken = resetToken;
    }

    // =====================================================================
    // LOGIN
    // =====================================================================

    @Given("Admin sets No Auth")
    public void admin_sets_no_auth() {
        requestSpec = baseSpec();
    }

    @Given("Admin prepares login request body for {string} from Excel")
    public void admin_prepares_login_request_body(String scenarioName) throws IOException {
        prepareRequest("Login", scenarioName);
    }

    @When("Admin sends {string} request to {string}")
    public void admin_sends_request(String method, String endpoint) {
        sendRequest(method, endpoint);
    }

    @Then("Admin validates login response with status code")
    public void admin_validates_login_response_with_status_code() {
        assertStatusAndMessage();
        if (isSuccessResponse()) {
            response.then().assertThat()
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/Login/UserSignInSchema.json"));
            String capturedToken = response.jsonPath().getString("token");
            Assert.assertNotNull(capturedToken, "Token was not generated");
            token = capturedToken;
            System.out.println("Token captured and stored in SharedTestData");
        }
    }

    // =====================================================================
    // FORGOT PASSWORD
    // =====================================================================

    @Given("Admin sets No Auth for forgot password")
    public void admin_sets_no_auth_for_forgot_password() {
        requestSpec = baseSpec();
    }

    @Given("Admin prepares forgot password request body for {string} from Excel")
    public void admin_prepares_forgot_password_request_body(String scenarioName) throws IOException {
        prepareRequest("ForgotPassword", scenarioName);
    }

    @When("Admin sends {string} request to {string} for forgot password")
    public void admin_sends_request_for_forgot_password(String method, String endpoint) {
        sendRequest(method, endpoint);
    }

    @Then("Admin validates forgot password response with status code")
    public void admin_validates_forgot_password_response_with_status_code() {
        assertStatusAndMessage();
        if (isSuccessResponse()) {
            response.then().assertThat()
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/Login/ForgotPasswordSchema.json"));
        }
    }

    // =====================================================================
    // RESET PASSWORD
    // =====================================================================

    @Given("Admin sets {string} authentication for reset password")
    public void admin_sets_authentication_for_reset_password(String authentication) {
        applyAuthentication(authentication);
    }

    @Given("Admin prepares reset password request body for {string} from Excel")
    public void admin_prepares_reset_password_request_body(String scenarioName) throws IOException {
        prepareRequest("ResetPassword", scenarioName);
    }

    @When("Admin sends {string} request to {string} for reset password")
    public void admin_sends_request_for_reset_password(String method, String endpoint) {
        sendRequest(method, endpoint);
    }

    @Then("Admin validates reset password response with status code")
    public void admin_validates_reset_password_response_with_status_code() {
        assertStatusAndMessage();
        if (isSuccessResponse()) {
            response.then().assertThat()
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/Login/ResetPasswordSchema.json"));
            Assert.assertTrue(response.jsonPath().getBoolean("success"), "Expected success = true");
        }
    }

    // =====================================================================
    // LOGOUT
    // =====================================================================

    @Given("Admin sets {string} authentication for logout")
    public void admin_sets_authentication_for_logout(String authentication) {
        applyAuthentication(authentication);
    }

    @Given("Admin prepares logout request body for {string} from Excel")
    public void admin_prepares_logout_request_body(String scenarioName) throws IOException {
        prepareRequest("Logout", scenarioName);
    }

    @When("Admin sends {string} request to {string} for logout")
    public void admin_sends_request_for_logout(String method, String endpoint) {
        sendRequest(method, endpoint);
    }

    @Then("Admin validates logout response with status code")
    public void admin_validates_logout_response_with_status_code() {
        // Logout returns a plain string, so there is no JSON schema to check here.
        assertStatusAndMessage();
    }

    // =====================================================================
    // SHARED HELPERS
    // =====================================================================

    /** Reads a required config value and fails with a clear message if it is missing or blank. */
    private String requireConfig(String key) {
        String value = ConfigReader.get(key);
        Assert.assertNotNull(value, "Missing config key '" + key + "'. Add it to env.properties, "
                + "endpoints.properties or credentials-<env>.properties (or pass -D" + key + "=value).");
        return value;
    }

    private RequestSpecification baseSpec() {
        return given()
                .baseUri(requireConfig("base.url"))
                .header("Content-Type", "application/json");
    }

    private String fetchLoginToken(String email, String password) {
        Response r = baseSpec()
                .body("{\"userLoginEmailId\":\"" + jsonEscape(email) + "\",\"password\":\"" + jsonEscape(password) + "\"}")
                .when().post(requireConfig("login.endpoint"));
        Assert.assertEquals(r.getStatusCode(), 200, "Login failed while fetching a token: " + r.getBody().asString());
        String t = r.jsonPath().getString("token");
        Assert.assertNotNull(t, "Login response did not contain a token");
        return t;
    }

    /** Escapes quotes and backslashes so passwords with special characters stay valid JSON. */
    private String jsonEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /** Reads the Excel row for this scenario, then sets the body and any special request details. */
    private void prepareRequest(String sheetName, String scenarioName) throws IOException {
        this.ScenarioName = scenarioName;
        data = ExcelReader.readExcelData(sheetName, scenarioName);
        Assert.assertNotNull(data, "No Excel data found for '" + scenarioName + "' in sheet " + sheetName);
        if (requestSpec == null) {
            requestSpec = baseSpec();
        }
        setBody();
        setSpecialRequestDetails();
    }

    private void setBody() {
        if (scenarioHas("without request body")) {
            requestSpec.body("");
            return;
        }
        String body = data.get("Body");
        if (body != null && !body.isBlank()) {
            requestSpec.body(resolvePlaceholders(body, false));
        }
    }

    private void setSpecialRequestDetails() {
        if (scenarioHas("invalid content type")) {
            requestSpec.contentType("text/plain");
        }
        if (scenarioHas("invalid base url")) {
            requestSpec.baseUri(requireConfig("login.invalidBaseUri"));
        }
    }

    private void applyAuthentication(String authentication) {
        requestSpec = baseSpec();
        switch (authentication.trim().toLowerCase()) {
            case "bearer token":
                Assert.assertTrue(scenarioToken != null && !scenarioToken.isBlank(),
                        "No token available - make sure the Background token step ran");
                requestSpec.header("Authorization", "Bearer " + scenarioToken);
                break;
            case "no auth":
                break;
            case "expired token":
                requestSpec.header("Authorization", "Bearer " + requireConfig("expired.token"));
                break;
            case "empty token":
                requestSpec.header("Authorization", "Bearer ");
                break;
            case "invalid token":
                requestSpec.header("Authorization", "Bearer invalidtoken123");
                break;
            case "different user token":
                // Main admin's token, used against the shared admin's (reset.email) request body.
                String otherToken = fetchLoginToken(requireConfig("admin.email"), requireConfig("admin.password"));
                requestSpec.header("Authorization", "Bearer " + otherToken);
                break;
            default:
                Assert.fail("Unknown authentication type: " + authentication);
        }
    }

    private void sendRequest(String method, String endpointKey) {
        String actualEndpoint = resolveEndpoint(endpointKey);
        System.out.println("-----------------------------------");
        System.out.println("Scenario : " + ScenarioName);
        System.out.println("Method   : " + method);
        System.out.println("Endpoint : " + actualEndpoint);
        System.out.println("-----------------------------------");
        if ("POST".equalsIgnoreCase(method)) {
            response = requestSpec.when().post(actualEndpoint);
        } else if ("GET".equalsIgnoreCase(method)) {
            response = requestSpec.when().get(actualEndpoint);
        } else {
            Assert.fail("Unsupported HTTP method: " + method);
        }
        System.out.println("Request Body:");
        System.out.println(getMaskedRequestBody());
        System.out.println("-----------------------------------");
        System.out.println("Actual Status Code: " + response.getStatusCode());
        System.out.println("Response Body:");
        System.out.println(response.getBody().asString());
        System.out.println("-----------------------------------");
    }

    /** Maps the endpoint key used in the feature file to a value in endpoints.properties. */
    private String resolveEndpoint(String key) {
        switch (key) {
            case "loginEndpoint":                  return requireConfig("login.endpoint");
            case "invalidEndpoint":                return requireConfig("login.invalidEndpoint");
            case "forgotPasswordEndpoint":         return requireConfig("login.forgotPassword.endpoint");
            case "forgotPasswordInvalidEndpoint":  return requireConfig("login.forgotPassword.invalidEndpoint");
            case "resetPasswordEndpoint":          return requireConfig("login.resetPassword.endpoint");
            case "resetPasswordInvalidEndpoint":   return requireConfig("login.resetPassword.invalidEndpoint");
            case "logoutEndpoint":                 return requireConfig("login.logout.endpoint");
            case "logoutInvalidEndpoint":          return requireConfig("login.logout.invalidEndpoint");
            default:                               return key;
        }
    }

    /** Checks the status code (Excel may hold one code or a list like "200,400"), then the message. */
    private void assertStatusAndMessage() {
        Assert.assertNotNull(response, "Response is null");
        Assert.assertNotNull(data, "Test data was not initialized");
        String expected = data.get("ExpectedStatusCode");
        Assert.assertNotNull(expected, "ExpectedStatusCode missing from Excel for: " + ScenarioName);

        int actual = response.getStatusCode();
        System.out.println("-----------------------------------");
        System.out.println("Scenario        : " + ScenarioName);
        System.out.println("Expected Status : " + expected);
        System.out.println("Actual Status   : " + actual);
        System.out.println("-----------------------------------");

        boolean matchFound = false;
        for (String code : expected.split(",")) {
            try {
                if (actual == (int) Double.parseDouble(code.trim())) {
                    matchFound = true;
                }
            } catch (NumberFormatException e) {
                Assert.fail("Non-numeric ExpectedStatusCode in Excel: '" + code.trim() + "'");
            }
        }
        Assert.assertTrue(matchFound, "Status code " + actual + " not in expected: " + expected);
        validateResponseMessage();
    }

    private void validateResponseMessage() {
        String expectedMessage = data.get("ExpectedMessage");
        if (expectedMessage == null || expectedMessage.isBlank()) {
            return;
        }
        String actualMessage = ResponseSpec.getResponseMessage(response);
        if (actualMessage == null) {
            // e.g. logout returns a plain string, not JSON
            actualMessage = response.getBody().asString();
        }
        System.out.println("Expected Message : " + expectedMessage);
        System.out.println("Actual Message   : " + actualMessage);
        Assert.assertTrue(
                actualMessage != null && actualMessage.contains(expectedMessage),
                "Expected message: " + expectedMessage + " but actual message: " + actualMessage);
    }

    /** True for any 2xx status (some endpoints return 201 where the contract says 200). */
    private boolean isSuccessResponse() {
        return response.getStatusCode() / 100 == 2;
    }

    private boolean scenarioHas(String textInLowerCase) {
        return ScenarioName != null && ScenarioName.toLowerCase().contains(textInLowerCase);
    }

    private String resolvePlaceholders(String body, boolean maskPasswords) {
        for (String[] p : PLACEHOLDERS) {
            if (body.contains(p[0])) {
                String value = (maskPasswords && p[0].endsWith("_PASS"))
                        ? "****MASKED****"
                        : requireConfig(p[1]);
                body = body.replace(p[0], value);
            }
        }
        return body;
    }

    private String getMaskedRequestBody() {
        if (data == null || data.isEmpty()) {
            return "";
        }
        String body = data.get("Body");
        if (body == null || body.isBlank()) {
            return "";
        }
        return resolvePlaceholders(body, true);
    }
}