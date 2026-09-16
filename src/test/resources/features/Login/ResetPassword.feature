@loginModule
Feature: Reset Password
  @resetPassword @resetPassword_Positive
  Scenario Outline: Admin resets password with valid details
    Given Admin sets "<Authentication>" authentication for reset password
    And Admin prepares reset password request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for reset password
    Then Admin validates reset password response with status code
    
    Examples:
      | ScenarioName               | Authentication | Method | Endpointkey                  |
      | Reset password valid token | Bearer Token   | POST   | resetPasswordEndpoint |

  @resetPassword @resetPassword_Negative
  Scenario Outline: Admin resets password with invalid details
    Given Admin sets "<Authentication>" authentication for reset password
    And Admin prepares reset password request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for reset password
    Then Admin validates reset password response with status code

    Examples:
      | ScenarioName                          | Authentication          | Method | Endpointkey                  |
     | Reset password invalid email          | Bearer Token            | POST   | resetPasswordEndpoint        |
      | Reset password invalid password       | Bearer Token            | POST   | resetPasswordEndpoint        |
     | Reset password invalid endpoint       | Bearer Token            | POST   | resetPasswordInvalidEndpoint |
      | Reset password invalid method         | Bearer Token            | GET    | resetPasswordEndpoint        |
      | Reset password without authentication | No Auth                 | POST   | resetPasswordEndpoint        |
      | Reset password expired token          | Expired Token           | POST   | resetPasswordEndpoint        |
      | Reset password empty token            | Empty Token             | POST   | resetPasswordEndpoint        |

