/*
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

package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.security.MockRoleBasedPermissionStore;
import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.guvnor.server.security.RoleBasedPermissionManager;
import org.drools.guvnor.server.security.RoleBasedPermissionResolver;
import org.drools.guvnor.server.security.RoleType;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.permission.PermissionResolver;
import org.junit.Test;

public class ServiceImplSecurityTest extends GuvnorTestBase {

    @Test
    public void testLoadRuleAssetAnalyst() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            impl.getRulesRepository().createPackage( "testLoadRuleAssetAnalystPack1",
                                                     "desc" );
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetAnalystCat1",
                                                      "this is a cat" );
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetAnalystCat2",
                                                      "this is a cat" );

            String uuid1 = impl.createNewRule( "testLoadRuleAssetAnalystRule1",
                                               "description",
                                               "testLoadRuleAssetAnalystCat1",
                                               "testLoadRuleAssetAnalystPack1",
                                               AssetFormats.DRL );
            String uuid2 = impl.createNewRule( "testLoadRuleAssetAnalystRule2",
                                               "description",
                                               "testLoadRuleAssetAnalystCat2",
                                               "testLoadRuleAssetAnalystPack1",
                                               AssetFormats.DRL );

            // Mock up SEAM contexts
            //            Map<String, Object> application = new HashMap<String, Object>();
            //            Lifecycle.beginApplication( application );
            //            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               "testLoadRuleAssetAnalystCat1" ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            //now lets see if we can access this asset with the permissions
            @SuppressWarnings("unused")
            RuleAsset asset = assetRepositoryService.loadRuleAsset( uuid1 );
            try {
                asset = assetRepositoryService.loadRuleAsset( uuid2 );
                fail( "Did not catch expected exception" );
            } catch ( AuthorizationException e ) {
            }
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testLoadRuleAssetPackageReadonly() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String package1Name = "testLoadRuleAssetPackageReadonlyPack1";
            @SuppressWarnings("unused")
            String package1Uuid = repositoryPackageService.createPackage( package1Name,
                                                                          "desc" );
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetPackageReadonlyCat1",
                                                      "this is a cat" );

            String uuid1 = impl.createNewRule( "testLoadRuleAssetPackageReadonlyRule1",
                                               "description",
                                               "testLoadRuleAssetPackageReadonlyCat1",
                                               "testLoadRuleAssetPackageReadonlyPack1",
                                               AssetFormats.DRL );

            impl.getRulesRepository().createPackage( "testLoadRuleAssetPackageReadonlyPack2",
                                                     "desc" );

            String uuid2 = impl.createNewRule( "testLoadRuleAssetPackageReadonlyRule2",
                                               "description",
                                               "testLoadRuleAssetPackageReadonlyCat1",
                                               "testLoadRuleAssetPackageReadonlyPack2",
                                               AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_READONLY.getName(),
                                               package1Name,
                                               null ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            //now lets see if we can access this asset with the permissions
            @SuppressWarnings("unused")
            RuleAsset asset = assetRepositoryService.loadRuleAsset( uuid1 );
            try {
                asset = assetRepositoryService.loadRuleAsset( uuid2 );
                fail( "Did not catch expected exception" );
            } catch ( AuthorizationException e ) {
            }
        } finally {
            Lifecycle.endApplication();
        }
    }

    // Access an asset that belongs to no category. No role permission defined. RoleBasedAuthorization is not enabled

    @Test
    public void testLoadRuleAssetNoCategory() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            impl.getRulesRepository().createPackage( "testLoadRuleAssetNoCategoryPack1",
                                                     "desc" );
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetNoCategoryCat1",
                                                      "this is a cat" );

            String uuid = impl.createNewRule( "testLoadRuleAssetNoCategoryRule1",
                                              "description",
                                              null,
                                              "testLoadRuleAssetNoCategoryPack1",
                                              AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( false );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            // now lets see if we can access this asset with the permissions
            RuleAsset asset = assetRepositoryService.loadRuleAsset( uuid );
            assertNotNull( asset );
        } finally {
            Lifecycle.endApplication();
        }
    }

    //Access an asset that belongs to no category.
    //The user role is admin

    @Test
    public void testLoadRuleAssetNoCategoryPackageAdmin() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            PackageItem packageItem = impl.getRulesRepository().createPackage( "testLoadRuleAssetNoCategoryPackageAdminPack1",
                                                                               "desc" );
            String packageName = packageItem.getName();
            @SuppressWarnings("unused")
            String packageUuid = packageItem.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetNoCategoryPackageAdminCat1",
                                                      "this is a cat" );

            String uuid = impl.createNewRule( "testLoadRuleAssetNoCategoryPackageAdminRule1",
                                              "description",
                                              null,
                                              "testLoadRuleAssetNoCategoryPackageAdminPack1",
                                              AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_ADMIN.getName(),
                                               packageName,
                                               null ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            //now lets see if we can access this asset with the permissions
            RuleAsset asset = assetRepositoryService.loadRuleAsset( uuid );
            assertNotNull( asset );
        } finally {
            Lifecycle.endApplication();
        }
    }

    //Access an asset that belongs to no category.
    //The user role is analyst

    @Test
    public void testLoadRuleAssetNoCategoryAnalystNegative() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            PackageItem packageItem = impl.getRulesRepository().createPackage( "testLoadRuleAssetNoCategoryAnalystPack1",
                                                                               "desc" );
            @SuppressWarnings("unused")
            String packageUuid = packageItem.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetNoCategoryAnalystCat1",
                                                      "this is a cat" );
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetNoCategoryAnalystCat2",
                                                      "this is a cat" );

            String uuid1 = impl.createNewRule( "testLoadRuleAssetNoCategoryAnalystRule1",
                                               "description",
                                               null,
                                               "testLoadRuleAssetNoCategoryAnalystPack1",
                                               AssetFormats.DRL );
            String uuid2 = impl.createNewRule( "testLoadRuleAssetNoCategoryAnalystRule2",
                                               "description",
                                               "testLoadRuleAssetNoCategoryAnalystCat2",
                                               "testLoadRuleAssetNoCategoryAnalystPack1",
                                               AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               "testLoadRuleAssetNoCategoryAnalystCat2" ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            //now lets see if we can access this asset with the permissions
            @SuppressWarnings("unused")
            RuleAsset asset2 = assetRepositoryService.loadRuleAsset( uuid2 );
            try {
                @SuppressWarnings("unused")
                RuleAsset asset1 = assetRepositoryService.loadRuleAsset( uuid1 );
                fail( "Did not catch expected exception" );
            } catch ( AuthorizationException e ) {
            }
        } finally {
            Lifecycle.endApplication();
        }
    }

    //Access an asset that belongs to no category.
    //The user role is analyst

    @Test
    public void testLoadRuleAssetNoCategoryAnalystPositive() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            PackageItem packageItem = impl.getRulesRepository().createPackage( "testLoadRuleAssetNoCategoryAnalystPositivePack1",
                                                                               "desc" );
            @SuppressWarnings("unused")
            String packageUuid = packageItem.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetNoCategoryAnalystPositiveCat1",
                                                      "this is a cat" );
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetNoCategoryAnalystPositiveCat2",
                                                      "this is a cat" );

            String uuid1 = impl.createNewRule( "testLoadRuleAssetNoCategoryAnalystPositiveRule1",
                                               "description",
                                               null,
                                               "testLoadRuleAssetNoCategoryAnalystPositivePack1",
                                               AssetFormats.DRL );
            @SuppressWarnings("unused")
            String uuid2 = impl.createNewRule( "testLoadRuleAssetNoCategoryAnalystPositiveRule2",
                                               "description",
                                               "testLoadRuleAssetNoCategoryAnalystPositiveCat2",
                                               "testLoadRuleAssetNoCategoryAnalystPositivePack1",
                                               AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               null ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            //now lets see if we can access this asset with the permissions
            //RuleAsset asset2 = impl.loadRuleAsset(uuid2);
            @SuppressWarnings("unused")
            RuleAsset asset1 = assetRepositoryService.loadRuleAsset( uuid1 );
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testLoadRuleAssetWithRoleBasedAuthrozationAssetHasCategory() throws Exception {
        try {

            String category1 = "testLoadRuleAssetWithRoleBasedAuthrozationAssetHasCategoryCat";

            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            PackageItem packageItem = impl.getRulesRepository().createPackage( "testLoadRuleAssetWithRoleBasedAuthrozationAssetHasCategoryPack",
                                                                               "desc" );
            @SuppressWarnings("unused")
            String packageUuid = packageItem.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category1,
                                                      "this is a cat" );

            String uuid = impl.createNewRule( "testLoadRuleAssetWithRoleBasedAuthrozationAssetHasCategory",
                                              "description",
                                              category1,
                                              "testLoadRuleAssetWithRoleBasedAuthrozationAssetHasCategoryPack",
                                              AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category1 ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            // now lets see if we can access this asset with the permissions
            @SuppressWarnings("unused")
            RuleAsset asset = null;
            try {
                asset = assetRepositoryService.loadRuleAsset( uuid );
            } catch ( AuthorizationException e ) {
                fail( "User has permissions for the category" );
            }

        } finally {
            Lifecycle.endApplication();
        }
    }

    //Access an asset that belongs to no category. The user role is analyst and package.admin.
    //Because the analyst role the user has has no category access to the asset,
    //the permission can not be granted even though the package.admin role has package access.

    @Test
    public void testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryMixed() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            PackageItem packageItem = impl.getRulesRepository().createPackage( "testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryMixedPack",
                                                                               "desc" );
            String packageUuid = packageItem.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryMixedCat",
                                                      "this is a cat" );

            String uuid = impl.createNewRule( "testLoadRuleAssetWithRoleBasedAuthrozation",
                                              "description",
                                              null,
                                              "testLoadRuleAssetWithRoleBasedAuthrozationAssetNoCategoryMixedPack",
                                              AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               "category1" ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_ADMIN.getName(),
                                               packageUuid,
                                               null ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            //now lets see if we can access this asset with the permissions
            try {
                RuleAsset asset = assetRepositoryService.loadRuleAsset( uuid );
                fail( "Did not catch expected exception" );
            } catch ( AuthorizationException e ) {
            }
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testCreateNewRule() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testSecurityCreateNewRule",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testSecurityCreateNewRule",
                                                  "this is a cat" );

        Lifecycle.beginApplication( new HashMap<String, Object>() );
        Lifecycle.beginCall();
        MockIdentity mi = new MockIdentity();
        mi.inject();
        mi.create();

        try {
            impl.createNewRule( "testCreateNewRuleName22",
                                "an initial desc",
                                "testSecurityCreateNewRule",
                                "testSecurityCreateNewRule",
                                AssetFormats.DSL_TEMPLATE_RULE );
            fail( "not allowed" );
        } catch ( AuthorizationException e ) {
            assertNotNull( e.getMessage() );
        }

        mi.addPermissionResolver( new PermissionResolver() {
            public void filterSetByAction(Set<Object> arg0,
                                          String arg1) {
            }

            public boolean hasPermission(Object arg0,
                                         String arg1) {
                return (arg1.equals( RoleType.PACKAGE_DEVELOPER.getName() ));
            }

        } );
        impl.createNewRule( "testCreateNewRuleName22",
                            "an initial desc",
                            "testSecurityCreateNewRule",
                            "testSecurityCreateNewRule",
                            AssetFormats.DSL_TEMPLATE_RULE );

        Lifecycle.endApplication();
    }

    @Test
    public void testCheckinWithPackageReadonly() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String packageUuid = repositoryPackageService.createPackage( "testCheckinWithPackageReadonlyPack",
                                                                     "desc" );
        repositoryCategoryService.createCategory( "/",
                                                  "testCheckinWithPackageReadonlyCat",
                                                  "this is a description" );
        repositoryCategoryService.createCategory( "testCheckinWithPackageReadonlyCat",
                                                  "deeper",
                                                  "description" );
        String uuid = impl.createNewRule( "testChecking",
                                          "this is a description",
                                          "testCheckinWithPackageReadonlyCat",
                                          "testCheckinWithPackageReadonlyPack",
                                          AssetFormats.DRL );

        RuleAsset asset = repositoryAssetService.loadRuleAsset( uuid );
        assertNotNull( asset.getLastModified() );
        asset.getMetaData().setCoverage( "boo" );
        asset.setContent( new RuleContentText() );
        ((RuleContentText) asset.getContent()).content = "yeah !";
        Thread.sleep( 100 );

        // Mock up SEAM contexts
        Map<String, Object> application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );
        midentity.addPermissionResolver( resolver );
        midentity.create();

        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );
        Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                          impl );
        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_READONLY.getName(),
                                           packageUuid,
                                           null ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        //now lets see if we can access this asset with the permissions
        try {
            repositoryAssetService.checkinVersion( asset );
            fail( "Did not catch expected exception" );
        } catch ( AuthorizationException e ) {
        }

        Lifecycle.endApplication();
    }

    @Test
    public void testCheckinPackageDeveloper() throws Exception {
        //try {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String packageName = "testCheckinPackageDeveloperPack1";
        @SuppressWarnings("unused")
        String packageUuid = repositoryPackageService.createPackage( packageName,
                                                                     "desc" );
        repositoryCategoryService.createCategory( "/",
                                                  "testCheckinPackageDeveloperCat1",
                                                  "this is a description" );
        repositoryCategoryService.createCategory( "testCheckinPackageDeveloperCat1",
                                                  "deeper",
                                                  "description" );
        String uuid = impl.createNewRule( "testCheckinPackageDeveloperRule1",
                                          "this is a description",
                                          "testCheckinPackageDeveloperCat1",
                                          "testCheckinPackageDeveloperPack1",
                                          AssetFormats.DRL );

        RuleAsset asset = repositoryAssetService.loadRuleAsset( uuid );
        assertNotNull( asset.getLastModified() );
        asset.getMetaData().setCoverage( "boo" );
        asset.setContent( new RuleContentText() );
        ((RuleContentText) asset.getContent()).content = "yeah !";
        Thread.sleep( 100 );

        // Mock up SEAM contexts
        Map<String, Object> application = new HashMap<String, Object>();
        Lifecycle.beginApplication( application );
        Lifecycle.beginCall();
        MockIdentity midentity = new MockIdentity();
        RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
        resolver.setEnableRoleBasedAuthorization( true );
        midentity.addPermissionResolver( resolver );
        midentity.create();

        Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                          midentity );
        Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                          impl );
        List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
        pbps.add( new RoleBasedPermission( "jervis",
                                           RoleType.PACKAGE_DEVELOPER.getName(),
                                           packageName,
                                           null ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );

        // now lets see if we can access this asset with the permissions
        String uuid2 = repositoryAssetService.checkinVersion( asset );
        assertEquals( uuid,
                      uuid2 );
        //} finally {
        Lifecycle.endApplication();
        //}
    }

    @Test
    public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyFilter() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryAssetService assetRepositoryService = getRepositoryAssetService();
            RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String package3Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack3";
            @SuppressWarnings("unused")
            String package3Uuid = repositoryPackageService.createPackage( package3Name,
                                                                          "desc" );
            repositoryCategoryService.createCategory( "",
                                                      "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat3",
                                                      "this is a cat" );
            @SuppressWarnings("unused")
            String uuid3 = impl.createNewRule( "testLoadRuleAssetWithRoleBasedAuthrozation",
                                               "ReadonlyFilterDescription",
                                               "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat3",
                                               package3Name,
                                               AssetFormats.DRL );

            String package4Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack4";
            impl.getRulesRepository().createPackage( package4Name,
                                                     "desc" );
            @SuppressWarnings("unused")
            String uuid2 = impl.createNewRule( "testLoadRuleAssetWithRoleBasedAuthrozation",
                                               "ReadonlyFilterDescription",
                                               "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat3",
                                               package4Name,
                                               AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_READONLY.getName(),
                                               package3Name,
                                               null ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            TableDataResult result = assetRepositoryService.queryFullText( "testLoadRuleAssetWithRoleBasedAuthrozation",
                                                                           true,
                                                                           0,
                                                                           -1 );
            assertEquals( 1,
                          result.data.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyMetaDataFilter() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String rule7Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData7";
            String rule8Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData8";

            String package7Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack7";
            String category7Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat7";
            PackageItem packageItem7 = impl.getRulesRepository().createPackage( package7Name,
                                                                                "desc" );
            @SuppressWarnings("unused")
            String packageItem7UUID = packageItem7.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category7Name,
                                                      "this is a rabbit" );
            @SuppressWarnings("unused")
            String uuid7 = impl.createNewRule( rule7Name,
                                               "MetaDataFilterDescription7",
                                               category7Name,
                                               package7Name,
                                               AssetFormats.DRL );

            String package8Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack8";
            String category8Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat8";
            PackageItem packageItem8 = impl.getRulesRepository().createPackage( package8Name,
                                                                                "desc" );
            @SuppressWarnings("unused")
            String packageItem8UUID = packageItem8.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category8Name,
                                                      "this is a mouse" );
            @SuppressWarnings("unused")
            String uuid8 = impl.createNewRule( rule8Name,
                                               "MetaDataFilterDescription8",
                                               category8Name,
                                               package8Name,
                                               AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_READONLY.getName(),
                                               package7Name,
                                               null ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category7Name ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category8Name ) );

            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            MetaDataQuery[] qr = new MetaDataQuery[1];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
            qr[0].valueList = "MetaDataFilterDescription%";
            TableDataResult result = impl.queryMetaData( qr,
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         false,
                                                         0,
                                                         -1 );
            assertEquals( 2,
                          result.data.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyMetaDataFilter2() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String rule5Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData5";
            String rule6Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData6";

            String package5Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack5";
            String category5Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat5";
            PackageItem packageItem5 = impl.getRulesRepository().createPackage( package5Name,
                                                                                "desc" );
            @SuppressWarnings("unused")
            String packageItem5UUID = packageItem5.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category5Name,
                                                      "this is a cat" );
            @SuppressWarnings("unused")
            String uuid7 = impl.createNewRule( rule5Name,
                                               "MetaDataFilter2Description5",
                                               category5Name,
                                               package5Name,
                                               AssetFormats.DRL );

            String package6Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack6";
            String category6Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat6";
            PackageItem packageItem6 = impl.getRulesRepository().createPackage( package6Name,
                                                                                "desc" );
            @SuppressWarnings("unused")
            String packageItem6UUID = packageItem6.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category6Name,
                                                      "this is a dog" );
            @SuppressWarnings("unused")
            String uuid6 = impl.createNewRule( rule6Name,
                                               "MetaDataFilter2Description6",
                                               category6Name,
                                               package6Name,
                                               AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_READONLY.getName(),
                                               package5Name,
                                               null ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_READONLY.getName(),
                                               package6Name,
                                               null ) );

            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            MetaDataQuery[] qr = new MetaDataQuery[1];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
            qr[0].valueList = "MetaDataFilter2Description%";
            TableDataResult result = impl.queryMetaData( qr,
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         false,
                                                         0,
                                                         -1 );
            assertEquals( 2,
                          result.data.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyMetaDataFilter3() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String rule9Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData9";
            String rule10Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData10";

            String package9Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack9";
            String category9Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat9";
            PackageItem packageItem9 = impl.getRulesRepository().createPackage( package9Name,
                                                                                "desc" );
            @SuppressWarnings("unused")
            String packageItem9UUID = packageItem9.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category9Name,
                                                      "this is a pigeon" );
            @SuppressWarnings("unused")
            String uuid9 = impl.createNewRule( rule9Name,
                                               "MetaDataFilter3Description9",
                                               category9Name,
                                               package9Name,
                                               AssetFormats.DRL );

            String package10Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack10";
            String category10Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat10";
            PackageItem packageItem10 = impl.getRulesRepository().createPackage( package10Name,
                                                                                 "desc" );
            @SuppressWarnings("unused")
            String packageItem10UUID = packageItem10.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category10Name,
                                                      "this is a sparrow" );
            @SuppressWarnings("unused")
            String uuid10 = impl.createNewRule( rule10Name,
                                                "MetaDataFilter3Description10",
                                                category10Name,
                                                package10Name,
                                                AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category9Name ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category10Name ) );

            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            MetaDataQuery[] qr = new MetaDataQuery[1];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
            qr[0].valueList = "MetaDataFilter3Description%";
            TableDataResult result = impl.queryMetaData( qr,
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         false,
                                                         0,
                                                         -1 );
            assertEquals( 2,
                          result.data.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testTableDisplayHandler() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String rule11Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData11";
            String rule12Name = "testLoadRuleAssetWithRoleBasedAuthrozationForMetaData12";

            String package11Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack11";
            String category11Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat11";
            PackageItem packageItem11 = impl.getRulesRepository().createPackage( package11Name,
                                                                                 "desc" );
            @SuppressWarnings("unused")
            String packageItem11UUID = packageItem11.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category11Name,
                                                      "this is a dock" );
            @SuppressWarnings("unused")
            String uuid11 = impl.createNewRule( rule11Name,
                                                "DisplayHandlerDescription11",
                                                category11Name,
                                                package11Name,
                                                AssetFormats.DRL );

            String package12Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyPack12";
            String category12Name = "testLoadRuleAssetWithRoleBasedAuthrozationPackageReadonlyCat12";
            PackageItem packageItem12 = impl.getRulesRepository().createPackage( package12Name,
                                                                                 "desc" );
            @SuppressWarnings("unused")
            String packageItem12UUID = packageItem12.getUUID();
            repositoryCategoryService.createCategory( "",
                                                      category12Name,
                                                      "this is a sparrow" );
            @SuppressWarnings("unused")
            String uuid12 = impl.createNewRule( rule12Name,
                                                "DisplayHandlerDescription12",
                                                category12Name,
                                                package12Name,
                                                AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category11Name ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category12Name ) );

            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            MetaDataQuery[] qr = new MetaDataQuery[1];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.DESCRIPTION_PROPERTY_NAME;
            qr[0].valueList = "DisplayHandlerDescription%";

            TableDataResult result = impl.queryMetaData( qr,
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         false,
                                                         1,
                                                         1 );
            assertEquals( 1,
                          result.data.length );

            result = impl.queryMetaData( qr,
                                         null,
                                         null,
                                         null,
                                         null,
                                         false,
                                         0,
                                         1 );
            assertEquals( 1,
                          result.data.length );

            result = impl.queryMetaData( qr,
                                         null,
                                         null,
                                         null,
                                         null,
                                         false,
                                         0,
                                         4 );
            assertEquals( 2,
                          result.data.length );

            result = impl.queryMetaData( qr,
                                         null,
                                         null,
                                         null,
                                         null,
                                         false,
                                         -1,
                                         4 );
            assertEquals( 2,
                          result.data.length );

            result = impl.queryMetaData( qr,
                                         null,
                                         null,
                                         null,
                                         null,
                                         false,
                                         6,
                                         4 );
            assertEquals( 0,
                          result.data.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

    //BRMS-282: listPackages only returns packages that the user has package.readonly permission or higher

    @Test
    public void testListPackagesPackageAdminAndAnalyst() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String package1Name = "testListPackagesPackageAdminAndAnalystPack1";
            String package2Name = "testListPackagesPackageAdminAndAnalystPack2";
            String category1Name = "testListPackagesPackageAdminAndAnalystCat1";
            @SuppressWarnings("unused")
            String package1UUID = (impl.getRulesRepository().createPackage( package1Name,
                                                                            "desc" )).getUUID();
            impl.getRulesRepository().createPackage( package2Name,
                                                     "desc" );
            repositoryCategoryService.createCategory( "",
                                                      category1Name,
                                                      "this is a cat" );

            impl.createNewRule( "testListPackagesPackageAdminAndAnalystRule1",
                                "description",
                                null,
                                package1Name,
                                AssetFormats.DRL );

            impl.createNewRule( "testListPackagesPackageAdminAndAnalystRule2",
                                "description",
                                category1Name,
                                package2Name,
                                AssetFormats.DRL );

            impl.createNewRule( "testListPackagesPackageAdminAndAnalystRule3",
                                "description",
                                null,
                                package2Name,
                                AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_ADMIN.getName(),
                                               package1Name,
                                               null ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category1Name ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            PackageConfigData[] res = repositoryPackageService.listPackages();
            assertEquals( 1,
                          res.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testLoadChildCategories() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String package1Name = "testLoadChildCategoriesPack1";
            String category1Name = "testLoadChildCategoriesCat1";
            String category2Name = "testLoadChildCategoriesCat2";

            impl.getRulesRepository().createPackage( package1Name,
                                                     "desc" );
            repositoryCategoryService.createCategory( "",
                                                      category1Name,
                                                      "this is a cat" );
            repositoryCategoryService.createCategory( "",
                                                      category2Name,
                                                      "this is a cat" );

            impl.createNewRule( "testLoadChildCategoriesRule1",
                                "description",
                                category1Name,
                                package1Name,
                                AssetFormats.DRL );

            impl.createNewRule( "testLoadChildCategoriesRule2",
                                "description",
                                category2Name,
                                package1Name,
                                AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST.getName(),
                                               null,
                                               category1Name ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            String[] res = repositoryCategoryService.loadChildCategories( "/" );
            assertEquals( 1,
                          res.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testloadRuleListForCategoriesPackageReadonly() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String package1Name = "testloadRuleListForCategoriesPackageReadonlyPack1";
            String category1Name = "testloadRuleListForCategoriesPackageReadonlyCat1";

            impl.getRulesRepository().createPackage( package1Name,
                                                     "desc" );
            repositoryCategoryService.createCategory( "",
                                                      category1Name,
                                                      "this is a cat" );

            impl.createNewRule( "testloadRuleListForCategoriesPackageReadonlyRule1",
                                "description",
                                category1Name,
                                package1Name,
                                AssetFormats.DRL );

            String package2Name = "testloadRuleListForCategoriesPackageReadonlyPack2";
            impl.getRulesRepository().createPackage( package2Name,
                                                     "desc" );

            impl.createNewRule( "testloadRuleListForCategoriesPackageReadonlyRule2",
                                "description",
                                category1Name,
                                package2Name,
                                AssetFormats.DRL );

            String package3Name = "testloadRuleListForCategoriesPackageReadonlyPack3";
            impl.getRulesRepository().createPackage( package3Name,
                                                     "desc" );

            impl.createNewRule( "testloadRuleListForCategoriesPackageReadonlyRule3",
                                "description",
                                category1Name,
                                package3Name,
                                AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_READONLY.getName(),
                                               package1Name,
                                               null ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_DEVELOPER.getName(),
                                               package2Name,
                                               null ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            TableDataResult res = repositoryCategoryService.loadRuleListForCategories( "testloadRuleListForCategoriesPackageReadonlyCat1",
                                                                                       0,
                                                                                       -1,
                                                                                       ExplorerNodeConfig.RULE_LIST_TABLE_ID );
            assertEquals( 0,
                          res.data.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

    @Test
    public void testloadRuleListForCategoriesPackageReadonlyPositive() throws Exception {
        try {
            ServiceImplementation impl = getServiceImplementation();
            RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
            String package1Name = "testloadRuleListForCategoriesPackageReadonlyPositivePack1";
            String category1Name = "testloadRuleListForCategoriesPackageReadonlyPositiveCat1";

            impl.getRulesRepository().createPackage( package1Name,
                                                     "desc" );
            repositoryCategoryService.createCategory( "",
                                                      category1Name,
                                                      "this is a cat" );

            impl.createNewRule( "testloadRuleListForCategoriesPackageReadonlyPositiveRule1",
                                "description",
                                category1Name,
                                package1Name,
                                AssetFormats.DRL );

            String package2Name = "testloadRuleListForCategoriesPackageReadonlyPositivePack2";
            impl.getRulesRepository().createPackage( package2Name,
                                                     "desc" );

            impl.createNewRule( "testloadRuleListForCategoriesPackageReadonlyPositiveRule2",
                                "description",
                                category1Name,
                                package2Name,
                                AssetFormats.DRL );

            String package3Name = "testloadRuleListForCategoriesPackageReadonlyPositivePack3";
            impl.getRulesRepository().createPackage( package3Name,
                                                     "desc" );

            impl.createNewRule( "testloadRuleListForCategoriesPackageReadonlyPositiveRule3",
                                "description",
                                category1Name,
                                package3Name,
                                AssetFormats.DRL );

            // Mock up SEAM contexts
            Map<String, Object> application = new HashMap<String, Object>();
            Lifecycle.beginApplication( application );
            Lifecycle.beginCall();
            MockIdentity midentity = new MockIdentity();
            RoleBasedPermissionResolver resolver = new RoleBasedPermissionResolver();
            resolver.setEnableRoleBasedAuthorization( true );
            midentity.addPermissionResolver( resolver );
            midentity.create();

            Contexts.getSessionContext().set( "org.jboss.seam.security.identity",
                                              midentity );
            Contexts.getSessionContext().set( "org.drools.guvnor.client.rpc.RepositoryService",
                                              impl );

            List<RoleBasedPermission> pbps = new ArrayList<RoleBasedPermission>();
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_READONLY.getName(),
                                               package1Name,
                                               null ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.PACKAGE_DEVELOPER.getName(),
                                               package2Name,
                                               null ) );
            pbps.add( new RoleBasedPermission( "jervis",
                                               RoleType.ANALYST_READ.getName(),
                                               null,
                                               category1Name ) );
            MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
            Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                              store );

            // Put permission list in session.
            RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
            testManager.create();
            Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                              testManager );

            TableDataResult res = repositoryCategoryService.loadRuleListForCategories( "testloadRuleListForCategoriesPackageReadonlyPositiveCat1",
                                                                                       0,
                                                                                       -1,
                                                                                       ExplorerNodeConfig.RULE_LIST_TABLE_ID );
            assertEquals( 3,
                          res.data.length );
        } finally {
            Lifecycle.endApplication();
        }
    }

}
