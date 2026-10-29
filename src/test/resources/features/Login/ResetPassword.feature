@loginModule
Feature: LMS Reset Password (Login Controller)

  # Every scenario starts from a fresh reset token (requested through Forgot Password).
  # The authentication set-up differs per scenario, so it is the first step of each scenario.
  Background:
    Given Admin has a fresh reset token from forgot password

  # ------------------------------------------- Authentication: Bearer Token

  @resetPassword @ResetPassword_Positive
  Scenario: Validate reset password with valid token
    Given Admin sets "Bearer Token" authentication for reset password
    Given Admin prepares reset password request body for "Reset password valid token" from Excel
    When Admin sends "POST" request to "resetPasswordEndpoint" for reset password
    Then Admin validates reset password response with status code

  @resetPassword @ResetPassword_Negative
  Scenario Outline: Validate reset password error handling with <ScenarioName>
    Given Admin sets "Bearer Token" authentication for reset password
    Given Admin prepares reset password request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for reset password
    Then Admin validates reset password response with status code

    Examples:
      | ScenarioName                        | Method | Endpointkey                  |
      | Reset password invalid email        | POST   | resetPasswordEndpoint        |
      | Reset password invalid password     | POST   | resetPasswordEndpoint        |
      | Reset password invalid endpoint     | POST   | resetPasswordInvalidEndpoint |
      | Reset password invalid content type | POST   | resetPasswordEndpoint        |
      | Reset password invalid method       | GET    | resetPasswordEndpoint        |

  # ------------------------------------------------- Authentication: No Auth

  @resetPassword @ResetPassword_Negative
  Scenario: Validate reset password without authentication
    Given Admin sets "No Auth" authentication for reset password
    Given Admin prepares reset password request body for "Reset password without authentication" from Excel
    When Admin sends "POST" request to "resetPasswordEndpoint" for reset password
    Then Admin validates reset password response with status code

  # -------------------------------------------- Authentication: Expired Token

  @resetPassword @ResetPassword_Negative
  Scenario: Validate reset password with expired token
    Given Admin sets "Expired Token" authentication for reset password
    Given Admin prepares reset password request body for "Reset password expired token" from Excel
    When Admin sends "POST" request to "resetPasswordEndpoint" for reset password
    Then Admin validates reset password response with status code

  # ---------------------------------------------- Authentication: Empty Token

  @resetPassword @ResetPassword_Negative
  Scenario: Validate reset password with empty token
    Given Admin sets "Empty Token" authentication for reset password
    Given Admin prepares reset password request body for "Reset password empty token" from Excel
    When Admin sends "POST" request to "resetPasswordEndpoint" for reset password
    Then Admin validates reset password response with status code

  # ------------------------------------- Authentication: Different User's Token

  @resetPassword @ResetPassword_Negative
  Scenario: Validate reset password with another user's token
    Given Admin sets "Different User Token" authentication for reset password
    Given Admin prepares reset password request body for "Reset password another user's token" from Excel
    When Admin sends "POST" request to "resetPasswordEndpoint" for reset password
    Then Admin validates reset password response with status code
