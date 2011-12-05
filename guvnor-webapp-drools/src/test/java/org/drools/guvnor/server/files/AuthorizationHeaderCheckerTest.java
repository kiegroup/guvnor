/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.files;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.util.codec.Base64;
import org.junit.Ignore;
import org.junit.Test;

public class AuthorizationHeaderCheckerTest extends GuvnorTestBase {

    @Inject
    private AuthorizationHeaderChecker authorizationHeaderChecker;

    public AuthorizationHeaderCheckerTest() {
        autoLoginAsAdmin = false;
    }

    @Test
    public void testExecuteAllowUser() throws Exception {
        assertFalse(identity.isLoggedIn());

        String authToken = "admin:admin";
        String encodedAuthToken = "BASIC " + new String(Base64.encodeBase64(authToken.getBytes()));
        boolean allowed = authorizationHeaderChecker.loginByHeader(encodedAuthToken);
        assertTrue(allowed);
        assertTrue(identity.isLoggedIn());
        
        identity.logout();
        credentials.clear();
    }

    @Test
    public void testExecuteAllowUserNoBasicAuthenticationHeader() throws Exception {
        assertFalse(identity.isLoggedIn());

        boolean allowed = authorizationHeaderChecker.loginByHeader(null);
        assertFalse(allowed);
        assertFalse(identity.isLoggedIn());

    }

    @Test @Ignore
    // TODO this test doesn't really seem to make any sense? If NON-Basic no authentication is needed?!?
    public void testExecuteAllowUserNotBasicAuthenticationHeader() throws Exception {
        assertFalse(identity.isLoggedIn());

        String encodedAuthToken = "NON-Basic ";
        boolean allowed = authorizationHeaderChecker.loginByHeader(encodedAuthToken);
        assertTrue(allowed);
        assertTrue(identity.isLoggedIn());
    }

    @Test
    public void testUnpack() {
        String b42 = "BASIC " + new String(Base64.encodeBase64("user:pass".getBytes()));
        String[] d = authorizationHeaderChecker.unpack(b42);
        assertEquals("user", d[0]);
        assertEquals("pass", d[1]);
    }

}
