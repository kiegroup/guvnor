/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RangeIterator;
import javax.jcr.Session;

import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.server.security.MockIdentity;
import org.drools.guvnor.server.security.MockRoleBasedPermissionStore;
import org.drools.guvnor.server.security.RoleBasedPermission;
import org.drools.guvnor.server.security.RoleBasedPermissionManager;
import org.drools.guvnor.server.security.RoleBasedPermissionResolver;
import org.drools.guvnor.server.security.RoleType;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemPageResult;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gwt.user.client.rpc.SerializationException;

public class RepositoryCategoryOperationsTest extends GuvnorTestBase {

    private final RulesRepository              rulesRepository              = mock( RulesRepository.class );
    private final RepositoryCategoryOperations repositoryCategoryOperations = new RepositoryCategoryOperations();

    @Before
    public void setUp() {
        repositoryCategoryOperations.setRulesRepository( rulesRepository );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testLoadChildCategories() {
        CategoryItem categoryItem = mock( CategoryItem.class );
        when( rulesRepository.loadCategory( "categorypath" ) ).thenReturn( categoryItem );
        List childTags = new ArrayList();
        CategoryItem categoryItemNode = mock( CategoryItem.class );
        childTags.add( categoryItemNode );
        when( categoryItemNode.getName() ).thenReturn( "categoryNodeName" );
        when( categoryItem.getChildTags() ).thenReturn( childTags );
        assertArrayEquals( repositoryCategoryOperations.loadChildCategories( "categorypath" ),
                           new String[]{"categoryNodeName"} );
    }

    @Test
    public void testCreateCategoryWhenPathIncludingHtml() {
        testAndVerifyCreateCategory( "<path>",
                                     "&lt;path&gt;" );
    }

    @Test
    public void testCreateCategoryWhenPathIsNull() {
        testAndVerifyCreateCategory( null,
                                     "/" );
    }

    @Test
    public void testCreateCategoryWhenPathIsEmpty() {
        testAndVerifyCreateCategory( "",
                                     "/" );
    }

    public void testAndVerifyCreateCategory(final String createPath,
                                            final String loadPath) {
        initSession();
        CategoryItem categoryItem = mock( CategoryItem.class );
        when( rulesRepository.loadCategory( loadPath ) ).thenReturn( categoryItem );
        repositoryCategoryOperations.createCategory( createPath,
                                                     "name",
                                                     "description" );
        verify( rulesRepository ).loadCategory( loadPath );
        verify( categoryItem ).addCategory( "name",
                                            "description" );
    }

    @Test
    public void testRenameCategory() {
        repositoryCategoryOperations.renameCategory( "orig",
                                                     "new" );
        verify( rulesRepository ).renameCategory( "orig",
                                                  "new" );
    }

    @Test
    public void testLoadRuleListForCategories() throws SerializationException {
        CategoryPageRequest categoryPageRequest = new CategoryPageRequest( "/path",
                                                                           0,
                                                                           new Integer( 10 ) );
        RangeIterator rangeIterator = mock( RangeIterator.class );
        when( rangeIterator.hasNext() ).thenReturn( false );
        when( rangeIterator.getPosition() ).thenReturn( 1L );
        AssetItemPageResult assetItemPageResult = new AssetItemPageResult( Arrays.asList( mock( AssetItem.class,
                                                                                                Mockito.RETURNS_MOCKS ) ),
                                                                           1,
                                                                           false );
        when( rulesRepository.findAssetsByCategory( categoryPageRequest.getCategoryPath(),
                                                    false,
                                                    categoryPageRequest.getStartRowIndex(),
                                                    10 ) ).thenReturn( assetItemPageResult );
        PageResponse<CategoryPageRow> loadRuleListForCategories = repositoryCategoryOperations.loadRuleListForCategories( categoryPageRequest );
        assertNotNull( loadRuleListForCategories );
        assertEquals( loadRuleListForCategories.getStartRowIndex(),
                      categoryPageRequest.getStartRowIndex() );
        assertNotNull( loadRuleListForCategories.getPageRowList() );
        assertEquals( loadRuleListForCategories.getPageRowList().size(),
                      1 );
        assertEquals( loadRuleListForCategories.isLastPage(),
                      true );

    }
    
    @Test
    public void testLoadRuleListForCategoriesWithAnalystPermission() throws SerializationException {
        ServiceImplementation impl = getServiceImplementation();
        CategoryItem rootCategory = impl.getRulesRepository().loadCategory( "/" );
        CategoryItem cat = rootCategory.addCategory("testLoadRuleListForCategoriesWithAnalystPermissionRootCat", "description");
        cat.addCategory( "testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                         "yeah");
        cat.addCategory( "testLoadRuleListForCategoriesWithAnalystPermissionCat2",
        "yeah");
        
        // Mock up SEAM contexts for role base authorization
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
                                           "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat" ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );
        
        RepositoryCategoryService repositoryCategoryService = new RepositoryCategoryService();
        repositoryCategoryService.setRulesRepository(impl.getRulesRepository());

        //Create assets for test
        PackageItem pkg = impl.getRulesRepository().createPackage( "testLoadRuleListForCategoriesWithAnalystPermission",
        "" );

        AssetItem asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission1",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat/testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission2",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission3",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission4",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat/testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission5",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        
        //Now test
        CategoryPageRequest request = new CategoryPageRequest( "/testLoadRuleListForCategoriesWithAnalystPermissionRootCat",
                0,
                new Integer( 2 ) );       
        PageResponse<CategoryPageRow> response = repositoryCategoryService.loadRuleListForCategories(request);

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 2,
                      response.getPageRowList().size() );
        assertFalse( response.isLastPage() );
        
        request.setStartRowIndex( 2 );
        response = repositoryCategoryService.loadRuleListForCategories(request);

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 2,
                      response.getStartRowIndex() );
        assertEquals( 1,
                      response.getPageRowList().size() );
        assertEquals( true,
                response.isTotalRowSizeExact());
        assertEquals( 3,
                response.getTotalRowSize() );
        assertTrue( response.isLastPage() );
    }
    
    @Test
    public void testLoadRuleListForCategoriesWithAnalystNoRootCatPermission() throws SerializationException {
        ServiceImplementation impl = getServiceImplementation();
        CategoryItem rootCategory = impl.getRulesRepository().loadCategory( "/" );
        CategoryItem cat = rootCategory.addCategory("testLoadRuleListForCategoriesWithAnalystNoRootCatPermission", "description");
        cat.addCategory( "testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                         "yeah");
        cat.addCategory( "testLoadRuleListForCategoriesWithAnalystPermissionCat2",
        "yeah");
        
        // Mock up SEAM contexts for role base authorization
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
                                           "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission/testLoadRuleListForCategoriesWithAnalystPermissionCat1" ) );
        MockRoleBasedPermissionStore store = new MockRoleBasedPermissionStore( pbps );
        Contexts.getSessionContext().set( "org.drools.guvnor.server.security.RoleBasedPermissionStore",
                                          store );

        // Put permission list in session.
        RoleBasedPermissionManager testManager = new RoleBasedPermissionManager();
        testManager.create();
        Contexts.getSessionContext().set( "roleBasedPermissionManager",
                                          testManager );
        
        RepositoryCategoryService repositoryCategoryService = new RepositoryCategoryService();
        repositoryCategoryService.setRulesRepository(impl.getRulesRepository());

        //Create asset for test
        PackageItem pkg = impl.getRulesRepository().createPackage( "testLoadRuleListForCategoriesWithAnalystPermission",
        "" );

        AssetItem asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission1",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission/testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission2",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission3",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission4",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission/testLoadRuleListForCategoriesWithAnalystPermissionCat1",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        asset = pkg
                .addAsset(
                        "testLoadRuleListForCategoriesWithAnalystPermission5",
                        "",
                        "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                        null);
        asset.updateSubject("testMetaDataSearch");
        asset.updateExternalSource("numberwang");
        asset.checkin("");
        
        //Now test
        CategoryPageRequest request = new CategoryPageRequest( "/testLoadRuleListForCategoriesWithAnalystNoRootCatPermission",
                0,
                new Integer( 2 ) );       
        PageResponse<CategoryPageRow> response = repositoryCategoryService.loadRuleListForCategories(request);

        assertNotNull( response );
        assertNotNull( response.getPageRowList() );
        assertEquals( 0,
                      response.getStartRowIndex() );
        assertEquals( 0,
                      response.getPageRowList().size() );
        assertEquals( true,
                response.isTotalRowSizeExact());
        assertEquals( 0,
                response.getTotalRowSize() );
        assertTrue( response.isLastPage() );
    }
    
    @Test
    public void testRemoveCategory() throws SerializationException {
        initSession();
        CategoryItem categoryItem = mock( CategoryItem.class );
        when( rulesRepository.loadCategory( "/path" ) ).thenReturn( categoryItem );

        repositoryCategoryOperations.removeCategory( "/path" );
        verify( rulesRepository ).loadCategory( "/path" );
        verify( categoryItem ).remove();
        verify( rulesRepository ).save();
    }

    private void initSession() {
        Session session = mock( Session.class );
        when( this.rulesRepository.getSession() ).thenReturn( session );
    }
}
