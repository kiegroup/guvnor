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

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.builder.AssemblyErrorLogger;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable;
import org.drools.ide.common.server.util.GuidedDTDRLPersistence;
import org.drools.ide.common.server.util.GuidedDTXMLPersistence;
import org.drools.repository.AssetItem;

import java.io.IOException;
import java.io.StringReader;

/**
 * For guided decision tables.
 */
public class GuidedDTContentHandler extends ContentHandler
    implements
    IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     AssetItem item) throws SerializationException {
        TypeSafeGuidedDecisionTable model = GuidedDTXMLPersistence.getInstance().unmarshal( item.getContent() );

        asset.setContent( model );

    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializationException {
        TypeSafeGuidedDecisionTable data = (TypeSafeGuidedDecisionTable) asset.getContent();
        if ( data.getTableName() == null ) {
            data.setTableName( repoAsset.getName() );
        }
        repoAsset.updateContent( GuidedDTXMLPersistence.getInstance().marshal( data ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        AssemblyErrorLogger logger) throws DroolsParserException,
                                                                   IOException {
        String drl = getRawDRL( asset );
        if ( drl.equals( "" ) ) return;
        builder.addPackageFromDrl( new StringReader( drl ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        RuleAsset asset,
                        AssemblyErrorLogger logger) throws DroolsParserException,
                                           IOException {
        TypeSafeGuidedDecisionTable model = (TypeSafeGuidedDecisionTable) asset.getContent();

        String drl = GuidedDTDRLPersistence.getInstance().marshal( model );

        if ( drl.equals( "" ) ) return;
        builder.addPackageFromDrl( new StringReader( drl ) );

    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            RuleAsset asset,
                            StringBuilder stringBuilder) {
        TypeSafeGuidedDecisionTable model = (TypeSafeGuidedDecisionTable) asset.getContent();

        stringBuilder.append( GuidedDTDRLPersistence.getInstance().marshal( model ) );
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuilder stringBuilder) {
        String drl = getRawDRL( asset );
        stringBuilder.append( drl );
    }

    public String getRawDRL(AssetItem asset) {
        TypeSafeGuidedDecisionTable model = GuidedDTXMLPersistence.getInstance().unmarshal( asset.getContent() );
        model.setTableName( asset.getName() );
        model.setParentName( this.parentNameFromCategory( asset,
                                                          model.getParentName() ) );

        return GuidedDTDRLPersistence.getInstance().marshal( model );
    }
}
