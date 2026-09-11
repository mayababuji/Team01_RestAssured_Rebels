package hooks;

import configReader.ConfigReader;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import specBuilder.RequestSpec;
import utils.ScenarioContext;

public class Hooks {

    private final ScenarioContext scenarioContext;


    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(order = 0)
    public void setupBaseUri() {
        RestAssured.baseURI = ConfigReader.get("base.url");
    }

    @Before(order = 1)
    public void logScenarioName(Scenario scenario) {
        RequestSpec.logScenarioName(scenario.getName());
    }

    @Before(order = 2, value = "@NoAuth")
    public void beforeNoAuth(Scenario scenario) {
        scenarioContext.setContext("SKIP_AUTH", true);
    }
}