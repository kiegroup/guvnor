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

import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeItemBaseView.Presenter;
import org.drools.guvnor.client.rpc.Module;

public abstract class ModulesTreeItemBase
        implements
        IsWidget,
        Presenter {

    protected PackageView packageHierarchy = new PackageHierarchicalView();
    protected ModulesTreeItemBaseView view;
    protected String perspectiveTypes;
    protected final ClientFactory clientFactory;

    public ModulesTreeItemBase(
            ClientFactory clientFactory,
            ModulesTreeItemBaseView view,
            String perspectiveTypes) {
        this.view = view;
        view.setPresenter(this);
        this.clientFactory = clientFactory;
        this.perspectiveTypes = perspectiveTypes;
        setUpRootItem();
    }

    protected void setUpRootItem() {
        fillModulesTree(view.addModulesTreeItem());
    }

    protected abstract void fillModulesTree(final IsTreeItem treeItem);

    public void onModuleSelected(Object userObject) {
        if (userObject instanceof Place) {
            clientFactory.getDeprecatedPlaceController().goTo((Place) userObject);
        }
    }

    public SafeHtml getModuleTreeRootNodeHeader() {
        return clientFactory.getNavigationViewFactory().getModulesTreeRootNodeHeader(perspectiveTypes);
    }

    protected void addModules(Module[] packageConfigDatas,
            IsTreeItem treeItem) {

        for (Module packageConfigData : packageConfigDatas) {
            packageHierarchy.addPackage(packageConfigData);
        }

        Folder rootFolder = packageHierarchy.getRootFolder();
        for (Folder childFolder : rootFolder.getChildren()) {
            createModuleTreeItem(treeItem,
                    childFolder);
        }
    }

    protected ModuleTreeItem createModuleTreeItem(IsTreeItem treeItem,
            Folder folder) {
        ModuleTreeItem mti = null;
        String folderName = folder.getFolderName();
        Module conf = folder.getPackageConfigData();
        if (conf != null) {
            mti = new ModuleTreeSelectableItem(
                    clientFactory.getNavigationViewFactory(),
                    view.addModuleTreeSelectableItem(
                            treeItem,
                            folderName),
                    conf);
        } else {
            mti = new ModuleTreeItem(
                    clientFactory.getNavigationViewFactory(),
                    view.addModuleTreeItem(treeItem,
                            folderName));
        }
        for (Folder childFolder : folder.getChildren()) {
            createModuleTreeItem(mti.getRootItem(),
                    childFolder).getRootItem();
        }
        return mti;
    }

    public Widget asWidget() {
        return view.asWidget();
    }
}
