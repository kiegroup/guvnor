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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.perspective.ChangePerspectiveEvent;

public class NavigationPanel implements ChangePerspectiveEvent.Handler, IsWidget {

    private final NavigationPanelView view;
    private final ClientFactory clientFactory;
    private final ResettableEventBus eventBus;

    public NavigationPanel(ClientFactory clientFactory, EventBus eventBus) {
        view = clientFactory.getNavigationViewFactory().getNavigationPanelView();
        eventBus.addHandler(ChangePerspectiveEvent.TYPE, this);
        this.eventBus = new ResettableEventBus(eventBus);
        this.clientFactory = clientFactory;
    }

    public void add(IsWidget header, IsWidget content) {
        view.add(header, content);
    }

    public void onChangePerspective(ChangePerspectiveEvent changePerspectiveEvent) {
        view.clear();
        eventBus.removeHandlers();

        addNavigationItems(changePerspectiveEvent);
    }

    private void addNavigationItems(ChangePerspectiveEvent changePerspectiveEvent) {
        for (NavigationItemBuilder navigationItemBuilder : changePerspectiveEvent.getWorkspace().getBuilders(clientFactory, eventBus)) {
            addNavigationItem(navigationItemBuilder);
        }
    }

    private void addNavigationItem(NavigationItemBuilder navigationItemBuilder) {
        if (navigationItemBuilder.hasPermissionToBuild()) {
            view.add(navigationItemBuilder.getHeader(), navigationItemBuilder.getContent());
        }
    }

    public Widget asWidget() {
        return view.asWidget();
    }
}
