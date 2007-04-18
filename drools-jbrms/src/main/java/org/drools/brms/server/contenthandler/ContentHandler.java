package org.drools.brms.server.contenthandler;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

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
            put(AssetFormats.DRL, new DRLFileContentHandler());
            put(AssetFormats.DSL, new DSLDefinitionContentHandler());
            put(AssetFormats.FUNCTION, new FunctionContentHandler());
            put(AssetFormats.MODEL, new ModelContentHandler());
            
        }};        
    }
    
    /**
     * When loading asset content.
     * @param asset The target.
     * @param item The source.
     * @throws SerializableException
     */
    public abstract void retrieveAssetContent(RuleAsset asset,
                                              PackageItem pkg, 
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
    
    /** 
     * @return true if the current content type is for a rule asset.
     * If it is a rule asset, then it can be assembled into a package. 
     * If its not, then it is there, nominally to support compiling or 
     * validation/testing of the package (eg a model, or a dsl file).
     */
    public boolean isRuleAsset() {
        return this instanceof IRuleAsset;
    }
    
}