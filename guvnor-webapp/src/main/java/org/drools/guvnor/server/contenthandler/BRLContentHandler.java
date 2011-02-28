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
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRDRLPersistence;
import org.drools.ide.common.server.util.BRLPersistence;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializationException;

public class BRLContentHandler extends ContentHandler
    implements
    IRuleAsset {

    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializationException {
        RuleModel ruleModel = getBrlXmlPersistence().unmarshal( item.getContent() );

        ruleModel.name = asset.metaData.name;

        asset.content = ruleModel;
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializationException {
        RuleModel data = (RuleModel) asset.content;
        if ( data.name == null ) {
            data.name = repoAsset.getName();
        }
        repoAsset.updateContent( getBrlXmlPersistence().marshal( data ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        AssetItem asset,
                        ContentPackageAssembler.ErrorLogger logger) throws DroolsParserException,
                                                                   IOException {
        builder.addPackageFromDrl( new StringReader( getSourceDRL( buildModelFromAsset( asset ),
                                                                   builder ) ) );
    }

    public void compile(BRMSPackageBuilder builder,
                        RuleAsset asset,
                        ErrorLogger logger) throws DroolsParserException,
                                           IOException {
        builder.addPackageFromDrl( new StringReader( getSourceDRL( (RuleModel) asset.content,
                                                                   builder ) ) );

    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            RuleAsset asset,
                            StringBuilder stringBuilder) {
        String drl = getSourceDRL( (RuleModel) asset.content,
                                   builder );
        stringBuilder.append( drl );
    }

    public void assembleDRL(BRMSPackageBuilder builder,
                            AssetItem asset,
                            StringBuilder stringBuilder) {
        String drl = getSourceDRL( buildModelFromAsset( asset ),
                                   builder );
        stringBuilder.append( drl );
    }

    private String getSourceDRL(RuleModel model,
                                BRMSPackageBuilder builder) {

        String drl = getBrlDrlPersistence().marshal( model );
        if ( builder.hasDSL() && model.hasDSLSentences() ) {
            drl = builder.getDSLExpander().expand( drl );
        }
        return drl;
    }

    protected RuleModel buildModelFromAsset(AssetItem asset) {
        RuleModel model = getBrlXmlPersistence().unmarshal( asset.getContent() );
        model.name = asset.getName();
        model.parentName = this.parentNameFromCategory( asset,
                                                        model.parentName );
        return model;
    }

    public String getRawDRL(AssetItem asset) {
        RuleModel model = getBrlXmlPersistence().unmarshal( asset.getContent() );

        return getBrlDrlPersistence().marshal( model );
    }

    protected BRLPersistence getBrlDrlPersistence() {
        return BRDRLPersistence.getInstance();
    }

    protected BRLPersistence getBrlXmlPersistence() {
        return BRXMLPersistence.getInstance();
    }

}
