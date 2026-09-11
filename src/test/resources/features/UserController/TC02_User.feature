Feature: User Module

  Background:
    Given Admin sets Bearer token

  @createuser
  Scenario Outline: Check if admin is able to create user with valid/invalid details
    Given Admin creates POST Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request and request Body for user
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                       |
      | Create_Valid_User_1                |
      | Create_Valid_User_2                |
      | Create_Valid_User_3                |
      | Create_User_Only_Mandatory_Field   |
      | Create_User_Empty_Payload          |
      | Create_User_Empty_Frist_Name_Field |
      | Create_User_Empty_Last_Name_Field  |
      | Create_User_Empty_Location         |
      | Create_User_Empty_VisaStatus       |
      | Create_User_Empty_RoleId           |
      | Create_User_Empty_RoleStatus       |
    #  | Create_User_Empty_LoginStatus      |
      | Create_User_Empty_Email            |
      | Create_User_Empty_PhoneNumber      |

  @GetAllActiveUsers @GetAllActiveUsers_Positive
  Scenario Outline: Admin retrieves all active users with valid Endpoint
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All Active Users
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                 |
      | Get_All_Active_Users_Success |

  @GetAllActiveUsers @GetAllActiveUsers_Negative
  Scenario Outline: Check if admin receives proper error code when retrieving active users with invalid request parameters
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All Active Users
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                          |
      | Get_All_Active_Users_Invalid_Endpoint |
     | Get_All_Active_Users_Invalid_Method   |
     | Get_All_Active_Users_No_Auth          |