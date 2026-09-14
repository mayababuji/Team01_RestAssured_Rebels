package stepDefinitions;

import httpRequest.ProgramRequestParser;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import pojo.CreateProgramRequest;
import pojo.CreateProgramResponse;
import utils.ExcelReader;
import utils.ProgramResponseValidator;
import utils.ScenarioContext;
import utils.SharedTestData;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertTrue;

public class ProgramStepDef extends SharedTestData {

    private Map<String, String> data;
    private Response response;
    private static CreateProgramRequest programInput;

    private final ScenarioContext scenarioContext;

    public ProgramStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }


    @When("Admin sends POST request to create program with different payload for {string} from dataSheet")
    public void admin_sends_post_request_to_create_program_with_different_payload_for_from_data_sheet(
            String scenarioNameFeature) throws IOException {

        System.out.println("Scenario FEATURE IS Name: " + scenarioNameFeature);

        data = ExcelReader.readExcelData("Program", scenarioNameFeature);

        if (data == null) {
            throw new RuntimeException("Test data not found for: " + scenarioNameFeature);
        }

        if (!scenarioNameFeature.equalsIgnoreCase(data.get("ScenarioName"))) {
            return;
        }

        // Parse request body
        programInput = ProgramRequestParser.createProgramParseData(data.get("Body"));

        // Handle missing programName scenario
        if (scenarioNameFeature.equalsIgnoreCase("CreateProgram_with_Missing_ProgramName")) {
            programInput.setProgramName(null);
        } else {
            // Generate unique program name
            String uniqueProgramName = programInput.getProgramName() + RandomStringUtils.randomAlphabetic(3);
            programInput.setProgramName(uniqueProgramName);
            SharedTestData.programName = uniqueProgramName;
        }

        // Get shared request spec
        RequestSpecification requestSpec = scenarioContext.getRequestSpec();
        if (requestSpec == null) {
            throw new IllegalStateException("requestSpec is null – did the Given step run?");
        }

        // Build request
        requestSpec = given().spec(requestSpec).body(programInput);

        // Unified dynamic endpoint logic
        String httpMethod = data.get("Method");
        String endPoint = data.get("Endpoint");

        if (endPoint.contains("{programId}")) {
            endPoint = endPoint.replace("{programId}", String.valueOf(SharedTestData.programId));
        }

        // Send request
        response = requestSpec.log().all()
                .when().request(httpMethod, endPoint)
                .then().log().all()
                .extract().response();
    }

    @Then("Admin verifies the response payload with expected output from the data sheet")
    public void admin_verifies_the_response_payload_with_expected_output_from_the_data_sheet() {

        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));

        response.then()
                .log()
                .all()
                .statusCode(expectedStatus);

        if (expectedStatus != 201) {
            ProgramResponseValidator.validateStatus(response, data);
            return;
        }

        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(
                        "schemas/Program/CreateProgramSchema.json"
                ));

        CreateProgramResponse actualResponse =
                response.as(CreateProgramResponse.class);

        if (actualResponse.getProgramId() <= 0) {
            throw new IllegalStateException(
                    "Create Program API returned an invalid programId: "
                            + actualResponse.getProgramId()
            );
        }

        if (actualResponse.getProgramName() == null
                || actualResponse.getProgramName().isBlank()) {
            throw new IllegalStateException(
                    "Create Program API returned null or blank programName."
            );
        }

        SharedTestData.programId = actualResponse.getProgramId();
        SharedTestData.programName = actualResponse.getProgramName();

        if (!SharedTestData.programIdList.contains(SharedTestData.programId)) {
            SharedTestData.programIdList.add(SharedTestData.programId);
        }

        if (!SharedTestData.programNameList.contains(SharedTestData.programName)) {
            SharedTestData.programNameList.add(SharedTestData.programName);
        }

        System.out.println("==========================================");
        System.out.println("PROGRAM DATA SAVED IN SHAREDMETHHHODDATA");
        System.out.println("programId: " + SharedTestData.programId);
        System.out.println("programName: " + SharedTestData.programName);
        System.out.println("==========================================");

        Assert.assertEquals(
                actualResponse.getProgramDescription(),
                programInput.getProgramDescription(),
                "ProgramDescription is not matching"
        );

        Assert.assertEquals(
                actualResponse.getProgramName(),
                programInput.getProgramName(),
                "ProgramName is not matching"
        );

        Assert.assertTrue(
                actualResponse.getProgramId() > 0,
                "ProgramId should be a positive value"
        );
    }

}