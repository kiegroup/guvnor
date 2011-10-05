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

import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.util.GWTDateConverter;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue.CellState;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.decoratedgrid.HasColumns;
import org.drools.guvnor.client.widgets.decoratedgrid.HasRows;
import org.drools.guvnor.client.widgets.decoratedgrid.HasSystemControlledColumns;
import org.drools.guvnor.client.widgets.decoratedgrid.MergableGridWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.data.Coordinate;
import org.drools.guvnor.client.widgets.decoratedgrid.data.DynamicData;
import org.drools.guvnor.client.widgets.decoratedgrid.data.DynamicDataRow;
import org.drools.guvnor.client.widgets.decoratedgrid.data.GroupedDynamicDataRow;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;

/**
 * An abstract Decision Table and the necessary boiler-plate to convert from
 * DTColumnConfig objects to the DynamicData related classes used by the
 * DecoratedGridWidget
 */
public abstract class AbstractDecisionTableWidget extends Composite
    implements
    HasRows,
    HasColumns<DTColumnConfig52>,
    HasSystemControlledColumns {

    // Decision Table data
    protected GuidedDecisionTable52                 model;
    protected DecoratedGridWidget<DTColumnConfig52> widget;
    protected SuggestionCompletionEngine            sce;
    protected DecisionTableCellFactory              cellFactory;
    protected DecisionTableCellValueFactory         cellValueFactory;
    protected DecisionTableControlsWidget           dtableCtrls;

    /**
     * Constructor
     * 
     * @param sce
     */
    public AbstractDecisionTableWidget(DecisionTableControlsWidget dtableCtrls,
                                       SuggestionCompletionEngine sce) {

        if ( dtableCtrls == null ) {
            throw new IllegalArgumentException( "dtableControls cannot be null" );
        }
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.sce = sce;
        this.dtableCtrls = dtableCtrls;
        this.dtableCtrls.setDecisionTableWidget( this );
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
     * Append an empty row to the end of the table
     */
    public void appendRow() {
        insertRowBefore( null );
    }

    /**
     * Delete the given column
     * 
     * @param modelColumn
     *            The Decision Table column to delete
     */
    public void deleteColumn(DTColumnConfig52 modelColumn) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException(
                                                "modelColumn cannot be null." );
        }
        DynamicColumn<DTColumnConfig52> col = getDynamicColumn( modelColumn );
        widget.deleteColumn( col );
    }

    /**
     * Delete the given row
     * 
     * @param The
     *            Decision Table row to delete
     */
    public void deleteRow(DynamicDataRow row) {
        if ( row == null ) {
            throw new IllegalArgumentException( "row cannot be null" );
        }
        widget.deleteRow( row );
        updateSystemControlledColumnValues();
        redrawSystemControlledColumns();
    }

    /**
     * Return the model
     * 
     * @return The DecisionTable data model
     */
    public GuidedDecisionTable52 getModel() {
        return this.model;
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
     * Insert an empty row before the given row
     * 
     * @param rowBefore
     *            The row before which the new (empty) row will be inserted. If
     *            this value is null the row will be appended to the end of the
     *            table
     */
    public void insertRowBefore(DynamicDataRow rowBefore) {
        List<CellValue< ? extends Comparable< ? >>> rowData = makeRowData();
        widget.insertRowBefore( rowBefore,
                                rowData );
        redrawSystemControlledColumns();
    }

    /**
     * Mark a cell as containing the magical "otherwise" value. The magical
     * "otherwise" value has the meaning of all values other than those
     * explicitly defined for this column.
     */
    public void makeOtherwiseCell() {

        MergableGridWidget<DTColumnConfig52> grid = widget.getGridWidget();
        List<CellValue< ? >> selections = grid.getSelectedCells();
        CellValue< ? > cell = selections.get( 0 );

        if ( canAcceptOtherwiseValues( cell ) ) {

            //Set "otherwise" property on cell
            for ( CellValue< ? > cv : selections ) {
                cv.addState( CellState.OTHERWISE );
            }
            grid.update( null );
        }
    }

    /**
     * Force the system controlled columns to be redrawn
     */
    public void redrawSystemControlledColumns() {
        widget.redrawSystemControlledColumns();
    }

    /**
     * Update the Decision Table model with the columns contained in the grid.
     * The Decision Table controls indexing of new columns to preserve grouping
     * of column types. If the order of columns is important to client-code this
     * can be called to ensure columns within the model are synchronised with
     * the Decision Table.
     */
    public void scrapeColumns() {

        RowNumberCol52 rnCol = null;
        DescriptionCol52 descCol = null;
        List<MetadataCol52> newMetaColumns = new ArrayList<MetadataCol52>();
        List<AttributeCol52> newAttributeColumns = new ArrayList<AttributeCol52>();
        List<Pattern52> newPatterns = new ArrayList<Pattern52>();
        List<ActionCol52> newActionColumns = new ArrayList<ActionCol52>();

        // Extract column information
        for ( DynamicColumn<DTColumnConfig52> column : widget.getGridWidget().getColumns() ) {
            DTColumnConfig52 modelCol = column.getModelColumn();
            if ( modelCol instanceof RowNumberCol52 ) {
                rnCol = (RowNumberCol52) modelCol;
                rnCol.setWidth( column.getWidth() );
                rnCol.setHideColumn( !column.isVisible() );
                model.setRowNumberCol( rnCol );

            } else if ( modelCol instanceof DescriptionCol52 ) {
                descCol = (DescriptionCol52) modelCol;
                descCol.setWidth( column.getWidth() );
                descCol.setHideColumn( !column.isVisible() );
                model.setDescriptionCol( descCol );

            } else if ( modelCol instanceof MetadataCol52 ) {
                MetadataCol52 tc = (MetadataCol52) modelCol;
                tc.setWidth( column.getWidth() );
                tc.setHideColumn( !column.isVisible() );
                newMetaColumns.add( tc );

            } else if ( modelCol instanceof AttributeCol52 ) {
                AttributeCol52 tc = (AttributeCol52) modelCol;
                tc.setWidth( column.getWidth() );
                tc.setHideColumn( !column.isVisible() );
                newAttributeColumns.add( tc );

            } else if ( modelCol instanceof ConditionCol52 ) {
                ConditionCol52 tc = (ConditionCol52) modelCol;
                tc.setWidth( column.getWidth() );
                tc.setHideColumn( !column.isVisible() );

                Pattern52 p = model.getPattern( tc );
                Pattern52 newPattern = getExistingNewPattern( newPatterns,
                                                              p );
                if ( newPattern == null ) {
                    newPattern = clonePatternExcludingConditions( p );
                    newPatterns.add( newPattern );
                }
                newPattern.getConditions().add( tc );

            } else if ( modelCol instanceof ActionCol52 ) {
                ActionCol52 tc = (ActionCol52) modelCol;
                tc.setWidth( column.getWidth() );
                tc.setHideColumn( !column.isVisible() );
                newActionColumns.add( tc );
            }

        }
        model.setMetadataCols( newMetaColumns );
        model.setAttributeCols( newAttributeColumns );
        model.setConditionPatterns( newPatterns );
        model.setActionCols( newActionColumns );

    }

    private Pattern52 clonePatternExcludingConditions(Pattern52 p) {
        Pattern52 newPattern = new Pattern52();
        newPattern.setBoundName( p.getBoundName() );
        newPattern.setEntryPointName( p.getEntryPointName() );
        newPattern.setFactType( p.getFactType() );
        newPattern.setNegated( p.isNegated() );
        newPattern.setWindow( p.getWindow() );
        return newPattern;
    }

    private Pattern52 getExistingNewPattern(List<Pattern52> newPatterns,
                                            Pattern52 pattern) {
        for ( Pattern52 p : newPatterns ) {
            if ( p.getBoundName().equals( pattern.getBoundName() ) ) {
                return p;
            }
        }
        return null;
    }

    /**
     * Update the Decision Table model with the data contained in the grid. The
     * Decision Table does not synchronise model data with UI data during user
     * interaction with the UI. Consequentially this should be called to refresh
     * the Model with the UI when needed.
     */
    public void scrapeData() {

        // Copy data
        final DynamicData data = widget.getGridWidget().getData().getFlattenedData();
        final List<DynamicColumn<DTColumnConfig52>> columns = widget.getGridWidget().getColumns();

        final int GRID_ROWS = data.size();
        List<List<DTCellValue52>> grid = new ArrayList<List<DTCellValue52>>();
        for ( int iRow = 0; iRow < GRID_ROWS; iRow++ ) {
            DynamicDataRow dataRow = data.get( iRow );
            List<DTCellValue52> row = new ArrayList<DTCellValue52>();
            for ( int iCol = 0; iCol < columns.size(); iCol++ ) {

                //Values put back into the Model are type-safe
                CellValue< ? > cv = dataRow.get( iCol );
                DTColumnConfig52 column = columns.get( iCol ).getModelColumn();
                DTCellValue52 dcv = cellValueFactory.convertToDTModelCell( column,
                                                                              cv );
                dcv.setOtherwise( cv.isOtherwise() );
                row.add( dcv );
            }
            grid.add( row );
        }
        this.model.setData( grid );
    }

    public void setColumnVisibility(DTColumnConfig52 modelColumn,
                                    boolean isVisible) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null" );
        }

        DynamicColumn<DTColumnConfig52> col = getDynamicColumn( modelColumn );
        widget.setColumnVisibility( col.getColumnIndex(),
                                            isVisible );
    }

    /**
     * Set the Decision Table's data. This removes all existing columns from the
     * Decision Table and re-creates them based upon the provided data.
     * 
     * @param data
     */
    public void setModel(GuidedDecisionTable52 model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }

        this.model = model;
        this.cellFactory = new DecisionTableCellFactory( sce,
                                                         widget.getGridWidget(),
                                                         this.model );
        this.cellValueFactory = new DecisionTableCellValueFactory( sce,
                                                                   this.model );

        //Date converter is injected so a GWT compatible one can be used here and another in testing
        DecisionTableCellValueFactory.injectDateConvertor( GWTDateConverter.getInstance() );

        //Setup command to recalculate System Controlled values when rows are added\deleted
        widget.getGridWidget().getData().setOnRowChangeCommand( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );

        widget.getGridWidget().getData().clear();
        widget.getGridWidget().getColumns().clear();

        // Dummy rows because the underlying DecoratedGridWidget expects there
        // to be enough rows to receive the columns data
        final DynamicData data = widget.getGridWidget().getData();
        for ( int iRow = 0; iRow < model.getData().size(); iRow++ ) {
            data.addRow();
        }

        // Static columns, Row#
        int colIndex = 0;
        DTColumnConfig52 colStatic;
        DynamicColumn<DTColumnConfig52> columnStatic;
        colStatic = model.getRowNumberCol();
        columnStatic = new DynamicColumn<DTColumnConfig52>( colStatic,
                                                            cellFactory.getCell( colStatic ),
                                                            colIndex,
                                                            true,
                                                            false );
        columnStatic.setWidth( 24 );
        widget.appendColumn( columnStatic,
                             makeColumnData( colStatic,
                                             colIndex++ ),
                             false );

        // Static columns, Description
        colStatic = model.getDescriptionCol();
        columnStatic = new DynamicColumn<DTColumnConfig52>( colStatic,
                                                            cellFactory.getCell( colStatic ),
                                                            colIndex );
        widget.appendColumn( columnStatic,
                             makeColumnData( colStatic,
                                             colIndex++ ),
                             false );

        // Initialise CellTable's Metadata columns
        for ( MetadataCol52 col : model.getMetadataCols() ) {
            DynamicColumn<DTColumnConfig52> column = new DynamicColumn<DTColumnConfig52>( col,
                                                                                          cellFactory.getCell( col ),
                                                                                          colIndex );
            column.setVisible( !col.isHideColumn() );
            widget.appendColumn( column,
                                 makeColumnData( col,
                                                 colIndex++ ),
                                 false );
        }

        // Initialise CellTable's Attribute columns
        for ( AttributeCol52 col : model.getAttributeCols() ) {
            DynamicColumn<DTColumnConfig52> column = new DynamicColumn<DTColumnConfig52>( col,
                                                                                          cellFactory.getCell( col ),
                                                                                          colIndex );
            column.setVisible( !col.isHideColumn() );
            column.setSystemControlled( col.isUseRowNumber() );
            column.setSortable( !col.isUseRowNumber() );
            widget.appendColumn( column,
                                 makeColumnData( col,
                                                 colIndex++ ),
                                 false );
        }

        // Initialise CellTable's Condition columns
        for ( Pattern52 p : model.getConditionPatterns() ) {
            for ( ConditionCol52 col : p.getConditions() ) {
                DynamicColumn<DTColumnConfig52> column = new DynamicColumn<DTColumnConfig52>( col,
                                                                                              cellFactory.getCell( col ),
                                                                                              colIndex );
                column.setVisible( !col.isHideColumn() );
                widget.appendColumn( column,
                                     makeColumnData( col,
                                                     colIndex++ ),
                                     false );

                //Ensure field data-type is set (field did not exist before 5.2)
                ConditionCol52 cc = (ConditionCol52) col;
                cc.setFieldType( sce.getFieldType( p.getFactType(),
                                                   cc.getFactField() ) );
            }
        }

        // Initialise CellTable's Action columns
        for ( ActionCol52 col : model.getActionCols() ) {
            DynamicColumn<DTColumnConfig52> column = new DynamicColumn<DTColumnConfig52>( col,
                                                                                          cellFactory.getCell( col ),
                                                                                          colIndex );
            column.setVisible( !col.isHideColumn() );
            widget.appendColumn( column,
                                 makeColumnData( col,
                                                 colIndex++ ),
                                 false );
        }

        // Ensure System Controlled values are correctly initialised
        updateSystemControlledColumnValues();

        // Schedule redraw of grid after sizes of child Elements have been set
        Scheduler.get().scheduleFinally( new ScheduledCommand() {

            public void execute() {
                // Draw header first as the size of child Elements depends upon it
                widget.getHeaderWidget().redraw();
                widget.getSidebarWidget().redraw();
                widget.getGridWidget().redraw();
            }

        } );
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
     * @param origCol
     *            The existing column in the grid
     * @param editColumn
     *            A copy (not clone) of the original column containing the
     *            modified values
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
        DynamicColumn<DTColumnConfig52> column = getDynamicColumn( origColumn );

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
                bUpdateCellsForDataType = true;
            }

        } else if ( !isEqualOrNull( origColumn.getFactType(),
                                    editColumn.getFactType() )
                    || !isEqualOrNull( origColumn.getFactField(),
                                       editColumn.getFactField() ) ) {
            bRedrawColumn = true;
            bUpdateCellsForDataType = true;
        }

        // Update column's cell content if the Optional Value list has changed
        if ( !isEqualOrNull( origColumn.getValueList(),
                             editColumn.getValueList() ) ) {
            bRedrawColumn = bRedrawColumn || updateCellsForOptionValueList( editColumn,
                                                                            column );
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update model with new cells, if required
        if ( bUpdateCellsForDataType ) {
            updateCellsForDataType( origColumn,
                                    column );
        }

        //Redraw columns
        if ( bRedrawColumn ) {
            int maxColumnIndex = widget.getGridWidget().getColumns().size() - 1;
            widget.getGridWidget().redrawColumns( column.getColumnIndex(),
                                                  maxColumnIndex );
        }

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.getHeaderWidget().redraw();
                }
            } );
        }

    }

    /**
     * Update an ActionSetFieldCol column
     * 
     * @param origCol
     *            The existing column in the grid
     * @param editColumn
     *            A copy (not clone) of the original column containing the
     *            modified values
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
        DynamicColumn<DTColumnConfig52> column = getDynamicColumn( origColumn );

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
                bUpdateCellsForDataType = true;
            }

        } else if ( !isEqualOrNull( origColumn.getFactField(),
                                       editColumn.getFactField() ) ) {
            bRedrawColumn = true;
            bUpdateCellsForDataType = true;
        }

        // Update column's cell content if the Optional Value list has changed
        if ( !isEqualOrNull( origColumn.getValueList(),
                             editColumn.getValueList() ) ) {
            bRedrawColumn = bRedrawColumn || updateCellsForOptionValueList( editColumn,
                                                                            column );
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update model with new cells, if required
        if ( bUpdateCellsForDataType ) {
            updateCellsForDataType( origColumn,
                                    column );
        }

        //Redraw columns
        if ( bRedrawColumn ) {
            int maxColumnIndex = widget.getGridWidget().getColumns().size() - 1;
            widget.getGridWidget().redrawColumns( column.getColumnIndex(),
                                                  maxColumnIndex );
        }

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.getHeaderWidget().redraw();
                }
            } );
        }

    }

    /**
     * Update a Condition column
     * 
     * @param origPattern
     *            The existing pattern to which the column related
     * @param origCol
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
        DynamicColumn<DTColumnConfig52> column = getDynamicColumn( origColumn );

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
                removeOtherwiseStates( column );
                bRedrawColumn = true;
            }
        }

        if ( !isEqualOrNull( origPattern.getBoundName(),
                             editPattern.getBoundName() ) ) {
            // Change in bound name requires column to be repositioned
            bRedrawHeader = true;
            addColumn( editColumn,
                       false );
            DynamicColumn<DTColumnConfig52> origCol = getDynamicColumn( origColumn );
            DynamicColumn<DTColumnConfig52> editCol = getDynamicColumn( editColumn );
            int origColIndex = widget.getGridWidget().getColumns().indexOf( origCol );
            int editColIndex = widget.getGridWidget().getColumns().indexOf( editCol );

            // If the FactType, FieldType and ConstraintValueType are unchanged
            // we can copy cell values from the old column into the new
            if ( isEqualOrNull( origPattern.getFactType(),
                                editPattern.getFactType() )
                 && isEqualOrNull( origColumn.getFactField(),
                                   editColumn.getFactField() )
                 && origColumn.getConstraintValueType() == editColumn.getConstraintValueType() ) {

                final DynamicData data = widget.getGridWidget().getData();
                for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                    DynamicDataRow row = data.get( iRow );
                    CellValue< ? > oldCell = row.get( origColIndex );
                    CellValue< ? > newCell = row.get( editColIndex );
                    newCell.setValue( oldCell.getValue() );
                }
            }

            // Delete old column and redraw
            widget.deleteColumn( origCol );
            origColIndex = Math.min( widget.getGridWidget().getColumns().size() - 1,
                                     origColIndex );
            editColIndex = Math.min( widget.getGridWidget().getColumns().size() - 1,
                                     editColIndex );
            if ( editColIndex > origColIndex ) {
                int temp = origColIndex;
                origColIndex = editColIndex;
                editColIndex = temp;
            }
            widget.getGridWidget().redrawColumns( editColIndex,
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
            bRedrawColumn = bRedrawColumn || updateCellsForOptionValueList( editColumn,
                                                                            column );
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

        //Update model with new cells, if required
        if ( bUpdateCellsForDataType ) {
            updateCellsForDataType( origColumn,
                                    column );
        }

        //Redraw columns
        if ( bRedrawColumn ) {
            int maxColumnIndex = widget.getGridWidget().getColumns().size() - 1;
            widget.getGridWidget().redrawColumns( column.getColumnIndex(),
                                                  maxColumnIndex );
        }

        // Schedule redraw event after column has been redrawn
        if ( bRedrawHeader ) {
            Scheduler.get().scheduleFinally( new ScheduledCommand() {
                public void execute() {
                    widget.getHeaderWidget().redraw();
                }
            } );
        }

    }

    public void updateSystemControlledColumnValues() {

        final DynamicData data = widget.getGridWidget().getData();
        final List<DynamicColumn<DTColumnConfig52>> columns = widget.getGridWidget().getColumns();

        for ( DynamicColumn<DTColumnConfig52> col : columns ) {

            DTColumnConfig52 modelColumn = col.getModelColumn();

            if ( modelColumn instanceof RowNumberCol52 ) {
                updateRowNumberColumnValues( data,
                                             col.getColumnIndex() );

            } else if ( modelColumn instanceof AttributeCol52 ) {

                // Update Salience values
                AttributeCol52 attrCol = (AttributeCol52) modelColumn;
                if ( attrCol.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                    if ( attrCol.isUseRowNumber() ) {
                        updateSalienceColumnValues( data,
                                                    col.getColumnIndex(),
                                                    attrCol.isReverseOrder() );
                    }

                    // Ensure Salience cells are rendered with the correct Cell
                    col.setCell( cellFactory.getCell( attrCol ) );
                    col.setSystemControlled( attrCol.isUseRowNumber() );
                    col.setSortable( !attrCol.isUseRowNumber() );
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
        insertColumnBefore( modelColumn,
                            index,
                            bRedraw );
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
        int index = widget.getGridWidget().getColumns().size() - 1;
        return index;
    }

    // Find the right-most index for a Attribute column
    private int findAttributeColumnIndex() {
        int index = 0;
        List<DynamicColumn<DTColumnConfig52>> columns = widget.getGridWidget().getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig52> column = columns.get( iCol );
            DTColumnConfig52 modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof AttributeCol52 ) {
                index = iCol;
            }
        }
        return index;
    }

    // Find the right-most index for a Condition column
    private int findConditionColumnIndex(ConditionCol52 col) {
        int index = 0;
        boolean bMatched = false;
        List<DynamicColumn<DTColumnConfig52>> columns = widget.getGridWidget().getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig52> column = columns.get( iCol );
            DTColumnConfig52 modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof AttributeCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof ConditionCol52 ) {
                if ( isEquivalentConditionColumn( (ConditionCol52) modelColumn,
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
        List<DynamicColumn<DTColumnConfig52>> columns = widget.getGridWidget().getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig52> column = columns.get( iCol );
            DTColumnConfig52 modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol52 ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol52 ) {
                index = iCol;
            }
        }
        return index;
    }

    // Retrieves the DynamicColumn relating to the Model column or null if it
    // cannot be found
    private DynamicColumn<DTColumnConfig52> getDynamicColumn(DTColumnConfig52 modelCol) {
        DynamicColumn<DTColumnConfig52> column = null;
        List<DynamicColumn<DTColumnConfig52>> columns = widget.getGridWidget().getColumns();
        for ( DynamicColumn<DTColumnConfig52> dc : columns ) {
            if ( dc.getModelColumn().equals( modelCol ) ) {
                column = dc;
                break;
            }
        }
        return column;
    }

    // Insert a new model column at the specified index
    private void insertColumnBefore(DTColumnConfig52 modelColumn,
                                    int index,
                                    boolean bRedraw) {

        // Create new column for grid
        DynamicColumn<DTColumnConfig52> column = new DynamicColumn<DTColumnConfig52>( modelColumn,
                                                                                      cellFactory.getCell( modelColumn ),
                                                                                      index );
        column.setVisible( !modelColumn.isHideColumn() );
        DynamicColumn<DTColumnConfig52> columnBefore = widget.getGridWidget().getColumns().get( index );

        // Create column data
        DynamicData data = widget.getGridWidget().getData();
        List<CellValue< ? extends Comparable< ? >>> columnData = new ArrayList<CellValue< ? extends Comparable< ? >>>();
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DTCellValue52 dcv = new DTCellValue52( modelColumn.getDefaultValue() );
            CellValue< ? > cell = cellValueFactory.makeCellValue( modelColumn,
                                                                  iRow,
                                                                  index,
                                                                  dcv );
            columnData.add( cell );
        }

        // Add column and data to grid
        widget.insertColumnBefore( columnBefore,
                                   column,
                                   columnData,
                                   bRedraw );

    }

    // Check whether two Strings are equal or both null
    private boolean isEqualOrNull(String s1,
                                  String s2) {
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

    // Make a row of data for insertion into a DecoratedGridWidget
    private List<CellValue< ? extends Comparable< ? >>> makeColumnData(DTColumnConfig52 column,
                                                                       int colIndex) {
        int dataSize = model.getData().size();
        List<CellValue< ? extends Comparable< ? >>> columnData = new ArrayList<CellValue< ? extends Comparable< ? >>>();

        for ( int iRow = 0; iRow < dataSize; iRow++ ) {
            List<DTCellValue52> row = model.getData().get( iRow );
            DTCellValue52 dcv = row.get( colIndex );
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.makeCellValue( column,
                                                                                       iRow,
                                                                                       colIndex,
                                                                                       dcv );
            columnData.add( cv );
        }
        return columnData;
    }

    // Construct a new row for insertion into a DecoratedGridWidget
    private List<CellValue< ? extends Comparable< ? >>> makeRowData() {
        List<CellValue< ? extends Comparable< ? >>> rowData = new ArrayList<CellValue< ? extends Comparable< ? >>>();
        List<DynamicColumn<DTColumnConfig52>> columns = widget.getGridWidget().getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DTColumnConfig52 col = columns.get( iCol ).getModelColumn();
            DTCellValue52 dcv = new DTCellValue52( col.getDefaultValue() );
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.makeCellValue( col,
                                                                                       0,
                                                                                       iCol,
                                                                                       dcv );
            rowData.add( cv );
        }
        return rowData;
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
    }

    //Remove Otherwise state from column cells
    private void removeOtherwiseStates(final DynamicColumn<DTColumnConfig52> column) {

        //Grouping needs to be removed
        if ( widget.getGridWidget().getData().isMerged() ) {
            widget.getGridWidget().toggleMerging();
        }

        DynamicData data = widget.getGridWidget().getData();
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
        if ( widget.getGridWidget().getData().isMerged() ) {
            widget.getGridWidget().toggleMerging();
        }

        DynamicData data = widget.getGridWidget().getData();
        column.setCell( cellFactory.getCell( editColumn ) );
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            row.set( column.getColumnIndex(),
                     cellValueFactory.makeCellValue( editColumn,
                                                     iRow,
                                                     column.getColumnIndex() ) );
        }

    }

    // Ensure the values in a column are within the Value List
    private boolean updateCellsForOptionValueList(final DTColumnConfig52 editColumn,
                                                  final DynamicColumn<DTColumnConfig52> column) {
        boolean bRedrawRequired = false;
        DynamicData data = widget.getGridWidget().getData();
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
    private void updateRowNumberColumnValues(DynamicData data,
                                             int iCol) {
        int iRowNum = 1;
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            if ( row instanceof GroupedDynamicDataRow ) {
                GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;

                //Setting value on a GroupedCellValue causes all children to assume the same value
                groupedRow.get( iCol ).setValue( new BigDecimal( iRowNum ) );
                for ( int iGroupedRow = 0; iGroupedRow < groupedRow.getChildRows().size(); iGroupedRow++ ) {
                    groupedRow.getChildRows().get( iGroupedRow ).get( iCol ).setValue( new BigDecimal( iRowNum ) );
                    iRowNum++;
                }
            } else {
                row.get( iCol ).setValue( new BigDecimal( iRowNum ) );
                iRowNum++;
            }
        }
    }

    // Update Salience column values
    private void updateSalienceColumnValues(DynamicData data,
                                            int iCol,
                                            boolean isReverseOrder) {

        if ( !isReverseOrder ) {
            updateRowNumberColumnValues( data,
                                         iCol );
        } else {

            //Get total row count
            int rowCount = 0;
            for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                DynamicDataRow row = data.get( iRow );
                if ( row instanceof GroupedDynamicDataRow ) {
                    GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;
                    rowCount = rowCount + groupedRow.getChildRows().size();
                } else {
                    rowCount++;
                }
            }

            int iRowNum = 0;
            for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                DynamicDataRow row = data.get( iRow );
                if ( row instanceof GroupedDynamicDataRow ) {
                    GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;

                    //Setting value on a GroupedCellValue causes all children to assume the same value
                    groupedRow.get( iCol ).setValue( new BigDecimal( rowCount - iRowNum ) );
                    for ( int iGroupedRow = 0; iGroupedRow < groupedRow.getChildRows().size(); iGroupedRow++ ) {
                        groupedRow.getChildRows().get( iGroupedRow ).get( iCol ).setValue( new BigDecimal( rowCount - iRowNum ) );
                        iRowNum++;
                    }
                } else {
                    row.get( iCol ).setValue( new BigDecimal( rowCount - iRowNum ) );
                    iRowNum++;
                }
            }

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
        MergableGridWidget<DTColumnConfig52> grid = widget.getGridWidget();
        DynamicColumn<DTColumnConfig52> column = grid.getColumns().get( c.getCol() );
        return canAcceptOtherwiseValues( column.getModelColumn() );
    }

}
