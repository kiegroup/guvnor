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

import javax.inject.Inject;

import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.server.GuvnorTestBase;
import org.jboss.seam.solder.beanManager.BeanManagerLocator;
import org.jboss.seam.security.AuthorizationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picketlink.idm.impl.api.PasswordCredential;

public class SecurityServiceImplTest extends GuvnorTestBase {

    private static final String USER_NAME = "securityServiceImplUser";

    @Inject
    private SecurityServiceImpl securityService;

    @Inject
    private RoleBasedPermissionStore roleBasedPermissionStore;

    @Inject
    private RoleBasedPermissionManager roleBasedPermissionManager;

    @Inject
    private RoleBasedPermissionResolver roleBasedPermissionResolver;

    public SecurityServiceImplTest() {
        autoLoginAsAdmin = false;
    }

    @Before
    public void loginAsSpecificUser() {
        credentials.setUsername(USER_NAME);
        credentials.setCredential(new PasswordCredential(USER_NAME));
        identity.login();
    }

    @After
    public void logoutAsSpecificUser() {
        identity.logout();
        credentials.clear();
    }

    @Test
    public void testLogin() throws Exception {
        logoutAsSpecificUser();
        try {
            assertTrue( securityService.login( USER_NAME,
                                    USER_NAME ) );
        } finally {
            loginAsSpecificUser();
        }
    }

    @Test
    public void testUser() throws Exception {
        assertNotNull( securityService.getCurrentUser() );
    }

    @Test
    public void testCapabilities() {
        List<Capability> userCapabilities = securityService.getUserCapabilities();

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
        roleBasedPermissionResolver.setEnableRoleBasedAuthorization(true);
        roleBasedPermissionStore.addRoleBasedPermissionForTesting(USER_NAME, new RoleBasedPermission( USER_NAME,
                                           RoleType.PACKAGE_READONLY.getName(),
                                           "packagename",
                                           null ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {
            List<Capability> c = securityService.getUserCapabilities();
            assertTrue(c.contains(Capability.SHOW_KNOWLEDGE_BASES_VIEW));

            //now lets give them no permissions
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting(USER_NAME);
            roleBasedPermissionManager.create(); // HACK flushes the permission cache
            try {
                securityService.getUserCapabilities();
                fail( "should not be allowed as there are no permissions" );
            } catch ( AuthorizationException e ) {
                assertNotNull( e.getMessage() );
            }
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization(false);
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting(USER_NAME);
        }
        securityService.getUserCapabilities(); // should not blow up !
    }

}
