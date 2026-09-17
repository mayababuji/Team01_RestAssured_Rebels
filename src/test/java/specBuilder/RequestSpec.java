package specBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import configReader.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import utils.SharedTestData;

public class RequestSpec {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static PrintStream logStream;

    public static RequestSpecification getRequestSpec() {
        initializeLogStream();

        if (SharedTestData.token == null
                || SharedTestData.token.isBlank()) {
            throw new IllegalStateException(
                    "Token is null or blank after token generation."
            );
        }

        return baseBuilder()
                .addHeader(
                        "Authorization",
                        "Bearer " + SharedTestData.token
                )
                .build();
    }

    public static RequestSpecification getRequestSpecWithoutAuth() {
        initializeLogStream();

        return baseBuilder()
                .build();
    }

    public static RequestSpecification getRequestSpecWithCustomToken(
            String customToken) {

        initializeLogStream();

        return baseBuilder()
                .addHeader(
                        "Authorization",
                        "Bearer " + customToken
                )
                .build();
    }

    public static RequestSpecification getRequestSpecInvalidAuth() {
        initializeLogStream();

        return baseBuilder()
                .addHeader("Authorization", "Bearer ")
                .build();
    }

    public static RequestSpecification getSpecForScenario(String scenarioName) {
        String scenario = scenarioName == null ? "" : scenarioName;

        if (scenario.contains("No_Auth")
                || scenario.contains("NoAuth")) {
            return getRequestSpecWithoutAuth();
        }

        if (scenario.contains("Invalid_Token")
                || scenario.contains("InvalidToken")) {
            return getRequestSpecWithCustomToken("invalid_token_12345");
        }

        if (scenario.contains("Invalid_Auth")
                || scenario.contains("Missing_Bearer")) {
            return getRequestSpecInvalidAuth();
        }

        return getRequestSpec();
    }

    private static RequestSpecBuilder baseBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.url"))
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream));
    }

    private static void initializeLogStream() {
        if (logStream != null) {
            return;
        }

        synchronized (RequestSpec.class) {
            if (logStream != null) {
                return;
            }

            try {
                String filePath = ConfigReader.get("LogFilePath");
                File logFile = new File(filePath);

                File parentDirectory = logFile.getParentFile();

                if (parentDirectory != null && !parentDirectory.exists()) {
                    parentDirectory.mkdirs();
                }

                logStream = new PrintStream(
                        new FileOutputStream(logFile, false)
                );

            } catch (Exception exception) {
                throw new RuntimeException(
                        "Failed to initialize the API log file.",
                        exception
                );
            }
        }
    }

    public static void logScenarioName(String scenarioName) {
        initializeLogStream();

        logStream.println();
        logStream.println("==================================================");
        logStream.println("SCENARIO: " + scenarioName);
        logStream.println("==================================================");
        logStream.println();

        logStream.flush();
    }

    public static String getBaseUri() {
        return ConfigReader.get("base.url");
    }

    /**
     * Parse a JSON request/response body into a POJO.
     * Reuses the same ObjectMapper instance used by Rest Assured.
     */
    public static <T> T parseBody(String body, Class<T> clazz)
            throws JsonProcessingException {

        return OBJECT_MAPPER.readValue(body, clazz);
    }
}