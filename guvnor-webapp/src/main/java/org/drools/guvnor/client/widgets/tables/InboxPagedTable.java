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

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.resources.RuleFormatImageResource;
import org.drools.guvnor.client.rpc.InboxPageRequest;
import org.drools.guvnor.client.rpc.InboxPageRow;
import org.drools.guvnor.client.rpc.PageResponse;

import java.util.Date;

/**
 * Widget with a table of inbox entries results.
 */
public class InboxPagedTable extends AbstractAssetPagedTable<InboxPageRow> implements IsInboxPagedTable {

    private static final int PAGE_SIZE = 10;
    private final ClientFactory clientFactory;

    public InboxPagedTable( final String inboxName,
                            ClientFactory clientFactory ) {
        super( PAGE_SIZE,
                clientFactory);
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

    @Override
    protected void addAncillaryColumns( ColumnPicker<InboxPageRow> columnPicker,
                                        SortableHeaderGroup<InboxPageRow> sortableHeaderGroup ) {

        Column<InboxPageRow, RuleFormatImageResource> formatColumn = new Column<InboxPageRow, RuleFormatImageResource>( new RuleFormatImageResourceCell() ) {

            public RuleFormatImageResource getValue( InboxPageRow row ) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new RuleFormatImageResource( row.getFormat(), factory.getAssetEditorIcon( row.getFormat() ) );
            }
        };
        columnPicker.addColumn( formatColumn,
                new SortableHeader<InboxPageRow, RuleFormatImageResource>(
                        sortableHeaderGroup,
                        constants.Format(),
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
                        constants.Name(),
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
                        constants.CreatedDate(),
                        dateColumn ),
                false );

    }

}
