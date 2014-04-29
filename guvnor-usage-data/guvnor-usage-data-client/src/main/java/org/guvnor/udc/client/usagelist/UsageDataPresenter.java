/*
 * Copyright 2013 JBoss Inc
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

package org.guvnor.udc.client.usagelist;

import java.util.List;
import java.util.Queue;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.udc.client.event.EventsUsageData;
import org.guvnor.udc.client.event.LevelsUsageEvent;
import org.guvnor.udc.client.event.StatusUsageEvent;
import org.guvnor.udc.client.i8n.Constants;
import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.UsageEventSummary;
import org.guvnor.udc.service.UDCServiceEntryPoint;
import org.guvnor.udc.service.UsageEventSummaryBuilder;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

@Dependent
@WorkbenchScreen(identifier = "Usage Data Collector")
public class UsageDataPresenter {

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private UsageDataView view;

    @Inject
    private Caller<UDCServiceEntryPoint> usageDataService;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @WorkbenchPartView
    public UberView<UsageDataPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.List_Usage_Data();
    }

    private List<UsageEventSummary> allUsageEventSummaries;

    private ListDataProvider<UsageEventSummary> dataProvider = new ListDataProvider<UsageEventSummary>();

    public interface UsageDataView extends UberView<UsageDataPresenter> {

        void displayNotification(String text);

        MultiSelectionModel<UsageEventSummary> getSelectionModel();

        TextBox getSearchBox();

        void refreshUsageDataCollector();

        void clearUsageData();

        void showInfoUsageData();
        
        void showInfoGFS();

        void exportCsvEvents();

        ListBox getEventTypesList();
    }

    public List<UsageEventSummary> getAllEventsSummaries() {
        return allUsageEventSummaries;
    }

    public void refreshUsageDataCollector() {
        usageDataService.call(new RemoteCallback<Queue<UsageEventSummary>>() {
            @Override
            public void callback(Queue<UsageEventSummary> events) {
                if (events != null) {
                    allUsageEventSummaries = Lists.newArrayList(events);
                }
                searchEvents(view.getSearchBox().getText());
            }
        }).readEventsByFilter(EventTypes.valueOf(view.getEventTypesList().getValue()), identity.getName());
    }

    public void clearUsageData() {
        if (allUsageEventSummaries != null && !allUsageEventSummaries.isEmpty()) {
            usageDataService.call(new RemoteCallback<Queue<UsageEventSummary>>() {
                @Override
                public void callback(Queue<UsageEventSummary> events) {
                    allUsageEventSummaries = Lists.newArrayList();
                    searchEvents(view.getSearchBox().getText());
                    view.displayNotification(constants.Clear_Msj());
                }
            }).removeEventsByFilter(EventTypes.valueOf(view.getEventTypesList().getValue()), identity.getName());
        }
    }

    public void searchEvents(String text) {
        if (text.equals("")) {
            if (allUsageEventSummaries != null) {
                dataProvider.getList().clear();
                dataProvider.setList(Lists.newArrayList(allUsageEventSummaries));
                dataProvider.refresh();
            }
        } else {
            if (allUsageEventSummaries != null) {
                List<UsageEventSummary> tasks = Lists.newArrayList(allUsageEventSummaries);
                List<UsageEventSummary> filteredTasksSimple = Lists.newArrayList();
                for (UsageEventSummary ts : tasks) {
                    if (ts.getComponent() == null || ts.getDescription() == null)
                        continue;
                    if (ts.getComponent().toLowerCase().contains(text.toLowerCase())
                            || ts.getDescription().toLowerCase().contains(text.toLowerCase())) {
                        filteredTasksSimple.add(ts);
                    }
                }
                dataProvider.getList().clear();
                dataProvider.setList(filteredTasksSimple);
                dataProvider.refresh();
                auditEvent("Search", "Search words: " + text, identity.getName(), EventsUsageData.UDC_SEARCH,
                        StatusUsageEvent.SUCCESS, LevelsUsageEvent.INFO);
            }
        }

    }

    public void filterEventsByType(EventTypes eventType) {
        usageDataService.call(new RemoteCallback<Queue<UsageEventSummary>>() {
            @Override
            public void callback(Queue<UsageEventSummary> events) {
                if (events != null) {
                    allUsageEventSummaries = Lists.newArrayList(events);
                }
                searchEvents(view.getSearchBox().getText());
            }
        }).readEventsByFilter(eventType, identity.getName());
    }

    public void addDataDisplay(HasData<UsageEventSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public void exportToCsv() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Export Usage Data");
        placeRequestImpl.addParameter("type", view.getEventTypesList().getValue());
        placeManager.goTo(placeRequestImpl);
    }

    public void showInfoUsageData() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Info Usage Data");
        placeManager.goTo(placeRequestImpl);
    }
    
    public void showInfoGFS() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Info GFS");
        placeManager.goTo(placeRequestImpl);
    }
    

    /**
     * Invoke Usage Data Collector
     */
    public void auditEvent(String key, String description, String user, EventsUsageData usageEvent, StatusUsageEvent status,
            LevelsUsageEvent level) {
        usageDataService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {

            }
        }).auditEventUDC(new UsageEventSummaryBuilder()
                		.key(key)
                		.description(description)
                		.from(user)
                		.component(usageEvent.getComponent())
                		.action(usageEvent.getAction())
                		.module(usageEvent.getModule())
                        .status(status.toString())
                        .level(level.toString())
                        .build());

    }

    /**
     * consumers could use UsageEventSummaryBuilder
     */
    public void auditEvent(UsageEventSummary usageEventSummary) {
        usageDataService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {

            }
        }).auditEventUDC(usageEventSummary);

    }

}