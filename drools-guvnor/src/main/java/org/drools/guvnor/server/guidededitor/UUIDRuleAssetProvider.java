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
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;

/**
 * Creates a new RuleAsset.
 * @author esteban.aliverti
 */
public class UUIDRuleAssetProvider implements RuleAssetProvider {

    private static final LoggingHelper log = LoggingHelper.getLogger(UUIDRuleAssetProvider.class);

    public RuleAsset[] getRuleAssets(String packageName, String categoryName, Object assetsUUIDs, Boolean hideLHSInEditor, Boolean hideRHSInEditor, Boolean hideAttributesInEditor) throws DetailedSerializationException {
        try {
            //assetsUUIDs must be a String[]
            if (!(assetsUUIDs instanceof String[])) {
                throw new IllegalArgumentException("Expected String[] and not " + assetsUUIDs.getClass().getName());
            }

            String[] uuids = (String[]) assetsUUIDs;
            RuleAsset[] assets = new RuleAsset[uuids.length];
            
            for (int i = 0; i < uuids.length; i++) {
                String uuid = uuids[i];
                assets[i] = this.getService().loadRuleAsset(uuid);
            }
            
            //update its content and reload
            for (int i = 0; i < assets.length; i++) {
                RuleAsset ruleAsset = assets[i];
                RuleModel model = (RuleModel) ruleAsset.content;
                model.updateMetadata(new RuleMetadata(RuleMetadata.HIDE_LHS_IN_EDITOR, hideLHSInEditor.toString()));
                model.updateMetadata(new RuleMetadata(RuleMetadata.HIDE_RHS_IN_EDITOR, hideRHSInEditor.toString()));
                model.updateMetadata(new RuleMetadata(RuleMetadata.HIDE_ATTRIBUTES_IN_EDITOR, hideAttributesInEditor.toString()));
                String ruleUUID = this.getService().checkinVersion(ruleAsset);
                assets[i] = this.getService().loadRuleAsset(ruleUUID);
            }
            
            return assets;
        } catch (SerializationException ex) {
            throw new DetailedSerializationException("Error creating rule asset", ex.getMessage());
        }


    }

    private ServiceImplementation getService() {
        return RepositoryServiceServlet.getService();
    }
}
