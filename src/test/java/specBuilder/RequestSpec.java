package specBuilder;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import configReader.ConfigReader;
import utils.SharedTestData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class RequestSpec {

    private static PrintStream logStream;

    public static RequestSpecification getRequestSpec() {
        initializeLogStream();
        if (SharedTestData.token == null || SharedTestData.token.trim().isEmpty()) {
            SharedTestData.generateAndSetToken();
        }
        return baseBuilder()
                .addHeader("Authorization", "Bearer " + SharedTestData.token)
                .build();
    }

    public static RequestSpecification getRequestSpecWithoutAuth() {
        initializeLogStream();
        return baseBuilder().build();
    }

    public static RequestSpecification getRequestSpecWithCustomToken(String customToken) {
        initializeLogStream();
        return baseBuilder()
                .addHeader("Authorization", "Bearer " + customToken)
                .build();
    }

    public static RequestSpecification getRequestSpecInvalidAuth() {
        initializeLogStream();
        return baseBuilder()
                .addHeader("Authorization", "Bearer ")
                .build();
    }

    private static RequestSpecBuilder baseBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.url"))
                .addHeader("Content-Type", "application/json")
                .addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream));
    }
    public static RequestSpecification getSpecForScenario(String scenarioName) {
        if (scenarioName.contains("No_Auth") || scenarioName.contains("NoAuth")) {
            return getRequestSpecWithoutAuth();
        } else if (scenarioName.contains("Invalid_Token") || scenarioName.contains("InvalidToken")) {
            return getRequestSpecWithCustomToken("invalid_token_12345");
        } else if (scenarioName.contains("Invalid_Auth") || scenarioName.contains("Missing_Bearer")) {
            return getRequestSpecInvalidAuth();
        } else {
            return getRequestSpec();
        }
    }

    private static void initializeLogStream() {
        if (logStream != null) return;

        synchronized (RequestSpec.class) {
            if (logStream == null) {
                try {
                    String filePath = ConfigReader.get("LogFilePath");
                    File logFile = new File(filePath);

                    if (logFile.getParentFile() != null && !logFile.getParentFile().exists()) {
                        logFile.getParentFile().mkdirs();
                    }

                    logStream = new PrintStream(new FileOutputStream(filePath, false));

                } catch (Exception e) {
                    throw new RuntimeException("Failed to initialize log file", e);
                }
            }
        }
    }

    public static void logScenarioName(String scenarioName) {
        initializeLogStream();
        logStream.println("\n==================================================");
        logStream.println("SCENARIO: " + scenarioName);
        logStream.println("==================================================\n");
        logStream.flush();
    }

    public static String getBaseUri() {
        return ConfigReader.get("base.url");
    }
}
