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

import java.util.Date;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.resources.ComparableImage;
import org.drools.guvnor.client.rpc.InboxPageRequest;
import org.drools.guvnor.client.rpc.InboxPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.widgets.tables.sorting.AbstractSortableHeaderGroup;
import org.drools.guvnor.client.widgets.tables.sorting.SimpleSortableHeader;
import org.drools.guvnor.client.widgets.tables.sorting.SimpleSortableHeaderGroup;

/**
 * Widget with a table of inbox entries results.
 */
public class InboxPagedTable extends AbstractAssetPagedTable<InboxPageRow> implements IsInboxPagedTable {

    private static final int PAGE_SIZE = 10;
    private final ClientFactory clientFactory;

    public InboxPagedTable( final String inboxName,
                            ClientFactory clientFactory ) {
        super( PAGE_SIZE,
               clientFactory );
        this.clientFactory = clientFactory;

        setDataProvider( new AsyncDataProvider<InboxPageRow>() {
            protected void onRangeChanged( HasData<InboxPageRow> display ) {
                InboxPageRequest request = new InboxPageRequest();
                request.setInboxName( inboxName );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                repositoryService.loadInbox( request,
                                             new GenericCallback<PageResponse<InboxPageRow>>() {
                                                 public void onSuccess( PageResponse<InboxPageRow> response ) {
                                                     updateRowCount( response.getTotalRowSize(),
                                                                     response.isTotalRowSizeExact() );
                                                     updateRowData( response.getStartRowIndex(),
                                                                    response.getPageRowList() );
                                                 }
                                             } );
            }
        } );
    }

    /**
     * Set up table and common columns. Additional columns can be appended
     * between the "checkbox" and "open" columns by overriding
     * <code>addAncillaryColumns()</code>
     */
    @Override
    protected void doCellTable() {

        ProvidesKey<InboxPageRow> providesKey = new ProvidesKey<InboxPageRow>() {
            public Object getKey( InboxPageRow row ) {
                return row.getUuid();
            }
        };

        cellTable = new CellTable<InboxPageRow>( providesKey );
        selectionModel = new MultiSelectionModel<InboxPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<InboxPageRow> columnPicker = new ColumnPicker<InboxPageRow>( cellTable );
        SimpleSortableHeaderGroup<InboxPageRow> sortableHeaderGroup = new SimpleSortableHeaderGroup<InboxPageRow>( cellTable );

        final TextColumn<InboxPageRow> uuidNumberColumn = new TextColumn<InboxPageRow>() {
            public String getValue( InboxPageRow row ) {
                return row.getUuid();
            }
        };
        columnPicker.addColumn( uuidNumberColumn,
                                new SimpleSortableHeader<InboxPageRow, String>( sortableHeaderGroup,
                                                                                constants.uuid(),
                                                                                uuidNumberColumn ),
                                false );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        // Add "Open" button column
        Column<InboxPageRow, String> openColumn = new Column<InboxPageRow, String>( new ButtonCell() ) {
            public String getValue( InboxPageRow row ) {
                return constants.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<InboxPageRow, String>() {
            public void update( int index,
                                InboxPageRow row,
                                String value ) {
                clientFactory.getPlaceController().goTo( new AssetEditorPlace( row.getUuid() ) );
            }
        } );
        columnPicker.addColumn( openColumn,
                                new TextHeader( constants.Open() ),
                                true );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    @Override
    protected void addAncillaryColumns( ColumnPicker<InboxPageRow> columnPicker,
                                        AbstractSortableHeaderGroup<InboxPageRow> sortableHeaderGroup ) {

        Column<InboxPageRow, ComparableImage> formatColumn = new Column<InboxPageRow, ComparableImage>( new ComparableImageCell() ) {

            public ComparableImage getValue( InboxPageRow row ) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new ComparableImage( row.getFormat(), factory.getAssetEditorIcon( row.getFormat() ) );
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SimpleSortableHeader<InboxPageRow, ComparableImage>( sortableHeaderGroup,
                                                                                         constants.Format(),
                                                                                         formatColumn ),
                                true );

        TextColumn<InboxPageRow> noteColumn = new TextColumn<InboxPageRow>() {
            public String getValue( InboxPageRow row ) {
                return row.getNote();
            }
        };
        columnPicker.addColumn( noteColumn,
                                new SimpleSortableHeader<InboxPageRow, String>( sortableHeaderGroup,
                                                                                constants.Name(),
                                                                                noteColumn ),
                                true );

        Column<InboxPageRow, Date> dateColumn = new Column<InboxPageRow, Date>( new DateCell( DateTimeFormat.getFormat(
                DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue( InboxPageRow row ) {
                return row.getTimestamp();
            }
        };
        columnPicker.addColumn( dateColumn,
                                new SimpleSortableHeader<InboxPageRow, Date>( sortableHeaderGroup,
                                                                              constants.CreatedDate(),
                                                                              dateColumn ),
                                false );

    }

}
