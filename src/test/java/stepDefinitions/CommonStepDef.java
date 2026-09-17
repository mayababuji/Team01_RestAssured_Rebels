package stepDefinitions;

import io.cucumber.java.en.Given;
import specBuilder.RequestSpec;
import utils.ScenarioContext;

public class CommonStepDef {

    private final ScenarioContext scenarioContext;

    public CommonStepDef(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("Admin sets authorization to Bearer Token")
    public void admin_sets_authorization_to_bearer_token() {

        Boolean skipAuth = (Boolean) scenarioContext.getContext(
                "SKIP_AUTH"
        );

        if (Boolean.TRUE.equals(skipAuth)) {
            return;
        }

        scenarioContext.setRequestSpec(
                RequestSpec.getRequestSpec()
        );
    }
}