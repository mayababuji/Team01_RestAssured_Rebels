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