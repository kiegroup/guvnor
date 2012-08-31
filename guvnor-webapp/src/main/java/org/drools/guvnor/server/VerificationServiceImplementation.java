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

package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.verification.AssetVerifier;
import org.drools.guvnor.server.verification.PackageVerifier;
import org.drools.guvnor.server.verification.VerifierConfigurationFactory;
import org.drools.repository.AssetItem;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.security.Restrict;

import java.util.Set;

public class VerificationServiceImplementation extends RemoteServiceServlet implements VerificationService {

    private static final long serialVersionUID = 510l;

    private static final LoggingHelper log = LoggingHelper.getLogger(ServiceImplementation.class);

    private final Verifier defaultVerifier = VerifierBuilderFactory.newVerifierBuilder().newVerifier();
    private final ServiceSecurity serviceSecurity = new ServiceSecurity();

    protected RepositoryAssetService getAssetService() {
        return RepositoryServiceServlet.getAssetService();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public AnalysisReport analysePackage(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( packageUUID );


        AnalysisReport report = new PackageVerifier(
                defaultVerifier,
                getAssetService().getRulesRepository().loadPackageByUUID(packageUUID)
        ).verify();

        defaultVerifier.flushKnowledgeSession();

        return report;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public AnalysisReport verifyAsset(RuleAsset asset,
                                      Set<String> activeWorkingSetIds) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( asset );

        return verify(
                asset,
                VerifierConfigurationFactory.getDefaultConfigurationWithWorkingSetConstraints(
                        loadWorkingSets(activeWorkingSetIds)));
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public AnalysisReport verifyAssetWithoutVerifiersRules(RuleAsset asset,
                                                           Set<String> activeWorkingIds) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( asset );

        return verify(
                asset,
                VerifierConfigurationFactory.getPlainWorkingSetVerifierConfiguration(
                        loadWorkingSets(activeWorkingIds)));
    }

    private RuleAsset[] loadWorkingSets(Set<String> activeWorkingSets) throws SerializationException {
        if (activeWorkingSets == null) {
            return new RuleAsset[0];
        } else {
            return getAssetService().loadRuleAssets(activeWorkingSets.toArray(new String[activeWorkingSets.size()]));
        }
    }

    private AnalysisReport verify(RuleAsset asset, VerifierConfiguration verifierConfiguration) throws SerializationException {
        long startTime = System.currentTimeMillis();

        AnalysisReport report = getAssetVerifier(
                verifierConfiguration,
                getAssetItem(asset)
        ).verify();

        log.debug("Asset verification took: " + (System.currentTimeMillis() - startTime));

        return report;
    }


    private AssetItem getAssetItem(RuleAsset asset) throws SerializationException {
        AssetItem assetItem = getAssetService().getRulesRepository().loadAssetByUUID(asset.getUuid());
        ContentHandler contentHandler = ContentManager.getHandler(asset.getFormat());
        contentHandler.storeAssetContent(asset, assetItem);
        return assetItem;
    }

    private AssetVerifier getAssetVerifier(VerifierConfiguration verifierConfiguration, AssetItem assetItem) throws SerializationException {
        return new AssetVerifier(
                VerifierBuilderFactory.newVerifierBuilder().newVerifier(verifierConfiguration),
                assetItem);
    }

}
