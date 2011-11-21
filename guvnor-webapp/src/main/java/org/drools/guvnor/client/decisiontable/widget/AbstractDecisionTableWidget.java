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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.decisiontable.analysis.DecisionTableAnalyzer;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue.CellState;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.Coordinate;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicData;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicDataRow;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.AppendRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertDecisionTableColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SelectedCellChangeEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetColumnVisibilityEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetGuidedDecisionTableModelEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.UpdateColumnEvent;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.Analysis;
import org.drools.ide.common.client.modeldriven.dt52.AnalysisCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;

/**
 * An abstract Decision Table and the necessary boiler-plate to convert from
 * DTColumnConfig objects to the DynamicData related classes used by the
 * DecoratedGridWidget
 */
public abstract class AbstractDecisionTableWidget extends Composite
    implements
    //TODO {manstis} HasRows<List<DTCellValue52>>,
    //TODO {manstis} HasColumns<DTColumnConfig52>,
    SelectedCellChangeEvent.Handler,
    InsertRowEvent.Handler,
    DeleteRowEvent.Handler,
    AppendRowEvent.Handler,
    DeleteColumnEvent.Handler,
    InsertDecisionTableColumnEvent.Handler<DTColumnConfig52> {

    // Decision Table data
    protected GuidedDecisionTable52                       model;
    protected AbstractDecoratedDecisionTableGridWidget    widget;
    protected SuggestionCompletionEngine                  sce;
    protected DecisionTableCellFactory                    cellFactory;
    protected DecisionTableCellValueFactory               cellValueFactory;
    protected DecisionTableControlsWidget                 dtableCtrls;
    protected final EventBus                              eventBus;

    protected static final DecisionTableResourcesProvider resources = new DecisionTableResourcesProvider();

    /**
     * Constructor
     * 
     * @param sce
     */
    public AbstractDecisionTableWidget(DecisionTableControlsWidget dtableCtrls,
                                       SuggestionCompletionEngine sce,
                                       EventBus eventBus) {

        if ( dtableCtrls == null ) {
            throw new IllegalArgumentException( "dtableControls cannot be null" );
        }
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        this.sce = sce;
        this.dtableCtrls = dtableCtrls;
        this.dtableCtrls.setDecisionTableWidget( this );
        this.eventBus = eventBus;

        //Wire-up the events
        eventBus.addHandler( InsertRowEvent.TYPE,
                             this );
        eventBus.addHandler( DeleteRowEvent.TYPE,
                             this );
        eventBus.addHandler( AppendRowEvent.TYPE,
                             this );
        eventBus.addHandler( SelectedCellChangeEvent.TYPE,
                             this );
        eventBus.addHandler( DeleteColumnEvent.TYPE,
                             this );
        eventBus.addHandler( InsertDecisionTableColumnEvent.TYPE,
                             this );
    }

    /**
     * Add a column to the table, at the appropriate position determined by the
     * column subclass: RowNumberCol, Metadata columns, Attribute columns,
     * Condition columns and lastly Action columns. Condition Ccolumns are
     * further grouped by Pattern.
     * 
     * @param modelColumn
     *            The Decision Table column to insert
     */
    public void addColumn(DTColumnConfig52 modelColumn) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException(
                                                "modelColumn cannot be null." );
        }
        addColumn( modelColumn,
                   true );
    }

    /**
     * Delete the column at the given index
     * 
     * @param index
     */
    public void deleteColumn(int index) {
        deleteColumn( index,
                      true );
    }

    // Delete the column at the given index with optional redraw
    private void deleteColumn(int index,
                              boolean redraw) {
        DeleteColumnEvent dce = new DeleteColumnEvent( index,
                                                       redraw );
        eventBus.fireEvent( dce );
    }

    public void appendRow() {
        AppendRowEvent are = new AppendRowEvent();
        eventBus.fireEvent( are );
    }

    /**
     * Return the SCE associated with this Decision Table
     * 
     * @return
     */
    public SuggestionCompletionEngine getSCE() {
        return this.sce;
    }

    /**
     * Mark a cell as containing the magical "otherwise" value. The magical
     * "otherwise" value has the meaning of all values other than those
     * explicitly defined for this column.
     */
    public void makeOtherwiseCell() {
        List<CellValue< ? >> selections = widget.getSelectedCells();
        CellValue< ? > cell = selections.get( 0 );

        if ( canAcceptOtherwiseValues( cell ) ) {

            //Set "otherwise" property on cell
            for ( CellValue< ? > cv : selections ) {
                cv.addState( CellState.OTHERWISE );
            }
            widget.setSelectedCellsValue( null );
        }
    }

    public void setColumnVisibility(DTColumnConfig52 modelColumn,
                                    boolean isVisible) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null" );
        }
        int index = model.getAllColumns().indexOf( modelColumn );
        SetColumnVisibilityEvent scve = new SetColumnVisibilityEvent( index,
                                                                      isVisible );
        eventBus.fireEvent( scve );
    }

    /**
     * Set the Decision Table's data. This removes all existing columns from the
     * Decision Table and re-creates them based upon the provided data.
     * 
     * @param model
     */
    public void setModel(GuidedDecisionTable52 model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        this.model = model;
        this.cellFactory.setModel( model );
        this.cellValueFactory.setModel( model );

        //Ensure field data-type is set (field did not exist before 5.2)
        for ( Pattern52 p : model.getConditionPatterns() ) {
            for ( ConditionCol52 col : p.getConditions() ) {
                ConditionCol52 cc = (ConditionCol52) col;
                cc.setFieldType( sce.getFieldType( p.getFactType(),
                                                   cc.getFactField() ) );
            }
        }

        //Fire event for UI components to set themselves up
        SetGuidedDecisionTableModelEvent sme = new SetGuidedDecisionTableModelEvent( model );
        eventBus.fireEvent( sme );
    }

    /**
     * Ensure the wrapped DecoratedGridWidget's size is set too
     */
    @Override
    public void setPixelSize(int width,
                             int height) {
        if ( width < 0 ) {
            throw new IllegalArgumentException( "width cannot be less than zero" );
        }
        if ( height < 0 ) {
            throw new IllegalArgumentException( "height cannot be less than zero" );
        }
        super.setPixelSize( width,
                            height );
        widget.setPixelSize( width,
                             height );
    }

    /**
     * Update an ActionSetFieldCol column
     * 
     * @param origColumn
     *            The existing column in the grid
     * @param editColumn
     *            A copy of the original column containing the modified values
     */
    public void updateColumn(final ActionInsertFactCol52 origColumn,
                             final ActionInsertFactCol52 editColumn) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bRedrawColumn = false;
        boolean bRedrawHeader = false;
        boolean bUpdateCellsForDataType = false;
        //TODO {manstis} Needs rewrite to events
        //DynamicColumn<DTColumnConfig52> column = getDynamicColumn( origColumn );

        // Update column's visibility
        if ( origColumn.isHideColumn() != editColumn.isHideColumn() ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Change in column's binding forces an update and redraw if FactType or
        // FactField are different; otherwise only need to update and redraw if
        // the FactType or FieldType have changed
        if ( !isEqualOrNull( origColumn.getBoundName(),
                             editColumn.getBoundName() ) ) {
            if ( !isEqualOrNull( origColumn.getFactType(),
                                 editColumn.getFactType() )
                 || !isEqualOrNull( origColumn.getFactField(),
                                    editColumn.getFactField() ) ) {
                bRedrawColumn = true;
                bRedrawHeader = true;
                bUpdateCellsForDataType = true;
            }

        } else if ( !isEqualOrNull( origColumn.getFactType(),
                                    editColumn.getFactType() )
                    || !isEqualOrNull( origColumn.getFactField(),
                                       editColumn.getFactField() ) ) {
            bRedrawColumn = true;
            bRedrawHeader = true;
            bUpdateCellsForDataType = true;
        }

        // Update column's cell content if the Optional Value list has changed
        if ( !isEqualOrNull( origColumn.getValueList(),
                             editColumn.getValueList() ) ) {
            //TODO {manstis} Needs rewrite to events
            //bRedrawColumn = bRedrawColumn || updateCellsForOptionValueList( editColumn,column );
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Update column field in Header Widget
        if ( origColumn.getFactField() != null && !origColumn.getFactField().equals( editColumn.getFactField() ) ) {
            bRedrawHeader = true;
        }

        // Update LimitedEntryValue in Header Widget
        if ( origColumn instanceof LimitedEntryCol && editColumn instanceof LimitedEntryCol ) {
            LimitedEntryCol lecOrig = (LimitedEntryCol) origColumn;
            LimitedEntryCol lecEditing = (LimitedEntryCol) editColumn;
            if ( !lecOrig.getValue().equals( lecEditing.getValue() ) ) {
                bRedrawHeader = true;
            }
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update model with new cells, if required
        if ( bUpdateCellsForDataType ) {
            //TODO {manstis} Needs rewrite to events
            //updateCellsForDataType( origColumn,column );
        }

        //Redraw columns
        if ( bRedrawColumn ) {
            int maxColumnIndex = model.getAllColumns().size() - 1;
            //TODO {manstis} Needs rewrite to events
            //widget.redrawColumns( column.getColumnIndex(),maxColumnIndex );
        }

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.redrawHeader();
                }
            } );
        }

    }

    /**
     * Update an ActionSetFieldCol column
     * 
     * @param origColumn
     *            The existing column in the grid
     * @param editColumn
     *            A copy of the original column containing the modified values
     */
    public void updateColumn(final ActionSetFieldCol52 origColumn,
                             final ActionSetFieldCol52 editColumn) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bRedrawColumn = false;
        boolean bRedrawHeader = false;
        boolean bUpdateCellsForDataType = false;
        //TODO {manstis} Needs rewrite to events
        //DynamicColumn<DTColumnConfig52> column = getDynamicColumn( origColumn );

        // Update column's visibility
        if ( origColumn.isHideColumn() != editColumn.isHideColumn() ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Change in column's binding forces an update and redraw if FactField
        // is different; otherwise only need to update and redraw if the
        // FieldType has changed
        if ( !isEqualOrNull( origColumn.getBoundName(),
                             editColumn.getBoundName() ) ) {
            if ( !isEqualOrNull( origColumn.getFactField(),
                                   editColumn.getFactField() ) ) {
                bRedrawColumn = true;
                bRedrawHeader = true;
                bUpdateCellsForDataType = true;
            }

        } else if ( !isEqualOrNull( origColumn.getFactField(),
                                       editColumn.getFactField() ) ) {
            bRedrawColumn = true;
            bRedrawHeader = true;
            bUpdateCellsForDataType = true;
        }

        // Update column's cell content if the Optional Value list has changed
        if ( !isEqualOrNull( origColumn.getValueList(),
                             editColumn.getValueList() ) ) {
            //TODO {manstis} Needs rewrite to events
            //bRedrawColumn = bRedrawColumn || updateCellsForOptionValueList( editColumn,column );
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Update column field in Header Widget
        if ( origColumn.getFactField() != null && !origColumn.getFactField().equals( editColumn.getFactField() ) ) {
            bRedrawHeader = true;
        }

        // Update LimitedEntryValue in Header Widget
        if ( origColumn instanceof LimitedEntryCol && editColumn instanceof LimitedEntryCol ) {
            LimitedEntryCol lecOrig = (LimitedEntryCol) origColumn;
            LimitedEntryCol lecEditing = (LimitedEntryCol) editColumn;
            if ( !lecOrig.getValue().equals( lecEditing.getValue() ) ) {
                bRedrawHeader = true;
            }
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update model with new cells, if required
        if ( bUpdateCellsForDataType ) {
            //TODO {manstis} Needs rewrite to events
            //updateCellsForDataType( origColumn,column );
        }

        //Redraw columns
        if ( bRedrawColumn ) {
            int maxColumnIndex = model.getAllColumns().size() - 1;
            //TODO {manstis} Needs rewrite to events
            //widget.redrawColumns( column.getColumnIndex(),maxColumnIndex );
        }

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.redrawHeader();
                }
            } );
        }

    }

    /**
     * Update an ActionWorkItemSetFieldCol52 column
     * 
     * @param origColumn
     *            The existing column in the grid
     * @param editColumn
     *            A copy of the original column containing the modified values
     */
    public void updateColumn(final ActionWorkItemSetFieldCol52 origColumn,
                             final ActionWorkItemSetFieldCol52 editColumn) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bRedrawHeader = false;

        // Update column's visibility
        if ( origColumn.isHideColumn() != editColumn.isHideColumn() ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Change in column's binding forces an update and redraw if FactField
        // is different; otherwise only need to update and redraw if the
        // FieldType has changed
        if ( !isEqualOrNull( origColumn.getBoundName(),
                             editColumn.getBoundName() ) ) {
            if ( !isEqualOrNull( origColumn.getFactField(),
                                 editColumn.getFactField() ) ) {
                bRedrawHeader = true;
            }

        } else if ( !isEqualOrNull( origColumn.getFactField(),
                                    editColumn.getFactField() ) ) {
            bRedrawHeader = true;
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Update column field in Header Widget
        if ( origColumn.getFactField() != null && !origColumn.getFactField().equals( editColumn.getFactField() ) ) {
            bRedrawHeader = true;
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.redrawHeader();
                }
            } );
        }

    }

    /**
     * Update an ActionRetractFactCol52 column
     * 
     * @param origColumn
     *            The existing column in the grid
     * @param editColumn
     *            A copy of the original column containing the modified values
     */
    public void updateColumn(final ActionRetractFactCol52 origColumn,
                             final ActionRetractFactCol52 editColumn) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        // Update column's visibility
        if ( origColumn.isHideColumn() != editColumn.isHideColumn() ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Update column header in Header Widget
        boolean bRedrawHeader = false;
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Update LimitedEntryValue in Header Widget
        if ( origColumn instanceof LimitedEntryCol && editColumn instanceof LimitedEntryCol ) {
            LimitedEntryCol lecOrig = (LimitedEntryCol) origColumn;
            LimitedEntryCol lecEditing = (LimitedEntryCol) editColumn;
            if ( !lecOrig.getValue().equals( lecEditing.getValue() ) ) {
                bRedrawHeader = true;
            }
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.redrawHeader();
                }
            } );
        }

    }

    /**
     * Update an ActionWorkItemCol52 column
     * 
     * @param origColumn
     *            The existing column in the grid
     * @param editColumn
     *            A copy of the original column containing the modified values
     */
    public void updateColumn(final ActionWorkItemCol52 origColumn,
                             final ActionWorkItemCol52 editColumn) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        // Update column's visibility
        if ( origColumn.isHideColumn() != editColumn.isHideColumn() ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Update column header in Header Widget
        boolean bRedrawHeader = false;
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.redrawHeader();
                }
            } );
        }

    }

    /**
     * Update a Condition column
     * 
     * @param origPattern
     *            The existing pattern to which the column related
     * @param origColumn
     *            The existing column in the grid
     * @param editPattern
     *            The new pattern to which the column relates
     * @param editColumn
     *            A copy of the original column containing the modified values
     */
    public void updateColumn(final Pattern52 origPattern,
                             final ConditionCol52 origColumn,
                             final Pattern52 editPattern,
                             final ConditionCol52 editColumn) {
        if ( origPattern == null ) {
            throw new IllegalArgumentException( "origPattern cannot be null" );
        }
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editPattern == null ) {
            throw new IllegalArgumentException( "editPattern cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bRedrawColumn = false;
        boolean bRedrawHeader = false;
        boolean bUpdateCellsForDataType = false;
        //TODO {manstis} Needs rewrite to events
        //DynamicColumn<DTColumnConfig52> column = getDynamicColumn( origColumn );

        // Update column's visibility
        if ( origColumn.isHideColumn() != editColumn.isHideColumn() ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Change in operator
        if ( !isEqualOrNull( origColumn.getOperator(),
                             editColumn.getOperator() ) ) {
            bRedrawHeader = true;

            //Clear otherwise if column cannot accept them
            if ( !canAcceptOtherwiseValues( editColumn ) ) {
                //TODO {manstis} Needs rewrite to events
                //removeOtherwiseStates( column );
                bRedrawColumn = true;
            }
        }

        if ( !isEqualOrNull( origPattern.getBoundName(),
                             editPattern.getBoundName() ) ) {
            // Change in bound name requires column to be repositioned
            bRedrawHeader = true;
            addColumn( editColumn,
                       false );
            int origColIndex = model.getAllColumns().indexOf( origColumn );
            int editColIndex = model.getAllColumns().indexOf( editColumn );

            // If the FactType, FieldType and ConstraintValueType are unchanged
            // we can copy cell values from the old column into the new
            if ( isEqualOrNull( origPattern.getFactType(),
                                editPattern.getFactType() )
                 && isEqualOrNull( origColumn.getFactField(),
                                   editColumn.getFactField() )
                 && origColumn.getConstraintValueType() == editColumn.getConstraintValueType() ) {

                final DynamicData data = widget.getData();
                for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                    DynamicDataRow row = data.get( iRow );
                    CellValue< ? > oldCell = row.get( origColIndex );
                    CellValue< ? > newCell = row.get( editColIndex );
                    newCell.setValue( oldCell.getValue() );
                }
            }

            // Delete old column and redraw
            int columnIndex = model.getAllColumns().indexOf( origColumn );
            deleteColumn( columnIndex,
                          false );
            origColIndex = Math.min( model.getAllColumns().size() - 1,
                                     origColIndex );
            editColIndex = Math.min( model.getAllColumns().size() - 1,
                                     editColIndex );
            if ( editColIndex > origColIndex ) {
                int temp = origColIndex;
                origColIndex = editColIndex;
                editColIndex = temp;
            }
            widget.redrawColumns( editColIndex,
                                  origColIndex );

        } else if ( !isEqualOrNull( origPattern.getFactType(),
                                    editPattern.getFactType() )
                    || !isEqualOrNull( origColumn.getFactField(),
                                       editColumn.getFactField() )
                    || !isEqualOrNull( origColumn.getFieldType(),
                                       editColumn.getFieldType() )
                    || !isEqualOrNull( origColumn.getOperator(),
                                       editColumn.getOperator() )
                    || origColumn.getConstraintValueType() != editColumn.getConstraintValueType() ) {

            // Update column's Cell type. Other than the obvious change in data-type if the 
            // Operator changes to or from "not set" (possible for literal columns and formulae)
            // the column needs to be changed to or from Text.
            bRedrawColumn = true;
            bUpdateCellsForDataType = true;
        }

        // Update column's cell content if the Optional Value list has changed
        if ( !isEqualOrNull( origColumn.getValueList(),
                             editColumn.getValueList() ) ) {
            //TODO {manstis} Needs rewrite to events
            //bRedrawColumn = bRedrawColumn || updateCellsForOptionValueList( editColumn,column );
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Update column field in Header Widget
        if ( origColumn.getFactField() != null && !origColumn.getFactField().equals( editColumn.getFactField() ) ) {
            bRedrawHeader = true;
        }

        // Update column binding in Header Widget
        if ( !origColumn.isBound() && editColumn.isBound() ) {
            bRedrawHeader = true;
        } else if ( origColumn.isBound() && !editColumn.isBound() ) {
            bRedrawHeader = true;
        } else if ( origColumn.isBound() && editColumn.isBound() && !origColumn.getBinding().equals( editColumn.getBinding() ) ) {
            bRedrawColumn = true;
        }

        // Update LimitedEntryValue in Header Widget
        if ( origColumn instanceof LimitedEntryCol && editColumn instanceof LimitedEntryCol ) {
            LimitedEntryCol lecOrig = (LimitedEntryCol) origColumn;
            LimitedEntryCol lecEditing = (LimitedEntryCol) editColumn;
            if ( isEqualOrNull( lecOrig.getValue(),
                                lecEditing.getValue() ) ) {
                bRedrawHeader = true;
            }
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update model with new cells, if required
        if ( bUpdateCellsForDataType ) {
            //TODO {manstis} Needs rewrite to events
            //updateCellsForDataType( origColumn,column );
        }

        //Redraw columns
        if ( bRedrawColumn ) {
            int maxColumnIndex = model.getAllColumns().size() - 1;
            //TODO {manstis} Needs rewrite to events
            //widget.redrawColumns( column.getColumnIndex(),maxColumnIndex );
        }

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.redrawHeader();
                }
            } );
        }

    }

    /**
     * Update values controlled by the decision table itself
     */
    public void updateSystemControlledColumnValues() {

        for ( DTColumnConfig52 column : model.getAllColumns() ) {
            if ( column instanceof RowNumberCol52 ) {
                updateRowNumberColumnValues( column );

            } else if ( column instanceof AttributeCol52 ) {

                // Update Salience values
                AttributeCol52 attrCol = (AttributeCol52) column;
                if ( attrCol.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                    updateSalienceColumnValues( attrCol );
                }
            }
        }
    }

    // Add column to table with optional redraw
    private void addColumn(DTColumnConfig52 modelColumn,
                           boolean bRedraw) {
        int index = 0;
        if ( modelColumn instanceof MetadataCol52 ) {
            index = findMetadataColumnIndex();
        } else if ( modelColumn instanceof AttributeCol52 ) {
            index = findAttributeColumnIndex();
        } else if ( modelColumn instanceof ConditionCol52 ) {
            index = findConditionColumnIndex( (ConditionCol52) modelColumn );
        } else if ( modelColumn instanceof ActionCol52 ) {
            index = findActionColumnIndex();
        }

        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( modelColumn,
                                                                                 index,
                                                                                 bRedraw );
        eventBus.fireEvent( dce );
    }

    /**
     * Check whether the given column can accept "otherwise" values
     * 
     * @param column
     * @return true if the Column can accept "otherwise" values
     */
    private boolean canAcceptOtherwiseValues(DTColumnConfig52 column) {

        //Check the column type is correct
        if ( !(column instanceof ConditionCol52) ) {
            return false;
        }
        ConditionCol52 cc = (ConditionCol52) column;

        //Check column contains literal values and uses the equals operator
        if ( cc.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_LITERAL ) {
            return false;
        }

        //Check operator is supported
        if ( cc.getOperator() == null ) {
            return false;
        }
        if ( cc.getOperator().equals( "==" ) ) {
            return true;
        }
        if ( cc.getOperator().equals( "!=" ) ) {
            return true;
        }
        return false;
    }

    // Find the right-most index for an Action column
    private int findActionColumnIndex() {
        int analysisColumnsSize = 1;
        int index = model.getAllColumns().size() - analysisColumnsSize;
        return index;
    }

    // Find the right-most index for a Attribute column
    private int findAttributeColumnIndex() {
        int index = 0;
        List<DTColumnConfig52> columns = model.getAllColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DTColumnConfig52 column = columns.get( iCol );
            if ( column instanceof RowNumberCol52 ) {
                index = iCol;
            } else if ( column instanceof DescriptionCol52 ) {
                index = iCol;
            } else if ( column instanceof MetadataCol52 ) {
                index = iCol;
            } else if ( column instanceof AttributeCol52 ) {
                index = iCol;
            }
        }
        return index;
    }

    // Find the right-most index for a Condition column
    private int findConditionColumnIndex(ConditionCol52 col) {
        int index = 0;
        boolean bMatched = false;
        List<DTColumnConfig52> columns = model.getAllColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DTColumnConfig52 column = columns.get( iCol );
            if ( column instanceof RowNumberCol52 ) {
                index = iCol;
            } else if ( column instanceof DescriptionCol52 ) {
                index = iCol;
            } else if ( column instanceof MetadataCol52 ) {
                index = iCol;
            } else if ( column instanceof AttributeCol52 ) {
                index = iCol;
            } else if ( column instanceof ConditionCol52 ) {
                if ( isEquivalentConditionColumn( (ConditionCol52) column,
                                                  col ) ) {
                    index = iCol;
                    bMatched = true;
                } else if ( !bMatched ) {
                    index = iCol;
                }
            }
        }
        return index;
    }

    // Find the right-most index for a Metadata column
    private int findMetadataColumnIndex() {
        int index = 0;
        List<DTColumnConfig52> columns = model.getAllColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DTColumnConfig52 column = columns.get( iCol );
            if ( column instanceof RowNumberCol52 ) {
                index = iCol;
            } else if ( column instanceof DescriptionCol52 ) {
                index = iCol;
            } else if ( column instanceof MetadataCol52 ) {
                index = iCol;
            }
        }
        return index;
    }

    // Retrieve the data for a particular column
    private List<CellValue< ? extends Comparable< ? >>> getColumnData(DTColumnConfig52 column) {
        int iColIndex = model.getAllColumns().indexOf( column );
        List<CellValue< ? extends Comparable< ? >>> columnData = new ArrayList<CellValue< ? extends Comparable< ? >>>();
        for ( List<DTCellValue52> row : model.getData() ) {
            DTCellValue52 dcv = row.get( iColIndex );
            columnData.add( cellValueFactory.convertModelCellValue( column,
                                                                    dcv ) );
        }
        return columnData;
    }

    // Check whether two Objects are equal or both null
    private boolean isEqualOrNull(Object s1,
                                  Object s2) {
        if ( s1 == null
             && s2 == null ) {
            return true;
        } else if ( s1 != null
                    && s2 != null
                    && s1.equals( s2 ) ) {
            return true;
        }
        return false;
    }

    // Check whether two ConditionCols are equivalent
    private boolean isEquivalentConditionColumn(ConditionCol52 c1,
                                                ConditionCol52 c2) {

        Pattern52 c1Pattern = model.getPattern( c1 );
        Pattern52 c2Pattern = model.getPattern( c2 );

        if ( isEqualOrNull( c1Pattern.getFactType(),
                            c2Pattern.getFactType() )
                && isEqualOrNull( c1Pattern.getBoundName(),
                                  c2Pattern.getBoundName() ) ) {
            return true;
        }
        return false;
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn(final ActionInsertFactCol52 col,
                                     final ActionInsertFactCol52 editingCol) {
        col.setBoundName( editingCol.getBoundName() );
        col.setType( editingCol.getType() );
        col.setFactField( editingCol.getFactField() );
        col.setHeader( editingCol.getHeader() );
        col.setValueList( editingCol.getValueList() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setFactType( editingCol.getFactType() );
        col.setInsertLogical( editingCol.isInsertLogical() );
        if ( col instanceof LimitedEntryCol && editingCol instanceof LimitedEntryCol ) {
            ((LimitedEntryCol) col).setValue( ((LimitedEntryCol) editingCol).getValue() );
        }
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn(final ActionSetFieldCol52 col,
                                     final ActionSetFieldCol52 editingCol) {
        col.setBoundName( editingCol.getBoundName() );
        col.setType( editingCol.getType() );
        col.setFactField( editingCol.getFactField() );
        col.setHeader( editingCol.getHeader() );
        col.setValueList( editingCol.getValueList() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setUpdate( editingCol.isUpdate() );
        if ( col instanceof LimitedEntryCol && editingCol instanceof LimitedEntryCol ) {
            ((LimitedEntryCol) col).setValue( ((LimitedEntryCol) editingCol).getValue() );
        }
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn(final ActionRetractFactCol52 col,
                                     final ActionRetractFactCol52 editingCol) {
        col.setHeader( editingCol.getHeader() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        if ( col instanceof LimitedEntryCol && editingCol instanceof LimitedEntryCol ) {
            ((LimitedEntryCol) col).setValue( ((LimitedEntryCol) editingCol).getValue() );
        }
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn(final ActionWorkItemCol52 col,
                                     final ActionWorkItemCol52 editingCol) {
        col.setHeader( editingCol.getHeader() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setWorkItemDefinition( editingCol.getWorkItemDefinition() );
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn(final ActionWorkItemSetFieldCol52 col,
                                     final ActionWorkItemSetFieldCol52 editingCol) {
        col.setBoundName( editingCol.getBoundName() );
        col.setType( editingCol.getType() );
        col.setFactField( editingCol.getFactField() );
        col.setHeader( editingCol.getHeader() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setUpdate( editingCol.isUpdate() );
        col.setWorkItemName( editingCol.getWorkItemName() );
        col.setWorkItemResultParameterName( editingCol.getWorkItemResultParameterName() );
        col.setParameterClassName( editingCol.getParameterClassName() );
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn(final ConditionCol52 col,
                                     final ConditionCol52 editingCol) {
        col.setConstraintValueType( editingCol.getConstraintValueType() );
        col.setFactField( editingCol.getFactField() );
        col.setFieldType( editingCol.getFieldType() );
        col.setHeader( editingCol.getHeader() );
        col.setOperator( editingCol.getOperator() );
        col.setValueList( editingCol.getValueList() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setParameters( editingCol.getParameters() );
        col.setBinding( editingCol.getBinding() );
        if ( col instanceof LimitedEntryCol && editingCol instanceof LimitedEntryCol ) {
            ((LimitedEntryCol) col).setValue( ((LimitedEntryCol) editingCol).getValue() );
        }
    }

    //Remove Otherwise state from column cells
    private void removeOtherwiseStates(final DynamicColumn<DTColumnConfig52> column) {

        //Grouping needs to be removed
        widget.setMerging( false );

        DynamicData data = widget.getData();
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            CellValue< ? > cv = row.get( column.getColumnIndex() );
            cv.removeState( CellState.OTHERWISE );
        }

    }

    // Ensure the Column cell type and corresponding values are correct
    private void updateCellsForDataType(final DTColumnConfig52 editColumn,
                                        final DynamicColumn<DTColumnConfig52> column) {

        //Grouping needs to be removed
        widget.setMerging( false );

        DynamicData data = widget.getData();
        column.setCell( cellFactory.getCell( editColumn ) );
        int iCol = model.getAllColumns().indexOf( editColumn );
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DTCellValue52 dcv = model.getData().get( iRow ).get( iCol );
            DynamicDataRow row = data.get( iRow );
            row.set( column.getColumnIndex(),
                     cellValueFactory.convertModelCellValue( editColumn,
                                                             dcv ) );
        }

    }

    // Ensure the values in a column are within the Value List
    private boolean updateCellsForOptionValueList(final DTColumnConfig52 editColumn,
                                                  final DynamicColumn<DTColumnConfig52> column) {
        boolean bRedrawRequired = false;
        DynamicData data = widget.getData();
        List<String> vals = Arrays.asList( model.getValueList( editColumn,
                                                               sce ) );
        column.setCell( cellFactory.getCell( editColumn ) );
        int iCol = column.getColumnIndex();
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            if ( !vals.contains( row.get( iCol ).getValue() ) ) {
                row.get( iCol ).setValue( null );
                bRedrawRequired = true;
            }
        }
        return bRedrawRequired;
    }

    // Update Row Number column values
    private void updateRowNumberColumnValues(DTColumnConfig52 column) {
        int rowNumber = 1;
        int iColIndex = model.getAllColumns().indexOf( column );
        for ( List<DTCellValue52> row : model.getData() ) {
            row.get( iColIndex ).setNumericValue( new BigDecimal( rowNumber ) );
            rowNumber++;
        }

        //Raise event to the grid widget
        UpdateColumnEvent uce = new UpdateColumnEvent( iColIndex,
                                                       getColumnData( column ) );
        eventBus.fireEvent( uce );
    }

    // Update Salience column values
    private void updateSalienceColumnValues(AttributeCol52 column) {

        if ( !column.isUseRowNumber() ) {
            return;
        }

        int iColIndex = model.getAllColumns().indexOf( column );
        if ( !column.isReverseOrder() ) {
            updateRowNumberColumnValues( column );
        } else {

            int salience = (column.isReverseOrder() ? model.getData().size() : 1);
            for ( List<DTCellValue52> row : model.getData() ) {
                row.get( iColIndex ).setNumericValue( new BigDecimal( salience ) );
                if ( column.isReverseOrder() ) {
                    salience--;
                } else {
                    salience++;
                }
            }

            //Raise event to the grid widget
            UpdateColumnEvent uce = new UpdateColumnEvent( iColIndex,
                                                           getColumnData( column ) );
            eventBus.fireEvent( uce );
        }
    }

    /**
     * Check whether the given Cell can accept "otherwise" values
     * 
     * @param cell
     * @return true if the Cell can accept "otherwise" values
     */
    protected boolean canAcceptOtherwiseValues(CellValue< ? > cell) {
        Coordinate c = cell.getCoordinate();
        DTColumnConfig52 column = model.getAllColumns().get( c.getCol() );
        return canAcceptOtherwiseValues( column );
    }

    public void analyze() {
        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( sce );
        List<Analysis> analysisData = analyzer.analyze( model );
        showAnalysis( analysisData );
    }

    @SuppressWarnings("unchecked")
    private void showAnalysis(List<Analysis> analysisData) {
        AnalysisCol52 analysisCol = model.getAnalysisCol();
        int analysisColumnIndex = model.getAllColumns().indexOf( analysisCol );
        DynamicData dynamicData = widget.getData();
        for ( int i = 0; i < analysisData.size(); i++ ) {
            CellValue<Analysis> cellValue = (CellValue<Analysis>) dynamicData.get( i ).get( analysisColumnIndex );
            Analysis analysis = analysisData.get( i );
            cellValue.setValue( analysis );
        }
        analysisCol.setHideColumn( false );
        setColumnVisibility( analysisCol,
                             !analysisCol.isHideColumn() );
        widget.redrawColumn( analysisColumnIndex );
    }

    /**
     * Move a Pattern to the given index in the model
     * 
     * @param pattern
     *            The Pattern to which the Condition relates
     * @param patternTargetIndex
     *            The index to which the pattern will be moved
     */
    public void movePattern(Pattern52 pattern,
                            int patternTargetIndex) {

        //Sanity check
        if ( patternTargetIndex < 0 || patternTargetIndex > model.getConditionPatterns().size() - 1 ) {
            throw new IndexOutOfBoundsException();
        }

        //If target index is the Patterns current position exit
        int patternSourceIndex = model.getConditionPatterns().indexOf( pattern );
        if ( patternSourceIndex == patternTargetIndex ) {
            return;
        }

        //Update model
        if ( patternTargetIndex > patternSourceIndex ) {
            //Move down (after)
            Pattern52 patternBeingMovedAfter = model.getConditionPatterns().get( patternTargetIndex );
            model.getConditionPatterns().remove( pattern );
            if ( patternTargetIndex > model.getConditionPatterns().size() - 1 ) {
                model.getConditionPatterns().add( pattern );
            } else {
                model.getConditionPatterns().add( patternTargetIndex,
                                                  pattern );
            }
            //Update UI
            movePatternAfter( pattern,
                              patternBeingMovedAfter );
        } else {
            //Move up (before)
            Pattern52 patternBeingMovedBefore = model.getConditionPatterns().get( patternTargetIndex );
            model.getConditionPatterns().remove( pattern );
            model.getConditionPatterns().add( patternTargetIndex,
                                              pattern );
            //Update UI
            movePatternBefore( pattern,
                               patternBeingMovedBefore );
        }

    }

    // Move a Pattern, or more accurately the group of columns relating to a
    // pattern, before another Pattern in the order in which they are added to a
    // rule's DRL
    private void movePatternBefore(Pattern52 pattern,
                                   Pattern52 beforePattern) {

        //Find the index of the first column of the pattern before which the one being 
        //moved will be inserted. Columns of the Pattern being moved will be inserted 
        //*before* this column.
        DTColumnConfig52 beforeColumn = beforePattern.getConditions().get( 0 );
        int beforeColumnIndex = model.getAllColumns().indexOf( beforeColumn );

        //Move columns
        for ( ConditionCol52 cc : pattern.getConditions() ) {
            int columnIndex = model.getAllColumns().indexOf( cc );
            List<CellValue< ? extends Comparable< ? >>> columnData = getColumnData( cc );
            deleteColumn( columnIndex,
                          false );
            //TODO {manstis} Need to move a column with existing data
            //insertColumnBefore( cc,columnData,beforeColumnIndex++,false );
        }

        //Partial redraw
        int startRedrawIndex = model.getAllColumns().indexOf( pattern.getConditions().get( 0 ) );
        int endRedrawIndex = model.getAllColumns().indexOf( beforePattern.getConditions().get( beforePattern.getConditions().size() - 1 ) );
        widget.redrawColumns( startRedrawIndex,
                              endRedrawIndex );
        widget.redrawHeader();
    }

    // Move a Pattern, or more accurately the group of columns relating to a
    // pattern, after another Pattern in the order in which they are added to a
    // rule's DRL
    private void movePatternAfter(Pattern52 pattern,
                                  Pattern52 afterPattern) {

        //Find the index of the last column of the pattern after which the one being 
        //moved will be inserted. Since columns of the Pattern being moved are first
        //deleted the afterColumnIndex effectively points to the first column after
        //the last column of the existing afterPattern.
        DTColumnConfig52 afterColumn = afterPattern.getConditions().get( afterPattern.getConditions().size() - 1 );
        int afterColumnIndex = model.getAllColumns().indexOf( afterColumn );

        //Move columns
        for ( ConditionCol52 cc : pattern.getConditions() ) {
            int columnIndex = model.getAllColumns().indexOf( cc );
            List<CellValue< ? extends Comparable< ? >>> columnData = getColumnData( cc );
            deleteColumn( columnIndex,
                          false );
            //TODO {manstis} Need to move a column with existing data
            //insertColumnBefore( cc,columnData,afterColumnIndex,false );
        }

        //Partial redraw
        int startRedrawIndex = model.getAllColumns().indexOf( afterPattern.getConditions().get( 0 ) );
        int endRedrawIndex = model.getAllColumns().indexOf( pattern.getConditions().get( pattern.getConditions().size() - 1 ) );
        widget.redrawColumns( startRedrawIndex,
                              endRedrawIndex );
        widget.redrawHeader();
    }

    /**
     * Move a Condition to the given index on a Pattern in the model
     * 
     * @param pattern
     *            The Pattern to which the Condition relates
     * @param condition
     *            The Condition being moved
     * @param conditionIndex
     *            The index in the pattern to which the column will be moved
     */
    public void moveCondition(Pattern52 pattern,
                              ConditionCol52 condition,
                              int conditionTargetIndex) {

        //Sanity check
        if ( conditionTargetIndex < 0 || conditionTargetIndex > pattern.getConditions().size() - 1 ) {
            throw new IndexOutOfBoundsException();
        }

        //If target index is the Conditions current position exit
        int conditionSourceIndex = pattern.getConditions().indexOf( condition );
        if ( conditionSourceIndex == conditionTargetIndex ) {
            return;
        }

        ConditionCol52 conditionTarget = pattern.getConditions().get( conditionTargetIndex );
        int conditionTargetColumnIndex = model.getAllColumns().indexOf( conditionTarget );
        int conditionSourceColumnIndex = model.getAllColumns().indexOf( condition );

        //Update model
        pattern.getConditions().remove( condition );
        if ( conditionTargetIndex > conditionSourceIndex ) {
            if ( conditionTargetIndex > pattern.getConditions().size() - 1 ) {
                pattern.getConditions().add( condition );
            } else {
                pattern.getConditions().add( conditionTargetIndex,
                                             condition );
            }

        } else {
            pattern.getConditions().add( conditionTargetIndex,
                                         condition );
        }

        //Update UI
        List<CellValue< ? extends Comparable< ? >>> columnData = getColumnData( condition );
        int columnIndex = model.getAllColumns().indexOf( condition );
        deleteColumn( columnIndex );
        //TODO {manstis} Need to move a column with existing data
        //insertColumnBefore( condition,columnData,conditionTargetColumnIndex,true );
    }

    /**
     * Move an action to the given index in the model
     * 
     * @param action
     *            The Action being moved
     * @param actionIndex
     *            The index in the model to which the column will be moved
     */
    public void moveAction(ActionCol52 action,
                           int actionTargetIndex) {

        //Sanity check
        if ( actionTargetIndex < 0 || actionTargetIndex > model.getActionCols().size() - 1 ) {
            throw new IndexOutOfBoundsException();
        }

        //If target index is the Actions current position exit
        int actionSourceIndex = model.getActionCols().indexOf( action );
        if ( actionSourceIndex == actionTargetIndex ) {
            return;
        }

        ActionCol52 actionTarget = model.getActionCols().get( actionTargetIndex );
        int actionTargetColumnIndex = model.getAllColumns().indexOf( actionTarget );
        int actionSourceColumnIndex = model.getAllColumns().indexOf( action );

        //Update model
        model.getActionCols().remove( action );
        if ( actionTargetIndex > actionSourceIndex ) {
            if ( actionTargetIndex > model.getActionCols().size() - 1 ) {
                model.getActionCols().add( action );
            } else {
                model.getActionCols().add( actionTargetIndex,
                                           action );
            }

        } else {
            model.getActionCols().add( actionTargetIndex,
                                       action );
        }

        //Update UI
        List<CellValue< ? extends Comparable< ? >>> columnData = getColumnData( action );
        int columnIndex = model.getAllColumns().indexOf( action );
        deleteColumn( columnIndex );
        //TODO {manstis} Need to move a column with existing data
        //insertColumnBefore( action,columnData,actionTargetColumnIndex,true );
    }

    public void onDeleteRow(DeleteRowEvent event) {
        model.getData().remove( event.getIndex() );
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );
    }

    public void onInsertRow(InsertRowEvent event) {
        List<DTCellValue52> data = cellValueFactory.makeRowData();
        model.getData().add( event.getIndex(),
                             data );
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );
    }

    public void onAppendRow(AppendRowEvent event) {
        List<DTCellValue52> data = cellValueFactory.makeRowData();
        model.getData().add( data );
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );
    }

    public void onDeleteColumn(DeleteColumnEvent event) {
        int index = event.getIndex();
        for ( List<DTCellValue52> row : model.getData() ) {
            row.remove( index );
        }
    }

    public void onInsertColumn(InsertColumnEvent<DTColumnConfig52> event) {
        int index = event.getIndex();
        DTColumnConfig52 column = event.getColumn();
        List<DTCellValue52> data = cellValueFactory.makeColumnData( column );
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DTCellValue52 dcv = data.get( iRow );
            List<DTCellValue52> row = model.getData().get( iRow );
            row.add( index,
                     dcv );
        }
    }

}
