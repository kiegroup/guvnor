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

package org.kie.guvnor.inbox.client.editor;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.data.tables.PageResponse;
import org.kie.guvnor.commons.ui.client.tables.AbstractPagedTable;
import org.kie.guvnor.commons.ui.client.tables.ColumnPicker;
import org.kie.guvnor.commons.ui.client.tables.ComparableImageResource;
import org.kie.guvnor.commons.ui.client.tables.ComparableImageResourceCell;
import org.kie.guvnor.commons.ui.client.tables.SelectionColumn;
import org.kie.guvnor.commons.ui.client.tables.SortableHeader;
import org.kie.guvnor.commons.ui.client.tables.SortableHeaderGroup;
import org.kie.guvnor.inbox.client.resources.images.ImageResources;
import org.kie.guvnor.inbox.model.InboxPageRequest;
import org.kie.guvnor.inbox.model.InboxPageRow;
import org.kie.guvnor.inbox.service.InboxService;

import java.util.Date;

/**
 * Widget with a table of inbox entries results.
 */
public class InboxPagedTable extends AbstractPagedTable<InboxPageRow> implements IsInboxPagedTable {

    interface Binder
            extends
            UiBinder<Widget, InboxPagedTable> {
    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private ColumnPicker<InboxPageRow> columnPicker = new ColumnPicker<InboxPageRow>(cellTable);

    private SelectionColumn<InboxPageRow> selectionColumn;
    private MultiSelectionModel<InboxPageRow> selectionModel;
    private static final int PAGE_SIZE = 10;
 
    public InboxPagedTable(final Caller<InboxService> inboxService, final String inboxName ) {
        super( PAGE_SIZE );

        setDataProvider( new AsyncDataProvider<InboxPageRow>() {
            protected void onRangeChanged( HasData<InboxPageRow> display ) {
                InboxPageRequest request = new InboxPageRequest();
                request.setInboxName( inboxName );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                
                inboxService.call(new RemoteCallback<PageResponse<InboxPageRow>>() {
                    @Override
                    public void callback(final PageResponse<InboxPageRow> response) {
                        updateRowCount(response.getTotalRowSize(),
                                response.isTotalRowSizeExact());
                        updateRowData(response.getStartRowIndex(),
                                response.getPageRowList());
                    }
                }).loadInbox(request);
                
/*                repositoryService.loadInbox( request,
                        new GenericCallback<PageResponse<InboxPageRow>>() {
                            public void onSuccess( PageResponse<InboxPageRow> response ) {
                                updateRowCount( response.getTotalRowSize(),
                                        response.isTotalRowSizeExact() );
                                updateRowData( response.getStartRowIndex(),
                                        response.getPageRowList() );
                            }
                        } );*/
            }
        } );
    }

    @Override
    protected void addAncillaryColumns( ColumnPicker<InboxPageRow> columnPicker,
                                        SortableHeaderGroup<InboxPageRow> sortableHeaderGroup ) {

        Column<InboxPageRow, ComparableImageResource> formatColumn = new Column<InboxPageRow, ComparableImageResource>( new ComparableImageResourceCell() ) {

            public ComparableImageResource getValue( InboxPageRow row ) {
                //TODO: get icons for different asset format
                //AssetEditorFactory factory = clientFactory.getAssetEditorFactory();                
                //return new ComparableImageResource( row.getFormat(), factory.getAssetEditorIcon( row.getFormat() ) );
                return new ComparableImageResource( row.getFormat(), new Image( ImageResources.INSTANCE.fileIcon() ) );
            }
        };
        columnPicker.addColumn( formatColumn,
                new SortableHeader<InboxPageRow, ComparableImageResource>(
                        sortableHeaderGroup,
                        "Format",
                        formatColumn ),
                true );

        TextColumn<InboxPageRow> noteColumn = new TextColumn<InboxPageRow>() {
            public String getValue( InboxPageRow row ) {
                return row.getNote();
            }
        };
        columnPicker.addColumn( noteColumn,
                new SortableHeader<InboxPageRow, String>(
                        sortableHeaderGroup,
                        "Name",
                        noteColumn ),
                true );

        Column<InboxPageRow, Date> dateColumn = new Column<InboxPageRow, Date>( new
                DateCell(
                DateTimeFormat.getFormat(
                        DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue( InboxPageRow row ) {
                return row.getTimestamp();
            }
        };
        columnPicker.addColumn( dateColumn,
                new SortableHeader<InboxPageRow, Date>(
                        sortableHeaderGroup,
                        "Created Date",
                        dateColumn ),
                true );

    }

    public void addColumn(Column<InboxPageRow, String> column, TextHeader textHeader) {
        columnPicker.addColumn(column,
                textHeader,
                true);
    }
    
    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi(this);
    }
}
