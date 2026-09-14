package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class BatchRequestUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BatchRequestUtil() {
        // Prevent utility-class instantiation.
    }

    /**
     * Produces a batch JSON request body with valid dynamic program data.
     *
     * @param requestBody Original JSON body read from Excel.
     * @param batchNoOfClassesValue Value to set for batchNoOfClasses.
     *                              Pass null to remove the property.
     * @param batchStatusValue Value to set for batchStatus.
     *                         Pass null to retain the Excel value.
     * @return Updated JSON string ready for REST Assured .body(...)
     */
    public static String createValidBatchJsonWithOverrides(
            String requestBody,
            Object batchNoOfClassesValue,
            String batchStatusValue) throws IOException {

        Map<String, Object> bodyMap = MAPPER.readValue(
                requestBody,
                new TypeReference<Map<String, Object>>() {
                }
        );

        String validBatchName = SharedTestData.programName
                + "_"
                + TestDataUtil.randomNumericSuffix(4);

        bodyMap.put("batchId", SharedTestData.batchId);
        bodyMap.put("batchName", validBatchName);
        bodyMap.put("programId", SharedTestData.programId);
        bodyMap.put("programName", SharedTestData.programName);

        if (batchNoOfClassesValue == null) {
            bodyMap.remove("batchNoOfClasses");
        } else {
            bodyMap.put("batchNoOfClasses", batchNoOfClassesValue);
        }

        if (batchStatusValue != null) {
            bodyMap.put("batchStatus", batchStatusValue);
        }

        return MAPPER.writeValueAsString(bodyMap);
    }

    /**
     * Produces a valid JSON body for a NoAuth PUT scenario.
     * This keeps every payload field valid so authorization is the only
     * expected reason for request failure.
     */
    public static String createValidBatchJson(
            String requestBody) throws IOException {

        return createValidBatchJsonWithOverrides(
                requestBody,
                1,
                "Active"
        );
    }

    /**
     * Produces a request body for an invalid/deleted program ID scenario.
     * The batch name must still follow:
     * <programName>_<number>
     */
    public static String createBatchJsonForSpecificProgram(
            String requestBody,
            int programId,
            String programName) throws IOException {

        Map<String, Object> bodyMap = MAPPER.readValue(
                requestBody,
                new TypeReference<Map<String, Object>>() {
                }
        );

        bodyMap.put("batchId", SharedTestData.batchId);
        bodyMap.put(
                "batchName",
                programName
                        + "_"
                        + TestDataUtil.randomNumericSuffix(4)
        );
        bodyMap.put("programId", programId);
        bodyMap.put("programName", programName);

        return MAPPER.writeValueAsString(bodyMap);
    }
}