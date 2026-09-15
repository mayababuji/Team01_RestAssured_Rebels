package pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequestForRoleId {
	private String roleId;
	private String userRoleStatus;
	

    public UserRequestForRoleId() {}

    public UserRequestForRoleId(String roleId, String userRoleStatus) {
        this.roleId = roleId;
        this.userRoleStatus = userRoleStatus;
    }

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getUserRoleStatus() { return userRoleStatus; }
    public void setUserRoleStatus(String userRoleStatus) { this.userRoleStatus = userRoleStatus; }

}
