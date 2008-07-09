package org.drools.guvnor.server.security;

public class PackageBasedPermission {
	private String packageUUID;
	private String userName;
	private String role;
	
	public PackageBasedPermission(String packageUUID, String userName, String role) {
		this.packageUUID = packageUUID;
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
	

}
