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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridHeaderWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicData;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicDataRow;
import org.drools.guvnor.client.widgets.decoratedgrid.HasColumns;
import org.drools.guvnor.client.widgets.decoratedgrid.HasRows;
import org.drools.guvnor.client.widgets.decoratedgrid.HasSystemControlledColumns;
import org.drools.guvnor.client.widgets.decoratedgrid.VerticalDecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.VerticalDecoratedGridWidget;
import org.drools.guvnor.client.widgets.tables.SortDirection;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.DescriptionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;

/**
 * A Vertical Decision Table composed of a VerticalDecoratedGridWidget and the
 * necessary boiler-plate to convert from DTColumnConfig objects to the
 * DynamicData related classes used by the DecoratedGridWidget
 */
public class VerticalDecisionTableWidget extends Composite
    implements
    HasRows,
    HasColumns<DTColumnConfig>,
    HasSystemControlledColumns {

    // Decision Table data
    protected GuidedDecisionTable                 model;
    protected DecoratedGridWidget<DTColumnConfig> widget;
    protected SuggestionCompletionEngine          sce;
    protected DecisionTableCellFactory            cellFactory;
    protected DecisionTableCellValueFactory       cellValueFactory;

    public VerticalDecisionTableWidget(SuggestionCompletionEngine sce) {

        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.sce = sce;

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedGridWidget<DTColumnConfig>();
        DecoratedGridHeaderWidget<DTColumnConfig> header = new VerticalDecisionTableHeaderWidget( widget );
        DecoratedGridSidebarWidget<DTColumnConfig> sidebar = new VerticalDecoratedGridSidebarWidget<DTColumnConfig>( widget,
                                                                                                                     this );
        widget.setHeaderWidget( header );
        widget.setSidebarWidget( sidebar );
        widget.setHasSystemControlledColumns( this );
        initWidget( widget );
    }

    public void addColumn(DTColumnConfig modelColumn) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException(
                                                "modelColumn cannot be null." );
        }
        addColumn( modelColumn,
                   true );
    }

    public void appendRow() {
        DynamicDataRow row = makeNewRow();
        widget.appendRow( row );
        updateSystemControlledColumnValues();
        redrawSystemControlledColumns();
    }

    public void deleteColumn(DTColumnConfig modelColumn) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException(
                                                "modelColumn cannot be null." );
        }

        DynamicColumn<DTColumnConfig> col = getDynamicColumn( modelColumn );
        widget.deleteColumn( col );
    }

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
    public GuidedDecisionTable getModel() {
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

    public void insertRowBefore(DynamicDataRow rowBefore) {
        if ( rowBefore == null ) {
            throw new IllegalArgumentException( "rowBefore cannot be null" );
        }

        DynamicDataRow newRow = makeNewRow();
        widget.insertRowBefore( rowBefore,
                                newRow );
        updateSystemControlledColumnValues();
        redrawSystemControlledColumns();
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

        // Clear existing definition
        model.getMetadataCols().clear();
        model.getAttributeCols().clear();
        model.getConditionCols().clear();
        model.getActionCols().clear();

        RowNumberCol rnCol = null;
        DescriptionCol descCol = null;

        // Extract column information
        for ( DynamicColumn<DTColumnConfig> column : widget.getColumns() ) {
            DTColumnConfig modelCol = column.getModelColumn();
            if ( modelCol instanceof RowNumberCol ) {
                rnCol = (RowNumberCol) modelCol;
                model.setRowNumberCol( rnCol );

            } else if ( modelCol instanceof DescriptionCol ) {
                descCol = (DescriptionCol) modelCol;
                model.setDescriptionCol( descCol );

            } else if ( modelCol instanceof MetadataCol ) {
                MetadataCol tc = (MetadataCol) modelCol;
                model.getMetadataCols().add( tc );

            } else if ( modelCol instanceof AttributeCol ) {
                AttributeCol tc = (AttributeCol) modelCol;
                model.getAttributeCols().add( tc );

            } else if ( modelCol instanceof ConditionCol ) {
                ConditionCol tc = (ConditionCol) modelCol;
                model.getConditionCols().add( tc );

            } else if ( modelCol instanceof ActionCol ) {
                ActionCol tc = (ActionCol) modelCol;
                model.getActionCols().add( tc );

            }
        }
    }

    /**
     * Update the Decision Table model with the data contained in the grid. The
     * Decision Table does not synchronise model data with UI data during user
     * interaction with the UI. Consequentially this should be called to refresh
     * the Model with the UI when needed.
     */
    public void scrapeData() {

        // Copy data
        DynamicData data = widget.getData();
        List<DynamicColumn<DTColumnConfig>> columns = widget.getColumns();
        final int GRID_ROWS = data.size();
        String[][] grid = new String[GRID_ROWS][];
        for ( int iRow = 0; iRow < GRID_ROWS; iRow++ ) {
            DynamicDataRow dataRow = data.get( iRow );
            String[] row = new String[dataRow.size()];
            for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
                CellValue< ? > cv = dataRow.get( iCol );
                DynamicColumn<DTColumnConfig> column = columns.get( iCol );
                String serialisedValue = cellValueFactory.serialiseValue( column.getModelColumn(),
                                                                          cv );
                row[iCol] = serialisedValue;
            }
            grid[iRow] = row;
        }
        this.model.setData( grid );
    }

    public void setColumnVisibility(DTColumnConfig modelColumn,
                                    boolean isVisible) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null" );
        }

        DynamicColumn<DTColumnConfig> col = getDynamicColumn( modelColumn );
        widget.setColumnVisibility( col.getColumnIndex(),
                                            isVisible );
    }

    /**
     * Set the Decision Table's data. This removes all existing columns from the
     * Decision Table and re-creates them based upon the provided data.
     * 
     * @param data
     */
    public void setModel(GuidedDecisionTable model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }

        this.model = model;
        this.cellFactory = new DecisionTableCellFactory( sce,
                                                         widget,
                                                         this.model );
        this.cellValueFactory = new DecisionTableCellValueFactory( this );

        widget.getData().clear();
        widget.getColumns().clear();

        // Dummy rows because the underlying DecoratedGridWidget expects there
        // to be enough rows to receive the columns data
        for ( int iRow = 0; iRow < model.getData().length; iRow++ ) {
            widget.getData().add( new DynamicDataRow() );
        }

        // Static columns, Row#
        int colIndex = 0;
        DTColumnConfig colStatic;
        DynamicColumn<DTColumnConfig> columnStatic;
        colStatic = model.getRowNumberCol();
        columnStatic = new DynamicColumn<DTColumnConfig>( colStatic,
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
        columnStatic = new DynamicColumn<DTColumnConfig>( colStatic,
                                                          cellFactory.getCell( colStatic ),
                                                          colIndex );
        widget.appendColumn( columnStatic,
                             makeColumnData( colStatic,
                                             colIndex++ ),
                             false );

        // Initialise CellTable's Metadata columns
        for ( DTColumnConfig col : model.getMetadataCols() ) {
            DynamicColumn<DTColumnConfig> column = new DynamicColumn<DTColumnConfig>( col,
                                                                                      cellFactory.getCell( col ),
                                                                                      colIndex );
            column.setVisible( !col.isHideColumn() );
            widget.appendColumn( column,
                                 makeColumnData( col,
                                                 colIndex++ ),
                                 false );
        }

        // Initialise CellTable's Attribute columns
        for ( DTColumnConfig col : model.getAttributeCols() ) {
            DynamicColumn<DTColumnConfig> column = new DynamicColumn<DTColumnConfig>( col,
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
        assertConditionColumnGrouping( model );
        for ( DTColumnConfig col : model.getConditionCols() ) {
            DynamicColumn<DTColumnConfig> column = new DynamicColumn<DTColumnConfig>( col,
                                                                                      cellFactory.getCell( col ),
                                                                                      colIndex );
            column.setVisible( !col.isHideColumn() );
            widget.appendColumn( column,
                                 makeColumnData( col,
                                                 colIndex++ ),
                                 false );
        }

        // Initialise CellTable's Action columns
        for ( DTColumnConfig col : model.getActionCols() ) {
            DynamicColumn<DTColumnConfig> column = new DynamicColumn<DTColumnConfig>( col,
                                                                                      cellFactory.getCell( col ),
                                                                                      colIndex );
            column.setVisible( !col.isHideColumn() );
            widget.appendColumn( column,
                                 makeColumnData( col,
                                                 colIndex++ ),
                                 false );
        }

        // Ensure cells are indexed correctly for start-up data
        widget.assertModelIndexes();

        // Draw header first as the size of child Elements depends upon it
        widget.getHeaderWidget().redraw();

        // Schedule redraw of grid after sizes of child Elements have been set
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            public void execute() {
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
    public void updateColumn(final ActionInsertFactCol origColumn,
                             final ActionInsertFactCol editColumn) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bRedrawColumn = false;
        boolean bRedrawHeader = false;
        DynamicColumn<DTColumnConfig> column = getDynamicColumn( origColumn );

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
                updateCellsForDataType( editColumn,
                                        column );
            }

        } else if ( !isEqualOrNull( origColumn.getFactType(),
                                    editColumn.getFactType() )
                    || !isEqualOrNull( origColumn.getFactField(),
                                       editColumn.getFactField() ) ) {
            bRedrawColumn = true;
            updateCellsForDataType( editColumn,
                                    column );
        }

        // Update column's cell content if the Optional Value list has changed
        if ( !isEqualOrNull( origColumn.getValueList(),
                             editColumn.getValueList() ) ) {
            bRedrawColumn = updateCellsForOptionValueList( editColumn,
                                                           column );
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        if ( bRedrawColumn ) {
            widget.getGridWidget().redrawColumn( column.getColumnIndex() );
        }
        if ( bRedrawHeader ) {
            // Schedule redraw event after column has been redrawn
            Scheduler.get().scheduleDeferred( new ScheduledCommand() {
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
    public void updateColumn(final ActionSetFieldCol origColumn,
                             final ActionSetFieldCol editColumn) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bRedrawColumn = false;
        boolean bRedrawHeader = false;
        DynamicColumn<DTColumnConfig> column = getDynamicColumn( origColumn );

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
                updateCellsForDataType( editColumn,
                                        column );
            }

        } else if ( !isEqualOrNull( origColumn.getFactField(),
                                       editColumn.getFactField() ) ) {
            bRedrawColumn = true;
            updateCellsForDataType( editColumn,
                                    column );
        }

        // Update column's cell content if the Optional Value list has changed
        if ( !isEqualOrNull( origColumn.getValueList(),
                             editColumn.getValueList() ) ) {
            bRedrawColumn = updateCellsForOptionValueList( editColumn,
                                                           column );
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        if ( bRedrawColumn ) {
            widget.getGridWidget().redrawColumn( column.getColumnIndex() );
        }
        if ( bRedrawHeader ) {
            // Schedule redraw event after column has been redrawn
            Scheduler.get().scheduleDeferred( new ScheduledCommand() {
                public void execute() {
                    widget.getHeaderWidget().redraw();
                }
            } );
        }

    }

    /**
     * Update a Condition column
     * 
     * @param origCol
     *            The existing column in the grid
     * @param editColumn
     *            A copy (not clone) of the original column containing the
     *            modified values
     */
    public void updateColumn(final ConditionCol origColumn,
                             final ConditionCol editColumn) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bRedrawColumn = false;
        boolean bRedrawHeader = false;
        DynamicColumn<DTColumnConfig> column = getDynamicColumn( origColumn );

        // Update column's visibility
        if ( origColumn.isHideColumn() != editColumn.isHideColumn() ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Change in operator
        if ( !isEqualOrNull( origColumn.getOperator(),
                             editColumn.getOperator() ) ) {
            bRedrawHeader = true;
        }

        if ( !isEqualOrNull( origColumn.getBoundName(),
                             editColumn.getBoundName() ) ) {
            // Change in bound name requires column to be repositioned
            bRedrawHeader = true;
            addColumn( editColumn,
                       false );
            DynamicColumn<DTColumnConfig> origCol = getDynamicColumn( origColumn );
            DynamicColumn<DTColumnConfig> editCol = getDynamicColumn( editColumn );
            int origColIndex = widget.getColumns().indexOf( origCol );
            int editColIndex = widget.getColumns().indexOf( editCol );

            // If the FactType, FieldType and ConstraintValueType are unchanged
            // we can copy cell values from the old column into the new
            if ( isEqualOrNull( origColumn.getFactType(),
                                editColumn.getFactType() )
                 && isEqualOrNull( origColumn.getFactField(),
                                   editColumn.getFactField() )
                 && origColumn.getConstraintValueType() == editColumn.getConstraintValueType() ) {

                for ( int iRow = 0; iRow < widget.getData().size(); iRow++ ) {
                    DynamicDataRow row = widget.getData().get( iRow );
                    CellValue< ? > oldCell = row.get( origColIndex );
                    CellValue< ? > newCell = row.get( editColIndex );
                    newCell.setValue( oldCell.getValue() );
                }
            }

            // Delete old column and redraw
            widget.deleteColumn( origCol );
            if ( editColIndex > origColIndex ) {
                int temp = origColIndex;
                origColIndex = editColIndex;
                editColIndex = temp;
            }
            widget.assertModelIndexes();
            widget.getGridWidget().redrawColumns( editColIndex,
                                                  origColIndex );

        } else if ( !isEqualOrNull( origColumn.getFactType(),
                                    editColumn.getFactType() )
                    || !isEqualOrNull( origColumn.getFactField(),
                                       editColumn.getFactField() )
                    || origColumn.getConstraintValueType() != editColumn.getConstraintValueType() ) {

            // Update column's Cell type
            bRedrawColumn = true;
            updateCellsForDataType( editColumn,
                                    column );
        }

        // Update column's cell content if the Optional Value list has changed
        if ( !isEqualOrNull( origColumn.getValueList(),
                             editColumn.getValueList() ) ) {
            bRedrawColumn = updateCellsForOptionValueList( editColumn,
                                                           column );
        }

        // Update column header in Header Widget
        if ( !origColumn.getHeader().equals( editColumn.getHeader() ) ) {
            bRedrawHeader = true;
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        if ( bRedrawColumn ) {
            widget.getGridWidget().redrawColumn( column.getColumnIndex() );
        }
        if ( bRedrawHeader ) {
            // Schedule redraw event after column has been redrawn
            Scheduler.get().scheduleDeferred( new ScheduledCommand() {
                public void execute() {
                    widget.getHeaderWidget().redraw();
                }
            } );
        }

    }

    public void updateSystemControlledColumnValues() {

        DynamicData data = widget.getData();
        List<DynamicColumn<DTColumnConfig>> columns = widget.getColumns();
        for ( DynamicColumn<DTColumnConfig> col : columns ) {

            DTColumnConfig modelColumn = col.getModelColumn();

            if ( modelColumn instanceof RowNumberCol ) {

                // Update Row Number column values
                for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                    data.get( iRow ).get( col.getColumnIndex() ).setValue( (long) iRow + 1 );
                }

            } else if ( modelColumn instanceof AttributeCol ) {

                // Update Salience values
                AttributeCol attrCol = (AttributeCol) modelColumn;
                if ( attrCol.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                    if ( attrCol.isUseRowNumber() ) {
                        col.setSortDirection( SortDirection.NONE );
                        final int MAX_ROWS = data.size();
                        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                            int salience = iRow + 1;
                            if ( attrCol.isReverseOrder() ) {
                                salience = Math.abs( iRow
                                                     - MAX_ROWS );
                            }
                            data.get( iRow ).get( col.getColumnIndex() )
                                    .setValue( (long) salience );
                        }
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
    private void addColumn(DTColumnConfig modelColumn,
                           boolean bRedraw) {
        int index = 0;
        if ( modelColumn instanceof MetadataCol ) {
            index = findMetadataColumnIndex();
        } else if ( modelColumn instanceof AttributeCol ) {
            index = findAttributeColumnIndex();
        } else if ( modelColumn instanceof ConditionCol ) {
            index = findConditionColumnIndex( (ConditionCol) modelColumn );
        } else if ( modelColumn instanceof ActionCol ) {
            index = findActionColumnIndex();
        }
        insertColumnBefore( modelColumn,
                            index,
                            bRedraw );
    }

    // Ensure Condition columns are grouped by pattern (as we merge equal
    // patterns in the UI). This operates on the original Model data and
    // therefore should be called before the Decision Table's internal data
    // representation (i.e. DynamicData, DynamicDataRow and CellValue) is
    // populated
    private void assertConditionColumnGrouping(GuidedDecisionTable model) {

        class ConditionColData {
            ConditionCol col;
            String[]     data;
        }

        // Offset into Model's data array
        final int DATA_COLUMN_OFFSET = model.getMetadataCols().size()
                                       + model.getAttributeCols().size()
                                       + 2;
        Map<String, List<ConditionColData>> groups = new HashMap<String, List<ConditionColData>>();
        final int DATA_ROWS = model.getData().length;

        // Copy conditions and related data into temporary groups
        for ( int iCol = 0; iCol < model.getConditionCols().size(); iCol++ ) {

            ConditionCol col = model.getConditionCols().get( iCol );
            String pattern = col.getBoundName();
            if ( !groups.containsKey( pattern ) ) {
                List<ConditionColData> groupCols = new ArrayList<ConditionColData>();
                groups.put( pattern,
                            groupCols );
            }
            List<ConditionColData> groupCols = groups.get( pattern );

            // Make a ConditionColData object
            ConditionColData ccd = new ConditionColData();
            int colIndex = DATA_COLUMN_OFFSET
                           + iCol;
            ccd.data = new String[DATA_ROWS];
            for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                ccd.data[iRow] = model.getData()[iRow][colIndex];
            }
            ccd.col = col;
            groupCols.add( ccd );

        }

        // Copy temporary groups back into the model
        int iCol = 0;
        model.getConditionCols().clear();
        for ( Map.Entry<String, List<ConditionColData>> me : groups.entrySet() ) {
            for ( ConditionColData ccd : me.getValue() ) {
                model.getConditionCols().add( ccd.col );
                int colIndex = DATA_COLUMN_OFFSET
                               + iCol;
                for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                    model.getData()[iRow][colIndex] = ccd.data[iRow];
                }
                iCol++;
            }
        }

    }

    // Find the right-most index for an Action column
    private int findActionColumnIndex() {
        int index = widget.getColumns().size() - 1;
        return index;
    }

    // Find the right-most index for a Attribute column
    private int findAttributeColumnIndex() {
        int index = 0;
        List<DynamicColumn<DTColumnConfig>> columns = widget.getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig> column = columns.get( iCol );
            DTColumnConfig modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol ) {
                index = iCol;
            } else if ( modelColumn instanceof AttributeCol ) {
                index = iCol;
            }
        }
        return index;
    }

    // Find the right-most index for a Condition column
    private int findConditionColumnIndex(ConditionCol col) {
        int index = 0;
        boolean bMatched = false;
        List<DynamicColumn<DTColumnConfig>> columns = widget.getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig> column = columns.get( iCol );
            DTColumnConfig modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol ) {
                index = iCol;
            } else if ( modelColumn instanceof AttributeCol ) {
                index = iCol;
            } else if ( modelColumn instanceof ConditionCol ) {
                if ( isEquivalentConditionColumn( (ConditionCol) modelColumn,
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
        List<DynamicColumn<DTColumnConfig>> columns = widget.getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig> column = columns.get( iCol );
            DTColumnConfig modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol ) {
                index = iCol;
            }
        }
        return index;
    }

    // Retrieves the DynamicColumn relating to the Model column or null if it
    // cannot be found
    private DynamicColumn<DTColumnConfig> getDynamicColumn(DTColumnConfig modelCol) {
        DynamicColumn<DTColumnConfig> column = null;
        List<DynamicColumn<DTColumnConfig>> columns = widget.getColumns();
        for ( DynamicColumn<DTColumnConfig> dc : columns ) {
            if ( dc.getModelColumn().equals( modelCol ) ) {
                column = dc;
                break;
            }
        }
        return column;
    }

    // Insert a new model column at the specified index
    private void insertColumnBefore(DTColumnConfig modelColumn,
                                    int index,
                                    boolean bRedraw) {

        // Create new column for grid
        DynamicColumn<DTColumnConfig> column = new DynamicColumn<DTColumnConfig>( modelColumn,
                                                                                  cellFactory.getCell( modelColumn ),
                                                                                  index );
        column.setVisible( !modelColumn.isHideColumn() );
        DynamicColumn<DTColumnConfig> columnBefore = widget.getColumns().get( index );

        // Create column data
        DynamicData data = widget.getData();
        List<CellValue< ? >> columnData = new ArrayList<CellValue< ? >>();
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            CellValue< ? > cell = cellValueFactory.getCellValue( modelColumn,
                                                                 iRow,
                                                                 index,
                                                                 modelColumn.getDefaultValue() );
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
    private boolean isEquivalentConditionColumn(ConditionCol c1,
                                                ConditionCol c2) {
        if ( isEqualOrNull( c1.getFactType(),
                            c2.getFactType() )
                && isEqualOrNull( c1.getBoundName(),
                                  c2.getBoundName() ) ) {
            return true;
        }
        return false;
    }

    // Make a row of data for insertion into a DecoratedGridWidget
    private List<CellValue< ? >> makeColumnData(DTColumnConfig column,
                                                int colIndex) {
        int dataSize = model.getData().length;
        List<CellValue< ? >> columnData = new ArrayList<CellValue< ? >>();
        for ( int iRow = 0; iRow < dataSize; iRow++ ) {
            String[] row = model.getData()[iRow];
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.getCellValue( column,
                                                                                      iRow,
                                                                                      colIndex,
                                                                                      row[colIndex] );
            columnData.add( cv );
        }
        return columnData;
    }

    // Construct a new row for insertion into a DecoratedGridWidget
    private DynamicDataRow makeNewRow() {
        DynamicDataRow row = new DynamicDataRow();
        List<DynamicColumn<DTColumnConfig>> columns = widget.getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DTColumnConfig col = columns.get( iCol ).getModelColumn();
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.getCellValue( col,
                                                                                      0,
                                                                                      iCol,
                                                                                      col.getDefaultValue() );
            row.add( cv );
        }
        return row;
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn(final ActionInsertFactCol col,
                                     final ActionInsertFactCol editingCol) {
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
    private void populateModelColumn(final ActionSetFieldCol col,
                                     final ActionSetFieldCol editingCol) {
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
    private void populateModelColumn(final ConditionCol col,
                                     final ConditionCol editingCol) {
        col.setBoundName( editingCol.getBoundName() );
        col.setConstraintValueType( editingCol
                .getConstraintValueType() );
        col.setFactField( editingCol.getFactField() );
        col.setFactType( editingCol.getFactType() );
        col.setHeader( editingCol.getHeader() );
        col.setOperator( editingCol.getOperator() );
        col.setValueList( editingCol.getValueList() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
    }

    // Ensure the Column cell type and corresponding values are correct
    private void updateCellsForDataType(final DTColumnConfig editColumn,
                                        final DynamicColumn<DTColumnConfig> column) {
        DynamicData data = widget.getData();
        column.setCell( cellFactory.getCell( editColumn ) );
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            row.set( column.getColumnIndex(),
                     cellValueFactory.getCellValue( editColumn,
                                                    iRow,
                                                    column.getColumnIndex(),
                                                    null ) );
        }

        // Setting CellValues mashes the indexes
        widget.assertModelIndexes();
    }

    // Ensure the values in a column are within the Value List
    private boolean updateCellsForOptionValueList(final DTColumnConfig editColumn,
                                                  final DynamicColumn<DTColumnConfig> column) {
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

}
