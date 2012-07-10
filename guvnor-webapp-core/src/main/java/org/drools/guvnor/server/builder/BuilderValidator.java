/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.builder;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.server.util.BuilderResultHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class BuilderValidator extends PackageAssemblerBase implements Validator {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    public static final String DEFAULT = "default";
    private AssetItem assetItemUnderValidation;

    public void init(ModuleItem moduleItem, ModuleAssemblerConfiguration moduleAssemblerConfiguration) {
        this.moduleItem = moduleItem;
        createBuilder();
    }

    public BuilderResult validateAsset(AssetItem item) {
        assetItemUnderValidation = item;
        this.moduleItem = item.getModule();
        createBuilder();
        if (setUpPackage()) {
            buildAsset(item);
        }
        return getResult();
    }

    public boolean validate(AssetItem item) {
        try {
            return !validateAsset(item).hasLines();
        } catch (RuntimeException re) {
            log.warn("Validation failed!", re);
            return false;
        }
    }

    public String getFormat() {
        return DEFAULT;
    }

    public BuilderResult getResult() {
        BuilderResult result = new BuilderResult();
        result.addLines(new BuilderResultHelper().generateBuilderResults(getErrors()));
        return result;
    }

    protected Iterator<AssetItem> getAssetItemIterator(String... formats) {
        AssetValidationIterator assetValidationIterator = new AssetValidationIterator(super.getAssetItemIterator(formats));
        assetValidationIterator.setAssetItemUnderValidation(assetItemUnderValidation);
        return assetValidationIterator;
    }
}
