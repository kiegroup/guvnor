package org.drools.brms.server.contenthandler;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * Default ones will store things as an attachment.
 */
public class DefaultContentHandler extends ContentHandler {

	@Override
	public void retrieveAssetContent(RuleAsset asset, PackageItem pkg,
			AssetItem item) throws SerializableException {
	}

	@Override
	public void storeAssetContent(RuleAsset asset, AssetItem repoAsset)
			throws SerializableException {
	}

}
