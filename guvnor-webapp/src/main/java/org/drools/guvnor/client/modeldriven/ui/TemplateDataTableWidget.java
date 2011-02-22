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
package org.drools.guvnor.client.modeldriven.ui;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.widgets.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridHeaderWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicDataRow;
import org.drools.guvnor.client.widgets.decoratedgrid.HasColumns;
import org.drools.guvnor.client.widgets.decoratedgrid.HasRows;
import org.drools.guvnor.client.widgets.decoratedgrid.VerticalDecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.VerticalDecoratedGridWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel.InterpolationVariable;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;

/**
 * A table in which Template data can be edited
 */
public class TemplateDataTableWidget extends Composite
    implements
    HasRows,
    HasColumns<TemplateDataColumn> {

    // Decision Table data
    protected DecoratedGridWidget<TemplateDataColumn> widget;
    protected TemplateDataCellFactory                 cellFactory;
    protected TemplateDataCellValueFactory            cellValueFactory;
    protected SuggestionCompletionEngine              sce;

    /**
     * Constructor
     */
    public TemplateDataTableWidget(SuggestionCompletionEngine sce) {

        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.sce = sce;

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedGridWidget<TemplateDataColumn>();
        DecoratedGridHeaderWidget<TemplateDataColumn> header = new TemplateDataHeaderWidget( widget );
        DecoratedGridSidebarWidget<TemplateDataColumn> sidebar = new VerticalDecoratedGridSidebarWidget<TemplateDataColumn>( widget,
                                                                                                                             this );
        widget.setHeaderWidget( header );
        widget.setSidebarWidget( sidebar );

        this.cellFactory = new TemplateDataCellFactory( sce,
                                                        widget );
        this.cellValueFactory = new TemplateDataCellValueFactory();

        initWidget( widget );
    }

    /**
     * Add a column to the end of the table
     */
    public void addColumn(TemplateDataColumn modelColumn) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException(
                                                "modelColumn cannot be null." );
        }
        addColumn( modelColumn,
                   true );
    }

    // Add column to table with optional redraw
    private void addColumn(TemplateDataColumn modelColumn,
                           boolean bRedraw) {
        int index = widget.getColumns().size();
        insertColumnBefore( modelColumn,
                            index,
                            bRedraw );
    }

    /**
     * Append a row to the end of the table
     */
    public void appendRow() {
        DynamicDataRow row = makeNewRow();
        widget.appendRow( row );
    }

    /**
     * Delete a column
     */
    public void deleteColumn(TemplateDataColumn modelColumn) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException(
                                                "modelColumn cannot be null." );
        }

        DynamicColumn<TemplateDataColumn> col = getDynamicColumn( modelColumn );
        widget.deleteColumn( col );
    }

    /**
     * Delete a row
     */
    public void deleteRow(DynamicDataRow row) {
        if ( row == null ) {
            throw new IllegalArgumentException( "row cannot be null" );
        }
        widget.deleteRow( row );
    }

    // Retrieves the DynamicColumn relating to the Model column or null if it
    // cannot be found
    private DynamicColumn<TemplateDataColumn> getDynamicColumn(TemplateDataColumn modelCol) {
        DynamicColumn<TemplateDataColumn> column = null;
        List<DynamicColumn<TemplateDataColumn>> columns = widget.getColumns();
        for ( DynamicColumn<TemplateDataColumn> dc : columns ) {
            if ( dc.getModelColumn().equals( modelCol ) ) {
                column = dc;
                break;
            }
        }
        return column;
    }

    // Insert a new model column at the specified index
    private void insertColumnBefore(TemplateDataColumn modelColumn,
                                    int index,
                                    boolean bRedraw) {

        // Create new column for grid
        DynamicColumn<TemplateDataColumn> column = new DynamicColumn<TemplateDataColumn>( modelColumn,
                                                                                          cellFactory.getCell( modelColumn ),
                                                                                          index );
        column.setVisible( true );

        // Create column data
        List<CellValue< ? >> columnData = makeColumnData( modelColumn,
                                                          index );

        // Add column and data to grid
        if ( index < widget.getColumns().size() ) {
            DynamicColumn<TemplateDataColumn> columnBefore = widget.getColumns().get( index );
            widget.insertColumnBefore( columnBefore,
                                       column,
                                       columnData,
                                       bRedraw );
        } else {
            widget.appendColumn( column,
                                 columnData,
                                 bRedraw );
        }

    }

    /**
     * Insert a row before that provided
     * 
     * @param rowBefore
     *            the row before which the new row should be inserted
     */
    public void insertRowBefore(DynamicDataRow rowBefore) {
        if ( rowBefore == null ) {
            throw new IllegalArgumentException( "rowBefore cannot be null" );
        }

        DynamicDataRow newRow = makeNewRow();
        widget.insertRowBefore( rowBefore,
                                newRow );
    }

    // Make a row of data for insertion into a DecoratedGridWidget
    private List<CellValue< ? >> makeColumnData(TemplateDataColumn column,
                                                int colIndex) {
        int dataSize = this.widget.getData().size();
        List<CellValue< ? >> columnData = new ArrayList<CellValue< ? >>();
        for ( int iRow = 0; iRow < dataSize; iRow++ ) {
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.getCellValue( column,
                                                                                      iRow,
                                                                                      colIndex,
                                                                                      null );
            columnData.add( cv );
        }
        return columnData;
    }

    // Construct a new row for insertion into a DecoratedGridWidget
    private DynamicDataRow makeNewRow() {
        DynamicDataRow row = new DynamicDataRow();
        List<DynamicColumn<TemplateDataColumn>> columns = widget.getColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            TemplateDataColumn col = columns.get( iCol ).getModelColumn();
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.getCellValue( col,
                                                                                      0,
                                                                                      iCol,
                                                                                      null );
            row.add( cv );
        }
        return row;
    }

    /**
     * Set column visibility
     */
    public void setColumnVisibility(TemplateDataColumn modelColumn,
                                    boolean isVisible) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null" );
        }

        DynamicColumn<TemplateDataColumn> col = getDynamicColumn( modelColumn );
        widget.setColumnVisibility( col.getColumnIndex(),
                                            isVisible );
    }

    public void scrapeData(TemplateModel model) {
        model.clearRows();

        List<DynamicColumn<TemplateDataColumn>> columns = widget.getColumns();
        int columnCount = columns.size();

        for ( DynamicDataRow row : widget.getData() ) {

            String[] rowData = new String[columnCount];

            for ( int iCol = 0; iCol < columnCount; iCol++ ) {
                CellValue< ? > cv = row.get( iCol );
                DynamicColumn<TemplateDataColumn> column = columns.get( iCol );
                String serialisedValue = cellValueFactory.serialiseValue( column.getModelColumn(),
                                                                          cv );
                rowData[iCol] = serialisedValue;
            }
            model.addRow( rowData );
        }

    }

    /**
     * Set the model to render in the table
     * 
     * @param model
     */
    public void setModel(TemplateModel model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }

        //Get interpolation variables
        InterpolationVariable[] vars = model.getInterpolationVariablesList();
        if ( vars.length == 0 ) {
            return;
        }

        //Add corresponding columns to table
        for ( InterpolationVariable var : vars ) {
            addColumn( new TemplateDataColumn( var.getVarName(),
                                               var.getDataType(),
                                               var.getFactType(),
                                               var.getFactField() ),
                       false );
        }

        //Set row data
        String[][] data = model.getTableAsArray();
        for ( int iRow = 0; iRow < data.length; iRow++ ) {
            DynamicDataRow row = new DynamicDataRow();
            String[] rowData = data[iRow];
            for ( int iCol = 0; iCol < widget.getColumns().size(); iCol++ ) {
                TemplateDataColumn col = widget.getColumns().get( iCol ).getModelColumn();

                //Underlying Template model uses empty Strings as null values; which is quite different in the MergedGrid world
                String initialValue = rowData[iCol];
                if ( initialValue != null && initialValue.equals( "" ) ) {
                    initialValue = null;
                }

                CellValue< ? extends Comparable< ? >> cv = cellValueFactory.getCellValue( col,
                                                                                          iRow,
                                                                                          iCol,
                                                                                          initialValue );
                row.add( cv );
            }
            widget.appendRow( row );
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

}
