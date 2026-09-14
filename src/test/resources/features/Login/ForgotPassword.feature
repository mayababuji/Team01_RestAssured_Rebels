@loginModule
Feature: Forgot Password Confirm Email

  Background:
    Given Admin sets No Auth for forgot password

  @forgotPassword
  Scenario Outline: Validate forgot password with <ScenarioName>
    Given Admin prepares forgot password request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpoint>" for forgot password 
    Then Admin validates forgot password response with status code

    Examples:
      | ScenarioName        | Method | Endpoint                     |
      | Valid email         | POST   | forgotPasswordEndpoint       |
      | Invalid content type| POST   | forgotPasswordEndpoint       |
      | Invalid method      | GET    | forgotPasswordEndpoint       |
      | Invalid endpoint    | POST   | forgotPasswordInvalidEndpoint|
      | Empty email         | POST   | forgotPasswordEndpoint       |
      | Invalid email       | POST   | forgotPasswordEndpoint       |
      | Null email          | POST   | forgotPasswordEndpoint       |
      | Unregistered email  | POST   | forgotPasswordEndpoint       |
