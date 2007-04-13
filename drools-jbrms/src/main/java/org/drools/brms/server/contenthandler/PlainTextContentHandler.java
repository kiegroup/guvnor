package org.drools.brms.server.contenthandler;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public abstract class PlainTextContentHandler extends ContentHandler {


    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg, 
                                     AssetItem item) throws SerializableException {
        //default to text, goode olde texte, just like mum used to make.
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        asset.content = text;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        repoAsset.updateContent( ((RuleContentText)asset.content).content ); 

    }

}
