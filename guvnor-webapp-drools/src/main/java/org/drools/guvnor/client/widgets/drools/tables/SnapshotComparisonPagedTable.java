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

package org.drools.guvnor.client.widgets.drools.tables;

import java.util.Set;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRow;
import org.drools.guvnor.client.widgets.tables.AbstractPagedTable;
import org.drools.guvnor.client.widgets.tables.ColumnPicker;
import org.drools.guvnor.client.widgets.tables.SelectionColumn;
import org.drools.guvnor.client.widgets.tables.SortableHeader;
import org.drools.guvnor.client.widgets.tables.SortableHeaderGroup;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Widget with a table of Snapshot comparison entries.
 */
public class SnapshotComparisonPagedTable extends AbstractPagedTable<SnapshotComparisonPageRow> {

    private final ClientFactory clientFactory;

    // UI
    interface SnapshotComparisonPagedTableBinder
        extends
        UiBinder<Widget, SnapshotComparisonPagedTable> {
    }

    private static SnapshotComparisonPagedTableBinder uiBinder = GWT.create( SnapshotComparisonPagedTableBinder.class );

    @UiField()
    protected Button                                  openSelectedButton;

    public MultiSelectionModel<SnapshotComparisonPageRow> getSelectionModel() {
        return this.selectionModel;
    }

    // Other stuff
    private static final int                                  PAGE_SIZE = 10;
    protected MultiSelectionModel<SnapshotComparisonPageRow>  selectionModel;
    private SortableHeader<SnapshotComparisonPageRow, String> lhsSnapshotHeader;
    private SortableHeader<SnapshotComparisonPageRow, String> rhsSnapshotHeader;

    /**
     * Constructor
     * 
     * @param packageName
     * @param firstSnapshotName
     * @param secondSnapshotName
     */
    public SnapshotComparisonPagedTable(final String packageName,
                                        final String firstSnapshotName,
                                        final String secondSnapshotName,
                                        ClientFactory clientFactory) {
        super( PAGE_SIZE );
        this.clientFactory = clientFactory;

        setDataProvider( new AsyncDataProvider<SnapshotComparisonPageRow>() {
            protected void onRangeChanged(HasData<SnapshotComparisonPageRow> display) {
                SnapshotComparisonPageRequest request = new SnapshotComparisonPageRequest();
                request.setPageSize( pageSize );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPackageName( packageName );
                request.setFirstSnapshotName( firstSnapshotName );
                request.setSecondSnapshotName( secondSnapshotName );
                packageService.compareSnapshots( request,
                                                             new GenericCallback<SnapshotComparisonPageResponse>() {
                                                                 public void onSuccess(SnapshotComparisonPageResponse response) {
                                                                     updateRowCount( response.getTotalRowSize(),
                                                                                     response.isTotalRowSizeExact() );
                                                                     updateRowData( response.getStartRowIndex(),
                                                                                    response.getPageRowList() );
                                                                     lhsSnapshotHeader.setValue( Constants.INSTANCE.Older0(
                                                                                                                response.getLeftSnapshotName() ) );
                                                                     rhsSnapshotHeader.setValue( Constants.INSTANCE.Newer0(
                                                                                                                response.getRightSnapshotName() ) );
                                                                 }
                                                             } );
            }
        } );
    }

    @Override
    protected void doCellTable() {

        ProvidesKey<SnapshotComparisonPageRow> providesKey = new ProvidesKey<SnapshotComparisonPageRow>() {
            public Object getKey(SnapshotComparisonPageRow row) {
                return row.getDiff().leftUuid;
            }
        };

        cellTable = new CellTable<SnapshotComparisonPageRow>( providesKey );
        selectionModel = new MultiSelectionModel<SnapshotComparisonPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<SnapshotComparisonPageRow> columnPicker = new ColumnPicker<SnapshotComparisonPageRow>( cellTable );
        SortableHeaderGroup<SnapshotComparisonPageRow> sortableHeaderGroup = new SortableHeaderGroup<SnapshotComparisonPageRow>( cellTable );

        final TextColumn<SnapshotComparisonPageRow> uuidNumberColumn = new TextColumn<SnapshotComparisonPageRow>() {
            public String getValue(SnapshotComparisonPageRow row) {
                return row.getDiff().rightUuid;
            }
        };
        columnPicker.addColumn( uuidNumberColumn,
                                new SortableHeader<SnapshotComparisonPageRow, String>(
                                                                                       sortableHeaderGroup,
                                                                                       Constants.INSTANCE.uuid(),
                                                                                       uuidNumberColumn ),
                                false );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        // Add "Open" button column
        Column<SnapshotComparisonPageRow, String> openColumn = new Column<SnapshotComparisonPageRow, String>( new ButtonCell() ) {
            public String getValue(SnapshotComparisonPageRow row) {
                return Constants.INSTANCE.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<SnapshotComparisonPageRow, String>() {
            public void update(int index,
                               SnapshotComparisonPageRow row,
                               String value) {
                clientFactory.getPlaceManager().goTo( new AssetEditorPlace( row.getDiff().rightUuid ));
            }
        } );
        columnPicker.addColumn( openColumn,
                                new TextHeader( Constants.INSTANCE.Open() ),
                                true );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<SnapshotComparisonPageRow> columnPicker,
                                       SortableHeaderGroup<SnapshotComparisonPageRow> sortableHeaderGroup) {

        Column<SnapshotComparisonPageRow, String> lhsSnapshotColumn = new Column<SnapshotComparisonPageRow, String>( new TextCell() ) {
            public String getValue(SnapshotComparisonPageRow row) {
                return row.getDiff().name;
            }
        };
        // Header text is set in call-back from Repository service
        this.lhsSnapshotHeader = new SortableHeader<SnapshotComparisonPageRow, String>(
                                                                                        sortableHeaderGroup,
                                                                                        "",
                                                                                        lhsSnapshotColumn );
        columnPicker.addColumn( lhsSnapshotColumn,
                                this.lhsSnapshotHeader,
                                true );

        Column<SnapshotComparisonPageRow, String> comparisonTypeColumn = new Column<SnapshotComparisonPageRow, String>( new SnapshotComparisonTypeCell() ) {
            public String getValue(SnapshotComparisonPageRow row) {
                return row.getDiff().diffType;
            }
        };
        columnPicker.addColumn( comparisonTypeColumn,
                                new SortableHeader<SnapshotComparisonPageRow, String>(
                                                                                       sortableHeaderGroup,
                                                                                       Constants.INSTANCE.Type(),
                                                                                       comparisonTypeColumn ),
                                true );

        Column<SnapshotComparisonPageRow, String> rhsSnapshotColumn = new Column<SnapshotComparisonPageRow, String>( new TextCell() ) {
            public String getValue(SnapshotComparisonPageRow row) {
                return row.getDiff().name;
            }
        };
        // Header text is set in call-back from Repository service
        this.rhsSnapshotHeader = new SortableHeader<SnapshotComparisonPageRow, String>(
                                                                                        sortableHeaderGroup,
                                                                                        "",
                                                                                        rhsSnapshotColumn );
        columnPicker.addColumn( rhsSnapshotColumn,
                                this.rhsSnapshotHeader,
                                true );

    }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("openSelectedButton")
    void openSelected(ClickEvent e) {
        Set<SnapshotComparisonPageRow> selectedSet = selectionModel.getSelectedSet();
        for ( SnapshotComparisonPageRow selected : selectedSet ) {
            clientFactory.getPlaceManager().goTo( new AssetEditorPlace( selected.getDiff().rightUuid ));
        }
    }

    @UiHandler("refreshButton")
    void refresh(ClickEvent e) {
        refresh();
    }

}
