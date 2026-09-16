@Batch
Feature: Program Batch module for LMS API
  Background:
    Given Admin sets authorization to Bearer Token
  @Post_Positive
  Scenario Outline: Check if Admin is able to create batch with valid batch name and description
    Given Admin create POST request with valid data for "<scenario>" from excel sheet
    When Admin sends POST request to create program batch
    Then Admin receives created status with response body

    Examples:
      | scenario                                  |
      | CreateBatch_Valid_batchName               |
      | CreateBatch_Valid_batchName_minLen        |
      | CreateBatch_Valid_batchDescription_minLen |
      | CreateBatch_Valid_batchDescription_maxLen |

  Scenario Outline: Check if Admin is able to create batch with invalid input
    Given Admin create POST request with invalid input for "<scenario>" from excel sheet
    When Admin sends POST request to create program batch
    Then Admin receives expected status code with error message

    Examples:
      | scenario                             |
      | CreateBatch_Existing_Batch           |
      | CreateBatch_Missing_Mandatory_Fields |

  @NoAuth
  Scenario: Check if Admin is able to create batch without authorization
    Given Admin create POST request with invalid input for "CreateBatch_NoAuth" from excel sheet
    When Admin sends POST request to create program batch
    Then Admin receives expected status code with error message

  @GetAllProgramBatch_Positive
  Scenario: Check if Admin is able to retrieve all program batches
    Given Admin create GET request with valid endpoint
    When Admin sends GET request to retrieve all batches
    Then Admin receives success code with response body

  @GetAllProgramBatch_Negative
  Scenario: Check if Admin is able to retrieve all batches with invalid endpoint
    Given Admin create GET request with invalid endpoint
    When Admin sends GET request to retrieve all batches
    Then Admin receives expected status code with error message

  @NoAuth
  Scenario: Check if Admin is able to retrieve all batches with no authentication
    Given Admin create GET request with no authentication
    When Admin sends GET request to retrieve all batches
    Then Admin receives expected status code with error message

  @GetProgramBatchByBatchId
  Scenario: Check if Admin is able to retrieve batch with valid batchId
    Given Admin create GET request with valid batchId
    When Admin sends GET request to retrieve the batch
    Then Admin receives success code with GET response body

  @GetByBatchId
  Scenario Outline: Check if Admin is able to retrieve batch by batchId with invalid input
    Given Admin create GET request by BatchId with invalid input for "<scenario>" from excel sheet
    When Admin sends GET request to retrieve the batch
    Then Admin receives expected status code with error message

    Examples:
      | scenario                      |
      | GetBatchById_InvalidBatchId   |
      |GetBatchById_Invalid_Endpoint  |

  @NoAuth
  Scenario: Check if Admin is able to retrieve batch by batchId with invalid input
    Given Admin create GET request by BatchId with invalid input for "GetBatch_ById_NoAuth" from excel sheet
    When Admin sends GET request to retrieve the batch
    Then Admin receives expected status code with error message

  @GetByBatchName
  Scenario: Check if Admin is able to retrieve batch with valid batch name
    Given Admin create GET request to retrieve batch with valid batch name
    When Admin sends GET request to retrieve the batch
    Then Admin receives success code with GET response body having given batch name

  @GetByBatchName
  Scenario Outline: Check if Admin is able to retrieve batch by BatchName with invalid input
    Given Admin create GET request by BatchName with invalid input for "<scenario>" from excel sheet
    When Admin sends GET request to retrieve the batch
    Then Admin receives expected status code with error message

    Examples:
      | scenario                         |
      | GetBatchByName_Invalid_BatchName |
      | GetBatchByName_Invalid_Endpoint  |

  @NoAuth
  Scenario: Check if Admin is unable to retrieve batch by batch name without authorization
    Given Admin create GET request by BatchName with invalid input for "GetBatchByName_NoAuth" from excel sheet
    When Admin sends GET request to retrieve the batch
    Then Admin receives expected status code with error message

  @GetByProgramId
  Scenario: Check if Admin is able to retrieve batch with valid programId
    Given Admin create GET request to retrieve batch with valid programId
    When Admin sends GET request to retrieve the batch
    Then Admin receives success code with GET response body having given programId



  @GetByProgramId
  Scenario Outline: Check if Admin is able to retrieve batch by programId with invalid input
    Given Admin create GET request by programId with invalid input for scenario "<scenario>" from excel sheet
    When Admin sends GET request to retrieve the batch
    Then Admin receives expected status code with error message

    Examples:
      | scenario                            |
      | GetBatchByProgram_Invalid_ProgramId |
      | GetBatchByProgram_Invalid_Endpoint  |

  @NoAuth
  Scenario: Check if Admin is unable to retrieve batches by programId without authorization
    Given Admin create GET request by programId with invalid input for scenario "GetBatchByProgram_NoAuth" from excel sheet
    When Admin sends GET request to retrieve batches by programId
    Then Admin receives expected status code with error message


  @PutBatchByBatchId
  Scenario: Check if Admin is able to update batch with valid batch Id to update programId
    Given Admin create PUT request to update batch with valid batchId for scenario "PutBatchById_Valid_BatchId_UpdateProgram"
    When Admin sends PUT request to update the batch
    Then Admin received success code with updated ProgramId in response
  @PutBatchByBatchId
  Scenario Outline: Check if Admin is able to update batch with invalid input
    Given Admin create PUT request with invalid input for each "<scenario>" from excel sheet
    When Admin sends PUT request to update the batch
    Then Admin receives expected status code with error message

    Examples:
      | scenario                              |
      | PutBatchById_Missing_Mandatory_Fields |
      | PutBatchById_Invalid_BatchId          |
      | PutBatchById_Invalid_Endpoint         |
      | PutBatchById_Invalid_BatchStatus      |
      | PutBatchById_Invalid_NoOfClasses      |
      | PutBatchById_Invalid_ProgramId        |
      | PutBatchById_Invalid_BatchName        |




  @PutBatchByBatchId
  Scenario: Check if Admin is able to update batch with valid batch Id to update batchname
    Given Admin create PUT request to update batch with valid batchId for scenario "PutBatchById_Valid_BatchId_UpdateBatchName"
    When Admin sends PUT request to update the batch
    Then Admin received success code with updated batchName in response

  @PutBatchByBatchId
  Scenario: Check if Admin is able to update batch with valid batch Id to update batchStatus
    Given Admin create PUT request to update batch with valid batchId for scenario "PutBatchById_Valid_BatchId_UpdateBatchStatus"
    When Admin sends PUT request to update the batch
    Then Admin received success code with updated batchStatus in response


  @PutBatchByBatchId
  Scenario: Check if Admin is able to update batch with valid batch Id to update batchNoOfClasses
    Given Admin create PUT request to update batch with valid batchId for scenario "PutBatchById_Valid_BatchId_UpdateNoOfClasses"
    When Admin sends PUT request to update the batch
    Then Admin received success code with updated batchNoOfClasses in response


  @PutBatchByBatchId
  Scenario: Check if Admin is able to update batch with valid batchId and missing additional fields
    Given Admin create PUT request to update batch with valid batchId for scenario "PutBatchById_Missing_Additional_Fields"
    When Admin sends PUT request to update the batch
    Then Admin receives expected status code with error message


  @PutBatchByBatchId
  @NoAuth
  Scenario: Check if Admin is unable to update batch without authorization
    Given Admin create PUT request with invalid input for each "PutBatchById_NoAuth" from excel sheet
    When Admin sends PUT request to update the batch
    Then Admin receives expected status code with error message

  @DeleteBatchById
  Scenario Outline: Check if Admin is able to delete batch by batchId with invalid input
    Given Admin create DELETE request by BatchId with invalid input for scenario "<scenario>" from excel sheet
    When Admin sends DELETE request to delete the batch
    Then Admin receives expected status code with error message

    Examples:
      | scenario                         |
      | DeleteBatchById_NoAuth           |
      | DeleteBatchById_Invalid_Endpoint |
      | DeleteBatchById_Invalid_BatchId  |

  @DeleteBatchById
  Scenario: Check if Admin is able to delete batchById with valid BatchId
    Given Admin create DELETE request with valid batchId
    When Admin sends DELETE request to delete the batch
    Then Admin receives success code with deleted message

  @DeleteBatchById
  Scenario: Check if Admin is able to get batchById after batch is deleted
    When Admin sends GET request to retrieve deleted batch with Id
    Then Admin receives success code with GET response body for deleted batch
#
  @DeleteBatchByName
  Scenario: Check if Admin is able to get batchByName after batch is deleted
    When Admin sends GET request to retrieve deleted batch with name
    Then Admin receives success code with GET response body for deleted batch by name

  @DeleteBatchById
  Scenario: Check if Admin is able to update deleted batchbyId
    When Admin sends PUT request to update deleted batch status
    Then Admin receives success code with Active batch status in the response body