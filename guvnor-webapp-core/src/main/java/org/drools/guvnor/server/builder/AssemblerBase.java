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
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.VersionedAssetItemIterator;

import java.util.Iterator;
import java.util.List;

/**
 * This assembles packages in the BRMS into binary package objects, and deals
 * with errors etc. Each content type is responsible for contributing to the
 * package.
 */
abstract class AssemblerBase {

    protected final PackageItem packageItem;
    protected BRMSPackageBuilder builder;
    protected final AssemblyErrorLogger errorLogger = new AssemblyErrorLogger();

    protected AssemblerBase(PackageItem packageItem) {
        this.packageItem = packageItem;

        createBuilder();
    }

    public void createBuilder() {
        builder = new BRMSPackageBuilder(packageItem);
    }

    public boolean hasErrors() {
        return errorLogger.hasErrors();
    }

    public List<ContentAssemblyError> getErrors() {
        return this.errorLogger.getErrors();
    }

    protected Iterator<AssetItem> getAllAssets() {
        Iterator<AssetItem> iterator = packageItem.getAssets();
        ((VersionedAssetItemIterator) iterator).setReturnAssetsWithVersionsSpecifiedByDependencies(true);
        return iterator;
    }

    protected void loadDSLFiles() {
        builder.setDSLFiles(DSLLoader.loadDSLMappingFiles(getAssetItemIterator(AssetFormats.DSL),
                new BRMSPackageBuilder.DSLErrorEvent() {
                    public void recordError(AssetItem asset,
                                            String message) {
                        errorLogger.addError(asset,
                                message);
                    }
                }));
    }

    protected Iterator<AssetItem> getAssetItemIterator(String... formats) {
        return this.packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(formats);
    }
}
