package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializationException;


/**
 * Default ones will store things as an attachment.
 */
public class DefaultContentHandler extends ContentHandler {

	@Override
	public void retrieveAssetContent(RuleAsset asset, PackageItem pkg,
			AssetItem item) throws SerializationException {
	}

	@Override
	public void storeAssetContent(RuleAsset asset, AssetItem repoAsset)
			throws SerializationException {
	}

}
