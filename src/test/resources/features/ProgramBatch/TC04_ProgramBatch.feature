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

