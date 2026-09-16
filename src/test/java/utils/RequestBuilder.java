package utils;

import static io.restassured.RestAssured.given;

import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import specBuilder.RequestSpec;
import io.restassured.response.Response;

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

    public static RequestSpecification byProgramId(
            String endpoint,
            Object programId,
            boolean noAuth) {

        RequestSpecification request = noAuth
                ? withoutAuth(endpoint)
                : authorized(endpoint);

        return request.pathParam("programId", programId);
    }

    public static RequestSpecification withPathParam(
            String endpoint,
            String parameterName,
            Object parameterValue,
            boolean noAuth) {

        RequestSpecification request = noAuth
                ? withoutAuth(endpoint)
                : authorized(endpoint);

        return request.pathParam(parameterName, parameterValue);
    }

    public static RequestSpecification putByBatchId(
            String endpoint,
            int batchId,
            Object requestBody,
            boolean noAuth) {

        RequestSpecification request = byBatchId(
                endpoint,
                batchId,
                noAuth
        );

        if (requestBody != null) {
            request.body(requestBody);
        }

        return request;
    }
    public static RequestSpecification withBody(
            String endpoint,
            Object requestBody,
            boolean noAuth) {

        RequestSpecification request = noAuth
                ? withoutAuth(endpoint)
                : authorized(endpoint);

        if (requestBody != null) {
            request.body(requestBody);
        }

        return request;
    }
    public static RequestSpecification withBodyAndBatchId(
            String endpoint,
            int batchId,
            Object requestBody,
            boolean noAuth) {

        RequestSpecification request = byBatchId(
                endpoint,
                batchId,
                noAuth
        );

        if (requestBody != null) {
            request.body(requestBody);
        }

        return request;
    }


}