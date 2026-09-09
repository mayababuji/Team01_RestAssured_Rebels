package utils;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private final Map<String, Object> scenarioData = new HashMap<>();

    public void setContext(String key, Object value) {
        scenarioData.put(key, value);
    }

    public Object getContext(String key) {
        return scenarioData.get(key);
    }

    public boolean contains(String key) {
        return scenarioData.containsKey(key);
    }

    public void clear() {
        scenarioData.clear();
    }
}
