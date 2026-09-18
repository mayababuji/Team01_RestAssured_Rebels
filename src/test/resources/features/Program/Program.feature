
@programmodule
Feature: Validate Program Module

  Background:
    Given Admin sets authorization to Bearer Token

  # ============================================================
  # POST VALID
 
  # ============================================================

  Scenario Outline: Verify if admin is able to create a Program
    Given Admin sets authorization to Bearer Token
    When Admin sends POST request to create program with different payload for "<ScenarioName>" from dataSheet
    Then Admin verifies the response payload with expected output from the data sheet

    Examples:
      | ScenarioName                               |
      | Create valid request body                  |
      | Create only Mandatory fields               |
      | Create description Length between 4 and 25 |
      | Create name Length between 4 and 25        |
      
      
  # ============================================================
  # POST INVALID
  
  # ============================================================

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


  # ============================================================
  # GET PROGRAM
  # ============================================================
@GetProgram
Scenario Outline: Admin GET Program by "<operation>" with "<scenarioName>"
    Given Admin creates GET request with "<scenarioName>" for Program
    When Admin sends request to get Programs
    Then Admin receives expected Program response for "<scenarioName>"

Examples:
    | operation        | scenarioName      |
    | Get all Programs | valid endPoint    |
    | Get all Programs | invalid endPoint  |
    | Get all Programs | invalid method    |
    | Program by ID    | valid programId   |
    | Program by ID    | Invalid programId |
    | Program by ID    | invalid endpoint  |
    | Program by ID    | invalid method    |


  # ============================================================
  # GET PROGRAM - NO AUTH
  # ============================================================

  @NoAuth
Scenario Outline: Unauthorized access validation for "<scenarioName>"
    Given Admin creates GET request with "<scenarioName>" for Program
    When Admin sends request to get Programs
    Then Admin receives expected Program response for "<scenarioName>"

Examples:
    | scenarioName             |
    | Get all Programs NoAuth  |
    | Get Program by ID NoAuth |


  # ============================================================
  # PUT PROGRAM
  # ============================================================

@UpdateProgram
Scenario Outline: Admin updates Program by "<operation>" with "<scenarioName>"
    Given Admin creates PUT request with "<scenarioName>" for Program
    When Admin sends request to update Program
    Then Admin receives expected Program response for "<scenarioName>"

Examples:
    | operation       | scenarioName                            |
    | Program by ID   | Update valid programId                  |
    | Program by ID   | Update invalid programId                |
    | Program by ID   | Update with existing programName        |
    | Program by ID   | Update without request body             |
    | Program by ID   | Update invalid baseURI                  |
    | Program by ID   | Update invalid endpoint                 |
    | Program by ID   | Update invalid method                   |
    | Program by Name | Update valid programName                |
    | Program by Name | Update non-existing PrmName             |
    | Program by Name | Update program missing mandatory fields |
    | Program by Name | Update invalid status in request body   |
    | Program by Name | Updates spcl char in program description |


  # ============================================================
  # PUT PROGRAM - NO AUTH
  # ============================================================
@NoAuth
Scenario Outline: Unauthorized access validation for "<scenarioName>"
    Given Admin creates PUT request with "<scenarioName>" for Program
    When Admin sends request to update Program
    Then Admin receives expected Program response for "<scenarioName>"

Examples:
    | scenarioName                         |
    | Update program by programName NoAuth |
    | Update program by programId NoAuth   |
