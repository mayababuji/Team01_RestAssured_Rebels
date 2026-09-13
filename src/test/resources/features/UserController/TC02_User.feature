Feature: User Module

  Background:
    Given Admin sets Bearer token

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
      # | Create_User_Empty_LoginStatus         |
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

  @GetActiveUserEmails @GetActiveUserEmails_Positive
  Scenario Outline: Admin retrieves all active user emails with valid Endpoint
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All Active Users
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                          |
      | Get_Active_User_Emails_Valid_Endpoint |

  @GetActiveUserEmails @GetActiveUserEmails_Negative
  Scenario Outline: Check if admin receives proper error code when retrieving active user emails with invalid request parameters
    Given Admin creates GET Request for the LMS API endpoint with data from Excel "<ScenarioName>"
    When Admin sends HTTPS Request for Get All Active Users
    Then Admin receives StatusCode and response body for "<ScenarioName>"

    Examples:
      | ScenarioName                            |
      | Get_Active_User_Emails_Invalid_Endpoint |
      | Get_Active_User_Emails_Invalid_Method   |
      | Get_Active_User_Emails_No_Auth          |
