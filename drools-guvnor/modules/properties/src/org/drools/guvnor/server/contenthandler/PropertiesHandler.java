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