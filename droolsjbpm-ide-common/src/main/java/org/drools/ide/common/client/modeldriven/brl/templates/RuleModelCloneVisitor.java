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
package org.drools.ide.common.client.modeldriven.brl.templates;

import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

/**
 * A Rule Model Visitor to create a clone
 * 
 * TODO Clone LHS of model...
 */
public class RuleModelCloneVisitor {

    private RuleModel clone;

    private void visit(Object o) {
        if ( o == null ) {
            return;
        }
        if ( o instanceof RuleModel ) {
            visitRuleModel( (RuleModel) o );
        } else if ( o instanceof RuleAttribute ) {
            visitRuleAttribute( (RuleAttribute) o );
        } else if ( o instanceof RuleMetadata ) {
            visitRuleMetadata( (RuleMetadata) o );
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
        } else if ( o instanceof FreeFormLine ) {
            visitFreeFormLine( (FreeFormLine) o );
        } else if ( o instanceof FromAccumulateCompositeFactPattern ) {
            visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) o );
        } else if ( o instanceof FromCollectCompositeFactPattern ) {
            visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) o );
        } else if ( o instanceof FromCompositeFactPattern ) {
            visitFromCompositeFactPattern( (FromCompositeFactPattern) o );
        } else if ( o instanceof DSLSentence ) {
            visitDSLSentence( (DSLSentence) o );
        } else if ( o instanceof ActionInsertFact ) {
            visitActionFieldList( (ActionInsertFact) o );
        } else if ( o instanceof ActionSetField ) {
            visitActionFieldList( (ActionSetField) o );
        } else if ( o instanceof ActionUpdateField ) {
            visitActionFieldList( (ActionUpdateField) o );
        }
    }

    private void visitRuleAttribute(RuleAttribute attr) {
        RuleAttribute attrClone = new RuleAttribute();
        attrClone.attributeName = attr.attributeName;
        attrClone.value = attr.value;
        this.clone.addAttribute( attrClone );
    }

    private void visitRuleMetadata(RuleMetadata md) {
        RuleMetadata mdClone = new RuleMetadata();
        mdClone.attributeName = md.attributeName;
        mdClone.value = md.value;
        this.clone.addMetadata( mdClone );
    }

    //ActionInsertFact, ActionSetField, ActionpdateField
    private void visitActionFieldList(ActionInsertFact afl) {
        ActionInsertFact aflClone = new ActionInsertFact();
        aflClone.factType = afl.factType;
        aflClone.setBoundName( afl.getBoundName() );
        for ( ActionFieldValue afv : afl.fieldValues ) {
            ActionFieldValue afvClone = new ActionFieldValue();
            afvClone.setField( afv.getField() );
            afvClone.setNature( afv.getNature() );
            afvClone.setType( afv.getType() );
            afvClone.setValue( afv.getValue() );
            aflClone.addFieldValue( afvClone );
        }
        this.clone.addRhsItem( aflClone );
    }

    private void visitActionFieldList(ActionSetField afl) {
        ActionSetField aflClone = new ActionSetField();
        aflClone.variable = afl.variable;
        for ( ActionFieldValue afv : afl.fieldValues ) {
            ActionFieldValue afvClone = new ActionFieldValue();
            afvClone.setField( afv.getField() );
            afvClone.setNature( afv.getNature() );
            afvClone.setType( afv.getType() );
            afvClone.setValue( afv.getValue() );
            aflClone.addFieldValue( afvClone );
        }
        this.clone.addRhsItem( aflClone );
    }

    private void visitActionFieldList(ActionUpdateField afl) {
        ActionUpdateField aflClone = new ActionUpdateField();
        aflClone.variable = afl.variable;
        for ( ActionFieldValue afv : afl.fieldValues ) {
            ActionFieldValue afvClone = new ActionFieldValue();
            afvClone.setField( afv.getField() );
            afvClone.setNature( afv.getNature() );
            afvClone.setType( afv.getType() );
            afvClone.setValue( afv.getValue() );
            aflClone.addFieldValue( afvClone );
        }
        this.clone.addRhsItem( aflClone );
    }

    private void visitCompositeFactPattern(CompositeFactPattern pattern) {
        if ( pattern.getPatterns() != null ) {
            for ( IFactPattern fp : pattern.getPatterns() ) {
                visit( fp );
            }
        }
    }

    private void visitCompositeFieldConstraint(CompositeFieldConstraint cfc) {
        if ( cfc.constraints != null ) {
            for ( FieldConstraint fc : cfc.constraints ) {
                visit( fc );
            }
        }
    }

    private void visitDSLSentence(final DSLSentence sentence) {
        String text = sentence.getDefinition();
    }

    private void visitFactPattern(FactPattern pattern) {
        for ( FieldConstraint fc : pattern.getFieldConstraints() ) {
            visit( fc );
        }
    }

    private void visitFreeFormLine(FreeFormLine ffl) {
    }

    private void visitFromAccumulateCompositeFactPattern(FromAccumulateCompositeFactPattern pattern) {
        visit( pattern.getFactPattern() );
        visit( pattern.getSourcePattern() );
        pattern.getActionCode();
        pattern.getInitCode();
        pattern.getReverseCode();
    }

    private void visitFromCollectCompositeFactPattern(FromCollectCompositeFactPattern pattern) {
        visit( pattern.getFactPattern() );
        visit( pattern.getRightPattern() );
    }

    private void visitFromCompositeFactPattern(FromCompositeFactPattern pattern) {
        visit( pattern.getFactPattern() );
        pattern.getExpression().getText();
    }

    public RuleModel visitRuleModel(RuleModel model) {
        this.clone = new RuleModel();
        this.clone.name = model.name;
        this.clone.parentName = model.parentName;
        this.clone.setNegated( model.isNegated() );

        if ( model.attributes != null ) {
            for ( RuleAttribute attr : model.attributes ) {
                visit( attr );
            }
        }
        if ( model.metadataList != null ) {
            for ( RuleMetadata md : model.metadataList ) {
                visit( md );
            }
        }
        if ( model.lhs != null ) {
            for ( IPattern pattern : model.lhs ) {
                visit( pattern );
            }
        }
        if ( model.rhs != null ) {
            for ( IAction action : model.rhs ) {
                visit( action );
            }
        }
        return this.clone;
    }

    private void visitSingleFieldConstraint(SingleFieldConstraint sfc) {

        //Visit Connection constraints
        if ( sfc.connectives != null ) {
            for ( int i = 0; i < sfc.connectives.length; i++ ) {
                final ConnectiveConstraint cc = sfc.connectives[i];
            }
        }
    }

    private void visitSingleFieldConstraint(SingleFieldConstraintEBLeftSide sfexp) {

        //Visit Connection constraints
        if ( sfexp.connectives != null ) {
            for ( int i = 0; i < sfexp.connectives.length; i++ ) {
                final ConnectiveConstraint cc = sfexp.connectives[i];
            }
        }

    }

}
