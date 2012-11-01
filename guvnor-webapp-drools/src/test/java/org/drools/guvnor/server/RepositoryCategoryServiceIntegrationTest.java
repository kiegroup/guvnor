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

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.repository.AssetItem;
import org.junit.Test;
import org.drools.guvnor.client.rpc.Path;

import javax.inject.Inject;

import static org.junit.Assert.*;

public class RepositoryCategoryServiceIntegrationTest extends GuvnorIntegrationTest {

    public RepositoryCategoryServiceIntegrationTest() {
        //Some tests control their own login\logout life-cycle
        autoLoginAsAdmin = false;
    }

    @Test
    public void testRemoveCategory() throws Exception {
        loginAs(ADMIN_USERNAME);

        String[] children = repositoryCategoryService.loadChildCategories("/");
        repositoryCategoryService.createCategory("/",
                "testRemoveCategory",
                "foo");

        repositoryCategoryService.removeCategory("testRemoveCategory");
        String[] _children = repositoryCategoryService.loadChildCategories("/");
        assertEquals(children.length,
                _children.length);

        logoutAs(ADMIN_USERNAME);
    }

    @Test
    public void testAddCategories() throws Exception {
        loginAs(ADMIN_USERNAME);

        rulesRepository.createModule("testAddCategoriesPackage",
                "desc");
        repositoryCategoryService.createCategory("",
                "testAddCategoriesCat1",
                "this is a cat");
        repositoryCategoryService.createCategory("",
                "testAddCategoriesCat2",
                "this is a cat");

        Path uuid = serviceImplementation.createNewRule("testCreateNewRuleName",
                "an initial desc",
                "testAddCategoriesCat1",
                "testAddCategoriesPackage",
                AssetFormats.DSL_TEMPLATE_RULE);

        AssetItem dtItem = rulesRepository.loadAssetByUUID(uuid.getUUID());
        dtItem.addCategory("testAddCategoriesCat1");
        rulesRepository.save();

        AssetItem dtItem1 = rulesRepository.loadAssetByUUID(uuid.getUUID());
        assertEquals(1,
                dtItem1.getCategories().size());
        assertTrue(dtItem1.getCategorySummary().contains("testAddCategoriesCat1"));

        AssetItem dtItem2 = rulesRepository.loadAssetByUUID(uuid.getUUID());
        dtItem2.addCategory("testAddCategoriesCat2");
        rulesRepository.save();

        AssetItem dtItem3 = rulesRepository.loadAssetByUUID(uuid.getUUID());
        assertEquals(2,
                dtItem3.getCategories().size());
        assertTrue(dtItem3.getCategorySummary().contains("testAddCategoriesCat2"));

        logoutAs(ADMIN_USERNAME);
    }

    @Test
    public void testLoadRuleListForCategoryPagedResultsCategory() throws Exception {
        loginAs(ADMIN_USERNAME);

        String[] originalCats = repositoryCategoryService.loadChildCategories("/");

        Boolean result = repositoryCategoryService.createCategory("/",
                "TopLevel1",
                "a description");
        assertTrue(result.booleanValue());

        result = repositoryCategoryService.createCategory("/",
                "TopLevel2",
                "a description");
        assertTrue(result.booleanValue());

        String[] cats = repositoryCategoryService.loadChildCategories("/");
        assertTrue(cats.length == originalCats.length + 2);

        result = repositoryCategoryService.createCategory("",
                "Top3",
                "description");
        assertTrue(result.booleanValue());

        result = repositoryCategoryService.createCategory(null,
                "Top4",
                "description");
        assertTrue(result.booleanValue());

        logoutAs(ADMIN_USERNAME);
    }

    @Test
    public void testLoadRuleListForCategoryPagedResults() throws Exception {
        loginAs(ADMIN_USERNAME);

        final int PAGE_SIZE = 2;

        String cat = "testLoadRuleListForCategoryPagedResultsCategory";
        repositoryCategoryService.createCategory("/",
                cat,
                "testCategoryDescription");
        repositoryPackageService.createModule("testLoadRuleListForCategoryPagedResultsCategoryPackage",
                "testLoadRuleListForCategoryPagedResultsCategoryPackageDescription",
                "package");

        serviceImplementation.createNewRule("testTextRule1",
                "testLoadRuleListForCategoryPagedResultsCategoryRule1",
                cat,
                "testLoadRuleListForCategoryPagedResultsCategoryPackage",
                AssetFormats.DRL);
        serviceImplementation.createNewRule("testTextRule2",
                "testLoadRuleListForCategoryPagedResultsCategoryRule2",
                cat,
                "testLoadRuleListForCategoryPagedResultsCategoryPackage",
                AssetFormats.DRL);
        serviceImplementation.createNewRule("testTextRule3",
                "testLoadRuleListForCategoryPagedResultsCategoryRule3",
                cat,
                "testLoadRuleListForCategoryPagedResultsCategoryPackage",
                AssetFormats.DRL);

        CategoryPageRequest request = new CategoryPageRequest(cat,
                0,
                PAGE_SIZE);
        PageResponse<CategoryPageRow> response;
        response = repositoryCategoryService.loadRuleListForCategories(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertTrue(response.getStartRowIndex() == 0);
        assertTrue(response.getPageRowList().size() == PAGE_SIZE);
        assertFalse(response.isLastPage());

        request.setStartRowIndex(PAGE_SIZE);
        response = repositoryCategoryService.loadRuleListForCategories(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertTrue(response.getStartRowIndex() == PAGE_SIZE);
        assertTrue(response.getPageRowList().size() == 1);
        assertTrue(response.isLastPage());

        logoutAs(ADMIN_USERNAME);
    }

    @Test
    public void testLoadRuleListForCategoryFullResults() throws Exception {
        loginAs(ADMIN_USERNAME);

        String cat = "testLoadRuleListForCategoryFullResultsCategory";
        repositoryCategoryService.createCategory("/",
                cat,
                "testLoadRuleListForCategoryFullResultsCategoryDescription");
        repositoryPackageService.createModule("testLoadRuleListForCategoryFullResultsCategoryPackage",
                "testLoadRuleListForCategoryFullResultsCategoryPackageDescription",
                "package");

        serviceImplementation.createNewRule("testTextRule1",
                "testLoadRuleListForCategoryFullResultsCategoryRule1",
                cat,
                "testLoadRuleListForCategoryFullResultsCategoryPackage",
                AssetFormats.DRL);
        serviceImplementation.createNewRule("testTextRule2",
                "testLoadRuleListForCategoryFullResultsCategoryRule2",
                cat,
                "testLoadRuleListForCategoryFullResultsCategoryPackage",
                AssetFormats.DRL);
        serviceImplementation.createNewRule("testTextRule3",
                "testLoadRuleListForCategoryFullResultsCategoryRule3",
                cat,
                "testLoadRuleListForCategoryFullResultsCategoryPackage",
                AssetFormats.DRL);

        CategoryPageRequest request = new CategoryPageRequest(cat,
                0,
                null);
        PageResponse<CategoryPageRow> response;
        response = repositoryCategoryService.loadRuleListForCategories(request);

        assertNotNull(response);
        assertNotNull(response.getPageRowList());
        assertTrue(response.getStartRowIndex() == 0);
        assertTrue(response.getPageRowList().size() == 3);
        assertTrue(response.isLastPage());

        logoutAs(ADMIN_USERNAME);
    }

}
