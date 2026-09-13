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

    /**
     * Extracts the main error message from the response.
     * Handles:
     * - Empty body (e.g. 401) → returns null
     * - JSON body → extracts from common fields
     * - Non-JSON body (e.g. "Invalid endpoint") → returns the whole body
     */
    public static String extractErrorMessage(Response response) {
        String body = response.asString();
        if (body == null || body.isBlank()) {
            return null;
        }

        String trimmed = body.trim();

        // Only try JSON parsing if it looks like JSON
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            try {
                String[] candidates = { "message", "errorMessage", "error", "batchNoOfClasses","batchDescription","batchName" };
                for (String field : candidates) {
                    String value = response.jsonPath().getString(field);
                    if (value != null && !value.isBlank()) {
                        return value;
                    }
                }

                // Fallback: first value in the JSON object
                Map<String, String> bodyMap = response.jsonPath().getMap("");
                if (bodyMap != null && !bodyMap.isEmpty()) {
                    String firstValue = bodyMap.values().iterator().next();
                    if (firstValue != null && !firstValue.isBlank()) {
                        return firstValue;
                    }
                }
            } catch (Exception e) {
                // JSON parsing failed; fall through to treat body as plain text
            }
        }

        // Non-JSON or failed parse: return raw body as message
        return trimmed;
    }

    private static void validate400(Response response, String expectedMsg) {
        String message = extractErrorMessage(response);

        if (message != null) {
            Assert.assertTrue(message.contains(expectedMsg), "400 message mismatch");
        } else {
            Map<String, String> bodyMap = response.jsonPath().getMap("");
            String firstValue = bodyMap.values().iterator().next();
            Assert.assertTrue(firstValue.contains(expectedMsg), "400 error body mismatch");
        }
    }

    private static void validate405(Response response, String expectedMsg) {
        String message = extractErrorMessage(response);
        Assert.assertTrue(message.contains(expectedMsg), "405 message mismatch");
    }
}