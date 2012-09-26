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

package org.drools.guvnor.client.explorer.navigation.admin.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.LogPageRow;
import org.drools.guvnor.client.widgets.tables.LogPagedTable;

/**
 * View for Event Log
 */
public class EventLogViewImpl extends Composite
        implements
        EventLogPresenter.EventLogView {

    private VerticalPanel layout;
    private LogPagedTable table;

    public EventLogViewImpl() {

        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel header = new VerticalPanel();
        ConstantsCore constants = ((ConstantsCore) GWT.create(ConstantsCore.class));
        Label caption = new Label(constants.ShowRecentLogTip());
        caption.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        header.add(caption);

        pf.addHeader(GuvnorImages.INSTANCE.EventLog(),
                header);

        layout = new VerticalPanel();
        layout.setHeight("100%");
        layout.setWidth("100%");

        pf.startSection();
        pf.addRow(layout);
        pf.endSection();

        setupWidget();
        initWidget(pf);
    }

    private void setupWidget() {
        this.table = new LogPagedTable();
        layout.add(table);
    }

    public HasClickHandlers getClearEventLogButton() {
        return this.table.getClearEventLogButton();
    }

    public HasClickHandlers getRefreshEventLogButton() {
        return this.table.getRefreshEventLogButton();
    }

    public void setDataProvider(AsyncDataProvider<LogPageRow> provider) {
        this.table.setDataProvider(provider);
    }

    public void refresh() {
        this.table.refresh();
    }

    public void showClearingLogMessage() {
        this.table.showClearingLogMessage();
    }

    public void hideClearingLogMessage() {
        this.table.hideClearingLogMessage();
    }

    public int getStartRowIndex() {
        return this.table.getStartRowIndex();
    }

    public int getPageSize() {
        return this.table.getPageSize();
    }

}
