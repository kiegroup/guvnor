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
package org.drools.guvnor.client.decisiontable.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellTableDropDownDataValueMapProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicData;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicDataRow;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.templates.RuleModelPeerVariableVisitor;
import org.drools.ide.common.client.modeldriven.brl.templates.RuleModelPeerVariableVisitor.ValueHolder;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BaseColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.cell.client.Cell.Context;

/**
 * A utility class to get the values of all Constraints\Actions in the scope of
 * a Template Key to drive dependent enumerations. A value is in scope if it is
 * on a Constraint or Action on the same Pattern of the base column.
 */
public class DecisionTableDropDownManager
    implements
    CellTableDropDownDataValueMapProvider {

    private final SuggestionCompletionEngine sce;
    private GuidedDecisionTable52            model;
    private DynamicData                      data;

    public DecisionTableDropDownManager(final SuggestionCompletionEngine sce) {
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.sce = sce;
    }

    public DecisionTableDropDownManager(final GuidedDecisionTable52 model,
                                        final DynamicData data,
                                        final SuggestionCompletionEngine sce) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        if ( data == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.model = model;
        this.data = data;
        this.sce = sce;
    }

    public void setModel(GuidedDecisionTable52 model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        this.model = model;
    }

    @Override
    public void setData(DynamicData data) {
        if ( data == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        this.data = data;
    }

    /**
     * Create a map of Field Values keyed on Field Names used by
     * SuggestionCompletionEngine.getEnums(String, String, Map<String, String>)
     * to drive dependent enumerations.
     * 
     * @param context
     *            The Context of the cell being edited containing physical
     *            coordinate in the data-space.
     */
    @Override
    public Map<String, String> getCurrentValueMap(Context context) {
        Map<String, String> currentValueMap = new HashMap<String, String>();

        final int iBaseRowIndex = context.getIndex();
        final int iBaseColIndex = context.getColumn();
        final DynamicDataRow rowData = this.data.get( iBaseRowIndex );

        //Get the column for the cell being edited
        List<BaseColumn> allColumns = this.model.getExpandedColumns();
        BaseColumn baseColumn = allColumns.get( iBaseColIndex );

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if ( baseColumn instanceof BRLConditionVariableColumn ) {
            final BRLConditionVariableColumn baseBRLConditionColumn = (BRLConditionVariableColumn) baseColumn;
            final BRLConditionColumn brl = model.getBRLColumn( baseBRLConditionColumn );
            final RuleModel rm = new RuleModel();
            IPattern[] lhs = new IPattern[brl.getDefinition().size()];
            brl.getDefinition().toArray( lhs );
            rm.lhs = lhs;

            final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor( rm,
                                                                                                       baseBRLConditionColumn.getVarName() );
            List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

            //Add other variables values
            for ( ValueHolder valueHolder : peerVariables ) {
                switch ( valueHolder.getType() ) {
                    case TEMPLATE_KEY :
                        final BRLConditionVariableColumn vc = getConditionVariableColumnIndex( brl.getChildColumns(),
                                                                                               valueHolder.getValue() );
                        final int iCol = model.getExpandedColumns().indexOf( vc );
                        final String field = vc.getFactField();

                        //The generic class CellValue can have different data-types however enumerations
                        //in a Decision Table are always String-based hence we can safely cast to a String
                        //to retrieve the correct String representation of the CellValue's value.
                        final String value = (String) this.data.get( iBaseRowIndex ).get( iCol ).getValue();
                        currentValueMap.put( field,
                                             value );
                        break;
                    case VALUE :
                        currentValueMap.put( valueHolder.getFieldName(),
                                             valueHolder.getValue() );
                }
            }

        } else if ( baseColumn instanceof BRLActionVariableColumn ) {
            final BRLActionVariableColumn baseBRLActionColumn = (BRLActionVariableColumn) baseColumn;
            final BRLActionColumn brl = model.getBRLColumn( baseBRLActionColumn );
            final RuleModel rm = new RuleModel();
            IAction[] rhs = new IAction[brl.getDefinition().size()];
            brl.getDefinition().toArray( rhs );
            rm.rhs = rhs;

            final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor( rm,
                                                                                                       baseBRLActionColumn.getVarName() );
            List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

            //Add other variables values
            for ( ValueHolder valueHolder : peerVariables ) {
                switch ( valueHolder.getType() ) {
                    case TEMPLATE_KEY :
                        final BRLActionVariableColumn vc = getActionVariableColumnIndex( brl.getChildColumns(),
                                                                                         valueHolder.getValue() );
                        final int iCol = model.getExpandedColumns().indexOf( vc );
                        final String field = vc.getFactField();

                        //The generic class CellValue can have different data-types however enumerations
                        //in a Decision Table are always String-based hence we can safely cast to a String
                        //to retrieve the correct String representation of the CellValue's value.
                        final String value = (String) this.data.get( iBaseRowIndex ).get( iCol ).getValue();
                        currentValueMap.put( field,
                                             value );
                        break;
                    case VALUE :
                        currentValueMap.put( valueHolder.getFieldName(),
                                             valueHolder.getValue() );
                }
            }

        } else if ( baseColumn instanceof ConditionCol52 ) {
            final ConditionCol52 baseConditionColumn = (ConditionCol52) baseColumn;
            final Pattern52 basePattern = this.model.getPattern( baseConditionColumn );
            for ( ConditionCol52 cc : basePattern.getChildColumns() ) {
                final int iCol = allColumns.indexOf( cc );
                currentValueMap.put( cc.getFactField(),
                                     getValue( rowData.get( iCol ) ) );
            }

        } else if ( baseColumn instanceof ActionSetFieldCol52 ) {
            ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionSetFieldCol52 ) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if ( asf.getBoundName().equals( binding ) ) {
                        final int iCol = allColumns.indexOf( asf );
                        currentValueMap.put( asf.getFactField(),
                                             getValue( rowData.get( iCol ) ) );
                    }
                }
            }

        } else if ( baseColumn instanceof ActionInsertFactCol52 ) {
            ActionInsertFactCol52 baseActionColumn = (ActionInsertFactCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionInsertFactCol52 ) {
                    final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                    if ( aif.getBoundName().equals( binding ) ) {
                        final int iCol = allColumns.indexOf( aif );
                        currentValueMap.put( aif.getFactField(),
                                             getValue( rowData.get( iCol ) ) );
                    }
                }
            }

        }
        return currentValueMap;
    }

    private String getValue(CellValue< ? extends Comparable< ? >> dcv) {
        if ( dcv == null || dcv.getValue() == null ) {
            return "";
        }
        //The generic class CellValue can have different data-types however enumerations
        //in a Decision Table are always String-based hence we can safely call toString()
        //to retrieve the correct String representation of the CellValue's value.
        return dcv.getValue().toString();
    }

    private BRLConditionVariableColumn getConditionVariableColumnIndex(final List<BRLConditionVariableColumn> definition,
                                                                       final String variableName) {
        for ( BRLConditionVariableColumn vc : definition ) {
            if ( vc.getVarName().equals( variableName ) ) {
                return vc;
            }
        }
        //This should never happen
        throw new IllegalArgumentException( "Variable '" + variableName + "' not found. This suggests an programming error." );
    }

    private BRLActionVariableColumn getActionVariableColumnIndex(final List<BRLActionVariableColumn> definition,
                                                                 final String variableName) {
        for ( BRLActionVariableColumn ac : definition ) {
            if ( ac.getVarName().equals( variableName ) ) {
                return ac;
            }
        }
        //This should never happen
        throw new IllegalArgumentException( "Variable '" + variableName + "' not found. This suggests an programming error." );
    }

    @Override
    public Set<Integer> getDependentColumnIndexes(int iBaseColIndex) {

        final Set<Integer> dependentColumnIndexes = new HashSet<Integer>();

        //Get the column for the cell being edited
        final List<BaseColumn> allColumns = this.model.getExpandedColumns();
        final BaseColumn baseColumn = allColumns.get( iBaseColIndex );

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if ( baseColumn instanceof BRLConditionVariableColumn ) {
            final BRLConditionVariableColumn baseBRLConditionColumn = (BRLConditionVariableColumn) baseColumn;
            final BRLConditionColumn brl = model.getBRLColumn( baseBRLConditionColumn );
            final RuleModel rm = new RuleModel();
            IPattern[] lhs = new IPattern[brl.getDefinition().size()];
            brl.getDefinition().toArray( lhs );
            rm.lhs = lhs;

            final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor( rm,
                                                                                                       baseBRLConditionColumn.getVarName() );
            List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

            //Add other variables values
            for ( ValueHolder valueHolder : peerVariables ) {
                switch ( valueHolder.getType() ) {
                    case TEMPLATE_KEY :
                        if ( sce.isDependentEnum( baseBRLConditionColumn.getFactType(),
                                                  baseBRLConditionColumn.getFactField(),
                                                  valueHolder.getFieldName() ) ) {
                            final BRLConditionVariableColumn vc = getConditionVariableColumnIndex( brl.getChildColumns(),
                                                                                                   valueHolder.getValue() );
                            final int iCol = model.getExpandedColumns().indexOf( vc );
                            dependentColumnIndexes.add( iCol );
                        }
                        break;
                }
            }

        } else if ( baseColumn instanceof BRLActionVariableColumn ) {
            final BRLActionVariableColumn baseBRLActionColumn = (BRLActionVariableColumn) baseColumn;
            final BRLActionColumn brl = model.getBRLColumn( baseBRLActionColumn );
            final RuleModel rm = new RuleModel();
            IAction[] rhs = new IAction[brl.getDefinition().size()];
            brl.getDefinition().toArray( rhs );
            rm.rhs = rhs;

            final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor( rm,
                                                                                                       baseBRLActionColumn.getVarName() );
            List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

            //Add other variables values
            for ( ValueHolder valueHolder : peerVariables ) {
                switch ( valueHolder.getType() ) {
                    case TEMPLATE_KEY :
                        if ( sce.isDependentEnum( baseBRLActionColumn.getFactType(),
                                                  baseBRLActionColumn.getFactField(),
                                                  valueHolder.getFieldName() ) ) {
                            final BRLActionVariableColumn vc = getActionVariableColumnIndex( brl.getChildColumns(),
                                                                                             valueHolder.getValue() );
                            final int iCol = model.getExpandedColumns().indexOf( vc );
                            dependentColumnIndexes.add( iCol );
                        }
                        break;
                }
            }

        } else if ( baseColumn instanceof ConditionCol52 ) {
            final ConditionCol52 baseConditionColumn = (ConditionCol52) baseColumn;
            final Pattern52 basePattern = this.model.getPattern( baseConditionColumn );
            for ( ConditionCol52 cc : basePattern.getChildColumns() ) {
                if ( sce.isDependentEnum( basePattern.getFactType(),
                                          baseConditionColumn.getFactField(),
                                          cc.getFactField() ) ) {
                    dependentColumnIndexes.add( allColumns.indexOf( cc ) );
                }
            }

        } else if ( baseColumn instanceof ActionSetFieldCol52 ) {
            final ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final Pattern52 basePattern = model.getConditionPattern( baseActionColumn.getBoundName() );
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionSetFieldCol52 ) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if ( asf.getBoundName().equals( binding ) ) {
                        if ( sce.isDependentEnum( basePattern.getFactType(),
                                                  baseActionColumn.getFactField(),
                                                  asf.getFactField() ) ) {
                            dependentColumnIndexes.add( allColumns.indexOf( ac ) );
                        }
                    }
                }
            }

        } else if ( baseColumn instanceof ActionInsertFactCol52 ) {
            final ActionInsertFactCol52 baseActionColumn = (ActionInsertFactCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionInsertFactCol52 ) {
                    final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                    if ( aif.getBoundName().equals( binding ) ) {
                        if ( sce.isDependentEnum( baseActionColumn.getFactType(),
                                                  baseActionColumn.getFactField(),
                                                  aif.getFactField() ) ) {
                            dependentColumnIndexes.add( allColumns.indexOf( ac ) );
                        }
                    }
                }
            }

        }

        return dependentColumnIndexes;
    }

}
