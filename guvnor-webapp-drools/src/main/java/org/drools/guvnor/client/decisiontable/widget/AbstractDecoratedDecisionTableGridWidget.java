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
import java.util.List;

import org.drools.guvnor.client.decisiontable.widget.events.InsertDecisionTableColumnEvent;
import org.drools.guvnor.client.decisiontable.widget.events.InsertInternalDecisionTableColumnEvent;
import org.drools.guvnor.client.decisiontable.widget.events.SetGuidedDecisionTableModelEvent;
import org.drools.guvnor.client.decisiontable.widget.events.SetInternalDecisionTableModelEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractDecoratedGridHeaderWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractDecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractDecoratedGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractMergableGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicData;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetModelEvent;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.AnalysisCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BaseColumn;
import org.drools.ide.common.client.modeldriven.dt52.CompositeColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Panel;

/**
 * A Decorated Grid for Decision Tables
 */
public abstract class AbstractDecoratedDecisionTableGridWidget extends AbstractDecoratedGridWidget<GuidedDecisionTable52, BaseColumn, DTCellValue52>
    implements
    DeleteColumnEvent.Handler,
    InsertColumnEvent.Handler<BaseColumn, DTCellValue52> {

    //Factories to create new data elements
    protected final DecisionTableCellFactory      cellFactory;
    protected final DecisionTableCellValueFactory cellValueFactory;

    public AbstractDecoratedDecisionTableGridWidget(ResourcesProvider<BaseColumn> resources,
                                                    DecisionTableCellFactory cellFactory,
                                                    DecisionTableCellValueFactory cellValueFactory,
                                                    EventBus eventBus,
                                                    Panel mainPanel,
                                                    Panel bodyPanel,
                                                    AbstractMergableGridWidget<GuidedDecisionTable52, BaseColumn> gridWidget,
                                                    AbstractDecoratedGridHeaderWidget<GuidedDecisionTable52, BaseColumn> headerWidget,
                                                    AbstractDecoratedGridSidebarWidget<GuidedDecisionTable52, BaseColumn> sidebarWidget) {
        super( resources,
               eventBus,
               mainPanel,
               bodyPanel,
               gridWidget,
               headerWidget,
               sidebarWidget );
        if ( cellFactory == null ) {
            throw new IllegalArgumentException( "cellFactory cannot be null" );
        }
        if ( cellValueFactory == null ) {
            throw new IllegalArgumentException( "cellValueFactory cannot be null" );
        }
        this.cellFactory = cellFactory;
        this.cellValueFactory = cellValueFactory;

        //Wire-up event handlers
        eventBus.addHandler( SetGuidedDecisionTableModelEvent.TYPE,
                             this );
        eventBus.addHandler( InsertDecisionTableColumnEvent.TYPE,
                             this );
        eventBus.addHandler( DeleteColumnEvent.TYPE,
                             this );
    }

    public void onSetModel(SetModelEvent<GuidedDecisionTable52> event) {

        GuidedDecisionTable52 model = event.getModel();

        DynamicData data = new DynamicData();
        List<DynamicColumn<BaseColumn>> columns = new ArrayList<DynamicColumn<BaseColumn>>();
        setupInternalModel( model,
                            columns,
                            data );

        //Raise event setting data and columns for UI components
        SetInternalDecisionTableModelEvent sime = new SetInternalDecisionTableModelEvent( model,
                                                                                          data,
                                                                                          columns );
        eventBus.fireEvent( sime );
    }

    private void setupInternalModel(GuidedDecisionTable52 model,
                                    List<DynamicColumn<BaseColumn>> columns,
                                    DynamicData data) {

        int colIndex = 0;
        int columnWidth = 0;
        int defaultColumnWidth = 100;

        // Dummy rows because the underlying DecoratedGridWidget expects there
        // to be enough rows to receive the columns data
        for ( int iRow = 0; iRow < model.getData().size(); iRow++ ) {
            data.addRow();
        }

        // Static columns, Row#
        BaseColumn rowNumberCol = model.getRowNumberCol();
        DynamicColumn<BaseColumn> rowNumberColumn = new DynamicColumn<BaseColumn>( rowNumberCol,
                                                                                   cellFactory.getCell( rowNumberCol ),
                                                                                   colIndex,
                                                                                   true,
                                                                                   false,
                                                                                   eventBus );
        rowNumberColumn.setWidth( 24 );
        columns.add( rowNumberColumn );

        data.addColumn( colIndex,
                        makeRowNumberColumnData( model,
                                                 rowNumberCol,
                                                 colIndex++ ),
                        true );

        // Static columns, Description
        BaseColumn descriptionCol = model.getDescriptionCol();
        DynamicColumn<BaseColumn> descriptionColumn = new DynamicColumn<BaseColumn>( descriptionCol,
                                                                                     cellFactory.getCell( descriptionCol ),
                                                                                     colIndex,
                                                                                     eventBus );
        columnWidth = descriptionCol.getWidth();
        descriptionColumn.setWidth( columnWidth < 0 ? defaultColumnWidth : columnWidth );
        columns.add( descriptionColumn );

        data.addColumn( colIndex,
                        makeColumnData( model,
                                        descriptionCol,
                                        colIndex++ ),
                                        true );

        // Initialise CellTable's Metadata columns
        for ( MetadataCol52 col : model.getMetadataCols() ) {
            DynamicColumn<BaseColumn> column = new DynamicColumn<BaseColumn>( col,
                                                                              cellFactory.getCell( col ),
                                                                              colIndex,
                                                                              eventBus );
            columnWidth = col.getWidth();
            col.setWidth( columnWidth < 0 ? defaultColumnWidth : columnWidth );
            column.setVisible( !col.isHideColumn() );
            columns.add( column );

            data.addColumn( colIndex,
                            makeColumnData( model,
                                            col,
                                            colIndex++ ),
                                            column.isVisible() );
        }

        // Initialise CellTable's Attribute columns
        for ( AttributeCol52 col : model.getAttributeCols() ) {
            DynamicColumn<BaseColumn> column = new DynamicColumn<BaseColumn>( col,
                                                                              cellFactory.getCell( col ),
                                                                              colIndex,
                                                                              eventBus );
            columnWidth = col.getWidth();
            column.setWidth( columnWidth < 0 ? defaultColumnWidth : columnWidth );
            column.setVisible( !col.isHideColumn() );
            column.setSystemControlled( col.isUseRowNumber() );
            column.setSortable( !col.isUseRowNumber() );
            columns.add( column );

            data.addColumn( colIndex,
                            makeColumnData( model,
                                            col,
                                            colIndex++ ),
                                            column.isVisible() );

        }

        // Initialise CellTable's Condition columns
        for ( CompositeColumn< ? > cc : model.getConditions() ) {

            if ( cc instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) cc;
                for ( BRLConditionVariableColumn variable : brl.getChildColumns() ) {
                    DynamicColumn<BaseColumn> column = new DynamicColumn<BaseColumn>( variable,
                                                                                      cellFactory.getCell( variable ),
                                                                                      colIndex,
                                                                                      eventBus );
                    columnWidth = variable.getWidth();
                    column.setWidth( columnWidth < 0 ? defaultColumnWidth : columnWidth );
                    column.setVisible( !variable.isHideColumn() );
                    columns.add( column );

                    data.addColumn( colIndex,
                                    makeColumnData( model,
                                                    variable,
                                                    colIndex++ ),
                                                    column.isVisible() );
                }

            } else if ( cc instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) cc;
                for ( ConditionCol52 col : p.getChildColumns() ) {
                    DynamicColumn<BaseColumn> column = new DynamicColumn<BaseColumn>( col,
                                                                                      cellFactory.getCell( col ),
                                                                                      colIndex,
                                                                                      eventBus );
                    columnWidth = col.getWidth();
                    column.setWidth( columnWidth < 0 ? defaultColumnWidth : columnWidth );
                    column.setVisible( !col.isHideColumn() );
                    columns.add( column );

                    data.addColumn( colIndex,
                                    makeColumnData( model,
                                                    col,
                                                    colIndex++ ),
                                                column.isVisible() );
                }
            }
        }

        // Initialise CellTable's Action columns
        for ( ActionCol52 col : model.getActionCols() ) {
            if ( col instanceof BRLActionColumn ) {
                BRLActionColumn brl = (BRLActionColumn) col;
                for ( BRLActionVariableColumn variable : brl.getChildColumns() ) {
                    DynamicColumn<BaseColumn> column = new DynamicColumn<BaseColumn>( variable,
                                                                                      cellFactory.getCell( variable ),
                                                                                      colIndex,
                                                                                      eventBus );
                    columnWidth = variable.getWidth();
                    column.setWidth( columnWidth < 0 ? defaultColumnWidth : columnWidth );
                    column.setVisible( !variable.isHideColumn() );
                    columns.add( column );

                    data.addColumn( colIndex,
                                    makeColumnData( model,
                                                    variable,
                                                    colIndex++ ),
                                                    column.isVisible() );
                }

            } else {

                DynamicColumn<BaseColumn> column = new DynamicColumn<BaseColumn>( col,
                                                                                  cellFactory.getCell( col ),
                                                                                  colIndex,
                                                                                  eventBus );
                columnWidth = col.getWidth();
                column.setWidth( columnWidth < 0 ? defaultColumnWidth : columnWidth );
                column.setVisible( !col.isHideColumn() );
                columns.add( column );

                data.addColumn( colIndex,
                                makeColumnData( model,
                                                col,
                                                colIndex++ ),
                                                column.isVisible() );
            }

        }

        AnalysisCol52 analysisCol = model.getAnalysisCol();
        DynamicColumn<BaseColumn> analysisColumn = new DynamicColumn<BaseColumn>( analysisCol,
                                                                                  cellFactory.getCell( analysisCol ),
                                                                                  colIndex,
                                                                                  true,
                                                                                  false,
                                                                                  eventBus );
        analysisColumn.setVisible( !analysisCol.isHideColumn() );
        analysisColumn.setWidth( 200 );
        columns.add( analysisColumn );

        data.addColumn( colIndex,
                        makeAnalysisColumnData( model,
                                                analysisCol,
                                                colIndex++ ),
                                                analysisColumn.isVisible() );
    }

    // Make a column of data representing the Row Number column for insertion into a DecoratedGridWidget
    // We don't rely upon the values in the existing data as legacy tables co't guarantee it is sorted
    private List<CellValue< ? extends Comparable< ? >>> makeRowNumberColumnData(GuidedDecisionTable52 model,
                                                                                BaseColumn column,
                                                                                int colIndex) {
        int dataSize = model.getData().size();
        List<CellValue< ? extends Comparable< ? >>> columnData = new ArrayList<CellValue< ? extends Comparable< ? >>>( dataSize );

        for ( int iRow = 0; iRow < dataSize; iRow++ ) {
            DTCellValue52 dcv = new DTCellValue52( iRow + 1 );
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.convertModelCellValue( column,
                                                                                               dcv );
            columnData.add( cv );
        }
        return columnData;
    }

    // Make a column of data for insertion into a DecoratedGridWidget
    private List<CellValue< ? extends Comparable< ? >>> makeColumnData(GuidedDecisionTable52 model,
                                                                       BaseColumn column,
                                                                       int colIndex) {
        int dataSize = model.getData().size();
        List<CellValue< ? extends Comparable< ? >>> columnData = new ArrayList<CellValue< ? extends Comparable< ? >>>( dataSize );

        for ( int iRow = 0; iRow < dataSize; iRow++ ) {
            List<DTCellValue52> row = model.getData().get( iRow );
            DTCellValue52 dcv = row.get( colIndex );
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.convertModelCellValue( column,
                                                                                               dcv );
            columnData.add( cv );
        }
        return columnData;
    }

    // Make a column of data representing the Analysis column for insertion into a DecoratedGridWidget
    private List<CellValue< ? extends Comparable< ? >>> makeAnalysisColumnData(GuidedDecisionTable52 model,
                                                                               AnalysisCol52 column,
                                                                               int colIndex) {
        model.initAnalysisColumn();
        int dataSize = model.getAnalysisData().size();
        List<CellValue< ? extends Comparable< ? >>> columnData = new ArrayList<CellValue< ? extends Comparable< ? >>>( dataSize );

        for ( int iRow = 0; iRow < dataSize; iRow++ ) {
            CellValue< ? extends Comparable< ? >> cv = cellValueFactory.makeNewAnalysisCellValue();
            columnData.add( cv );
        }
        return columnData;
    }

    public void onInsertColumn(InsertColumnEvent<BaseColumn, DTCellValue52> event) {
        DynamicColumn<BaseColumn> column = new DynamicColumn<BaseColumn>( event.getColumn(),
                                                                          cellFactory.getCell( event.getColumn() ),
                                                                          eventBus );
        column.setVisible( !event.getColumn().isHideColumn() );
        List<CellValue< ? extends Comparable< ? >>> data = cellValueFactory.convertColumnData( event.getColumn(),
                                                                                               event.getColumnData() );

        //Raise event setting data and columns for UI components
        InsertInternalDecisionTableColumnEvent ice = new InsertInternalDecisionTableColumnEvent( column,
                                                                                                 event.getIndex(),
                                                                                                 event.redraw(),
                                                                                                 data );
        eventBus.fireEvent( ice );

        //Assert dimensions once column has been added
        if ( event.redraw() ) {
            Scheduler.get().scheduleDeferred( new Command() {

                public void execute() {
                    assertDimensions();
                }

            } );
        }
    }

    public void onDeleteColumn(DeleteColumnEvent event) {
        if ( event.redraw() ) {
            Scheduler.get().scheduleDeferred( new Command() {

                public void execute() {
                    assertDimensions();
                }

            } );
        }
    }

}
