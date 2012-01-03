package org.drools.guvnor.server.verification;

import org.drools.builder.ResourceType;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.server.builder.AssetValidationIterator;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.io.ResourceFactory;
import org.drools.repository.ModuleItem;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.data.VerifierReport;

public class TemporalBRLAssetVerifier extends VerifierRunner {

    private final Asset ruleAsset;
    private final Verifier verifier;

    public TemporalBRLAssetVerifier(Verifier verifier, Asset ruleAsset, ModuleItem packageItem) {
        super(verifier, packageItem);
        
        if (!ruleAsset.getFormat().equals(AssetFormats.BUSINESS_RULE)){
            throw new IllegalStateException("Unexpected format "+ruleAsset.getFormat()+"! Only "+AssetFormats.BUSINESS_RULE+" expected!");
        }
        
        this.verifier = verifier;
        this.ruleAsset = ruleAsset;
    }

    @Override
    protected ScopesAgendaFilter getScopesAgendaFilter() {
        if (isAssetDecisionTable(ruleAsset)) {
            return new ScopesAgendaFilter(true, ScopesAgendaFilter.VERIFYING_SCOPE_DECISION_TABLE);
        }
        return new ScopesAgendaFilter(true, ScopesAgendaFilter.VERIFYING_SCOPE_SINGLE_RULE);

    }

    private boolean isAssetDecisionTable(Asset ruleAsset) {
        return AssetFormats.DECISION_TABLE_GUIDED.equals(ruleAsset.getFormat()) || AssetFormats.DECISION_SPREADSHEET_XLS.equals(ruleAsset.getFormat());
    }

    @Override
    protected AssetValidationIterator listAssetsByFormat(String format) {
        throw new IllegalStateException("This method should never be invoked!");
    }
    
    @Override
    public AnalysisReport verify() {
        addHeaderToVerifier();
        
        addRuleAssetToVerifier();
        
        fireAnalysis();

        VerifierReport report = verifier.getResult();

        return VerifierReportCreator.doReport(report);
    }
    
    private void addRuleAssetToVerifier(){
        
        ContentHandler handler = ContentManager.getHandler(ruleAsset.getFormat());
        
        if (!(handler instanceof IRuleAsset)){
            throw new IllegalStateException("IRuleAsset Expected");
        }

        RuleModel model = (RuleModel) ruleAsset.getContent();
        
        String brl = BRXMLPersistence.getInstance().marshal(model);
        
        verifier.addResourcesToVerify(ResourceFactory.newByteArrayResource(brl.getBytes()), ResourceType.BRL);
    }
    
}

