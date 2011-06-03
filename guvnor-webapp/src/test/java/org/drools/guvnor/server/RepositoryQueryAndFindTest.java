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

import org.drools.core.util.DateUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.QueryMetadataPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.type.DateFormatsImpl;
import org.junit.Test;

public class RepositoryQueryAndFindTest extends GuvnorTestBase {

    @Test
    public void testQueryFullTextPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule1",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule2",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule3",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.queryFullText( request );

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

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule1",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule2",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule3",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 3,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQuickFindAssetPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule1",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule2",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule3",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.queryFullText( request );

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

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule1",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule2",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "testTextRule3",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
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
    public void testQueryMetaDataPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        PackageItem pkg = impl.getRulesRepository().createPackage( "testMetaDataSearch",
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
        response = impl.queryMetaData( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.queryMetaData( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( PAGE_SIZE,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQueryMetaDataFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        PackageItem pkg = impl.getRulesRepository().createPackage( "testMetaDataSearch",
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
                                                                         null );

        PageResponse<QueryPageRow> response;
        response = impl.queryMetaData( request );

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

        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        PackageItem packageItem = impl.getRulesRepository().createPackage( "testFindAssetPagePackage",
                                                                           "testFindAssetPagePackageDescription" );
        repositoryCategoryService.createCategory( "",
                                                  "testFindAssetPageCategory",
                                                  "testFindAssetPageCategoryDescription" );

        impl.createNewRule( "testFindAssetPageAsset1",
                            "testFindAssetPageAsset1Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testFindAssetPageAsset2",
                            "testFindAssetPageAsset2Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testFindAssetPageAsset3",
                            "testFindAssetPageAsset3Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        List<String> formats = new ArrayList<String>();
        formats.add( AssetFormats.BUSINESS_RULE );
        AssetPageRequest request = new AssetPageRequest( packageItem.getUUID(),
                                                         formats,
                                                         null,
                                                         0,
                                                         PAGE_SIZE );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
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
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        PackageItem packageItem = impl.getRulesRepository().createPackage( "testFindAssetPagePackage",
                                                                           "testFindAssetPagePackageDescription" );
        repositoryCategoryService.createCategory( "",
                                                  "testFindAssetPageCategory",
                                                  "testFindAssetPageCategoryDescription" );

        impl.createNewRule( "testFindAssetPageAsset1",
                            "testFindAssetPageAsset1Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testFindAssetPageAsset2",
                            "testFindAssetPageAsset2Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        impl.createNewRule( "testFindAssetPageAsset3",
                            "testFindAssetPageAsset3Description",
                            "testFindAssetPageCategory",
                            "testFindAssetPagePackage",
                            AssetFormats.BUSINESS_RULE );

        List<String> formats = new ArrayList<String>();
        formats.add( AssetFormats.BUSINESS_RULE );
        AssetPageRequest request = new AssetPageRequest( packageItem.getUUID(),
                                                         formats,
                                                         null,
                                                         0,
                                                         null );
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
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
        ServiceImplementation impl = getServiceImplementation();
        PackageItem packageItem = impl.getRulesRepository().createPackage( "testFindAssetPageUnregisteredAssetFormats",
                                                                           "testFindAssetPageUnregisteredAssetFormatsDescription" );
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
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage( request );

        assertEquals( 1,
                      response.getPageRowList().size() );
    }

    @Test
    public void testQuickFindAssetCaseInsensitiveFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "TESTTEXTRULE",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "tEsTtExTrUlE",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule",
                                                         false,
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
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

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "TESTTEXTRULE",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "tEsTtExTrUlE",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule",
                                                         false,
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        response = repositoryAssetService.quickFindAsset( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.queryFullText( request );

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

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "TESTTEXTRULE",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "tEsTtExTrUlE",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule",
                                                         false,
                                                         false,
                                                         0,
                                                         null );
        PageResponse<QueryPageRow> response;
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertTrue( response.getStartRowIndex() == 0 );
        assertTrue( response.getPageRowList().size() == 3 );
        assertTrue( response.isLastPage() );
    }

    @Test
    public void testQueryFullTextCaseInsensitivePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testTextSearch";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "qkfnd" );
        repositoryPackageService.createPackage( "testTextSearch",
                                                "for testing search." );

        impl.createNewRule( "testTextRule",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "TESTTEXTRULE",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        impl.createNewRule( "tEsTtExTrUlE",
                            "desc",
                            cat,
                            "testTextSearch",
                            AssetFormats.DRL );

        QueryPageRequest request = new QueryPageRequest( "testTextRule*",
                                                         false,
                                                         false,
                                                         0,
                                                         PAGE_SIZE );
        PageResponse<QueryPageRow> response;
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( PAGE_SIZE,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );

        request.setStartRowIndex( PAGE_SIZE );
        response = impl.queryFullText( request );

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( PAGE_SIZE,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertTrue( response.isLastPage() );
    }

}
