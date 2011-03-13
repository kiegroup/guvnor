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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;

import org.drools.repository.CategoryItem;
import org.drools.repository.RulesRepository;
import org.junit.Before;
import org.junit.Test;

public class RepositoryCategoryOperationsTest {

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
    public void testCreateCategoryWhenIncludingHtml() {
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

    private void initSession() {
        Session session = mock( Session.class );
        when( this.rulesRepository.getSession() ).thenReturn( session );
    }
}
