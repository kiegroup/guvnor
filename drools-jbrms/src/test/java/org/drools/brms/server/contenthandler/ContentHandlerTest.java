package org.drools.brms.server.contenthandler;

import org.drools.brms.client.common.AssetFormats;

import junit.framework.TestCase;

public class ContentHandlerTest extends TestCase {

    public void testContentFormat() {
        assertTrue(ContentHandler.getHandler( AssetFormats.DRL ) instanceof DRLFileContentHandler);
        assertTrue(ContentHandler.getHandler( AssetFormats.DSL ) instanceof DSLDefinitionContentHandler);
        assertTrue(ContentHandler.getHandler( AssetFormats.DSL_TEMPLATE_RULE ) instanceof DSLRuleContentHandler);
        assertTrue(ContentHandler.getHandler( AssetFormats.BUSINESS_RULE ) instanceof BRXMLContentHandler);
        
        try {
            ContentHandler.getHandler( "XXX" );
            fail("should have thrown an exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }        
    }
    
    public void testRuleAssetType() {
        assertTrue(ContentHandler.getHandler( AssetFormats.DRL ).isRuleAsset());
        assertTrue(ContentHandler.getHandler( AssetFormats.DSL_TEMPLATE_RULE ).isRuleAsset());
        assertTrue(ContentHandler.getHandler( AssetFormats.BUSINESS_RULE ).isRuleAsset());
        
        assertFalse(ContentHandler.getHandler( AssetFormats.DSL ).isRuleAsset());
        assertFalse(ContentHandler.getHandler( AssetFormats.MODEL ).isRuleAsset());
    }
    
}
