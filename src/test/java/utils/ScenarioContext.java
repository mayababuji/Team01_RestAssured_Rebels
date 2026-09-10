package utils;

import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private final Map<String, Object> scenarioData = new HashMap<>();
    private static final String KEY_REQUEST_SPEC = "requestSpec";

    public void setContext(String key, Object value) {
        scenarioData.put(key, value);
    }

    public Object getContext(String key) {
        return scenarioData.get(key);
    }

    public void setRequestSpec(RequestSpecification requestSpec) {
        scenarioData.put(KEY_REQUEST_SPEC, requestSpec);
    }

    public RequestSpecification getRequestSpec() {
        return (RequestSpecification) scenarioData.get(KEY_REQUEST_SPEC);
    }

    public boolean contains(String key) {
        return scenarioData.containsKey(key);
    }

    public void clear() {
        scenarioData.clear();
    }
}
