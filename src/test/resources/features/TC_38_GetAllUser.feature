@User
Feature: User module for LMS API

  Background:
     Given Admin sets Bearer token

  @Get_Positive
  Scenario Outline: Check if Admin is able to retrieve all users with valid endpoint
    Given Admin create GET request with valid data for "<scenario>" from excel sheet
    When Admin sends GET request to retrieve all users
    Then Admin receives 200 OK status with response body

    Examples:
      | scenario                   |
      |Get_All_Users_Valid |

  @Get_Negative
  Scenario Outline: Check if Admin is unable to retrieve all users with invalid endpoint
    Given Admin create GET request with invalid input for "<scenario>" from excel sheet
    When Admin sends GET request to retrieve all users
    Then Admin receives expected status code for invalid endpoint

    Examples:
      | scenario                     |
      | Get_All_Users_Invalid_Endpoint |

  @Get_Negative
  Scenario Outline: Check if Admin is unable to retrieve all users with invalid method
    Given Admin create invalid request for "<scenario>" from excel sheet
    When Admin sends invalid method request to retrieve all users
    Then Admin receives expected status code for invalid method

    Examples:
      | scenario                   |
      | Get_All_Users_Invalid_Method |

  @Get_Negative
  Scenario Outline: Check if Admin is unable to retrieve all users without authorization
    Given Admin create GET request without authorization for "<scenario>" from excel sheet
    When Admin sends GET request to retrieve all users
    Then Admin receives expected status code for user without authorization

    Examples:
      | scenario                              |
      | GetAllUsers_Without_Authorization    |