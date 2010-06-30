package org.drools.guvnor.client.rpc;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

@RemoteServiceRelativePath("verificationService")
public interface VerificationService
    extends
    RemoteService {

    public AnalysisReport verifyAsset(RuleAsset asset,
                                      Set<String> activeWorkingSets) throws SerializationException;

    public AnalysisReport verifyAssetWithoutVerifiersRules(RuleAsset asset,
                                                           Set<String> activeWorkingSets) throws SerializationException;

}
