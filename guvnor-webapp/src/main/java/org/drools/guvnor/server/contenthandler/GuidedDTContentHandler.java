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

package org.drools.guvnor.server.contenthandler;

import java.io.IOException;
import java.io.StringReader;

import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
import org.drools.guvnor.server.builder.ContentPackageAssembler.ErrorLogger;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.server.util.GuidedDTDRLPersistence;
import org.drools.ide.common.server.util.GuidedDTXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * For guided decision tables.
 */
public class GuidedDTContentHandler extends ContentHandler
    implements
    IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializationException {
        GuidedDecisionTable model = GuidedDTXMLPersistence.getInstance().unmarshal( item.getContent() );

        asset.content = model;

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializationException {
        GuidedDecisionTable data = (GuidedDecisionTable) asset.content;
        if ( data.getTableName() == null ) {
            data.setTableName( repoAsset.getName() );
        }

        // Change the row numbers so they are in the same order as the rows.
        for ( int i = 0; i < data.getData().length; i++ ) {
            data.getData()[i][0] = String.valueOf( i + 1 );
        }

        repoAsset.updateContent( GuidedDTXMLPersistence.getInstance().marshal( data ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        ContentPackageAssembler.ErrorLogger logger) throws DroolsParserException,
                                                                   IOException {
        String drl = getRawDRL( asset );
        if ( drl.equals( "" ) ) return;
        builder.addPackageFromDrl( new StringReader( drl ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        RuleAsset asset,
                        ErrorLogger logger) throws DroolsParserException,
                                           IOException {
        GuidedDecisionTable model = (GuidedDecisionTable) asset.content;

        String drl = GuidedDTDRLPersistence.getInstance().marshal( model );

        if ( drl.equals( "" ) ) return;
        builder.addPackageFromDrl( new StringReader( drl ) );

    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            RuleAsset asset,
                            StringBuilder stringBuilder) {
        GuidedDecisionTable model = (GuidedDecisionTable) asset.content;

        stringBuilder.append( GuidedDTDRLPersistence.getInstance().marshal( model ) );
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuilder stringBuilder) {
        String drl = getRawDRL( asset );
        stringBuilder.append( drl );
    }

    public String getRawDRL(AssetItem asset) {
        GuidedDecisionTable model = GuidedDTXMLPersistence.getInstance().unmarshal( asset.getContent() );
        model.setTableName( asset.getName() );
        model.setParentName( this.parentNameFromCategory( asset,
                                                          model.getParentName() ) );

        return GuidedDTDRLPersistence.getInstance().marshal( model );
    }
}
