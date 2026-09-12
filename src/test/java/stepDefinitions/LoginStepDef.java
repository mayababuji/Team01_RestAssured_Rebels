package stepDefinitions;

import configReader.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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
            response = requestSpec.when().log().all().post(actualEndpoint);
        } else if ("GET".equalsIgnoreCase(method)) {
            response = requestSpec.when().log().all().get(actualEndpoint);
        } else {
            Assert.fail("Unsupported HTTP method: " + method);
        }

        System.out.println("Actual Status Code: " + response.getStatusCode());
    }

    @Then("Admin validates login response with status code {string}")
    public void admin_validates_login_response(String statusCode) {
        Assert.assertNotNull(response, "Response is null");

        int expectedStatusCode = Integer.parseInt(statusCode);
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
                    .body(io.restassured.module.jsv.JsonSchemaValidator
                            .matchesJsonSchemaInClasspath("schemas/Login/UserSignInSchema.json"));

            String capturedToken = response.jsonPath().getString("token");
            Assert.assertNotNull(capturedToken, "Token was not generated");
            token = capturedToken;
            System.out.println("Token Captured and Stored in SharedTestData: " + token);
        }
    }

    private void setRequestBody() {
        String body = data.get("Body");

        if ("Valid credential".equalsIgnoreCase(ScenarioName)) {
            String email = ConfigReader.get("admin.email");
            String password = ConfigReader.get("admin.password");
            body = "{\"userLoginEmailId\":\"" + email + "\",\"password\":\"" + password + "\"}";
        }

        if (body != null && !body.isBlank()) {
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
}