/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.ide.common.server.util.upgrade;

import org.drools.ide.common.client.modeldriven.brl.ActionCallMethod;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;

/**
 * Utility class to support upgrades of the RuleModel model. Release pre-5.2
 */
public class RuleModelUpgradeHelper1
    implements
    IUpgradeHelper<RuleModel, RuleModel> {

    public RuleModel upgrade(RuleModel model) {
        updateMetadata( model );
        updateMethodCall( model );
        return model;
    }

    //Fixme, hack for a upgrade to add Metadata
    private void updateMetadata(RuleModel model) {
        if ( model.metadataList == null ) {
            model.metadataList = new RuleMetadata[0];
        }
    }

    // The way method calls are done changed after 5.0.0.CR1 so every rule done
    // before that needs to be updated.
    private RuleModel updateMethodCall(RuleModel model) {

        for ( int i = 0; i < model.rhs.length; i++ ) {
            if ( model.rhs[i] instanceof ActionCallMethod ) {
                ActionCallMethod action = (ActionCallMethod) model.rhs[i];
                // Check if method name is filled, if not this was made with an older Guvnor version
                if ( action.methodName == null || "".equals( action.methodName ) ) {
                    if ( action.fieldValues != null && action.fieldValues.length >= 1 ) {
                        action.methodName = action.fieldValues[0].field;

                        action.fieldValues = new ActionFieldValue[0];
                        action.state = ActionCallMethod.TYPE_DEFINED;
                    }
                }
            }
        }

        return model;
    }

}
