package org.drools.brms.server.contenthandler;

import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.server.util.BRLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class BRXMLContentHandler extends ContentHandler {
    

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleModel model = BRLPersistence.getInstance().toModel( item.getContent() );


        
        asset.content = model;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        RuleModel data = (RuleModel) asset.content;
        repoAsset.updateContent( BRLPersistence.getInstance().toXML( data ) );
    }
    

}
