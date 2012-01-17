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
package org.drools.guvnor.client.decisiontable.widget;

import java.math.BigDecimal;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.decisiontable.cells.AnalysisCell;
import org.drools.guvnor.client.decisiontable.cells.PopupBoundPatternDropDownEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupDropDownEditCell;
import org.drools.guvnor.client.decisiontable.cells.RowNumberCell;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractCellFactory;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DecoratedGridCellValueAdaptor;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.Analysis;
import org.drools.ide.common.client.modeldriven.dt52.AnalysisCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLRuleModel;
import org.drools.ide.common.client.modeldriven.dt52.BaseColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryBRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;

import com.google.gwt.event.shared.EventBus;

/**
 * A Factory to provide the Cells for given coordinate for Decision Tables.
 */
public class DecisionTableCellFactory extends AbstractCellFactory<BaseColumn> {

    private static String[]       DIALECTS = {"java", "mvel"};

    private GuidedDecisionTable52 model;

    /**
     * Construct a Cell Factory for a specific Decision Table
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param model
     *            The Decision Table model to assist data-type derivation
     * @param eventBus
     *            An EventBus on which cells can subscribe to events
     */
    public DecisionTableCellFactory(SuggestionCompletionEngine sce,
                                    EventBus eventBus) {
        super( sce,
               eventBus );
    }

    /**
     * Set the model for which cells will be created
     * 
     * @param model
     */
    public void setModel(GuidedDecisionTable52 model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        this.model = model;
    }

    /**
     * Create a Cell for the given DTColumnConfig
     * 
     * @param column
     *            The Decision Table model column
     * @return A Cell
     */
    public DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> getCell(BaseColumn column) {

        //This is the cell that will be used to edit values; its type can differ to the "fieldType" 
        //of the underlying model. For example a "Guvnor-enum" requires a drop-down list of potential 
        //values whereas the "fieldType" may be a String. 
        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell = makeTextCell();

        if ( column instanceof RowNumberCol52 ) {
            cell = makeRowNumberCell();

        } else if ( column instanceof AttributeCol52 ) {
            AttributeCol52 attrCol = (AttributeCol52) column;
            String attrName = attrCol.getAttribute();
            if ( attrName.equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                if ( attrCol.isUseRowNumber() ) {
                    cell = makeRowNumberCell();
                } else {
                    cell = makeNumericCell();
                }
            } else if ( attrName.equals( RuleAttributeWidget.ENABLED_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( RuleAttributeWidget.NO_LOOP_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( RuleAttributeWidget.DURATION_ATTR ) ) {
                cell = makeNumericCell();
            } else if ( attrName.equals( RuleAttributeWidget.AUTO_FOCUS_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( RuleAttributeWidget.LOCK_ON_ACTIVE_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( RuleAttributeWidget.DATE_EFFECTIVE_ATTR ) ) {
                cell = makeDateCell();
            } else if ( attrName.equals( RuleAttributeWidget.DATE_EXPIRES_ATTR ) ) {
                cell = makeDateCell();
            } else if ( attrName.equals( RuleAttributeWidget.DIALECT_ATTR ) ) {
                cell = makeDialectCell();
            } else if ( attrName.equals( GuidedDecisionTable52.NEGATE_RULE_ATTR ) ) {
                cell = makeBooleanCell();
            }

        } else if ( column instanceof BRLConditionVariableColumn ) {
            cell = derieveCellFromCondition( (BRLConditionVariableColumn) column );

        } else if ( column instanceof ConditionCol52 ) {
            cell = derieveCellFromCondition( (ConditionCol52) column );

        } else if ( column instanceof ActionWorkItemSetFieldCol52 ) {
            //Before ActionSetFieldCol52 as this is a sub-class
            cell = makeBooleanCell();

        } else if ( column instanceof ActionWorkItemInsertFactCol52 ) {
            //Before ActionInsertFactCol52 as this is a sub-class
            cell = makeBooleanCell();

        } else if ( column instanceof ActionSetFieldCol52 ) {
            cell = derieveCellFromAction( (ActionSetFieldCol52) column );

        } else if ( column instanceof ActionInsertFactCol52 ) {
            cell = derieveCellFromAction( (ActionInsertFactCol52) column );

        } else if ( column instanceof ActionRetractFactCol52 ) {
            cell = derieveCellFromAction( (ActionRetractFactCol52) column );

        } else if ( column instanceof ActionWorkItemCol52 ) {
            cell = makeBooleanCell();

        } else if ( column instanceof LimitedEntryBRLActionColumn ) {
            cell = derieveCellFromAction( (LimitedEntryBRLActionColumn) column );

        } else if ( column instanceof BRLActionVariableColumn ) {
            cell = derieveCellFromAction( (BRLActionVariableColumn) column );

        } else if ( column instanceof AnalysisCol52 ) {
            cell = makeRowAnalysisCell();
        }

        return cell;

    }

    // Make a new Cell for Condition columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromCondition(ConditionCol52 col) {

        //Limited Entry are simply boolean
        if ( col instanceof LimitedEntryCol ) {
            return makeBooleanCell();
        }

        //Operators "is null" and "is not null" require a boolean cell
        if ( col.getOperator() != null && (col.getOperator().equals( "== null" ) || col.getOperator().equals( "!= null" )) ) {
            return makeBooleanCell();
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for Actions columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromAction(ActionCol52 col) {

        //Limited Entry are simply boolean
        if ( col instanceof LimitedEntryCol ) {
            return makeBooleanCell();
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for Actions columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromAction(ActionRetractFactCol52 col) {

        //Limited Entry are simply boolean
        if ( col instanceof LimitedEntryCol ) {
            return makeBooleanCell();
        }

        //Drop down of possible patterns
        PopupBoundPatternDropDownEditCell pudd = new PopupBoundPatternDropDownEditCell( eventBus );
        BRLRuleModel rm = new BRLRuleModel( model );
        pudd.setFactBindings( rm.getLHSBoundFacts() );
        return new DecoratedGridCellValueAdaptor<String>( pudd,
                                                          eventBus );
    }

    //Get Cell applicable to Model's data-type
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromModel(DTColumnConfig52 col) {

        //Extended Entry...
        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell = makeTextCell();
        String type = model.getType( col,
                                     sce );

        //Retrieve "Guvnor" enums
        String[] vals = model.getValueList( col,
                                            sce );
        if ( vals.length == 0 ) {

            //Null means the field is free-format
            if ( type == null ) {
                return cell;
            }

            if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
                cell = makeNumericCell();
            } else if ( type.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
                cell = makeBooleanCell();
            } else if ( type.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
                cell = makeDateCell();
            }
        } else {

            // Columns with lists of values, enums etc are always Text (for now)
            PopupDropDownEditCell pudd = new PopupDropDownEditCell();
            pudd.setItems( vals );
            cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                              eventBus );
        }
        return cell;
    }

    // Make a new Cell for Dialect columns
    private DecoratedGridCellValueAdaptor<String> makeDialectCell() {
        PopupDropDownEditCell pudd = new PopupDropDownEditCell();
        pudd.setItems( DIALECTS );
        return new DecoratedGridCellValueAdaptor<String>( pudd,
                                                          eventBus );
    }

    // Make a new Cell for Row Number columns
    private DecoratedGridCellValueAdaptor<BigDecimal> makeRowNumberCell() {
        return new DecoratedGridCellValueAdaptor<BigDecimal>( new RowNumberCell(),
                                                              eventBus );
    }

    // Make a new Cell for Rule Analysis columns
    private DecoratedGridCellValueAdaptor<Analysis> makeRowAnalysisCell() {
        return new DecoratedGridCellValueAdaptor<Analysis>( new AnalysisCell(),
                                                            eventBus );
    }

}
