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

package org.drools.guvnor.client.perspective.runtime;

import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.explorer.navigation.processes.ExecutionHistoryActivity;
import org.drools.guvnor.client.explorer.navigation.processes.ExecutionHistoryPlace;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewActivity;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesActivity;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace;
import org.drools.guvnor.client.explorer.navigation.settings.PreferencesActivity;
import org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace;
import org.drools.guvnor.client.explorer.navigation.settings.SystemActivity;
import org.drools.guvnor.client.explorer.navigation.settings.SystemPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksActivity;
import org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksActivity;
import org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.ActivityMapper;

public class BpmConsoleActivityMapper
        implements
        ActivityMapper {

//    private final ClientFactory clientFactory = new RuntimeClientFactory();

    public Activity getActivity(Place place) {
//        if (place instanceof PersonalTasksPlace) {
//            return new PersonalTasksActivity(clientFactory);
//        } else if (place instanceof GroupTasksPlace) {
//            return new GroupTasksActivity(clientFactory);
//        } else if (place instanceof ReportTemplatesPlace) {
//            return new ReportTemplatesActivity(clientFactory);
//        } else if (place instanceof PreferencesPlace) {
//            return new PreferencesActivity(clientFactory.getController());
//        } else if (place instanceof ProcessOverviewPlace) {
//            return new ProcessOverviewActivity(clientFactory);
//        } else if (place instanceof ExecutionHistoryPlace) {
//            return new ExecutionHistoryActivity(clientFactory.getController());
//        } else if (place instanceof SystemPlace) {
//            return new SystemActivity(clientFactory);
//        } else {
            return null;
//        }
    }
}
