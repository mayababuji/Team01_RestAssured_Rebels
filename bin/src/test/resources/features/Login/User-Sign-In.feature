@loginModule
Feature: User Sign In (Login Controller)

  Background:
    Given Admin sets No Auth

  @login @Login_Positive
  Scenario Outline: Validate login with <ScenarioName>
    Given Admin prepares login request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>"
    Then Admin validates login response with status code

    Examples:
      | ScenarioName      | Method | Endpointkey      |
      | Valid credential  | POST   | loginEndpoint |

  @login @Login_Negative
  Scenario Outline: Validate login error handling with <ScenarioName>
    Given Admin prepares login request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>"
    Then Admin validates login response with status code

    Examples:
      | ScenarioName                    | Method | Endpointkey        |
      | Invalid method                  | GET    | loginEndpoint   |
      | Invalid base URL                | POST   | loginEndpoint   |
      | Invalid content type            | POST   | loginEndpoint   |
      | Invalid endpoint                | POST   | invalidEndpoint |
      | Empty email                     | POST   | loginEndpoint   |
      | Special characters in email     | POST   | loginEndpoint   |
      | Email having spaces             | POST   | loginEndpoint   |
      | Null in email field             | POST   | loginEndpoint   |
      | Unregistered email              | POST   | loginEndpoint   |
      | Empty password                  | POST   | loginEndpoint   |
      | Special characters in password  | POST   | loginEndpoint   |
      | Password having spaces          | POST   | loginEndpoint   |
      | Null in password field          | POST   | loginEndpoint   |
      | Inactive user                   | POST   | loginEndpoint   |
      | Without request body            | POST   | loginEndpoint   |