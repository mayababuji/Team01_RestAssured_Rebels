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
}