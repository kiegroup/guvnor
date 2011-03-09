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
import org.drools.guvnor.client.decisiontable.cells.RowNumberCell;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.AbstractCellFactory;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridCellValueAdaptor;
import org.drools.guvnor.client.widgets.decoratedgrid.MergableGridWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

/**
 * A Factory to provide the Cells for given coordinate for Decision Tables.
 */
public class DecisionTableCellFactory extends AbstractCellFactory<DTColumnConfig> {

    private static String[]     DIALECTS = {"java", "mvel"};

    // Model used to determine data-types etc for cells
    private GuidedDecisionTable model;

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
                                    MergableGridWidget<DTColumnConfig> grid,
                                    GuidedDecisionTable model) {
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
    public DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, DTColumnConfig> getCell(DTColumnConfig column) {

        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, DTColumnConfig> cell = makeTextCell();

        if ( column instanceof RowNumberCol ) {
            cell = makeRowNumberCell();

        } else if ( column instanceof AttributeCol ) {
            AttributeCol attrCol = (AttributeCol) column;
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
            } else if ( attrName.equals( GuidedDecisionTable.OTHERWISE_ATTR ) ) {
                cell = makeBooleanCell();
            }

        } else if ( column instanceof ConditionCol ) {
            cell = makeNewCell( column );

        } else if ( column instanceof ActionSetFieldCol ) {
            cell = makeNewCell( column );

        } else if ( column instanceof ActionInsertFactCol ) {
            cell = makeNewCell( column );

        }
        
        cell.setMergableGridWidget( grid );
        return cell;

    }

    // Make a new Cell for Dialect columns
    private DecoratedGridCellValueAdaptor<String, DTColumnConfig> makeDialectCell() {
        PopupDropDownEditCell pudd = new PopupDropDownEditCell();
        pudd.setItems( DIALECTS );
        return new DecoratedGridCellValueAdaptor<String, DTColumnConfig>( pudd );
    }

    // Make a new Cell for Condition and Actions columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, DTColumnConfig> makeNewCell(
                                                                                                  DTColumnConfig col) {

        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, DTColumnConfig> cell = makeTextCell();

        // Columns with lists of values, enums etc are always Text (for now)
        String[] vals = model.getValueList( col,
                                            sce );
        if ( vals.length == 0 ) {
            if ( model.isNumeric( col,
                                  sce ) ) {
                cell = makeNumericCell();
            } else if ( model.isBoolean( col,
                                         sce ) ) {
                cell = makeBooleanCell();
            } else if ( model.isDate( col,
                                      sce ) ) {
                cell = makeDateCell();
            }
        } else {
            PopupDropDownEditCell pudd = new PopupDropDownEditCell();
            pudd.setItems( vals );
            cell = new DecoratedGridCellValueAdaptor<String, DTColumnConfig>( pudd );
        }
        return cell;
    }

    // Make a new Cell for Row Number columns
    private DecoratedGridCellValueAdaptor<BigDecimal, DTColumnConfig> makeRowNumberCell() {
        return new DecoratedGridCellValueAdaptor<BigDecimal, DTColumnConfig>( new RowNumberCell() );
    }

}
