package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private RequestSpecification requestSpec;
    private Response response;
    private Map<String, String> excelData;

    private final Map<String, Object> contextData = new HashMap<>();

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

    public void setExcelData(Map<String, String> excelData) {
        this.excelData = excelData;
    }

    public Map<String, String> getExcelData() {
        return excelData;
    }

    public void setContext(String key, Object value) {
        contextData.put(key, value);
    }

    public Object getContext(String key) {
        return contextData.get(key);
    }
}