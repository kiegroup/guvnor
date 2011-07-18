/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client;

import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.*;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;

import java.util.Collection;

public class AppController {

    private final ClientFactory clientFactory;

    private final PerspectivesPanel perspectivesPanel;

    public AppController( ClientFactory clientFactory ) {
        this.clientFactory = clientFactory;

        perspectivesPanel = createPerspectivesPanel();
        loadPerspectives();
        setUpActivityMapper();
        setUpHistoryMapper();
    }

    private void setUpHistoryMapper() {
        PlaceHistoryHandler historyHandler = clientFactory.getPlaceHistoryHandler();
        historyHandler.register(
                clientFactory.getPlaceController(),
                clientFactory.getEventBus(),
                new FindPlace() );

        historyHandler.handleCurrentHistory();
    }

    private PerspectivesPanel createPerspectivesPanel() {
        return new PerspectivesPanel(
                clientFactory.getPerspectivesPanelView(),
                clientFactory.getPlaceController() );
    }

    private void setUpActivityMapper() {
        MultiActivityManager activityManager = clientFactory.getActivityManager();
        activityManager.setTabbedPanel( perspectivesPanel.getTabbedPanel() );
    }

    public IsWidget getMainPanel() {
        return perspectivesPanel.getView();
    }

    private void loadPerspectives() {
        ConfigurationServiceAsync configurationServiceAsync = clientFactory.getConfigurationService();

        PerspectiveLoader perspectiveLoader = new PerspectiveLoader( configurationServiceAsync );
        perspectiveLoader.loadPerspectives( new LoadPerspectives() {
            public void loadPerspectives( Collection<Perspective> perspectives ) {
                for (Perspective perspective : perspectives) {
                    perspectivesPanel.addPerspective( perspective );
                }
            }
        } );
    }

    public void setUserName( String userName ) {
        perspectivesPanel.setUserName( userName );
    }
}
