package utils;

import java.util.concurrent.atomic.AtomicInteger;

public final class TestDataUtil {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private TestDataUtil() {
    }

    public static String randomEmail() {
        return "team01"
                + getUniqueToken()
                + "@gmail.com";
    }

    public static String randomPhone() {
        String timestamp = String.valueOf(
                System.currentTimeMillis()
        );

        String counter = String.format(
                "%03d",
                COUNTER.getAndIncrement() % 1000
        );

        String digits = (timestamp + counter);

        // Keep only the last 10 digits for the phone number.
        digits = digits.substring(
                Math.max(0, digits.length() - 10)
        );

        return "+91 " + digits;
    }

    public static String randomNumericSuffix(int length) {

        if (length <= 0) {
            throw new IllegalArgumentException(
                    "Suffix length must be greater than zero."
            );
        }

        String uniqueDigits = String.valueOf(
                System.currentTimeMillis()
        ) + String.format(
                "%03d",
                COUNTER.getAndIncrement() % 1000
        );

        if (length >= uniqueDigits.length()) {
            return uniqueDigits;
        }

        return uniqueDigits.substring(
                uniqueDigits.length() - length
        );
    }

    public static String generateUniqueProgramName() {

        String timestamp = String.valueOf(
                System.currentTimeMillis()
        );

        StringBuilder programName = new StringBuilder(
                "Prog"
        );

        for (char digit : timestamp.toCharArray()) {
            programName.append(
                    (char) ('A' + (digit - '0'))
            );
        }

        programName.append(
                (char) ('A' + (COUNTER.getAndIncrement() % 26))
        );

        return programName.toString();
    }

    private static String getUniqueToken() {

        String timestamp = String.valueOf(
                System.currentTimeMillis()
        );

        StringBuilder token = new StringBuilder();

        for (char digit : timestamp.toCharArray()) {
            token.append(
                    (char) ('a' + (digit - '0'))
            );
        }

        token.append(
                (char) ('a' + (COUNTER.getAndIncrement() % 26))
        );

        return token.toString();
    }
}