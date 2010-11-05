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

package org.drools.guvnor.server.guidededitor;

import java.util.ArrayList;
import java.util.List;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRXMLPersistence;

/**
 * BRL -> RuleAsset converter used by standalone guided editor.
 * For each brl provided, a new RuleAsset is created. The name of the RuleAsset
 * will be the name present in the brl concatenated with a unique number.
 * @author esteban.aliverti
 */
public class BRLRuleAssetProvider implements RuleAssetProvider {
    
    private String packageName;
    private String categoryName; 
    private String[] initialBRLs;

    public BRLRuleAssetProvider(String packageName, String categoryName, String[] initialBRLs) {
        this.packageName = packageName;
        this.categoryName = categoryName;
        this.initialBRLs = initialBRLs;
    }
    
    public RuleAsset[] getRuleAssets() throws DetailedSerializationException {
        
        List<RuleModel> models = new ArrayList<RuleModel>(initialBRLs.length);
        List<RuleAsset> assets = new ArrayList<RuleAsset>(initialBRLs.length);
        
        //We wan't to avoid inconsistent states, that is why we first unmarshal
        //each brl and then (if nothing fails) create each rule
        for (String brl : initialBRLs) {
            //convert the BRL to RuleModel
            models.add(BRXMLPersistence.getInstance().unmarshal(brl));
        }
        
        //no unmarshal errors, it's time to create the rules
        try{
            for (RuleModel ruleModel : models) {
                assets.add(this.createRuleAssetFromRuleModel(ruleModel));
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
    
    /**
     * Creates a new RuleAsset from a RuleModel. The name of the RuleAsset will
     * be the original name plus a unique number.
     * @return
     * @throws DetailedSerializationException
     */
    private RuleAsset createRuleAssetFromRuleModel(RuleModel model) throws DetailedSerializationException {

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
            throw new DetailedSerializationException("Unable to create Rule",
                    ex.getMessage());
        }

    }
    
    private ServiceImplementation getService() {
        return RepositoryServiceServlet.getService();
    }

}
