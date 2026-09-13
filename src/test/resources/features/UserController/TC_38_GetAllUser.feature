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
      | GetAllUsers_Without_Authorization     |

  @GetUserCount_Positive
  Scenario Outline: Check if Admin is able to retrieve count of active and inactive users with valid role
    Given Admin create GET request with valid data for user count scenario "<scenario>" from excel sheet
    When Admin sends GET request to retrieve active and inactive user count
    Then Admin receives 200 OK status with response body for user count

    Examples:
      | scenario                              |
      | Get_Active_Inactive_User_Count_All    |
      | Get_Active_Inactive_User_Count_R01    |
      | Get_Active_Inactive_User_Count_R02    |
      | Get_Active_Inactive_User_Count_R03    |


@GetUserCount_Negative
Scenario Outline: Get user count with invalid Role ID
    Given Admin creates GET request with invalid Role ID for "<scenario>" from Excel sheet
    When Admin sends GET request to retrieve active and inactive user count
    Then Admin receives 404 Not Found status with Role ID not found message

    Examples:
      | scenario                                      |
      | Get_Active_Inactive_User_Count_Invalid_RoleID |

  @GetUserCount_Negative
  Scenario Outline: Check if Admin is unable to retrieve user count with invalid endpoint 
   Given Admin creates GET request with invalid endpoint for user count scenario "<scenario>" from excel sheet
    When Admin sends GET request to retrieve active and inactive user count
    Then Admin receives expected status code for invalid endpoint for user count

    Examples:
      | scenario                                      |
      | Get_Active_Inactive_User_Count_Invalid_Endpoint |


  @GetUserCount_Negative
  Scenario Outline: Check if Admin is unable to retrieve user count with invalid method
    Given Admin create invalid request for "<scenario>" from excel sheet
    When Admin sends invalid method request to retrieve active and inactive user count
    Then Admin receives expected status code for invalid method

    Examples:
      | scenario                                    |
      | Get_Active_Inactive_User_Count_Invalid_Method |


  @GetUserCount_Negative
  Scenario Outline: Check if Admin is unable to retrieve user count without authorization
    Given Admin create GET request without authorization for "<scenario>" from excel sheet
    When Admin sends GET request to retrieve active and inactive user count
    Then Admin receives expected status code for user without authorization

    Examples:
      | scenario                                         |
      | Get_Active_Inactive_User_Count_Without_Authorization |