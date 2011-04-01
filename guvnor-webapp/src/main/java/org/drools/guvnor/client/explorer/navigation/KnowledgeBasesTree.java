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

import java.util.Arrays;
import java.util.Map;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.PackageHierarchy;
import org.drools.guvnor.client.explorer.TabContainer;
import org.drools.guvnor.client.explorer.TabManager;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.util.Util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class KnowledgeBasesTree extends NavigationItemBuilderOld
        implements
        OpenHandler<TreeItem> {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    private boolean packagesLoaded = false;

    public KnowledgeBasesTree() {


        mainTree.setAnimationEnabled(true);
        mainTree.addSelectionHandler(this);
        mainTree.addOpenHandler(this);
    }

    public MenuBar createMenu() {
        if (UserCapabilities.INSTANCE.hasCapability(Capability.SHOW_CREATE_NEW_ASSET)) {
            return PackagesNewMenu.getMenu(this);
        } else {
            return null;
        }
    }

    public Tree createTree() {
        return new Tree();
    }

    public String getName() {
        return constants.KnowledgeBases();
    }

    public ImageResource getImage() {
        return images.packages();
    }

    public IsWidget createContent() {
        return this;
    }

    public void loadPackageList() {
        if (!packagesLoaded) {
            setupPackagesTree();
            packagesLoaded = true;
        }
    }

    public void refreshTree() {
        mainTree.clear();
        itemWidgets.clear();

        setupPackagesTree();
    }

    private void setupPackagesTree() {
        TreeItem packageRootNode = new TreeItem(Util.getHeader(images.chartOrganisation(),
                constants.Packages()));
        setupPackageNode(packageRootNode);
        mainTree.addItem(packageRootNode);

        setupGlobalNode(mainTree,
                itemWidgets);
    }

    private void setupPackageNode(final TreeItem packageRootNode) {
        packageRootNode.setState(true);
        packageRootNode.setUserObject("rootNode");

        RepositoryServiceFactory.getPackageService().listPackages(new GenericCallback<PackageConfigData[]>() {
            public void onSuccess(PackageConfigData[] packageConfigDatas) {
                PackageHierarchy packageHierarchy = new PackageHierarchy();

                for (PackageConfigData packageConfigData : packageConfigDatas) {
                    packageHierarchy.addPackage(packageConfigData);
                }

                for (PackageHierarchy.Folder folder : packageHierarchy.getRoot().getChildren()) {
                    buildPkgTree(packageRootNode,
                            folder);
                }
            }
        });
    }

    private void setupGlobalNode(final Tree root,
                                 final Map<TreeItem, String> itemWidgets) {
        RepositoryServiceFactory.getPackageService().loadGlobalPackage(new GenericCallback<PackageConfigData>() {
            public void onSuccess(PackageConfigData value) {
                TreeItem globalRootNode = ExplorerNodeConfig.getPackageItemStructure(constants.GlobalArea(),
                        value.uuid,
                        itemWidgets);
                globalRootNode.setHTML(Util.getHeader(images.chartOrganisation(),
                        constants.GlobalArea()));
                globalRootNode.setUserObject(value);

                root.addItem(globalRootNode);
            }
        });
    }

    private void buildPkgTree(TreeItem root,
                              PackageHierarchy.Folder folder) {
        if (folder.getConfig() != null) {
            root.addItem(loadPackage(folder.getName(),
                    folder.getConfig()));
        } else {
            TreeItem treeItem = new TreeItem(Util.getHeader(images.emptyPackage(),
                    folder.getName()));
            root.addItem(treeItem);

            for (PackageHierarchy.Folder childFolder : folder.getChildren()) {
                buildPkgTree(treeItem,
                        childFolder);
            }
        }
    }

    private TreeItem loadPackage(String name,
                                 PackageConfigData config) {
        TreeItem treeItem = ExplorerNodeConfig.getPackageItemStructure(name,
                config.uuid,
                itemWidgets);
        treeItem.setUserObject(config);
        return treeItem;
    }

    public static String key(String[] formats,
                             PackageConfigData userObject) {
        StringBuilder keyBuilder = new StringBuilder(userObject.uuid);
        if (formats.length == 0) {
            keyBuilder.append("[0]");
        } else {
            for (String format : formats) {
                keyBuilder.append(format);
            }
        }
        return keyBuilder.toString();
    }

    // Show the associated widget in the deck panel
    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem node = event.getSelectedItem();
        Object userObject = node.getUserObject();

        TabManager tabManager = TabContainer.getInstance();

        if (userObject != null) {
            if (userObject instanceof PackageConfigData && !((PackageConfigData) userObject).isGlobal()) {
                PackageConfigData pc = (PackageConfigData) userObject;
                RulePackageSelector.currentlySelectedPackage = pc.name;

                String uuid = pc.uuid;
                tabManager.openPackageEditor(uuid,
                        new Command() {
                            public void execute() {
                                // refresh the package tree.
                                refreshTree();
                            }
                        });
            } else if (userObject instanceof String[]) {
                final String[] formats = (String[]) userObject;
                final PackageConfigData packageConfigData = (PackageConfigData) node.getParentItem().getUserObject();
                RulePackageSelector.currentlySelectedPackage = packageConfigData.name;
                String key = key(formats,
                        packageConfigData);
                tabManager.openPackageViewAssets(packageConfigData.uuid,
                        packageConfigData.name,
                        key,
                        formats.length == 0 ? null : Arrays.asList(formats),
                        formats.length == 0 ? Boolean.TRUE : null,
                        node.getText());
            } else if (userObject instanceof String) {
                // Ignore, there is no click event for this.
            } else {
                throw new IllegalArgumentException("The userObject (" + userObject + ") is not supported.");
            }
        }
    }

    public void onOpen(OpenEvent<TreeItem> event) {
        TreeItem node = event.getTarget();
        Object userObject = node.getUserObject();

        if (userObject != null && userObject instanceof String && "rootNode".equals((String) userObject)) {
            node.removeItems();
            setupPackageNode(node);
        }
    }
}
