package stepDefinitions;

import java.io.IOException;
import java.util.Map;
import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.CreateUserResponse;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;
import utils.TestDataUtil;

public class UserStepDef {

    private Map<String, String> testData;
    private final ScenarioContext scenarioContext;
    
    public UserStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("Admin sets Bearer token")
    public void admin_sets_bearer_token() {
       // SharedTestData.generateAndSetToken();

        scenarioContext.setRequestSpec(RequestSpec.getRequestSpec());
    }
 // Create user
    @Given("Admin creates POST Request for the LMS API endpoint with data from Excel {string}")
    public void admin_creates_post_request_for_the_lms_api_endpoint_with_data_from_excel(String scenarioName) throws IOException {
        RequestSpec.logScenarioName(scenarioName);
        testData = ExcelReader.readExcelData("User", scenarioName);

        String rawRequestBody = testData.get("Body");
        String processedRequestBody = rawRequestBody != null ? rawRequestBody
                .replace("<random_email>", TestDataUtil.randomEmail())
                .replace("<random_phone>", TestDataUtil.randomPhone()) : "";

        String auth = testData.get("Auth");
        boolean isNoAuthScenario = scenarioName.toLowerCase().contains("no_auth") 
                || "No_Auth".equalsIgnoreCase(auth)
                || "None".equalsIgnoreCase(auth);

        RequestSpecification baseSpec = isNoAuthScenario ? RequestSpec.getRequestSpecWithoutAuth()
                : RequestSpec.getRequestSpec();

        String endpoint = testData.get("Endpoint") != null ? testData.get("Endpoint") : testData.get("EndPoint");
        RequestSpecification requestSpec = RestAssured.given().spec(baseSpec).basePath(endpoint)
                .body(processedRequestBody);

        if (scenarioName.contains("Invalid_Content_Type") || scenarioName.contains("InvalidContentType")) {
            requestSpec.contentType("text/plain");
        }

        scenarioContext.setRequestSpec(requestSpec);
    }

    @When("Admin sends HTTPS Request and request Body for user")
    public void admin_sends_https_request_and_request_body_for_user() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException("RequestSpecification in ScenarioContext is null.");
        }

        if (testData != null && testData.get("ScenarioName") != null && testData.get("ScenarioName").contains("InvalidContentType")) {
            requestSpec.contentType("text/plain");
        }

        Response response = requestSpec.when().post();
        scenarioContext.setResponse(response);
    }
//GET all active User//Get all Actve Email//Get all Roles
    @Given("Admin creates GET Request for the LMS API endpoint with data from Excel {string}")
    public void admin_creates_get_request_for_the_lms_api_endpoint_with_data_from_excel(String scenarioName) throws IOException {
        RequestSpec.logScenarioName(scenarioName);
        testData = ExcelReader.readExcelData("User", scenarioName);

        String endpoint = testData.get("EndPoint") != null ? testData.get("EndPoint") : testData.get("Endpoint");
        if (endpoint == null) {
            throw new IllegalStateException("Endpoint is not found for scenario: " + scenarioName);
        }

        String auth = testData.get("Auth");
        boolean isNoAuthScenario = scenarioName.toLowerCase().contains("no_auth") 
                || "No_Auth".equalsIgnoreCase(auth) 
                || "None".equalsIgnoreCase(auth);

        RequestSpecification baseSpec;
        if (isNoAuthScenario) {
            baseSpec = RequestSpec.getRequestSpecWithoutAuth();
        } else {
            if (SharedTestData.token == null || SharedTestData.token.trim().isEmpty()) {
                throw new IllegalStateException("SharedTestData.token is null.");
            }
            baseSpec = RequestSpec.getRequestSpec();
        }

        RequestSpecification requestSpec = RestAssured.given()
                .spec(baseSpec)
                .basePath(endpoint);

        scenarioContext.setRequestSpec(requestSpec);
    }

    @When("Admin sends HTTPS Request for Get All Active Users")
    public void admin_sends_https_request_for_get_all_active_users() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException("RequestSpecification in ScenarioContext is null.");
        }

        String method = "GET";
        if (testData != null && testData.containsKey("Method") && testData.get("Method") != null) {
            method = testData.get("Method").trim().toUpperCase();
        }

        Response executedResponse = "POST".equalsIgnoreCase(method) ? requestSpec.when().post() : requestSpec.when().get();
        scenarioContext.setResponse(executedResponse);
    }

    @When("Admin sends HTTPS Request for Get All User Roles")
    public void admin_sends_https_request_for_get_all_user_roles() {
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();

        if (requestSpec == null) {
            throw new IllegalStateException("RequestSpecification in ScenarioContext is null.");
        }

        String method = testData != null ? testData.getOrDefault("Method", "GET") : "GET";
        Response executedResponse;

        switch (method.toUpperCase()) {
            case "POST":
                executedResponse = requestSpec.when().post();
                break;
            case "GET":
            default:
                executedResponse = requestSpec.when().get();
                break;
        }

        scenarioContext.setResponse(executedResponse);
    }
//Common Then
    @Then("Admin receives StatusCode and response body for {string}")
    public void admin_receives_status_code_and_response_body_for(String scenario) {
    	
        Response response = scenarioContext.getResponse();

        String rawStatusCode = testData != null ? testData.get("Response Code") : null;
        if (rawStatusCode == null) {
            throw new IllegalStateException("'Response Code' key is missing: " + scenario);
        }

        int expectedStatusCode = Integer.parseInt(rawStatusCode.trim());
        int actualStatusCode = response.getStatusCode();

        System.out.println("\n==================================================");
        System.out.println(" SCENARIO : " + scenario);
        System.out.println(" STATUS   : " + actualStatusCode + " (Expected: " + expectedStatusCode + ")");
        System.out.println(" RESPONSE :");
        System.out.println(response.getBody().asString());
        System.out.println("==================================================\n");
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status Code Mismatch for scenario: " + scenario);
//        deserialization
        if (actualStatusCode == 201 || actualStatusCode == 200) {
            try {
                CreateUserResponse userResponse = response.as(CreateUserResponse.class);
                String extractedUserId = userResponse.getSafeUserId();
                String extractedRoleId = userResponse.getSafeRoleId();

                if (extractedUserId != null) SharedTestData.userId = extractedUserId;
                if (extractedRoleId != null) SharedTestData.roleId = extractedRoleId;
            } catch (Exception e) {
                System.out.println("Response body deserialization skipped: " + e.getMessage());
            }
        }
    }
}