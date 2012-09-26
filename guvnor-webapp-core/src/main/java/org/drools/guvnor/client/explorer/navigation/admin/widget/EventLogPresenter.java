/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.explorer.navigation.admin.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.*;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * Presenter for Event Log
 */
@Dependent
@WorkbenchScreen(identifier = "eventLogManager")
public class EventLogPresenter {

    public interface EventLogView
            extends
            IsWidget {

        HasClickHandlers getClearEventLogButton();

        HasClickHandlers getRefreshEventLogButton();

        void setDataProvider(AsyncDataProvider<LogPageRow> provider);

        void refresh();

        void showClearingLogMessage();

        void hideClearingLogMessage();

        int getStartRowIndex();

        int getPageSize();

    }

    private final EventLogView view;

    protected RepositoryServiceAsync repositoryService;

    @Inject
    public EventLogPresenter(ClientFactory clientFactory,
                             EventLogViewImpl view) {
        this.repositoryService = clientFactory.getRepositoryService();
        this.view = view;
        bind();
        setDataProvider();
    }

    public void bind() {
        view.getClearEventLogButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doClearEventLog();
            }
        });

        view.getRefreshEventLogButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doRefreshEventLog();
            }
        });

    }

    private void doClearEventLog() {
        view.showClearingLogMessage();
        RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);
        repositoryService.cleanLog(new GenericCallback<java.lang.Void>() {
            public void onSuccess(Void v) {
                view.refresh();
                view.hideClearingLogMessage();
            }
        });
    }

    private void doRefreshEventLog() {
        view.refresh();
    }

    private void setDataProvider() {
        view.setDataProvider(new AsyncDataProvider<LogPageRow>() {
            protected void onRangeChanged(HasData<LogPageRow> table) {
                PageRequest request = new PageRequest();
                request.setStartRowIndex(view.getStartRowIndex());
                request.setPageSize(view.getPageSize());
                repositoryService.showLog(request,
                        new GenericCallback<PageResponse<LogPageRow>>() {
                            public void onSuccess(PageResponse<LogPageRow> response) {
                                updateRowCount(response.getTotalRowSize(),
                                        response.isTotalRowSizeExact());
                                updateRowData(response.getStartRowIndex(),
                                        response.getPageRowList());
                            }
                        });
            }
        });
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ConstantsCore.INSTANCE.EventLog();
    }
}
