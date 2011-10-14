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
package org.drools.ide.common.server.util;

import org.drools.ide.common.client.modeldriven.brl.ActionCallMethod;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;

/**
 * Utility class to support upgrades of the RuleModel model
 */
public class RuleModelUpgradeHelper
    implements
    IUpgradeHelper<RuleModel, RuleModel> {

    public RuleModel upgrade(RuleModel model) {
        updateMetadata( model );
        updateMethodCall( model );
        updateConnectiveConstraints( model );
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

    //Connective Constraints in 5.2.0 were changed to include the Fact and Field to which they relate.
    //While this should not be necessary (as a ConnectiveConstraint is a further constraint on an already
    //known Fact and Field) it became essential as a hack to have ConnectiveConstraints on sub-fields
    //in a Pattern when an Expression is not used. This codes ensures ConnectiveConstraints on legacy
    //repositories have the fields correctly set.
    private void updateConnectiveConstraints(RuleModel model) {
        for ( IPattern p : model.lhs ) {
            fixConnectiveConstraints( p );
        }
    }

    //Descent into the model
    private void fixConnectiveConstraints(IPattern p) {
        if ( p instanceof FactPattern ) {
            fixConnectiveConstraints( (FactPattern) p );
        } else if ( p instanceof CompositeFactPattern ) {
            fixConnectiveConstraints( (CompositeFactPattern) p );
        }
    }

    private void fixConnectiveConstraints(FactPattern p) {
        for ( FieldConstraint fc : p.getFieldConstraints() ) {
            fixConnectiveConstraints( fc );
        }
    }

    private void fixConnectiveConstraints(CompositeFactPattern p) {
        for ( IPattern sp : p.getPatterns() ) {
            fixConnectiveConstraints( sp );
        }
    }

    private void fixConnectiveConstraints(FieldConstraint fc) {
        if ( fc instanceof SingleFieldConstraint ) {
            fixConnectiveConstraints( (SingleFieldConstraint) fc );
        } else if ( fc instanceof CompositeFieldConstraint ) {
            fixConnectiveConstraints( (CompositeFieldConstraint) fc );
        }
    }

    private void fixConnectiveConstraints(SingleFieldConstraint sfc) {
        if ( sfc.connectives == null ) {
            return;
        }
        for ( ConnectiveConstraint cc : sfc.connectives ) {
            if ( cc.getFieldName() == null ) {
                cc.setFieldName( sfc.getFieldName() );
                cc.setFieldType( sfc.getFieldType() );
            }
        }
    }

    private void fixConnectiveConstraints(CompositeFieldConstraint cfc) {
        if ( cfc.constraints == null ) {
            return;
        }
        for ( FieldConstraint fc : cfc.constraints ) {
            fixConnectiveConstraints( fc );
        }
    }

}
