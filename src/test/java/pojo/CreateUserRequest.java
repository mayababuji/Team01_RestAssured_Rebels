package pojo;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
public class CreateUserRequest {

    private String userComments;
    private String userEduPg;
    private String userEduUg;
    private String userFirstName;
    private String userLastName;
    private String userMiddleName;
    private String userPhoneNumber;
    private String userLinkedinUrl;
    private String userLocation;
    private UserLogin userLogin;
    private List<UserRoleMap> userRoleMaps;
    private String userTimeZone;
    private String userVisaStatus;

    public CreateUserRequest() {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    // Inner class for userLogin
    public static class UserLogin {
        private String loginStatus;
        private String userLoginEmail;

        public UserLogin() {}

        public UserLogin(String loginStatus, String userLoginEmail) {
            this.loginStatus = loginStatus;
            this.userLoginEmail = userLoginEmail;
        }

        public String getLoginStatus() { return loginStatus; }
        public void setLoginStatus(String loginStatus) { this.loginStatus = loginStatus; }

        public String getUserLoginEmail() { return userLoginEmail; }
        public void setUserLoginEmail(String userLoginEmail) { this.userLoginEmail = userLoginEmail; }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    // Inner class for userRoleMaps
    public static class UserRoleMap {
        private String roleId;
        private String userRoleStatus;

        public UserRoleMap() {}

        public UserRoleMap(String roleId, String userRoleStatus) {
            this.roleId = roleId;
            this.userRoleStatus = userRoleStatus;
        }

        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }

        public String getUserRoleStatus() { return userRoleStatus; }
        public void setUserRoleStatus(String userRoleStatus) { this.userRoleStatus = userRoleStatus; }
    }

    // Getters and Setters
    public String getUserComments() { return userComments; }
    public void setUserComments(String userComments) { this.userComments = userComments; }

    public String getUserEduPg() { return userEduPg; }
    public void setUserEduPg(String userEduPg) { this.userEduPg = userEduPg; }

    public String getUserEduUg() { return userEduUg; }
    public void setUserEduUg(String userEduUg) { this.userEduUg = userEduUg; }

    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }

    public String getUserLastName() { return userLastName; }
    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }

    public String getUserMiddleName() { return userMiddleName; }
    public void setUserMiddleName(String userMiddleName) { this.userMiddleName = userMiddleName; }

    public String getUserPhoneNumber() { return userPhoneNumber; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }

    public String getUserLinkedinUrl() { return userLinkedinUrl; }
    public void setUserLinkedinUrl(String userLinkedinUrl) { this.userLinkedinUrl = userLinkedinUrl; }

    public String getUserLocation() { return userLocation; }
    public void setUserLocation(String userLocation) { this.userLocation = userLocation; }

    public UserLogin getUserLogin() { return userLogin; }
    public void setUserLogin(UserLogin userLogin) { this.userLogin = userLogin; }

    public List<UserRoleMap> getUserRoleMaps() { return userRoleMaps; }
    public void setUserRoleMaps(List<UserRoleMap> userRoleMaps) { this.userRoleMaps = userRoleMaps; }

    public String getUserTimeZone() { return userTimeZone; }
    public void setUserTimeZone(String userTimeZone) { this.userTimeZone = userTimeZone; }

    public String getUserVisaStatus() { return userVisaStatus; }
    public void setUserVisaStatus(String userVisaStatus) { this.userVisaStatus = userVisaStatus; }
}