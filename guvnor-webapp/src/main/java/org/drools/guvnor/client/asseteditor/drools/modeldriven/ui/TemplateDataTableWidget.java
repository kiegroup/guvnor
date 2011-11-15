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
package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.util.GWTDateConverter;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DecoratedGridHeaderWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DecoratedGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.HasColumns;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.HasRows;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.VerticalDecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.VerticalDecoratedGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertRowEvent;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel.InterpolationVariable;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;

/**
 * A table in which Template data can be edited
 */
public class TemplateDataTableWidget extends Composite
    implements
    HasRows<List<String>>,
    //TODO {manstis} HasColumns<T>,
    HasColumns<TemplateDataColumn>,
    InsertRowEvent.Handler,
    DeleteRowEvent.Handler {

    // Decision Table data
    protected TemplateModel                                      model;
    protected DecoratedGridWidget<TemplateDataColumn>            widget;
    protected TemplateDataCellFactory                            cellFactory;
    protected TemplateDataCellValueFactory                       cellValueFactory;
    protected SuggestionCompletionEngine                         sce;
    protected final EventBus                                     eventBus;

    protected static final ResourcesProvider<TemplateDataColumn> resources = new TemplateDataTableResourcesProvider();

    /**
     * Constructor
     */
    public TemplateDataTableWidget(SuggestionCompletionEngine sce,
                                   EventBus eventBus) {
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        this.sce = sce;
        this.eventBus = eventBus;

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedGridWidget<TemplateDataColumn>( resources,
                                                                      eventBus );
        DecoratedGridHeaderWidget<TemplateDataColumn> header = new TemplateDataHeaderWidget( resources,
                                                                                             eventBus,
                                                                                             widget );
        DecoratedGridSidebarWidget<TemplateDataColumn> sidebar = new VerticalDecoratedGridSidebarWidget<TemplateDataColumn>( resources,
                                                                                                                             eventBus,
                                                                                                                             widget );
        widget.setHeaderWidget( header );
        widget.setSidebarWidget( sidebar );

        //Date converter is injected so a GWT compatible one can be used here and another in testing
        TemplateDataCellValueFactory.injectDateConvertor( GWTDateConverter.getInstance() );

        initWidget( widget );
    }

    /**
     * Add a column to the end of the table
     */
    public void addColumn(TemplateDataColumn modelColumn) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        addColumn( modelColumn,
                   true );
    }

    /**
     * Append a row to the end of the table
     */
    public void appendRow() {
        List<String> data = cellValueFactory.makeRowData();
        appendRow( data );
    }

    /**
     * Append an empty row to the end of the table
     * 
     * @param data
     *            The row's data
     */
    public void appendRow(List<String> data) {
        List<CellValue< ? extends Comparable< ? >>> uiData = cellValueFactory.convertRowData( data );
        model.addRow( data.toArray( new String[data.size()] ) );
        widget.appendRow( uiData );
    }

    /**
     * Get the number of rows
     */
    public int rowCount() {
        return this.model.getRowsCount();
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
        widget.deleteColumn( col,
                             true );
    }

    /**
     * Delete a row
     */
    public void deleteRow(int index) {
        model.removeRow( index );
        widget.deleteRow( index );
    }

    /**
     * Insert an empty row before the given row
     * 
     * @param index
     *            The index of the row before which the new (empty) row will be
     *            inserted.
     */
    public void insertRowBefore(int index) {
        List<String> data = cellValueFactory.makeRowData();
        insertRowBefore( index,
                         data );
    }

    /**
     * Insert an empty row before the given row
     * 
     * @param index
     *            The index of the row before which the new (empty) row will be
     *            inserted.
     * @param data
     *            The row's data
     */
    public void insertRowBefore(int index,
                                List<String> data) {
        List<CellValue< ? extends Comparable< ? >>> uiData = cellValueFactory.convertRowData( data );
        model.addRow( Integer.toString( index ),
                      data.toArray( new String[data.size()] ) );
        widget.insertRowBefore( index,
                                uiData );
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

    /**
     * Set the model to render in the table
     * 
     * @param model
     */
    public void setModel(TemplateModel model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }

        this.model = model;
        this.cellFactory = new TemplateDataCellFactory( sce,
                                                        widget );
        this.cellValueFactory = new TemplateDataCellValueFactory( sce,
                                                                  model );

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
        final List<DynamicColumn<TemplateDataColumn>> columns = widget.getColumns();
        for ( int iRow = 0; iRow < data.length; iRow++ ) {
            List<CellValue< ? extends Comparable< ? >>> row = new ArrayList<CellValue< ? extends Comparable< ? >>>();
            String[] rowData = data[iRow];
            for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
                TemplateDataColumn col = columns.get( iCol ).getModelColumn();

                //Underlying Template model uses empty Strings as null values; which is quite different in the MergedGrid world
                String initialValue = rowData[iCol];
                if ( initialValue != null && initialValue.equals( "" ) ) {
                    initialValue = null;
                }
                CellValue< ? extends Comparable< ? >> cv = cellValueFactory.convertModelCellValue( col,
                                                                                                   initialValue );
                row.add( cv );
            }
            widget.appendRow( row );
        }

        // Schedule redraw of grid after sizes of child Elements have been set
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            public void execute() {
                widget.redraw();
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

    // Add column to table with optional redraw
    private void addColumn(TemplateDataColumn modelColumn,
                           boolean bRedraw) {
        int index = widget.getColumns().size();
        insertColumnBefore( modelColumn,
                            index,
                            bRedraw );
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
        List<CellValue< ? extends Comparable< ? >>> columnData = makeColumnData( modelColumn,
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

    // Make a row of data for insertion into a DecoratedGridWidget
    private List<CellValue< ? extends Comparable< ? >>> makeColumnData(TemplateDataColumn column,
                                                                       int colIndex) {
        int dataSize = this.widget.getData().size();
        List<CellValue< ? extends Comparable< ? >>> columnData = new ArrayList<CellValue< ? extends Comparable< ? >>>();
        for ( int iRow = 0; iRow < dataSize; iRow++ ) {
            String value = cellValueFactory.makeModelCellValue( column );
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.convertModelCellValue( column,
                                                                                               value );
            columnData.add( cv );
        }
        return columnData;
    }

    public void onDeleteRow(DeleteRowEvent event) {
        deleteRow( event.getIndex() );
    }

    public void onInsertRow(InsertRowEvent event) {
        insertRowBefore( event.getIndex() );
    }

}
