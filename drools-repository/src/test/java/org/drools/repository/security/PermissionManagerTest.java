package org.drools.repository.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.repository.RepositorySessionUtil;

import junit.framework.TestCase;

public class PermissionManagerTest extends TestCase {

	public void testLoadSave() throws Exception {
		PermissionManager pm = new PermissionManager(RepositorySessionUtil.getRepository());
		Map<String, List<String>> perms = new HashMap<String, List<String>>() {{
			put("package.admin", new ArrayList<String>() {{add("1234567890");}});
			put("package.developer", new ArrayList<String>() {{add("1"); add("2");}});
			put("analyst", new ArrayList<String>() {{add("HR");}});
			put("admin", new ArrayList<String>());
		}};
		pm.updateUserPermissions("wankle", perms);

		Map<String, List<String>> perms_ = pm.retrieveUserPermissions("wankle");
		assertEquals(perms.size(), perms_.size());

		perms_ = pm.retrieveUserPermissions("wankle");
		assertEquals(perms.size(), perms_.size());

		List<String> padmin = perms_.get("package.admin");
		assertEquals(1, padmin.size());
		assertEquals("1234567890", padmin.get(0));

		List<String> pdev = perms_.get("package.developer");
		assertEquals(2, pdev.size());

		perms = new HashMap<String, List<String>>() {{
			put("admin", null);
		}};
		pm.updateUserPermissions("wankle2", perms);

		perms_ = pm.retrieveUserPermissions("wankle2");
		List<String> aperms = perms_.get("admin");
		assertEquals(0, aperms.size());

		perms_ = pm.retrieveUserPermissions("wankle");

		padmin = perms_.get("package.admin");
		assertEquals(1, padmin.size());
		assertEquals("1234567890", padmin.get(0));

	}

	public void testNilUser() throws Exception {
		PermissionManager pm = new PermissionManager(RepositorySessionUtil.getRepository());
		Map<String, List<String>> perms_ = pm.retrieveUserPermissions("nobody");
		assertEquals(0, perms_.size());

		perms_ = pm.retrieveUserPermissions("nobody");
		assertEquals(0, perms_.size());
	}
}
