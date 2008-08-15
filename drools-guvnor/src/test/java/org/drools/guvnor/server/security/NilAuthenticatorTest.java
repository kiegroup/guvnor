package org.drools.guvnor.server.security;

import java.util.HashMap;

import org.jboss.seam.contexts.Lifecycle;

import junit.framework.TestCase;

public class NilAuthenticatorTest extends TestCase {

	public void testAdmin() {
		NilAuthenticator ni = new NilAuthenticator();

		assertTrue(ni.authenticate());
	}

}
