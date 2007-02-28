package org.drools.brms.server;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.server.contenthandler.BRXMLContentHandler;
import org.drools.brms.server.contenthandler.ContentHandler;
import org.drools.brms.server.contenthandler.DSLRuleContentHandler;
import org.drools.brms.server.contenthandler.PlainTextContentHandler;

import junit.framework.TestCase;

public class ContentHandlerTest extends TestCase {

    
    public void testContentHandlerCreate() {
        assertTrue(ContentHandler.getHandler( AssetFormats.DRL ) instanceof PlainTextContentHandler);
        assertTrue(ContentHandler.getHandler( AssetFormats.DSL_TEMPLATE_RULE ) instanceof DSLRuleContentHandler);
        assertTrue(ContentHandler.getHandler( AssetFormats.BUSINESS_RULE ) instanceof BRXMLContentHandler);
        try {
            ContentHandler.getHandler( "XXX" );
            fail("should have thrown an exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
}
