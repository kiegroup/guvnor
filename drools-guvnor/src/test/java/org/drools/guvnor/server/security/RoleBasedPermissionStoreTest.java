package org.drools.guvnor.server.security;
/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.Identity;

import junit.framework.TestCase;

public class RoleBasedPermissionStoreTest extends TestCase {
	public void testGetRoleBasedPermissionsByUserName() throws Exception {
		RoleBasedPermissionStore store = getStore();
	
		store.addRoleBasedPermission("jervis", new RoleBasedPermission("jervis", RoleTypes.PACKAGE_ADMIN, "package1Name", null));
		store.addRoleBasedPermission("jervis", new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "package2Name", null));
		store.addRoleBasedPermission("jervis", new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "package3Name", null));
		store.addRoleBasedPermission("jervis", new RoleBasedPermission("jervis", RoleTypes.ANALYST, null, "category1"));
		store.addRoleBasedPermission("john", new RoleBasedPermission("jervis", RoleTypes.ANALYST, null, "category2"));
		List<RoleBasedPermission> perms = store.getRoleBasedPermissionsByUserName("jervis");
		assertTrue(perms.size() == 4);
		
		List<RoleBasedPermission> expectedPerms = new ArrayList<RoleBasedPermission>();
		expectedPerms.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_ADMIN, "package1Name", null));
		expectedPerms.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "package2Name", null));
		expectedPerms.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "package3Name", null));
		expectedPerms.add(new RoleBasedPermission("jervis", RoleTypes.ANALYST, null, "category1"));
		for(RoleBasedPermission perm : perms) {
			for(RoleBasedPermission expectedPerm : expectedPerms) {
				if(perm.getPackageName() != null && perm.getPackageName().equals(expectedPerm.getPackageName()) && 
						perm.getRole().equals(expectedPerm.getRole()))	 {
					expectedPerms.remove(expectedPerm);
					break;
				} else if(perm.getCategoryPath() != null && perm.getCategoryPath().equals(expectedPerm.getCategoryPath())&& 
						perm.getRole().equals(expectedPerm.getRole()))	 {
					expectedPerms.remove(expectedPerm);
					break;
				}
			}
		}
		assertTrue(expectedPerms.size() == 0);
		
		perms = store.getRoleBasedPermissionsByUserName("john");
		assertTrue(perms.size() == 1);
	}

	private RoleBasedPermissionStore getStore() throws Exception {
		RoleBasedPermissionStore store = new RoleBasedPermissionStore();

		store.repository = new RulesRepository(TestEnvironmentSessionHelper
				.getSession());
		return store;
	}

}
