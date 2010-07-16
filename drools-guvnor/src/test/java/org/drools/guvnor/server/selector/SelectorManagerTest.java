/**
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

import junit.framework.TestCase;

public class SelectorManagerTest extends TestCase {

    public void testSelectorMangerConfig() {
    	SelectorManager sm = new SelectorManager("/selectors-test.properties");
        assertNotNull(sm);
        assertNotNull(sm.selectors);

        assertNull(sm.getSelector( "goo" ));
        assertNotNull(sm.getSelector( "selector1" ));
        assertTrue(sm.getSelector( "selector1" ) instanceof TestSelector);
        assertNotNull(sm.getSelector( "selector2" ));
        assertTrue(sm.getSelector( "selector2" ) instanceof RuleBasedSelector);

        RuleBasedSelector sel = (RuleBasedSelector) sm.getSelector( "selector2" );
        assertEquals("/TestSelector.drl", sel.ruleFile);

        assertFalse(sel.evalRules( new DummyClass() ));
        assertTrue(sel.evalRules( new Allow() ));
        assertFalse(sel.evalRules( new DummyClass() ));
        assertTrue(sel.evalRules( new Allow() ));


        assertNull(sm.getSelector( "selector3" ));

        assertNotNull(sm.getSelector( "" ));
        assertNotNull(sm.getSelector( null ));
        AssetSelector nil = sm.getSelector( null );
        assertTrue(nil.isAssetAllowed( null ));


        sm = new SelectorManager("/emptyselectors.properties");

        assertNull(sm.getSelector( "XX" ));
        assertNotNull(sm.getSelector( null ));

        nil = sm.getSelector( " " );
        assertTrue(nil.isAssetAllowed( null ));


        assertSame( SelectorManager.getInstance(), SelectorManager.getInstance());
    }

    public void testGetBuiltInSelector() {
    	SelectorManager sm = new SelectorManager("/selectors-test.properties");
        assertNotNull(sm);
        assertNotNull(sm.selectors);

        assertTrue(sm.getSelector( "BuiltInSelector" ) instanceof BuiltInSelector);
    }
    
    public void testGetCustomSelectors() {
    	SelectorManager sm = new SelectorManager("/selectors-test.properties");
        assertNotNull(sm);
        assertNotNull(sm.selectors);

        assertEquals(2, sm.getCustomSelectors().length);
    }
    
    public void testBadConfig() throws Exception {
        SelectorManager sm = new SelectorManager("/badselectors.properties");

    }

}
