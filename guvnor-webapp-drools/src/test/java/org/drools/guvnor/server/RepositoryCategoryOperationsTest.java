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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.RangeIterator;
import javax.jcr.Session;

import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemPageResult;
import org.drools.repository.CategoryItem;
import org.drools.repository.RulesRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gwt.user.client.rpc.SerializationException;

public class RepositoryCategoryOperationsTest {

    // TODO this entire test must be rewritten to extend GuvnorTestBase and test it for real

    private final RulesRepository              rulesRepository              = mock( RulesRepository.class );
    private final RepositoryCategoryOperations repositoryCategoryOperations = new RepositoryCategoryOperations();

    @Before
    public void setUp() {
        repositoryCategoryOperations.setRulesRepositoryForTest(rulesRepository);
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
