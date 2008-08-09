package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.ruleeditor.PropertiesHolder;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetItem;
import com.google.gwt.user.client.rpc.SerializableException;

/**
 *  TODO:
 */
public class PropertiesHandler extends ContentHandler {
    public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item)
            throws SerializableException {
        asset.content = new PropertiesHolder();
    }

    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset)
            throws SerializableException {
        PropertiesHolder holder = (PropertiesHolder) asset.content;
        repoAsset.updateContent(holder.toString());
    }
}