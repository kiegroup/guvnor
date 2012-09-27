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

package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.GuvnorEventBus;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.moduleeditor.ModuleEditorWrapper;
import org.drools.guvnor.client.rpc.Module;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "moduleEditor")
public class ModuleEditorActivity {

    private final ClientFactory clientFactory;
    private ModuleEditorActivityView view;
    private final PlaceManager placeManager;
    private final SimplePanel simplePanel = new SimplePanel();
    private final GuvnorEventBus eventBus;

    // TODO: add handler for module refresh event -Rikkola-

    @Inject
    public ModuleEditorActivity(PlaceManager placeManager,
                                ModuleEditorActivityView view,
                                ClientFactory clientFactory,
                                GuvnorEventBus eventBus) {
        this.placeManager = placeManager;
        this.view = view;
        this.eventBus = eventBus;

        this.clientFactory = clientFactory;
    }

    @OnStart
    public void init() {
        clientFactory.getModuleService().loadModule(placeManager.getCurrentPlaceRequest().getParameters().get("uuid"),
                new GenericCallback<Module>() {
                    public void onSuccess(Module packageConfigData) {
                        RulePackageSelector.currentlySelectedPackage = packageConfigData.getUuid();
                        simplePanel.add(new ModuleEditorWrapper(packageConfigData, clientFactory, eventBus));

                        view.closeLoadingPackageInformationMessage();
                    }
                });

        view.showLoadingPackageInformationMessage();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return simplePanel;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "packageConfigData.getName()"; // TODO : -Rikkola-
    }
}
