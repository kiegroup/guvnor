package org.drools.guvnor.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.server.security.CategoryBasedPermissionResolver;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.security.PackageBasedPermissionResolver;
import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.permission.PermissionResolver;

import junit.framework.TestCase;

public class ServiceImplSecurityTest extends TestCase {

	public void testLoadRuleAssetWithRoleBasedAuthrozationAnalyst() throws Exception {
		try {
			ServiceImplementation impl = getService();
			impl.repository.createPackage(
					"testLoadRuleAssetWithRoleBasedAuthrozation", "desc");
			impl.createCategory("",
					"testLoadRuleAssetWithRoleBasedAuthrozationCat1",
					"this is a cat");
			impl.createCategory("",
					"testLoadRuleAssetWithRoleBasedAuthrozationCat2",
					"this is a cat");

			String uuid1 = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testLoadRuleAssetWithRoleBasedAuthrozationCat1",
					"testLoadRuleAssetWithRoleBasedAuthrozation", "drl");
			String uuid2 = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation2",
					"description",
					"testLoadRuleAssetWithRoleBasedAuthrozationCat2",
					"testLoadRuleAssetWithRoleBasedAuthrozation", "drl");

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
			// this makes Identity.hasRole("admin") return false
			midentity.setHasRole(false);
			midentity.addPermissionResolver(new PackageBasedPermissionResolver());
			midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis", RoleTypes.ANALYST, null,
					"testLoadRuleAssetWithRoleBasedAuthrozationCat1"));
			Contexts.getSessionContext().set("packageBasedPermission", pbps);


			//now lets see if we can access this asset with the permissions
			RuleAsset asset = impl.loadRuleAsset(uuid1);
			try {
				asset = impl.loadRuleAsset(uuid2);
				fail("Did not catch expected exception");
			} catch (AuthorizationException e) {
			}
		} finally {
			Lifecycle.endApplication();
		}
	}

	public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonly() throws Exception {
		try {
			ServiceImplementation impl = getService();
			String package1Uuid = impl.createPackage(
					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack1", "desc");
			impl.createCategory("",
					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat1",
					"this is a cat");

			String uuid1 = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat1",
					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack1", "drl");

			impl.repository.createPackage(
					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack2", "desc");

			String uuid2 = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat1",
					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack2", "drl");

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
			// this makes Identity.hasRole("admin") return false
			midentity.setHasRole(false);
			midentity.addPermissionResolver(new PackageBasedPermissionResolver());
			midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_READONLY,
					package1Uuid, null));

			Contexts.getSessionContext().set("packageBasedPermission", pbps);

			
			//now lets see if we can access this asset with the permissions
			RuleAsset asset = impl.loadRuleAsset(uuid1);
			try {
				asset = impl.loadRuleAsset(uuid2);
				fail("Did not catch expected exception");
			} catch (AuthorizationException e) {
			}
		} finally {
			Lifecycle.endApplication();
		}
	}

	// Access an asset that belongs to no category. e.g., Packages -> Create New
	// -> "upload new Model jar".
	// The user role is admin
	public void testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategory() throws Exception {
		try {
			ServiceImplementation impl = getService();
			impl.repository.createPackage(
							"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryPack",
							"desc");
			impl.createCategory(
							"",
							"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryCat",
							"this is a cat");

			String uuid = impl.createNewRule(
							"testLoadRuleAssetWithRoleBasedAuthrozation",
							"description",
							null,
							"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryPack",
							"drl");

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
			// this makes Identity.hasRole("admin") return true
			midentity.setHasRole(true);
			midentity.addPermissionResolver(new PackageBasedPermissionResolver());
			midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();

			Contexts.getSessionContext().set("packageBasedPermission", pbps);

			// now lets see if we can access this asset with the permissions
			RuleAsset asset = impl.loadRuleAsset(uuid);
			assertNotNull(asset);
		} finally {
			Lifecycle.endApplication();
		}
	}

	//Access an asset that belongs to no category. e.g., Packages -> Create New -> "upload new Model jar".
	//The user role is admin
	public void testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryPackageAdmin() throws Exception {
		try {
			ServiceImplementation impl = getService();
			PackageItem packageItem = impl.repository.createPackage(
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryPackageAdminPack", "desc");
			String packageUuid = packageItem.getUUID();
			impl.createCategory("",
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryPackageAdminCat",
					"this is a cat");

			String uuid = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					null,
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryPackageAdminPack", "drl");

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
			// this makes Identity.hasRole("admin") return false
			midentity.setHasRole(false);
			midentity.addPermissionResolver(new PackageBasedPermissionResolver());
			midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_ADMIN,
					packageUuid, null));

			Contexts.getSessionContext().set("packageBasedPermission", pbps);

			//now lets see if we can access this asset with the permissions
			RuleAsset asset = impl.loadRuleAsset(uuid);
			assertNotNull(asset);
		} finally {
			Lifecycle.endApplication();
		}
	}

	//Access an asset that belongs to no category. e.g., Packages -> Create New -> "upload new Model jar".
	//The user role is analyst
	public void testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryAnalyst() throws Exception {
		try {
			ServiceImplementation impl = getService();
			PackageItem packageItem = impl.repository.createPackage(
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryAnalystPack", "desc");
			String packageUuid = packageItem.getUUID();
			impl.createCategory("",
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryAnalystCat",
					"this is a cat");

			String uuid = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					null,
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryAnalystPack", "drl");

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
			// this makes Identity.hasRole("admin") return false
			midentity.setHasRole(false);
			midentity.addPermissionResolver(new PackageBasedPermissionResolver());
			midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.ANALYST,
					null, "category1"));

			Contexts.getSessionContext().set("packageBasedPermission", pbps);

			//now lets see if we can access this asset with the permissions
			try {
				RuleAsset asset = impl.loadRuleAsset(uuid);
				fail("Did not catch expected exception");
			} catch (AuthorizationException e) {
			}
		} finally {
			Lifecycle.endApplication();
		}
	}

	//Access an asset that belongs to no category. The user role is analyst and package.admin.
	//Because the analyst role the user has has no category access to the asset,
	//the permission can not be granted even though the package.admin role has package access.
	public void testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryMixed() throws Exception {
		try {
			ServiceImplementation impl = getService();
			PackageItem packageItem = impl.repository.createPackage(
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryMixedPack", "desc");
			String packageUuid = packageItem.getUUID();
			impl.createCategory("",
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryMixedCat",
					"this is a cat");

			String uuid = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					null,
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryMixedPack", "drl");

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
			// this makes Identity.hasRole("admin") return false
			midentity.setHasRole(false);
			midentity.addPermissionResolver(new PackageBasedPermissionResolver());
			midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.ANALYST,
					null, "category1"));
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_ADMIN,
					packageUuid, null));

			Contexts.getSessionContext().set("packageBasedPermission", pbps);

			//now lets see if we can access this asset with the permissions
			try {
				RuleAsset asset = impl.loadRuleAsset(uuid);
				fail("Did not catch expected exception");
			} catch (AuthorizationException e) {
			}
		} finally {
			Lifecycle.endApplication();
		}
	}

	public void testCreateNewRule() throws Exception {
		ServiceImplementation impl = getService();
		impl.repository.createPackage("testSecurityCreateNewRule", "desc");
		impl.createCategory("", "testSecurityCreateNewRule", "this is a cat");

		Lifecycle.beginApplication(new HashMap());
		Lifecycle.beginCall();
		MockIdentity mi = new MockIdentity();
		mi.inject();

		try {
			impl.createNewRule("testCreateNewRuleName22",
					"an initial desc", "testSecurityCreateNewRule", "testSecurityCreateNewRule",
					AssetFormats.DSL_TEMPLATE_RULE);
			fail("not allowed");
		} catch (AuthorizationException e) {
			assertNotNull(e.getMessage());
		}

		mi.addPermissionResolver(new PermissionResolver() {
			public void filterSetByAction(Set<Object> arg0, String arg1) {
			}

			public boolean hasPermission(Object arg0, String arg1) {
				return (arg1.equals(RoleTypes.PACKAGE_DEVELOPER));
			}

		});
		impl.createNewRule("testCreateNewRuleName22",
				"an initial desc", "testSecurityCreateNewRule", "testSecurityCreateNewRule",
				AssetFormats.DSL_TEMPLATE_RULE);

		Lifecycle.endApplication();
	}

	public void testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonly() throws Exception {
		try {
			ServiceImplementation impl = getService();
			impl.repository.createPackage(
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack1", "desc");
			impl.createCategory("",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyCat1",
					"this is a cat");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyCat1",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack1", "drl");

			impl.repository.createPackage(
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack2", "desc");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyCat1",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack2", "drl");

			impl.repository.createPackage(
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack3", "desc");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyCat1",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack3", "drl");

			PackageItem source = impl.repository.loadPackage("testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack1");
	        String package1Uuid = source.getUUID();
			source = impl.repository.loadPackage("testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack2");
	        String package2Uuid = source.getUUID();

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
			// this makes Identity.hasRole("admin") return false
			midentity.setHasRole(false);
			midentity.addPermissionResolver(new PackageBasedPermissionResolver());
			midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_READONLY,
					package1Uuid, null));
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_DEVELOPER,
					package2Uuid, null));

			Contexts.getSessionContext().set("packageBasedPermission", pbps);


			TableDataResult res = impl.loadRuleListForCategories(
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyCat1", 0, -1,
					AssetItemGrid.RULE_LIST_TABLE_ID);
			assertEquals(2, res.data.length);
		} finally {
			Lifecycle.endApplication();
		}
	}

	public void testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalyst() throws Exception {
		try {
			ServiceImplementation impl = getService();
			impl.repository.createPackage(
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack1", "desc");
			impl.createCategory("",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystCat1",
					"this is a cat");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystCat1",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack1", "drl");

			impl.repository.createPackage(
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack2", "desc");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystCat1",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack2", "drl");

			impl.repository.createPackage(
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack3", "desc");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystCat1",
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack3", "drl");

			PackageItem source = impl.repository.loadPackage("testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack2");
	        String package2Uuid = source.getUUID();
			source = impl.repository.loadPackage("testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack3");
	        String package3Uuid = source.getUUID();

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
			// this makes Identity.hasRole("admin") return false
			midentity.setHasRole(false);
			midentity.addPermissionResolver(new PackageBasedPermissionResolver());
			midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.ANALYST,
					null, "testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystCat1"));
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_READONLY,
					package2Uuid, null));
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_DEVELOPER,
					package3Uuid, null));

			Contexts.getSessionContext().set("packageBasedPermission", pbps);


			TableDataResult res = impl.loadRuleListForCategories(
					"testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystCat1", 0, -1,
					AssetItemGrid.RULE_LIST_TABLE_ID);
			assertEquals(3, res.data.length);
		} finally {
			Lifecycle.endApplication();
		}
	}
	
	public void testCheckinWithPackageReadonly() throws Exception {
		ServiceImplementation impl = getService();
		String packageUuid = impl.createPackage(
				"testCheckinWithPackageReadonlyPack", "desc");
		impl.createCategory("/", "testCheckinWithPackageReadonlyCat",
						"this is a description");
		impl.createCategory("testCheckinWithPackageReadonlyCat", "deeper", "description");
		String uuid = impl.createNewRule("testChecking",
				"this is a description", "testCheckinWithPackageReadonlyCat",
				"testCheckinWithPackageReadonlyPack", "drl");
		RuleAsset asset = impl.loadRuleAsset(uuid);
		assertNotNull(asset.metaData.lastModifiedDate);
		asset.metaData.coverage = "boo";
		asset.content = new RuleContentText();
		((RuleContentText) asset.content).content = "yeah !";
		Thread.sleep(100);

		// Mock up SEAM contexts
		Map application = new HashMap<String, Object>();
		Lifecycle.beginApplication(application);
		Lifecycle.beginCall();
		MockIdentity midentity = new MockIdentity();
		// this makes Identity.hasRole("admin") return false
		midentity.setHasRole(false);
		midentity.addPermissionResolver(new PackageBasedPermissionResolver());
		midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

		Contexts.getSessionContext().set(
				"org.jboss.seam.security.identity", midentity);
		Contexts.getSessionContext().set(
				"org.drools.guvnor.client.rpc.RepositoryService", impl);
		List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis",
				RoleTypes.PACKAGE_READONLY,
				packageUuid, null));
		Contexts.getSessionContext().set("packageBasedPermission", pbps);		
		
		//now lets see if we can access this asset with the permissions
		try {
			impl.checkinVersion(asset);
			fail("Did not catch expected exception");
		} catch (AuthorizationException e) {
		}
		
		Lifecycle.endApplication();
	}
	
	public void testCheckinWithPackageDeveloper() throws Exception {
		ServiceImplementation impl = getService();
		String packageUuid = impl.createPackage(
				"testCheckinWithPackageDeveloperPack", "desc");
		impl.createCategory("/", "testCheckinWithPackageDeveloperCat",
						"this is a description");
		impl.createCategory("testCheckinWithPackageDeveloperCat", "deeper", "description");
		String uuid = impl.createNewRule("testChecking",
				"this is a description", "testCheckinWithPackageDeveloperCat",
				"testCheckinWithPackageDeveloperPack", "drl");
		RuleAsset asset = impl.loadRuleAsset(uuid);
		assertNotNull(asset.metaData.lastModifiedDate);
		asset.metaData.coverage = "boo";
		asset.content = new RuleContentText();
		((RuleContentText) asset.content).content = "yeah !";
		Thread.sleep(100);

		// Mock up SEAM contexts
		Map application = new HashMap<String, Object>();
		Lifecycle.beginApplication(application);
		Lifecycle.beginCall();
		MockIdentity midentity = new MockIdentity();
		// this makes Identity.hasRole("admin") return false
		midentity.setHasRole(false);
		midentity.addPermissionResolver(new PackageBasedPermissionResolver());
		midentity.addPermissionResolver(new CategoryBasedPermissionResolver());

		Contexts.getSessionContext().set(
				"org.jboss.seam.security.identity", midentity);
		Contexts.getSessionContext().set(
				"org.drools.guvnor.client.rpc.RepositoryService", impl);
		List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis",
				RoleTypes.PACKAGE_DEVELOPER,
				packageUuid, null));
		Contexts.getSessionContext().set("packageBasedPermission", pbps);		
		
		//now lets see if we can access this asset with the permissions
		String uuid2 =  impl.checkinVersion(asset);
		assertEquals(uuid, uuid2);
		
		Lifecycle.endApplication();
	}
	
	private ServiceImplementation getService() throws Exception {
		ServiceImplementation impl = new ServiceImplementation();

		impl.repository = new RulesRepository(TestEnvironmentSessionHelper
				.getSession());
		return impl;
	}

}
