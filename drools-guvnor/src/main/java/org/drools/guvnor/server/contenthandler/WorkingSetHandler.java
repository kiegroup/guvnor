package org.drools.guvnor.server.contenthandler;

import org.apache.log4j.spi.LoggerFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.slf4j.Logger;

import com.google.gwt.user.client.rpc.SerializableException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *
 */
public class WorkingSetHandler extends ContentHandler {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(WorkingSetHandler.class);
	
	private static final XStream xt = new XStream( new DomDriver() );
	
    public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item)
            throws SerializableException {
        if (item.getContent() != null && item.getContent().length() > 0) {
            try {
				asset.content = (WorkingSetConfigData) xt.fromXML(item.getContent());
			} catch (RulesRepositoryException e) {
				log.error("error marshalling working set: " + asset.metaData.name, e);
				throw new SerializableException(e.getMessage());
			}
        } else {
        	asset.content = new WorkingSetConfigData();
        }
    }

    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset) throws SerializableException {
    	try {
			WorkingSetConfigData wsData = (WorkingSetConfigData) asset.content;
			repoAsset.updateContent(xt.toXML(wsData));
		} catch (Exception e) {
			log.error("error marshalling working set: " + asset.metaData.name, e);
			throw new SerializableException(e.getMessage());
		}
    }
}
