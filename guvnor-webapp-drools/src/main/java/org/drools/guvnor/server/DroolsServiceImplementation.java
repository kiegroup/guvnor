/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.repository.RulesRepository;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.annotations.LoggedIn;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Iterator;

@ApplicationScoped
public class DroolsServiceImplementation
        implements DroolsService {

    private static final LoggingHelper log = LoggingHelper.getLogger(DroolsService.class);

    private final RulesRepository rulesRepository;

    private final ServiceSecurity serviceSecurity;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private RepositoryAssetService repositoryAssetService;


    @Inject
    public DroolsServiceImplementation(RulesRepository rulesRepository, ServiceSecurity serviceSecurity) {
        this.rulesRepository = rulesRepository;
        this.serviceSecurity = serviceSecurity;
    }

    /**
     * This will create a new Guided Decision Table asset. The initial state
     * will be the draft state. Returns the UUID of the asset. The new Asset
     * will be SAVED and CHECKED-IN.
     */
    @WebRemote
    @LoggedIn
    //@Restrict("#{identity.checkPermission(new PackageNameType( packageName ),initialPackage)}")
    public String createNewRule(NewGuidedDecisionTableAssetConfiguration configuration) throws SerializationException {
        String assetName = configuration.getAssetName();
        String description = configuration.getDescription();
        String initialCategory = configuration.getInitialCategory();
        String packageName = configuration.getPackageName();
        String format = configuration.getFormat();

        //Create the asset
        String uuid = repositoryService.createNewRule(assetName,
                description,
                initialCategory,
                packageName,
                format);

        //Set the Table Format and check-in
        //TODO Is it possible to alter the content and save without checking-in?
        Asset asset = repositoryAssetService.loadRuleAsset(uuid);
        GuidedDecisionTable52 content = (GuidedDecisionTable52) asset.getContent();
        content.setTableFormat(configuration.getTableFormat());
        asset.setCheckinComment("Table Format automatically set to [" + configuration.getTableFormat().toString() + "]");
        repositoryAssetService.checkinVersion(asset);

        return uuid;
    }

    @WebRemote
    @LoggedIn
    public ValidatedResponse validateModule(Module data) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid(data.getUuid());
        log.info("USER:" + getCurrentUserName() + " validateModule module [" + data.getName() + "]");

        RuleBaseCache.getInstance().remove(data.getUuid());
        BRMSSuggestionCompletionLoader loader = createBRMSSuggestionCompletionLoader();
        loader.getSuggestionEngine(rulesRepository.loadModule(data.getName()),
                data.getHeader());

        return validateBRMSSuggestionCompletionLoaderResponse(loader);
    }

    BRMSSuggestionCompletionLoader createBRMSSuggestionCompletionLoader() {
        return new BRMSSuggestionCompletionLoader();
    }

    private ValidatedResponse validateBRMSSuggestionCompletionLoaderResponse(BRMSSuggestionCompletionLoader loader) {
        ValidatedResponse res = new ValidatedResponse();
        if (loader.hasErrors()) {
            res.hasErrors = true;
            String err = "";
            for (Iterator iter = loader.getErrors().iterator(); iter.hasNext(); ) {
                err += (String) iter.next();
                if (iter.hasNext()) err += "\n";
            }
            res.errorHeader = "Package validation errors";
            res.errorMessage = err;
        }
        return res;
    }

    private String getCurrentUserName() {
        return rulesRepository.getSession().getUserID();
    }
}
