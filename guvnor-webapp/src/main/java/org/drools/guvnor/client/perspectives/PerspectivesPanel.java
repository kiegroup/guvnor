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

package org.drools.guvnor.client.perspectives;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.perspectives.PerspectivesPanelView.Presenter;
import org.drools.guvnor.client.perspectives.author.AuthorPerspective;
import org.drools.guvnor.client.perspectives.runtime.RunTimePerspective;
import org.drools.guvnor.client.perspectives.soa.SOAPerspective;
import org.drools.guvnor.client.util.TabbedPanel;

public class PerspectivesPanel implements Presenter {

    private final PerspectivesPanelView view;
    private final EventBus eventBus;
    private final ClientFactory clientFactory;

    public PerspectivesPanel(ClientFactory clientFactory, EventBus eventBus) {
        this.eventBus = eventBus;
        this.clientFactory = clientFactory;
        this.view = clientFactory.getPerspectivesPanelView();
        this.view.setPresenter(this);
        setPerspective(new AuthorPerspective());
        String[] registeredPerspectiveTypes = clientFactory.getPerspectiveFactory().getRegisteredPerspectiveTypes();
        for(String perspectiveType : registeredPerspectiveTypes) {
            //TODO: Get perspective title from PerspectiveFactory
            view.addPerspective(perspectiveType, perspectiveType);
        }
    }

    private void setPerspective(Perspective perspective) {
        eventBus.fireEvent(new ChangePerspectiveEvent(perspective));
    }

    public PerspectivesPanelView getView() {
        return view;
    }

    public void setUserName(String userName) {
        view.setUserName(userName);
    }
    
    public void onChangePerspective(String perspectiveType) {
        setPerspective(clientFactory.getPerspectiveFactory().getPerspective(perspectiveType));        
    }
    
    public TabbedPanel getTabbedPanel() {
        return view.getTabbedPanel();
    }

}
