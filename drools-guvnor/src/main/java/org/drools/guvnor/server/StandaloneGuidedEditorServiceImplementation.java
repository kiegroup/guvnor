/*
 * Copyright 2005 JBoss Inc
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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.StandaloneGuidedEditorService;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.repository.RulesRepository;
import org.jboss.seam.annotations.In;


import org.drools.ide.common.server.util.BRXMLPersistence;

/**
 * @author esteban.aliverti
 */
public class StandaloneGuidedEditorServiceImplementation extends RemoteServiceServlet
        implements
        StandaloneGuidedEditorService {

    @In
    public RulesRepository repository;
    private static final long serialVersionUID = 520l;
    private static final LoggingHelper log = LoggingHelper.getLogger(StandaloneGuidedEditorServiceImplementation.class);

    public RulesRepository getRulesRepository() {
        return this.repository;
    }

    private ServiceImplementation getService() {
        return RepositoryServiceServlet.getService();
    }

       
    public RuleAsset[] loadRuleAssetsFromSession() throws DetailedSerializationException{
        
        HttpSession session = this.getThreadLocalRequest().getSession();
        
        String packageName = (String)session.getAttribute(GuidedEditorServlet.GE_PACKAGE_PARAMETER_NAME);
        String categoryName = (String)session.getAttribute(GuidedEditorServlet.GE_CATEGORY_PARAMETER_NAME);
        String[] initialBRL = (String[])session.getAttribute(GuidedEditorServlet.GE_BRL_PARAMETER_NAME);
        
        List<RuleModel> models = new ArrayList<RuleModel>(initialBRL.length);
        List<RuleAsset> assets = new ArrayList<RuleAsset>(initialBRL.length);
        
        //We wan't to avoid inconsistent states, that is why we first unmarshal
        //each brl and then (if nothing fails) create each rule
        for (String brl : initialBRL) {
            //convert the BRL to RuleModel and update the rule
            models.add(BRXMLPersistence.getInstance().unmarshal(brl));
        }
        
        //no unmarshal errors, it's time to create the rules
        try{
            for (RuleModel ruleModel : models) {
                assets.add(this.createRuleAssetFromRuleModel(packageName, categoryName, ruleModel));
            }
        } catch (Exception e){
            //if something failed, delete the generated assets
            for (RuleAsset ruleAsset : assets) {
                this.getService().removeAsset(ruleAsset.uuid);
            }
            
            if (e instanceof DetailedSerializationException){
                throw (DetailedSerializationException)e;
            }
            
            throw new DetailedSerializationException("Error creating assets", e.getMessage());
        }
        
        return assets.toArray(new RuleAsset[assets.size()]);
        
    }
    
    private RuleAsset createRuleAssetFromRuleModel(String packageName, String categoryName, RuleModel model) throws DetailedSerializationException {

        try {
            //creates a new empty rule with a unique name (this is because
            //multiple clients could be opening the same rule at the same time)
            String ruleUUID = this.getService().createNewRule(model.name+System.nanoTime(), "imported from BRL", categoryName, packageName, AssetFormats.BUSINESS_RULE);
            RuleAsset newRule = this.getService().loadRuleAsset(ruleUUID);
            
            //update its content and persist
            newRule.content = model;
            ruleUUID = this.getService().checkinVersion(newRule);

            if (ruleUUID == null) {
                throw new IllegalStateException("Failed checking int the new version");
            }

            return this.getService().loadRuleAsset(ruleUUID);
        } catch (Exception ex) {
            log.error("Unable to create Rule: " + ex.getMessage());
            throw new DetailedSerializationException("Unable to create Rule",
                    ex.getMessage());
        }

    }
}
