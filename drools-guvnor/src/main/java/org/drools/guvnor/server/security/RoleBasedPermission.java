package org.drools.guvnor.server.security;

import java.io.Serializable;

public class RoleBasedPermission implements Serializable {
	private String packageName;
	private String categoryPath;
	private String userName;
	private String role;
	
	public RoleBasedPermission(String userName, String role, String packageName, String categoryPath) {
		this.packageName = packageName;
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

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getCategoryPath() {
		return categoryPath;
	}

	public void setCategoryPath(String categoryPath) {
		this.categoryPath = categoryPath;
	}
	

}
