package utils;

import java.util.ArrayList;
import java.util.List;

import configReader.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import specBuilder.RequestSpec;

public class SharedTestData {

    protected static final List<Integer> batchIds = new ArrayList<>();
    protected static final List<Integer> programIdList = new ArrayList<>();
    protected static final List<String> programNameList = new ArrayList<>();

    protected static int batchId;
    protected static String batchName;
    protected static int programId;
    protected static String programName;

    // Auth token shared across tests
    public static String token;

    public SharedTestData() {
    }

    public static void generateAndSetToken() {

        // Don't generate token again if already available
        if (token != null && !token.trim().isEmpty()) {
            return;
        }

        // Read configuration from env.properties / credentials file
        String baseUrl = ConfigReader.get("base.url");
        String adminEmail = ConfigReader.get("admin.email");
        String adminPassword = ConfigReader.get("admin.password");

        System.out.println("===== LOGIN CONFIG =====");
        System.out.println("base.url = " + baseUrl);
        System.out.println("admin.email = " + adminEmail);
        System.out.println("admin.password = "
                + (adminPassword != null ? "SET" : "NULL"));
        System.out.println("========================");

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalStateException(
                    "base.url is missing from configuration");
        }

        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            throw new IllegalStateException(
                    "admin.email is missing from configuration");
        }

        if (adminPassword == null || adminPassword.trim().isEmpty()) {
            throw new IllegalStateException(
                    "admin.password is missing from configuration");
        }

        //String loginEndpoint = baseUrl + "login";
        String loginEndpoint = baseUrl.endsWith("/")
        ? baseUrl + "login"
        : baseUrl + "/login";

        String requestBody = String.format(
                "{\"userLoginEmailId\":\"%s\",\"password\":\"%s\"}",
                adminEmail,
                adminPassword
        );

        System.out.println("===== LOGIN REQUEST =====");
        System.out.println("POST " + loginEndpoint);
        System.out.println("Request Body = " + requestBody);
        System.out.println("=========================");

        Response response = RestAssured
                .given()
                .spec(RequestSpec.getRequestSpecWithoutAuth())
                .body(requestBody)
                .when()
                .post(loginEndpoint);

        System.out.println("===== LOGIN RESPONSE =====");
        System.out.println("Status Code = " + response.getStatusCode());
        System.out.println("Response Body = " + response.getBody().asString());
        System.out.println("==========================");

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException(
                    "Login failed. Status: "
                            + response.getStatusCode()
                            + ", Response: "
                            + response.getBody().asString()
            );
        }

        String capturedToken = response.jsonPath().getString("token");

        if (capturedToken == null || capturedToken.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Login succeeded but token was not found in response. Response: "
                            + response.getBody().asString()
            );
        }

        token = capturedToken;

        System.out.println("===== TOKEN GENERATED SUCCESSFULLY =====");
    }
}