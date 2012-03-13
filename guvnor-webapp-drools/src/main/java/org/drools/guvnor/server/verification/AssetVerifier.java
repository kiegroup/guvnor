package org.drools.guvnor.server.verification;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.builder.AssetValidationIterator;
import org.drools.repository.AssetItem;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.ScopesAgendaFilter;

public class AssetVerifier extends VerifierRunner {

    private final AssetItem assetItem;

    public AssetVerifier(Verifier verifier, AssetItem assetItem) {
        super(verifier, assetItem.getModule());
        this.assetItem = assetItem;
    }

    @Override
    protected ScopesAgendaFilter getScopesAgendaFilter() {
        if (isAssetDecisionTable(assetItem)) {
            return new ScopesAgendaFilter(true, ScopesAgendaFilter.VERIFYING_SCOPE_DECISION_TABLE);
        }
        return new ScopesAgendaFilter(true, ScopesAgendaFilter.VERIFYING_SCOPE_SINGLE_RULE);

    }

    private boolean isAssetDecisionTable(AssetItem assetItem) {
        return AssetFormats.DECISION_TABLE_GUIDED.equals(assetItem.getFormat()) || AssetFormats.DECISION_SPREADSHEET_XLS.equals(assetItem.getFormat());
    }

    @Override
    protected AssetValidationIterator listAssetsByFormat(String format) {
        AssetValidationIterator assetValidationIterator = new AssetValidationIterator(packageItem.listAssetsByFormat(format));
        if (assetItem.getFormat().equals(format)) {
            assetValidationIterator.setAssetItemUnderValidation(assetItem);
        }
        return assetValidationIterator;
    }
}

