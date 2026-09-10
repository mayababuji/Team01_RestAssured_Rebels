Feature: User Module [Post Operation]

    Background: 
        Given Admin sets Bearer token

    @createuser
    Scenario Outline: Check if admin is able to create user with valid/invalid details
        Given Admin creates POST Request for the LMS API endpoint with data from Excel "<ScenarioName>"
        When Admin sends HTTPS Request and request Body for user1
        Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
        | ScenarioName      |
        | Create_Valid_User |