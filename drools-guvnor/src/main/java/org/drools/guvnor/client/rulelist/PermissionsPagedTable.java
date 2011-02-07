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

package org.drools.guvnor.client.rulelist;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PermissionsPageRow;
import org.drools.guvnor.client.table.ColumnPicker;
import org.drools.guvnor.client.table.SelectionColumn;
import org.drools.guvnor.client.table.SortableHeader;
import org.drools.guvnor.client.table.SortableHeaderGroup;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Widget with a table of Permission entries.
 * 
 * @author manstis
 */
public class PermissionsPagedTable extends AbstractPagedTable<PermissionsPageRow> {

    private static final int                           PAGE_SIZE = 10;

    protected OpenItemCommand                          openCommand;
    protected SingleSelectionModel<PermissionsPageRow> selectionModel;

    public SingleSelectionModel<PermissionsPageRow> getSelectionModel() {
        return this.selectionModel;
    }

    public PermissionsPagedTable(OpenItemCommand openCommand) {
        super( PAGE_SIZE );
        this.openCommand = openCommand;
        setDataProvider( new AsyncDataProvider<PermissionsPageRow>() {
            protected void onRangeChanged(HasData<PermissionsPageRow> display) {
                PageRequest request = new PageRequest();
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                repositoryService.listUserPermissions( request,
                                                             new GenericCallback<PageResponse<PermissionsPageRow>>() {
                                                                 public void onSuccess(PageResponse<PermissionsPageRow> response) {
                                                                     updateRowCount( response.getTotalRowSize(),
                                                                                     response.isTotalRowSizeExact() );
                                                                     updateRowData( response.getStartRowIndex(),
                                                                                    response.getPageRowList() );
                                                                 }
                                                             } );
            }
        } );
    }

    @Override
    protected void doCellTable() {

        ProvidesKey<PermissionsPageRow> providesKey = new ProvidesKey<PermissionsPageRow>() {
            public Object getKey(PermissionsPageRow row) {
                return row.getUserName();
            }
        };

        cellTable = new CellTable<PermissionsPageRow>( providesKey );
        selectionModel = new SingleSelectionModel<PermissionsPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<PermissionsPageRow> columnPicker = new ColumnPicker<PermissionsPageRow>( cellTable );
        SortableHeaderGroup<PermissionsPageRow> sortableHeaderGroup = new SortableHeaderGroup<PermissionsPageRow>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        // Add "Open" button column
        Column<PermissionsPageRow, String> openColumn = new Column<PermissionsPageRow, String>( new ButtonCell() ) {
            public String getValue(PermissionsPageRow row) {
                return constants.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<PermissionsPageRow, String>() {
            public void update(int index,
                               PermissionsPageRow row,
                               String value) {
                openCommand.open( row.getUserName() );
            }
        } );
        columnPicker.addColumn( openColumn,
                                new TextHeader( constants.Open() ),
                                true );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<PermissionsPageRow> columnPicker,
                                       SortableHeaderGroup<PermissionsPageRow> sortableHeaderGroup) {

        Column<PermissionsPageRow, String> userNameColumn = new Column<PermissionsPageRow, String>( new TextCell() ) {
            public String getValue(PermissionsPageRow row) {
                return row.getUserName();
            }
        };
        columnPicker.addColumn( userNameColumn,
                                new SortableHeader<PermissionsPageRow, String>(
                                                                                sortableHeaderGroup,
                                                                                constants.UserName1(),
                                                                                userNameColumn ),
                                true );

        Column<PermissionsPageRow, String> isAdminColumn = new Column<PermissionsPageRow, String>( new TextCell() ) {
            public String getValue(PermissionsPageRow row) {
                return row.isAdministrator() ? constants.Yes() : "";
            }
        };
        columnPicker.addColumn( isAdminColumn,
                                new SortableHeader<PermissionsPageRow, String>(
                                                                                sortableHeaderGroup,
                                                                                constants.Administrator(),
                                                                                isAdminColumn ),
                                true );

        Column<PermissionsPageRow, String> hasPackagePermissionsColumn = new Column<PermissionsPageRow, String>( new TextCell() ) {
            public String getValue(PermissionsPageRow row) {
                return row.hasPackagePermissions() ? constants.Yes() : "";
            }
        };
        columnPicker.addColumn( hasPackagePermissionsColumn,
                                new SortableHeader<PermissionsPageRow, String>(
                                                                                sortableHeaderGroup,
                                                                                constants.HasPackagePermissions(),
                                                                                hasPackagePermissionsColumn ),
                                true );

        Column<PermissionsPageRow, String> hasCategoryPermissionsColumn = new Column<PermissionsPageRow, String>( new TextCell() ) {
            public String getValue(PermissionsPageRow row) {
                return row.hasCategoryPermissions() ? constants.Yes() : "";
            }
        };
        columnPicker.addColumn( hasCategoryPermissionsColumn,
                                new SortableHeader<PermissionsPageRow, String>(
                                                                                sortableHeaderGroup,
                                                                                constants.HasCategoryPermissions(),
                                                                                hasCategoryPermissionsColumn ),
                                true );

    }
    
    void openSelected(ClickEvent e) {
        PermissionsPageRow selectedItem = selectionModel.getSelectedObject();
            openCommand.open( selectedItem.getUserName() );
    }

}
