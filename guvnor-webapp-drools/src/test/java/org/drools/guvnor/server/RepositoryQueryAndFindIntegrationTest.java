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
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.ModuleItem;
import org.drools.type.DateFormatsImpl;
import org.junit.Test;

public class RepositoryQueryAndFindIntegrationTest extends GuvnorIntegrationTest {

    @Inject
    private RoleBasedPermissionStore    roleBasedPermissionStore;

    @Inject
    private RoleBasedPermissionManager  roleBasedPermissionManager;

    @Inject
    private RoleBasedPermissionResolver roleBasedPermissionResolver;

    @Test
    public void testQueryFullTextPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule*",
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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule*",
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
        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermissionCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermissionCategoryDescription";
        CategoryItem cat = rootCategory.addCategory( categoryName,
                                                     categoryDescription );

        String subCategory1Name = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermissionCat1";
        cat.addCategory( subCategory1Name,
                         "yeah" );
        String subCategory2Name = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermissionCat2";
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

            String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermissionPackage";
            String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermissionPackageDescription";
            ModuleItem pkg = rulesRepository.createModule( packageName,
                                                             packageDescription );

            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.asset1",
                                            "",
                                            categoryName + "/" + subCategory1Name,
                                            null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.asset2",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.asset3",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.asset4",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.asset4" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.asset5",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsWithAnalystPermission.asset*",
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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule*",
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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule3Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule*",
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
        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermissionCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermissionCategoryDescription";
        CategoryItem cat = rootCategory.addCategory( categoryName,
                                                     categoryDescription );

        String subCategory1Name = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermissionCat1";
        cat.addCategory( subCategory1Name,
                         "yeah" );
        String subCategory2Name = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermissionCat2";
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

            String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermissionPackage";
            String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermissionPackageDescription";
            ModuleItem pkg = rulesRepository.createModule( packageName,
                                                             packageDescription );

            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.asset1",
                                            "",
                                            categoryName + "/" + subCategory1Name,
                                            null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.asset2",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.asset3",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.asset4",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.asset4" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.asset5",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetWithAnalystPermission.asset*",
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

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsPackageDescription";
        ModuleItem pkg = rulesRepository.createModule( packageName,
                                                         packageDescription );

        AssetItem[] assets = new AssetItem[3];
        for ( int i = 0; i < assets.length; i++ ) {
            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.testMetaDataSearchAsset" + i,
                                            "" );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.numberwang" + i );
            asset.checkin( "" );
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.wang, org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.numberwan*";

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
        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionCategoryDescription";
        CategoryItem cat = rootCategory.addCategory( categoryName,
                                                     categoryDescription );

        String subCategory1Name = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionCat1";
        cat.addCategory( subCategory1Name,
                         "yeah" );
        String subCategory2Name = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionCat2";
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

            String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionPackage";
            String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionPackageDescription";
            ModuleItem pkg = rulesRepository.createModule( packageName,
                                                             packageDescription );

            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset1",
                                            "",
                                            categoryName + "/" + subCategory1Name,
                                            null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset2",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset3",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset4",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearchAsset5",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwang" );
            asset.checkin( "" );

            MetaDataQuery[] qr = new MetaDataQuery[2];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
            qr[0].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.wang, org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.testMetaDataSearch";
            qr[1] = new MetaDataQuery();
            qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
            qr[1].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermission.numberwan*";

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
        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCategoryDescription";
        CategoryItem cat = rootCategory.addCategory( categoryName,
                                                     categoryDescription );

        String subCategory1Name = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat1";
        cat.addCategory( subCategory1Name,
                         "yeah" );
        String subCategory2Name = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat2";
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

            String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootPackage";
            String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRootPackageDescription";
            ModuleItem pkg = rulesRepository.createModule( packageName,
                                                             packageDescription );

            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset1",
                                            "",
                                            categoryName + "/" + subCategory1Name,
                                            null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset2",
                                  "",
                                  categoryName,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset3",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset4",
                                  "",
                                  categoryName + "/" + subCategory2Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearchAsset5",
                                  "",
                                  categoryName + "/" + subCategory1Name,
                                  null );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwang" );
            asset.checkin( "" );

            MetaDataQuery[] qr = new MetaDataQuery[2];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
            qr[0].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.wang, org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.testMetaDataSearch";
            qr[1] = new MetaDataQuery();
            qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
            qr[1].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsWithAnalystPermissionRoot.numberwan*";

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

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResultsPackageDescription";
        ModuleItem pkg = rulesRepository.createModule( packageName,
                                                         packageDescription );

        AssetItem[] assets = new AssetItem[3];
        for ( int i = 0; i < assets.length; i++ ) {
            AssetItem asset = pkg.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.testMetaDataSearchAsset" + i,
                                            "" );
            asset.updateSubject( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.testMetaDataSearch" );
            asset.updateExternalSource( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.numberwang" + i );
            asset.checkin( "" );
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.wang, org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.numberwan*";

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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResultsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule( packageName,
                                                                 packageDescription,
                                                                 "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule3Description",
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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResultsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule( packageName,
                                                                 packageDescription,
                                                                 "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule1",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule1Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule2",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule2Description",
                                             categoryName,
                                             packageName,
                                             AssetFormats.BUSINESS_RULE );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule3",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule3Description",
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

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormatsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormatsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule( packageName,
                                                                 packageDescription );

        AssetItem as;
        as = packageItem.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormats.assetWithKnownFormat",
                                   "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormats.assetWithKnownFormatDescription" );
        as.updateFormat( AssetFormats.DRL );
        as.checkin( "" );

        as = packageItem.addAsset( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormats.assetWithUnknownFormat",
                                   "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormats.assetWithUnknownFormatDescription" );
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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRule",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRuleDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.TESTTEXTRULE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.TESTTEXTRULEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.tEsTtExTrUlE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.tEsTtExTrUlEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRule",
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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRule",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRuleDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.TESTTEXTRULE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.TESTTEXTRULEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.tEsTtExTrUlE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.tEsTtExTrUlEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRule",
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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.testTextRule",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.testTextRuleDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.TESTTEXTRULE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.TESTTEXTRULEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.tEsTtExTrUlE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.tEsTtExTrUlEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.testTextRule",
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

        String categoryName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResultsCategory";
        String categoryDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory( "/",
                                                  categoryName,
                                                  categoryDescription );

        String packageName = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResultsPackage";
        String packageDescription = "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResultsPackageDescription";
        repositoryPackageService.createModule( packageName,
                                                packageDescription,
                                                "package" );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.testTextRule",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.testTextRuleDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.TESTTEXTRULE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.TESTTEXTRULEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        serviceImplementation.createNewRule( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.tEsTtExTrUlE",
                                             "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.tEsTtExTrUlEDescription",
                                             categoryName,
                                             packageName,
                                             AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "org.drools.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.testTextRule*",
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
