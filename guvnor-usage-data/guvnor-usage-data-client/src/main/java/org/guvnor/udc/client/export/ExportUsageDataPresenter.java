/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.udc.client.export;

import java.util.List;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.udc.client.i8n.Constants;
import org.guvnor.udc.client.util.UtilUsageData;
import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.UsageEventSummary;
import org.guvnor.udc.service.UDCServiceEntryPoint;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchPopup(identifier = "Export Usage Data")
public class ExportUsageDataPresenter {
    
    public ExportUsageDataPresenter() {
    }
    
    @Inject
    ExportUsageDataEventView view;

    @Inject
    Identity identity;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    @Inject
	private Caller<UDCServiceEntryPoint> usageDataService;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Info_Usage_Data();
    }

    @WorkbenchPartView
    public UberView<ExportUsageDataPresenter> getView() {
        return view;
    }

    private Constants constants = GWT.create(Constants.class);

    private String textFormatCsv;

    private List<UsageEventSummary> allUsageEventSummaries;

    private PlaceRequest place;
    
    public interface ExportUsageDataEventView extends UberView<ExportUsageDataPresenter> {
        void displayNotification(String text);
    }

    @PostConstruct
    public void init() {
    }
    
    @OnStartup
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }
    
    @OnOpen 
    public void onReveal() {
    	formatInfoCsv(EventTypes.valueOf(place.getParameter("type", null)));
    }

    public void formatInfoCsv(EventTypes typeEvent) {
        textFormatCsv = "";
        usageDataService.call(new RemoteCallback<Queue<UsageEventSummary>>() {
            @Override
            public void callback(Queue<UsageEventSummary> events) {
                if (events != null) {
                    allUsageEventSummaries = Lists.newArrayList(events);
                    setFormatCsv();
                }
            }
        }).readEventsByFilter(typeEvent, identity.getName());
    }

    private void setFormatCsv(){
    	StringBuilder formatCsv = new StringBuilder();
    	for (UsageEventSummary usage : allUsageEventSummaries) {
            formatCsv.append(UtilUsageData.getRowFormatted(usage));
        }
    	textFormatCsv = formatCsv.toString(); 
    }

	public String getTextFormtCsv() {
		return textFormatCsv;
	}
	
	public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }

}
