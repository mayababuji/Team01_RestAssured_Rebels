package stepDefinitions;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.restassured.specification.RequestSpecification;

import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ScenarioContext;

public class GetUserCountInvalidEndpointStepDef {

    private RequestSpecification request;
    private Map<String, String> testData;

    private final ScenarioContext scenarioContext;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public GetUserCountInvalidEndpointStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    // ============================================================
    // INVALID ENDPOINT REQUEST
    // ============================================================

    @Given("Admin creates GET request with invalid endpoint for user count scenario {string} from excel sheet")
    public void admin_create_get_request_with_invalid_input_for_from_excel_sheet(
            String scenarioName) throws IOException {

        System.out.println("==================================================");
        System.out.println("Scenario: " + scenarioName);

        // --------------------------------------------------------
        // Read test data from Excel
        // --------------------------------------------------------

        testData = ExcelReader.readExcelData(
                "User",
                scenarioName
        );

        Assert.assertNotNull(
                testData,
                "Test data not found in Excel for scenario: " + scenarioName
        );

        // --------------------------------------------------------
        // Get invalid endpoint
        // --------------------------------------------------------

        String endpoint = testData.get("EndPoint");

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = testData.get("Endpoint");
        }

        Assert.assertNotNull(
                endpoint,
                "Endpoint not found in Excel."
        );

        Assert.assertFalse(
                endpoint.trim().isEmpty(),
                "Endpoint is empty in Excel."
        );

        endpoint = endpoint.trim();

        System.out.println(
                "Invalid Endpoint from Excel: " + endpoint
        );

        // --------------------------------------------------------
        // Create authenticated GET request
        // --------------------------------------------------------

        request = given()
                .spec(RequestSpec.getRequestSpec());

        // --------------------------------------------------------
        // Store request and test data in ScenarioContext
        // --------------------------------------------------------

        scenarioContext.setContext(
                "GET_USER_COUNT_REQUEST",
                request
        );

        scenarioContext.setContext(
                "GET_USER_COUNT_TEST_DATA",
                testData
        );

        // --------------------------------------------------------
        // Log final request
        // --------------------------------------------------------

        System.out.println(
                "Final Request: " + endpoint
        );
    }


    
}