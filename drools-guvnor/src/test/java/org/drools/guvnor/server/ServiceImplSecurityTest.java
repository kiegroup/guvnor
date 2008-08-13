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
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.security.MockRoleBasedPermissionStore;
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
import org.jboss.seam.security.permission.RoleBasedPermissionResolver;

 import org.drools.guvnor.client.common.AssetFormats;
 import org.drools.guvnor.client.rpc.MetaDataQuery;
 import org.drools.guvnor.client.rpc.RepositoryService;
 import org.drools.guvnor.client.rpc.RuleAsset;
 import org.drools.guvnor.client.rpc.RuleContentText;

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
	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
	    	resolver.setEnableRoleBasedAuthorization(true);
			midentity.addPermissionResolver(resolver);

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis", RoleTypes.ANALYST, null,
					"testLoadRuleAssetWithRoleBasedAuthrozationCat1"));
	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);


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
			String package1Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack1"; 
			String package1Uuid = impl.createPackage(package1Name, "desc");
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
	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
	    	resolver.setEnableRoleBasedAuthorization(true);
			midentity.addPermissionResolver(resolver);

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_READONLY,
					package1Name, null));
	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);


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
	// -> "upload new�Model jar".
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
	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
	    	resolver.setEnableRoleBasedAuthorization(false);
			midentity.addPermissionResolver(resolver);

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

			// now lets see if we can access this asset with the permissions
			RuleAsset asset = impl.loadRuleAsset(uuid);
			assertNotNull(asset);
		} finally {
			Lifecycle.endApplication();
		}
	}

	//Access an asset that belongs to no category. e.g., Packages -> Create New -> "upload new�Model jar".
	//The user role is admin
	public void testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryPackageAdmin() throws Exception {
		try {
			ServiceImplementation impl = getService();
			PackageItem packageItem = impl.repository.createPackage(
					"testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryPackageAdminPack", "desc");
			String packageName = packageItem.getName();
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
	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
	    	resolver.setEnableRoleBasedAuthorization(true);
			midentity.addPermissionResolver(resolver);

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_ADMIN,
					packageName, null));
	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

			//now lets see if we can access this asset with the permissions
			RuleAsset asset = impl.loadRuleAsset(uuid);
			assertNotNull(asset);
		} finally {
			Lifecycle.endApplication();
		}
	}

	//Access an asset that belongs to no category. e.g., Packages -> Create New -> "upload new�Model jar".
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
	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
	    	resolver.setEnableRoleBasedAuthorization(true);
			midentity.addPermissionResolver(resolver);

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.ANALYST,
					null, "category1"));
	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

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
	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
	    	resolver.setEnableRoleBasedAuthorization(true);
			midentity.addPermissionResolver(resolver);

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
	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

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
			String package1Name = "testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack1";
			String category1Name = "testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyCat1"; 
			
			impl.repository.createPackage(package1Name, "desc");
			impl.createCategory("", category1Name, "this is a cat");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description", category1Name, package1Name, "drl");

			String package2Name = "testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack2"; 
			impl.repository.createPackage(package2Name, "desc");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description", category1Name, package2Name, "drl");

			String package3Name = "testloadRuleListForCategoriesWithRoleBasedAuthrozationPackageReadonlyPack3";
			impl.repository.createPackage(package3Name, "desc");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description", category1Name, package3Name, "drl");

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
	    	resolver.setEnableRoleBasedAuthorization(true);
			midentity.addPermissionResolver(resolver);

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_READONLY, 
					package1Name, null));
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_DEVELOPER,
					package2Name, null));
	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);


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
			String package1Name = "testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack1";
			String category1Name = "testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystCat1"; 
			impl.repository.createPackage(
					package1Name, "desc");
			impl.createCategory("",category1Name, "this is a cat");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description", category1Name, package1Name, "drl");

			String package2Name = "testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack2";
			impl.repository.createPackage(package2Name, "desc");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description", category1Name, package2Name, "drl");

			String package3Name = "testloadRuleListForCategoriesWithRoleBasedAuthrozationAnalystPack3"; 
			impl.repository.createPackage(package3Name, "desc");

			impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
					"description", category1Name, package3Name, "drl");

			// Mock up SEAM contexts
			Map application = new HashMap<String, Object>();
			Lifecycle.beginApplication(application);
			Lifecycle.beginCall();
			MockIdentity midentity = new MockIdentity();
	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
	    	resolver.setEnableRoleBasedAuthorization(true);
			midentity.addPermissionResolver(resolver);

			Contexts.getSessionContext().set(
					"org.jboss.seam.security.identity", midentity);
			Contexts.getSessionContext().set(
					"org.drools.guvnor.client.rpc.RepositoryService", impl);

			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.ANALYST,
					null, category1Name));
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_READONLY,
					package2Name, null));
			pbps.add(new RoleBasedPermission("jervis",
					RoleTypes.PACKAGE_DEVELOPER,
					package3Name, null));
	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);


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
    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
    	resolver.setEnableRoleBasedAuthorization(true);
		midentity.addPermissionResolver(resolver);

		Contexts.getSessionContext().set(
				"org.jboss.seam.security.identity", midentity);
		Contexts.getSessionContext().set(
				"org.drools.guvnor.client.rpc.RepositoryService", impl);
		List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis",
				RoleTypes.PACKAGE_READONLY,
				packageUuid, null));
    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

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
		String packageName = "testCheckinWithPackageDeveloperPack"; 
		String packageUuid = impl.createPackage(packageName, "desc");
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
    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
    	resolver.setEnableRoleBasedAuthorization(true);
		midentity.addPermissionResolver(resolver);

		Contexts.getSessionContext().set(
				"org.jboss.seam.security.identity", midentity);
		Contexts.getSessionContext().set(
				"org.drools.guvnor.client.rpc.RepositoryService", impl);
		List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
		pbps.add(new RoleBasedPermission("jervis",
				RoleTypes.PACKAGE_DEVELOPER,
				packageName, null));
    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

		//now lets see if we can access this asset with the permissions
		String uuid2 =  impl.checkinVersion(asset);
		assertEquals(uuid, uuid2);

		Lifecycle.endApplication();
	}

 	public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyFilter() throws Exception {
 		try {
 			ServiceImplementation impl = getService();
 			String package3Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack3";
 			String package3Uuid = impl.createPackage(package3Name, "desc");
 			impl.createCategory("",
 					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat3",
 					"this is a cat");

 			String uuid3 = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
 					"ReadonlyFilterDescription",
 					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat3",
 					package3Name, "drl");

 			String package4Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack4";
 			impl.repository.createPackage(package4Name, "desc");

 			String uuid2 = impl.createNewRule("testLoadRuleAssetWithRoleBasedAuthrozation",
 					"ReadonlyFilterDescription",
 					"testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat3",
 					package4Name, "drl");

 			// Mock up SEAM contexts
 			Map application = new HashMap<String, Object>();
 			Lifecycle.beginApplication(application);
 			Lifecycle.beginCall();
 			MockIdentity midentity = new MockIdentity();
 	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
 	    	resolver.setEnableRoleBasedAuthorization(true);
 			midentity.addPermissionResolver(resolver);

 			Contexts.getSessionContext().set(
 					"org.jboss.seam.security.identity", midentity);
 			Contexts.getSessionContext().set(
 					"org.drools.guvnor.client.rpc.RepositoryService", impl);

 			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.PACKAGE_READONLY,
 					package3Name, null));
 	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
 	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

 			TableDataResult result = impl.queryFullText("testLoadRuleAssetWithRoleBasedAuthrozation", true, 0, -1);
 			assertEquals(1, result.data.length);
 		} finally {
 			Lifecycle.endApplication();
 		}
 	}

 	public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyMetaDataFilter() throws Exception {
 		try {
 			ServiceImplementation impl = getService();

 			String rule7Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData7";
 			String rule8Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData8";

 			String package7Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack7";
 			String category7Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat7";
 			PackageItem packageItem7 = impl.repository.createPackage(package7Name, "desc");
 			String packageItem7UUID = packageItem7.getUUID();
 			impl.createCategory("", category7Name, "this is a rabbit");

 			String uuid7 = impl.createNewRule(rule7Name,
 					"MetaDataFilterDescription7", category7Name, package7Name, "drl");

 			String package8Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack8";
 			String category8Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat8";
 			PackageItem packageItem8 = impl.repository.createPackage(package8Name, "desc");
 			String packageItem8UUID = packageItem8.getUUID();
 			impl.createCategory("", category8Name, "this is a mouse");
 			String uuid8 = impl.createNewRule(rule8Name,
 					"MetaDataFilterDescription8", category8Name, package8Name, "drl");

 			// Mock up SEAM contexts
 			Map application = new HashMap<String, Object>();
 			Lifecycle.beginApplication(application);
 			Lifecycle.beginCall();
 			MockIdentity midentity = new MockIdentity();
 	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
 	    	resolver.setEnableRoleBasedAuthorization(true);
 			midentity.addPermissionResolver(resolver);

 			Contexts.getSessionContext().set(
 					"org.jboss.seam.security.identity", midentity);
 			Contexts.getSessionContext().set(
 					"org.drools.guvnor.client.rpc.RepositoryService", impl);

 			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.PACKAGE_READONLY,
 					package7Name, null));
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.ANALYST,
 					null, category7Name));
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.ANALYST,
 					null, category8Name));

 	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
 	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

 			MetaDataQuery[] qr = new MetaDataQuery[1];
 			qr[0] = new MetaDataQuery();
 			qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
 			qr[0].valueList = "MetaDataFilterDescription%";
 			TableDataResult result = impl.queryMetaData(qr, null, null, null, null, false, 0, -1);
 			assertEquals(2, result.data.length);
 		} finally {
 			Lifecycle.endApplication();
 		}
 	}

 	public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyMetaDataFilter2() throws Exception {
 		try {
 			ServiceImplementation impl = getService();

 			String rule5Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData5";
 			String rule6Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData6";

 			String package5Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack5";
 			String category5Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat5";
 			PackageItem packageItem5 = impl.repository.createPackage(package5Name, "desc");
 			String packageItem5UUID = packageItem5.getUUID();
 			impl.createCategory("", category5Name, "this is a cat");
 			String uuid7 = impl.createNewRule(rule5Name,
 					"MetaDataFilter2Description5", category5Name, package5Name, "drl");

 			String package6Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack6";
 			String category6Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat6";
 			PackageItem packageItem6 = impl.repository.createPackage(package6Name, "desc");
 			String packageItem6UUID = packageItem6.getUUID();
 			impl.createCategory("", category6Name, "this is a dog");
 			String uuid6 = impl.createNewRule(rule6Name,
 					"MetaDataFilter2Description6", category6Name, package6Name, "drl");

 			// Mock up SEAM contexts
 			Map application = new HashMap<String, Object>();
 			Lifecycle.beginApplication(application);
 			Lifecycle.beginCall();
 			MockIdentity midentity = new MockIdentity();
 	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
 	    	resolver.setEnableRoleBasedAuthorization(true);
 			midentity.addPermissionResolver(resolver);

 			Contexts.getSessionContext().set(
 					"org.jboss.seam.security.identity", midentity);
 			Contexts.getSessionContext().set(
 					"org.drools.guvnor.client.rpc.RepositoryService", impl);

 			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.PACKAGE_READONLY,
 					package5Name, null));
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.PACKAGE_READONLY,
 					package6Name, null));

 	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
 	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

 			MetaDataQuery[] qr = new MetaDataQuery[1];
 			qr[0] = new MetaDataQuery();
 			qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
 			qr[0].valueList = "MetaDataFilter2Description%";
 			TableDataResult result = impl.queryMetaData(qr, null, null, null, null, false, 0, -1);
 			assertEquals(2, result.data.length);
 		} finally {
 			Lifecycle.endApplication();
 		}
 	}

 	public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyMetaDataFilter3() throws Exception {
 		try {
 			ServiceImplementation impl = getService();

 			String rule9Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData9";
 			String rule10Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData10";

 			String package9Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack9";
 			String category9Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat9";
 			PackageItem packageItem9 = impl.repository.createPackage(package9Name, "desc");
 			String packageItem9UUID = packageItem9.getUUID();
 			impl.createCategory("", category9Name, "this is a pigeon");
 			String uuid9 = impl.createNewRule(rule9Name,
 					"MetaDataFilter3Description9", category9Name, package9Name, "drl");

 			String package10Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack10";
 			String category10Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat10";
 			PackageItem packageItem10 = impl.repository.createPackage(package10Name, "desc");
 			String packageItem10UUID = packageItem10.getUUID();
 			impl.createCategory("", category10Name, "this is a sparrow");
 			String uuid10 = impl.createNewRule(rule10Name,
 					"MetaDataFilter3Description10", category10Name, package10Name, "drl");

 			// Mock up SEAM contexts
 			Map application = new HashMap<String, Object>();
 			Lifecycle.beginApplication(application);
 			Lifecycle.beginCall();
 			MockIdentity midentity = new MockIdentity();
 	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
 	    	resolver.setEnableRoleBasedAuthorization(true);
 			midentity.addPermissionResolver(resolver);

 			Contexts.getSessionContext().set(
 					"org.jboss.seam.security.identity", midentity);
 			Contexts.getSessionContext().set(
 					"org.drools.guvnor.client.rpc.RepositoryService", impl);

 			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.ANALYST,
 					null, category9Name));
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.ANALYST,
 					null, category10Name));

 	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
 	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

 			MetaDataQuery[] qr = new MetaDataQuery[1];
 			qr[0] = new MetaDataQuery();
 			qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
 			qr[0].valueList = "MetaDataFilter3Description%";
 			TableDataResult result = impl.queryMetaData(qr, null, null, null, null, false, 0, -1);
 			assertEquals(2, result.data.length);
 		} finally {
 			Lifecycle.endApplication();
 		}
 	}

 	public void testTableDisplayHandler() throws Exception {
 		try {
 			ServiceImplementation impl = getService();

 			String rule11Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData11";
 			String rule12Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData12";

 			String package11Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack11";
 			String category11Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat11";
 			PackageItem packageItem11 = impl.repository.createPackage(package11Name, "desc");
 			String packageItem11UUID = packageItem11.getUUID();
 			impl.createCategory("", category11Name, "this is a dock");
 			String uuid11 = impl.createNewRule(rule11Name,
 					"DisplayHandlerDescription11", category11Name, package11Name, "drl");

 			String package12Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack12";
 			String category12Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat12";
 			PackageItem packageItem12 = impl.repository.createPackage(package12Name, "desc");
 			String packageItem12UUID = packageItem12.getUUID();
 			impl.createCategory("", category12Name, "this is a sparrow");
 			String uuid12 = impl.createNewRule(rule12Name,
 					"DisplayHandlerDescription12", category12Name, package12Name, "drl");

 			// Mock up SEAM contexts
 			Map application = new HashMap<String, Object>();
 			Lifecycle.beginApplication(application);
 			Lifecycle.beginCall();
 			MockIdentity midentity = new MockIdentity();
 	    	RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
 	    	resolver.setEnableRoleBasedAuthorization(true);
 			midentity.addPermissionResolver(resolver);

 			Contexts.getSessionContext().set(
 					"org.jboss.seam.security.identity", midentity);
 			Contexts.getSessionContext().set(
 					"org.drools.guvnor.client.rpc.RepositoryService", impl);

 			List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.ANALYST,
 					null, category11Name));
 			pbps.add(new RoleBasedPermission("jervis",
 					RoleTypes.ANALYST,
 					null, category12Name));

 	    	MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore(pbps);
 	    	Contexts.getSessionContext().set("org.drools.guvnor.server.security.RoleBasedPermissionStore", store);

 			MetaDataQuery[] qr = new MetaDataQuery[1];
 			qr[0] = new MetaDataQuery();
 			qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
 			qr[0].valueList = "DisplayHandlerDescription%";

 			TableDataResult result = impl.queryMetaData(qr, null, null, null, null, false, 1, 1);
 			assertEquals(1, result.data.length);

 			result = impl.queryMetaData(qr, null, null, null, null, false, 0, 1);
 			assertEquals(1, result.data.length);

 			result = impl.queryMetaData(qr, null, null, null, null, false, 0, 4);
 			assertEquals(2, result.data.length);

 			result = impl.queryMetaData(qr, null, null, null, null, false, -1, 4);
 			assertEquals(2, result.data.length);

 			result = impl.queryMetaData(qr, null, null, null, null, false, 6, 4);
 			assertEquals(0, result.data.length);
 		} finally {
 			Lifecycle.endApplication();
 		}
 	}

	private ServiceImplementation getService() throws Exception {
		ServiceImplementation impl = new ServiceImplementation();

		impl.repository = new RulesRepository(TestEnvironmentSessionHelper
				.getSession());
		return impl;
	}

}
