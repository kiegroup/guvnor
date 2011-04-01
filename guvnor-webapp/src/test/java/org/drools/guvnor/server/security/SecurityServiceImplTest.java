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

package org.drools.guvnor.server.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.configurations.Capability;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.AuthorizationException;
import org.junit.Test;

public class SecurityServiceImplTest {

    @Test
    public void testLogin() throws Exception {
        SecurityServiceImpl impl = new SecurityServiceImpl();
        assertTrue( impl.login( "XXX",
                                null ) );
    }

    @Test
    public void testUser() throws Exception {
        SecurityServiceImpl impl = new SecurityServiceImpl();
        assertNotNull( impl.getCurrentUser() );
    }

    @Test
    public void testCapabilities() {
        SecurityServiceImpl impl = new SecurityServiceImpl();

        List<Capability> userCapabilities = impl.getUserCapabilities();

        assertTrue(userCapabilities.contains(Capability.SHOW_ADMIN));
        assertTrue(userCapabilities.contains(Capability.SHOW_CREATE_NEW_ASSET));
        assertTrue(userCapabilities.contains(Capability.SHOW_CREATE_NEW_PACKAGE));
        assertTrue(userCapabilities.contains(Capability.SHOW_DEPLOYMENT));
        assertTrue(userCapabilities.contains(Capability.SHOW_DEPLOYMENT_NEW));
        assertTrue(userCapabilities.contains(Capability.SHOW_KNOWLEDGE_BASES_VIEW));
        assertTrue(userCapabilities.contains(Capability.SHOW_QA));
    }

    @Test
    public void testCapabilitiesWithContext() {
        SecurityServiceImpl impl = new SecurityServiceImpl();

        // Mock up SEAM contexts
        Map<String, Object> application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );
        midentity.addPermissionResolver( resolver );

        Contexts.getSessionContext().set( "org.jboss.seam.security.roleBasedPermissionResolver",
                                          resolver );
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );
        Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                          impl );

        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_READONLY.getName(),
                                           "packagename",
                                           null ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        List<Capability> c = impl.getUserCapabilities();
        assertTrue(c.contains(Capability.SHOW_KNOWLEDGE_BASES_VIEW));

        //now lets give them no permissions
        pbps.clear();
        try {
            impl.getUserCapabilities();
            fail( "should not be allowed as there are no permissions" );
        } catch ( AuthorizationException e ) {
            assertNotNull( e.getMessage() );
            assertTrue( midentity.loggoutCalled );
        }

        //now lets turn off the role based stuff
        resolver.setEnableRoleBasedAuthorization( false );
        impl.getUserCapabilities(); // should not blow up !

        Lifecycle.endApplication();
    }

    @Test
    public void testCapabilitiesContext() throws Exception {
        SecurityServiceImpl impl = new SecurityServiceImpl();

        // Mock up SEAM contexts
        Map<String, Object> application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        midentity.addRole( RoleType.ADMIN.getName() );
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );

        List<Capability> userCapabilities = impl.getUserCapabilities();

        assertTrue(userCapabilities.contains(Capability.SHOW_ADMIN));
        assertTrue(userCapabilities.contains(Capability.SHOW_CREATE_NEW_ASSET));
        assertTrue(userCapabilities.contains(Capability.SHOW_CREATE_NEW_PACKAGE));
        assertTrue(userCapabilities.contains(Capability.SHOW_DEPLOYMENT));
        assertTrue(userCapabilities.contains(Capability.SHOW_DEPLOYMENT_NEW));
        assertTrue(userCapabilities.contains(Capability.SHOW_KNOWLEDGE_BASES_VIEW));
        assertTrue(userCapabilities.contains(Capability.SHOW_QA));

        Lifecycle.endApplication();

    }

}
