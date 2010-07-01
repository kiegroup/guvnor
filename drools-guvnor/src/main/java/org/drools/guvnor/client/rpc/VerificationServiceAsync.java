package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author rikkola
 *
 */
public interface VerificationServiceAsync {
    
    void verifyAsset(org.drools.guvnor.client.rpc.RuleAsset asset,
                     java.util.Set<String> sactiveWorkingSets,
                     AsyncCallback<org.drools.guvnor.client.rpc.AnalysisReport> arg2);

    void verifyAssetWithoutVerifiersRules(org.drools.guvnor.client.rpc.RuleAsset asset,
                                          java.util.Set<String> sactiveWorkingSets,
                                          AsyncCallback<org.drools.guvnor.client.rpc.AnalysisReport> arg2);

    void analysePackage(String packageUUID,
                        AsyncCallback<AnalysisReport> callback);

}
