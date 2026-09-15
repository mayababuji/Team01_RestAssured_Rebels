package utils;

import org.apache.commons.lang3.RandomStringUtils;

public class TestDataUtil {

	public static String randomEmail() {
		String random = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
		return "team01" + random + "@gmail.com";

	}

	public static String randomPhone() {
		String digits = RandomStringUtils.randomNumeric(10);

		return "+91 " + digits;

	}

	public static String randomNumericSuffix(int length) {
		return RandomStringUtils.randomNumeric(length);
	}

	// generating random number
	public static String generateUniqueProgramName() {

		String timestamp = String.valueOf(System.currentTimeMillis());

		StringBuilder programName = new StringBuilder("Program");

		for (char digit : timestamp.toCharArray()) {
			programName.append((char) ('A' + (digit - '0')));
		}

		return programName.toString();
	}

}