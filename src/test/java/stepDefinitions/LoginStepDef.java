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

    // User Sign In Steps
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
      // Forgot Password steps
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
    // helper methods login
    private void setRequestBody() {
        String body = data.get("Body");
        String scenario = ScenarioName.toLowerCase();
        if ("without request body".equals(scenario)) {
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
    private void setForgotPasswordRequestBody() {
        String body = data.get("Body");
        String scenario = ScenarioName.toLowerCase();
        if ("without request body".equals(scenario)) {
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
    // Shared helper methods
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
        String body = data.get("Body");
        if (body == null || body.isBlank()) {
            return "";
        }
        body = body.replace("VALID_EMAIL", ConfigReader.get("admin.email"));
        body = body.replace("VALID_PASS", "****MASKED****");
        return body;
    }
}