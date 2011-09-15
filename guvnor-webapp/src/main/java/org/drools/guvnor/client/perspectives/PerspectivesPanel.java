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
import com.google.gwt.user.client.Window;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.navigation.modules.ModuleTreeSelectableItem;
import org.drools.guvnor.client.perspectives.PerspectivesPanelView.Presenter;
import org.drools.guvnor.client.perspectives.author.AuthorPerspective;
import org.drools.guvnor.client.perspectives.runtime.RunTimePerspective;
import org.drools.guvnor.client.perspectives.soa.SOAPerspective;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.ValidatedResponse;
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
        updateGlobalAreaType(perspectiveType);
        setPerspective(clientFactory.getPerspectiveFactory().getPerspective(perspectiveType));        
    }
    
    public TabbedPanel getTabbedPanel() {
        return view.getTabbedPanel();
    }
    
    //TODO: a temporary hack
    private void updateGlobalAreaType(final String perspectiveType) {
        clientFactory.getPackageService().loadGlobalPackage( new GenericCallback<PackageConfigData>() {
            public void onSuccess( PackageConfigData packageConfigData ) {
                if("author".equals(perspectiveType)) {
                    packageConfigData.setFormat("package");
                } else if("soaservice".equals(perspectiveType)) {
                    packageConfigData.setFormat("soaservice");
                }
                clientFactory.getPackageService().savePackage( packageConfigData,
                        new GenericCallback<ValidatedResponse>() {
                            public void onSuccess(ValidatedResponse data) {
                            }
                } );
            }
        } );
        
    }

}
