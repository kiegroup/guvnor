/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.builder;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import java.util.Iterator;

public class PackageDRLAssembler extends AssemblerBase {

    private StringBuilder src;

    public PackageDRLAssembler(PackageItem packageItem) {
        super(packageItem);
    }

    public String getDRL() {
        src = new StringBuilder();

        loadHeader();
        loadDSLFiles();
        loadDeclaredTypes();
        loadFunctions();
        loadRuleAssets();

        return src.toString();
    }

    private void loadHeader() {
        src.append("package ").append(this.packageItem.getName()).append("\n");
        src.append(DroolsHeader.getDroolsHeader(this.packageItem)).append("\n\n");
    }

    private void loadDeclaredTypes() {
        Iterator<AssetItem> assetItemIterator = getAssetItemIterator(AssetFormats.DRL_MODEL);
        while (assetItemIterator.hasNext()) {
            addAsset(assetItemIterator.next());
        }
    }

    private void loadFunctions() {
        Iterator<AssetItem> assetItemIterator = getAssetItemIterator(AssetFormats.FUNCTION);
        while (assetItemIterator.hasNext()) {
            addAsset(assetItemIterator.next());
        }
    }

    private void loadRuleAssets() {
        Iterator<AssetItem> assetItemIterator = getAllAssets();
        while (assetItemIterator.hasNext()) {
            addRuleAsset(assetItemIterator.next());
        }
    }

    private void addRuleAsset(AssetItem asset) {
        if (!asset.isArchived() && !asset.getDisabled()) {
            ContentHandler handler = ContentManager.getHandler(asset.getFormat());
            if (handler.isRuleAsset()) {
                IRuleAsset ruleAsset = (IRuleAsset) handler;
                ruleAsset.assembleDRL(builder,
                        asset,
                        src);
            }
            src.append("\n\n");
        }
    }

    private void addAsset(AssetItem assetItem) {
        if (!assetItem.isArchived() && !assetItem.getDisabled()) {
            src.append(assetItem.getContent()).append("\n\n");
        }
    }

}
