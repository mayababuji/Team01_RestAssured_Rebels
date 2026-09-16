package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class ApiExecutor {

    private ApiExecutor() {
    }

    public static Response get(RequestSpecification request) {
        return request
                .when()
                .log()
                .all()
                .get();
    }

    public static Response put(RequestSpecification request) {
        return request
                .when()
                .log()
                .all()
                .put();
    }

    public static Response delete(RequestSpecification request) {
        return request
                .when()
                .log()
                .all()
                .delete();
    }

    public static Response post(RequestSpecification request) {
        return request
                .when()
                .log()
                .all()
                .post();
    }
}