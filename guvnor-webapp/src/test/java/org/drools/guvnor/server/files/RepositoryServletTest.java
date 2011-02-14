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

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.util.codec.Base64;
import org.drools.guvnor.server.security.MockIdentity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class RepositoryServletTest extends GuvnorTestBase {

    @Before
    public void setUp() throws Exception {
        setUpSeam();
    }

    @After
    public void tearDown() throws Exception {
        tearAllDown();
    }

    @Test
    public void testAllowUser() throws Exception {
        setUpMockIdentity(getMockIdentity());

        String authToken = "usr:pwd";
        String encodedAuthToken = "BASIC " + new String(Base64.encodeBase64(authToken.getBytes()));
        boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
        assertTrue(allowed);
    }

    @Test
    public void testAllowUserNoBasicAuthenticationHeader() throws Exception {
        setUpMockIdentity(getMockIdentity());

        String encodedAuthToken = null;
        boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
        assertTrue(allowed);
    }

    @Test
    public void testAllowUserNoBasicAuthenticationHeaderNotAllowLogin() throws Exception {
        MockIdentity mockIdentity = new MockIdentity();
        mockIdentity.setIsLoggedIn(false);
        mockIdentity.setAllowLogin(false);
        setUpMockIdentity(mockIdentity);

        String encodedAuthToken = null;
        boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
        assertFalse(allowed);
    }

    @Test
    public void testAllowUserNotBasicAuthenticationHeader() throws Exception {
        setUpMockIdentity(getMockIdentity());

        String encodedAuthToken = "NON-Basic ";
        boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
        assertTrue(allowed);
    }


    @Test
    public void testUnpack() {
        String b42 = "BASIC " + new String(Base64.encodeBase64("user:pass".getBytes()));
        String[] d = RepositoryServlet.unpack(b42);
        assertEquals("user", d[0]);
        assertEquals("pass", d[1]);
    }

    private MockIdentity getMockIdentity() {
        MockIdentity mockIdentity = new MockIdentity();
        mockIdentity.setIsLoggedIn(false);
        mockIdentity.setAllowLogin(true);
        return mockIdentity;
    }

}
