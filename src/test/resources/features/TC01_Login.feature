@LoginModule
Feature: Validate User Login Module


  Scenario Outline: Verify User Sign In with No Auth
    Given Admin has the test data for "<ScenarioName>" from Excel with No Auth
    When Admin sends the post request for  Sign In
    Then Admin should receive the status code as  in Excel
    Examples:
      | ScenarioName                      |
      |Valid credential                   |

