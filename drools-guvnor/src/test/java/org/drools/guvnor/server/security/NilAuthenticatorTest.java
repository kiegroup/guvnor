package org.drools.guvnor.server.security;

import junit.framework.TestCase;

public class NilAuthenticatorTest extends TestCase {

	public void testAdmin() {
		NilAuthenticator ni = new NilAuthenticator();

		assertTrue(ni.authenticate());
	}

}
