@programmodule
Feature: Validate Program Controller

  Background:
    Given Admin sets Authorization to Bearer Token.


# ------------------------------------------------Add New Program-----------------------------------------------


# -----------------------------------------------Post Valid Details---------------------------------------------

  @create_program
  Scenario Outline: Create program with valid and boundary data with "<Scenario>"
    Given Admin creates POST request using "<Scenario>"
    When Admin sends Post request to create program with different payload for "<Scenario>" from data sheet
    Then Admin verifies the response payload with expected output for "<Scenario>" from the data sheet

    Examples:
      | Scenario                                   |
      | Create valid request body                  |
      | Create only Mandatory fields               |
      | Create description Length between 4 and 25 |
      | Create name Length between 4 and 25        |
      
# -------------------------------------------------POST Invalid----------------------------------------

 Scenario Outline: Create program with invalid data "<Scenario>"
   Given Admin creates POST request using "<Scenario>"
   When Admin sends Post request to create program with different payload for "<Scenario>" from data sheet
   Then Admin verifies the response payload with expected output for "<Scenario>" from the data sheet

    Examples:
      | Scenario                                                           |
      | Create Program with Invalid EndPoint                               |
      | Create Program with Invalid Content type                           |
      | Create Program with Invalid Method                                 |
      | Create Program with Existing Program Name                          |
      | Create Program with trailing Space in Program Name                 |
      | Create Program with numbers in program Name                        |
      | Create Program with invalid Status                                 |
      | Create Program without Prgram Name                                 |
      | Create Program with Program Name less than desired length          |
      | Create Program with empty Payload                                  |
      | Create Program with alpha and special chars in program description |
      | Create Program with Program Name larger than desired length        |

























# -------------------------------------------------------------------------------------------------------------------
# Scenario Outline: Verify if admin is able to create a Program
# Given Admin sets authorization to Bearer Token
# When Admin sends POST request to create program with different payload for "<ScenarioName>" from dataSheet
# Then Admin verifies the response payload with expected output from the data sheet
# Examples:
# | ScenarioName      |
# | Create_NewProgram |
