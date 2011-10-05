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
import org.drools.repository.RulesRepository;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.annotations.LoggedIn;
import org.jboss.seam.security.Identity;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

public class VerificationServiceImplementation extends RemoteServiceServlet implements VerificationService {

    private static final long serialVersionUID = 510l;

    private static final LoggingHelper log = LoggingHelper.getLogger(ServiceImplementation.class);

    private final Verifier defaultVerifier = VerifierBuilderFactory.newVerifierBuilder().newVerifier();
    
    @Inject
    protected ServiceSecurity serviceSecurity;
    
    @Inject
    protected RulesRepository rulesRepository;
    
    @Inject
    protected RepositoryAssetService repositoryAssetService;

    @WebRemote
    @LoggedIn
    public AnalysisReport analysePackage(String packageUUID) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( packageUUID );

        AnalysisReport report = new PackageVerifier(
                defaultVerifier,
                rulesRepository.loadPackageByUUID(packageUUID)
        ).verify();

        defaultVerifier.flushKnowledgeSession();

        return report;
    }

    @WebRemote
    @LoggedIn
    public AnalysisReport verifyAsset(RuleAsset asset,
                                      Set<String> activeWorkingSetIds) throws SerializationException {
        serviceSecurity.checkIsPackageDeveloperOrAnalyst( asset );

        return verify(
                asset,
                VerifierConfigurationFactory.getDefaultConfigurationWithWorkingSetConstraints(
                        loadWorkingSets(activeWorkingSetIds)));
    }

    @WebRemote
    @LoggedIn
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
            return repositoryAssetService.loadRuleAssets(activeWorkingSets.toArray(new String[activeWorkingSets.size()]));
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
        AssetItem assetItem = rulesRepository.loadAssetByUUID(asset.uuid);
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
