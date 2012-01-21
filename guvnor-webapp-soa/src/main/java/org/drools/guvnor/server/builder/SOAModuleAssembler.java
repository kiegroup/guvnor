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

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.drools.guvnor.server.files.AssetZipper;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;

/**
 * Package all assets in a service module into a zip.
 */
public class SOAModuleAssembler extends AssemblerBase {
    private static final LoggingHelper log = LoggingHelper.getLogger(SOAModuleAssembler.class);
    private ModuleAssemblerConfiguration configuration;  

    public void init(ModuleItem moduleItem, ModuleAssemblerConfiguration moduleAssemblerConfiguration) {
        this.moduleItem = moduleItem;
        this.configuration = moduleAssemblerConfiguration;
    }

    public void compile() {
        InputStream is = generateZip();
        //byte[] compiledPackageByte = modulegeAssembler.getCompiledBinary();
        moduleItem.updateCompiledBinary(is);            
        moduleItem.updateBinaryUpToDate( true );
        
        moduleItem.getRulesRepository().save();
    }

    /**
     * This will return true if there is an error in the module configuration
     * @return
     */
    public boolean isModuleConfigurationInError() {
        return errorLogger.hasErrors() && this.errorLogger.getErrors().get(0).isModuleItem();
    }

    public byte[] getCompiledBinary() {
        return moduleItem.getCompiledBinaryBytes();
    }

    public String getCompiledSource() {
        //NOT_APPLICABLE
        return null;
    }   
        
    protected InputStream generateZip() {
        List<AssetItem> jarAssets = new LinkedList<AssetItem>();
        AssetZipper assetZipper = null;
        
        Iterator<AssetItem> assetItemIterator = getAssetItemIterator("jar", "wsdl", "xmlschema");
        while (assetItemIterator.hasNext()) {
            AssetItem assetItem = assetItemIterator.next();
            if (!assetItem.isArchived() && !assetItem.getDisabled()) {
                jarAssets.add(assetItem);
            }
        }
        if (jarAssets.size() != 0) {
            assetZipper = new AssetZipper(jarAssets, null);

            return assetZipper.zipAssets();
        }
        
        //REVISIT: return an empty zip instead?
        return null;
    }  
}
