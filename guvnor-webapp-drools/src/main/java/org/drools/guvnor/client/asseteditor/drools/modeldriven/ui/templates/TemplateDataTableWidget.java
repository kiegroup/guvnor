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
package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates.events.SetTemplateDataEvent;
import org.drools.guvnor.client.util.GWTDateConverter;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractDecoratedGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.Coordinate;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.AppendRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.CopyRowsEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.PasteRowsEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.UpdateModelEvent;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.templates.InterpolationVariable;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;

/**
 * A table in which Template data can be edited
 */
public class TemplateDataTableWidget extends Composite
    implements
    InsertRowEvent.Handler,
    DeleteRowEvent.Handler,
    AppendRowEvent.Handler,
    CopyRowsEvent.Handler,
    PasteRowsEvent.Handler,
    UpdateModelEvent.Handler {

    // Decision Table data
    protected TemplateModel                                                          model;
    protected AbstractDecoratedGridWidget<TemplateModel, TemplateDataColumn, String> widget;
    protected TemplateDataCellFactory                                                cellFactory;
    protected TemplateDataCellValueFactory                                           cellValueFactory;
    protected SuggestionCompletionEngine                                             sce;

    //This EventBus is local to the screen and should be used for local operations, set data, add rows etc
    private EventBus                                                                 eventBus   = new SimpleEventBus();

    //This EventBus is global to Guvnor and should be used for global operations, navigate pages etc 
    @SuppressWarnings("unused")
    private EventBus                                                                 globalEventBus;

    //Rows that have been copied in a copy-paste operation
    private List<String[]>                                                           copiedRows = new ArrayList<String[]>();

    protected static final ResourcesProvider<TemplateDataColumn>                     resources  = new TemplateDataTableResourcesProvider();

    /**
     * Constructor
     */
    public TemplateDataTableWidget(SuggestionCompletionEngine sce,
                                   EventBus globalEventBus) {
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        if ( globalEventBus == null ) {
            throw new IllegalArgumentException( "globalEventBus cannot be null" );
        }
        this.sce = sce;
        this.globalEventBus = globalEventBus;

        //Factories for new cell elements
        this.cellFactory = new TemplateDataCellFactory( sce,
                                                        eventBus );
        this.cellValueFactory = new TemplateDataCellValueFactory( sce );

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedTemplateDataGridWidget( resources,
                                                              cellFactory,
                                                              cellValueFactory,
                                                              eventBus );

        //Date converter is injected so a GWT compatible one can be used here and another in testing
        TemplateDataCellValueFactory.injectDateConvertor( GWTDateConverter.getInstance() );

        //Wire-up event handlers
        eventBus.addHandler( DeleteRowEvent.TYPE,
                             this );
        eventBus.addHandler( InsertRowEvent.TYPE,
                             this );
        eventBus.addHandler( AppendRowEvent.TYPE,
                             this );
        eventBus.addHandler( CopyRowsEvent.TYPE,
                             this );
        eventBus.addHandler( PasteRowsEvent.TYPE,
                             this );
        eventBus.addHandler( UpdateModelEvent.TYPE,
                             this );

        initWidget( widget );
    }

    public void appendRow() {
        AppendRowEvent are = new AppendRowEvent();
        eventBus.fireEvent( are );
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

        //Fire event for UI components to set themselves up
        SetTemplateDataEvent sme = new SetTemplateDataEvent( model );
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

    public void onDeleteRow(DeleteRowEvent event) {
        model.removeRow( event.getIndex() );
    }

    public void onCopyRows(CopyRowsEvent event) {
        copiedRows.clear();
        for ( Integer iRow : event.getRowIndexes() ) {
            String[] rowData = model.getTableAsArray()[iRow];
            copiedRows.add( rowData );
        }
    }

    public void onPasteRows(PasteRowsEvent event) {
        if ( copiedRows == null || copiedRows.size() == 0 ) {
            return;
        }
        int iRow = event.getTargetRowIndex();
        for ( String[] sourceRowData : copiedRows ) {
            String[] rowData = cellValueFactory.makeRowData().toArray( new String[0] );
            for ( int iCol = 0; iCol < sourceRowData.length; iCol++ ) {
                rowData[iCol] = sourceRowData[iCol];
            }
            model.addRow( iRow,
                          rowData );
            iRow++;
        }

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

    public void onUpdateModel(UpdateModelEvent event) {

        //Copy data into the underlying model
        List<List<CellValue< ? extends Comparable< ? >>>> changedData = event.getData();
        Coordinate originCoordinate = event.getOriginCoordinate();
        int originRowIndex = originCoordinate.getRow();
        int originColumnIndex = originCoordinate.getCol();

        InterpolationVariable[] vars = model.getInterpolationVariablesList();

        for ( int iRow = 0; iRow < changedData.size(); iRow++ ) {
            List<CellValue< ? extends Comparable< ? >>> changedRow = changedData.get( iRow );
            int targetRowIndex = originRowIndex + iRow;
            for ( int iCol = 0; iCol < changedRow.size(); iCol++ ) {
                int targetColumnIndex = originColumnIndex + iCol;
                CellValue< ? extends Comparable< ? >> changedCell = changedRow.get( iCol );

                InterpolationVariable var = vars[targetColumnIndex];
                TemplateDataColumn col = new TemplateDataColumn( var.getVarName(),
                                                                 var.getDataType(),
                                                                 var.getFactType(),
                                                                 var.getFactField() );

                String dcv = cellValueFactory.convertToModelCell( col,
                                                                  changedCell );

                List<String> columnData = model.getTable().get( var.getVarName() );
                columnData.set( targetRowIndex,
                                dcv );
            }
        }

    }

}
