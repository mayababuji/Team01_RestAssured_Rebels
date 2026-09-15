package pojo;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse {

	private String userId;
	private String userLoginEmail;
	private String roleId;
	private String roleName;

	private CreateUserResponse user;
	private List<UserRequestForRoleId> roles;

	public CreateUserResponse() {
	}

	public String getSafeUserId() {
		if (this.userId != null) {
			return this.userId;
		}
		if (this.user != null && this.user.getUserId() != null) {
			return this.user.getUserId();
		}
		return null;
	}

	public String getSafeRoleId() {
		if (this.roleId != null) {
			return this.roleId;
		}
		if (this.user != null && this.user.getRoleId() != null) {
			return this.user.getRoleId();
		}
		if (this.roles != null && !this.roles.isEmpty() && this.roles.get(0).getRoleId() != null) {
			return this.roles.get(0).getRoleId();
		}
		return null;
	}

	// Getters and Setters
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserLoginEmail() {
		return userLoginEmail;
	}

	public void setUserLoginEmail(String userLoginEmail) {
		this.userLoginEmail = userLoginEmail;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public CreateUserResponse getUser() {
		return user;
	}

	public void setUser(CreateUserResponse user) {
		this.user = user;
	}

	public List<UserRequestForRoleId> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRequestForRoleId> roles) {
		this.roles = roles;
	}
}