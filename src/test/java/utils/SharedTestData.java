package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import specBuilder.RequestSpec;

public class SharedTestData {

    // Lists to store multiple IDs and names across scenarios
    protected static final List<Integer> batchIds = new ArrayList<>();
    protected static final List<Integer> programIdList = new ArrayList<>();
    protected static final List<String> programNameList = new ArrayList<>();

    // Single-value shared fields
    protected static int batchId;
    protected static String batchName;
    protected static int programId;
    protected static String programName;

    // Auth token shared across tests
    public static String token;

    public SharedTestData() {

    }

    public static void generateAndSetToken() {
        // 1. Skip if token is already set from a previous scenario
        if (token != null && !token.trim().isEmpty()) {
            return;
        }

        try {
            // 2. Read valid login credentials from Excel
            Map<String, String> loginData = ExcelReader.readExcelData("Login", "Valid credential");
            String requestBody = loginData.get("Body");
            String endpoint = loginData.get("Endpoint");

            // 3. Send POST request using unauthenticated spec
            Response response = RestAssured.given()
                    .spec(RequestSpec.getRequestSpecWithoutAuth())
                    .body(requestBody)
                    .when()
                    .post(endpoint);

            // 4. Extract token
            String capturedToken = response.jsonPath().getString("token");

            if (capturedToken == null || capturedToken.trim().isEmpty()) {
                throw new IllegalStateException("Failed to capture token! API Response: " + response.getBody().asString());
            }

            token = capturedToken;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read login data from Excel for token generation", e);
        }
    }
}
