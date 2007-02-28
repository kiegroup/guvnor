package org.drools.brms.server.contenthandler;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepositoryException;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * All content handlers must implement this, and be registered in content_types.properties
 * @author Michael Neale
 *
 */
public abstract class ContentHandler {

    static Map handlers;
    
    static {
        handlers = new HashMap() {{
            put(AssetFormats.BUSINESS_RULE, new BRXMLContentHandler());
            put(AssetFormats.DSL_TEMPLATE_RULE, new DSLRuleContentHandler());
            put(AssetFormats.DRL, new PlainTextContentHandler());
            put(AssetFormats.DSL, new PlainTextContentHandler());
            put(AssetFormats.FUNCTION, new PlainTextContentHandler());
            put(AssetFormats.MODEL, new ModelContentHandler());
            put(AssetFormats.TECHNICAL_RULE, new PlainTextContentHandler());
            
        }};        
    }
    
    /**
     * When loading asset content.
     * @param asset The target.
     * @param item The source.
     * @throws SerializableException
     */
    public abstract void retrieveAssetContent(RuleAsset asset,
                                              AssetItem item) throws SerializableException;

    /**
     * For storing the asset content back into the repo node (any changes).
     * @param asset
     * @param repoAsset
     * @throws SerializableException
     */
    public abstract void storeAssetContent(RuleAsset asset,
                                           AssetItem repoAsset) throws SerializableException;

    
    public static ContentHandler getHandler(String format) {
        
        ContentHandler h =  (ContentHandler) handlers.get( format );
        if (h == null) throw new IllegalArgumentException("Unable to handle the content type: " + format);
        return h;
    }
    
}