package utils;

import static io.restassured.module.jsv.JsonSchemaValidator
        .matchesJsonSchemaInClasspath;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Map;

public final class ResponseValidator {

    private static final String[] ERROR_KEYS = {
            "message",
            "error",
            "detail",
            "title",
            "status",

            "programId",
            "programName",
            "programDescription",
            "programStatus",

            "batchId",
            "batchName",
            "batchDescription",
            "batchStatus",
            "batchNoOfClasses"
    };

    private ResponseValidator() {
    }

    // ============================================================
    // Generic Status-Code Validation
    // ============================================================

    public static void validateStatus(
            Response response,
            String expectedStatusCode) {

        Assert.assertNotNull(
                response,
                "Response is null. Ensure the request was sent before validation."
        );

        response.then()
                .log()
                .all()
                .statusCode(Integer.parseInt(expectedStatusCode.trim()));
    }

    // ============================================================
    // Batch PUT Response Validation
    // ============================================================

    public static void validateBatchPutResponse(
            Response response,
            String expectedStatusCode) {

        Assert.assertNotNull(
                response,
                "Response is null. Ensure the PUT request was sent before validation."
        );

        response.then()
                .log()
                .all()
                .statusCode(Integer.parseInt(expectedStatusCode.trim()))
                .body(matchesJsonSchemaInClasspath(
                        "schemas/batch/PutBatchByIdResponseSchema.json"
                ));
    }

    // ============================================================
    // Generic Error-Response Validation
    // ============================================================

    public static void validateErrorResponse(
            Response response,
            Map<String, String> data) {

        Assert.assertNotNull(
                response,
                "Response is null. Ensure the request was sent before validation."
        );

        String expectedStatusCodeText = getExcelValue(
                data,
                "ExpectedStatusCode"
        );

        if (expectedStatusCodeText.isBlank()) {
            throw new IllegalStateException(
                    "ExpectedStatusCode is missing or blank in Excel."
            );
        }

        int expectedStatusCode = Integer.parseInt(
                expectedStatusCodeText
        );

        String expectedStatus = getExcelValue(
                data,
                "ExpectedStatus"
        );

        String expectedMessage = getExcelValue(
                data,
                "ExpectedMessage"
        );

        int actualStatusCode = response.getStatusCode();
        String actualStatus = getHttpStatusMessage(response);
        String actualResponseBody = response.asString();

        printErrorValidationDetails(
                expectedStatusCode,
                actualStatusCode,
                expectedStatus,
                actualStatus,
                expectedMessage,
                actualResponseBody
        );

        // 1. Validate HTTP status code
        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                "API HTTP status-code mismatch"
        );

        // 2. Validate HTTP status text only when Excel provides it
        if (!expectedStatus.isBlank()) {
            Assert.assertEquals(
                    actualStatus,
                    expectedStatus,
                    "API HTTP status-message mismatch"
            );
        }

        // 3. Validate API error message only when Excel provides it
        if (!expectedMessage.isBlank()) {
            String actualMessage = extractErrorMessage(response);

            Assert.assertNotNull(
                    actualMessage,
                    "Expected error message ["
                            + expectedMessage
                            + "] but no message could be extracted. "
                            + "Response body: "
                            + actualResponseBody
            );

            Assert.assertTrue(
                    actualMessage.contains(expectedMessage),
                    "Expected message ["
                            + expectedMessage
                            + "] was not found in actual message ["
                            + actualMessage
                            + "]"
            );
        }

        System.out.println("==========================================");
        System.out.println("API ERROR RESPONSE VALIDATION PASSED");
        System.out.println("==========================================");
    }

    // ============================================================
    // Error-Message Extraction
    // ============================================================

    public static String extractErrorMessage(Response response) {

        if (response == null) {
            return null;
        }

        String responseBody = response.asString();

        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        String trimmedResponseBody = responseBody.trim();

        try {
            for (String errorKey : ERROR_KEYS) {
                String errorMessage = response.jsonPath()
                        .getString(errorKey);

                if (errorMessage != null && !errorMessage.isBlank()) {
                    return errorMessage.trim();
                }
            }
        } catch (Exception ignored) {
            System.out.println(
                    "Response is not JSON. Plain-text response will be used for validation."
            );
        }

        return trimmedResponseBody;
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    private static String getExcelValue(
            Map<String, String> data,
            String columnName) {

        if (data == null) {
            return "";
        }

        String value = data.get(columnName);

        return value == null ? "" : value.trim();
    }

    private static String getHttpStatusMessage(Response response) {

        String statusLine = response.getStatusLine();

        if (statusLine == null || statusLine.isBlank()) {
            return "";
        }

        /*
         * Example status line:
         * HTTP/1.1 400 Bad Request
         *
         * Result:
         * Bad Request
         */
        return statusLine.replaceFirst(
                "^HTTP/\\S+\\s+\\d{3}\\s*",
                ""
        ).trim();
    }

    private static void printErrorValidationDetails(
            int expectedStatusCode,
            int actualStatusCode,
            String expectedStatus,
            String actualStatus,
            String expectedMessage,
            String actualResponseBody) {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("API ERROR RESPONSE VALIDATION");
        System.out.println("==========================================");
        System.out.println("Expected Status Code : " + expectedStatusCode);
        System.out.println("Actual Status Code   : " + actualStatusCode);
        System.out.println("Expected Status      : " + expectedStatus);
        System.out.println("Actual Status        : " + actualStatus);
        System.out.println("Expected Message     : " + expectedMessage);
        System.out.println("Actual Response Body : " + actualResponseBody);
        System.out.println("==========================================");
    }

    public static void validateStatusAndSchema(
            Response response,
            String expectedStatusCode,
            String schemaPath) {

        Assert.assertNotNull(
                response,
                "Response is null. Ensure the request was sent before validation."
        );

        if (expectedStatusCode == null || expectedStatusCode.isBlank()) {
            throw new IllegalStateException(
                    "ExpectedStatusCode is missing or blank in Excel."
            );
        }

        if (schemaPath == null || schemaPath.isBlank()) {
            throw new IllegalArgumentException(
                    "Schema path is missing or blank."
            );
        }

        response.then()
                .log()
                .all()
                .statusCode(Integer.parseInt(expectedStatusCode.trim()))
                .body(matchesJsonSchemaInClasspath(schemaPath));
    }
    public static void validateResponseMessage(
            Response response,
            String expectedMessage) {

        Assert.assertNotNull(
                response,
                "Response is null. Ensure the request was sent before validation."
        );

        if (expectedMessage == null || expectedMessage.isBlank()) {
            System.out.println(
                    "ExpectedMessage is blank; message validation is skipped."
            );
            return;
        }

        String actualBody = response.getBody().asString();

        Assert.assertNotNull(
                actualBody,
                "Expected message: '" + expectedMessage
                        + "', but API returned a null response body."
        );

        Assert.assertFalse(
                actualBody.isBlank(),
                "Expected message: '" + expectedMessage
                        + "', but API returned an empty response body."
        );

        System.out.println("Expected message: " + expectedMessage);
        System.out.println("Actual response: " + actualBody);

        Assert.assertTrue(
                actualBody.contains(expectedMessage.trim()),
                "Expected message does not match. Expected text: '"
                        + expectedMessage
                        + "', actual response: '"
                        + actualBody
                        + "'"
        );
    }
}