@User
Feature: User Controller

  Background:
    Given  Admin has a valid authorization token in user controller


  Scenario Outline: Check if admin able to create a admin with valid endpoint and request body
    When Admin sends HTTPS POST Request for "<ScenarioName>" from Excel to create user
    Then Admin receives 201 Created Status with response body
    Examples:
      | ScenarioName                      |
      |CreateUser_with_valid_mandatory_feilds                   |
