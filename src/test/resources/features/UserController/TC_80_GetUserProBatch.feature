@User
Feature: Get all users linked to batch ID

  Background:
    Given Admin sets Bearer token

  @GetUserProBatch_Positive
  Scenario Outline: Check if Admin is able to retrieve all users linked to batch ID with valid endpoint
    Given Admin create GET request with valid data for "<scenario>" from excel sheet
    When Admin sends GET request to retrieve all users linked to batch
     Then Admin receives 200 OK status with response body for users linked to batch
   

    Examples:
      | scenario                    |
      | Get_User_Pro_Batch_Valid    |


  @GetUserProBatch_Negative
  Scenario Outline: Check if Admin is unable to retrieve users linked to batch with invalid batch ID
    Given Admin create GET request with invalid batch ID for "<scenario>" from excel sheet
    When Admin sends TC-80 invalid batch ID request
    Then Admin receives 404 Not Found status with batch ID not found message

    Examples:
      | scenario                              |
      | Get_User_Pro_Batch_Invalid_BatchID    |


  @GetUserProBatch_Negative
  Scenario Outline: Check if Admin is unable to retrieve users linked to batch with invalid endpoint
    Given Admin create GET request with invalid endpoint for "<scenario>" from excel sheet
   When Admin sends TC-80 invalid endpoint request
   Then Admin receives expected 404 status code for TC-80 invalid endpoint

    Examples:
      | scenario                              |
      | Get_User_Pro_Batch_Invalid_Endpoint   |


 @GetUserProBatch_Negative
    Scenario Outline: Check if Admin is unable to retrieve users linked to batch with invalid method
    Given Admin create invalid request for TC-80 "<scenario>" from excel sheet
    When Admin sends invalid method request to retrieve all users linked to batch
    Then Admin receives expected 405 status code for TC-80 invalid method

    Examples:
      | scenario                          |
      | Get_User_Pro_Batch_Invalid_Method |

  
  @NoAuth
 @GetUserProBatch_Negative
Scenario Outline: Check if Admin is unable to retrieve users linked to batch without authorization
    Given Admin create TC-80 GET request without authorization for "<scenario>" from excel sheet
    When Admin sends TC-80 GET request without authorization
    Then Admin receives expected 401 status code for TC-80 without authorization

    Examples:
      | scenario                                  |
      | Get_User_Pro_Batch_Without_Authorization |