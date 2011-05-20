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
import org.drools.repository.PackageItem;

public class AssetItemValidator {

    private ContentHandler handler;

    public AssetItemValidator(ContentHandler handler) {
        this.handler = handler;
    }

    public BuilderResult validate(AssetItem assetItem) {
        if (handler instanceof IHasCustomValidator) {
            return ((IHasCustomValidator) handler).validateAsset(assetItem);
        } else {
            // TODO: There is still one flaw here, what if the asset does not affect the build? -Rikkola-
            BuilderValidator builderValidator = new BuilderValidator(assetItem.getPackage());
            builderValidator.validate(assetItem);
            return builderValidator.getResult();
        }
    }

    private class BuilderValidator extends PackageAssemblerBase {

        public BuilderValidator(PackageItem packageItem) {
            super(packageItem);
        }

        public void validate(AssetItem assetItem) {
            if (setUpPackage()) {
                buildAsset(assetItem);
            }
        }

        public BuilderResult getResult() {
            BuilderResult result = new BuilderResult();
            result.setLines(new BuilderResultHelper().generateBuilderResults(getErrors()));
            return result;
        }
    }
}
