package org.drools.brms.server.contenthandler;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class DSLRuleContentHandler extends ContentHandler {


    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        

        asset.content = text;
        
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {

        RuleContentText text = (RuleContentText) asset.content;
        repoAsset.updateContent( text.content );

    }
    

}
