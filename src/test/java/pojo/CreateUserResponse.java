package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse {

    private User user;
    private List<Role> roles;

    public CreateUserResponse() {}

    // Inner Class for User object
    public static class User {
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
        private String userLoginEmail;

        public User() {}

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

        public String getUserLoginEmail() { return userLoginEmail; }
        public void setUserLoginEmail(String userLoginEmail) { this.userLoginEmail = userLoginEmail; }
    }

    // Inner Class for Role object
    public static class Role {
        private String roleId;
        private String userRoleStatus;

        public Role() {}

        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }

        public String getUserRoleStatus() { return userRoleStatus; }
        public void setUserRoleStatus(String userRoleStatus) { this.userRoleStatus = userRoleStatus; }
    }

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
}