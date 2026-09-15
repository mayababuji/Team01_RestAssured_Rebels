package stepDefinitions;
import java.io.IOException;
import java.util.Map;
import org.testng.Assert;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;
import static io.restassured.RestAssured.given;

public class GetUsersByProgramBatchStepDef extends SharedTestData {

    private Response response;

    private final ScenarioContext scenarioContext;

    public GetUsersByProgramBatchStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @When("Admin sends GET request to retrieve all users linked to batch")
    public void admin_sends_get_request_to_retrieve_all_users_linked_to_batch()
            throws IOException {

        // Read TC-80 test data from Excel
        Map<String, String> userData =
                ExcelReader.readExcelData(
                        "User",
                        "Get_User_Pro_Batch_Valid"
                );

        // Read endpoint from Excel
        String endpoint = userData.get("EndPoint");

        if (endpoint == null || endpoint.isBlank()) {
            endpoint = userData.get("Endpoint");
        }

        Assert.assertNotNull(
                endpoint,
                "TC-80 endpoint is missing in Excel."
        );

        Assert.assertFalse(
                endpoint.isBlank(),
                "TC-80 endpoint is empty in Excel."
        );

        // Read Batch ID from Excel
        String batchId = userData.get("BatchId");

        Assert.assertNotNull(
                batchId,
                "TC-80 BatchId is missing in Excel."
        );

        Assert.assertFalse(
                batchId.isBlank(),
                "TC-80 BatchId is empty in Excel."
        );

        // Replace batchId placeholder with the Batch ID from Excel
        endpoint = endpoint.replace(
                "batchId",
                batchId
        );

        System.out.println("TC-80 Endpoint: " + endpoint);
        System.out.println("TC-80 Batch ID: " + batchId);

        // Create authenticated request specification
        RequestSpecification requestSpec =
                RequestSpec.getRequestSpec();

        // Send GET request
        response = given()
                .spec(requestSpec)
                .basePath(endpoint)
                .when()
                .get();

        System.out.println(
                "TC-80 Status Code: " + response.getStatusCode()
        );

        System.out.println(
                "TC-80 Response Body: " + response.asString()
        );

        // Store response in ScenarioContext
        scenarioContext.setContext(
                "GET_USERS_BY_PROGRAM_BATCH_RESPONSE",
                response
        );
    }

    @Then("Admin receives {int} OK status with response body for users linked to batch")
    public void admin_receives_ok_status_with_response_body_for_users_linked_to_batch(
            int expectedStatusCode) {

        Assert.assertNotNull(
                response,
                "TC-80 response is null."
        );

        int actualStatusCode = response.getStatusCode();

        System.out.println(
                "TC-80 Expected Status: " + expectedStatusCode
                        + " | Actual Status: " + actualStatusCode
        );

        System.out.println(
                "TC-80 Response Body: " + response.asString()
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "TC-80 status code mismatch."
        );

        // Validate response against JSON schema
        response.then()
                .assertThat()
                .body(
                        io.restassured.module.jsv.JsonSchemaValidator
                                .matchesJsonSchemaInClasspath(
                                    //schema result
                                        "schemas/User/GetUsersByProgramBatchResponseSchema.json"
                                )
                );
    }

    @Given("Admin create GET request with invalid batch ID for {string} from excel sheet")
    public void admin_create_get_request_with_invalid_batch_id(
            String scenarioName) throws IOException {

        RequestSpec.logScenarioName(scenarioName);

        Map<String, String> userData =
                ExcelReader.readExcelData("User", scenarioName);

        scenarioContext.setContext(
                "TC80_TEST_DATA",
                userData
        );

        String endpoint = userData.get("EndPoint");

        if (endpoint == null || endpoint.isBlank()) {
            endpoint = userData.get("Endpoint");
        }

        Assert.assertNotNull(endpoint, "TC-80 endpoint is missing in Excel.");
        Assert.assertFalse(endpoint.isBlank(), "TC-80 endpoint is empty in Excel.");

        String batchId = userData.get("BatchId");

        Assert.assertNotNull(batchId, "TC-80 invalid BatchId is missing in Excel.");
        Assert.assertFalse(batchId.isBlank(), "TC-80 invalid BatchId is empty in Excel.");

        endpoint = endpoint.replace("batchId", batchId);

        System.out.println("TC-80 Invalid Batch ID Endpoint: " + endpoint);
        System.out.println("TC-80 Invalid Batch ID: " + batchId);

        response = given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(endpoint)
                .when()
                .get();

        System.out.println("TC-80 Invalid Batch ID Status: "
                + response.getStatusCode());
        System.out.println("TC-80 Response: "
                + response.asString());
    }


    @Then("Admin receives 404 Not Found status with batch ID not found message")
    public void admin_receives_404_for_invalid_batch_id() {

        Assert.assertNotNull(
                response,
                "TC-80 response is null."
        );

        int actualStatusCode = response.getStatusCode();

        Assert.assertEquals(
                actualStatusCode,
                404,
                "TC-80 Invalid Batch ID should return 404."
        );

        System.out.println(
                "TC-80 Invalid Batch ID validation passed."
        );
    }


    
    @Given("Admin create GET request with invalid endpoint for {string} from excel sheet")
    public void admin_create_get_request_with_invalid_endpoint(
            String scenarioName) throws IOException {

        RequestSpec.logScenarioName(scenarioName);

        Map<String, String> userData =
                ExcelReader.readExcelData("User", scenarioName);

        scenarioContext.setContext(
                "TC80_TEST_DATA",
                userData
        );

        String endpoint = userData.get("EndPoint");

        if (endpoint == null || endpoint.isBlank()) {
            endpoint = userData.get("Endpoint");
        }

        Assert.assertNotNull(endpoint, "TC-80 invalid endpoint is missing in Excel.");
        Assert.assertFalse(endpoint.isBlank(), "TC-80 invalid endpoint is empty in Excel.");

        response = given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(endpoint)
                .when()
                .get();

        System.out.println("TC-80 Invalid Endpoint: " + endpoint);
        System.out.println("TC-80 Invalid Endpoint Status: "
                + response.getStatusCode());
        System.out.println("TC-80 Response: "
                + response.asString());
    }


    //tc80 negative invalid method
    @Given("Admin create invalid request for TC-80 {string} from excel sheet")
public void admin_create_invalid_request_for_tc80(
        String scenarioName) throws IOException {

    RequestSpec.logScenarioName(scenarioName);

    Map<String, String> userData =
            ExcelReader.readExcelData("User", scenarioName);

    scenarioContext.setContext(
            "TC80_TEST_DATA",
            userData
    );

    String endpoint = userData.get("EndPoint");

    if (endpoint == null || endpoint.isBlank()) {
        endpoint = userData.get("Endpoint");
    }

    Assert.assertNotNull(endpoint, "TC-80 endpoint is missing in Excel.");
    Assert.assertFalse(endpoint.isBlank(), "TC-80 endpoint is empty in Excel.");

    RequestSpec.getRequestSpec();

    scenarioContext.setContext(
            "TC80_INVALID_METHOD_ENDPOINT",
            endpoint
    );

    System.out.println("TC-80 Invalid Method Endpoint: " + endpoint);
}

    @When("Admin sends invalid method request to retrieve all users linked to batch")
    public void admin_sends_invalid_method_request_tc80() {

        String endpoint =
                (String) scenarioContext.getContext(
                        "TC80_INVALID_METHOD_ENDPOINT"
                );

        Assert.assertNotNull(
                endpoint,
                "TC-80 invalid method endpoint is null."
        );

        response = given()
                .spec(RequestSpec.getRequestSpec())
                .when()
                .post(endpoint);

        System.out.println("TC-80 Invalid Method: POST");
        System.out.println("TC-80 Endpoint: " + endpoint);
        System.out.println("TC-80 Status: " + response.getStatusCode());
        System.out.println("TC-80 Response: " + response.asString());
    }


   @Given("Admin create TC-80 GET request without authorization for {string} from excel sheet")
    public void admin_create_get_request_without_authorization_tc80(
            String scenarioName) throws IOException {

        RequestSpec.logScenarioName(scenarioName);

        Map<String, String> userData =
                ExcelReader.readExcelData("User", scenarioName);
                System.out.println("TC-80 NoAuth Excel Data: " + userData);

        scenarioContext.setContext(
                "TC80_TEST_DATA",
                userData
        );

        String endpoint = userData.get("EndPoint");

        if (endpoint == null || endpoint.isBlank()) {
            endpoint = userData.get("Endpoint");
        }

        Assert.assertNotNull(
                endpoint,
                "TC-80 endpoint is missing in Excel."
        );

        Assert.assertFalse(
                endpoint.isBlank(),
                "TC-80 endpoint is empty in Excel."
        );

        String batchId = userData.get("BatchId");

        Assert.assertNotNull(
                batchId,
                "TC-80 BatchId is missing in Excel."
        );

        endpoint = endpoint.replace("batchId", batchId);

        response = given()
                .spec(RequestSpec.getRequestSpecWithoutAuth())
                .basePath(endpoint)
                .when()
                .get();

        System.out.println("TC-80 Without Authorization Endpoint: "
                + endpoint);
        System.out.println("TC-80 Without Authorization Status: "
                + response.getStatusCode());
        System.out.println("TC-80 Response: "
                + response.asString());
    }

    @Then("Admin receives expected 404 status code for TC-80 invalid endpoint")
    public void admin_receives_expected_status_code_for_tc80_invalid_endpoint() {

        assertExpectedStatus("TC-80 Invalid Endpoint");
    }


    @Then("Admin receives expected 405 status code for TC-80 invalid method")
    public void admin_receives_expected_status_code_for_tc80_invalid_method() {

        assertExpectedStatus("TC-80 Invalid Method");
    }


   @Then("Admin receives expected 401 status code for TC-80 without authorization")
    public void admin_receives_expected_status_code_for_tc80_without_authorization() {

        assertExpectedStatus("TC-80 Without Authorization");
    }


    private void assertExpectedStatus(String scenarioDescription) {

        Assert.assertNotNull(
                response,
                scenarioDescription + " response is null."
        );

        @SuppressWarnings("unchecked")
        Map<String, String> userData =
                (Map<String, String>) scenarioContext.getContext(
                        "TC80_TEST_DATA"
                );

        Assert.assertNotNull(
                userData,
                scenarioDescription + " test data is null."
        );

        String expectedStatus =
                userData.get("Response Code");

        Assert.assertNotNull(
                expectedStatus,
                scenarioDescription + " Response Code is missing in Excel."
        );

        int expectedStatusCode =
                Integer.parseInt(expectedStatus.trim());

        int actualStatusCode =
                response.getStatusCode();

        System.out.println(
                scenarioDescription
                        + " | Expected: "
                        + expectedStatusCode
                        + " | Actual: "
                        + actualStatusCode
        );

        System.out.println(
                scenarioDescription
                        + " Response: "
                        + response.asString()
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                scenarioDescription + " status code mismatch."
        );
    }
    @When("Admin sends TC-80 invalid batch ID request")
public void admin_sends_tc80_invalid_batch_id_request() {
    // Request is already executed in the Given step.
    System.out.println("TC-80 invalid Batch ID request already executed.");
}
//endpoin
@When("Admin sends TC-80 invalid endpoint request")
public void admin_sends_tc80_invalid_endpoint_request() {
    // Request is already executed in the Given step.
    System.out.println("TC-80 invalid endpoint request already executed.");
}
//noauth
@When("Admin sends TC-80 GET request without authorization")
public void admin_sends_tc80_get_request_without_authorization() {
    System.out.println("TC-80 without-authorization request already executed.");
}

}
