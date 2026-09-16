@loginModule
Feature: User Logout

  @logout @Logout_Negative
  Scenario Outline: Admin logs out with invalid details
    Given Admin sets "<Authentication>" authentication for logout
    And Admin prepares logout request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for logout
    Then Admin validates logout response with status code

    Examples:
      | ScenarioName                          | Authentication | Method | Endpointkey           |
      | Logout with invalid endpoint          | Bearer Token   | GET    | logoutInvalidEndpoint |
      | Logout with invalid method            | Bearer Token   | POST   | logoutEndpoint        |
      | Logout with no authorization          | No Auth        | GET    | logoutEndpoint        |
      | Logout with invalid token             | Invalid Token  | GET    | logoutEndpoint        |
      | Logout with expired token             | Expired Token  | GET    | logoutEndpoint        |

  @logout @Logout_Positive
  Scenario Outline: Admin logs out with valid details
    Given Admin sets "<Authentication>" authentication for logout
    And Admin prepares logout request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for logout
    Then Admin validates logout response with status code

    Examples:
      | ScenarioName            | Authentication | Method | Endpointkey    |
      | Valid logout token      | Bearer Token   | GET    | logoutEndpoint |