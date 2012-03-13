/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.contenthandler;

import com.google.gwt.user.client.rpc.SerializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepositoryException;
import org.slf4j.Logger;

import java.io.Serializable;

public abstract class BaseXStreamContentHandler<T extends Serializable> extends ContentHandler {
    private final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    private static final XStream xt = new XStream(new DomDriver());

    protected XStream getXStream() {
        return xt;
    }

    @SuppressWarnings("unchecked")
    public void retrieveAssetContent(Asset ruleAsset,
                                     AssetItem assetItem) throws SerializationException {
        if (assetItem.getContent() != null && assetItem.getContent().length() > 0) {
            try {
                ruleAsset.setContent((T) getXStream().fromXML(assetItem.getContent()));
            } catch (RulesRepositoryException e) {
                log.error("error marshalling asset content: " + ruleAsset.getName(),
                        e);
                throw new SerializationException(e.getMessage());
            }
        } else {
            ruleAsset.setContent(new WorkingSetConfigData());
        }
    }

    public void storeAssetContent(Asset ruleAsset,
                                  AssetItem assetItem) throws SerializationException {
        try {
            assetItem.updateContent(getXStream().toXML(ruleAsset.getContent()));
        } catch (Exception e) {
            log.error("error marshalling asset content: " + ruleAsset.getName(),
                    e);
            throw new SerializationException(e.getMessage());
        }
    }
}
