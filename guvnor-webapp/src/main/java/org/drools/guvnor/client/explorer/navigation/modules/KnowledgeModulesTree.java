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
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;

public class KnowledgeModulesTree
    implements
    KnowledgeModulesTreeView.Presenter {

    private KnowledgeModulesTreeView view;
    private ClientFactory            clientFactory;
    private final EventBus eventBus;

    public KnowledgeModulesTree(ClientFactory clientFactory, EventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.view = clientFactory.getNavigationViewFactory().getKnowledgeModulesTreeView();
        this.view.setPresenter( this );
        addRootPanels();
    }

    private void addRootPanels() {
        view.setGlobalAreaTreeItem( new GlobalAreaTreeItem( clientFactory ) );

        view.setKnowledgeModulesTreeItem( new KnowledgeModulesTreeItem( clientFactory, eventBus ) );

        if ( UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_CREATE_NEW_ASSET ) ) {
            view.setNewAssetMenu(
                    new ModulesNewAssetMenu( clientFactory, eventBus ) );
        }
    }

    public void setFlatView() {
        ChangeModulePackageHierarchyEvent event = new ChangeModulePackageHierarchyEvent( new PackageFlatView() );
        eventBus.fireEvent( event );
    }

    public void setHierarchyView() {
        ChangeModulePackageHierarchyEvent event = new ChangeModulePackageHierarchyEvent( new PackageHierarchicalView() );
        eventBus.fireEvent( event );
    }

    public void expandAll() {
        ExpandAllEvent event = new ExpandAllEvent();
        eventBus.fireEvent( event );
    }

    public void collapseAll() {
        CollapseAllEvent event = new CollapseAllEvent();
        eventBus.fireEvent( event );
    }

}
