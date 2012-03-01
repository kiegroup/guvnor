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

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.decisiontable.cells.AnalysisCell;
import org.drools.guvnor.client.decisiontable.cells.PopupBoundPatternDropDownEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupDialectDropDownEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupDropDownEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupTextEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupValueListDropDownEditCell;
import org.drools.guvnor.client.decisiontable.cells.RowNumberCell;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractCellFactory;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellTableDropDownDataValueMapProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DecoratedGridCellValueAdaptor;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
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
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;

import com.google.gwt.event.shared.EventBus;

/**
 * A Factory to provide the Cells for given coordinate for Decision Tables.
 */
public class DecisionTableCellFactory extends AbstractCellFactory<BaseColumn> {

    private GuidedDecisionTable52                 model;
    private CellTableDropDownDataValueMapProvider dropDownManager;

    /**
     * Construct a Cell Factory for a specific Decision Table
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param isReadOnly
     *            Should cells be created for a read-only mode of operation
     * @param eventBus
     *            An EventBus on which cells can subscribe to events
     */
    public DecisionTableCellFactory(SuggestionCompletionEngine sce,
                                    boolean isReadOnly,
                                    EventBus eventBus) {
        super( sce,
               isReadOnly,
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
     * Set the DropDownManager
     * 
     * @param dropDownManager
     */
    public void setDropDownManager(CellTableDropDownDataValueMapProvider dropDownManager) {
        if ( dropDownManager == null ) {
            throw new IllegalArgumentException( "dropDownManager cannot be null" );
        }
        this.dropDownManager = dropDownManager;
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
                    cell = makeNumericIntegerCell();
                }
            } else if ( attrName.equals( GuidedDecisionTable52.ENABLED_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( GuidedDecisionTable52.NO_LOOP_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( GuidedDecisionTable52.DURATION_ATTR ) ) {
                cell = makeNumericLongCell();
            } else if ( attrName.equals( GuidedDecisionTable52.TIMER_ATTR ) ) {
                cell = makeTimerCell();
            } else if ( attrName.equals( GuidedDecisionTable52.CALENDARS_ATTR ) ) {
                cell = makeCalendarsCell();
            } else if ( attrName.equals( GuidedDecisionTable52.AUTO_FOCUS_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( GuidedDecisionTable52.LOCK_ON_ACTIVE_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( GuidedDecisionTable52.DATE_EFFECTIVE_ATTR ) ) {
                cell = makeDateCell();
            } else if ( attrName.equals( GuidedDecisionTable52.DATE_EXPIRES_ATTR ) ) {
                cell = makeDateCell();
            } else if ( attrName.equals( GuidedDecisionTable52.DIALECT_ATTR ) ) {
                cell = makeDialectCell();
            } else if ( attrName.equals( GuidedDecisionTable52.NEGATE_RULE_ATTR ) ) {
                cell = makeBooleanCell();
            }

        } else if ( column instanceof LimitedEntryCol ) {
            cell = makeBooleanCell();

        } else if ( column instanceof BRLConditionVariableColumn ) {
            //Before ConditionCol52 as this is a sub-class
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

        } else if ( column instanceof BRLActionVariableColumn ) {
            cell = derieveCellFromAction( (BRLActionVariableColumn) column );

        } else if ( column instanceof AnalysisCol52 ) {
            cell = makeRowAnalysisCell();
        }

        return cell;

    }

    // Make a new Cell for Condition columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromCondition(ConditionCol52 col) {

        //Operators "is null" and "is not null" require a boolean cell
        if ( col.getOperator() != null && (col.getOperator().equals( "== null" ) || col.getOperator().equals( "!= null" )) ) {
            return makeBooleanCell();
        }

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        final String factType = model.getPattern( col ).getFactType();
        final String fieldName = col.getFactField();
        if ( model.hasValueList( col ) ) {
            return makeValueListCell( col );

        } else if ( sce.hasEnums( factType,
                                  fieldName ) ) {
            return makeEnumCell( factType,
                                 fieldName );
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for BRLConditionVariableColumn columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromCondition(BRLConditionVariableColumn col) {

        //Check if the column has an enumeration
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        if ( sce.hasEnums( factType,
                           fieldName ) ) {
            return makeEnumCell( factType,
                                 fieldName );
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for ActionSetField columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromAction(ActionSetFieldCol52 col) {

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        final String factType = model.getBoundFactType( col.getBoundName() );
        final String fieldName = col.getFactField();
        if ( model.hasValueList( col ) ) {
            return makeValueListCell( col );

        } else if ( sce.hasEnums( factType,
                                  fieldName ) ) {
            return makeEnumCell( factType,
                                 fieldName );
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for ActionInsertFact columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromAction(ActionInsertFactCol52 col) {

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        if ( model.hasValueList( col ) ) {
            return makeValueListCell( col );

        } else if ( sce.hasEnums( factType,
                                  fieldName ) ) {
            return makeEnumCell( factType,
                                 fieldName );
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for ActionRetractFactCol52 columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromAction(ActionRetractFactCol52 col) {

        //Drop down of possible patterns
        PopupBoundPatternDropDownEditCell pudd = new PopupBoundPatternDropDownEditCell( eventBus,
                                                                                        isReadOnly );
        BRLRuleModel rm = new BRLRuleModel( model );
        pudd.setFactBindings( rm.getLHSBoundFacts() );
        return new DecoratedGridCellValueAdaptor<String>( pudd,
                                                          eventBus );
    }

    // Make a new Cell for BRLActionVariableColumn columns
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromAction(BRLActionVariableColumn col) {

        //Check if the column has an enumeration
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        if ( sce.hasEnums( factType,
                           fieldName ) ) {
            return makeEnumCell( factType,
                                 fieldName );
        }

        return derieveCellFromModel( col );
    }

    //Get Cell applicable to Model's data-type
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> derieveCellFromModel(DTColumnConfig52 col) {

        //Extended Entry...
        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell = makeTextCell();

        //Get a cell based upon the data-type
        String type = model.getType( col,
                                     sce );

        if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            cell = makeNumericCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BIGDECIMAL ) ) {
            cell = makeNumericBigDecimalCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BIGINTEGER ) ) {
            cell = makeNumericBigIntegerCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BYTE ) ) {
            cell = makeNumericByteCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_DOUBLE ) ) {
            cell = makeNumericDoubleCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT ) ) {
            cell = makeNumericFloatCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER ) ) {
            cell = makeNumericIntegerCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_LONG ) ) {
            cell = makeNumericLongCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_NUMERIC_SHORT ) ) {
            cell = makeNumericShortCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            cell = makeBooleanCell();
        } else if ( type.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            cell = makeDateCell();
        }

        return cell;
    }

    // Make a new Cell for Dialect columns
    private DecoratedGridCellValueAdaptor<String> makeDialectCell() {
        PopupDialectDropDownEditCell pudd = new PopupDialectDropDownEditCell( isReadOnly );
        return new DecoratedGridCellValueAdaptor<String>( pudd,
                                                          eventBus );
    }

    // Make a new Cell for Row Number columns
    private DecoratedGridCellValueAdaptor<Integer> makeRowNumberCell() {
        return new DecoratedGridCellValueAdaptor<Integer>( new RowNumberCell(),
                                                           eventBus );
    }

    // Make a new Cell for Timer columns
    private DecoratedGridCellValueAdaptor<String> makeTimerCell() {
        return new DecoratedGridCellValueAdaptor<String>( new PopupTextEditCell( isReadOnly ),
                                                          eventBus );
    }

    // Make a new Cell for Calendars columns
    private DecoratedGridCellValueAdaptor<String> makeCalendarsCell() {
        return new DecoratedGridCellValueAdaptor<String>( new PopupTextEditCell( isReadOnly ),
                                                          eventBus );
    }

    // Make a new Cell for Rule Analysis columns
    private DecoratedGridCellValueAdaptor<Analysis> makeRowAnalysisCell() {
        return new DecoratedGridCellValueAdaptor<Analysis>( new AnalysisCell(),
                                                            eventBus );
    }

    //Get a cell for a Value List
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> makeValueListCell(DTColumnConfig52 col) {

        // Columns with "Value Lists" are always Text (for now)
        PopupValueListDropDownEditCell pudd = new PopupValueListDropDownEditCell( model.getValueList( col,
                                                                                                      sce ),
                                                                                  isReadOnly );
        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                                                                                    eventBus );
        return cell;
    }

    //Get a cell for a Value List
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> makeEnumCell(String factType,
                                                                                   String fieldName) {

        // Columns with enumerations are always Text
        PopupDropDownEditCell pudd = new PopupDropDownEditCell( factType,
                                                                fieldName,
                                                                sce,
                                                                dropDownManager,
                                                                isReadOnly );
        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                                                                                    eventBus );
        return cell;
    }

}
