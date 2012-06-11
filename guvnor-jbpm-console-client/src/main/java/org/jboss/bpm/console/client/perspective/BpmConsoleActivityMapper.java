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

package org.jboss.bpm.console.client.perspective;

import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.explorer.GuvnorActivityMapper;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.ActivityMapper;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;
import org.jboss.bpm.console.client.navigation.processes.ExecutionHistoryActivity;
import org.jboss.bpm.console.client.navigation.processes.ExecutionHistoryPlace;
import org.jboss.bpm.console.client.navigation.processes.ProcessOverviewActivity;
import org.jboss.bpm.console.client.navigation.processes.ProcessOverviewPlace;
import org.jboss.bpm.console.client.navigation.reporting.ReportTemplatesActivity;
import org.jboss.bpm.console.client.navigation.reporting.ReportTemplatesPlace;
import org.jboss.bpm.console.client.navigation.settings.PreferencesActivity;
import org.jboss.bpm.console.client.navigation.settings.PreferencesPlace;
import org.jboss.bpm.console.client.navigation.settings.SystemActivity;
import org.jboss.bpm.console.client.navigation.settings.SystemPlace;
import org.jboss.bpm.console.client.navigation.tasks.GroupTasksActivity;
import org.jboss.bpm.console.client.navigation.tasks.GroupTasksPlace;
import org.jboss.bpm.console.client.navigation.tasks.PersonalTasksActivity;
import org.jboss.bpm.console.client.navigation.tasks.PersonalTasksPlace;

public class BpmConsoleActivityMapper
        implements ActivityMapper {

    private final BpmConsoleClientFactory clientFactory;
    private final GuvnorActivityMapper guvnorActivityMapper;


    public BpmConsoleActivityMapper(RuntimeClientFactory clientFactory) {
        guvnorActivityMapper = new GuvnorActivityMapper(clientFactory);
        this.clientFactory = clientFactory;
    }

    public Activity getActivity(Place place) {
        if (place instanceof PersonalTasksPlace) {
            return new PersonalTasksActivity(clientFactory);
        } else if (place instanceof GroupTasksPlace) {
            return new GroupTasksActivity(clientFactory);
        } else if (place instanceof ReportTemplatesPlace) {
            return new ReportTemplatesActivity(clientFactory);
        } else if (place instanceof PreferencesPlace) {
            return new PreferencesActivity(clientFactory.getController());
        } else if (place instanceof ProcessOverviewPlace) {
            return new ProcessOverviewActivity(clientFactory);
        } else if (place instanceof ExecutionHistoryPlace) {
            return new ExecutionHistoryActivity(clientFactory.getController());
        } else if (place instanceof SystemPlace) {
            return new SystemActivity(clientFactory);
        } else {
            return tryGuvnorCore(place);
        }
    }

    private Activity tryGuvnorCore(Place place) {
        return guvnorActivityMapper.getActivity(place);
    }
}
