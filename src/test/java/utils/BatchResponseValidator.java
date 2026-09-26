package utils;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public final class BatchResponseValidator {

    private BatchResponseValidator() {
    }

    public static void validateInactiveBatch(
            JsonPath json,
            int expectedBatchId,
            String expectedBatchName) {

        Object responseBody = json.get("$");

        Assert.assertNotNull(
                responseBody,
                "Batch response body is null."
        );

        Map<String, Object> matchingBatch;

        // Response is a list:
        // [
        //   { "batchId": 1137, "batchName": "...", "batchStatus": "Inactive" }
        // ]
        if (responseBody instanceof List<?>) {

            List<Map<String, Object>> batches = json.getList("$");

            Assert.assertNotNull(
                    batches,
                    "Expected Batch list in the response, but it was null."
            );

            Assert.assertFalse(
                    batches.isEmpty(),
                    "Expected at least one Batch in the response, but the list was empty."
            );

            matchingBatch = batches.stream()
                    .filter(batch -> {
                        Object batchId = batch.get("batchId");

                        return batchId instanceof Number
                                && ((Number) batchId).intValue() == expectedBatchId;
                    })
                    .findFirst()
                    .orElse(null);

            Assert.assertNotNull(
                    matchingBatch,
                    "Batch ID " + expectedBatchId
                            + " was not found in the response list."
            );

            // Response is one JSON object:
            // {
            //   "batchId": 1137,
            //   "batchName": "...",
            //   "batchStatus": "Inactive"
            // }
        } else if (responseBody instanceof Map<?, ?>) {

            Map<String, Object> batch = json.getMap("$");

            Assert.assertNotNull(
                    batch,
                    "Expected Batch object in the response, but it was null."
            );

            matchingBatch = batch;

        } else {
            throw new IllegalStateException(
                    "Unexpected Batch response type: "
                            + responseBody.getClass().getName()
            );
        }

        Object actualBatchId = matchingBatch.get("batchId");

        Assert.assertTrue(
                actualBatchId instanceof Number,
                "batchId should be numeric. Actual value: " + actualBatchId
        );

        Assert.assertEquals(
                ((Number) actualBatchId).intValue(),
                expectedBatchId,
                "Batch ID does not match."
        );

        Assert.assertEquals(
                String.valueOf(matchingBatch.get("batchName")),
                expectedBatchName,
                "Batch name does not match."
        );

        Assert.assertEquals(
                String.valueOf(matchingBatch.get("batchStatus")),
                "Inactive",
                "Deleted Batch status should be Inactive."
        );
    }
}