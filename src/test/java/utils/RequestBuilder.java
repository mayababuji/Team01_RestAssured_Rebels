package utils;

import static io.restassured.RestAssured.given;

import io.restassured.specification.RequestSpecification;
import specBuilder.RequestSpec;

public final class RequestBuilder {

    private RequestBuilder() {
    }

    public static RequestSpecification authorized(String endpoint) {
        return given()
                .spec(RequestSpec.getRequestSpec())
                .basePath(endpoint);
    }

    public static RequestSpecification withoutAuth(String endpoint) {
        return given()
                .spec(RequestSpec.getRequestSpecWithoutAuth())
                .basePath(endpoint);
    }

    public static RequestSpecification byBatchId(
            String endpoint,
            int batchId,
            boolean noAuth) {

        RequestSpecification request = noAuth
                ? withoutAuth(endpoint)
                : authorized(endpoint);

        return request.pathParam("batchId", batchId);
    }

    public static RequestSpecification byBatchName(
            String endpoint,
            String batchName,
            boolean noAuth) {

        RequestSpecification request = noAuth
                ? withoutAuth(endpoint)
                : authorized(endpoint);

        return request.pathParam("batchName", batchName);
    }
}