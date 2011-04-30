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

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.repository.AssetItem;
import org.junit.Test;

public class RepositoryCategoryServiceTest extends GuvnorTestBase {

    @Test
    public void testRemoveCategory() throws Exception {

        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String[] children = repositoryCategoryService.loadChildCategories( "/" );
        repositoryCategoryService.createCategory( "/",
                                                  "testRemoveCategory",
                                                  "foo" );

        repositoryCategoryService.removeCategory( "testRemoveCategory" );
        String[] _children = repositoryCategoryService.loadChildCategories( "/" );
        assertEquals( children.length,
                      _children.length );

    }

    @Test
    public void testAddCategories() throws Exception {
        ServiceImplementation impl = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        impl.getRulesRepository().createPackage( "testAddCategoriesPackage",
                                                 "desc" );
        repositoryCategoryService.createCategory( "",
                                                  "testAddCategoriesCat1",
                                                  "this is a cat" );
        repositoryCategoryService.createCategory( "",
                                                  "testAddCategoriesCat2",
                                                  "this is a cat" );

        String uuid = impl.createNewRule( "testCreateNewRuleName",
                                          "an initial desc",
                                          "testAddCategoriesCat1",
                                          "testAddCategoriesPackage",
                                          AssetFormats.DSL_TEMPLATE_RULE );

        AssetItem dtItem = impl.getRulesRepository().loadAssetByUUID( uuid );
        dtItem.addCategory( "testAddCategoriesCat1" );
        impl.getRulesRepository().save();

        AssetItem dtItem1 = impl.getRulesRepository().loadAssetByUUID( uuid );
        assertEquals( 1,
                      dtItem1.getCategories().size() );
        assertTrue( dtItem1.getCategorySummary().contains( "testAddCategoriesCat1" ) );

        AssetItem dtItem2 = impl.getRulesRepository().loadAssetByUUID( uuid );
        dtItem2.addCategory( "testAddCategoriesCat2" );
        impl.getRulesRepository().save();

        AssetItem dtItem3 = impl.getRulesRepository().loadAssetByUUID( uuid );
        assertEquals( 2,
                      dtItem3.getCategories().size() );
        assertTrue( dtItem3.getCategorySummary().contains( "testAddCategoriesCat2" ) );
    }

    @Test
    public void testCategory() throws Exception {
        ServiceImplementation serviceImplementation = getServiceImplementation();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();

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

    }

    @Test
    public void testLoadRuleListForCategoryPagedResults() throws Exception {

        final int PAGE_SIZE = 2;

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testCategory";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "testCategoryDescription" );
        repositoryPackageService.createPackage( "testCategoryPackage",
                                                "testCategoryPackageDescription" );

        impl.createNewRule( "testTextRule1",
                            "testCategoryRule1",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );
        impl.createNewRule( "testTextRule2",
                            "testCategoryRule2",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );
        impl.createNewRule( "testTextRule3",
                            "testCategoryRule3",
                            cat,
                            "testCategoryPackage",
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
    }

    @Test
    public void testLoadRuleListForCategoryFullResults() throws Exception {

        ServiceImplementation impl = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryCategoryService repositoryCategoryService = getRepositoryCategoryService();
        String cat = "testCategory";
        repositoryCategoryService.createCategory( "/",
                                                  cat,
                                                  "testCategoryDescription" );
        repositoryPackageService.createPackage( "testCategoryPackage",
                                                "testCategoryPackageDescription" );

        impl.createNewRule( "testTextRule1",
                            "testCategoryRule1",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );
        impl.createNewRule( "testTextRule2",
                            "testCategoryRule2",
                            cat,
                            "testCategoryPackage",
                            AssetFormats.DRL );
        impl.createNewRule( "testTextRule3",
                            "testCategoryRule3",
                            cat,
                            "testCategoryPackage",
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
    }
}
