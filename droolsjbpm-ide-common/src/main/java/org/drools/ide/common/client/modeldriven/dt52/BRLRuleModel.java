/*
 * Copyright 2012 JBoss Inc
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
package org.drools.ide.common.client.modeldriven.dt52;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.CEPWindow;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

/**
 * A RuleModel that can provide details of bound Facts and Fields from an
 * associated Decision Table. This allows columns using BRL fragments to
 * integrate with Decision Table columns
 */
public class BRLRuleModel extends RuleModel {

    private static final long     serialVersionUID = 540l;

    private GuidedDecisionTable52 dtable;

    public BRLRuleModel(GuidedDecisionTable52 dtable) {
        if ( dtable == null ) {
            throw new NullPointerException( "dtable cannot be null" );
        }
        this.dtable = dtable;
    }

    @Override
    public List<String> getLHSBoundFacts() {
        List<String> facts = new ArrayList<String>();
        for ( CompositeColumn< ? extends BaseColumn> col : dtable.getConditions() ) {
            if ( col instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) col;
                if ( p.isBound() ) {
                    facts.add( p.getBoundName() );
                }
            } else if ( col instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) col;
                for ( IPattern p : brl.getDefinition() ) {
                    if ( p instanceof FactPattern ) {
                        final FactPattern fp = (FactPattern) p;
                        if ( fp.isBound() ) {
                            facts.add( fp.getBoundName() );
                        }
                    }
                }
            }
        }
        return facts;
    }

    @Override
    public FactPattern getLHSBoundFact(String var) {
        for ( CompositeColumn< ? extends BaseColumn> col : dtable.getConditions() ) {
            if ( col instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) col;
                if ( p.isBound() && p.getBoundName().equals( var ) ) {
                    return new Pattern52FactPatternAdaptor( p );
                }
            } else if ( col instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) col;
                for ( IPattern p : brl.getDefinition() ) {
                    if ( p instanceof FactPattern ) {
                        final FactPattern fp = (FactPattern) p;
                        if ( fp.isBound() && fp.getBoundName().equals( var ) ) {
                            return fp;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public FieldConstraint getLHSBoundField(String var) {
        for ( CompositeColumn< ? extends BaseColumn> col : dtable.getConditions() ) {
            if ( col instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) col;
                for ( ConditionCol52 cc : p.getChildColumns() ) {
                    if ( cc.isBound() && cc.getBinding().equals( var ) ) {
                        return new ConditionCol52FieldConstraintAdaptor( cc );
                    }
                }
            } else if ( col instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) col;
                for ( IPattern p : brl.getDefinition() ) {
                    if ( p instanceof FactPattern ) {
                        final FactPattern fp = (FactPattern) p;
                        for ( FieldConstraint fc : fp.getFieldConstraints() ) {
                            List<String> fieldBindings = getFieldBinding( fc );
                            if ( fieldBindings.contains( var ) ) {
                                return fc;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getLHSBindingType(String var) {
        for ( CompositeColumn< ? extends BaseColumn> col : dtable.getConditions() ) {
            if ( col instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) col;
                if ( p.isBound() && p.getBoundName().equals( var ) ) {
                    return p.getFactType();
                }
                for ( ConditionCol52 cc : p.getChildColumns() ) {
                    if ( cc.isBound() && cc.getBinding().equals( var ) ) {
                        return cc.getFieldType();
                    }
                }

            } else if ( col instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) col;
                for ( IPattern p : brl.getDefinition() ) {
                    if ( p instanceof FactPattern ) {
                        final FactPattern fp = (FactPattern) p;
                        if ( fp.isBound() && fp.getBoundName().equals( var ) ) {
                            return fp.getFactType();
                        }
                        for ( FieldConstraint fc : fp.getFieldConstraints() ) {
                            String type = getFieldBinding( fc,
                                                           var );
                            if ( type != null ) {
                                return type;
                            }
                        }

                    }
                }
            }
        }
        return null;
    }

    @Override
    public FactPattern getLHSParentFactPatternForBinding(String var) {
        for ( CompositeColumn< ? extends BaseColumn> col : dtable.getConditions() ) {
            if ( col instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) col;
                if ( p.isBound() && p.getBoundName().equals( var ) ) {
                    return new Pattern52FactPatternAdaptor( p );
                }
                for ( ConditionCol52 cc : p.getChildColumns() ) {
                    if ( cc.isBound() && cc.getBinding().equals( var ) ) {
                        return new Pattern52FactPatternAdaptor( p );
                    }
                }

            } else if ( col instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) col;
                for ( IPattern p : brl.getDefinition() ) {
                    if ( p instanceof FactPattern ) {
                        final FactPattern fp = (FactPattern) p;
                        if ( fp.isBound() && var.equals( fp.getBoundName() ) ) {
                            return fp;
                        }
                        for ( FieldConstraint fc : fp.getFieldConstraints() ) {
                            List<String> fieldBindings = getFieldBinding( fc );
                            if ( fieldBindings.contains( var ) ) {
                                return fp;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<String> getAllVariables() {
        List<String> variables = new ArrayList<String>();
        for ( CompositeColumn< ? extends BaseColumn> col : dtable.getConditions() ) {
            if ( col instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) col;
                if ( p.isBound() ) {
                    variables.add( p.getBoundName() );
                }
                for ( ConditionCol52 cc : p.getChildColumns() ) {
                    if ( cc.isBound() ) {
                        variables.add( cc.getBinding() );
                    }
                }

            } else if ( col instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) col;
                for ( IPattern p : brl.getDefinition() ) {
                    if ( p instanceof FactPattern ) {
                        FactPattern fp = (FactPattern) p;
                        if ( fp.isBound() ) {
                            variables.add( fp.getBoundName() );
                        }

                        for ( FieldConstraint fc : fp.getFieldConstraints() ) {
                            if ( fc instanceof SingleFieldConstraintEBLeftSide ) {
                                SingleFieldConstraintEBLeftSide exp = (SingleFieldConstraintEBLeftSide) fc;
                                if ( exp.getExpressionLeftSide() != null && exp.getExpressionLeftSide().isBound() ) {
                                    variables.add( exp.getExpressionLeftSide().getBinding() );
                                }
                            } else if ( fc instanceof SingleFieldConstraint ) {
                                SingleFieldConstraint sfc = (SingleFieldConstraint) fc;
                                if ( sfc.isBound() ) {
                                    variables.add( sfc.getFieldBinding() );
                                }
                                if ( sfc.getExpressionValue() != null && sfc.getExpressionValue().isBound() ) {
                                    variables.add( sfc.getExpressionValue().getBinding() );
                                }
                            }
                        }
                    }
                }
            }
        }
        for ( ActionCol52 col : dtable.getActionCols() ) {
            if ( col instanceof ActionInsertFactCol52 ) {
                ActionInsertFactCol52 action = (ActionInsertFactCol52) col;
                variables.add( action.getBoundName() );

            } else if ( col instanceof BRLActionColumn ) {
                BRLActionColumn brl = (BRLActionColumn) col;
                for ( IAction a : brl.getDefinition() ) {
                    if ( a instanceof ActionInsertFact ) {
                        ActionInsertFact action = (ActionInsertFact) a;
                        if ( action.isBound() ) {
                            variables.add( action.getBoundName() );
                        }
                    }
                }
            }
        }
        return variables;
    }

    @Override
    public boolean isBoundFactUsed(String binding) {
        for ( ActionCol52 col : dtable.getActionCols() ) {
            if ( col instanceof ActionInsertFactCol52 ) {
                ActionInsertFactCol52 action = (ActionInsertFactCol52) col;
                if ( action.getBoundName().equals( binding ) ) {
                    return true;
                }
            } else if ( col instanceof ActionRetractFactCol52 ) {

                if ( col instanceof LimitedEntryActionRetractFactCol52 ) {

                    //Check whether Limited Entry retraction is bound to Pattern
                    LimitedEntryActionRetractFactCol52 ler = (LimitedEntryActionRetractFactCol52) col;
                    if ( ler.getValue().getStringValue().equals( binding ) ) {
                        return false;
                    }

                } else {

                    //Check whether data for column contains Pattern binding
                    int colIndex = dtable.getAllColumns().indexOf( col );
                    for ( List<DTCellValue52> row : dtable.getData() ) {
                        DTCellValue52 cell = row.get( colIndex );
                        if ( cell != null && cell.getStringValue().equals( binding ) ) {
                            return true;
                        }
                    }
                }

            } else if ( col instanceof BRLActionColumn ) {
                BRLActionColumn brl = (BRLActionColumn) col;
                for ( IAction a : brl.getDefinition() ) {
                    if ( a instanceof ActionSetField ) {
                        final ActionSetField action = (ActionSetField) a;
                        if ( action.variable.equals( binding ) ) {
                            return true;
                        }
                    } else if ( a instanceof ActionRetractFact ) {
                        final ActionRetractFact action = (ActionRetractFact) a;
                        if ( action.variableName.equals( binding ) ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> getBoundVariablesInScope(BaseSingleFieldConstraint con) {
        List<String> variables = new ArrayList<String>();
        for ( CompositeColumn< ? extends BaseColumn> col : dtable.getConditions() ) {
            if ( col instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) col;
                if ( p.isBound() ) {
                    variables.add( p.getBoundName() );
                }
                for ( ConditionCol52 cc : p.getChildColumns() ) {
                    if ( cc.isBound() ) {
                        variables.add( cc.getBinding() );
                    }
                }

            } else if ( col instanceof BRLConditionColumn ) {
                //Delegate to super class's implementation
                RuleModel rm = new RuleModel();
                BRLConditionColumn brl = (BRLConditionColumn) col;
                rm.lhs = brl.getDefinition().toArray( new IPattern[brl.getDefinition().size()] );
                variables.addAll( rm.getBoundVariablesInScope( con ) );
            }
        }

        return variables;
    }

    @Override
    public boolean isVariableNameUsed(String s) {
        return super.isVariableNameUsed( s );
    }

    @Override
    public List<String> getRHSBoundFacts() {
        final List<String> variables = new ArrayList<String>();
        for ( ActionCol52 col : dtable.getActionCols() ) {
            if ( col instanceof ActionInsertFactCol52 ) {
                ActionInsertFactCol52 action = (ActionInsertFactCol52) col;
                variables.add( action.getBoundName() );

            } else if ( col instanceof BRLActionColumn ) {
                BRLActionColumn brl = (BRLActionColumn) col;
                for ( IAction a : brl.getDefinition() ) {
                    if ( a instanceof ActionInsertFact ) {
                        ActionInsertFact action = (ActionInsertFact) a;
                        if ( action.isBound() ) {
                            variables.add( action.getBoundName() );
                        }
                    }
                }
            }
        }
        return variables;
    }

    @Override
    public ActionInsertFact getRHSBoundFact(String var) {
        for ( ActionCol52 col : dtable.getActionCols() ) {
            if ( col instanceof ActionInsertFactCol52 ) {
                ActionInsertFactCol52 action = (ActionInsertFactCol52) col;
                if ( action.getBoundName().equals( var ) ) {
                    if ( action.isInsertLogical() ) {
                        return new ActionInsertFactCol52ActionInsertLogicalFactAdaptor( action );
                    }
                    return new ActionInsertFactCol52ActionInsertFactAdaptor( action );
                }

            } else if ( col instanceof BRLActionColumn ) {
                BRLActionColumn brl = (BRLActionColumn) col;
                for ( IAction a : brl.getDefinition() ) {
                    if ( a instanceof ActionInsertFact ) {
                        ActionInsertFact action = (ActionInsertFact) a;
                        if ( action.isBound() ) {
                            if ( action.getBoundName().equals( var ) ) {
                                return action;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static class Pattern52FactPatternAdaptor extends FactPattern {

        private static final long serialVersionUID = 540l;

        private Pattern52         pattern;

        private Pattern52FactPatternAdaptor(Pattern52 pattern) {
            if ( pattern == null ) {
                throw new NullPointerException( "pattern cannot be null" );
            }
            this.pattern = pattern;
        }

        @Override
        public boolean isBound() {
            return pattern.isBound();
        }

        @Override
        public String getBoundName() {
            return pattern.getBoundName();
        }

        @Override
        public String getFactType() {
            return pattern.getFactType();
        }

        @Override
        public boolean isNegated() {
            return pattern.isNegated();
        }

        @Override
        public void setBoundName(String boundName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNegated(boolean isNegated) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addConstraint(FieldConstraint constraint) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeConstraint(int idx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FieldConstraint[] getFieldConstraints() {
            throw new UnsupportedOperationException();
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void setFieldConstraints(List sortedConstraints) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFactType(String factType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWindow(CEPWindow window) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CEPWindow getWindow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getNumberOfConstraints() {
            throw new UnsupportedOperationException();
        }

    }

    public static class ConditionCol52FieldConstraintAdaptor extends SingleFieldConstraint {

        private static final long serialVersionUID = 540l;

        private ConditionCol52    condition;

        private ConditionCol52FieldConstraintAdaptor(ConditionCol52 condition) {
            if ( condition == null ) {
                throw new NullPointerException( "condition cannot be null" );
            }
            this.condition = condition;
        }

        @Override
        public boolean isBound() {
            return condition.isBound();
        }

        @Override
        public String getFieldBinding() {
            return condition.getBinding();
        }

        @Override
        public String getFieldName() {
            return condition.getFactField();
        }

        @Override
        public String getFieldType() {
            return condition.getFieldType();
        }

        @Override
        public void setFieldBinding(String fieldBinding) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addNewConnective() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeConnective(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFieldName(String fieldName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFieldType(String fieldType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setParent(FieldConstraint parent) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setId(String id) {
            throw new UnsupportedOperationException();
        }

    }

    public static class ActionInsertFactCol52ActionInsertFactAdaptor extends ActionInsertFact {

        private static final long     serialVersionUID = 540l;

        private ActionInsertFactCol52 action;

        private ActionInsertFactCol52ActionInsertFactAdaptor(ActionInsertFactCol52 action) {
            if ( action == null ) {
                throw new NullPointerException( "action cannot be null" );
            }
            this.action = action;
            this.factType = action.getFactType();
            ActionFieldValue afv = new ActionFieldValue();
            afv.field = action.getFactField();
            afv.nature = BaseSingleFieldConstraint.TYPE_LITERAL;
            afv.type = action.getType();
            super.addFieldValue( afv );
        }

        @Override
        public boolean isBound() {
            return !(action.getBoundName() == null || "".equals( action.getBoundName() ));
        }

        @Override
        public String getBoundName() {
            return action.getBoundName();
        }

        @Override
        public void setBoundName(String boundName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeField(int idx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addFieldValue(ActionFieldValue val) {
            throw new UnsupportedOperationException();
        }

    }

    public static class ActionInsertFactCol52ActionInsertLogicalFactAdaptor extends ActionInsertLogicalFact {

        private static final long     serialVersionUID = 540l;

        private ActionInsertFactCol52 action;

        private ActionInsertFactCol52ActionInsertLogicalFactAdaptor(ActionInsertFactCol52 action) {
            if ( action == null ) {
                throw new NullPointerException( "action cannot be null" );
            }
            this.action = action;
            this.factType = action.getFactType();
            ActionFieldValue afv = new ActionFieldValue();
            afv.field = action.getFactField();
            afv.nature = BaseSingleFieldConstraint.TYPE_LITERAL;
            afv.type = action.getType();
            super.addFieldValue( afv );
        }

        @Override
        public boolean isBound() {
            return !(action.getBoundName() == null || "".equals( action.getBoundName() ));
        }

        @Override
        public String getBoundName() {
            return action.getBoundName();
        }

        @Override
        public void setBoundName(String boundName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeField(int idx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addFieldValue(ActionFieldValue val) {
            throw new UnsupportedOperationException();
        }

    }

}
