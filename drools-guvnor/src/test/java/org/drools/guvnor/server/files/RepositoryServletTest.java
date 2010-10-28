/**
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

import java.util.HashMap;
import java.util.Map;

import org.apache.util.Base64;
import org.drools.guvnor.server.security.MockIdentity;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

import junit.framework.TestCase;

public class RepositoryServletTest extends TestCase {

	public void testAllowUser() throws Exception {
        //Mock up SEAM contexts
        Map application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn(false);
        midentity.setAllowLogin(true);
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );

		String authToken = "usr:pwd";
		String encodedAuthToken = "BASIC " + new String(Base64.encode(authToken.getBytes()));
		boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
		assertTrue(allowed);
		
        Lifecycle.endApplication();
	}
	

	public void testAllowUserNoBasicAuthticationHeader() throws Exception {
        //Mock up SEAM contexts
        Map application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn(false);
        midentity.setAllowLogin(true);
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );

		String encodedAuthToken = null;
		boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
		assertTrue(allowed);
		
        Lifecycle.endApplication();
	}
	
	public void testAllowUserNoBasicAuthticationHeaderNotAllowLogin() throws Exception {
        //Mock up SEAM contexts
        Map application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn(false);
        midentity.setAllowLogin(false);
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );

		String encodedAuthToken = null;
		boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
		assertFalse(allowed);
		
        Lifecycle.endApplication();
	}
	
	public void testAllowUserNotBasicAuthticationHeader() throws Exception {
        //Mock up SEAM contexts
        Map application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        midentity.setIsLoggedIn(false);
        midentity.setAllowLogin(true);
        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );

		String encodedAuthToken = "NON-Basic ";
		boolean allowed = RepositoryServlet.allowUser(encodedAuthToken);
		assertTrue(allowed);
		
        Lifecycle.endApplication();
	}

	public void testUnpack() {
		String b42 = "BASIC " + new String( Base64.encode("user:pass".getBytes()) );
		String[] d = RepositoryServlet.unpack(b42);
		assertEquals("user", d[0]);
		assertEquals("pass", d[1]);
	}
}
