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

package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.uberfire.security.Identity;

public class ModulesTree
        implements
        ModulesTreeView.Presenter {

    private ModulesTreeView view;
    private ClientFactory clientFactory;
    private final EventBus eventBus;
    private String perspectiveTypes;
    private final Identity identity;

    public ModulesTree(ClientFactory clientFactory,
                       EventBus eventBus,
                       Identity identity,
                       String perspectiveTypes) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.identity = identity;
        this.perspectiveTypes = perspectiveTypes;
        this.view = clientFactory.getNavigationViewFactory().getModulesTreeView();
        this.view.setPresenter(this);
        addRootPanels();
    }

    private void addRootPanels() {
        view.setGlobalAreaTreeItem(new GlobalAreaTreeItem(clientFactory));

        view.setModulesTreeItem(new ModulesTreeItem(clientFactory, eventBus, perspectiveTypes));

        if (UserCapabilities.canCreateNewAsset(identity)) {
            view.setNewAssetMenu(clientFactory.getNavigationViewFactory().getModulesNewAssetMenu(perspectiveTypes));
        }
    }

    public void setFlatView() {
        ChangeModuleHierarchyEvent event = new ChangeModuleHierarchyEvent(new PackageFlatView());
        eventBus.fireEvent(event);
    }

    public void setHierarchyView() {
        ChangeModuleHierarchyEvent event = new ChangeModuleHierarchyEvent(new PackageHierarchicalView());
        eventBus.fireEvent(event);
    }

    public void expandAll() {
        ExpandAllEvent event = new ExpandAllEvent();
        eventBus.fireEvent(event);
    }

    public void collapseAll() {
        CollapseAllEvent event = new CollapseAllEvent();
        eventBus.fireEvent(event);
    }

}
