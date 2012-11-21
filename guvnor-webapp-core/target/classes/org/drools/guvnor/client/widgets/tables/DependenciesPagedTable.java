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

package org.drools.guvnor.client.widgets.tables;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.moduleeditor.DependencyWidget;
import org.drools.guvnor.client.rpc.DependenciesPageRow;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.widgets.query.OpenItemCommand;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Widget with a table of Dependencies entries.
 * 
 */
public class DependenciesPagedTable extends AbstractPagedTable<DependenciesPageRow> {
    // UI
    interface DependenciesPagedTableBinder
        extends
        UiBinder<Widget, DependenciesPagedTable> {
    }

    private static DependenciesPagedTableBinder         uiBinder  = GWT.create( DependenciesPagedTableBinder.class );

    // Commands for UI
    private OpenItemCommand                            openSelectedCommand;

    // Other stuff
    private static final int                           PAGE_SIZE = 5;

    protected SingleSelectionModel<DependenciesPageRow> selectionModel;
    
    private final String uuid;

    public DependenciesPagedTable(String theUuid,
            OpenItemCommand openSelectedCommand) {
        super( PAGE_SIZE );
        this.openSelectedCommand = openSelectedCommand;
        this.uuid = theUuid;
        
        setDataProvider( new AsyncDataProvider<DependenciesPageRow>() {
            protected void onRangeChanged(HasData<DependenciesPageRow> display) {
                LoadingPopup.showMessage("please wait...");
                RepositoryServiceFactory.getPackageService().getDependencies( uuid,
                        new GenericCallback<String[]>() {
                            public void onSuccess(String[] dependencies) {
                                LoadingPopup.close();
                                final List<DependenciesPageRow> dependencyList = new ArrayList<DependenciesPageRow>();
                                for(String dependency: dependencies) {
                                    DependenciesPageRow row = new DependenciesPageRow();
                                    row.setDependencyPath(DependencyWidget.decodeDependencyPath(dependency)[0]);
                                    row.setDependencyVersion(DependencyWidget.decodeDependencyPath(dependency)[1]);
                                    dependencyList.add(row);
                                }
                                updateRowCount( dependencyList.size(), true );
                                updateRowData( 0, dependencyList );
                            }
                        } );

            }
        } );
    }

    public SingleSelectionModel<DependenciesPageRow> getSelectionModel() {
        return this.selectionModel;
    }
    
    @Override
    protected void doCellTable() {
        ProvidesKey<DependenciesPageRow> providesKey = new ProvidesKey<DependenciesPageRow>() {
            public Object getKey(DependenciesPageRow row) {
                return row.getDependencyPath();
            }
        };

        cellTable = new CellTable<DependenciesPageRow>( providesKey );
        selectionModel = new SingleSelectionModel<DependenciesPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<DependenciesPageRow> columnPicker = new ColumnPicker<DependenciesPageRow>( cellTable );
        SortableHeaderGroup<DependenciesPageRow> sortableHeaderGroup = new SortableHeaderGroup<DependenciesPageRow>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        // Add "Open" button column

        Column<DependenciesPageRow, String> openColumn = new Column<DependenciesPageRow, String>( new ButtonCell() ) {
            public String getValue(DependenciesPageRow row) {
                return constants.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<DependenciesPageRow, String>() {
            public void update(int index,
            		DependenciesPageRow row,
                               String value) {
            	openSelectedCommand.open( DependencyWidget.encodeDependencyPath(row.getDependencyPath(), row.getDependencyVersion()) );
            }
        } );

        columnPicker.addColumn( openColumn,
                                new TextHeader( constants.Open() ),
                                true );
        
        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<DependenciesPageRow> columnPicker,
                                       SortableHeaderGroup<DependenciesPageRow> sortableHeaderGroup) {

        Column<DependenciesPageRow, String> dependencyPathColumn = new Column<DependenciesPageRow, String>( new TextCell() ) {
            public String getValue(DependenciesPageRow row) {
                return row.getDependencyPath();
            }
        };
        columnPicker.addColumn( dependencyPathColumn,
                                new SortableHeader<DependenciesPageRow, String>(
                                                                                sortableHeaderGroup,
                                                                                "Dependency Path",
                                                                                dependencyPathColumn ),
                                true );

        Column<DependenciesPageRow, String> dependencyVersionColumn = new Column<DependenciesPageRow, String>( new TextCell() ) {
            public String getValue(DependenciesPageRow row) {
            	return row.getDependencyVersion();
            }
        };
        columnPicker.addColumn( dependencyVersionColumn,
                                new SortableHeader<DependenciesPageRow, String>(
                                                                                sortableHeaderGroup,
                                                                                "Dependency Version",
                                                                                dependencyVersionColumn ),
                                true );
    }
    
    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("refreshButton")
    void refresh(ClickEvent e) {
        refresh();
    }
}
