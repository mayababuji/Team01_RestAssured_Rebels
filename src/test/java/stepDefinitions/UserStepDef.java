package stepDefinitions;

import java.io.IOException;
import java.util.Map;
import org.testng.Assert;

import httpRequest.UserRequestParser;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.CreateUserRequest;
import specBuilder.RequestSpec;
import specBuilder.ResponseSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;
import utils.TestDataUtil;

public class UserStepDef {
    private RequestSpecification request;
    private Response response;
    private Map<String, String> testData;
    private final ScenarioContext scenarioContext;

    public UserStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }
    @Given("Admin sets Bearer token")
    public void admin_sets_bearer_token() {
    	 Boolean skipAuth = (Boolean) scenarioContext.getContext("SKIP_AUTH");
         if (Boolean.TRUE.equals(skipAuth)) {
             return; // do nothing, no auth header
         }

         RequestSpecification spec = RequestSpec.getRequestSpec();
         scenarioContext.setRequestSpec(spec);
     
    }

    @Given("Admin creates POST Request for the LMS API endpoint with data from Excel {string}")
    public void admin_creates_post_request_for_the_lms_api_endpoint_with_data_from_excel(String scenarioName)
            throws IOException {

        RequestSpec.logScenarioName(scenarioName);

        // 1. Fetch test data from Excel FIRST
        testData = ExcelReader.readExcelData("User", scenarioName);
        
        // Check both potential key names for body
        String rawRequestBody = testData.get("Body") != null ? testData.get("Body") : testData.get("RequestBody");

        if (rawRequestBody == null || rawRequestBody.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Request body for scenario '" + scenarioName + "' is null or empty in Excel.");
        }

        // 2. Replace dynamic placeholders (email/phone)
        String processedRequestBody = rawRequestBody
                .replace("<random_email>", TestDataUtil.randomEmail())
                .replace("<random_phone>", TestDataUtil.randomPhone());

        // 3. Configure Request Specification (Auth)
        if (scenarioName.contains("No_Auth")) {
            request = RestAssured.given().spec(RequestSpec.getRequestSpecWithoutAuth());
        } else if (scenarioName.contains("Invalid_Token")) {
            request = RestAssured.given().spec(RequestSpec.getRequestSpecWithCustomToken("invalid_token_12345"));
        } else {
            request = RestAssured.given().spec(RequestSpec.getRequestSpec());
        }

        // 4. Handle Content Type & Body Serialization
        if (scenarioName.contains("Invalid_Content_Type") || scenarioName.contains("InvalidContentType")) {
            request.contentType("text/plain").body(processedRequestBody);
        } else {
            // Parse processed JSON string into POJO
            CreateUserRequest userPayload = UserRequestParser.createUserParseData(processedRequestBody);
            
            // REST Assured auto-serializes POJO to JSON
            request.contentType(ContentType.JSON).body(userPayload);
        }
    }

    @When("Admin sends HTTPS Request and request Body for user")
    public void admin_sends_https_request_and_request_body_for_user() {

        String endpoint = testData.get("EndPoint") != null ? testData.get("EndPoint") : testData.get("Endpoint");
        
        // Read HTTP Method from Excel (e.g., GET, POST, PUT, DELETE)
        String httpMethod = testData.getOrDefault("Method", "POST").trim().toUpperCase();

        if (endpoint == null) {
            throw new IllegalStateException("Endpoint key was not found in testData map or contains a null value.");
        }

        if (testData.get("ScenarioName") != null && testData.get("ScenarioName").contains("InvalidContentType")) {
            request.contentType("text/plain");
        }

        switch (httpMethod) {
            case "GET":
                response = request.when().get(endpoint);
                break;
            default:
                response = request.when().post(endpoint);
                break;
        }
    }

    @Then("Admin receives StatusCode and response body for {string}")
    public void admin_receives_status_code_and_response_body_for(String scenario) {

        int expectedStatusCode = Integer.parseInt(testData.get("Response Code"));

        // Validate status code
        response.then().spec(ResponseSpec.status(expectedStatusCode));

        // Get message and fallback to full body if null
        String responseMessage = ResponseSpec.getResponseMessage(response);
        if (responseMessage == null || responseMessage.equals("null")) {
            responseMessage = response.getBody().asString();
        }

        System.out.println("Status: " + response.getStatusCode() + " | Message/Response: " + responseMessage);

        // Extract user ID on 201
        if (response.getStatusCode() == 201) {
            String generatedUserId = response.jsonPath().getString("userId");
            if (generatedUserId != null) {
                SharedTestData.userId = generatedUserId;
            }
        }
 
 
    }
    @Given("Admin creates GET Request for the LMS API endpoint with data from Excel {string}")
    public void admin_creates_get_request_for_the_lms_api_endpoint_with_data_from_excel(String scenarioName)
            throws IOException {
        
        RequestSpec.logScenarioName(scenarioName);
        testData = ExcelReader.readExcelData("User", scenarioName);

        String auth = testData.get("Auth");
        boolean isNoAuthScenario = scenarioName.toLowerCase().contains("no_auth") 
                || "No_Auth".equalsIgnoreCase(auth)
                || "None".equalsIgnoreCase(auth);

        if (isNoAuthScenario) {
            request = RestAssured.given().spec(RequestSpec.getRequestSpecWithoutAuth());
        } else {
            if (SharedTestData.token == null || SharedTestData.token.trim().isEmpty()) {
                throw new IllegalStateException("SharedTestData.token is null.");
            }
            request = RestAssured.given().spec(RequestSpec.getRequestSpec());
        }
    }

    @When("Admin sends HTTPS Request for Get All Active Users")
    public void admin_sends_https_request_for_get_all_active_users() {

        String endpoint = testData.get("EndPoint") != null ? testData.get("EndPoint") : testData.get("Endpoint");
        String method = testData.getOrDefault("Method", "GET");

        if (endpoint == null) {
            throw new IllegalStateException("Endpoint key was not found.");
        }

        switch (method.toUpperCase()) {
            case "POST":
                response = request.when().post(endpoint);
                break;
            case "PUT":
                response = request.when().put(endpoint);
                break;
            case "DELETE":
                response = request.when().delete(endpoint);
                break;
            default:
                response = request.when().get(endpoint);
                break;
        }
    }
}
