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
import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEvent;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEventHandler;
import org.drools.guvnor.client.rpc.PackageConfigData;

public class ModulesTreeItem extends ModulesTreeItemBase {

    private final EventBus eventBus;

    public ModulesTreeItem(ClientFactory clientFactory, EventBus eventBus, String perspectiveTypes) {
        super(clientFactory,
              clientFactory.getNavigationViewFactory().getModulesTreeItemView(),
              perspectiveTypes);

        this.eventBus = eventBus;

        setRefreshHandler();
        setModuleHierarchyChangeHandler();
        setCollapseAllChangeHandler();
        setExpandAllChangeHandler();
    }

    private void setRefreshHandler() {
        eventBus.addHandler(RefreshModuleListEvent.TYPE,
                new RefreshModuleListEventHandler() {
                    public void onRefreshList(RefreshModuleListEvent refreshModuleListEvent) {
                        getView().clearModulesTreeItem();
                        packageHierarchy.clear();
                        setUpRootItem();
                    }
                });
    }

    private void setModuleHierarchyChangeHandler() {
        eventBus.addHandler(ChangeModuleHierarchyEvent.TYPE,
                new ChangeModuleHierarchyEventHandler() {
                    public void onChangeModuleHierarchy(ChangeModuleHierarchyEvent event) {
                        getView().clearModulesTreeItem();
                        packageHierarchy = event.getPackageHierarchy();
                        setUpRootItem();
                    }
                });
    }

    private void setCollapseAllChangeHandler() {
        eventBus.addHandler(CollapseAllEvent.TYPE,
                new CollapseAllEventHandler() {
                    public void onCollapseAll(CollapseAllEvent event) {
                        getView().collapseAll();
                    }
                });
    }

    private void setExpandAllChangeHandler() {
        eventBus.addHandler(ExpandAllEvent.TYPE,
                new ExpandAllEventHandler() {
                    public void onExpandAll(ExpandAllEvent event) {
                        getView().expandAll();
                    }
                });
    }

    @Override
    protected void fillModulesTree(final IsTreeItem treeItem) {
        clientFactory.getPackageService().listPackages(new GenericCallback<PackageConfigData[]>() {
            public void onSuccess(PackageConfigData[] packageConfigDatas) {
                addModules(packageConfigDatas,
                        treeItem);
            }
        });
    }

    private ModulesTreeItemView getView() {
        return (ModulesTreeItemView) view;
    }

}
