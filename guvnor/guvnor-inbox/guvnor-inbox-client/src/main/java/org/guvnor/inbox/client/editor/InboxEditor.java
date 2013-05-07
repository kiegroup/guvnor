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
import org.jboss.errai.ioc.client.api.Caller;
import org.guvnor.inbox.model.InboxPageRow;
import org.guvnor.inbox.service.InboxService;

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
    protected Button deleteSelectedButton;

    @UiField()
    protected Button refreshButton;

    @UiField()
    protected Button openSelectedButton;

    @UiField(provided = true)
    public InboxPagedTable inboxPagedTable;

    private static InboxPagedTableBinder uiBinder = GWT.create(InboxPagedTableBinder.class);

    protected MultiSelectionModel<InboxPageRow> selectionModel;

    //private Caller<M2RepoService> m2RepoService;


    public InboxEditor(Caller<InboxService> inboxService) {
        this(inboxService, null);

    }

    public InboxEditor(Caller<InboxService> inboxService, final String inboxName) {
        //this.m2RepoService = repoService;
        inboxPagedTable = new InboxPagedTable(inboxService, inboxName);


        Column<InboxPageRow, String> openColumn = new Column<InboxPageRow, String>(new ButtonCell()) {
            public String getValue(InboxPageRow row) {
                return "Open";
            }
        };

        openColumn.setFieldUpdater(new FieldUpdater<InboxPageRow, String>() {
            public void update(int index,
                               InboxPageRow row,
                               String value) {
/*                Window.open(getFileDownloadURL(row.getPath()),
                        "downloading",
                        "resizable=no,scrollbars=yes,status=no");*/
            }
        });

        inboxPagedTable.addColumn(openColumn, new TextHeader("Open"));

        initWidget(uiBinder.createAndBindUi(this));
    }


    @UiHandler("deleteSelectedButton")
    void deleteSelected(ClickEvent e) {
/*        if (getSelectedJars() == null) {
            Window.alert("Please Select A Jar To Delete");
            return;
        }
        if (!Window.confirm("AreYouSureYouWantToDeleteTheseItems")) {
            return;
        }
        m2RepoService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                Window.alert("Deleted successfully");
                pagedJarTable.refresh();
            }
        }).deleteJar(getSelectedJars());*/
    }

    public String[] getSelected() {
/*        Set<InboxPageRow> selectedRows = selectionModel.getSelectedSet();

        // Compatibility with existing API
        if (selectedRows.size() == 0) {
            return null;
        }

        // Create the array of paths
        String[] paths = new String[selectedRows.size()];
        int rowCount = 0;
        for (InboxPageRow row : selectedRows) {
            paths[rowCount++] = row.getPath();
        }
        return paths;*/
        return null;
    }

    @UiHandler("refreshButton")
    void refresh(ClickEvent e) {
        inboxPagedTable.refresh();
    }

    @UiHandler("openSelectedButton")
    void openSelected(ClickEvent e) {

    }
}
