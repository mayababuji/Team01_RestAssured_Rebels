@loginModule
Feature: User Sign In(Login Controller))

  Background:
    Given Admin sets No Auth
  @login
  Scenario Outline: Validate login with <ScenarioName>
    Given Admin prepares login request body for "<ScenarioName>" from Excel
    When Admin sends "<Method>" request to "<Endpoint>"
    Then Admin validates login response with status code "<StatusCode>"

    Examples:
      | ScenarioName               | Method | Endpoint     | StatusCode |
      | Valid credential           | POST   | loginEndpoint   | 200        |
      | Invalid method             | GET    | loginEndpoint   | 405        |
      | Invalid base URL           | POST   | loginEndpoint   | 404        |
      | Invalid content type       | POST   | loginEndpoint   | 415        |
      | Invalid endpoint           | POST   | invalidEndpoint | 401        |
      | Empty email                | POST   | loginEndpoint   | 400        |
      | Special characters in email| POST   | loginEndpoint   | 400        |
      | Email having spaces        | POST   | loginEndpoint   | 400        |
      | Null in email field        | POST   | loginEndpoint   | 400        |
      | Unregistered email         | POST   | loginEndpoint   | 400        |
      | Empty password             | POST   | loginEndpoint   | 400        |
      | Special characters in password | POST | loginEndpoint | 401        |
      | Password having spaces     | POST   | loginEndpoint   | 400        |
      | Null in password field     | POST   | loginEndpoint   | 400        |
      | Inactive user              | POST   | loginEndpoint   | 400        |
      | Without request body       | POST   | loginEndpoint   | 400        |