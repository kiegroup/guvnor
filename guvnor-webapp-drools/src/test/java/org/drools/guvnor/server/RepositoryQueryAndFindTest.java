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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.drools.repository.PackageItem;
import org.drools.type.DateFormatsImpl;
import org.junit.Test;

public class RepositoryQueryAndFindTest extends GuvnorTestBase {

    @Inject
    private RoleBasedPermissionStore roleBasedPermissionStore;

    @Inject
    private RoleBasedPermissionManager roleBasedPermissionManager;

    @Inject
    private RoleBasedPermissionResolver roleBasedPermissionResolver;

    @Test
    public void testQueryFullTextPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String cat = "testQueryFullTextPagedResults";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQueryFullTextPagedResults",
                                                "for testing search.",
                                                "package" );

        serviceImplementation.createNewRule("testTextRule1",
                "desc",
                cat,
                "testQueryFullTextPagedResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testTextRule2",
                "desc",
                cat,
                "testQueryFullTextPagedResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testTextRule3",
                "desc",
                cat,
                "testQueryFullTextPagedResults",
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText( request );

        assertNotNull(response);
        assertNotNull( response.getPageRowList() );
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
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

        String cat = "testQueryFullTextFullResults";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQueryFullTextFullResults",
                                                "for testing search.",
                                                "package" );

        serviceImplementation.createNewRule("testTextRule1",
                "desc",
                cat,
                "testQueryFullTextFullResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testTextRule2",
                "desc",
                cat,
                "testQueryFullTextFullResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testTextRule3",
                "desc",
                cat,
                "testQueryFullTextFullResults",
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
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
        CategoryItem rootCategory = rulesRepository.loadCategory("/");
        CategoryItem cat = rootCategory.addCategory("testQueryFullTextFullResultsWithAnalystPermission", "description");
        cat.addCategory( "testQueryFullTextFullResultsWithAnalystPermissionCat1",
                         "yeah");
        cat.addCategory( "testQueryFullTextFullResultsWithAnalystPermissionCat2",
                         "yeah");
        
        logoutAs(ADMIN_USERNAME);
        final String USERNAME = "queryAndFindUser";
        loginAs(USERNAME);

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization(true);
        roleBasedPermissionStore.addRoleBasedPermissionForTesting(USERNAME, new RoleBasedPermission( USERNAME,
                                           RoleType.ANALYST.getName(),
                                           null,
                                           "testQueryFullTextFullResultsWithAnalystPermission/testQueryFullTextFullResultsWithAnalystPermissionCat1" ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {
        
            final int PAGE_SIZE = 2;

            PackageItem pkg = rulesRepository.createPackage("testQueryFullTextFullResultsWithAnalystPermission",
                    "");

            AssetItem asset = pkg.addAsset( "asset1",
                    "",
                    "testQueryFullTextFullResultsWithAnalystPermission/testQueryFullTextFullResultsWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "asset2",
                    "",
                    "testQueryFullTextFullResultsWithAnalystPermission/testQueryFullTextFullResultsWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "asset3",
                    "",
                    "testQueryFullTextFullResultsWithAnalystPermission/testQueryFullTextFullResultsWithAnalystPermissionCat2",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "asset4",
                    "",
                    "testQueryFullTextFullResultsWithAnalystPermission/testQueryFullTextFullResultsWithAnalystPermissionCat2",
                    null);
            asset.updateSubject( "asset4" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "asset5",
                    "",
                    "testQueryFullTextFullResultsWithAnalystPermission/testQueryFullTextFullResultsWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );

            QueryPageRequest request = new QueryPageRequest( "asset*",
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
                    response.isTotalRowSizeExact());
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
                    response.isTotalRowSizeExact());
            assertEquals( 3,
                    response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization(false);
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting(USERNAME);
            logoutAs(USERNAME);
            loginAs(ADMIN_USERNAME);
        }
    }
    
    @Test
    public void testQuickFindAssetPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String cat = "testQuickFindAssetPagedResults";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQuickFindAssetPagedResults",
                                                "for testing search.",
                                                "package" );

        serviceImplementation.createNewRule("testTextRule1",
                "desc",
                cat,
                "testQuickFindAssetPagedResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testTextRule2",
                "desc",
                cat,
                "testQuickFindAssetPagedResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testTextRule3",
                "desc",
                cat,
                "testQuickFindAssetPagedResults",
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
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

        String cat = "testQuickFindAssetFullResults";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQuickFindAssetFullResults",
                                                "for testing search.",
                                                "package" );

        serviceImplementation.createNewRule("testTextRule1",
                "desc",
                cat,
                "testQuickFindAssetFullResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testTextRule2",
                "desc",
                cat,
                "testQuickFindAssetFullResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("testTextRule3",
                "desc",
                cat,
                "testQuickFindAssetFullResults",
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
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
        CategoryItem rootCategory = rulesRepository.loadCategory("/");
        CategoryItem cat = rootCategory.addCategory("testQuickFindAssetWithAnalystPermissionRootCat", "description");
        cat.addCategory( "testQuickFindAssetWithAnalystPermissionCat1",
                         "yeah");
        cat.addCategory( "testQuickFindAssetWithAnalystPermissionCat2",
                         "yeah");
        
        logoutAs(ADMIN_USERNAME);
        final String USERNAME = "queryAndFindUser";
        loginAs(USERNAME);

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization(true);
        roleBasedPermissionStore.addRoleBasedPermissionForTesting(USERNAME, new RoleBasedPermission( USERNAME,
                                           RoleType.ANALYST.getName(),
                                           null,
                                           "testQuickFindAssetWithAnalystPermissionRootCat/testQuickFindAssetWithAnalystPermissionCat1" ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {
            final int PAGE_SIZE = 2;

            PackageItem pkg = rulesRepository.createPackage( "testQuickFindAssetWithAnalystPermission",
                                                                       "" );

            AssetItem asset = pkg.addAsset( "asset1",
                    "",
                    "testQuickFindAssetWithAnalystPermissionRootCat/testQuickFindAssetWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "asset2",
                    "",
                    "testQuickFindAssetWithAnalystPermissionRootCat/testQuickFindAssetWithAnalystPermissionCat2",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "asset3",
                    "",
                    "testQuickFindAssetWithAnalystPermissionRootCat/testQuickFindAssetWithAnalystPermissionCat2",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "asset4",
                    "",
                    "testQuickFindAssetWithAnalystPermissionRootCat/testQuickFindAssetWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "asset4" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "asset5",
                    "",
                    "testQuickFindAssetWithAnalystPermissionRootCat/testQuickFindAssetWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );

            QueryPageRequest request = new QueryPageRequest( "asset*",
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
                    response.isTotalRowSizeExact());
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
                    response.isTotalRowSizeExact());
            assertEquals( 3,
                    response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization(false);
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting(USERNAME);
            logoutAs(USERNAME);
            loginAs(ADMIN_USERNAME);
        }
    }
    
    @Test
    public void testQueryMetaDataPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        PackageItem pkg = rulesRepository.createPackage( "testMetaDataSearch",
                                                                   "" );

        AssetItem[] assets = new AssetItem[3];
        for ( int i = 0; i < assets.length; i++ ) {
            AssetItem asset = pkg.addAsset( "testMetaDataSearchAsset" + i,
                                            "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" + i );
            asset.checkin( "" );
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "wang, testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "numberwan*";

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
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(PAGE_SIZE,
                response.getPageRowList().size());
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
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
        CategoryItem cat = rootCategory.addCategory("testQueryMetaDataPagedResultsWithAnalystPermissionRootCat", "description");
        cat.addCategory( "testQueryMetaDataPagedResultsWithAnalystPermissionCat1",
                         "yeah");
        cat.addCategory( "testQueryMetaDataPagedResultsWithAnalystPermissionCat2",
        "yeah");
        
        logoutAs(ADMIN_USERNAME);
        final String USERNAME = "queryAndFindUser";
        loginAs(USERNAME);

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization(true);
        roleBasedPermissionStore.addRoleBasedPermissionForTesting(USERNAME,  new RoleBasedPermission( USERNAME,
                                           RoleType.ANALYST.getName(),
                                           null,
                                           "testQueryMetaDataPagedResultsWithAnalystPermissionRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionCat1" ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {
            final int PAGE_SIZE = 2;

            PackageItem pkg = rulesRepository.createPackage( "testQueryMetaDataPagedResultsWithAnalystPermission_package",
                                                                       "" );

            AssetItem asset = pkg.addAsset( "testMetaDataSearchAsset1",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "testMetaDataSearchAsset2",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionCat2",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "testMetaDataSearchAsset3",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "testMetaDataSearchAsset4",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionCat2",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "testMetaDataSearchAsset5",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );

            MetaDataQuery[] qr = new MetaDataQuery[2];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
            qr[0].valueList = "wang, testMetaDataSearch";
            qr[1] = new MetaDataQuery();
            qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
            qr[1].valueList = "numberwan*";

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
                    response.isTotalRowSizeExact());
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
                    response.isTotalRowSizeExact());
            assertEquals( 3,
                    response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization(false);
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting(USERNAME);
            logoutAs(USERNAME);
            loginAs(ADMIN_USERNAME);
        }
    }
    
    @Test
    public void testQueryMetaDataPagedResultsWithAnalystPermissionRootCategory() throws Exception {
        CategoryItem rootCategory = rulesRepository.loadCategory("/");
        CategoryItem cat = rootCategory.addCategory("testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryRootCat", "description");
        cat.addCategory( "testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat1",
                         "yeah");
        cat.addCategory( "testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat2",
        "yeah");
        
        logoutAs(ADMIN_USERNAME);
        final String USERNAME = "queryAndFindUser";
        loginAs(USERNAME);

        roleBasedPermissionResolver.setEnableRoleBasedAuthorization(true);
        roleBasedPermissionStore.addRoleBasedPermissionForTesting(USERNAME,  new RoleBasedPermission( USERNAME,
                                           RoleType.ANALYST.getName(),
                                           null,
                                           "testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat1" ) );
        roleBasedPermissionManager.create(); // HACK flushes the permission cache

        try {
            final int PAGE_SIZE = 2;

            PackageItem pkg = rulesRepository.createPackage("testQueryMetaDataPagedResultsWithAnalystPermissionRootCategory_package",
                    "");

            AssetItem asset = pkg.addAsset( "testMetaDataSearchAsset1",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "testMetaDataSearchAsset2",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryRootCat",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "testMetaDataSearchAsset3",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "testMetaDataSearchAsset4",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat2",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );
            asset = pkg.addAsset( "testMetaDataSearchAsset5",
                    "",
                    "testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryRootCat/testQueryMetaDataPagedResultsWithAnalystPermissionRootCategoryCat1",
                    null);
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang");
            asset.checkin( "" );

            MetaDataQuery[] qr = new MetaDataQuery[2];
            qr[0] = new MetaDataQuery();
            qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
            qr[0].valueList = "wang, testMetaDataSearch";
            qr[1] = new MetaDataQuery();
            qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
            qr[1].valueList = "numberwan*";

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
                    response.isTotalRowSizeExact());
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
                    response.isTotalRowSizeExact());
            assertEquals( 3,
                    response.getTotalRowSize() );
            assertTrue( response.isLastPage() );
        } finally {
            roleBasedPermissionResolver.setEnableRoleBasedAuthorization(false);
            roleBasedPermissionStore.clearAllRoleBasedPermissionsForTesting(USERNAME);
            logoutAs(USERNAME);
            loginAs(ADMIN_USERNAME);
        }
    }


    @Test
    public void testQueryMetaDataFullResults() throws Exception {

        PackageItem pkg = rulesRepository.createPackage("testQueryMetaDataFullResults_package",
                "");

        AssetItem[] assets = new AssetItem[3];
        for ( int i = 0; i < assets.length; i++ ) {
            AssetItem asset = pkg.addAsset( "testMetaDataSearchAsset" + i,
                                            "" );
            asset.updateSubject( "testMetaDataSearch" );
            asset.updateExternalSource( "numberwang" + i );
            asset.checkin( "" );
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "wang, testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "numberwan*";

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

        PackageItem packageItem = rulesRepository.createPackage("testFindAssetPagePackage",
                "testFindAssetPagePackageDescription");
        repositoryCategoryService.createCategory("",
                "testFindAssetPageCategory",
                "testFindAssetPageCategoryDescription");

        serviceImplementation.createNewRule("testFindAssetPageAsset1",
                "testFindAssetPageAsset1Description",
                "testFindAssetPageCategory",
                "testFindAssetPagePackage",
                AssetFormats.BUSINESS_RULE);

        serviceImplementation.createNewRule("testFindAssetPageAsset2",
                "testFindAssetPageAsset2Description",
                "testFindAssetPageCategory",
                "testFindAssetPagePackage",
                AssetFormats.BUSINESS_RULE);

        serviceImplementation.createNewRule("testFindAssetPageAsset3",
                "testFindAssetPageAsset3Description",
                "testFindAssetPageCategory",
                "testFindAssetPagePackage",
                AssetFormats.BUSINESS_RULE);

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
        PackageItem packageItem = rulesRepository.createPackage("testFindAssetPagePackage",
                "testFindAssetPagePackageDescription");
        repositoryCategoryService.createCategory("",
                "testFindAssetPageCategory",
                "testFindAssetPageCategoryDescription");

        serviceImplementation.createNewRule("testFindAssetPageAsset1",
                "testFindAssetPageAsset1Description",
                "testFindAssetPageCategory",
                "testFindAssetPagePackage",
                AssetFormats.BUSINESS_RULE);

        serviceImplementation.createNewRule("testFindAssetPageAsset2",
                "testFindAssetPageAsset2Description",
                "testFindAssetPageCategory",
                "testFindAssetPagePackage",
                AssetFormats.BUSINESS_RULE);

        serviceImplementation.createNewRule("testFindAssetPageAsset3",
                "testFindAssetPageAsset3Description",
                "testFindAssetPageCategory",
                "testFindAssetPagePackage",
                AssetFormats.BUSINESS_RULE);

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
        PackageItem packageItem = rulesRepository.createPackage("testFindAssetPageUnregisteredAssetFormats",
                "testFindAssetPageUnregisteredAssetFormatsDescription");
        AssetItem as;

        as = packageItem.addAsset( "assetWithKnownFormat",
                                   "assetWithKnownFormatDescription" );
        as.updateFormat( AssetFormats.DRL );
        as.checkin( "" );

        as = packageItem.addAsset( "assetWithUnknownFormat",
                                   "assetWithUnknownFormatDescription" );
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

        String cat = "testQuickFindAssetCaseInsensitiveFullResults";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQuickFindAssetCaseInsensitiveFullResults",
                                                "for testing search.",
                                                "package" );

        serviceImplementation.createNewRule("testTextRule",
                "desc",
                cat,
                "testQuickFindAssetCaseInsensitiveFullResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("TESTTEXTRULE",
                "desc",
                cat,
                "testQuickFindAssetCaseInsensitiveFullResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("tEsTtExTrUlE",
                "desc",
                cat,
                "testQuickFindAssetCaseInsensitiveFullResults",
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest( "testTextRule",
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

        String cat = "testQuickFindAssetCaseInsensitivePagedResults";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQuickFindAssetCaseInsensitivePagedResults",
                                                "for testing search.",
                                                "package" );

        serviceImplementation.createNewRule("testTextRule",
                "desc",
                cat,
                "testQuickFindAssetCaseInsensitivePagedResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("TESTTEXTRULE",
                "desc",
                cat,
                "testQuickFindAssetCaseInsensitivePagedResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("tEsTtExTrUlE",
                "desc",
                cat,
                "testQuickFindAssetCaseInsensitivePagedResults",
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest( "testTextRule",
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
    public void testQueryFullTextCaseInsensitiveFullResults() throws Exception {

        String cat = "testQueryFullTextCaseInsensitiveFullResults";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQueryFullTextCaseInsensitiveFullResults",
                                                "for testing search.",
                                                "package" );

        serviceImplementation.createNewRule("testTextRule",
                "desc",
                cat,
                "testQueryFullTextCaseInsensitiveFullResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("TESTTEXTRULE",
                "desc",
                cat,
                "testQueryFullTextCaseInsensitiveFullResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("tEsTtExTrUlE",
                "desc",
                cat,
                "testQueryFullTextCaseInsensitiveFullResults",
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest( "testTextRule",
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

        String cat = "testQueryFullTextCaseInsensitivePagedResults";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testQueryFullTextCaseInsensitivePagedResults",
                                                "for testing search.",
                                                "package" );

        serviceImplementation.createNewRule("testTextRule",
                "desc",
                cat,
                "testQueryFullTextCaseInsensitivePagedResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("TESTTEXTRULE",
                "desc",
                cat,
                "testQueryFullTextCaseInsensitivePagedResults",
                AssetFormats.DRL);

        serviceImplementation.createNewRule("tEsTtExTrUlE",
                "desc",
                cat,
                "testQueryFullTextCaseInsensitivePagedResults",
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText( request );

        assertNotNull(response);
        assertNotNull( response.getPageRowList() );
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
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
