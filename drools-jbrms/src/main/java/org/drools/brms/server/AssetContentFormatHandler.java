package org.drools.brms.server;


import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.server.util.BRLPersistence;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This takes care of the different asset types, based on the dublin core "format"
 * attribute.
 * 
 * This handles the loading/storing of these assets, as some of them require massaging 
 * before they are stored in the repository.
 * 
 * It uses the content attribute of asset nodes (drools:content) based
 * on the (drools:format) format key.
 * 
 * @see AssetFormats
 * 
 * @author Michael Neale
 */
public class AssetContentFormatHandler {

    /**
     * When loading asset content.
     * @param asset The target.
     * @param item The source.
     * @throws SerializableException
     */
    public void retrieveAssetContent(RuleAsset asset,
                                     AssetItem item) throws SerializableException {
        if (item.getFormat().equals( AssetFormats.DSL_TEMPLATE_RULE)) {
            //ok here is where we do DSLs...
            throw new SerializableException("Can't load DSL rules just yet.");

        } else if (item.getFormat().equals( AssetFormats.BUSINESS_RULE )) {             
            RuleModel model = BRLPersistence.getInstance().toModel( item.getContent() );
            asset.content = model;
        } else {
            //default to text, goode olde texte, just like mum used to make.
            RuleContentText text = new RuleContentText();
            text.content = item.getContent();
            asset.content = text;

        }
        

        
    }
    
    /**
     * For storing the asset content back into the repo node (any changes).
     * @param asset
     * @param repoAsset
     * @throws SerializableException
     */
    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset) throws SerializableException {
        if (asset.content instanceof RuleContentText) {
            repoAsset.updateContent( ((RuleContentText)asset.content).content );        
        } else if (asset.content instanceof RuleModel) {
            RuleModel model = (RuleModel) asset.content;
            repoAsset.updateContent( BRLPersistence.getInstance().toXML( model ) );
        } else {
            throw new SerializableException("Not able to handle that type of content just yet...");
        }
    }
    
    
    
    

}
