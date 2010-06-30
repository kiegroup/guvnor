package org.drools.guvnor.client.rpc;

/**
 * 
 * @author rikkola
 *
 */
public interface VerificationServiceAsync {
    void verifyAsset(org.drools.guvnor.client.rpc.RuleAsset asset,
                     java.util.Set<String> sactiveWorkingSets,
                     com.google.gwt.user.client.rpc.AsyncCallback<org.drools.guvnor.client.rpc.AnalysisReport> arg2);

    void verifyAssetWithoutVerifiersRules(org.drools.guvnor.client.rpc.RuleAsset asset,
                                          java.util.Set<String> sactiveWorkingSets,
                                          com.google.gwt.user.client.rpc.AsyncCallback<org.drools.guvnor.client.rpc.AnalysisReport> arg2);

}
