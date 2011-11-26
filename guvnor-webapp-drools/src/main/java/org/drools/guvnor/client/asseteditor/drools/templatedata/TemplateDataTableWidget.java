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
package org.drools.guvnor.client.asseteditor.drools.templatedata;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.util.GWTDateConverter;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractDecoratedGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.SelectedCellValueUpdater;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.AppendRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertTemplateDataColumnEvent;
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
    InsertRowEvent.Handler,
    DeleteRowEvent.Handler,
    AppendRowEvent.Handler,
    DeleteColumnEvent.Handler,
    InsertTemplateDataColumnEvent.Handler<TemplateDataColumn, String> {

    // Decision Table data
    protected TemplateModel                                                          model;
    protected AbstractDecoratedGridWidget<TemplateModel, TemplateDataColumn, String> widget;
    protected TemplateDataCellFactory                                                cellFactory;
    protected TemplateDataCellValueFactory                                           cellValueFactory;
    protected SuggestionCompletionEngine                                             sce;
    protected final EventBus                                                         eventBus;

    protected static final ResourcesProvider<TemplateDataColumn>                     resources = new TemplateDataTableResourcesProvider();

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

        //Callback for cell updates
        //TODO {manstis} This might become an event raised from the UI
        SelectedCellValueUpdater selectedCellValueUpdater = new SelectedCellValueUpdater() {

            public void setSelectedCellsValue(Object value) {
                // TODO {manstis} Add some code
            }

        };

        //Factories for new cell elements
        this.cellFactory = new TemplateDataCellFactory( sce,
                                                        selectedCellValueUpdater );
        this.cellValueFactory = new TemplateDataCellValueFactory( sce );

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedTemplateDataGridWidget( resources,
                                                              cellFactory,
                                                              cellValueFactory,
                                                              eventBus );

        //Date converter is injected so a GWT compatible one can be used here and another in testing
        TemplateDataCellValueFactory.injectDateConvertor( GWTDateConverter.getInstance() );

        setModel( model );

        //Wire-up event handlers
        eventBus.addHandler( DeleteRowEvent.TYPE,
                             this );
        eventBus.addHandler( InsertRowEvent.TYPE,
                             this );
        eventBus.addHandler( AppendRowEvent.TYPE,
                             this );
        eventBus.addHandler( DeleteColumnEvent.TYPE,
                             this );

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

    public void appendRow() {
        AppendRowEvent are = new AppendRowEvent();
        eventBus.fireEvent( are );
    }

    /**
     * Get the number of rows
     */
    public List<List<String>> getRows() {
        return this.model.getTableAsList();
    }

    /**
     * Delete a column
     */
    public void deleteColumn(TemplateDataColumn modelColumn) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        //TODO {manstis} Need to store list of columns probably
        //        int index = model.getAllColumns().indexOf( modelColumn );
        //        DeleteColumnEvent dce = new DeleteColumnEvent( index );
        //        eventBus.fireEvent( dce );
    }

    /**
     * Set column visibility
     */
    public void setColumnVisibility(TemplateDataColumn modelColumn,
                                    boolean isVisible) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null" );
        }

        //TODO {manstis} Needs rewrite to events
        //DynamicColumn<TemplateDataColumn> col = getDynamicColumn( modelColumn );
        //widget.setColumnVisibility( col.getColumnIndex(),isVisible );
    }

    /**
     * Set the Template Data editor's data. This removes all existing columns
     * from the Template Data editor and re-creates them with the provided data.
     * 
     * @param model
     */
    public void setModel(TemplateModel model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        this.model = model;
        this.cellValueFactory.setModel( model );

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
        //TODO {manstis} Needs rewrite to events
        //final List<DynamicColumn<TemplateDataColumn>> columns = widget.getColumns();
        //        for ( int iRow = 0; iRow < data.length; iRow++ ) {
        //            DynamicDataRow row = new DynamicDataRow();
        //            String[] rowData = data[iRow];
        //            for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
        //                TemplateDataColumn col = columns.get( iCol ).getModelColumn();
        //
        //                //Underlying Template model uses empty Strings as null values; which is quite different in the MergedGrid world
        //                String initialValue = rowData[iCol];
        //                if ( initialValue != null && initialValue.equals( "" ) ) {
        //                    initialValue = null;
        //                }
        //                CellValue< ? extends Comparable< ? >> cv = cellValueFactory.convertModelCellValue( col,
        //                                                                                                   initialValue );
        //                row.add( cv );
        //            }
        //            widget.appendRow( row );
        //        }

        // Schedule redraw
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            public void execute() {
//                widget.redraw();
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
        //TODO {manstis} Needs rewrite to events
        //        int index = widget.getColumns().size();
        //        insertColumnBefore( modelColumn,
        //                            index,
        //                            bRedraw );
    }

    // Insert a new model column at the specified index
    private void insertColumnBefore(TemplateDataColumn modelColumn,
                                    int index,
                                    boolean bRedraw) {

        // Create new column for grid
        DynamicColumn<TemplateDataColumn> column = new DynamicColumn<TemplateDataColumn>( modelColumn,
                                                                                          cellFactory.getCell( modelColumn ),
                                                                                          index,
                                                                                          eventBus );
        column.setVisible( true );

        // Create column data
        List<CellValue< ? extends Comparable< ? >>> columnData = makeColumnData( modelColumn,
                                                                                 index );

        // Add column and data to grid
        //TODO {manstis} Needs rewrite to events
        //        if ( index < widget.getColumns().size() ) {
        //            DynamicColumn<TemplateDataColumn> columnBefore = widget.getColumns().get( index );
        //            //widget.insertColumnBefore( columnBefore,column,columnData,bRedraw );
        //        } else {
        //            //widget.appendColumn( column,columnData,bRedraw );
        //        }

    }

    // Make a row of data for insertion into a DecoratedGridWidget
    private List<CellValue< ? extends Comparable< ? >>> makeColumnData(TemplateDataColumn column,
                                                                       int colIndex) {
        int dataSize = this.model.getRowsCount();
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
        model.removeRow( event.getIndex() );
    }

    public void onInsertRow(InsertRowEvent event) {
        List<String> data = cellValueFactory.makeRowData();
        model.addRow( Integer.toString( event.getIndex() ),
                      data.toArray( new String[data.size()] ) );
    }

    public void onAppendRow(AppendRowEvent event) {
        List<String> data = cellValueFactory.makeRowData();
        model.addRow( data.toArray( new String[data.size()] ) );
    }

    public void onDeleteColumn(DeleteColumnEvent event) {
        // TODO {manstis} Add some code
    }

    public void onInsertColumn(InsertColumnEvent<TemplateDataColumn, String> event) {
        // TODO {manstis} Add some code
    }

}
