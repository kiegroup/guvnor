package org.drools.guvnor.server.contenthandler;
/*
 * Copyright 2005 JBoss Inc
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

import com.google.gwt.user.client.rpc.SerializableException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.PropertiesHolder;
import org.drools.guvnor.server.util.PropertiesPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Handle *.properties file as a content for rule asset instead of a binary attachment
 *
 * @author Anton Arhipov
 */
public class PropertiesHandler extends ContentHandler {
    public void retrieveAssetContent(RuleAsset asset, PackageItem pkg, AssetItem item)
            throws SerializableException {
        if (item.getContent() != null) {
            asset.content = PropertiesPersistence.getInstance().unmarshal(item.getContent());
        }
    }

    public void storeAssetContent(RuleAsset asset, AssetItem repoAsset)
            throws SerializableException {
        PropertiesHolder holder = (PropertiesHolder) asset.content;
        String toSave = PropertiesPersistence.getInstance().marshal(holder);

        try {
            InputStream input = new ByteArrayInputStream(toSave.getBytes("UTF-8"));
            repoAsset.updateBinaryContentAttachment(input);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);     //TODO: ?
        }

    }
}