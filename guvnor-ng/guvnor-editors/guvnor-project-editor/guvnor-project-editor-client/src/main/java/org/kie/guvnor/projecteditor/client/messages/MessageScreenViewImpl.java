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

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import org.kie.guvnor.projecteditor.client.resources.ProjectEditorResources;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.guvnor.projecteditor.model.builder.Message;

import javax.inject.Inject;

public class MessageScreenViewImpl
        extends Composite
        implements MessageScreenView,
        RequiresResize {

    private static Binder uiBinder = GWT.create(Binder.class);
    private Presenter presenter;

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
        dataGrid = new DataGrid<Message>(KEY_PROVIDER);
        dataGrid.setWidth("100%");

        dataGrid.setAutoHeaderRefreshDisabled(true);

        dataGrid.setEmptyTableWidget(new Label("---"));

        setUpColumns();

        messageService.addDataDisplay(dataGrid);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void onResize() {
        dataGrid.setHeight(getParent().getOffsetHeight() + "px");
    }

    private void setUpColumns() {
        addLevelColumn();
        addTextColumn();
        addColumnColumn();
        addLineColumn();
    }

    private void addLineColumn() {
        Column<Message, String> lineColumn = new Column<Message, String>(new TextCell()) {
            @Override
            public String getValue(Message message) {
                return Integer.toString(message.getLine());
            }
        };
        dataGrid.addColumn(lineColumn, ProjectEditorConstants.INSTANCE.Line());
        dataGrid.setColumnWidth(lineColumn, 60, Style.Unit.PCT);
    }

    private void addColumnColumn() {
        Column<Message, String> column = new Column<Message, String>(new TextCell()) {
            @Override
            public String getValue(Message message) {
                return Integer.toString(message.getColumn());
            }
        };
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.Column());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addTextColumn() {
        Column<Message, String> column = new Column<Message, String>(new TextCell()) {
            @Override
            public String getValue(Message message) {
                return message.getText();
            }
        };
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.Text());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addLevelColumn() {
        Column<Message, ImageResource> column = new Column<Message, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(Message message) {
                switch (message.getLevel()) {
                    case ERROR:
                        return ProjectEditorResources.INSTANCE.Error();

                    case WARNING:
                        return ProjectEditorResources.INSTANCE.Warning();
                    case INFO:
                    default:
                        return ProjectEditorResources.INSTANCE.Information();
                }
            }
        };
        dataGrid.addColumn(column, ProjectEditorConstants.INSTANCE.Level());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
