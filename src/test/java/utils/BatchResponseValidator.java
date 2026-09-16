package utils;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;

public final class BatchResponseValidator {

    private BatchResponseValidator() {
    }

    public static void validateInactiveBatch(
            JsonPath json,
            int expectedBatchId,
            String expectedBatchName) {

        Assert.assertEquals(
                json.getInt("batchId"),
                expectedBatchId,
                "Batch ID does not match"
        );

        Assert.assertEquals(
                json.getString("batchName"),
                expectedBatchName,
                "Batch name does not match"
        );

        Assert.assertEquals(
                json.getString("batchStatus"),
                "Inactive",
                "Batch status should be Inactive"
        );
    }
}
