package org.drools.guvnor.server.contenthandler;

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

import java.io.IOException;
import java.io.StringReader;

import org.drools.compiler.DroolsParserException;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ContentPackageAssembler.ErrorLogger;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is for handling XLS content (classic decision tables).
 *
 * @author Michael Neale
 */
public class DecisionTableXLSHandler extends ContentHandler
    implements
    IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        //do nothing, as we have an attachment
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        //do nothing, as we have an attachment
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuffer buf) {
        buf.append( getRawDRL( asset ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        ErrorLogger logger) throws DroolsParserException,
                                           IOException {
        StringBuffer buf = new StringBuffer();

        assembleDRL( builder,
                     asset,
                     buf );
        builder.addPackageFromDrl( new StringReader( buf.toString() ) );

    }

    public String getRawDRL(AssetItem asset) {
        SpreadsheetCompiler comp = new SpreadsheetCompiler();
        String drl = comp.compile( false,
                                   asset.getBinaryContentAttachment(),
                                   InputType.XLS );
        return drl;
    }

}