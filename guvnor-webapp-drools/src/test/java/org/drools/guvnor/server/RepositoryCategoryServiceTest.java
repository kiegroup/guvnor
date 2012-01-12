/*
 * Copyright 2011 JBoss Inc
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.guvnor.server.security.RoleBasedPermissionManager;
import org.drools.guvnor.server.security.RoleBasedPermissionResolver;
import org.drools.guvnor.server.security.RoleBasedPermissionStore;
import org.drools.guvnor.server.security.RoleType;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.ModuleItem;
import org.junit.Test;

import com.google.gwt.user.client.rpc.SerializationException;

public class RepositoryCategoryServiceTest extends GuvnorTestBase {

    @Inject
    private RoleBasedPermissionStore    roleBasedPermissionStore;

    @Inject
    private RoleBasedPermissionManager  roleBasedPermissionManager;

    @Inject
    private RoleBasedPermissionResolver roleBasedPermissionResolver;

    public RepositoryCategoryServiceTest() {
        //Some tests control their own login\logout life-cycle
        autoLoginAsAdmin = false;
    }

    @Test
    public void testRemoveCategory() throws Exception {
        loginAs( ADMIN_USERNAME );

        String[] children = repositoryCategoryService.loadChildCategories( "/" );
        repositoryCategoryService.createCategory( "/",
                                                  "testRemoveCategory",
                                                  "foo" );

        repositoryCategoryService.removeCategory( "testRemoveCategory" );
        String[] _children = repositoryCategoryService.loadChildCategories( "/" );
        assertEquals( children.length,
                      _children.length );

        logoutAs( ADMIN_USERNAME );
    }

    @Test
    public void testAddCategories() throws Exception {
        loginAs( ADMIN_USERNAME );

        rulesRepository.createModule( "testAddCategoriesPackage",
                                      "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testAddCategoriesCat1",
                                                  "this is a cat" );
        repositoryCategoryService.createCategory( "",
                                                  "testAddCategoriesCat2",
                                                  "this is a cat" );

        String uuid = serviceImplementation.createNewRule( "testCreateNewRuleName",
                                                           "an initial desc",
                                                           "testAddCategoriesCat1",
                                                           "testAddCategoriesPackage",
                                                           AssetFormats.DSL_TEMPLATE_RULE );

        AssetItem dtItem = rulesRepository.loadAssetByUUID( uuid );
        dtItem.addCategory( "testAddCategoriesCat1" );
        rulesRepository.save();

        AssetItem dtItem1 = rulesRepository.loadAssetByUUID( uuid );
        assertEquals( 1,
                      dtItem1.getCategories().size() );
        assertTrue( dtItem1.getCategorySummary().contains( "testAddCategoriesCat1" ) );

        AssetItem dtItem2 = rulesRepository.loadAssetByUUID( uuid );
        dtItem2.addCategory( "testAddCategoriesCat2" );
        rulesRepository.save();

        AssetItem dtItem3 = rulesRepository.loadAssetByUUID( uuid );
        assertEquals( 2,
                      dtItem3.getCategories().size() );
        assertTrue( dtItem3.getCategorySummary().contains( "testAddCategoriesCat2" ) );

        logoutAs( ADMIN_USERNAME );
    }

    @Test
    public void testLoadRuleListForCategoryPagedResultsCategory() throws Exception {
        loginAs( ADMIN_USERNAME );

        String[] originalCats = repositoryCategoryService.loadChildCategories( "/" );

        Boolean result = repositoryCategoryService.createCategory( "/",
                                                                   "TopLevel1",
                                                                   "a description" );
        assertTrue( result.booleanValue() );

        result = repositoryCategoryService.createCategory( "/",
                                                           "TopLevel2",
                                                           "a description" );
        assertTrue( result.booleanValue() );

        String[] cats = repositoryCategoryService.loadChildCategories( "/" );
        assertTrue( cats.length == originalCats.length + 2 );

        result = repositoryCategoryService.createCategory( "",
                                                           "Top3",
                                                           "description" );
        assertTrue( result.booleanValue() );

        result = repositoryCategoryService.createCategory( null,
                                                           "Top4",
                                                           "description" );
        assertTrue( result.booleanValue() );

        logoutAs( ADMIN_USERNAME );
    }

    @Test
    public void testLoadRuleListForCategoryPagedResults() throws Exception {
        loginAs( ADMIN_USERNAME );

        final int PAGE_SIZE = 2;

        String cat = "testLoadRuleListForCategoryPagedResultsCategory";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "testCategoryDescription" );
        repositoryPackageService.createModule( "testLoadRuleListForCategoryPagedResultsCategoryPackage",
                                               "testLoadRuleListForCategoryPagedResultsCategoryPackageDescription",
                                               "package" );

        serviceImplementation.createNewRule( "testTextRule1",
                                             "testLoadRuleListForCategoryPagedResultsCategoryRule1",
                                             cat,
                                             "testLoadRuleListForCategoryPagedResultsCategoryPackage",
                                             AssetFormats.DRL );
        serviceImplementation.createNewRule( "testTextRule2",
                                             "testLoadRuleListForCategoryPagedResultsCategoryRule2",
                                             cat,
                                             "testLoadRuleListForCategoryPagedResultsCategoryPackage",
                                             AssetFormats.DRL );
        serviceImplementation.createNewRule( "testTextRule3",
                                             "testLoadRuleListForCategoryPagedResultsCategoryRule3",
                                             cat,
                                             "testLoadRuleListForCategoryPagedResultsCategoryPackage",
                                             AssetFormats.DRL );

        CategoryPageRequest request = new CategoryPageRequest( cat,
                                                               0,
                                                               PAGE_SIZE );
        PageResponse<CategoryPageRow> response;
        response = repositoryCategoryService.loadRuleListForCategories( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == PAGE_SIZE );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = repositoryCategoryService.loadRuleListForCategories( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == PAGE_SIZE );
        assertTrue( response.getPageRowList().size() == 1 );
        assertTrue( response.isLastPage() );

        logoutAs( ADMIN_USERNAME );
    }

    @Test
    public void testLoadRuleListForCategoryFullResults() throws Exception {
        loginAs( ADMIN_USERNAME );

        String cat = "testLoadRuleListForCategoryFullResultsCategory";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "testLoadRuleListForCategoryFullResultsCategoryDescription" );
        repositoryPackageService.createModule( "testLoadRuleListForCategoryFullResultsCategoryPackage",
                                               "testLoadRuleListForCategoryFullResultsCategoryPackageDescription",
                                               "package" );

        serviceImplementation.createNewRule( "testTextRule1",
                                             "testLoadRuleListForCategoryFullResultsCategoryRule1",
                                             cat,
                                             "testLoadRuleListForCategoryFullResultsCategoryPackage",
                                             AssetFormats.DRL );
        serviceImplementation.createNewRule( "testTextRule2",
                                             "testLoadRuleListForCategoryFullResultsCategoryRule2",
                                             cat,
                                             "testLoadRuleListForCategoryFullResultsCategoryPackage",
                                             AssetFormats.DRL );
        serviceImplementation.createNewRule( "testTextRule3",
                                             "testLoadRuleListForCategoryFullResultsCategoryRule3",
                                             cat,
                                             "testLoadRuleListForCategoryFullResultsCategoryPackage",
                                             AssetFormats.DRL );

        CategoryPageRequest request = new CategoryPageRequest( cat,
                                                               0,
                                                               null );
        PageResponse<CategoryPageRow> response;
        response = repositoryCategoryService.loadRuleListForCategories( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );

        logoutAs( ADMIN_USERNAME );
    }

    @Test
    public void testLoadRuleListForCategoriesWithAnalystPermission() throws SerializationException {
        loginAs( ADMIN_USERNAME );

        CategoryItem rootCategory = rulesRepository.loadCategory( "/" );
        CategoryItem cat = rootCategory.addCategory( "testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                                                     "description" );
        cat.addCategory( "testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                         "yeah" );
        cat.addCategory( "testLoadRuleListForCategoriesWithAnalystPermissionCat2",
                         "yeah" );

        logoutAs( ADMIN_USERNAME );

        final String USERNAME = "categoryUser";
        loginAs( USERNAME );

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization( true );
        roleBasedPermissionStore.addRoleBasedPermissionForTesting( USERNAME,
                                                                   new RoleBasedPermission( USERNAME,
                                                                                            RoleType.ANALYST.getName(),
                                                                                            null,
                                                                                            "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat" ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        RuntimeException failingException = null;
        try {
            //Create assets for test
            ModuleItem pkg = rulesRepository.createModule( "testLoadRuleListForCategoriesWithAnalystPermission",
                                                           "" );

            AssetItem asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystPermission1",
                                            "",
                                            "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat/testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                                            null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystPermission2",
                                  "",
                                  "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                                  null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystPermission3",
                                  "",
                                  "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                                  null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystPermission4",
                                  "",
                                  "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat/testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                                  null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystPermission5",
                                  "",
                                  "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                                  null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );

            //Now test
            CategoryPageRequest request = new CategoryPageRequest( "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                                                                   0,
                                                                   new Integer( 2 ) );
            PageResponse<CategoryPageRow> response = repositoryCategoryService.loadRuleListForCategories( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( 0,
                          response.getStartRowIndex() );
            assertEquals( 2,
                          response.getPageRowList().size() );
            assertFalse( response.isLastPage() );

            request.setStartRowIndex( 2 );
            response = repositoryCategoryService.loadRuleListForCategories( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( 2,
                          response.getStartRowIndex() );
            assertEquals( 1,
                          response.getPageRowList().size() );
            assertEquals( true,
                          response.isTotalRowSizeExact() );
            assertEquals( 3,
                          response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } catch ( RuntimeException e ) {
            failingException = e;
        } finally {
            try {
                roleBasedPermissionResolver.setEnableRoleBasedAuthorization( false );
                roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting( USERNAME );

                try {
                    logoutAs( USERNAME );
                } catch ( IllegalStateException ise ) {
                    //Swallow it is expected - See http://seamframework.org/Community/OrgjbossseamsecurityIdentityImpllogoutRegression
                }

            } catch ( RuntimeException e ) {
                throw (failingException != null) ? failingException : e;
            }
        }
    }

    @Test
    public void testLoadRuleListForCategoriesWithAnalystNoRootCatPermission() throws SerializationException {
        loginAs( ADMIN_USERNAME );

        CategoryItem rootCategory = rulesRepository.loadCategory( "/" );
        CategoryItem cat = rootCategory.addCategory( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                                                     "description" );
        cat.addCategory( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermissionCat1",
                         "yeah" );
        cat.addCategory( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermissionCat2",
                         "yeah" );

        logoutAs( ADMIN_USERNAME );

        final String USERNAME = "categoryUser";
        loginAs( USERNAME );

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization( true );
        roleBasedPermissionStore.addRoleBasedPermissionForTesting( USERNAME,
                                                                   new RoleBasedPermission( USERNAME,
                                                                                            RoleType.ANALYST.getName(),
                                                                                            null,
                                                                                            "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission/testLoadRuleListForCategoriesWithAnalystNoRootCatPermissionCat1" ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        RuntimeException failingException = null;
        try {
            //Create asset for test
            ModuleItem pkg = rulesRepository.createModule( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                                                           "" );

            AssetItem asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermission1",
                                            "",
                                            "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission/testLoadRuleListForCategoriesWithAnalystNoRootCatPermissionCat1",
                                            null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermission2",
                                  "",
                                  "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                                  null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermission3",
                                  "",
                                  "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                                  null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermission4",
                                  "",
                                  "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission/testLoadRuleListForCategoriesWithAnalystNoRootCatPermissionCat1",
                                  null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );
            asset = pkg.addAsset( "testLoadRuleListForCategoriesWithAnalystNoRootCatPermission5",
                                  "",
                                  "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                                  null );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" );
            asset.checkin( "" );

            //Now test
            CategoryPageRequest request = new CategoryPageRequest( "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                                                                   0,
                                                                   new Integer( 2 ) );
            PageResponse<CategoryPageRow> response = repositoryCategoryService.loadRuleListForCategories( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( 0,
                          response.getStartRowIndex() );
            assertEquals( 0,
                          response.getPageRowList().size() );
            assertEquals( true,
                          response.isTotalRowSizeExact() );
            assertEquals( 0,
                          response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } catch ( RuntimeException e ) {
            failingException = e;
        } finally {
            try {
                roleBasedPermissionResolver.setEnableRoleBasedAuthorization( false );
                roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting( USERNAME );

                try {
                    logoutAs( USERNAME );
                } catch ( IllegalStateException ise ) {
                    //Swallow it is expected - See http://seamframework.org/Community/OrgjbossseamsecurityIdentityImpllogoutRegression
                }

            } catch ( RuntimeException e ) {
                throw (failingException != null) ? failingException : e;
            }
        }
    }

}
