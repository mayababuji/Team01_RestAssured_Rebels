Feature: LMS  User Module

  Background:
    Given Admin sets authorization to Bearer Token

  @createuser
  Scenario Outline: Check if admin is able to create user with valid/invalid details
    Given Admin creates POST Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request and request Body for user
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                            |
      | Create_Valid_User_1                     |
      | Create_Valid_User_2                     |
      | Create_Valid_User_3                     |
      | Create_User_Only_Mandatory_Field        |
      | Create_User_Empty_Payload               |
      | Create_User_Empty_Frist_Name_Field      |
      | Create_User_Empty_Last_Name_Field       |
      | Create_User_Empty_Location              |
      | Create_User_Empty_VisaStatus            |
      | Create_User_Empty_RoleId                |
      | Create_User_Empty_RoleStatus            |
       | Create_User_Empty_LoginStatus         |
      | Create_User_Empty_Email                 |
      | Create_User_Empty_PhoneNumber           |
      | Create_User_Duplicate_Email             |
      | Create_User_Duplicate_Phone_Number      |
      | Create_User_Invalid_UserEduPg           |
      | Create_User_Invalid_UserEduUg           |
      | Create_User_Invalid_First_Name          |
      | Create_User_Invalid_Last_Name           |
      | Create_User_Invalid_Middle_Name         |
      | Create_User_Invalid_UserLinkedinUrl     |
      | Create_User_Invalid_UserLocation        |
      | Create_User_Invalid_Phone_Number_Format |
      | Create_User_Invalid_User_Role_Id        |
      | Create_User_Invalid_User_Role_Status    |
      | Create_User_Invalid_User_Time_Zone      |
      | Create_User_Invalid_User_Visa_Status    |
      | Create_User_Invalid_Token               |
      | Create_User_Invalid_Endpoint            |

      | Create_User_Invalid_Content_Type        |
     # | Create_User_Invalid_Method            |
      | Create_User_No_Auth                     |

  @GetAllActiveUsers_Positive

  Scenario Outline: Admin retrieves all active users with valid Endpoint
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All Active Users
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                 |
      | Get_All_Active_Users_Success |

  @GetAllActiveUsers_Negative
  Scenario Outline: Check if admin receives proper error code when retrieving active users with invalid request parameters
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All Active Users
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                          |
      | Get_All_Active_Users_Invalid_Endpoint |
      | Get_All_Active_Users_Invalid_Method   |
      | Get_All_Active_Users_No_Auth          |


  @GetActiveUserEmails_Positive


  Scenario Outline: Admin retrieves all active user emails with valid Endpoint
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All Active Users
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                          |
      | Get_Active_User_Emails_Valid_Endpoint |


  @GetActiveUserEmails_Negative

  Scenario Outline: Check if admin receives proper error code when retrieving active user emails with invalid request parameters
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All Active Users
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                            |
      | Get_Active_User_Emails_Invalid_Endpoint |
      | Get_Active_User_Emails_Invalid_Method   |
      | Get_Active_User_Emails_No_Auth          |
  @GetAllRoles_Positive
  Scenario Outline: Admin retrieves all user roles with valid Endpoint
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All User Roles
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                    |
      | Get_All_Roles_Valid_Endpoint    |

  @GetAllRoles_Negative
  Scenario Outline: Check if admin receives proper error code when retrieving user roles with invalid request parameters
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All User Roles
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                    |
      | Get_All_Roles_Invalid_Endpoint  |
      | Get_All_Roles_Invalid_Method    |
      | Get_All_Roles_No_Auth           |

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