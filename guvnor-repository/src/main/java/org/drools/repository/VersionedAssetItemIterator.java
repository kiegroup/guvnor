/*
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

package org.drools.repository;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.NodeIterator;


/**
 * This iterates over nodes and produces RuleItem's.
 * Also allows "skipping" of results to jump to certain items,
 * as per JCRs "skip".
 *
 * JCR iterators are/can be lazy, so this makes the most of it for large
 * numbers of assets.
 */
public class VersionedAssetItemIterator extends AssetItemIterator {
    Map<String, String> dependencyVersionMap = new HashMap<String, String>();
    private boolean enableGetHistoricalVersionBasedOnDependency = false;
    
    public VersionedAssetItemIterator(NodeIterator nodes,
                            RulesRepository repo,
                            String[] dependencies) {
        super(nodes, repo);
        //this.dependencies = dependencies;
        for(String dependency : dependencies) {
            String[] decodedPath = PackageItem.decodeDependencyPath(dependency);
            if(!"LATEST".equals(decodedPath[1])) {
                dependencyVersionMap.put(PackageItem.parseDependencyAssetName(decodedPath[0]), decodedPath[1]);
            }
        }        
    }

    public AssetItem next() {
        AssetItem ai = super.next();
        if(enableGetHistoricalVersionBasedOnDependency && dependencyVersionMap.get(ai.getName()) != null) {
            String version = dependencyVersionMap.get(ai.getName());
            AssetItem historicalAsset = loadAssetWithVersion(ai, version);
            if(historicalAsset !=null) {
                return historicalAsset;
            }
        }
        return ai;
    }
    
    public void setEnableGetHistoricalVersionBasedOnDependency(boolean flag) {
        this.enableGetHistoricalVersionBasedOnDependency = flag;
    }
    
    protected AssetItem loadAssetWithVersion(final AssetItem assetItem,
            String version) {
        AssetHistoryIterator it = assetItem.getHistory();

        while (it.hasNext()) {
            AssetItem historical = (AssetItem) it.next();
            String versionNumber = Long.toString(historical.getVersionNumber());
            if (version.equals(versionNumber)) {
                return historical;
            }
        }
        return null;
    }
}
