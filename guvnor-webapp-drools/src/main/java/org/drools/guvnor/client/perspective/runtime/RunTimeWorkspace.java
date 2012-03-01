/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.perspective.runtime;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessesNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksNavigationItemBuilder;
import org.drools.guvnor.client.perspective.Workspace;

public class RunTimeWorkspace extends Workspace {

    @Override
    public Collection<NavigationItemBuilder> getBuilders(ClientFactory clientFactory, EventBus eventBus) {
        Collection<NavigationItemBuilder> builders = new ArrayList<NavigationItemBuilder>();

        builders.add(new TasksNavigationItemBuilder(clientFactory.getNavigationViewFactory(), clientFactory.getPlaceController()));
        builders.add(new ProcessesNavigationItemBuilder(clientFactory.getNavigationViewFactory(), clientFactory.getPlaceController()));
        builders.add(new ReportingNavigationItemBuilder(clientFactory.getNavigationViewFactory(), clientFactory.getPlaceController()));
        builders.add(new SettingsNavigationItemBuilder(clientFactory.getNavigationViewFactory(), clientFactory.getPlaceController()));

        return builders;
    }
}
