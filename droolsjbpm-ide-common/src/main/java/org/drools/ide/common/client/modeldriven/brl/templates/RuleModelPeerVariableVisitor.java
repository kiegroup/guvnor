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
package org.drools.ide.common.client.modeldriven.brl.templates;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.FieldNature;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldList;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

/**
 * A Rule Model Visitor to extract InterpolationVariables that are defined on
 * the same Pattern or Action as the provided base variable. This is used to get
 * a list of InterpolationVariables (or literal values) used to drive dependent
 * enumerations.
 */
public class RuleModelPeerVariableVisitor {

    private final TemplateModel     model;
    private final String            baseVariableName;
    private final List<ValueHolder> peerVariables = new ArrayList<ValueHolder>();

    //Container for extracted InterpolationVariable or literal value
    public static class ValueHolder {

        private final String fieldName;
        private final String value;
        private final Type   type;

        public enum Type {
            VALUE,
            TEMPLATE_KEY
        }

        ValueHolder(String fieldName,
                    String value,
                    Type type) {
            this.fieldName = fieldName;
            this.value = value;
            this.type = type;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public String getValue() {
            return this.value;
        }

        public Type getType() {
            return this.type;
        }
    }

    public RuleModelPeerVariableVisitor(final TemplateModel model,
                                        final String baseVariableName) {
        this.model = model;
        this.baseVariableName = baseVariableName;
    }

    public List<ValueHolder> getPeerVariables() {
        visit( this.model );
        return this.peerVariables;
    }

    private void visit(Object o) {
        if ( o == null ) {
            return;
        }
        if ( o instanceof RuleModel ) {
            visitRuleModel( (RuleModel) o );
        } else if ( o instanceof FactPattern ) {
            visitFactPattern( (FactPattern) o );
        } else if ( o instanceof CompositeFieldConstraint ) {
            visitCompositeFieldConstraint( (CompositeFieldConstraint) o );
        } else if ( o instanceof SingleFieldConstraintEBLeftSide ) {
            visitSingleFieldConstraint( (SingleFieldConstraintEBLeftSide) o );
        } else if ( o instanceof SingleFieldConstraint ) {
            visitSingleFieldConstraint( (SingleFieldConstraint) o );
        } else if ( o instanceof CompositeFactPattern ) {
            visitCompositeFactPattern( (CompositeFactPattern) o );
        } else if ( o instanceof FromAccumulateCompositeFactPattern ) {
            visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) o );
        } else if ( o instanceof FromCollectCompositeFactPattern ) {
            visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) o );
        } else if ( o instanceof FromCompositeFactPattern ) {
            visitFromCompositeFactPattern( (FromCompositeFactPattern) o );
        } else if ( o instanceof ActionFieldList ) {
            visitActionFieldList( (ActionFieldList) o );
        }
    }

    private void visitRuleModel(final RuleModel rm) {
        for ( IPattern p : rm.lhs ) {
            visit( p );
        }
        for ( IAction a : rm.rhs ) {
            visit( a );
        }
    }

    private void visitFactPattern(final FactPattern fp) {
        if ( isParentFactPattern( fp ) ) {
            for ( FieldConstraint fc : fp.getFieldConstraints() ) {
                visit( fc );
            }
        }
    }

    private boolean isParentFactPattern(final FactPattern fp) {
        boolean isParent = false;
        for ( FieldConstraint fc : fp.getFieldConstraints() ) {
            if ( fc instanceof SingleFieldConstraint ) {
                final SingleFieldConstraint sfc = (SingleFieldConstraint) fc;
                if ( sfc.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_TEMPLATE ) {
                    if ( sfc.getValue().equals( this.baseVariableName ) ) {
                        isParent = true;
                        break;
                    }
                }
            }
        }
        return isParent;
    }

    private void visitCompositeFactPattern(final CompositeFactPattern cfp) {
        for ( IPattern p : cfp.getPatterns() ) {
            visit( p );
        }
    }

    private void visitCompositeFieldConstraint(final CompositeFieldConstraint cfc) {
        for ( FieldConstraint fc : cfc.constraints ) {
            visit( fc );
        }
    }

    private void visitFromAccumulateCompositeFactPattern(final FromAccumulateCompositeFactPattern pattern) {
        if ( pattern.factPattern != null ) {
            visit( pattern.factPattern );
        }
    }

    private void visitFromCollectCompositeFactPattern(final FromCollectCompositeFactPattern pattern) {
        if ( pattern.factPattern != null ) {
            visit( pattern.factPattern );
        }
    }

    private void visitFromCompositeFactPattern(final FromCompositeFactPattern pattern) {
        if ( pattern.factPattern != null ) {
            visit( pattern.factPattern );
        }
    }

    private void visitSingleFieldConstraint(final SingleFieldConstraint sfc) {
        if ( sfc.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_TEMPLATE ) {
            this.peerVariables.add( new ValueHolder( sfc.getFieldName(),
                                                     sfc.getValue(),
                                                     ValueHolder.Type.TEMPLATE_KEY ) );
        } else {
            this.peerVariables.add( new ValueHolder( sfc.getFieldName(),
                                                     sfc.getValue(),
                                                     ValueHolder.Type.VALUE ) );
        }
    }

    private void visitSingleFieldConstraint(final SingleFieldConstraintEBLeftSide sfexp) {
        if ( sfexp.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_TEMPLATE ) {
            this.peerVariables.add( new ValueHolder( sfexp.getFieldName(),
                                                     sfexp.getValue(),
                                                     ValueHolder.Type.TEMPLATE_KEY ) );
        } else {
            this.peerVariables.add( new ValueHolder( sfexp.getFieldName(),
                                                     sfexp.getValue(),
                                                     ValueHolder.Type.VALUE ) );
        }
    }

    private void visitActionFieldList(final ActionFieldList afl) {
        boolean addVariables = false;
        List<ValueHolder> variables = new ArrayList<ValueHolder>();
        for ( ActionFieldValue afv : afl.fieldValues ) {
            if ( afv.nature == FieldNature.TYPE_TEMPLATE ) {
                if ( afv.getValue().equals( this.baseVariableName ) ) {
                    addVariables = true;
                }
                ValueHolder vh = new ValueHolder( afv.getField(),
                                                   afv.getValue(),
                                                   ValueHolder.Type.TEMPLATE_KEY );
                variables.add( vh );
            } else {
                ValueHolder vh = new ValueHolder( afv.getField(),
                                                   afv.getValue(),
                                                   ValueHolder.Type.VALUE );
                variables.add( vh );
            }
        }
        if ( addVariables ) {
            this.peerVariables.addAll( variables );
        }

    }

}
