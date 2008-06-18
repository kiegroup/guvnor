package org.drools.brms.server.contenthandler;
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



import java.util.Map;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * All content handlers must implement this, and be registered in content_types.properties
 * @author Michael Neale
 *
 */
public abstract class ContentHandler {

    /**
     * When loading asset content.
     * @param asset The target.
     * @param item The source.
     * @throws SerializableException
     */
    public abstract void retrieveAssetContent(RuleAsset asset,
                                              PackageItem pkg,
                                              AssetItem item) throws SerializableException;

    /**
     * For storing the asset content back into the repo node (any changes).
     * @param asset
     * @param repoAsset
     * @throws SerializableException
     */
    public abstract void storeAssetContent(RuleAsset asset,
                                           AssetItem repoAsset) throws SerializableException;

    /**
     * @return true if the current content type is for a rule asset.
     * If it is a rule asset, then it can be assembled into a package.
     * If its not, then it is there, nominally to support compiling or
     * validation/testing of the package (eg a model, or a dsl file).
     */
    public boolean isRuleAsset() {
        return this instanceof IRuleAsset;
    }

}