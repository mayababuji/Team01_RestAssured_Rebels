@programmodule
Feature: LMS  Program Module

  Scenario Outline: Verify if admin is able to create a Program
    Given Admin sets authorization to Bearer Token
    When Admin sends POST request to create program with different payload for "<ScenarioName>" from dataSheet
    Then Admin verifies the response payload with expected output from the data sheet

    Examples:
      | ScenarioName                               |
      | Create valid request body                  |
      | Create description Length between 4 and 25 |
      | Create name Length between 4 and 25        |
# -------------------------------------------------POST Invalid----------------------------------------

  Scenario Outline: Verify if admin is able to create a Program with Invalid Data
    Given Admin sets authorization to Bearer Token
    When Admin sends POST request to create program with different payload for "<ScenarioName>" from dataSheet
    Then Admin verifies the response payload with expected output from the data sheet

    Examples:
      | ScenarioName                                                       |
      | Create Program with Invalid EndPoint                               |
      | Create Program with Invalid Content type                           |
      | Create Program with Invalid Method                                 |
      | Create Program with Existing Program Name                          |
      | Create Program with trailing Space in Program Name                 |
      | Create Program with numbers in program Name                        |
      | Create Program with invalid Status                                 |
      | Create Program with Program Name less than desired length          |
      | Create Program with empty Payload                                  |
      | Create Program with alpha and special chars in program description |
      | Create Program with Program Name larger than desired length        |
      | Create Program without Program Name                                |