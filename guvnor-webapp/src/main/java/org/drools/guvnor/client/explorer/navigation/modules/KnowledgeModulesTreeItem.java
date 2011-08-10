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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.packages.RefreshModuleListEvent;
import org.drools.guvnor.client.packages.RefreshModuleListEventHandler;
import org.drools.guvnor.client.rpc.PackageConfigData;

import com.google.gwt.user.client.ui.IsTreeItem;

public class KnowledgeModulesTreeItem extends ModulesTreeItemBase {

    public KnowledgeModulesTreeItem(ClientFactory clientFactory) {
        super( clientFactory,
               clientFactory.getNavigationViewFactory().getKnowledgeModulesTreeItemView() );

        setRefreshHandler( clientFactory );
        setPackageHierarchyChangeHandler( clientFactory );
        setCollapseAllChangeHandler( clientFactory );
        setExpandAllChangeHandler( clientFactory );
    }

    private void setRefreshHandler(ClientFactory clientFactory) {
        clientFactory.getEventBus().addHandler( RefreshModuleListEvent.TYPE,
                                                new RefreshModuleListEventHandler() {
                                                    public void onRefreshList(RefreshModuleListEvent refreshModuleListEvent) {
                                                        getView().clearModulesTreeItem();
                                                        setUpRootItem();
                                                    }
                                                } );
    }

    private void setPackageHierarchyChangeHandler(ClientFactory clientFactory) {
        clientFactory.getEventBus().addHandler( ChangeModulePackageHierarchyEvent.TYPE,
                                                new ChangeModulePackageHierarchyEventHandler() {
                                                    public void onChangeModulePackageHierarchy(ChangeModulePackageHierarchyEvent event) {
                                                        getView().clearModulesTreeItem();
                                                        packageHierarchy = event.getPackageHierarchy();
                                                        setUpRootItem();
                                                    }
                                                } );
    }

    private void setCollapseAllChangeHandler(ClientFactory clientFactory) {
        clientFactory.getEventBus().addHandler( CollapseAllEvent.TYPE,
                                                new CollapseAllEventHandler() {
                                                    public void onCollapseAll(CollapseAllEvent event) {
                                                        getView().collapseAll();
                                                    }
                                                } );
    }

    private void setExpandAllChangeHandler(ClientFactory clientFactory) {
        clientFactory.getEventBus().addHandler( ExpandAllEvent.TYPE,
                                                new ExpandAllEventHandler() {
                                                    public void onExpandAll(ExpandAllEvent event) {
                                                        getView().expandAll();
                                                    }
                                                } );
    }

    @Override
    protected void fillModulesTree(final IsTreeItem treeItem) {
        clientFactory.getPackageService().listPackages( new GenericCallback<PackageConfigData[]>() {
            public void onSuccess(PackageConfigData[] packageConfigDatas) {
                addModules( packageConfigDatas,
                            treeItem );
            }
        } );
    }

    private KnowledgeModulesTreeItemView getView() {
        return (KnowledgeModulesTreeItemView) view;
    }

}
