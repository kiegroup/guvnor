/*
 * Copyright 2007 JBoss Inc
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

package org.drools.guvnor.server.contenthandler.drools;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.server.contenthandler.IHasCustomValidator;
import org.drools.guvnor.server.contenthandler.PlainTextContentHandler;
import org.drools.ide.common.server.util.DataEnumLoader;
import org.drools.repository.AssetItem;

public class EnumerationContentHandler extends PlainTextContentHandler
    implements
        IHasCustomValidator {

    public BuilderResult validateAsset(AssetItem asset) {

        String content = asset.getContent();
        DataEnumLoader loader = new DataEnumLoader( content );
        if ( !loader.hasErrors() ) {
            return new BuilderResult();
        } else {
            List<BuilderResultLine> errors = new ArrayList<BuilderResultLine>();
            List<String> errs = loader.getErrors();

            for ( String message : errs ) {

                BuilderResultLine result = new BuilderResultLine().setAssetName(asset.getName()).setAssetFormat(asset.getFormat()).setUuid(asset.getUUID()).setMessage(message);
                errors.add( result );
            }

            BuilderResult result = new BuilderResult();
            result.addLines(errors);

            return result;
        }
    }
}
