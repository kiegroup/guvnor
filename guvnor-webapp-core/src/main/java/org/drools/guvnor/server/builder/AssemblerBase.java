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

import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.VersionedAssetItemIterator;

import java.util.Iterator;
import java.util.List;

/**
 * This assembles modules into deployment bundles, and deals
 * with errors etc. Each content type is responsible for contributing to the
 * module deployment bundles.
 */
abstract class AssemblerBase implements ModuleAssembler {

    protected ModuleItem moduleItem;
    protected BRMSPackageBuilder builder;
    protected final AssemblyErrorLogger errorLogger = new AssemblyErrorLogger();

    public boolean hasErrors() {
        return errorLogger.hasErrors();
    }

    public List<ContentAssemblyError> getErrors() {
        return this.errorLogger.getErrors();
    }

    protected Iterator<AssetItem> getAllAssets() {
        Iterator<AssetItem> iterator = moduleItem.getAssets();
        ((VersionedAssetItemIterator) iterator).setReturnAssetsWithVersionsSpecifiedByDependencies(true);
        return iterator;
    }

    protected Iterator<AssetItem> getAssetItemIterator(String... formats) {
        return this.moduleItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(formats);
    }
    protected List<AssetItem> getAllNotToIncludeAssets(StringBuilder includedAssets){
        return null;
    }
}
