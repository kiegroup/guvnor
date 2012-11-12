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

import org.drools.core.util.DateUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.type.DateFormatsImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RepositoryQueryAndFindIntegrationTest extends GuvnorIntegrationTest {

    @Test
    public void testQueryFullTextPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResultsPackageDescription";
        repositoryPackageService.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule1",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule1Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule2",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule2Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule3",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule3Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextPagedResults.testTextRule*",
                false,
                0,
                PAGE_SIZE);
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(PAGE_SIZE,
                response.getPageRowList().size());
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
        response = serviceImplementation.queryFullText(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(PAGE_SIZE,
                response.getStartRowIndex());
        assertEquals(1,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testQueryFullTextFullResults() throws Exception {

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResultsPackageDescription";
        repositoryPackageService.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule1",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule1Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule2",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule2Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule3",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule3Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextFullResults.testTextRule*",
                false,
                0,
                null);
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(3,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testQuickFindAssetPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResultsPackageDescription";
        repositoryPackageService.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule1",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule1Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule2",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule2Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule3",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule3Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetPagedResults.testTextRule*",
                false,
                0,
                PAGE_SIZE);
        PageResponse<QueryPageRow> response;

        response = repositoryAssetService.quickFindAsset(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(PAGE_SIZE,
                response.getPageRowList().size());
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
        response = serviceImplementation.queryFullText(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(PAGE_SIZE,
                response.getStartRowIndex());
        assertEquals(1,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testQuickFindAssetFullResults() throws Exception {

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResultsPackageDescription";
        repositoryPackageService.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule1",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule1Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule2",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule2Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule3",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule3Description",
                categoryName,
                packageName,
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetFullResults.testTextRule*",
                false,
                0,
                null);
        PageResponse<QueryPageRow> response;

        response = repositoryAssetService.quickFindAsset(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(3,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testQueryMetaDataPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResultsPackageDescription";
        ModuleItem pkg = rulesRepository.createModule(packageName,
                packageDescription);

        AssetItem[] assets = new AssetItem[3];
        for (int i = 0; i < assets.length; i++) {
            AssetItem asset = pkg.addAsset("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.testMetaDataSearchAsset" + i,
                    "");
            asset.updateSubject("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.testMetaDataSearch");
            asset.updateExternalSource("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.numberwang" + i);
            asset.checkin("");
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.wang, org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataPagedResults.numberwan*";

        List<MetaDataQuery> metadata = Arrays.asList(qr);
        QueryMetadataPageRequest request = new QueryMetadataPageRequest(metadata,
                DateUtils.parseDate("10-Jul-1974",
                        new DateFormatsImpl()),
                null,
                null,
                null,
                false,
                0,
                PAGE_SIZE);

        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryMetaData(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(PAGE_SIZE,
                response.getPageRowList().size());
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
        response = serviceImplementation.queryMetaData(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(PAGE_SIZE,
                response.getStartRowIndex());
        assertEquals(1,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testQueryMetaDataFullResults() throws Exception {

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResultsPackageDescription";
        ModuleItem pkg = rulesRepository.createModule(packageName,
                packageDescription);

        AssetItem[] assets = new AssetItem[3];
        for (int i = 0; i < assets.length; i++) {
            AssetItem asset = pkg.addAsset("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.testMetaDataSearchAsset" + i,
                    "");
            asset.updateSubject("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.testMetaDataSearch");
            asset.updateExternalSource("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.numberwang" + i);
            asset.checkin("");
        }

        MetaDataQuery[] qr = new MetaDataQuery[2];
        qr[0] = new MetaDataQuery();
        qr[0].attribute = AssetItem.SUBJECT_PROPERTY_NAME;
        qr[0].valueList = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.wang, org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.testMetaDataSearch";
        qr[1] = new MetaDataQuery();
        qr[1].attribute = AssetItem.SOURCE_PROPERTY_NAME;
        qr[1].valueList = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryMetaDataFullResults.numberwan*";

        List<MetaDataQuery> metadata = Arrays.asList(qr);
        QueryMetadataPageRequest request = new QueryMetadataPageRequest(metadata,
                DateUtils.parseDate("10-Jul-1974",
                        new DateFormatsImpl()),
                null,
                null,
                null,
                false,
                0,
                null);

        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryMetaData(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(3,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testFindAssetPagePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResultsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule1",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule1Description",
                categoryName,
                packageName,
                AssetFormats.BUSINESS_RULE);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule2",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule2Description",
                categoryName,
                packageName,
                AssetFormats.BUSINESS_RULE);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule3",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPagePagedResults.testTextRule3Description",
                categoryName,
                packageName,
                AssetFormats.BUSINESS_RULE);

        List<String> formats = new ArrayList<String>();
        formats.add(AssetFormats.BUSINESS_RULE);
        Path path = new PathImpl();
        path.setUUID(packageItem.getUUID());
        AssetPageRequest request = new AssetPageRequest(path,
                formats,
                null,
                0,
                PAGE_SIZE);

        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(PAGE_SIZE,
                response.getPageRowList().size());
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
        response = repositoryAssetService.findAssetPage(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(PAGE_SIZE,
                response.getStartRowIndex());
        assertEquals(1,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testFindAssetPageFullResults() throws Exception {

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResultsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule1",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule1Description",
                categoryName,
                packageName,
                AssetFormats.BUSINESS_RULE);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule2",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule2Description",
                categoryName,
                packageName,
                AssetFormats.BUSINESS_RULE);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule3",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageFullResults.testTextRule3Description",
                categoryName,
                packageName,
                AssetFormats.BUSINESS_RULE);

        List<String> formats = new ArrayList<String>();
        formats.add(AssetFormats.BUSINESS_RULE);
        Path path = new PathImpl();
        path.setUUID(packageItem.getUUID());
        AssetPageRequest request = new AssetPageRequest(path,
                formats,
                null,
                0,
                null);

        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(3,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testFindAssetPageUnregisteredAssetFormats() throws Exception {

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormatsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormatsPackageDescription";
        ModuleItem packageItem = rulesRepository.createModule(packageName,
                packageDescription);

        AssetItem as;
        as = packageItem.addAsset("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormats.assetWithKnownFormat",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormats.assetWithKnownFormatDescription");
        as.updateFormat(AssetFormats.DRL);
        as.checkin("");

        as = packageItem.addAsset("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormats.assetWithUnknownFormat",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testFindAssetPageUnregisteredAssetFormats.assetWithUnknownFormatDescription");
        as.updateFormat("something_silly");
        as.checkin("");

        List<String> formats = new ArrayList<String>();
        formats.add(AssetFormats.DRL);
        Path path = new PathImpl();
        path.setUUID(packageItem.getUUID());
        AssetPageRequest request = new AssetPageRequest(path,
                formats,
                null,
                0,
                null);

        PageResponse<AssetPageRow> response;
        response = repositoryAssetService.findAssetPage(request);

        assertEquals(1,
                response.getPageRowList().size());
    }

    @Test
    public void testQuickFindAssetCaseInsensitiveFullResults() throws Exception {

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResultsPackageDescription";
        repositoryPackageService.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRule",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRuleDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.TESTTEXTRULE",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.TESTTEXTRULEDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.tEsTtExTrUlE",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.tEsTtExTrUlEDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitiveFullResults.testTextRule",
                false,
                false,
                0,
                null);
        PageResponse<QueryPageRow> response;

        response = repositoryAssetService.quickFindAsset(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(3,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testQuickFindAssetCaseInsensitivePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResultsPackageDescription";
        repositoryPackageService.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRule",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRuleDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.TESTTEXTRULE",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.TESTTEXTRULEDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.tEsTtExTrUlE",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.tEsTtExTrUlEDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQuickFindAssetCaseInsensitivePagedResults.testTextRule",
                false,
                false,
                0,
                PAGE_SIZE);
        PageResponse<QueryPageRow> response;

        response = repositoryAssetService.quickFindAsset(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(PAGE_SIZE,
                response.getPageRowList().size());
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
        response = repositoryAssetService.quickFindAsset(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(PAGE_SIZE,
                response.getStartRowIndex());
        assertEquals(1,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testQueryFullTextCaseInsensitiveFullResults() throws Exception {

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResultsPackageDescription";
        repositoryPackageService.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.testTextRule",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.testTextRuleDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.TESTTEXTRULE",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.TESTTEXTRULEDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.tEsTtExTrUlE",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.tEsTtExTrUlEDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitiveFullResults.testTextRule",
                false,
                false,
                0,
                null);
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertTrue(response.getStartRowIndex() == 0);
        assertTrue(response.getPageRowList().size() == 3);
        assertTrue(response.isLastPage());
    }

    @Test
    public void testQueryFullTextCaseInsensitivePagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        String categoryName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResultsCategory";
        String categoryDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResultsCategoryDescription";
        repositoryCategoryService.createCategory("/",
                categoryName,
                categoryDescription);

        String packageName = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResultsPackage";
        String packageDescription = "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResultsPackageDescription";
        repositoryPackageService.createModule(packageName,
                packageDescription,
                "package");

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.testTextRule",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.testTextRuleDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.TESTTEXTRULE",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.TESTTEXTRULEDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        serviceImplementation.createNewRule("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.tEsTtExTrUlE",
                "org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.tEsTtExTrUlEDescription",
                categoryName,
                packageName,
                AssetFormats.DRL);

        QueryPageRequest request = new QueryPageRequest("org.kie.guvnor.server.RepositoryQueryAndFindIntegrationTest.testQueryFullTextCaseInsensitivePagedResults.testTextRule*",
                false,
                false,
                0,
                PAGE_SIZE);
        PageResponse<QueryPageRow> response;
        response = serviceImplementation.queryFullText(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(0,
                response.getStartRowIndex());
        assertEquals(PAGE_SIZE,
                response.getPageRowList().size());
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
        response = serviceImplementation.queryFullText(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertEquals(PAGE_SIZE,
                response.getStartRowIndex());
        assertEquals(1,
                response.getPageRowList().size());
        assertTrue(response.isLastPage());
    }

}
