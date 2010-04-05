package org.drools.guvnor.server.util;

import java.io.StringReader;
import java.util.Collection;

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
import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierConfigurationImpl;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;

public class VerifierRunner {

    private Verifier    verifier;
    private PackageItem packageItem;
    private boolean useDefaultConfig = true;

	public AnalysisReport verify(String drl, String scope) {
		return verify(drl, scope, null);
	}

	public AnalysisReport verify(String drl, String scope,
			Collection<String> additionalVerifierRules) {

        initVerifier( scope, additionalVerifierRules );

        verifier.addResourcesToVerify( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                                       ResourceType.DRL );
        verifier.fireAnalysis();
        VerifierReport res = verifier.getResult();

        return VerifierReportCreator.doReport( res );
    }

    public AnalysisReport verify(PackageItem packageItem,
                                 String scope, Collection<String> additionalVerifierRules) {
        this.packageItem = packageItem;

        initVerifier( scope, additionalVerifierRules );

        addHeaderToVerifier();

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.DSL} ),
                       ResourceType.DSL );

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.DRL_MODEL} ),
                       ResourceType.DRL );

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.FUNCTION} ),
                       ResourceType.DRL );

        addToVerifier( packageItem.listAssetsByFormat( new String[]{AssetFormats.DRL} ),
                       ResourceType.DRL );

        addToRulesVerifier();

        verifier.fireAnalysis();

        VerifierReport report = verifier.getResult();

        return VerifierReportCreator.doReport( report );
    }

    private void initVerifier(String scope, Collection<String> additionalVerifierRules) {
        VerifierConfiguration conf = new DefaultVerifierConfiguration();
        if(useDefaultConfig){
            conf = new DefaultVerifierConfiguration();
        }else{
            conf = new VerifierConfigurationImpl();
        }

        conf.getVerifyingScopes().clear();
        conf.getVerifyingScopes().add( scope );
        conf.setAcceptRulesWithoutVerifiyingScope( true );
		if (additionalVerifierRules != null) {
			for (String rule : additionalVerifierRules) {
				conf.getVerifyingResources().put(
						ResourceFactory.newByteArrayResource(rule.getBytes()),
						ResourceType.DRL);
			}
		}
        verifier = VerifierBuilderFactory.newVerifierBuilder().newVerifier( conf );
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

    private void addToRulesVerifier() {

        AssetItemIterator rules = packageItem.listAssetsByFormat( AssetFormats.BUSINESS_RULE_FORMATS );

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

    public boolean isUseDefaultConfig() {
        return useDefaultConfig;
    }

    public void setUseDefaultConfig(boolean useDefaultConfig) {
        this.useDefaultConfig = useDefaultConfig;
    }



}
