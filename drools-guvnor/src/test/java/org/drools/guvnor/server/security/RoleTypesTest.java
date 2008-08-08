package org.drools.guvnor.server.security;

import junit.framework.TestCase;

public class RoleTypesTest extends TestCase {

	public void testListAllTypes() {
		String[] t = RoleTypes.listAvailableTypes();
		assertEquals(5, t.length);
		assertEquals("admin", t[0]);
	}

}
