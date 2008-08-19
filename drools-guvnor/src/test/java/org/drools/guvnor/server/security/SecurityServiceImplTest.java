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

import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.server.security.SecurityServiceImpl;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.permission.RoleBasedPermissionResolver;
import org.jboss.security.identity.RoleType;

import junit.framework.TestCase;

public class SecurityServiceImplTest extends TestCase {

    public void testLogin() throws Exception {
        SecurityServiceImpl impl = new SecurityServiceImpl();
        assertTrue(impl.login( "XXX", null ));
    }

    public void testUser() throws Exception {
        SecurityServiceImpl impl = new SecurityServiceImpl();
        assertNotNull(impl.getCurrentUser());
    }

    public void testCapabilities() {
    	SecurityServiceImpl impl = new SecurityServiceImpl();

    	Capabilities c = impl.getUserCapabilities();
    	assertTrue(c.list.size() > 1);
    }

    public void testCapabilitiesWithContext() {
    	SecurityServiceImpl impl = new SecurityServiceImpl();

		// Mock up SEAM contexts
		Map application = new HashMap<String, Object>();
		Lifecycle.beginApplication(application);
		Lifecycle.beginCall();
		MockIdentity midentity = new MockIdentity();
		RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
		resolver.setEnableRoleBasedAuthorization(true);
		midentity.addPermissionResolver(resolver);

		Contexts.getSessionContext().set("org.jboss.seam.security.identity",
				midentity);
		Contexts.getSessionContext().set(
				"org.drools.guvnor.client.rpc.RepositoryService", impl);

		List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis", RoleTypes.PACKAGE_READONLY, "packagename",
				null));
    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

		// Put permission list in session.
		RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
		testManager.create();
		Contexts.getSessionContext().set("roleBasedPermissionManager",
				testManager);

		Capabilities c = impl.getUserCapabilities();
		assertTrue(c.list.size() == 1);

    	Lifecycle.endApplication();
    }

    public void testCapabilitiesContext() throws Exception {
    	SecurityServiceImpl impl = new SecurityServiceImpl();

		// Mock up SEAM contexts
		Map application = new HashMap<String, Object>();
		Lifecycle.beginApplication(application);
		Lifecycle.beginCall();
		MockIdentity midentity = new MockIdentity();
		midentity.addRole(RoleTypes.ADMIN);
		Contexts.getSessionContext().set("org.jboss.seam.security.identity",
				midentity);

    	Capabilities c = impl.getUserCapabilities();
    	assertTrue(c.list.size() > 1);

    	Lifecycle.endApplication();




    }

}
