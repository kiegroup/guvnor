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

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.GuvnorEventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.perspective.ChangePerspectiveEvent;
import org.drools.guvnor.client.perspective.Perspective;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "navigationPanel")
public class NavigationPanel implements ChangePerspectiveEvent.Handler, IsWidget {

    private final NavigationPanelView view;
    private final ClientFactory clientFactory;
    private final ResettableEventBus eventBus;

    @Inject
    public NavigationPanel(ClientFactory clientFactory, GuvnorEventBus eventBus, Perspective perspective) {
        view = clientFactory.getNavigationViewFactory().getNavigationPanelView();
        eventBus.addHandler(ChangePerspectiveEvent.TYPE, this);
        this.eventBus = new ResettableEventBus(eventBus);
        this.clientFactory = clientFactory;

        addNavigationItems(perspective);
    }

    public void add(IsWidget header, IsWidget content) {
        view.add(header, content);
    }

    public void onChangePerspective(ChangePerspectiveEvent changePerspectiveEvent) {
        view.clear();
        eventBus.removeHandlers();

        addNavigationItems(changePerspectiveEvent.getPerspective());
    }

    private void addNavigationItems(Perspective perspective) {
        for (NavigationItemBuilder navigationItemBuilder : perspective.getBuilders(clientFactory, eventBus)) {
            addNavigationItem(navigationItemBuilder);
        }
    }

    private void addNavigationItem(NavigationItemBuilder navigationItemBuilder) {
        if (navigationItemBuilder.hasPermissionToBuild()) {
            view.add(navigationItemBuilder.getHeader(), navigationItemBuilder.getContent());
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Navigation"; //TODO -Rikkola-
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }
}
