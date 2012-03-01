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

package org.drools.guvnor.client.perspective;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.perspective.PerspectivesPanelView.Presenter;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.client.util.TabbedPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;

public class PerspectivesPanel implements Presenter {

    private final PerspectivesPanelView view;
    private final EventBus eventBus;
    private final ClientFactory clientFactory;

    public PerspectivesPanel(ClientFactory clientFactory, EventBus eventBus) {
        this.eventBus = eventBus;
        this.clientFactory = clientFactory;
        this.view = clientFactory.getNavigationViewFactory().getPerspectivesPanelView();
        this.view.setPresenter(this);

        String[] registeredPerspectiveTypes = clientFactory.getPerspectiveFactory().getRegisteredPerspectiveTypes();
        for(String perspectiveType : registeredPerspectiveTypes) {
            //TODO: Get perspective title from PerspectiveFactory
            view.addPerspective(perspectiveType, perspectiveType);
        }
        
        //Use the first one as the default perspective.
        if(registeredPerspectiveTypes != null && registeredPerspectiveTypes.length !=0) {
            setPerspective(clientFactory.getPerspectiveFactory().getPerspective(registeredPerspectiveTypes[0]));
        }
    }

    private void setPerspective(Workspace workspace) {
        eventBus.fireEvent(new ChangePerspectiveEvent(workspace));
    }

    public PerspectivesPanelView getView() {
        return view;
    }

    public void setUserName(String userName) {
        view.setUserName(userName);
    }
    
    public void onChangePerspective(String perspectiveType) {
        updateGlobalAreaType(perspectiveType);
        setPerspective(clientFactory.getPerspectiveFactory().getPerspective(perspectiveType));        
    }

    public void onLogout() {
        clientFactory.getSecurityService().logout(new GenericCallback() {
            public void onSuccess(Object result) {
                Window.open(GWT.getModuleBaseURL() + "Guvnor.jsp", "_self", "");
            }
        });
    }

    public TabbedPanel getTabbedPanel() {
        return view.getTabbedPanel();
    }
    
    //TODO: a temporary hack
    private void updateGlobalAreaType(final String perspectiveType) {
        clientFactory.getModuleService().loadGlobalModule( new GenericCallback<Module>() {
            public void onSuccess( Module packageConfigData ) {
                if("author".equals(perspectiveType)) {
                    packageConfigData.setFormat("package");
                } else if("soaservice".equals(perspectiveType)) {
                    packageConfigData.setFormat("soaservice");
                }
                clientFactory.getModuleService().saveModule( packageConfigData,
                        new GenericCallback<ValidatedResponse>() {
                            public void onSuccess(ValidatedResponse data) {
                            }
                } );
            }
        } );
        
    }

}
