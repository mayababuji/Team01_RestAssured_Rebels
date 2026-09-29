@loginModule
Feature: LMS User Sign In and Forgot Password (Login Controller)

  Background:
    Given Admin sets No Auth

  # ---------------------------------------------------------------- SIGN IN

  @login @Login_Positive
  Scenario: Validate login with valid credentials
    Given Admin prepares login request body for "Login valid credentials" from Excel
    When Admin sends "POST" request to "loginEndpoint"
    Then Admin validates login response with status code

  @login @Login_Negative
  Scenario Outline: Validate login error handling with <ScenarioName>
    Given Admin prepares login request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>"
    Then Admin validates login response with status code

    Examples:
      | ScenarioName                         | Method | Endpointkey     |
      | Login invalid method                 | GET    | loginEndpoint   |
      | Login invalid base URL               | POST   | loginEndpoint   |
      | Login invalid content type           | POST   | loginEndpoint   |
      | Login invalid endpoint               | POST   | invalidEndpoint |
      | Login empty email                    | POST   | loginEndpoint   |
      | Login special characters in email    | POST   | loginEndpoint   |
      | Login email with spaces              | POST   | loginEndpoint   |
      | Login null email                     | POST   | loginEndpoint   |
      | Login unregistered email             | POST   | loginEndpoint   |
      | Login empty password                 | POST   | loginEndpoint   |
      | Login special characters in password | POST   | loginEndpoint   |
      | Login password with spaces           | POST   | loginEndpoint   |
      | Login null password                  | POST   | loginEndpoint   |
      | Login inactive user                  | POST   | loginEndpoint   |
      | Login without request body           | POST   | loginEndpoint   |

  # ---------------------------------------------------------- FORGOT PASSWORD

  @forgotPassword @ForgotPassword_Positive
  Scenario: Validate forgot password with valid email
    Given Admin prepares forgot password request body for "Forgot password valid email" from Excel
    When Admin sends "POST" request to "forgotPasswordEndpoint" for forgot password
    Then Admin validates forgot password response with status code

  @forgotPassword @ForgotPassword_Negative
  Scenario Outline: Validate forgot password error handling with <ScenarioName>
    Given Admin prepares forgot password request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for forgot password
    Then Admin validates forgot password response with status code

    Examples:
      | ScenarioName                         | Method | Endpointkey                   |
      | Forgot password invalid content type | POST   | forgotPasswordEndpoint        |
      | Forgot password invalid method       | GET    | forgotPasswordEndpoint        |
      | Forgot password invalid endpoint     | POST   | forgotPasswordInvalidEndpoint |
      | Forgot password empty email          | POST   | forgotPasswordEndpoint        |
      | Forgot password invalid email        | POST   | forgotPasswordEndpoint        |
      | Forgot password null email           | POST   | forgotPasswordEndpoint        |
      | Forgot password unregistered email   | POST   | forgotPasswordEndpoint        |
