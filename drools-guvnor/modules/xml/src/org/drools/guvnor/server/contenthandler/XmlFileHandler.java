package org.drools.guvnor.server.contenthandler;

import com.google.gwt.user.client.rpc.SerializableException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

/**
 * @author Anton Arhipov
 */
public class XmlFileHandler extends ContentHandler {
    public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item)
            throws SerializableException {
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        asset.content = text;
    }

    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset)
            throws SerializableException {
        RuleContentText text = (RuleContentText) asset.content;
        repoAsset.updateContent(text.content);
    }
}
