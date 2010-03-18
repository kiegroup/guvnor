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
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.util.BRDRLPersistence;
import org.drools.guvnor.server.util.BRXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class BRLContentHandler extends ContentHandler
    implements
    IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleModel model = BRXMLPersistence.getInstance().unmarshal( item.getContent() );

        asset.content = model;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        RuleModel data = (RuleModel) asset.content;
        if ( data.name == null ) {
            data.name = repoAsset.getName();
        }
        repoAsset.updateContent( BRXMLPersistence.getInstance().marshal( data ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        ContentPackageAssembler.ErrorLogger logger) throws DroolsParserException,
                                                                   IOException {
        builder.addPackageFromDrl( new StringReader( getSourceDRL( asset,
                                                                   builder ) ) );
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuffer buf) {
        String drl = getSourceDRL( asset,
                                   builder );
        buf.append( drl );
    }

    private String getSourceDRL(AssetItem asset,
                                BRMSPackageBuilder builder) {
        RuleModel model = BRXMLPersistence.getInstance().unmarshal( asset.getContent() );
        model.name = asset.getName();
        model.parentName = this.parentNameFromCategory( asset,
                                                        model.parentName );

        String drl = BRDRLPersistence.getInstance().marshal( model );
        if ( builder.hasDSL() && model.hasDSLSentences() ) {
            drl = builder.getDSLExpander().expand( drl );
        }
        return drl;
    }

    public String getRawDRL(AssetItem asset) {
        RuleModel model = BRXMLPersistence.getInstance().unmarshal( asset.getContent() );

        return BRDRLPersistence.getInstance().marshal( model );
    }
}