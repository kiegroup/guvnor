/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.inbox.client.editor;

import java.util.Date;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.guvnor.inbox.client.InboxPresenter;
import org.guvnor.inbox.client.resources.i18n.InboxConstants;
import org.guvnor.inbox.client.resources.images.ImageResources;
import org.guvnor.inbox.model.InboxPageRequest;
import org.guvnor.inbox.model.InboxPageRow;
import org.guvnor.inbox.service.InboxService;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.widgets.common.client.tables.ComparableImageResource;
import org.uberfire.ext.widgets.common.client.tables.ComparableImageResourceCell;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.paging.PageResponse;

/**
 * Widget with a table of inbox entries results.
 */
public class InboxViewImpl extends Composite implements InboxView {

    private static final int PAGE_SIZE = 10;

    interface InboxViewImplWidgetBinder
            extends
            UiBinder<Widget, InboxViewImpl> {
    }

    private InboxViewImplWidgetBinder uiBinder = GWT.create( InboxViewImplWidgetBinder.class );

    @UiField(provided = true)
    final PagedTable<InboxPageRow> table = new PagedTable<InboxPageRow>( PAGE_SIZE );

    public InboxViewImpl( final Caller<InboxService> inboxService,
                          final String inboxName,
                          final InboxPresenter presenter ) {
        Column<InboxPageRow, String> openColumn = new Column<InboxPageRow, String>( new ButtonCell() ) {
            public String getValue( final InboxPageRow row ) {
                return InboxConstants.INSTANCE.open();
            }
        };

        openColumn.setFieldUpdater( new FieldUpdater<InboxPageRow, String>() {
            public void update( final int index,
                                final InboxPageRow row,
                                final String value ) {
                presenter.open( row );
            }
        } );

        table.addColumn( openColumn,
                InboxConstants.INSTANCE.open() );

        Column<InboxPageRow, ComparableImageResource> formatColumn = new Column<InboxPageRow, ComparableImageResource>( new ComparableImageResourceCell() ) {

            public ComparableImageResource getValue( InboxPageRow row ) {
                return new ComparableImageResource( row.getFormat(),
                                                    new Image( ImageResources.INSTANCE.fileIcon() ) );
            }
        };
        table.addColumn( formatColumn,
                InboxConstants.INSTANCE.format() );

        TextColumn<InboxPageRow> noteColumn = new TextColumn<InboxPageRow>() {
            public String getValue( InboxPageRow row ) {
                return row.getNote();
            }
        };
        table.addColumn( noteColumn,
                InboxConstants.INSTANCE.name() );

        Column<InboxPageRow, Date> dateColumn = new Column<InboxPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue( InboxPageRow row ) {
                return row.getTimestamp();
            }
        };
        table.addColumn( dateColumn,
                InboxConstants.INSTANCE.createdDate() );

        table.setDataProvider( new AsyncDataProvider<InboxPageRow>() {
            protected void onRangeChanged( HasData<InboxPageRow> display ) {
                InboxPageRequest request = new InboxPageRequest( inboxName, table.dataGrid.getPageStart(), PAGE_SIZE );

                inboxService.call( new RemoteCallback<PageResponse<InboxPageRow>>() {
                    @Override
                    public void callback( final PageResponse<InboxPageRow> response ) {
                        updateRowCount( response.getTotalRowSize(),
                                        response.isTotalRowSizeExact() );
                        updateRowData( response.getStartRowIndex(),
                                       response.getPageRowList() );
                    }
                } ).loadInbox( request );

            }
        } );

        final Button refreshButton = new Button();
        refreshButton.setIcon( IconType.REFRESH );
        refreshButton.setTitle( InboxConstants.INSTANCE.refresh() );
        refreshButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                table.refresh();
            }
        } );
        table.getToolbar().add( refreshButton );

        initWidget( uiBinder.createAndBindUi( this ) );
    }

}
