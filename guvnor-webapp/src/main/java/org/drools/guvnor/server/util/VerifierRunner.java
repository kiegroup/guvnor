/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.util;

import java.io.StringReader;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.io.ResourceFactory;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.data.VerifierReport;

public class VerifierRunner {

    private Verifier    verifier;
    private PackageItem packageItem;

    public VerifierRunner(Verifier verifier) {
        this.verifier = verifier;
    }

    public AnalysisReport verify(PackageItem packageItem,
                                 ScopesAgendaFilter scopesAgendaFilter) {
        this.packageItem = packageItem;

        addHeaderToVerifier();

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.DSL} ),
                       ResourceType.DSL );

        // TODO: Model JARS

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.DRL_MODEL} ),
                       ResourceType.DRL );

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.FUNCTION} ),
                       ResourceType.DRL );

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.DSL_TEMPLATE_RULE} ),
                       ResourceType.DSLR );

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.DECISION_SPREADSHEET_XLS} ),
                       ResourceType.DTABLE );

        addGuidedDecisionTablesToVerifier();

        addDRLRulesToVerifier();

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.BUSINESS_RULE} ),
                       ResourceType.BRL );

        fireAnalysis( scopesAgendaFilter );

        VerifierReport report = verifier.getResult();

        return VerifierReportCreator.doReport( report );
    }

    private void fireAnalysis(ScopesAgendaFilter scopesAgendaFilter) throws RuntimeException {

        verifier.fireAnalysis( scopesAgendaFilter );

        if ( verifier.hasErrors() ) {
            StringBuilder message = new StringBuilder( "Verifier Errors:\n" );
            for ( VerifierError verifierError : verifier.getErrors() ) {
                message.append( "\t" );
                message.append( verifierError.getMessage() );
                message.append( "\n" );
            }
            throw new RuntimeException( message.toString() );
        }
    }

    private void addHeaderToVerifier() {
        StringBuilder header = new StringBuilder();
        header.append( "package " + packageItem.getName() + "\n" );
        header.append( DroolsHeader.getDroolsHeader( packageItem ) + "\n" );

        verifier.addResourcesToVerify( ResourceFactory.newReaderResource( new StringReader( header.toString() ) ),
                                       ResourceType.DRL );

    }

    private void addToVerifier(AssetItemIterator assets,
                               ResourceType resourceType) {
        while ( assets.hasNext() ) {
            AssetItem asset = assets.next();
            if ( !asset.isArchived() && !asset.getDisabled() ) {
                if ( resourceType == ResourceType.DTABLE ) {
                    DecisionTableConfiguration dtableconfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
                    dtableconfiguration.setInputType( DecisionTableInputType.XLS );

                    verifier.addResourcesToVerify( ResourceFactory.newByteArrayResource( asset.getBinaryContentAsBytes() ),
                                                   resourceType,
                                                   (ResourceConfiguration) dtableconfiguration );
                } else {
                    verifier.addResourcesToVerify( ResourceFactory.newReaderResource( new StringReader( asset.getContent() ) ),
                                                   resourceType );
                }
            }
        }
    }

    private void addGuidedDecisionTablesToVerifier() {

        AssetItemIterator rules = packageItem.listAssetsByFormat( AssetFormats.DECISION_TABLE_GUIDED );

        while ( rules.hasNext() ) {
            AssetItem rule = rules.next();

            ContentHandler contentHandler = ContentManager.getHandler( rule.getFormat() );
            if ( contentHandler.isRuleAsset() ) {
                IRuleAsset ruleAsset = (IRuleAsset) contentHandler;
                String drl = ruleAsset.getRawDRL( rule );
                verifier.addResourcesToVerify( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                                               ResourceType.DRL );

            }
        }
    }

    private void addDRLRulesToVerifier() {

        AssetItemIterator rules = packageItem.listAssetsByFormat( AssetFormats.DRL );

        while ( rules.hasNext() ) {
            AssetItem rule = rules.next();

            ContentHandler contentHandler = ContentManager.getHandler( rule.getFormat() );
            if ( contentHandler.isRuleAsset() ) {
                IRuleAsset ruleAsset = (IRuleAsset) contentHandler;
                String drl = ruleAsset.getRawDRL( rule );
                verifier.addResourcesToVerify( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                                               ResourceType.DRL );

            }
        }
    }
}
