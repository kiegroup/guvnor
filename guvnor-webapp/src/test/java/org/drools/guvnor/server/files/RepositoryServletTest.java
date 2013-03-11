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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.util.codec.Base64;
import org.junit.Test;


public class RepositoryServletTest extends GuvnorTestBase {

    @Test
        public void testExecutellowUser() throws Exception {
            setUpMockIdentity(getMockIdentity());
    
            String authToken = "usr:pwd";
            String encodedAuthToken = "BASIC " + new String(Base64.encodeBase64(authToken.getBytes()));
            boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
            assertTrue(allowed);
        }

    @Test
        public void testExecutellowUserNoBasicAuthenticationHeader() throws Exception {
            setUpMockIdentity(getMockIdentity());
    
            String encodedAuthToken = null;
            boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
            assertTrue(allowed);
        }

    @Test
        public void testExecutellowUserNoBasicAuthenticationHeaderNotAllowLogin() throws Exception {
            MockIdentity mockIdentity = new MockIdentity();
            mockIdentity.setIsLoggedIn(false);
            mockIdentity.setAllowLogin(false);
            setUpMockIdentity(mockIdentity);
    
            String encodedAuthToken = null;
            boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
            assertFalse(allowed);
        }

    @Test
        public void testExecutellowUserNotBasicAuthenticationHeader() throws Exception {
            setUpMockIdentity(getMockIdentity());
    
            String encodedAuthToken = "NON-Basic ";
            boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
            assertTrue(allowed);
        }


    @Test
    public void testUnpack() {
        String b42 = "BASIC " + new String(Base64.encodeBase64("user:t:e&st".getBytes()));
        String[] d = RepositoryServlet.unpack(b42);
        assertEquals("user", d[0]);
        assertEquals("t:e&st", d[1]);
    }

    private MockIdentity getMockIdentity() {
        MockIdentity mockIdentity = new MockIdentity();
        mockIdentity.setIsLoggedIn(false);
        mockIdentity.setAllowLogin(true);
        return mockIdentity;
    }

}
