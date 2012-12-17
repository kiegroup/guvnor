/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.projecteditor.client.messages;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import org.kie.guvnor.projecteditor.model.builder.Message;

import javax.inject.Inject;

public class MessageScreenViewImpl
        extends Composite
        implements MessageScreenView {

    private static Binder uiBinder = GWT.create(Binder.class);
    private Presenter presenter;
    private final MessageService messageService;

    interface Binder extends UiBinder<Widget, MessageScreenViewImpl> {
    }

    @UiField(provided = true)
    DataGrid<Message> dataGrid;

    public static final ProvidesKey<Message> KEY_PROVIDER = new ProvidesKey<Message>() {
        @Override
        public Object getKey(Message item) {
            return item == null ? null : item.getId();
        }
    };


    @Inject
    public MessageScreenViewImpl(MessageService messageService) {
        this.messageService = messageService;
        dataGrid = new DataGrid<Message>(KEY_PROVIDER);
        dataGrid.setWidth("100%");

        dataGrid.setAutoHeaderRefreshDisabled(true);

        dataGrid.setEmptyTableWidget(new Label("EMPTY")); // TODO i18n -Rikkola-

        ColumnSortEvent.ListHandler<Message> sortHandler =
                new ColumnSortEvent.ListHandler<Message>(messageService.getDataProvider().getList());
        dataGrid.addColumnSortHandler(sortHandler);

        final SelectionModel<Message> selectionModel =
                new MultiSelectionModel<Message>(KEY_PROVIDER);
        dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager
                .<Message>createCheckboxManager());

        initTableColumns(selectionModel, sortHandler);

        messageService.addDataDisplay(dataGrid);

        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Add the columns to the table.
     */
    private void initTableColumns(final SelectionModel<Message> selectionModel,
                                  ColumnSortEvent.ListHandler<Message> sortHandler) {

        // Address.
        Column<Message, String> addressColumn = new Column<Message, String>(new TextCell()) {
            @Override
            public String getValue(Message message) {
                return message.getText();
            }
        };
        dataGrid.addColumn(addressColumn, "Text"); // TODO i18n -Rikkola-
        dataGrid.setColumnWidth(addressColumn, 60, Style.Unit.PCT);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
