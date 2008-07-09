package org.drools.guvnor.server.security;

import java.util.ArrayList;
import java.util.List;

public class PackageBasedPermissionStore {
	private static List<PackageBasedPermission> pbps = new ArrayList<PackageBasedPermission>();
	
	//Mock data	
	static {
		pbps.add(new PackageBasedPermission("631b3d79-5b67-42fb-83da-714624970a6b", "jervis", "package.admin"));
		pbps.add(new PackageBasedPermission("47982482-7912-4881-97ec-e852494383d7", "jervis", "package.guest"));		
		//pbps.add(new PackageBasedPermission(null, "jervis", "admin"));			
	}
	
	public PackageBasedPermissionStore() {
	}
	
	public List<PackageBasedPermission> getPackageBasedPermissions() {
		return null;
	}
	
	public List<PackageBasedPermission> getPackageBasedPermissionsByUserName(String userName) {
		return pbps;
	}	
	
	public List<PackageBasedPermission> getPackageBasedPermissionsByPackage(String packageName) {
		return null;
	}
	
	public void addPackageBasedPermission(PackageBasedPermission pbp) {
		pbps.add(pbp);		
	}


}
