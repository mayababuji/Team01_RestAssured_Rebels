//package hooks;
//
//import configReader.ConfigReader;
//import io.cucumber.java.Before;
//import io.cucumber.java.Scenario;
//import io.restassured.RestAssured;
//import specBuilder.RequestSpec;
//import utils.ScenarioContext;
//import utils.SharedTestData;
//
//public class Hooks {
//
//    private final ScenarioContext scenarioContext;
//
//    public Hooks(ScenarioContext scenarioContext) {
//        this.scenarioContext = scenarioContext;
//    }
//
//    @Before(order = 0)
//    public void setupBaseUri() {
//        System.out.println(">>> hooks.Hooks is running  setupBaseUri <<<");
//        RestAssured.baseURI = ConfigReader.get("base.url");
//    }
//
//    @Before(order = 1, value = "@NoAuth")
//    public void beforeNoAuth(Scenario scenario) {
//        scenarioContext.setContext("SKIP_AUTH", true);
//
//        System.out.println("======================================");
//        System.out.println("No-auth scenario: " + scenario.getName());
//        System.out.println(">>> hooks.Hooks is running  beforeNoAuth<<<");
//        System.out.println("Authorization header will not be added.");
//        System.out.println("======================================");
//    }
//
//    @Before(order = 2)
//    public void generateTokenForAuthenticatedScenarios(Scenario scenario) {
//        System.out.println(">>> hooks.Hooks is running generateTokenForAuthenticatedScenarios <<<");
//        boolean isNoAuthScenario = scenario.getSourceTagNames()
//                .contains("@NoAuth");
//
//        if (isNoAuthScenario) {
//            System.out.println(">>> hooks.Hooks is running <<<");
//            System.out.println(
//                    "Token generation skipped for @NoAuth scenario: "
//                            + scenario.getName()
//            );
//            return;
//        }
//
//        SharedTestData.generateAndSetToken();
//
//        if (SharedTestData.token == null || SharedTestData.token.isBlank()) {
//            throw new IllegalStateException(
//                    "Token generation completed, but SharedTestData.token is null or blank."
//            );
//        }
//    }
//
//    @Before(order = 3)
//
//    public void logScenarioName(Scenario scenario) {
//        System.out.println(">>> hooks.Hooks is running logScenarioName <<<");
//        RequestSpec.logScenarioName(scenario.getName());
//    }
//}