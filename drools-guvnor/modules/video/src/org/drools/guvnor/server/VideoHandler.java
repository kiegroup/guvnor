package org.drools.guvnor.server;

import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetItem;
import com.google.gwt.user.client.rpc.SerializableException;

/**
 *
 */
public class VideoHandler extends ContentHandler {
    public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item)
            throws SerializableException {

    }

    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset)
            throws SerializableException {

    }
}
