package stepDefinitions;


import configReader.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import specBuilder.RequestSpec;
import specBuilder.ResponseSpec;
import utils.ExcelReader;
import utils.SharedTestData;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.lessThan;

public class LoginStepDef extends SharedTestData {
    private Map<String, String> testData;
    private Response response;
    private RequestSpecification requestSpec;
    @Given("Admin has the test data for {string} from Excel with No Auth")
    public void admin_has_the_test_data_for_from_excel_with_no_auth(String scenarioName) throws IOException {

        testData = ExcelReader.readExcelData("Login", scenarioName);

        String requestBody = testData.get("Body");

        System.out.println(requestBody);

        RequestSpec.logScenarioName(scenarioName);

        requestSpec = given()
                .spec(RequestSpec.getRequestSpecWithoutAuth())
                .body(requestBody);
    }

    @When("Admin sends the post request for  Sign In")
    public void admin_sends_the_post_request_for_sign_in() {

        String endpoint = testData.get("Endpoint");

        System.out.println(ConfigReader.get("base.url") + endpoint);

        if (testData.get("ScenarioName").contains("InvalidContentType")) {
            requestSpec.contentType("text/plain");
        }

        response = requestSpec.when().post(endpoint);
    }
    @Then("Admin should receive the status code as  in Excel")
    public void admin_should_receive_the_status_code_as_in_excel() {

        int expectedStatusCode = Integer.parseInt(testData.get("ExpectedStatusCode"));
        String scenarioName = testData.get("ScenarioName");

        response.then().spec(ResponseSpec.status(expectedStatusCode));

        String contentType = response.getHeader("Content-Type");

        if (contentType != null && contentType.contains("application/json")) {

            // Response time check (functional + performance mix)
            response.then().time(lessThan(2000L));

            // Only validate schema + token for valid login
            if (response.getStatusCode() == 200 &&
                    "Valid credential".equals(scenarioName.trim())) {

                response.then().assertThat()
                        .body(matchesJsonSchemaInClasspath("schemas/Login/UserSignInSchema.json"));

                String capturedToken = response.jsonPath().getString("token");
                if (capturedToken != null) {
                    token = capturedToken;
                }


            }
        }
    }
}
