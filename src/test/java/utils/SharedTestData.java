package utils;

import java.util.ArrayList;
import java.util.List;

import configReader.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import specBuilder.RequestSpec;

public class SharedTestData {

    // Stores IDs/names created during test execution
    protected static final List<Integer> batchIds = new ArrayList<>();
    protected static final List<Integer> programIdList = new ArrayList<>();
    protected static final List<String> programNameList = new ArrayList<>();

    // Current program and batch data used by dependent scenarios
    protected static int batchId;
    protected static String batchName;

    protected static int programId;
    protected static String programName;

    // Shared authentication token
    public static String token;

    public SharedTestData() {
        // Default constructor required by step-definition inheritance.
    }

    /**
     * Logs in once and stores the returned token.
     * Later scenarios reuse the same token unless it is cleared.
     */
    public static void generateAndSetToken() {

        // Reuse token if it already exists.
        if (token != null && !token.isBlank()) {
            System.out.println("Existing authentication token is being reused.");
            return;
        }

        String baseUrl = ConfigReader.get("base.url");
        String adminEmail = ConfigReader.get("admin.email");
        String adminPassword = ConfigReader.get("admin.password");

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException(
                    "base.url is missing in the configuration file."
            );
        }

        if (adminEmail == null || adminEmail.isBlank()) {
            throw new IllegalStateException(
                    "admin.email is missing in the configuration file."
            );
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "admin.password is missing in the configuration file."
            );
        }


        String loginEndpoint = baseUrl.endsWith("/")
                ? baseUrl + "login"
                : baseUrl + "/login";

        String requestBody = String.format(
                "{\"userLoginEmailId\":\"%s\",\"password\":\"%s\"}",
                adminEmail,
                adminPassword
        );

        System.out.println("==========================================");
        System.out.println("Generating authentication token");
        System.out.println("Login endpoint: " + loginEndpoint);
        System.out.println("Admin email: " + adminEmail);
        System.out.println("==========================================");

        /*
         * Use getRequestSpecWithoutAuth() because the login API must not
         * receive Authorization: Bearer null.
         */
        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpecWithoutAuth())
                .body(requestBody)
                .when()
                .post(loginEndpoint);

        System.out.println("Login status code: " + response.getStatusCode());

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException(
                    "Login failed while generating the token. Status code: "
                            + response.getStatusCode()
                            + ", response: "
                            + response.asString()
            );
        }

        String capturedToken = response.jsonPath().getString("token");

        if (capturedToken == null || capturedToken.isBlank()) {
            throw new IllegalStateException(
                    "Login returned HTTP 200, but no token was found in the response: "
                            + response.asString()
            );
        }

        SharedTestData.token = capturedToken;

        System.out.println("Authentication token generated successfully.");
    }

    /**
     * Use only when you deliberately need to force a new login token.
     */
    public static void clearToken() {
        token = null;
    }

    /**
     * Clears program test data.
     * Do not call this before a Batch scenario unless you create a new program afterward.
     */
    public static void resetProgramData() {
        programId = 0;
        programName = null;
        programIdList.clear();
        programNameList.clear();
    }

    /**
     * Clears batch test data.
     */
    public static void resetBatchData() {
        batchId = 0;
        batchName = null;
        batchIds.clear();
    }

    /**
     * Prints the values Batch tests require.
     * To check if programId 0 or programName null.
     */
    public static void printProgramData() {
        System.out.println("==========================================");
        System.out.println("Current shared Program data");
        System.out.println("programId: " + programId);
        System.out.println("programName: " + programName);
        System.out.println("==========================================");
    }


    public static void printBatchData() {
        System.out.println("==========================================");
        System.out.println("Current shared Batch data");
        System.out.println("batchId: " + batchId);
        System.out.println("batchName: " + batchName);
        System.out.println("batchIds: " + batchIds);
        System.out.println("==========================================");
    }
}