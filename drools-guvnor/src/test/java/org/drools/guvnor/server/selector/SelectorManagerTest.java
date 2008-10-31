package org.drools.guvnor.server.selector;

import junit.framework.TestCase;

public class SelectorManagerTest extends TestCase {

    public void testSelectorMangerConfig() {
        SelectorManager sm = SelectorManager.getInstance();
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

}
