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

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.IHasCustomValidator;
import org.drools.guvnor.server.util.BuilderResultHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;

import java.util.Iterator;

public class AssetItemValidator {

    private final ContentHandler handler;
    private final AssetItem assetItemUnderValidation;

    public AssetItemValidator(ContentHandler handler, AssetItem assetItemUnderValidation) {
        this.handler = handler;
        this.assetItemUnderValidation = assetItemUnderValidation;
    }

    public BuilderResult validate() {
        if (handler instanceof IHasCustomValidator) {
            return ((IHasCustomValidator) handler).validateAsset(assetItemUnderValidation);
        } else {
            BuilderValidator validator = new BuilderValidator();
            validator.init(assetItemUnderValidation.getModule(), null);
            return validator.validate();
        }
    }

    private class BuilderValidator extends PackageAssemblerBase {
        
        public void init(ModuleItem moduleItem, ModuleAssemblerConfiguration moduleAssemblerConfiguration) {
            this.moduleItem = moduleItem;
            createBuilder();
        }

        public BuilderResult validate() {
            if (setUpPackage()) {
                buildAsset(assetItemUnderValidation);
            }
            return getResult();
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
}
