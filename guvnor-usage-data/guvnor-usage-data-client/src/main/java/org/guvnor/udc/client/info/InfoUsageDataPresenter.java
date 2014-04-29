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

package org.guvnor.udc.client.info;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.udc.client.i8n.Constants;
import org.guvnor.udc.client.util.UtilUsageData;
import org.guvnor.udc.model.InfoUsageDataSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

@Dependent
@WorkbenchPopup(identifier = "Info Usage Data")
public class InfoUsageDataPresenter {

    public InfoUsageDataPresenter() {
    }
    
    private Constants constants = GWT.create(Constants.class);
    
    @Inject
    InfoUsageDataEventView view;

    @Inject
    Identity identity;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Info_Usage_Data();
    }

    @WorkbenchPartView
    public UberView<InfoUsageDataPresenter> getView() {
        return view;
    }

    public interface InfoUsageDataEventView extends UberView<InfoUsageDataPresenter> {

        void displayNotification(String text);

        void refreshGridsInfo();

    }

    private PlaceRequest place;
    
    private ListDataProvider<InfoUsageDataSummary> moduleProvider = new ListDataProvider<InfoUsageDataSummary>();
    
    private ListDataProvider<InfoUsageDataSummary> infoProvider = new ListDataProvider<InfoUsageDataSummary>();
    
    private List<InfoUsageDataSummary> allInfo;
    
    private List<InfoUsageDataSummary> allModules;

    @PostConstruct
    public void init() {
    }

    public void refreshModules(){
        List<InfoUsageDataSummary> listModulesAudited = Lists.newArrayList();
        Map<String, Set<String>> componentsAudited = UtilUsageData.getAllComponentByModule();
        for (Map.Entry<String, Set<String>> entry : componentsAudited.entrySet()) {
            InfoUsageDataSummary info = new InfoUsageDataSummary();
            info.setComponent(entry.getKey());
            info.setDescription(UtilUsageData.getComponentFormated(entry.getValue()));
            listModulesAudited.add(info);
        }
        allModules = listModulesAudited;
        if (allModules != null) {
        	moduleProvider.getList().clear();
        	moduleProvider.setList(Lists.newArrayList(allModules));
        	moduleProvider.refresh();
        }
    }
    
    public void refreshInfo(){
    	infoProvider.getList().clear();
    	infoProvider.setList(Lists.newArrayList(UtilUsageData.getComponentsAudited()));
    	infoProvider.refresh();
    }
    
    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
    
    public void addModuleDataDisplay(HasData<InfoUsageDataSummary> display) {
    	moduleProvider.addDataDisplay(display);
    }
    
    public void addInfoDataDisplay(HasData<InfoUsageDataSummary> display) {
    	infoProvider.addDataDisplay(display);
    }

    public List<InfoUsageDataSummary> getAllModules() {
        return allModules;
    }

	public List<InfoUsageDataSummary> getAllInfo() {
		return allInfo;
	}
   
}
