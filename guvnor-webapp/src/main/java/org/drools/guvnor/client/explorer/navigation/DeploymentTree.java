/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.DeploymentNewMenu;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.TabContainer;
import org.drools.guvnor.client.explorer.TabManager;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class DeploymentTree extends NavigationItemBuilderOld
        implements
        OpenHandler<TreeItem> {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = (Images) GWT.create(Images.class);

    public DeploymentTree() {

        mainTree.setAnimationEnabled(true);
        ExplorerNodeConfig.setupDeploymentTree(mainTree,
                itemWidgets);
        mainTree.addSelectionHandler(this);
        mainTree.addOpenHandler(this);
    }

    public MenuBar createMenu() {
        if (UserCapabilities.INSTANCE.hasCapability(Capability.SHOW_CREATE_NEW_ASSET)) {
            return DeploymentNewMenu.getMenu(this);
        } else {
            return null;
        }
    }

    public Tree createTree() {
        return new Tree();
    }

    public String getName() {
        return constants.PackageSnapshots();
    }

    public ImageResource getImage() {
        return images.deploy();
    }

    public IsWidget createContent() {
        return this;
    }

    public void refreshTree() {
        mainTree.clear();
        itemWidgets.clear();
        ExplorerNodeConfig.setupDeploymentTree(mainTree,
                itemWidgets);
    }

    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();

        if (item.getUserObject() instanceof Object[]) {
            Object[] o = (Object[]) item.getUserObject();
            final String snapName = ((SnapshotInfo) o[0]).name;
            PackageConfigData conf = (PackageConfigData) o[1];
            RepositoryServiceFactory.getPackageService().listSnapshots(conf.name,
                    new GenericCallback<SnapshotInfo[]>() {
                        public void onSuccess(SnapshotInfo[] a) {
                            for (SnapshotInfo snap : a) {
                                if (snap.name.equals(snapName)) {
                                    TabManager tabManager = TabContainer.getInstance();
                                    tabManager.openSnapshot(snap);
                                    return;
                                }
                            }
                        }
                    });
        }
    }

    public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem node = event.getTarget();
        if (ExplorerNodeConfig.PACKAGE_SNAPSHOTS.equals(itemWidgets.get(node))) {
            return;
        }
        final PackageConfigData conf = (PackageConfigData) node.getUserObject();
        if (conf != null) {
            RepositoryServiceFactory.getPackageService().listSnapshots(conf.name,
                    new GenericCallback<SnapshotInfo[]>() {
                        public void onSuccess(SnapshotInfo[] snaps) {
                            node.removeItems();
                            for (final SnapshotInfo snapInfo : snaps) {
                                TreeItem snap = new TreeItem(snapInfo.name);
                                //snap.setTooltip(snapInfo.comment);
                                snap.setUserObject(new Object[]{snapInfo, conf});
                                node.addItem(snap);
                            }
                        }
                    });
        }
    }
}
