package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.slf4j.Logger;

import com.google.gwt.user.client.rpc.SerializableException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public abstract class BaseXStreamContentHandler<T extends PortableObject> extends ContentHandler {
	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	private static final XStream xt = new XStream(new DomDriver());

	protected XStream getXStream() {
		return xt;
	}

	@SuppressWarnings("unchecked")
	public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item) throws SerializableException {
		if (item.getContent() != null && item.getContent().length() > 0) {
			try {
				asset.content = (T) getXStream().fromXML(item.getContent());
			} catch (RulesRepositoryException e) {
				log.error("error marshalling asset content: " + asset.metaData.name, e);
				throw new SerializableException(e.getMessage());
			}
		} else {
			asset.content = new WorkingSetConfigData();
		}
	}

	public void storeAssetContent(RuleAsset asset, AssetItem repoAsset) throws SerializableException {
		try {
			repoAsset.updateContent(getXStream().toXML(asset.content));
		} catch (Exception e) {
			log.error("error marshalling asset content: " + asset.metaData.name, e);
			throw new SerializableException(e.getMessage());
		}
	}
}
