package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;
import configReader.ConfigReader;

import static io.restassured.RestAssured.given;

public class TokenGenerator {

    /**
     * Generates an admin token by calling the login API.
     * Adjust endpoint, body, and JSON path to match your project.
     */
    public static String generateToken() {

        // Use your real credentials file or env config
        String email    = ConfigReader.get("admin.email");
        String password = ConfigReader.get("admin.password");

        // Adjust endpoint to your real login path
        String loginPath = ConfigReader.get("login.path");

        if (email == null || email.isBlank()) {
            throw new IllegalStateException(
                    "admin.email not configured in config."
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "admin.password not configured in config."
            );
        }

        if (loginPath == null || loginPath.isBlank()) {
            throw new IllegalStateException(
                    "login.path not configured in config."
            );
        }

        Response response = given()
                .baseUri(ConfigReader.get("base.url"))
                .contentType("application/json")
                .accept("*/*")
                .body(Map.of(
                        "userLoginEmailId", email,
                        "password", password
                ))
                .when()
                .post(loginPath);

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException(
                    "Token generation failed. Status: "
                            + response.getStatusCode()
                            + ", Body: "
                            + response.getBody().asString()
            );
        }

        // Adjust this path to where your token lives in the JSON
        String token = response.jsonPath().getString("token");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Token field missing or blank in login response."
            );
        }

        return token;
    }
}