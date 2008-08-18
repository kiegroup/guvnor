package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetItem;
import com.google.gwt.user.client.rpc.SerializableException;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 */
public class XmlFileHandler extends PlainTextContentHandler {
   /* public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item)
            throws SerializableException {
        if (item.getContent() != null) {
            asset.content = null; //PropertiesPersistence.getInstance().unmarshal(item.getContent());
        }
    }

    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset) throws SerializableException {
        try {
            InputStream input = new ByteArrayInputStream("xml".getBytes("UTF-8"));
            repoAsset.updateBinaryContentAttachment(input);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);     //TODO: ?
        }
    }*/
}
