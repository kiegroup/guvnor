package org.drools.guvnor.server.security;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.security.Capabilities;

import junit.framework.TestCase;

public class CapabilityCalculatorTest extends TestCase {


	public void testAdmin() {
		CapabilityCalculator loader = new CapabilityCalculator();
		List<RoleBasedPermission> perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("s", RoleTypes.ADMIN, null, null  ));
		Capabilities caps = loader.calcCapabilities(perms);
		assertEquals(7, caps.list.size());
	}

	public void testCapabilitiesCalculate() {
		CapabilityCalculator loader = new CapabilityCalculator();
		List<RoleBasedPermission> perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_DEVELOPER, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.ANALYST, null, null));
		Capabilities caps = loader.calcCapabilities(perms);
		assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));

		perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_ADMIN, null, null));
		caps = loader.calcCapabilities(perms);
		assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));

		perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		caps = loader.calcCapabilities(perms);
		assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
		assertEquals(1, caps.list.size());

		perms = new ArrayList<RoleBasedPermission>();
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_READONLY, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.ANALYST_READ, null, null));
		perms.add(new RoleBasedPermission("", RoleTypes.PACKAGE_DEVELOPER, null, null));
		caps = loader.calcCapabilities(perms);
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
		caps = loader.calcCapabilities(perms);
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
		caps = loader.calcCapabilities(perms);
		assertEquals(7, caps.list.size());

	}

}
