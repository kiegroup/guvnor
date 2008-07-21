package org.drools.guvnor.server.security;

public class RoleBasedPermission {
	private String packageUUID;
	private String categoryPath;
	private String userName;
	private String role;
	
	public RoleBasedPermission(String userName, String role, String packageUUID, String categoryPath) {
		this.packageUUID = packageUUID;
		this.categoryPath = categoryPath;		
		this.userName = userName;
		this.role = role;		
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPackageUUID() {
		return packageUUID;
	}

	public void setPackageUUID(String packageUUID) {
		this.packageUUID = packageUUID;
	}

	public String getCategoryPath() {
		return categoryPath;
	}

	public void setCategoryPath(String categoryPath) {
		this.categoryPath = categoryPath;
	}
	

}
