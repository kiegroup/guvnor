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
package org.drools.guvnor.client.decisiontable;

import java.util.List;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModellerConfiguration;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.dt52.BRLColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.event.shared.EventBus;

/**
 * An editor for a BRL Condition Columns
 */
public class BRLConditionColumnViewImpl extends AbstractBRLColumnViewImpl<IPattern> {

    public BRLConditionColumnViewImpl(final SuggestionCompletionEngine sce,
                                      final GuidedDecisionTable52 model,
                                      final GenericColumnCommand refreshGrid,
                                      final boolean isNew,
                                      final RuleAsset asset,
                                      final BRLConditionColumn column,
                                      final ClientFactory clientFactory,
                                      final EventBus eventBus) {
        super( sce,
               model,
               isNew,
               asset,
               column,
               clientFactory,
               eventBus );
    }

    protected boolean isHeaderUnique(String header) {
        for ( Pattern52 p : model.getConditionPatterns() ) {
            for ( ConditionCol52 c : p.getConditions() ) {
                if ( c.getHeader().equals( header ) ) return false;
            }
        }
        return true;
    }

    public RuleModel getRuleModel(BRLColumn<IPattern> column) {
        RuleModel ruleModel = new RuleModel();
        List<IPattern> definition = column.getDefinition();
        ruleModel.lhs = definition.toArray( new IPattern[definition.size()] );
        return ruleModel;
    }

    public RuleModellerConfiguration getRuleModellerConfiguration() {
        return new RuleModellerConfiguration( false,
                                              true,
                                              true );
    }

}
