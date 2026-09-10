@programmodule
Feature: Validate Program Module


  Scenario Outline: Verify if admin is able to create a Program
    Given Admin has a valid authorization token set
    When Admin sends POST request to create program with different payload for "<ScenarioName>" from dataSheet
    Then Admin verifies the response payload with expected output from the data sheet

    Examples:
      | ScenarioName                                                    |
      | Create_NewProgram                                               |