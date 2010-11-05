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

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.ServiceImplementation;

/**
 * Creates a new RuleAsset.
 * @author esteban.aliverti
 */
public class NewRuleAssetProvider implements RuleAssetProvider {

    private String packageName;
    private String categoryName; 
    private String ruleName;

    public NewRuleAssetProvider(String packageName, String categoryName, String ruleName) {
        this.packageName = packageName;
        this.categoryName = categoryName;
        this.ruleName = ruleName;
    }
    
    public RuleAsset[] getRuleAssets() throws DetailedSerializationException {
        try {
            //creates a new empty rule with a unique name (this is because
            //multiple clients could be opening the same rule at the same time)
            String ruleUUID = this.getService().createNewRule(ruleName, "created by standalone guided editor", categoryName, packageName, AssetFormats.BUSINESS_RULE);
            RuleAsset newRule = this.getService().loadRuleAsset(ruleUUID);

            return new RuleAsset[]{newRule};
        } catch (SerializationException ex) {
            throw new DetailedSerializationException("Error creating rule asset", ex.getMessage());
        }

    }

    private ServiceImplementation getService() {
        return RepositoryServiceServlet.getService();
    }
}
