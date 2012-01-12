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

import java.util.Map;

import org.drools.ide.common.client.modeldriven.FieldNature;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
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
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

/**
 * A Rule Model Visitor to extract Interpolation Variables (Template Keys)
 */
public class RuleModelVisitor {

    private IFactPattern                        factPattern;
    private RuleModel                           model = new RuleModel();
    private Map<InterpolationVariable, Integer> vars;

    public RuleModelVisitor(Map<InterpolationVariable, Integer> vars) {
        this.vars = vars;
    }

    public RuleModelVisitor(IPattern pattern,
                            Map<InterpolationVariable, Integer> vars) {
        this.vars = vars;
        this.model.addLhsItem( pattern );
    }

    public RuleModelVisitor(IAction action,
                            Map<InterpolationVariable, Integer> vars) {
        this.vars = vars;
        this.model.addRhsItem( action );
    }

    private void parseStringPattern(String text) {
        if ( text == null || text.length() == 0 ) {
            return;
        }
        int pos = 0;
        while ( (pos = text.indexOf( "@{",
                                     pos )) != -1 ) {
            int end = text.indexOf( '}',
                                    pos + 2 );
            if ( end != -1 ) {
                String varName = text.substring( pos + 2,
                                                 end );
                pos = end + 1;
                InterpolationVariable var = new InterpolationVariable( varName,
                                                                       SuggestionCompletionEngine.TYPE_OBJECT );
                if ( !vars.containsKey( var ) ) {
                    vars.put( var,
                              vars.size() );
                }
            }
        }
    }

    public void visit(Object o) {
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
        } else if ( o instanceof ActionUpdateField ) {
            visitActionFieldList( (ActionUpdateField) o );
        } else if ( o instanceof ActionSetField ) {
            visitActionFieldList( (ActionSetField) o );
        }
    }

    //ActionInsertFact, ActionSetField, ActionpdateField
    private void visitActionFieldList(ActionInsertFact afl) {
        String factType = afl.factType;
        for ( ActionFieldValue afv : afl.fieldValues ) {
            if ( afv.nature == FieldNature.TYPE_TEMPLATE && !vars.containsKey( afv.value ) ) {
                InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                       afv.getType(),
                                                                       factType,
                                                                       afv.getField() );
                vars.put( var,
                          vars.size() );
            }
        }
    }

    private void visitActionFieldList(ActionSetField afl) {
        String factType = model.getLHSBindingType( afl.variable );
        for ( ActionFieldValue afv : afl.fieldValues ) {
            if ( afv.nature == FieldNature.TYPE_TEMPLATE && !vars.containsKey( afv.value ) ) {
                InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                       afv.getType(),
                                                                       factType,
                                                                       afv.getField() );
                vars.put( var,
                          vars.size() );
            }
        }
    }

    private void visitActionFieldList(ActionUpdateField afl) {
        String factType = model.getLHSBindingType( afl.variable );
        for ( ActionFieldValue afv : afl.fieldValues ) {
            if ( afv.nature == FieldNature.TYPE_TEMPLATE && !vars.containsKey( afv.value ) ) {
                InterpolationVariable var = new InterpolationVariable( afv.getValue(),
                                                                       afv.getType(),
                                                                       factType,
                                                                       afv.getField() );
                vars.put( var,
                          vars.size() );
            }
        }
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

    //TODO Handle definition and value
    private void visitDSLSentence(final DSLSentence sentence) {
        String text = sentence.getDefinition();
        parseStringPattern( text );
    }

    private void visitFactPattern(FactPattern pattern) {
        this.factPattern = pattern;
        for ( FieldConstraint fc : pattern.getFieldConstraints() ) {
            visit( fc );
        }
    }

    private void visitFreeFormLine(FreeFormLine ffl) {
        parseStringPattern( ffl.text );
    }

    private void visitFromAccumulateCompositeFactPattern(FromAccumulateCompositeFactPattern pattern) {
        visit( pattern.getFactPattern() );
        visit( pattern.getSourcePattern() );

        parseStringPattern( pattern.getActionCode() );
        parseStringPattern( pattern.getInitCode() );
        parseStringPattern( pattern.getReverseCode() );
    }

    private void visitFromCollectCompositeFactPattern(FromCollectCompositeFactPattern pattern) {
        visit( pattern.getFactPattern() );
        visit( pattern.getRightPattern() );
    }

    private void visitFromCompositeFactPattern(FromCompositeFactPattern pattern) {
        visit( pattern.getFactPattern() );
        parseStringPattern( pattern.getExpression().getText() );
    }

    private void visitRuleModel(RuleModel model) {
        this.model = model;
        if ( model.lhs != null ) {
            for ( IPattern pat : model.lhs ) {
                visit( pat );
            }
        }
        if ( model.rhs != null ) {
            for ( IAction action : model.rhs ) {
                visit( action );
            }
        }
    }

    private void visitSingleFieldConstraint(SingleFieldConstraint sfc) {
        if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == sfc.getConstraintValueType() && !vars.containsKey( sfc.getValue() ) ) {
            InterpolationVariable var = new InterpolationVariable( sfc.getValue(),
                                                                   sfc.getFieldType(),
                                                                   factPattern.getFactType(),
                                                                   sfc.getFieldName() );
            vars.put( var,
                      vars.size() );
        }

        //Visit Connection constraints
        if ( sfc.connectives != null ) {
            for ( int i = 0; i < sfc.connectives.length; i++ ) {
                final ConnectiveConstraint cc = sfc.connectives[i];
                if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == cc.getConstraintValueType() && !vars.containsKey( cc.getValue() ) ) {
                    InterpolationVariable var = new InterpolationVariable( cc.getValue(),
                                                                           cc.getFieldType(),
                                                                           factPattern.getFactType(),
                                                                           cc.getFieldName() );
                    vars.put( var,
                              vars.size() );
                }
            }
        }
    }

    private void visitSingleFieldConstraint(SingleFieldConstraintEBLeftSide sfexp) {
        if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == sfexp.getConstraintValueType() && !vars.containsKey( sfexp.getValue() ) ) {
            InterpolationVariable var = new InterpolationVariable( sfexp.getValue(),
                                                                   sfexp.getExpressionLeftSide().getGenericType(),
                                                                   factPattern.getFactType(),
                                                                   sfexp.getFieldName() );
            vars.put( var,
                      vars.size() );
        }

        //Visit Connection constraints
        if ( sfexp.connectives != null ) {
            for ( int i = 0; i < sfexp.connectives.length; i++ ) {
                final ConnectiveConstraint cc = sfexp.connectives[i];
                if ( BaseSingleFieldConstraint.TYPE_TEMPLATE == cc.getConstraintValueType() && !vars.containsKey( cc.getValue() ) ) {
                    InterpolationVariable var = new InterpolationVariable( cc.getValue(),
                                                                           sfexp.getExpressionLeftSide().getGenericType(),
                                                                           factPattern.getFactType(),
                                                                           cc.getFieldName() );
                    vars.put( var,
                              vars.size() );
                }
            }
        }

    }

}
