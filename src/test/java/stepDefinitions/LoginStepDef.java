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

public class LoginStepDef extends SharedTestData {
    private RequestSpecification requestSpec;
    private Response response;
    private Map<String, String> data;
    private String ScenarioName;

    // LOGIN

    @Given("Admin sets No Auth")
    public void admin_sets_no_auth() {
        requestSpec = given()
                .baseUri(ConfigReader.get("base.url"))
                .header("Content-Type", "application/json");
    }

    @Given("Admin prepares login request body for {string} from Excel")
    public void admin_prepares_login_request_body(String scenarioName) throws IOException {
        this.ScenarioName = scenarioName;
        data = ExcelReader.readExcelData("Login", ScenarioName);
        setRequestBody();
        setSpecialRequestDetails();
    }

    @When("Admin sends {string} request to {string}")
    public void admin_sends_request(String method, String endpoint) {
        String actualEndpoint = getEndpoint(endpoint);
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

    @Then("Admin validates login response with status code")
    public void admin_validates_login_response_with_status_code() {
        Assert.assertNotNull(response, "Response is null");
        int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode"));
        int actualStatusCode = response.getStatusCode();
        System.out.println("-----------------------------------");
        System.out.println("Scenario        : " + ScenarioName);
        System.out.println("Expected Status : " + expectedStatusCode);
        System.out.println("Actual Status   : " + actualStatusCode);
        System.out.println("-----------------------------------");
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status code does not match");
        validateResponseMessage();
        if (expectedStatusCode == 200) {
            response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/Login/UserSignInSchema.json"));
            String capturedToken = response.jsonPath().getString("token");
            Assert.assertNotNull(capturedToken, "Token was not generated");
            token = capturedToken;
            System.out.println("Token Captured and Stored in SharedTestData: " + token);
        }
    }
    // FORGOT PASSWORD

    @Given("Admin sets No Auth for forgot password")
    public void admin_sets_no_auth_for_forgot_password() {
        requestSpec = given()
                .baseUri(ConfigReader.get("base.url"))
                .header("Content-Type", "application/json");
    }

    @Given("Admin prepares forgot password request body for {string} from Excel")
    public void admin_prepares_forgot_password_request_body(String scenarioName) throws IOException {
        this.ScenarioName = scenarioName;
        data = ExcelReader.readExcelData("ForgotPassword", ScenarioName);
        setForgotPasswordRequestBody();
        setForgotPasswordSpecialDetails();
    }

    @When("Admin sends {string} request to {string} for forgot password")
    public void admin_sends_request_for_forgot_password(String method, String endpoint) {
        String actualEndpoint = getForgotPasswordEndpoint(endpoint);
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

    @Then("Admin validates forgot password response with status code")
    public void admin_validates_forgot_password_response_with_status_code() {
        Assert.assertNotNull(response, "Response is null");
        int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode"));
        int actualStatusCode = response.getStatusCode();
        System.out.println("-----------------------------------");
        System.out.println("Scenario        : " + ScenarioName);
        System.out.println("Expected Status : " + expectedStatusCode);
        System.out.println("Actual Status   : " + actualStatusCode);
        System.out.println("-----------------------------------");
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status code does not match");
        validateResponseMessage();
    }
    // RESET PASSWORD

    @Given("Admin sets {string} authentication for reset password")
    public void admin_sets_authentication_for_reset_password(String authentication) {
        requestSpec = given()
                .baseUri(ConfigReader.get("base.url"))
                .header("Content-Type", "application/json");
        if ("Bearer Token".equalsIgnoreCase(authentication)) {
            if (token == null || token.isBlank()) {
                SharedTestData.generateAndSetToken();
            }
            requestSpec.header("Authorization", "Bearer " + token);
        } else if ("No Auth".equalsIgnoreCase(authentication)) {
            // No Authorization header
        } else if ("Expired Token".equalsIgnoreCase(authentication)) {
            String expiredToken = ConfigReader.get("expired.token");
            Assert.assertNotNull(expiredToken, "expired.token is not configured.");
            requestSpec.header("Authorization", "Bearer " + expiredToken);
        } else if ("Empty Token".equalsIgnoreCase(authentication)) {
            requestSpec.header("Authorization", "Bearer ");
        } else {
            Assert.fail("Unknown authentication type: " + authentication);
        }
    }

    @Given("Admin prepares reset password request body for {string} from Excel")
    public void admin_prepares_reset_password_request_body(String scenarioName) throws IOException {
        this.ScenarioName = scenarioName;
        data = ExcelReader.readExcelData("ResetPassword", ScenarioName);
        setResetPasswordRequestBody();
    }

    @When("Admin sends {string} request to {string} for reset password")
    public void admin_sends_request_for_reset_password(String method, String endpoint) {
        String actualEndpoint = getResetPasswordEndpoint(endpoint);
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

    @Then("Admin validates reset password response with status code")
    public void admin_validates_reset_password_response_with_status_code() {
        Assert.assertNotNull(response, "Response is null");
        Assert.assertNotNull(data, "Test data was not initialized");
        String statusStr = data.get("ExpectedStatusCode");
        Assert.assertNotNull(statusStr, "ExpectedStatusCode missing from Excel");
        int actualStatusCode = response.getStatusCode();
        System.out.println("-----------------------------------");
        System.out.println("Scenario        : " + ScenarioName);
        System.out.println("Expected Status : " + statusStr);
        System.out.println("Actual Status   : " + actualStatusCode);
        System.out.println("-----------------------------------");
        if (statusStr.contains(",")) {
            String[] expectedCodes = statusStr.split(",");
            boolean matchFound = false;
            for (String code : expectedCodes) {
                try {
                    if (actualStatusCode == Integer.parseInt(code.trim())) {
                        matchFound = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                    Assert.fail("Non-numeric status code in Excel: '" + code.trim() + "'");
                }
            }
            Assert.assertTrue(matchFound,
                "Status code " + actualStatusCode + " not in expected: " + statusStr);
        } else {
            try {
                int expectedStatusCode = Integer.parseInt(statusStr);
                Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status code does not match");
            } catch (NumberFormatException e) {
                Assert.fail("Non-numeric ExpectedStatusCode: '" + statusStr + "'");
            }
        }
        validateResponseMessage();
    }
    // LOGOUT

    @Given("Admin sets {string} authentication for logout")
    public void admin_sets_authentication_for_logout(String authentication) {
        requestSpec = given()
                .baseUri(ConfigReader.get("base.url"))
                .header("Content-Type", "application/json");
        if ("Bearer Token".equalsIgnoreCase(authentication)) {
            if (token == null || token.isBlank()) {
                SharedTestData.generateAndSetToken();
            }
            requestSpec.header("Authorization", "Bearer " + token);
        } else if ("No Auth".equalsIgnoreCase(authentication)) {
            // No Authorization header
        } else if ("Invalid Token".equalsIgnoreCase(authentication)) {
            requestSpec.header("Authorization", "Bearer invalidtoken123");
        } else if ("Expired Token".equalsIgnoreCase(authentication)) {
            String expiredToken = ConfigReader.get("expired.token");
            Assert.assertNotNull(expiredToken, "expired.token is not configured.");
            requestSpec.header("Authorization", "Bearer " + expiredToken);
        } else {
            Assert.fail("Unknown authentication type: " + authentication);
        }
    }

    @Given("Admin prepares logout request body for {string} from Excel")
    public void admin_prepares_logout_request_body(String scenarioName) throws IOException {
        this.ScenarioName = scenarioName;
        data = ExcelReader.readExcelData("Logout", ScenarioName);
    }

    @When("Admin sends {string} request to {string} for logout")
    public void admin_sends_request_for_logout(String method, String endpoint) {
        String actualEndpoint = getLogoutEndpoint(endpoint);
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

    @Then("Admin validates logout response with status code")
    public void admin_validates_logout_response_with_status_code() {
        Assert.assertNotNull(response, "Response is null");
        int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode"));
        int actualStatusCode = response.getStatusCode();
        System.out.println("-----------------------------------");
        System.out.println("Scenario        : " + ScenarioName);
        System.out.println("Expected Status : " + expectedStatusCode);
        System.out.println("Actual Status   : " + actualStatusCode);
        System.out.println("-----------------------------------");
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status code does not match");
        validateResponseMessage();
    }

    // helper methods - LOGIN
   

    private void setRequestBody() {
        String body = data.get("Body");
        if ("Without request body".equalsIgnoreCase(ScenarioName)) {
            requestSpec.body("");
            return;
        }
        if (body != null && !body.isBlank()) {
            String email = ConfigReader.get("admin.email");
            String password = ConfigReader.get("admin.password");
            body = body.replace("VALID_EMAIL", email);
            body = body.replace("VALID_PASS", password);
            requestSpec.body(body);
        }
    }

    private void setSpecialRequestDetails() {
        if ("Invalid content type".equalsIgnoreCase(ScenarioName)) {
            requestSpec.contentType("text/plain");
        }
        if ("Invalid base URL".equalsIgnoreCase(ScenarioName)) {
            requestSpec.baseUri(ConfigReader.get("login.invalidBaseUri"));
        }
        if ("Without request body".equalsIgnoreCase(ScenarioName)) {
            requestSpec.body("");
        }
    }

    private String getEndpoint(String endpoint) {
        if ("loginEndpoint".equalsIgnoreCase(endpoint)) {
            return ConfigReader.get("login.endpoint");
        }
        if ("invalidEndpoint".equalsIgnoreCase(endpoint)) {
            return ConfigReader.get("login.invalidEndpoint");
        }
        return endpoint;
    }
    // helper methods - FORGOT PASSWORD

    private void setForgotPasswordRequestBody() {
        String body = data.get("Body");
        if ("Without request body".equalsIgnoreCase(ScenarioName)) {
            requestSpec.body("");
            return;
        }
        if (body != null && !body.isBlank()) {
            String email = ConfigReader.get("admin.email");
            body = body.replace("VALID_EMAIL", email);
            requestSpec.body(body);
        }
    }
    private void setForgotPasswordSpecialDetails() {
    if ("Invalid content type".equalsIgnoreCase(ScenarioName)) {
        requestSpec.contentType("text/plain");
    }
}

    private String getForgotPasswordEndpoint(String endpoint) {
        if ("forgotPasswordEndpoint".equalsIgnoreCase(endpoint)) {
            return ConfigReader.get("login.forgotPassword.endpoint");
        }
        if ("forgotPasswordInvalidEndpoint".equalsIgnoreCase(endpoint)) {
            return ConfigReader.get("login.forgotPassword.invalidEndpoint");
        }
        return endpoint;
    }
    // helper methods - RESET PASSWORD

    private void setResetPasswordRequestBody() {
        String body = data.get("Body");
        if ("Without request body".equalsIgnoreCase(ScenarioName)) {
            requestSpec.body("");
            return;
        }
        if (body != null && !body.isBlank()) {
            String email = ConfigReader.get("admin.email");
            String password = ConfigReader.get("admin.password");
            body = body.replace("VALID_EMAIL", email);
            body = body.replace("VALID_PASS", password);
            requestSpec.body(body);
        }
    }

    private String getResetPasswordEndpoint(String endpoint) {
        if ("resetPasswordEndpoint".equalsIgnoreCase(endpoint)) {
            return ConfigReader.get("login.resetPassword.endpoint");
        }
        if ("resetPasswordInvalidEndpoint".equalsIgnoreCase(endpoint)) {
            return ConfigReader.get("login.resetPassword.invalidEndpoint");
        }
        return endpoint;
    }

    // helper methods - LOGOUT

    private String getLogoutEndpoint(String endpoint) {
        if ("logoutEndpoint".equalsIgnoreCase(endpoint)) {
            return ConfigReader.get("login.logout.endpoint");
        }
        if ("logoutInvalidEndpoint".equalsIgnoreCase(endpoint)) {
            return ConfigReader.get("login.logout.invalidEndpoint");
        }
        return endpoint;
    }
    // SHARED HELPERS

    private void validateResponseMessage() {
        String expectedMessage = data.get("ExpectedMessage");
        if (expectedMessage == null || expectedMessage.isBlank()) {
            return;
        }
        String actualMessage = ResponseSpec.getResponseMessage(response);
        System.out.println("Expected Message : " + expectedMessage);
        System.out.println("Actual Message   : " + actualMessage);
        Assert.assertTrue(
                actualMessage != null && actualMessage.contains(expectedMessage),
                "Expected message: " + expectedMessage + " but actual message: " + actualMessage
        );
    }

    private String getMaskedRequestBody() {
        if (data == null || data.isEmpty()) {
            return "";
        }
        String body = data.get("Body");
        if (body == null || body.isBlank()) {
            return "";
        }
        body = body.replace("VALID_EMAIL", ConfigReader.get("admin.email"));
        body = body.replace("VALID_PASS", "****MASKED****");
        return body;
    }
}