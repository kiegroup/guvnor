package org.drools.brms.server.contenthandler;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class ModelContentHandler extends ContentHandler {


    public void retrieveAssetContent(RuleAsset asset,
                                     AssetItem item) throws SerializableException {
        //do nothing, as we have an attachment
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        //do nothing, as we have an attachment
    }

}
