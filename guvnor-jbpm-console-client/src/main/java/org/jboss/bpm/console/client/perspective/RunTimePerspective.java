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

package org.jboss.bpm.console.client.perspective;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.drools.guvnor.client.perspective.Perspective;
import org.jboss.bpm.console.client.navigation.processes.ProcessNavigationViewFactory;
import org.jboss.bpm.console.client.navigation.processes.ProcessNavigationViewFactoryImpl;
import org.jboss.bpm.console.client.navigation.processes.ProcessesNavigationItemBuilder;
import org.jboss.bpm.console.client.navigation.reporting.ReportingNavigationItemBuilder;
import org.jboss.bpm.console.client.navigation.settings.SettingsNavigationItemBuilder;
import org.jboss.bpm.console.client.navigation.tasks.TasksNavigationItemBuilder;

public class RunTimePerspective extends Perspective {

    private ProcessNavigationViewFactory navigationViewFactory;

    @Override
    public Collection<NavigationItemBuilder> getBuilders(
            ClientFactory clientFactory,
            EventBus eventBus) {
        initNavigationViewFactory();

        Collection<NavigationItemBuilder> builders = new ArrayList<NavigationItemBuilder>();

        builders.add(new TasksNavigationItemBuilder(navigationViewFactory, clientFactory.getPlaceController()));
        builders.add(new ProcessesNavigationItemBuilder(navigationViewFactory, clientFactory.getPlaceController()));
        builders.add(new ReportingNavigationItemBuilder(navigationViewFactory, clientFactory.getPlaceController()));
        builders.add(new SettingsNavigationItemBuilder(navigationViewFactory, clientFactory.getPlaceController()));

        return builders;
    }

    private void initNavigationViewFactory() {
        if (navigationViewFactory == null) {
            navigationViewFactory = new ProcessNavigationViewFactoryImpl();
        }
    }
}
