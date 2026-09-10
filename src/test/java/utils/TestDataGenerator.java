package utils;

import java.util.Random;

public class TestDataGenerator {

	private static final Random RANDOM = new Random();

	public static String replaceDynamicPlaceholders(String jsonBody) {
		if (jsonBody == null || jsonBody.isEmpty()) {
			return jsonBody;
		}

		String dynamicPhone = generateIndianPhoneNumber();
		String dynamicEmail = generateEmail();

		String processed = jsonBody.replace("<random_email>", dynamicEmail).replace("<random_phone>", dynamicPhone);

		processed = processed.replaceAll("\"userPhoneNumber\"\\s*:\\s*\"[^\"]*\"",
				"\"userPhoneNumber\": \"" + dynamicPhone + "\"");

		processed = processed.replaceAll("\"userLoginEmail\"\\s*:\\s*\"[^\"]*\"",
				"\"userLoginEmail\": \"" + dynamicEmail + "\"");

		return processed.replaceAll("\"userEmail\"\\s*:\\s*\"[^\"]*\"\\s*,?", "");
	}

	public static String generateIndianPhoneNumber() {
		long random10Digit = 6000000000L + (long) (RANDOM.nextDouble() * 3000000000L);
		return "+91 " + random10Digit;
	}

	public static String generateEmail() {
		return "pam" + System.currentTimeMillis() + "@gmail.com";
	}
}