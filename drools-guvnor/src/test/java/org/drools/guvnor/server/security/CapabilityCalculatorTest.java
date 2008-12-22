package org.drools.guvnor.server.security;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import junit.framework.TestCase;

import org.drools.guvnor.client.security.Capabilities;

public class CapabilityCalculatorTest extends TestCase {


	public void testAdmin() {
		CapabilityCalculator loader = new CapabilityCalculator();
		List<RoleBasedPermission> perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("s", RoleTypes.ADMIN, null, null  ));

        HashMap hm = new HashMap();
		Capabilities caps = loader.calcCapabilities(perms, hm);
		assertEquals(7, caps.list.size());
        assertSame(hm, caps.prefs);
	}

	public void testCapabilitiesCalculate() {
        HashMap hm = new HashMap();
		CapabilityCalculator loader = new CapabilityCalculator();
		List<RoleBasedPermission> perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_DEVELOPER, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.ANALYST, null, null));
		Capabilities caps = loader.calcCapabilities(perms, hm);
		assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
        assertSame(hm, caps.prefs);

		perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_ADMIN, null, null));
		caps = loader.calcCapabilities(perms, hm);
		assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
        assertSame(hm, caps.prefs);

		perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		caps = loader.calcCapabilities(perms, hm);
		assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
		assertEquals(1, caps.list.size());
        assertSame(hm, caps.prefs);

		perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.ANALYST_READ, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_DEVELOPER, null, null));
		caps = loader.calcCapabilities(perms, hm);
        assertSame(hm, caps.prefs);
		assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
		assertTrue(caps.list.contains(Capabilities.SHOW_CREATE_NEW_ASSET));
		assertFalse(caps.list.contains(Capabilities.SHOW_CREATE_NEW_PACKAGE));
		assertTrue(caps.list.contains(Capabilities.SHOW_QA));
		assertEquals(3, caps.list.size());

		perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.ANALYST, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_ADMIN, null, null));
		caps = loader.calcCapabilities(perms, hm);
        assertSame(hm, caps.prefs);
		assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
		assertTrue(caps.list.contains(Capabilities.SHOW_CREATE_NEW_ASSET));
		assertTrue(caps.list.contains(Capabilities.SHOW_CREATE_NEW_PACKAGE));
		assertTrue(caps.list.contains(Capabilities.SHOW_DEPLOYMENT));
		assertTrue(caps.list.contains(Capabilities.SHOW_DEPLOYMENT_NEW));
		assertTrue(caps.list.contains(Capabilities.SHOW_QA));

		assertEquals(6, caps.list.size());

		perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.ADMIN, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_ADMIN, null, null));
		caps = loader.calcCapabilities(perms, hm);
        assertSame(hm, caps.prefs);
		assertEquals(7, caps.list.size());

	}

}
