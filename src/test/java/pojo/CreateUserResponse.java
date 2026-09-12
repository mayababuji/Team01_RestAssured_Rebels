package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse {
	
private String userId;
	private String userFirstName;
    private String userLastName;
    private String userMiddleName;
    private String userPhoneNumber;
    private String userLocation;
    private String userTimeZone;
    private String userLinkedinUrl;
    private String userEduUg;
    private String userEduPg;
    private String userComments;
    private String userVisaStatus;
    private   String userRoleMaps;
    private  String userLoginEmail ;

    // Default Constructor
    public CreateUserResponse() {}

    // Getters and Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }

    public String getUserLastName() { return userLastName; }
    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }

    public String getUserMiddleName() { return userMiddleName; }
    public void setUserMiddleName(String userMiddleName) { this.userMiddleName = userMiddleName; }

   public String getUserPhoneNumber() { return userPhoneNumber; }
   public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }

    public String getUserLocation() { return userLocation; }
    public void setUserLocation(String userLocation) { this.userLocation = userLocation; }

    public String getUserTimeZone() { return userTimeZone; }
    public void setUserTimeZone(String userTimeZone) { this.userTimeZone = userTimeZone; }

    public String getUserLinkedinUrl() { return userLinkedinUrl; }
    public void setUserLinkedinUrl(String userLinkedinUrl) { this.userLinkedinUrl = userLinkedinUrl; }

    public String getUserEduUg() { return userEduUg; }
    public void setUserEduUg(String userEduUg) { this.userEduUg = userEduUg; }

    public String getUserEduPg() { return userEduPg; }
    public void setUserEduPg(String userEduPg) { this.userEduPg = userEduPg; }

    public String getUserComments() { return userComments; }
    public void setUserComments(String userComments) { this.userComments = userComments; }

    public String getUserVisaStatus() { return userVisaStatus; }
    public void setUserVisaStatus(String userVisaStatus) { this.userVisaStatus = userVisaStatus; }

    public String getUserRoleMaps() { return userRoleMaps; }
    public void setUserRoleMaps(String userRoleMaps) { this.userRoleMaps = userRoleMaps; }

    public String getUserLoginEmail() { return userLoginEmail; }
   public void setUserLoginEmail(String userLogin) { this.userLoginEmail = userLogin; }
}