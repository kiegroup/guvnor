/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.slf4j.Logger;

import com.google.gwt.user.client.rpc.SerializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public abstract class BaseXStreamContentHandler<T extends PortableObject> extends ContentHandler {
	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	private static final XStream xt = new XStream(new DomDriver());

	protected XStream getXStream() {
		return xt;
	}

	@SuppressWarnings("unchecked")
	public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item) throws SerializationException {
		if (item.getContent() != null && item.getContent().length() > 0) {
			try {
				asset.content = (T) getXStream().fromXML(item.getContent());
			} catch (RulesRepositoryException e) {
				log.error("error marshalling asset content: " + asset.metaData.name, e);
				throw new SerializationException(e.getMessage());
			}
		} else {
			asset.content = new WorkingSetConfigData();
		}
	}

	public void storeAssetContent(RuleAsset asset, AssetItem repoAsset) throws SerializationException {
		try {
			repoAsset.updateContent(getXStream().toXML(asset.content));
		} catch (Exception e) {
			log.error("error marshalling asset content: " + asset.metaData.name, e);
			throw new SerializationException(e.getMessage());
		}
	}
}
