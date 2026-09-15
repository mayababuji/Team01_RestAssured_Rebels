package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private final Map<String, Object> scenarioData = new HashMap<>();
    private RequestSpecification requestSpec;
    private Response response;

    public void setContext(String key, Object value) {
        scenarioData.put(key, value);
    }

    public Object getContext(String key) {
        return scenarioData.get(key);
    }

    public boolean contains(String key) {
        return scenarioData.containsKey(key);
    }

    public void setRequestSpec(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void clear() {
        scenarioData.clear();
        this.requestSpec = null;
        this.response = null;
    }
}