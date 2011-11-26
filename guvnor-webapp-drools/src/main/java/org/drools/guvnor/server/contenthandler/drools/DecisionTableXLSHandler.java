/*
 * Copyright 2005 JBoss Inc
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

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.compiler.DroolsParserException;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.builder.AssemblyErrorLogger;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.repository.AssetItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * This is for handling XLS content (classic decision tables).
 */
public class DecisionTableXLSHandler extends ContentHandler
        implements
        IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     AssetItem item) throws SerializationException {
        //do nothing, as we have an attachment
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializationException {
        //do nothing, as we have an attachment
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            RuleAsset asset,
                            StringBuilder stringBuilder) {
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuilder stringBuilder) {
        stringBuilder.append(getRawDRL(asset));
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        AssemblyErrorLogger logger) throws DroolsParserException,
            IOException {
        StringBuilder stringBuilder = new StringBuilder();

        assembleDRL(builder,
                asset,
                stringBuilder);
        builder.addPackageFromDrl(new StringReader(stringBuilder.toString()));

    }

    public String getRawDRL(AssetItem asset) {
        return getDRL(asset.getBinaryContentAttachment());
    }

    private String getDRL(InputStream stream) {
        SpreadsheetCompiler comp = new SpreadsheetCompiler();
        return comp.compile(false,
                stream,
                InputType.XLS);
    }

}
