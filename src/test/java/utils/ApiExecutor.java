package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class ApiExecutor {

    private ApiExecutor() {
    }

    public static Response get(RequestSpecification request) {
        return requireRequest(request)
                .when()
                .log()
                .all()
                .get();
    }

    public static Response put(RequestSpecification request) {
        return requireRequest(request)
                .when()
                .log()
                .all()
                .put();
    }

    public static Response delete(RequestSpecification request) {
        return requireRequest(request)
                .when()
                .log()
                .all()
                .delete();
    }

    public static Response post(RequestSpecification request) {
        return requireRequest(request)
                .when()
                .log()
                .all()
                .post();
    }

    private static RequestSpecification requireRequest(
            RequestSpecification request) {

        if (request == null) {
            throw new IllegalStateException(
                    "Request specification is null. "
                            + "Ensure the matching Given step ran before the When step."
            );
        }

        return request;
    }
}