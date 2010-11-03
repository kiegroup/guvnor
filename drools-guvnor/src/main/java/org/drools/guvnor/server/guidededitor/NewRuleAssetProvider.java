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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRXMLPersistence;

/**
 * Creates a new RuleAsset.
 * @author esteban.aliverti
 */
public class NewRuleAssetProvider implements RuleAssetProvider {

    private static final LoggingHelper log = LoggingHelper.getLogger(NewRuleAssetProvider.class);

    public RuleAsset[] getRuleAssets(String packageName, String categoryName, Object ruleName, Boolean hideLHSInEditor, Boolean hideRHSInEditor, Boolean hideAttributesInEditor) throws DetailedSerializationException {
        try {
            //ruleName must be a String
            if (!(ruleName instanceof String)) {
                throw new IllegalArgumentException("Expected String and not " + ruleName.getClass().getName());
            }

            String name = (String) ruleName;

            //creates a new empty rule with a unique name (this is because
            //multiple clients could be opening the same rule at the same time)
            String ruleUUID = this.getService().createNewRule(name, "created by standalone guided editor", categoryName, packageName, AssetFormats.BUSINESS_RULE);
            RuleAsset newRule = this.getService().loadRuleAsset(ruleUUID);

            //update its content and persist
            RuleModel model = (RuleModel) newRule.content;
            model.updateMetadata(new RuleMetadata(RuleMetadata.HIDE_LHS_IN_EDITOR, hideLHSInEditor.toString()));
            model.updateMetadata(new RuleMetadata(RuleMetadata.HIDE_RHS_IN_EDITOR, hideRHSInEditor.toString()));
            model.updateMetadata(new RuleMetadata(RuleMetadata.HIDE_ATTRIBUTES_IN_EDITOR, hideAttributesInEditor.toString()));
            ruleUUID = this.getService().checkinVersion(newRule);

            if (ruleUUID == null) {
                throw new IllegalStateException("Failed checking int the new version");
            }

            newRule = this.getService().loadRuleAsset(ruleUUID);
            
            return new RuleAsset[]{newRule};
        } catch (SerializationException ex) {
            throw new DetailedSerializationException("Error creating rule asset", ex.getMessage());
        }


    }

    private ServiceImplementation getService() {
        return RepositoryServiceServlet.getService();
    }
}
