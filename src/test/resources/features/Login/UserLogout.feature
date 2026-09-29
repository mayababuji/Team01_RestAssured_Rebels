@loginModule
Feature: LMS User Logout (Login Controller)

  # Every scenario starts from a fresh login token.
  # The authentication set-up differs per scenario, so it is the first step of each scenario.
  Background:
    Given Admin has a fresh login token

  # ------------------------------------------- Authentication: Bearer Token

  @logout @Logout_Positive
  Scenario: Validate logout with valid token
    Given Admin sets "Bearer Token" authentication for logout
    Given Admin prepares logout request body for "Logout valid token" from Excel
    When Admin sends "GET" request to "logoutEndpoint" for logout
    Then Admin validates logout response with status code

  @logout @Logout_Negative
  Scenario Outline: Validate logout error handling with <ScenarioName>
    Given Admin sets "Bearer Token" authentication for logout
    Given Admin prepares logout request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpointkey>" for logout
    Then Admin validates logout response with status code

    Examples:
      | ScenarioName             | Method | Endpointkey           |
      | Logout invalid endpoint  | GET    | logoutInvalidEndpoint |
      | Logout invalid method    | POST   | logoutEndpoint        |

  # ------------------------------------------------- Authentication: No Auth

  @logout @Logout_Negative
  Scenario: Validate logout without authorization
    Given Admin sets "No Auth" authentication for logout
    Given Admin prepares logout request body for "Logout without authorization" from Excel
    When Admin sends "GET" request to "logoutEndpoint" for logout
    Then Admin validates logout response with status code

  # ------------------------------------------- Authentication: Invalid Token

  @logout @Logout_Negative
  Scenario: Validate logout with invalid token
    Given Admin sets "Invalid Token" authentication for logout
    Given Admin prepares logout request body for "Logout invalid token" from Excel
    When Admin sends "GET" request to "logoutEndpoint" for logout
    Then Admin validates logout response with status code

  # -------------------------------------------- Authentication: Expired Token

  @logout @Logout_Negative
  Scenario: Validate logout with expired token
    Given Admin sets "Expired Token" authentication for logout
    Given Admin prepares logout request body for "Logout expired token" from Excel
    When Admin sends "GET" request to "logoutEndpoint" for logout
    Then Admin validates logout response with status code
