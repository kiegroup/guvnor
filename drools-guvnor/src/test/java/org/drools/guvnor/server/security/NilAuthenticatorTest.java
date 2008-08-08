package org.drools.guvnor.server.security;

import java.util.HashMap;

import org.jboss.seam.contexts.Lifecycle;

import junit.framework.TestCase;

public class NilAuthenticatorTest extends TestCase {

	public void testAdmin() {
		Lifecycle.beginApplication(new HashMap<String, Object>());
		Lifecycle.beginCall();

		MockIdentity mi = new MockIdentity();
		mi.inject();

		NilAuthenticator ni = new NilAuthenticator();
		assertFalse(mi.hasRole("admin"));

		ni.authenticate();
		assertTrue(mi.hasRole("admin"));

		Lifecycle.endApplication();
	}

}
