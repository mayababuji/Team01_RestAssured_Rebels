package utils;

import java.util.Random;

public class TestDataGenerator {

    private static final Random RANDOM = new Random();

    public static String replaceDynamicPlaceholders(String jsonBody) {
        if (jsonBody == null || jsonBody.isEmpty()){
            return jsonBody;
        }

        if (jsonBody.contains("<random_email>")) {
            jsonBody = jsonBody.replace("<random_email>", generateEmail());
        }

        if (jsonBody.contains("<random_phone>")) {
            jsonBody = jsonBody.replace("<random_phone>", generateIndianPhoneNumber());
        }

        return jsonBody.replaceAll("\"userEmail\"\\s*:\\s*\"[^\"]*\"\\s*,?", "");
    }

    public static String generateIndianPhoneNumber() {
        long random10Digit = 6000000000L + (long) (RANDOM.nextDouble() * 3000000000L);
        return "+91 " + random10Digit;
    }

    public static String generateEmail() {
        return "pam" + System.currentTimeMillis() + "@gmail.com";
    }
}