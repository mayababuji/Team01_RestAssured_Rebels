package stepDefinitions;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;
import utils.SharedTestData;

public class GetUserCountInvalidStepDef {

    private RequestSpecification request;
    private Map<String, String> testData;

    private final ScenarioContext scenarioContext;

    public GetUserCountInvalidStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }
@Given("Admin creates GET request with invalid Role ID for {string} from Excel sheet")
public void admin_creates_get_request_with_invalid_role_id_for_from_excel_sheet(
        String scenarioName) throws IOException {

    System.out.println("==================================================");
    System.out.println("Scenario: " + scenarioName);

    RequestSpec.logScenarioName(scenarioName);

    // READ TEST DATA FROM EXCEL
    testData = ExcelReader.readExcelData("User", scenarioName);

    String endpoint = testData.get("EndPoint");

    if (endpoint == null || endpoint.trim().isEmpty()) {
        endpoint = testData.get("Endpoint");
    }

    Assert.assertNotNull(endpoint, "Endpoint not found in Excel.");

    Assert.assertFalse(
            endpoint.trim().isEmpty(),
            "Endpoint is empty in Excel."
    );

    System.out.println("Endpoint from Excel: " + endpoint);

    // GENERATE INVALID ROLE ID
    Random random = new Random();

    String roleId;

    do {
        roleId = String.format(
                "R%02d",
                random.nextInt(99) + 1
        );
    } while (
            "R01".equals(roleId)
                    || "R02".equals(roleId)
                    || "R03".equals(roleId)
    );

    System.out.println("Generated Invalid Role ID: " + roleId);

    // CREATE AUTHENTICATED GET REQUEST
    request = given()
            .spec(RequestSpec.getRequestSpec())
            .queryParam("id", roleId);

    scenarioContext.setContext(
            "GET_USER_COUNT_REQUEST",
            request
    );

    scenarioContext.setContext(
            "GET_USER_COUNT_TEST_DATA",
            testData
    );

    System.out.println(
            "Final Request: "
                    + endpoint
                    + "?id="
                    + roleId
    );
}
   

    @Then("Admin receives 404 Not Found status with Role ID not found message")
    public void admin_receives_404_not_found_status_with_role_id_not_found_message() {

        // Get response from ScenarioContext
        Response response =
                (Response) scenarioContext
                        .getContext("GET_USER_COUNT_RESPONSE");

        // Get test data from ScenarioContext
        @SuppressWarnings("unchecked")
        Map<String, String> responseTestData =
                (Map<String, String>) scenarioContext
                        .getContext("GET_USER_COUNT_TEST_DATA");

        Assert.assertNotNull(
                response,
                "Response is null. GET request was not executed."
        );

        Assert.assertNotNull(
                responseTestData,
                "Test data is null."
        );

        int expectedStatusCode = Integer.parseInt(
                responseTestData.get("Response Code")
        );

        int actualStatusCode = response.getStatusCode();

        System.out.println(
                "Expected Status: "
                        + expectedStatusCode
                        + " | Actual Status: "
                        + actualStatusCode
        );

        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "Invalid Role ID returned unexpected status code."
        );

        String responseBody = response.getBody().asString();

        System.out.println(
                "Invalid Role ID Response Body: "
                        + responseBody
        );

        Assert.assertTrue(
                responseBody.contains("RoleID"),
                "Response should contain RoleID."
        );

        Assert.assertTrue(
                responseBody.toLowerCase().contains("not found"),
                "Response should contain 'not found'."
        );

        System.out.println(
                "Negative invalid Role ID validation passed."
        );
    }
}