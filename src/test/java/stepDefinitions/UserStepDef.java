package stepDefinitions;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.testng.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import specBuilder.RequestSpec;
import specBuilder.ResponseSpec;
import utils.ExcelReader;
import utils.TestDataUtil;
import utils.SharedTestData;

public class UserStepDef extends SharedTestData {
    private Response response;
    private Map<String, String> testData;
    private RequestSpecification requestSpec;
    private String generatedEmail;
    private String generatedPhone;

    @Given("Admin has a valid authorization token in user controller")
    public void admin_has_a_valid_authoization_token_in_user_controller() {
        requestSpec = RequestSpec.getRequestSpec();
        RequestSpec.logScenarioName("USER CONTROLLER MODULE LOGS");
    }
    @When("Admin sends HTTPS POST Request for {string} from Excel to create user")
    public void admin_sends_https_post_request_and_request_body_with_mandatory_fields_and_admin_role(String scenarioName)
            throws Exception {


//        testData = ExcelReader.readExcelData("User", scenarioName);
//        String requestBody = testData.get("Body");
//
//        System.out.println(requestBody);
//        requestSpec = requestSpec.body(requestBody);
//        response = RequestSpec.sendRequest(requestSpec, "POST", testData.get("Endpoint"));
        testData = ExcelReader.readExcelData("User", scenarioName);
        String requestBody = testData.get("Body");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(requestBody);

        // Generate random values
        generatedEmail = TestDataUtil.randomEmail();
        generatedPhone = TestDataUtil.randomPhone();

        // Update fields in JSON
        if (root.has("userPhoneNumber")) {
            ((ObjectNode) root).put("userPhoneNumber", generatedPhone);
        }
        if (root.has("userLogin") && root.get("userLogin").has("userLoginEmail")) {
            ((ObjectNode) root.get("userLogin")).put("userLoginEmail", generatedEmail);
        }

        String updatedBody = mapper.writeValueAsString(root);

        System.out.println("Generated email: " + generatedEmail);
        System.out.println("Generated phone: " + generatedPhone);
        System.out.println("Request body: " + updatedBody);

        requestSpec = requestSpec.body(updatedBody);
        response = RequestSpec.sendRequest(requestSpec, "POST", testData.get("Endpoint"));
    }
    @Then("Admin receives {int} Created Status with response body")
    public void admin_receives_created_status_with_response_body(Integer num) {
        response.then().log().ifValidationFails().spec(ResponseSpec.status(num));
    }
}
