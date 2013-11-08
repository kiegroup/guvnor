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

package org.guvnor.inbox.client.editor;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import org.guvnor.inbox.client.InboxPresenter;
import org.guvnor.inbox.client.resources.i18n.InboxConstants;
import org.guvnor.inbox.model.InboxPageRow;
import org.guvnor.inbox.service.InboxService;
import org.jboss.errai.common.client.api.Caller;

/**
 * Widget with a table of inbox
 */
public class InboxEditor
        extends Composite {

    // UI
    interface InboxPagedTableBinder
            extends
            UiBinder<Widget, InboxEditor> {

    }

    @UiField()
    protected Button refreshButton;

    @UiField(provided = true)
    public InboxPagedTable inboxPagedTable;

    private static InboxPagedTableBinder uiBinder = GWT.create( InboxPagedTableBinder.class );

    protected MultiSelectionModel<InboxPageRow> selectionModel;

    public InboxEditor( final String inboxName,
                        final Caller<InboxService> inboxService,
                        final InboxPresenter presenter ) {
        inboxPagedTable = new InboxPagedTable( inboxService, inboxName );

        Column<InboxPageRow, String> openColumn = new Column<InboxPageRow, String>( new ButtonCell() ) {
            public String getValue( final InboxPageRow row ) {
                return "Open";
            }
        };

        openColumn.setFieldUpdater( new FieldUpdater<InboxPageRow, String>() {
            public void update( final int index,
                                final InboxPageRow row,
                                final String value ) {
                presenter.open( row );
            }
        } );

        inboxPagedTable.addColumn( openColumn,
                                   new TextHeader( InboxConstants.INSTANCE.open() ) );

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @UiHandler("refreshButton")
    void refresh( ClickEvent e ) {
        inboxPagedTable.refresh();
    }

}
