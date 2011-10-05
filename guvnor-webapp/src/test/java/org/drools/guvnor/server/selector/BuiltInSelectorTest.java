/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.selector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.junit.Test;

public class BuiltInSelectorTest extends GuvnorTestBase {

    @Test
    public void testBuiltInSelector() throws Exception {
        rulesRepository.loadDefaultPackage();
        rulesRepository.createPackage( "testBuiltInSelectorPackage",
                                                 "woot" );
        rulesRepository.createState( "Dev" );
        rulesRepository.createState( "QA" );

        CategoryItem cat = rulesRepository.loadCategory( "/" );
        cat.addCategory( "testBuiltInSelectorCat1",
                         "yeah" );
        cat.addCategory( "testBuiltInSelectorCat2",
                         "yeah" );

        String uuid1 = serviceImplementation.createNewRule( "test AddRule1",
                                           "a description",
                                           "testBuiltInSelectorCat1",
                                           "testBuiltInSelectorPackage",
                                           "txt" );
        AssetItem item1 = rulesRepository.loadAssetByUUID( uuid1 );
        item1.updateState( "Dev" );
        String uuid2 = serviceImplementation.createNewRule( "test AddRule2",
                                           "a description",
                                           "testBuiltInSelectorCat1",
                                           "testBuiltInSelectorPackage",
                                           "txt" );
        AssetItem item2 = rulesRepository.loadAssetByUUID( uuid2 );
        item2.updateState( "QA" );
        String uuid3 = serviceImplementation.createNewRule( "test AddRule3",
                                           "a description",
                                           "testBuiltInSelectorCat2",
                                           "testBuiltInSelectorPackage",
                                           "txt" );
        AssetItem item3 = rulesRepository.loadAssetByUUID( uuid3 );
        item3.updateState( "Dev" );
        String uuid4 = serviceImplementation.createNewRule( "test AddRule4",
                                           "a description",
                                           "testBuiltInSelectorCat2",
                                           "testBuiltInSelectorPackage",
                                           "txt" );
        AssetItem item4 = rulesRepository.loadAssetByUUID( uuid4 );
        item4.updateState( "QA" );

        //Select asset using "category =" 
        BuiltInSelector selector1 = (BuiltInSelector) SelectorManager.getInstance().getSelector( "BuiltInSelector" );
        selector1.setCategory( "testBuiltInSelectorCat1" );
        selector1.setCategoryOperator( "=" );
        selector1.setEnableCategorySelector( true );
        selector1.setStatus( "Dev" );
        selector1.setStatusOperator( "=" );
        selector1.setEnableStatusSelector( false );

        assertTrue( selector1.isAssetAllowed( item1 ) );
        assertTrue( selector1.isAssetAllowed( item2 ) );
        assertFalse( selector1.isAssetAllowed( item3 ) );
        assertFalse( selector1.isAssetAllowed( item4 ) );

        //Select asset using "category !=" 
        BuiltInSelector selector2 = (BuiltInSelector) SelectorManager.getInstance().getSelector( "BuiltInSelector" );
        selector2.setCategory( "testBuiltInSelectorCat1" );
        selector2.setCategoryOperator( "!=" );
        selector2.setEnableCategorySelector( true );
        selector2.setStatus( "Dev" );
        selector2.setStatusOperator( "=" );
        selector2.setEnableStatusSelector( false );

        assertFalse( selector2.isAssetAllowed( item1 ) );
        assertFalse( selector2.isAssetAllowed( item2 ) );
        assertTrue( selector2.isAssetAllowed( item3 ) );
        assertTrue( selector2.isAssetAllowed( item4 ) );

        //Select asset using "status =" 
        BuiltInSelector selector3 = (BuiltInSelector) SelectorManager.getInstance().getSelector( "BuiltInSelector" );
        selector3.setCategory( "testBuiltInSelectorCat1" );
        selector3.setCategoryOperator( "!=" );
        selector3.setEnableCategorySelector( false );
        selector3.setStatus( "Dev" );
        selector3.setStatusOperator( "=" );
        selector3.setEnableStatusSelector( true );

        assertTrue( selector3.isAssetAllowed( item1 ) );
        assertFalse( selector3.isAssetAllowed( item2 ) );
        assertTrue( selector3.isAssetAllowed( item3 ) );
        assertFalse( selector3.isAssetAllowed( item4 ) );

        //Select asset using "status !=" 
        BuiltInSelector selector4 = (BuiltInSelector) SelectorManager.getInstance().getSelector( "BuiltInSelector" );
        selector4.setCategory( "testBuiltInSelectorCat1" );
        selector4.setCategoryOperator( "!=" );
        selector4.setEnableCategorySelector( false );
        selector4.setStatus( "Dev" );
        selector4.setStatusOperator( "!=" );
        selector4.setEnableStatusSelector( true );

        assertFalse( selector4.isAssetAllowed( item1 ) );
        assertTrue( selector4.isAssetAllowed( item2 ) );
        assertFalse( selector4.isAssetAllowed( item3 ) );
        assertTrue( selector4.isAssetAllowed( item4 ) );

        //Select asset using "status =" AND "category ="
        BuiltInSelector selector5 = (BuiltInSelector) SelectorManager.getInstance().getSelector( "BuiltInSelector" );
        selector5.setCategory( "testBuiltInSelectorCat1" );
        selector5.setCategoryOperator( "=" );
        selector5.setEnableCategorySelector( true );
        selector5.setStatus( "Dev" );
        selector5.setStatusOperator( "=" );
        selector5.setEnableStatusSelector( true );

        assertTrue( selector5.isAssetAllowed( item1 ) );
        assertFalse( selector5.isAssetAllowed( item2 ) );
        assertFalse( selector5.isAssetAllowed( item3 ) );
        assertFalse( selector5.isAssetAllowed( item4 ) );

        //Select asset using "status =" AND "category !="
        BuiltInSelector selector6 = (BuiltInSelector) SelectorManager.getInstance().getSelector( "BuiltInSelector" );
        selector6.setCategory( "testBuiltInSelectorCat1" );
        selector6.setCategoryOperator( "!=" );
        selector6.setEnableCategorySelector( true );
        selector6.setStatus( "Dev" );
        selector6.setStatusOperator( "=" );
        selector6.setEnableStatusSelector( true );

        assertFalse( selector6.isAssetAllowed( item1 ) );
        assertFalse( selector6.isAssetAllowed( item2 ) );
        assertTrue( selector6.isAssetAllowed( item3 ) );
        assertFalse( selector6.isAssetAllowed( item4 ) );

        //Allow everything if both selectors are disabled
        BuiltInSelector selector7 = (BuiltInSelector) SelectorManager.getInstance().getSelector( "BuiltInSelector" );
        selector7.setCategory( "testBuiltInSelectorCat1" );
        selector7.setCategoryOperator( "!=" );
        selector7.setEnableCategorySelector( false );
        selector7.setStatus( "Dev" );
        selector7.setStatusOperator( "=" );
        selector7.setEnableStatusSelector( false );

        assertTrue( selector6.isAssetAllowed( item1 ) );
        assertTrue( selector6.isAssetAllowed( item2 ) );
        assertTrue( selector6.isAssetAllowed( item3 ) );
        assertTrue( selector6.isAssetAllowed( item4 ) );
    }

}
