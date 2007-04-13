package org.drools.brms.server.contenthandler;

import org.drools.brms.client.common.AssetFormats;

import junit.framework.TestCase;

public class ContentHandlerTest extends TestCase {

    public void testContentFormat() {
        assertTrue(ContentHandler.getHandler( AssetFormats.DRL ) instanceof DRLFileContentHandler);
        assertTrue(ContentHandler.getHandler( AssetFormats.DSL ) instanceof DSLDefinitionContentHandler);
        assertTrue(ContentHandler.getHandler( AssetFormats.BUSINESS_RULE ) instanceof BRXMLContentHandler);

    }
    
}
