package utils;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Map;

public class ProgramResponseValidator {

    public static void validateStatus(Response response, Map<String, String> data) {
        int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode"));
        String expectedMsg = data.get("ExpectedMessage");

        switch (expectedStatus) {
            case 400:
                validate400(response, expectedMsg);
                break;
            case 405:
                validate405(response, expectedMsg);
                break;
            case 404:
            default:
                // Add custom logic if needed
                break;
        }
    }

    private static void validate400(Response response, String expectedMsg) {
        String message = response.jsonPath().getString("message");

        if (message != null) {
            Assert.assertEquals(message, expectedMsg, "400 message mismatch");
        } else {
            Map<String, String> bodyMap = response.jsonPath().getMap("");
            String firstValue = bodyMap.values().iterator().next();
            Assert.assertEquals(firstValue, expectedMsg, "400 error body mismatch");
        }
    }

    private static void validate405(Response response, String expectedMsg) {
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, expectedMsg, "405 message mismatch");
    }
}