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
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.SharedTestData;
import utils.TestDataUtil;

public class UserStepDef {
	private RequestSpecification request;
	private Response response;

	private ExcelReader excelReader = new ExcelReader();
	private Map<String, String> testData;

	@Given("Admin sets Bearer token")
	public void admin_sets_bearer_token() {
		SharedTestData.generateAndSetToken();

	}

	@Given("Admin creates POST Request for the LMS API endpoint with data from Excel {string}")
	public void admin_creates_post_request_for_the_lms_api_endpoint_with_data_from_excel(String scenarioName)
			throws IOException {

		RequestSpec.logScenarioName(scenarioName);

		testData = ExcelReader.readExcelData("User", scenarioName);
		String rawRequestBody = testData.get("Body");

		if (rawRequestBody == null || rawRequestBody.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Request body for scenario '" + scenarioName + "' is null or empty in Excel.");
		}
		String processedRequestBody = rawRequestBody
	            .replace("<random_email>", TestDataUtil.randomEmail())
	            .replace("<random_phone>", TestDataUtil.randomPhone());
		request = RestAssured.given().spec(RequestSpec.getRequestSpec()).body(processedRequestBody);
	}

	@When("Admin sends HTTPS Request and request Body for user")
	public void admin_sends_https_request_and_request_body_for_user1() {

		String endpoint = testData.get("EndPoint") != null ? testData.get("EndPoint") : testData.get("Endpoint");

		if (endpoint == null) {
			throw new IllegalStateException("Endpoint key was not found in testData map or contains a null value.");
		}

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

		Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status Code Mismatch for scenario: " + scenario);

	}

	@Given("Admin creates GET Request for the LMS API endpoint with data from Excel {string}")
	public void admin_creates_get_request_for_the_lms_api_endpoint_with_data_from_excel(String scenarioName)
			throws IOException {
		RequestSpec.logScenarioName(scenarioName);
		testData = ExcelReader.readExcelData("User", scenarioName);

		String auth = testData.get("Auth");
		boolean isNoAuthScenario = scenarioName.toLowerCase().contains("no_auth") || "No_Auth".equalsIgnoreCase(auth)
				|| "None".equalsIgnoreCase(auth);

		if (isNoAuthScenario) {
		
			request = RestAssured.given().spec(RequestSpec.getRequestSpecWithoutAuth());
		} else {
		
			if (SharedTestData.token == null || SharedTestData.token.trim().isEmpty()) {
				throw new IllegalStateException(
						"SharedTestData.token is null.");
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

		}
	}

}
