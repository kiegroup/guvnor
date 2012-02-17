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

import org.drools.guvnor.client.decisiontable.cells.PopupDropDownEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupTextEditCell;
import org.drools.guvnor.client.decisiontable.cells.RowNumberCell;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.AbstractCellFactory;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridCellValueAdaptor;
import org.drools.guvnor.client.widgets.decoratedgrid.MergableGridWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;

/**
 * A Factory to provide the Cells for given coordinate for Decision Tables.
 */
public class DecisionTableCellFactory extends AbstractCellFactory<DTColumnConfig52> {

    private static String[]       DIALECTS = {"java", "mvel"};

    // Model used to determine data-types etc for cells
    private GuidedDecisionTable52 model;

    /**
     * Construct a Cell Factory for a specific Decision Table
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param grid
     *            MergableGridWidget to which cells will send their updates
     * @param model
     *            The Decision Table model to assist data-type derivation
     */
    public DecisionTableCellFactory(SuggestionCompletionEngine sce,
                                    MergableGridWidget<DTColumnConfig52> grid,
                                    GuidedDecisionTable52 model) {
        super( sce,
               grid );
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
    public DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> getCell(DTColumnConfig52 column) {

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
            } else if ( attrName.equals( GuidedDecisionTable52.TIMER_ATTR ) ) {
                cell = makeTimerCell();
            } else if ( attrName.equals( GuidedDecisionTable52.CALENDARS_ATTR ) ) {
                cell = makeCalendarsCell();
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

        } else if ( column instanceof ConditionCol52 ) {
            cell = derieveNewCellFromModel( column );

        } else if ( column instanceof ActionSetFieldCol52 ) {
            cell = derieveNewCellFromModel( column );

        } else if ( column instanceof ActionInsertFactCol52 ) {
            cell = derieveNewCellFromModel( column );

        }

        cell.setMergableGridWidget( grid );
        return cell;

    }

    // Make a new Cell for Condition and Actions columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveNewCellFromModel(DTColumnConfig52 col) {

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
            cell = new DecoratedGridCellValueAdaptor<String>( pudd );
        }
        return cell;
    }

    // Make a new Cell for Dialect columns
    private DecoratedGridCellValueAdaptor<String> makeDialectCell() {
        PopupDropDownEditCell pudd = new PopupDropDownEditCell();
        pudd.setItems( DIALECTS );
        return new DecoratedGridCellValueAdaptor<String>( pudd );
    }

    // Make a new Cell for Timer columns
    private DecoratedGridCellValueAdaptor<String> makeTimerCell() {
        return new DecoratedGridCellValueAdaptor<String>( new PopupTextEditCell() );
    }

    // Make a new Cell for Calendars columns
    private DecoratedGridCellValueAdaptor<String> makeCalendarsCell() {
        return new DecoratedGridCellValueAdaptor<String>( new PopupTextEditCell() );
    }

    // Make a new Cell for Row Number columns
    private DecoratedGridCellValueAdaptor<BigDecimal> makeRowNumberCell() {
        return new DecoratedGridCellValueAdaptor<BigDecimal>( new RowNumberCell() );
    }

}
