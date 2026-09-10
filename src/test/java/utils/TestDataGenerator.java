package utils;

import java.util.Random;

public class TestDataGenerator {

    public static String replaceDynamicPlaceholders(String jsonBody) {
        if (jsonBody == null || jsonBody.isEmpty()) {
            return jsonBody;
        }

        // Clean, standard email format (lowercase, no special characters except @ and .)
        String randomEmail = "pam" + System.currentTimeMillis() + "@gmail.com";
        
        // Valid 10-digit phone number with +91 country code
        Random random = new Random();
        long random10Digit = 6000000000L + (long)(random.nextDouble() * 3000000000L);
        String randomPhone = "+91 " + random10Digit;

        return jsonBody
                .replace("<random_email>", randomEmail)
                .replace("<random_phone>", randomPhone);
    }
}