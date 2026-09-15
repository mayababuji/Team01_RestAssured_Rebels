package utils;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Map;

public class ProgramResponseValidator {

	public static void validateStatus(Response response, Map<String, String> data) {

		// --------------------------------------------------------
		// Expected values from Excel
		// --------------------------------------------------------

		int expectedStatusCode = Integer.parseInt(data.get("ExpectedStatusCode").trim());

		String expectedStatus = getExcelValue(data, "ExpectedStatus");

		String expectedMessage = getExcelValue(data, "ExpectedMessage");

		// --------------------------------------------------------
		// Actual values
		// --------------------------------------------------------

		int actualStatusCode = response.getStatusCode();

		String actualResponseBody = response.asString();

		// --------------------------------------------------------
		// Print validation information
		// --------------------------------------------------------

		System.out.println();
		System.out.println("==========================================");
		System.out.println("PROGRAM ERROR RESPONSE VALIDATION");
		System.out.println("==========================================");

		System.out.println("Expected Status Code : " + expectedStatusCode);

		System.out.println("Actual Status Code   : " + actualStatusCode);

		System.out.println("Expected Status      : " + expectedStatus);

		System.out.println("Actual Status        : " + getStatusMessage(actualStatusCode));

		System.out.println("Expected Message     : " + expectedMessage);

		System.out.println("Actual Response Body : " + actualResponseBody);

		// --------------------------------------------------------
		// 1. Validate Status Code
		// --------------------------------------------------------

		Assert.assertEquals(actualStatusCode, expectedStatusCode, "Program API HTTP Status Code mismatch");

		// --------------------------------------------------------
		// 2. Validate HTTP Status Message
		// --------------------------------------------------------

		if (!expectedStatus.isEmpty()) {

			String actualStatus = getStatusMessage(actualStatusCode);

			Assert.assertEquals(actualStatus, expectedStatus, "Program API HTTP Status Message mismatch");
		}

		// --------------------------------------------------------
		// 3. Validate Response Message
		// --------------------------------------------------------

		if (!expectedMessage.isEmpty()) {

			String actualMessage = extractErrorMessage(response);

			Assert.assertNotNull(actualMessage, "Expected an error message but no error message "
					+ "could be extracted from response. " + "Response body: " + actualResponseBody);

			Assert.assertTrue(actualMessage.contains(expectedMessage), "Expected message [" + expectedMessage
					+ "] was not found in actual message [" + actualMessage + "]");
		}

		System.out.println("==========================================");
		System.out.println("PROGRAM ERROR VALIDATION PASSED");
		System.out.println("==========================================");
	}

	// ============================================================
	// Extract Error Message
	// ============================================================

	
	public static String extractErrorMessage(Response response) {

		if (response == null) {
			return null;
		}

		String body = response.asString();

		// --------------------------------------------------------
		// Empty response
		// --------------------------------------------------------

		if (body == null || body.trim().isEmpty()) {

			return null;
		}

		String trimmedBody = body.trim();

		// --------------------------------------------------------
		// Try JSON response
		// --------------------------------------------------------

		try {

			// --------------------------------------------
			// 1. message
			// --------------------------------------------

			String message = response.jsonPath().getString("message");

			if (message != null && !message.trim().isEmpty()) {

				return message.trim();
			}

			// --------------------------------------------
			// 2. error
			// --------------------------------------------

			String error = response.jsonPath().getString("error");

			if (error != null && !error.trim().isEmpty()) {

				return error.trim();
			}

			// --------------------------------------------
			// 3. programName
			// --------------------------------------------

			String programNameError = response.jsonPath().getString("programName");

			if (programNameError != null && !programNameError.trim().isEmpty()) {

				return programNameError.trim();
			}

			// --------------------------------------------
			// 4. programDescription
			// --------------------------------------------

			String programDescriptionError = response.jsonPath().getString("programDescription");

			if (programDescriptionError != null && !programDescriptionError.trim().isEmpty()) {

				return programDescriptionError.trim();
			}

			// --------------------------------------------
			// 5. programStatus
			// --------------------------------------------

			String programStatusError = response.jsonPath().getString("programStatus");

			if (programStatusError != null && !programStatusError.trim().isEmpty()) {

				return programStatusError.trim();
			}

		} catch (Exception e) {

			System.out.println("Response is not a standard JSON error response.");
		}

		// --------------------------------------------------------
		// Plain text response
		// --------------------------------------------------------

		return trimmedBody;
	}

	// ============================================================
	// Get Excel Value 
	// ============================================================

	private static String getExcelValue(Map<String, String> data, String columnName) {

		String value = data.get(columnName);

		if (value == null) {
			return "";
		}

		return value.trim();
	}

	// ============================================================
	// HTTP Status Messages
	// ============================================================

	private static String getStatusMessage(int statusCode) {

		switch (statusCode) {

		case 200:
			return "OK";

		case 201:
			return "Created";

		case 400:
			return "Bad Request";

		case 401:
			return "Unauthorized";

		case 403:
			return "Forbidden";

		case 404:
			return "Not Found";

		case 405:
			return "Method Not Allowed";

		case 406:
			return "Not Acceptable";

		case 415:
			return "Unsupported Media Type";

		case 422:
			return "Unprocessable Entity";

		case 500:
			return "Internal Server Error";

		default:
			return "Unknown";
		}
	}
}
