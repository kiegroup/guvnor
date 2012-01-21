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

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.selector.AssetSelector;
import org.drools.guvnor.server.selector.BuiltInSelector;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepositoryException;
import org.drools.rule.Package;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;

/**
 * This assembles packages in the BRMS into binary package objects, and deals
 * with errors etc. Each content type is responsible for contributing to the
 * package.
 */
public class PackageAssembler extends PackageAssemblerBase {

    private static final LoggingHelper log = LoggingHelper.getLogger(PackageAssembler.class);

    private ModuleAssemblerConfiguration configuration;
    private AssetSelector selector;
 
    public void init(ModuleItem moduleItem, ModuleAssemblerConfiguration moduleAssemblerConfiguration) {
        this.moduleItem = moduleItem;
        
        if(moduleAssemblerConfiguration == null) {
            this.configuration = new ModuleAssemblerConfiguration();
        } else {
            this.configuration = moduleAssemblerConfiguration;
        }
        createBuilder();
        src = new StringBuilder();
    }
    
    public void compile() {
        if (setUpPackage()) {
            buildPackage();
        }
        
        if (!hasErrors() ) {
            RuleBase ruleBase = RuleBaseFactory.newRuleBase(
                new RuleBaseConfiguration(getClassLoaders())
            );
            ruleBase.addPackage(builder.getPackage());

            byte[] compiledPackageByte = getCompiledBinary();
            moduleItem.updateCompiledBinary(new ByteArrayInputStream(compiledPackageByte));
            moduleItem.updateBinaryUpToDate(true);
            moduleItem.getRulesRepository().save();         
        }

    }

    private ClassLoader[] getClassLoaders() {
        Collection<ClassLoader> loaders = getBuilder().getRootClassLoader().getClassLoaders();
        return loaders.toArray( new ClassLoader[loaders.size()] );
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
            errorLogger.addError(moduleItem, "The selector named " + configuration.getCustomSelectorConfigName() + " is not available.");
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
    public boolean isModuleConfigurationInError() {
        return errorLogger.hasErrors() && this.errorLogger.getErrors().get(0).isModuleItem();
    }
    
    public byte[] getCompiledBinary() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new DroolsObjectOutputStream( bout );
            out.writeObject( getBinaryPackage() );
            out.flush();
            out.close();       
        } catch (IOException e) {
            e.printStackTrace();
            log.error( "An error occurred building the module [" + moduleItem.getName() + "]: " + e.getMessage() );
            throw new RulesRepositoryException( "An error occurred building the module.",
                    e );
        }
        return bout.toByteArray();
    }
    
    /**
     * I've got a package people !
     */
    private Package getBinaryPackage() {
        if (this.hasErrors()) {
            throw new IllegalStateException("There is no package available, as there were errors.");
        }
        return builder.getPackage();
    }
    
    public BRMSPackageBuilder getBuilder() {
        return builder;
    }
    
    public String getCompiledSource() {
        src = new StringBuilder();

        loadHeaderToSource();
        loadDSLFiles();
        loadDeclaredTypesToSource();
        loadFunctionsToSource();
        loadRuleAssets();

        return src.toString();
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

}
