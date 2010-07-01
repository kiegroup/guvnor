package org.drools.guvnor.server.util;

import java.io.StringReader;

import org.drools.builder.ResourceType;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.server.ServiceImplementation;
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
        StringBuffer header = new StringBuffer();
        header.append( "package " + packageItem.getName() + "\n" );
        header.append( ServiceImplementation.getDroolsHeader( packageItem ) + "\n" );

        verifier.addResourcesToVerify( ResourceFactory.newReaderResource( new StringReader( header.toString() ) ),
                                       ResourceType.DRL );

    }

    private void addToVerifier(AssetItemIterator assets,
                               ResourceType resourceType) {
        while ( assets.hasNext() ) {
            AssetItem asset = assets.next();
            if ( !asset.isArchived() && !asset.getDisabled() ) {
                verifier.addResourcesToVerify( ResourceFactory.newReaderResource( new StringReader( asset.getContent() ) ),
                                               resourceType );
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
