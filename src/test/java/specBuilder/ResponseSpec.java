package specBuilder;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpec {

    public static ResponseSpecification status(Integer code) {
        return new ResponseSpecBuilder()
                .expectStatusCode(code)
                .build();
    }

    public static String getResponseMessage(Response response) {
        String contentType = response.getContentType();

        if (contentType != null && contentType.contains("application/json")) {
            return response.jsonPath().getString("message");
        }

        return response.asString();
    }
}
