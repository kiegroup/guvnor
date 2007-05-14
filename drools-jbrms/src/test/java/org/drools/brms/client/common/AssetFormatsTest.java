package org.drools.brms.client.common;

import junit.framework.TestCase;

public class AssetFormatsTest extends TestCase {

    
    public void testGrouping() {
        String[] formats = AssetFormats.BUSINESS_RULE_FORMATS;
        for ( int i = 0; i < formats.length; i++ ) {
            String fmt = formats[i];
            if (! (fmt.equals( AssetFormats.BUSINESS_RULE )
                    ||
                    fmt.equals( AssetFormats.DECISION_SPREADSHEET_XLS )
                    ||
                    fmt.equals( AssetFormats.DSL_TEMPLATE_RULE )
            ) ) {
                fail("Incorrect grouping of business rules.");
            }
        }
    }
    
    public void testPackageDependencies() {
        assertFalse(AssetFormats.isPackageDependency(AssetFormats.BUSINESS_RULE));
        assertFalse(AssetFormats.isPackageDependency(AssetFormats.DRL));
        assertTrue(AssetFormats.isPackageDependency(AssetFormats.DSL));
        assertTrue(AssetFormats.isPackageDependency(AssetFormats.MODEL));
        assertTrue(AssetFormats.isPackageDependency(AssetFormats.FUNCTION));
    }
    
}
