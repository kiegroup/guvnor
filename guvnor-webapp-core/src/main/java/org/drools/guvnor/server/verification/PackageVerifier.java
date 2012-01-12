package org.drools.guvnor.server.verification;

import org.drools.guvnor.server.builder.AssetValidationIterator;
import org.drools.repository.ModuleItem;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.ScopesAgendaFilter;

public class PackageVerifier extends VerifierRunner {

    public PackageVerifier(Verifier verifier, ModuleItem packageItem) {
        super(
                verifier,
                packageItem);
    }

    @Override
    protected ScopesAgendaFilter getScopesAgendaFilter() {
        return new ScopesAgendaFilter(
                true,
                ScopesAgendaFilter.VERIFYING_SCOPE_KNOWLEDGE_PACKAGE);
    }

    @Override
    protected AssetValidationIterator listAssetsByFormat(String format) {
        return new AssetValidationIterator(packageItem.listAssetsByFormat(format));
    }
}
