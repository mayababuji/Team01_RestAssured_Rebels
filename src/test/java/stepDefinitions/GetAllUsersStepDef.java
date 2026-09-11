package stepDefinitions;

import org.testng.Assert;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import io.restassured.RestAssured;
import io.restassured.response.Response;


import utils.SharedTestData;
import io.restassured.specification.RequestSpecification;


import specBuilder.RequestSpec;
import utils.ExcelReader;


import java.io.IOException;
import java.util.Map;


public class GetAllUsersStepDef {


   private RequestSpecification request;
   private Response response;
   private Map<String, String> testData;
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


 

   @When("Admin sends GET request to retrieve all users")
   public void admin_sends_get_request_to_retrieve_all_users() {


       String endpoint = testData.get("EndPoint");


       if (endpoint == null) {
           endpoint = testData.get("Endpoint");
       }


       if (endpoint == null || endpoint.trim().isEmpty()) {
           throw new IllegalStateException(
                   "Endpoint not found for scenario: "
                           + testData.get("ScenarioName"));
       }


       response = request.when().get(endpoint);
   }



   @Then("Admin receives 200 OK status with response body")
   public void admin_receives_200_ok_status_with_response_body() {


       int expectedStatusCode =
               Integer.parseInt(testData.get("Response Code"));


       int actualStatusCode = response.getStatusCode();


       System.out.println(
               "Expected Status: " + expectedStatusCode
                       + " | Actual Status: " + actualStatusCode);


       System.out.println(
               "Response: " + response.getBody().asString());


       Assert.assertEquals(
               actualStatusCode,
               expectedStatusCode,
               "Status Code Mismatch!");


       Assert.assertNotNull(
               response.getBody(),
               "Response body should not be null");
   }




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


       // Read invalid endpoint test data
       testData = ExcelReader.readExcelData("User", scenarioName);


       // Create authenticated request
       request = RestAssured.given()
               .spec(RequestSpec.getRequestSpec());
   }




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




       testData = ExcelReader.readExcelData("User", scenarioName);
      
       request = RestAssured.given()
               .spec(RequestSpec.getRequestSpec());
   }


  @When("Admin sends invalid method request to retrieve all users")
public void admin_sends_invalid_method_request_to_retrieve_all_users() {


   String endpoint = testData.get("EndPoint");


   if (endpoint == null) {
       endpoint = testData.get("Endpoint");
   }


   // Send POST to the GET-only endpoint
   response = request.when().post(endpoint);
}


@Given("Admin create GET request without authorization for {string} from excel sheet")
public void admin_create_get_request_without_authorization(String scenarioName) throws IOException {


   RequestSpec.logScenarioName(scenarioName);


   testData = ExcelReader.readExcelData("User", scenarioName);


   request = RestAssured.given()
           .spec(RequestSpec.getRequestSpecWithoutAuth());
}




   @When("Admin sends GET request to retrieve all users without authorization")
   public void admin_sends_get_request_without_authorization() {


       String endpoint = testData.get("EndPoint");


       if (endpoint == null) {
           endpoint = testData.get("Endpoint");
       }


       request = RestAssured.given()
               .spec(RequestSpec.getRequestSpecWithoutAuth());


       response = request.when().get(endpoint);
   }




@Then("Admin receives expected status code for user without authorization")
public void admin_receives_expected_status_code_for_user_without_authorization() {


   int expectedStatusCode =
           Integer.parseInt(testData.get("Response Code"));


   int actualStatusCode = response.getStatusCode();


   System.out.println(
           "Expected Status: " + expectedStatusCode
                   + " | Actual Status: " + actualStatusCode);


   System.out.println(
           "Response: " + response.getBody().asString());


   Assert.assertEquals(
           actualStatusCode,
           expectedStatusCode,
           "Status Code Mismatch!");
}



   @Then("Admin receives expected status code for invalid endpoint")
   public void admin_receives_expected_status_code_for_invalid_endpoint() {


       int expectedStatusCode =
               Integer.parseInt(testData.get("Response Code"));


       int actualStatusCode = response.getStatusCode();


       System.out.println(
               "Expected Status: " + expectedStatusCode
                       + " | Actual Status: " + actualStatusCode);


       System.out.println(
               "Response: " + response.getBody().asString());


       Assert.assertEquals(
               actualStatusCode,
               expectedStatusCode,
               "Status Code Mismatch!");
   }
@Then("Admin receives expected status code for invalid method")
public void admin_receives_expected_status_code_for_invalid_method() {


   int expectedStatusCode =
           Integer.parseInt(testData.get("Response Code"));


   int actualStatusCode = response.getStatusCode();


   System.out.println(
           "Expected Status: " + expectedStatusCode
                   + " | Actual Status: " + actualStatusCode);


   System.out.println(
           "Response: " + response.getBody().asString());


   Assert.assertEquals(
           actualStatusCode,
           expectedStatusCode,
           "Status Code Mismatch!");
}


}

