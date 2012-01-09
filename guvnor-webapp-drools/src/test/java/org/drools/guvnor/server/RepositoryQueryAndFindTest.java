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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.drools.core.util.DateUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.QueryMetadataPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.guvnor.server.security.RoleBasedPermissionManager;
import org.drools.guvnor.server.security.RoleBasedPermissionResolver;
import org.drools.guvnor.server.security.RoleBasedPermissionStore;
import org.drools.guvnor.server.security.RoleType;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.ModuleItem;
import org.drools.type.DateFormatsImpl;
import org.junit.Test;

public class RepositoryQueryAndFindTest extends GuvnorTestBase {

    @Inject
    private RoleBasedPermissionStore    roleBasedPermissionStore;

    @Inject
    private RoleBasedPermissionManager  roleBasedPermissionManager;

    @Inject
    private RoleBasedPermissionResolver roleBasedPermissionResolver;

    @Test
    public void testQueryFullTextPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextPagedResults.testTextRule*",
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = serviceImplementation.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( PAGE_SIZE,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQueryFullTextFullResults() throws Exception {

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResults.testTextRule*",
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 3,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQueryFullTextFullResultsWithAnalystPermission() throws Exception {
        CategoryItem rootCategory = rulesRepository.loadCategory( "/" );
        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermissionCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermissionCategoryDescription";
        CategoryItem cat = rootCategory.addCategory( categoryName,
                                                     categoryDescription );

        String subCategory1Name = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermissionCat1";
        cat.addCategory( subCategory1Name,
                         "yeah" );
        String subCategory2Name = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermissionCat2";
        cat.addCategory( subCategory2Name,
                         "yeah" );

        logoutAs( ADMIN_USERNAME );
        final String USERNAME = "queryAndFindUser";
        loginAs( USERNAME );

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization( true );
        roleBasedPermissionStore.addRoleBasedPermissionForTesting( USERNAME,
                                                                   new RoleBasedPermission( USERNAME,
                                                                                            RoleType.ANALYST.getName(),
                                                                                            null,
                                                                                            categoryName + "/" + subCategory1Name ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {

            final int PAGE_SIZE = 2;

            String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermissionPackage";
            String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermissionPackageDescription";
            ModuleItem pkg = rulesRepository.createModule( packageName,
                                                             packageDescription );

            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.asset1",
                                            "",
                                            categoryName + "/" + subCategory1Name,
                                            null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.asset2",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.asset3",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.asset4",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.asset4" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.asset5",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextFullResultsWithAnalystPermission.asset*",
                                                             false,
                                                             0,
                                                             PAGE_SIZE );

            PageResponse<QueryPageRow> response = serviceImplementation.queryFullText( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( 0,
                          response.getStartRowIndex() );
            assertEquals( PAGE_SIZE,
                          response.getPageRowList().size() );
            assertEquals( false,
                          response.isTotalRowSizeExact() );
            assertFalse( response.isLastPage() );

            request.setStartRowIndex( PAGE_SIZE );
            response = repositoryAssetService.quickFindAsset( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( PAGE_SIZE,
                          response.getStartRowIndex() );
            assertEquals( 1,
                          response.getPageRowList().size() );
            assertEquals( true,
                          response.isTotalRowSizeExact() );
            assertEquals( 3,
                          response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization( false );
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting( USERNAME );
            try {
                logoutAs( USERNAME );
                loginAs( ADMIN_USERNAME );
            } catch ( IllegalStateException ise ) {
                //TODO logoutAs(USERNAME) throws an exception causing the test to fail
                System.err.println( ise.getMessage() );
            }
        }
    }

    @Test
    public void testQuickFindAssetPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetPagedResults.testTextRule*",
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;

        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = serviceImplementation.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( PAGE_SIZE,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQuickFindAssetFullResults() throws Exception {

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetFullResults.testTextRule*",
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;

        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 3,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQuickFindAssetWithAnalystPermission() throws Exception {

        CategoryItem rootCategory = rulesRepository.loadCategory( "/" );
        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermissionCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermissionCategoryDescription";
        CategoryItem cat = rootCategory.addCategory( categoryName,
                                                     categoryDescription );

        String subCategory1Name = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermissionCat1";
        cat.addCategory( subCategory1Name,
                         "yeah" );
        String subCategory2Name = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermissionCat2";
        cat.addCategory( subCategory2Name,
                         "yeah" );

        logoutAs( ADMIN_USERNAME );
        final String USERNAME = "queryAndFindUser";
        loginAs( USERNAME );

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization( true );
        roleBasedPermissionStore.addRoleBasedPermissionForTesting( USERNAME,
                                                                   new RoleBasedPermission( USERNAME,
                                                                                            RoleType.ANALYST.getName(),
                                                                                            null,
                                                                                            categoryName + "/" + subCategory1Name ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {
            final int PAGE_SIZE = 2;

            String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermissionPackage";
            String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermissionPackageDescription";
            ModuleItem pkg = rulesRepository.createModule( packageName,
                                                             packageDescription );

            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.asset1",
                                            "",
                                            categoryName + "/" + subCategory1Name,
                                            null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.asset2",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.asset3",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.asset4",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.asset4" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.asset5",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetWithAnalystPermission.asset*",
                                                             false,
                                                             0,
                                                             PAGE_SIZE );

            PageResponse<QueryPageRow> response = repositoryAssetService.quickFindAsset( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( 0,
                          response.getStartRowIndex() );
            assertEquals( PAGE_SIZE,
                          response.getPageRowList().size() );
            assertEquals( false,
                          response.isTotalRowSizeExact() );
            assertFalse( response.isLastPage() );

            request.setStartRowIndex( PAGE_SIZE );
            response = repositoryAssetService.quickFindAsset( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( PAGE_SIZE,
                          response.getStartRowIndex() );
            assertEquals( 1,
                          response.getPageRowList().size() );
            assertEquals( true,
                          response.isTotalRowSizeExact() );
            assertEquals( 3,
                          response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization( false );
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting( USERNAME );
            try {
                logoutAs( USERNAME );
                loginAs( ADMIN_USERNAME );
            } catch ( IllegalStateException ise ) {
                //TODO logoutAs(USERNAME) throws an exception causing the test to fail
                System.err.println( ise.getMessage() );
            }
        }
    }

    @Test
    public void testQueryMetaDataPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsPackageDescription";
        ModuleItem pkg = rulesRepository.createModule( packageName,
                                                         packageDescription );

        AssetItem[] assets = new AssetItem[3];
        for ( int i = 0; i < assets.length; i++ ) {
            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResults.testMetaDataSearchAsset" + i,
                                            "" );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResults.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResults.numberwang" + i );
            asset.checkin( "" );
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResults.wang, org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResults.testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResults.numberwan*";

        List<MetaDataQuery> metadata = Arrays.asList( qr );
        QueryMetadataPageRequest request = new QueryMetadataPageRequest( metadata,
                                                                         DateUtils.parseDate( "10-Jul-1974",
                                                                                              new DateFormatsImpl() ),
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         false,
                                                                         0,
                                                                         PAGE_SIZE );

        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryMetaData( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = serviceImplementation.queryMetaData( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( PAGE_SIZE,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQueryMetaDataPagedResultsWithAnalystPermission() throws Exception {

        CategoryItem rootCategory = rulesRepository.loadCategory( "/" );
        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionCategoryDescription";
        CategoryItem cat = rootCategory.addCategory( categoryName,
                                                     categoryDescription );

        String subCategory1Name = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionCat1";
        cat.addCategory( subCategory1Name,
                         "yeah" );
        String subCategory2Name = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionCat2";
        cat.addCategory( subCategory2Name,
                         "yeah" );

        logoutAs( ADMIN_USERNAME );
        final String USERNAME = "queryAndFindUser";
        loginAs( USERNAME );

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization( true );
        roleBasedPermissionStore.addRoleBasedPermissionForTesting( USERNAME,
                                                                   new RoleBasedPermission( USERNAME,
                                                                                            RoleType.ANALYST.getName(),
                                                                                            null,
                                                                                            categoryName + "/" + subCategory1Name ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {
            final int PAGE_SIZE = 2;

            String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionPackage";
            String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionPackageDescription";
            ModuleItem pkg = rulesRepository.createModule( packageName,
                                                             packageDescription );

            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset1",
                                            "",
                                            categoryName + "/" + subCategory1Name,
                                            null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset2",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset3",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset4",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset5",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            MetaDataQuery[] qr = new MetaDataQuery[2];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
            qr[0].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.wang, org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch";
            qr[1] = new MetaDataQuery();
            qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
            qr[1].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwan*";

            List<MetaDataQuery> metadata = Arrays.asList( qr );
            QueryMetadataPageRequest request = new QueryMetadataPageRequest( metadata,
                                                                             DateUtils.parseDate( "10-Jul-1974",
                                                                                                  new DateFormatsImpl() ),
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             false,
                                                                             0,
                                                                             PAGE_SIZE );

            PageResponse<QueryPageRow> response;
            response = serviceImplementation.queryMetaData( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( 0,
                          response.getStartRowIndex() );
            assertEquals( PAGE_SIZE,
                          response.getPageRowList().size() );
            assertEquals( false,
                          response.isTotalRowSizeExact() );
            assertFalse( response.isLastPage() );

            request.setStartRowIndex( PAGE_SIZE );
            response = serviceImplementation.queryMetaData( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( PAGE_SIZE,
                          response.getStartRowIndex() );
            assertEquals( 1,
                          response.getPageRowList().size() );
            assertEquals( true,
                          response.isTotalRowSizeExact() );
            assertEquals( 3,
                          response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization( false );
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting( USERNAME );
            try {
                logoutAs( USERNAME );
                loginAs( ADMIN_USERNAME );
            } catch ( IllegalStateException ise ) {
                //TODO logoutAs(USERNAME) throws an exception causing the test to fail
                System.err.println( ise.getMessage() );
            }
        }
    }

    @Test
    public void testQueryMetaDataPagedResultsWithAnalystPermissionRootCategory() throws Exception {

        CategoryItem rootCategory = rulesRepository.loadCategory( "/" );
        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCategoryDescription";
        CategoryItem cat = rootCategory.addCategory( categoryName,
                                                     categoryDescription );

        String subCategory1Name = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat1";
        cat.addCategory( subCategory1Name,
                         "yeah" );
        String subCategory2Name = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat2";
        cat.addCategory( subCategory2Name,
                         "yeah" );

        logoutAs( ADMIN_USERNAME );
        final String USERNAME = "queryAndFindUser";
        loginAs( USERNAME );

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization( true );
        roleBasedPermissionStore.addRoleBasedPermissionForTesting( USERNAME,
                                                                   new RoleBasedPermission( USERNAME,
                                                                                            RoleType.ANALYST.getName(),
                                                                                            null,
                                                                                            categoryName + "/" + subCategory1Name ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {
            final int PAGE_SIZE = 2;

            String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootPackage";
            String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootPackageDescription";
            ModuleItem pkg = rulesRepository.createModule( packageName,
                                                             packageDescription );

            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset1",
                                            "",
                                            categoryName + "/" + subCategory1Name,
                                            null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset2",
                                  "",
                                  categoryName,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset3",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset4",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset5",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            MetaDataQuery[] qr = new MetaDataQuery[2];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
            qr[0].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.wang, org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch";
            qr[1] = new MetaDataQuery();
            qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
            qr[1].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwan*";

            List<MetaDataQuery> metadata = Arrays.asList( qr );
            QueryMetadataPageRequest request = new QueryMetadataPageRequest( metadata,
                                                                             DateUtils.parseDate( "10-Jul-1974",
                                                                                                  new DateFormatsImpl() ),
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             false,
                                                                             0,
                                                                             PAGE_SIZE );

            PageResponse<QueryPageRow> response;
            response = serviceImplementation.queryMetaData( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( 0,
                          response.getStartRowIndex() );
            assertEquals( PAGE_SIZE,
                          response.getPageRowList().size() );
            assertEquals( false,
                          response.isTotalRowSizeExact() );
            assertFalse( response.isLastPage() );

            request.setStartRowIndex( PAGE_SIZE );
            response = serviceImplementation.queryMetaData( request );

            assertNotNull( response );
            assertNotNull( response.getPageRowList() );
            assertEquals( PAGE_SIZE,
                          response.getStartRowIndex() );
            assertEquals( 1,
                          response.getPageRowList().size() );
            assertEquals( true,
                          response.isTotalRowSizeExact() );
            assertEquals( 3,
                          response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization( false );
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting( USERNAME );
            try {
                logoutAs( USERNAME );
                loginAs( ADMIN_USERNAME );
            } catch ( IllegalStateException ise ) {
                //TODO logoutAs(USERNAME) throws an exception causing the test to fail
                System.err.println( ise.getMessage() );
            }
        }
    }

    @Test
    public void testQueryMetaDataFullResults() throws Exception {

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataFullResultsPackageDescription";
        ModuleItem pkg = rulesRepository.createModule( packageName,
                                                         packageDescription );

        AssetItem[] assets = new AssetItem[3];
        for ( int i = 0; i < assets.length; i++ ) {
            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataFullResults.testMetaDataSearchAsset" + i,
                                            "" );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataFullResults.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataFullResults.numberwang" + i );
            asset.checkin( "" );
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataFullResults.wang, org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataFullResults.testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryMetaDataFullResults.numberwan*";

        List<MetaDataQuery> metadata = Arrays.asList( qr );
        QueryMetadataPageRequest request = new QueryMetadataPageRequest( metadata,
                                                                         DateUtils.parseDate( "10-Jul-1974",
                                                                                              new DateFormatsImpl() ),
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         false,
                                                                         0,
                                                                         null );

        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryMetaData( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 3,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testFindAssetPagePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResultsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule( packageName,
                                                                 packageDescription,
                                                                 "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPagePagedResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        List<String> formats = new ArrayList<String>();
        formats.add( AssetFormats.BUSINESS_RULE );
        AssetPageRequest request = new AssetPageRequest( packageItem.getUUID(),
                                                         formats,
                                                         null,
                                                         0,
                                                         PAGE_SIZE );

        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = repositoryAssetService.findAssetPage( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( PAGE_SIZE,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testFindAssetPageFullResults() throws Exception {

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResultsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule( packageName,
                                                                 packageDescription,
                                                                 "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageFullResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        List<String> formats = new ArrayList<String>();
        formats.add( AssetFormats.BUSINESS_RULE );
        AssetPageRequest request = new AssetPageRequest( packageItem.getUUID(),
                                                         formats,
                                                         null,
                                                         0,
                                                         null );

        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 3,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testFindAssetPageUnregisteredAssetFormats() throws Exception {

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageUnregisteredAssetFormatsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageUnregisteredAssetFormatsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule( packageName,
                                                                 packageDescription );

        AssetItem as;
        as = packageItem.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageUnregisteredAssetFormats.assetWithKnownFormat",
                                   "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageUnregisteredAssetFormats.assetWithKnownFormatDescription" );
        as.updateFormat( AssetFormats.DRL );
        as.checkin( "" );

        as = packageItem.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageUnregisteredAssetFormats.assetWithUnknownFormat",
                                   "org.drools.guvnor.server.RepositoryQueryAndFindTest.testFindAssetPageUnregisteredAssetFormats.assetWithUnknownFormatDescription" );
        as.updateFormat( "something_silly" );
        as.checkin( "" );

        List<String> formats = new ArrayList<String>();
        formats.add( AssetFormats.DRL );
        AssetPageRequest request = new AssetPageRequest( packageItem.getUUID(),
                                                         formats,
                                                         null,
                                                         0,
                                                         null );

        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage( request );

        assertEquals( 1,
                      response.getPageRowList().size() );
    }

    @Test
    public void testQuickFindAssetCaseInsensitiveFullResults() throws Exception {

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRule",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRuleDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResults.TESTTEXTRULE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResults.TESTTEXTRULEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResults.tEsTtExTrUlE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResults.tEsTtExTrUlEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRule",
                                                         false,
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;

        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 3,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQuickFindAssetCaseInsensitivePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRule",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRuleDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResults.TESTTEXTRULE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResults.TESTTEXTRULEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResults.tEsTtExTrUlE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResults.tEsTtExTrUlEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRule",
                                                         false,
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;

        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( PAGE_SIZE,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQueryFullTextCaseInsensitiveFullResults() throws Exception {

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResults.testTextRule",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResults.testTextRuleDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResults.TESTTEXTRULE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResults.TESTTEXTRULEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResults.tEsTtExTrUlE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResults.tEsTtExTrUlEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitiveFullResults.testTextRule",
                                                         false,
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQueryFullTextCaseInsensitivePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResults.testTextRule",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResults.testTextRuleDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResults.TESTTEXTRULE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResults.TESTTEXTRULEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResults.tEsTtExTrUlE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResults.tEsTtExTrUlEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindTest.testQueryFullTextCaseInsensitivePagedResults.testTextRule*",
                                                         false,
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = serviceImplementation.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( PAGE_SIZE,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

}
