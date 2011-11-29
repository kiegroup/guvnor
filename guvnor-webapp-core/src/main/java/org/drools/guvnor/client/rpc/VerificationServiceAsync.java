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

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VerificationServiceAsync {
    
    void verifyAsset(org.drools.guvnor.client.rpc.RuleAsset asset,
                     java.util.Set<String> sactiveWorkingSets,
                     AsyncCallback<org.drools.guvnor.client.rpc.AnalysisReport> arg2);

    void verifyAssetWithoutVerifiersRules(org.drools.guvnor.client.rpc.RuleAsset asset,
                                          java.util.Set<WorkingSetConfigData> activeWorkingSets,
                                          AsyncCallback<org.drools.guvnor.client.rpc.AnalysisReport> arg2);

    void analysePackage(String packageUUID,
                        AsyncCallback<AnalysisReport> callback);

}
