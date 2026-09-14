@loginModule
Feature: Forgot Password Confirm Email

  Background:
    Given Admin sets No Auth for forgot password

  @forgotPassword @ForgotPassword_Positive
  Scenario Outline: Validate forgot password with <ScenarioName>
    Given Admin prepares forgot password request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for forgot password
    Then Admin validates forgot password response with status code

    Examples:
      | ScenarioName | Method | Endpointkey                |
      | Valid email  | POST   | forgotPasswordEndpoint  |

  @forgotPassword @ForgotPassword_Negative
  Scenario Outline: Validate forgot password error handling with <ScenarioName>
    Given Admin prepares forgot password request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for forgot password
    Then Admin validates forgot password response with status code

    Examples:
      | ScenarioName          | Method | Endpointkey                   |
      | Invalid content type  | POST   | forgotPasswordEndpoint        |
      | Invalid method        | GET    | forgotPasswordEndpoint        |
      | Invalid endpoint      | POST   | forgotPasswordInvalidEndpoint |
      | Empty email           | POST   | forgotPasswordEndpoint        |
      | Invalid email         | POST   | forgotPasswordEndpoint        |
      | Null email            | POST   | forgotPasswordEndpoint        |
      | Unregistered email    | POST   | forgotPasswordEndpoint        |