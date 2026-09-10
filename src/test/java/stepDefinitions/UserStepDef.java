package stepDefinitions;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;

import configReader.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import specBuilder.RequestSpec;
import utils.ExcelReader;


public class UserStepDef {
	private RequestSpecification request;
    private Response response;
  
    private ExcelReader excelReader = new ExcelReader();
    private Map<String, String> testData;
	
	@Given("Admin sets Bearer token")
	public void admin_sets_bearer_token() {
		RestAssured.given().spec(RequestSpec.getRequestSpec());
	}

	@Given("Admin creates POST Request for the LMS API endpoint with data from Excel {string}")
	public void admin_creates_post_request_for_the_lms_api_endpoint_with_data_from_excel(String scenarioName) throws IOException {
	
		    
		    RequestSpec.logScenarioName(scenarioName);

		  
		    testData = ExcelReader.readExcelData("User", scenarioName);

		    String requestBody = testData.get("Body");

		    request = RestAssured.given()
		            .spec(RequestSpec.getRequestSpec())
		            .body(requestBody);
		
	}
	@When("Admin sends HTTPS Request and request Body for user1")
	public void admin_sends_https_request_and_request_body_for_user1() {
	   
	    String endpoint = testData.get("EndPoint") != null ? testData.get("EndPoint") : testData.get("Endpoint");

	 
	    if (endpoint == null) {
	        throw new IllegalStateException("Endpoint key was not found in testData map or contains a null value.");
	    }

	    System.out.println("Executing POST Request to: " + ConfigReader.get("base.url") + endpoint);

	    if (testData.get("ScenarioName") != null && testData.get("ScenarioName").contains("InvalidContentType")) {
	        request.contentType("text/plain");
	    }
	    response = request.when().post(endpoint);
	}

	@Then("Admin receives StatusCode and response body for {string}")
	public void admin_receives_status_code_and_response_body_for(String scenario) {
	   
	    int expectedStatusCode = Integer.parseInt(testData.get("Response Code"));
	    int actualStatusCode = response.getStatusCode();

	    System.out.println("Status: " + actualStatusCode + " | Response: " + response.getBody().asString());

	    Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status Code Mismatch!");

	    if (actualStatusCode == 201) {
	        Assert.assertNotNull(response.jsonPath().get("userId"), "User ID should not be null in response!");
	    }
	}

}

