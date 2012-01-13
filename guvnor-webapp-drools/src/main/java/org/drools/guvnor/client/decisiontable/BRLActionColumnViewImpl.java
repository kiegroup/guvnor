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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModellerConfiguration;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.templates.InterpolationVariable;
import org.drools.ide.common.client.modeldriven.brl.templates.RuleModelCloneVisitor;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLRuleModel;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.event.shared.EventBus;

/**
 * An editor for a BRL Action Columns
 */
public class BRLActionColumnViewImpl extends AbstractBRLColumnViewImpl<IAction, BRLActionVariableColumn>
    implements
    BRLActionColumnView {

    private Presenter presenter;

    public BRLActionColumnViewImpl(final SuggestionCompletionEngine sce,
                                   final GuidedDecisionTable52 model,
                                   final boolean isNew,
                                   final Asset asset,
                                   final BRLActionColumn column,
                                   final ClientFactory clientFactory,
                                   final EventBus eventBus) {
        super( sce,
               model,
               isNew,
               asset,
               column,
               clientFactory,
               eventBus );

        setTitle( constants.ActionBRLFragmentConfiguration() );
    }

    protected boolean isHeaderUnique(String header) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) return false;
        }
        return true;
    }

    protected BRLRuleModel getRuleModel(BRLColumn<IAction, BRLActionVariableColumn> column) {
        BRLRuleModel ruleModel = new BRLRuleModel( model );
        List<IAction> definition = column.getDefinition();
        ruleModel.rhs = definition.toArray( new IAction[definition.size()] );
        return ruleModel;
    }

    protected RuleModellerConfiguration getRuleModellerConfiguration() {
        return new RuleModellerConfiguration( true,
                                              false,
                                              true );
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void doInsertColumn() {
        this.editingCol.setDefinition( Arrays.asList( this.ruleModel.rhs ) );
        presenter.insertColumn( (BRLActionColumn) this.editingCol );
    }

    @Override
    protected void doUpdateColumn() {
        this.editingCol.setDefinition( Arrays.asList( this.ruleModel.rhs ) );
        presenter.updateColumn( (BRLActionColumn) this.originalCol,
                                (BRLActionColumn) this.editingCol );
    }

    @Override
    protected List<BRLActionVariableColumn> convertInterpolationVariables(Map<InterpolationVariable, Integer> ivs) {

        //Convert to columns for use in the Decision Table
        BRLActionVariableColumn[] variables = new BRLActionVariableColumn[ivs.size()];
        for ( Map.Entry<InterpolationVariable, Integer> me : ivs.entrySet() ) {
            InterpolationVariable iv = me.getKey();
            int index = me.getValue();
            BRLActionVariableColumn variable = new BRLActionVariableColumn( iv.getVarName(),
                                                                            iv.getDataType(),
                                                                            iv.getFactType(),
                                                                            iv.getFactField() );
            variable.setHeader( editingCol.getHeader() );
            variable.setHideColumn( editingCol.isHideColumn() );
            variables[index] = variable;
        }
        return Arrays.asList( variables );
    }

    @Override
    protected BRLColumn<IAction, BRLActionVariableColumn> cloneBRLColumn(BRLColumn<IAction, BRLActionVariableColumn> col) {
        BRLActionColumn clone = new BRLActionColumn();
        clone.setHeader( col.getHeader() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setChildColumns( cloneVariables( col.getChildColumns() ) );
        clone.setDefinition( cloneDefinition( col.getDefinition() ) );
        return clone;
    }

    private List<BRLActionVariableColumn> cloneVariables(List<BRLActionVariableColumn> variables) {
        List<BRLActionVariableColumn> clone = new ArrayList<BRLActionVariableColumn>();
        for ( BRLActionVariableColumn variable : variables ) {
            clone.add( cloneVariable( variable ) );
        }
        return clone;
    }

    private BRLActionVariableColumn cloneVariable(BRLActionVariableColumn variable) {
        BRLActionVariableColumn clone = new BRLActionVariableColumn( variable.getVarName(),
                                                                     variable.getFieldType(),
                                                                     variable.getFactType(),
                                                                     variable.getFactField() );
        clone.setHeader( variable.getHeader() );
        clone.setHideColumn( variable.isHideColumn() );
        clone.setWidth( variable.getWidth() );
        return clone;
    }

    private List<IAction> cloneDefinition(List<IAction> definition) {
        RuleModelCloneVisitor visitor = new RuleModelCloneVisitor();
        RuleModel rm = new RuleModel();
        for ( IAction action : definition ) {
            rm.addRhsItem( action );
        }
        RuleModel rmClone = visitor.visitRuleModel( rm );
        List<IAction> clone = new ArrayList<IAction>();
        for ( IAction action : rmClone.rhs ) {
            clone.add( action );
        }
        return clone;
    }

}
