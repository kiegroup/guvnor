package org.drools.guvnor.server.security;

import java.util.ArrayList;
import java.util.List;

public class RoleBasedPermissionStore {
	private static List<RoleBasedPermission> rbps = new ArrayList<RoleBasedPermission>();
	
	//Mock data	
	static {
		rbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_ADMIN, "631b3d79-5b67-42fb-83da-714624970a6b", null));
		rbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "47982482-7912-4881-97ec-e852494383d7", null));		
		rbps.add(new RoleBasedPermission("jervis", RoleTypes.ANALYST, null, "category1"));		
		rbps.add(new RoleBasedPermission("jervis", RoleTypes.ANALYST, null, "category2"));		
	}
	
	public RoleBasedPermissionStore() {
	}
	
	public List<RoleBasedPermission> getRoleBasedPermissions() {
		return null;
	}
	
	public List<RoleBasedPermission> getRoleBasedPermissionsByUserName(String userName) {
		return rbps;
	}	
	
	public List<RoleBasedPermission> getRoleBasedPermissionsByPackage(String packageName) {
		return null;
	}
	
	public void addRoleBasedPermission(RoleBasedPermission rbp) {
		rbps.add(rbp);		
	}


}
