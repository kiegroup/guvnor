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
import org.drools.guvnor.server.selector.AssetSelector;
import org.drools.guvnor.server.selector.BuiltInSelector;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.rule.Package;

import java.util.Iterator;

/**
 * This assembles packages in the BRMS into binary package objects, and deals
 * with errors etc. Each content type is responsible for contributing to the
 * package.
 */
public class PackageAssembler extends PackageAssemblerBase {

    private static final LoggingHelper log = LoggingHelper.getLogger(PackageAssembler.class);

    private final PackageAssemblerConfiguration configuration;
    private AssetSelector selector;

    public PackageAssembler(PackageItem packageItem) {
        this(packageItem,
                new PackageAssemblerConfiguration());
    }

    public PackageAssembler(PackageItem packageItem,
                            PackageAssemblerConfiguration packageAssemblerConfiguration) {
        super(packageItem);
        configuration = packageAssemblerConfiguration;
    }

    public void compile() {
        if (setUpPackage()) {
            buildPackage();
        }
    }

    /**
     * This will build the package - preparePackage would have been called first.
     * This will always prioritise DRL before other assets.
     */
    private void buildPackage() {
        if (setUpSelector()) {
            loadAssets();
        }
    }

    private void loadAssets() {
        StringBuilder includedAssets = new StringBuilder("Following assets have been included in package build: ");

        loadDRLAssets(includedAssets);
        loadAllButDRLAssets(includedAssets);

        log.info(includedAssets.toString());
    }

    private void loadAllButDRLAssets(StringBuilder includedAssets) {
        Iterator<AssetItem> iterator = getAllAssets();
        while (iterator.hasNext()) {
            AssetItem asset = iterator.next();
            if (!asset.getFormat().equals(AssetFormats.DRL) && assetCanBeAdded(asset)) {
                addAsset(includedAssets, asset);
            }
        }
    }

    private void loadDRLAssets(StringBuilder includedAssets) {
        Iterator<AssetItem> drlAssetItemIterator = getAssetItemIterator(AssetFormats.DRL);
        while (drlAssetItemIterator.hasNext()) {
            AssetItem asset = drlAssetItemIterator.next();
            if (assetCanBeAdded(asset)) {
                addAsset(includedAssets, asset);
            }
        }
    }

    private void addAsset(StringBuilder includedAssets, AssetItem asset) {
        buildAsset(asset);
        includedAssets.append(asset.getName()).append(", ");
    }

    private boolean assetCanBeAdded(AssetItem asset) {
        return !asset.isArchived() && (selector.isAssetAllowed(asset));
    }

    private boolean setUpSelector() {
        if (SelectorManager.CUSTOM_SELECTOR.equals(configuration.getBuildMode())) {
            selector = SelectorManager.getInstance().getSelector(configuration.getCustomSelectorConfigName());
        } else if (SelectorManager.BUILT_IN_SELECTOR.equals(configuration.getBuildMode())) {
            selector = setUpBuiltInSelector();
        } else {
            //return the NilSelector, i.e., allows everything
            selector = SelectorManager.getInstance().getSelector(null);
        }

        if (selector == null) {
            errorLogger.addError(packageItem, "The selector named " + configuration.getCustomSelectorConfigName() + " is not available.");
            return false;
        } else {
            return true;
        }
    }

    private AssetSelector setUpBuiltInSelector() {
        BuiltInSelector builtInSelector = (BuiltInSelector) SelectorManager.getInstance().getSelector(SelectorManager.BUILT_IN_SELECTOR);
        builtInSelector.setStatusOperator(configuration.getStatusOperator());
        builtInSelector.setStatus(configuration.getStatusDescriptionValue());
        builtInSelector.setEnableStatusSelector(configuration.isEnableStatusSelector());
        builtInSelector.setCategory(configuration.getCategoryValue());
        builtInSelector.setCategoryOperator(configuration.getCategoryOperator());
        builtInSelector.setEnableCategorySelector(configuration.isEnableCategorySelector());
        return builtInSelector;
    }

    /**
     * This will return true if there is an error in the package configuration
     * or functions.
     *
     * @return
     */
    public boolean isPackageConfigurationInError() {
        return errorLogger.hasErrors() && this.errorLogger.getErrors().get(0).isPackageItem();
    }

    /**
     * I've got a package people !
     */
    public Package getBinaryPackage() {
        if (this.hasErrors()) {
            throw new IllegalStateException("There is no package available, as there were errors.");
        }
        return builder.getPackage();
    }

    public BRMSPackageBuilder getBuilder() {
        return builder;
    }
}
