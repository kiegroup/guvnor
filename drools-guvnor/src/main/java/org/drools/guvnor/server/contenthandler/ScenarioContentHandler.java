package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.modeldriven.testing.Scenario;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.util.ScenarioXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class ScenarioContentHandler extends PlainTextContentHandler {

	@Override
	public void retrieveAssetContent(RuleAsset asset, PackageItem pkg,
			AssetItem item) throws SerializableException {
		Scenario sc = ScenarioXMLPersistence.getInstance().unmarshal(item.getContent());
		asset.content = sc;

	}

	@Override
	public void storeAssetContent(RuleAsset asset, AssetItem repoAsset)
			throws SerializableException {
		Scenario sc = (Scenario) asset.content;
		repoAsset.updateContent(ScenarioXMLPersistence.getInstance().marshal(sc));
	}

}
