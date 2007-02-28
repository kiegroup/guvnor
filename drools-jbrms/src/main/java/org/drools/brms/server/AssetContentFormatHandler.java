package org.drools.brms.server;


import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.DSLSentenceFragment;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.rpc.DSLRuleData;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.client.rpc.RuleModelData;
import org.drools.brms.server.contenthandler.ContentHandler;
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
 * NOTE: when enhancing this, don't forget to do the retrieve AND the store !
 * 
 * @see AssetFormats
 * 
 * NOTE: THIS IS NOT NEEDED... remove it when integration tests completeted.
 * 
 * @author Michael Neale
 */
public class AssetContentFormatHandler {//implements ContentHandler {

//    /* (non-Javadoc)
//     * @see org.drools.brms.server.FormatHandler#retrieveAssetContent(org.drools.brms.client.rpc.RuleAsset, org.drools.repository.AssetItem)
//     */
//    public void retrieveAssetContent(RuleAsset asset,
//                                     AssetItem item) throws SerializableException {
//        if (item.getFormat().equals( AssetFormats.BUSINESS_RULE )) {             
//            RuleModel model = BRLPersistence.getInstance().toModel( item.getContent() );
//
//            RuleModelData data = new RuleModelData();
//            data.model = model;
//            //TODO: replace with the code that loads it from a cache server side.
//            //otherwise it will look at the current package, and then work out the model from that.
//            data.completionEngine = getDummySuggestionEngine();
//            
//            asset.content = data;
//        } else if (item.getFormat().equals( AssetFormats.DSL_TEMPLATE_RULE )) {
//            RuleContentText text = new RuleContentText();
//            text.content = item.getContent();
//            
//            //TODO: make this read in the DSL files in the current package.
//            DSLRuleData data = getDummyDSLSuggestions();
//            data.text = text;
//            
//            asset.content = data;            
//        } else {
//            //default to text, goode olde texte, just like mum used to make.
//            RuleContentText text = new RuleContentText();
//            text.content = item.getContent();
//            asset.content = text;
//
//        }
//        
//
//        
//    }
//    
//
//
//
//    
//    /* (non-Javadoc)
//     * @see org.drools.brms.server.FormatHandler#storeAssetContent(org.drools.brms.client.rpc.RuleAsset, org.drools.repository.AssetItem)
//     */
//    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset) throws SerializableException {
//        if (asset.content instanceof RuleContentText) {
//            repoAsset.updateContent( ((RuleContentText)asset.content).content );        
//        } else if (asset.content instanceof RuleModelData) {
//            RuleModelData data = (RuleModelData) asset.content;
//            repoAsset.updateContent( BRLPersistence.getInstance().toXML( data.model ) );
//        } else if (asset.content instanceof DSLRuleData) {
//            DSLRuleData data = (DSLRuleData) asset.content;
//            repoAsset.updateContent( data.text.content );
//        } else if (asset.metaData.format.equals( AssetFormats.MODEL )) {
//            //do nothing, as we have an attachment
//        } else {
//            throw new SerializableException("Not able to handle that type of content just yet...");
//        }
//    }
    
    
    
    

}
