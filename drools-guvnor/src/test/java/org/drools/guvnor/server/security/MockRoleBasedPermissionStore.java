package org.drools.guvnor.server.security;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

@Name("org.drools.guvnor.server.security.RoleBasedPermissionStore")
@AutoCreate
public class MockRoleBasedPermissionStore extends RoleBasedPermissionStore {

	List<RoleBasedPermission> pbps;

	public MockRoleBasedPermissionStore(List<RoleBasedPermission> pbps) {
		this.pbps = pbps;
	}

	public List<RoleBasedPermission> getRoleBasedPermissionsByUserName(
			String userName) {
		return pbps;
	}

}
