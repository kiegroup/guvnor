/*
 * Copyright 2011 JBoss by Red Hat.
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
package org.drools.guvnor.client.widgets.drools.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.PackageBuilderWidget;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;
import org.drools.guvnor.client.rpc.SnapshotInfo;

import static org.drools.guvnor.client.widgets.drools.explorer.ExplorerRenderMode.*;

public class PackageResourceExplorerWidget extends AbstractPackageDefinitionExplorerWidget {

    private boolean globalArea;

    //Services
    private ModuleServiceAsync packageService;

    //Package Info
    private String packageUUID;

    //UI Elements

    // UI
    interface CreatePackageResourceWidgetBinder
            extends
            UiBinder<Widget, PackageResourceExplorerWidget> {

    }

    private static CreatePackageResourceWidgetBinder uiBinder = GWT.create(CreatePackageResourceWidgetBinder.class);

    @UiField
    protected TextBox txtName;

    @UiField
    protected TextBox txtDescription;

    @UiField
    protected Tree packageTree;

    @UiField
    protected Label labelName;

    @UiField
    protected Label labelDescr;

    public PackageResourceExplorerWidget(final String packageUUID,
            final String packageName,
            final ClientFactory clientFactory,
            final ExplorerRenderMode mode) {

        this.initWidget(uiBinder.createAndBindUi(this));

        this.globalArea = packageName.equals("globalArea");

        this.packageService = clientFactory.getModuleService();

        //store data
        this.packageUUID = packageUUID;

        this.initializePackageTree();

        if (mode.equals(HIDE_NAME_AND_DESCRIPTION)) {
            txtName.setVisible(false);
            txtDescription.setVisible(false);
            labelName.setVisible(false);
            labelDescr.setVisible(false);
        }
    }

    private void initializePackageTree() {

        //if we are in globalArea, then we need to add all pakcages
        //including globalArea

        if (this.globalArea) {
            //Global Area Data
            this.packageService.loadGlobalModule(new AsyncCallback<Module>() {

                public void onFailure(Throwable caught) {
                    ErrorPopup.showMessage("Error listing Global Area information!");
                }

                public void onSuccess(Module result) {
                    populatePackageTree(result, null);
                }
            });

            //Packages Data
            this.packageService.listModules(new AsyncCallback<Module[]>() {

                public void onFailure(Throwable caught) {
                    ErrorPopup.showMessage("Error listing package information!");
                }

                public void onSuccess(Module[] result) {
                    for (int i = 0; i < result.length; i++) {
                        final Module packageConfigData = result[i];
                        populatePackageTree(packageConfigData, null);
                    }
                }
            });
        } else {
            Path path = new PathImpl();
            path.setUUID(this.packageUUID);   
            this.packageService.loadModule(path,
                    new AsyncCallback<Module>() {

                        public void onFailure(Throwable caught) {
                            ErrorPopup.showMessage("Error listing package information!");
                        }

                        public void onSuccess(Module result) {
                            populatePackageTree(result,
                                    null);
                        }

                    });
        }

        this.packageTree.setStyleName("category-explorer-Tree"); //NON-NLS

    }

    private void populatePackageTree(final Module packageConfigData, final TreeItem rootItem) {

        final TreeItem packageItem = new TreeItem(packageConfigData.getName());

        packageItem.addItem(createTreeItem("LATEST", packageConfigData.getName(), PackageBuilderWidget.getDownloadLink(packageConfigData)));

        this.packageService.listSnapshots(packageConfigData.getName(),
                new AsyncCallback<SnapshotInfo[]>() {

                    public void onFailure(Throwable caught) {
                        ErrorPopup.showMessage("Error listing snapshots information!");
                    }

                    public void onSuccess(SnapshotInfo[] result) {
                        for (int j = 0; j < result.length; j++) {
                            final SnapshotInfo snapshotInfo = result[j];
                            ModuleServiceAsync moduleService = GWT.create(ModuleService.class);
                            Path path = new PathImpl();
                            path.setUUID(snapshotInfo.getUuid());    
                            moduleService.loadModule(path,
                                    new AsyncCallback<Module>() {

                                        public void onFailure(Throwable caught) {
                                            ErrorPopup.showMessage("Error listing snapshots information!");
                                        }

                                        public void onSuccess(Module result) {
                                            packageItem.addItem(createTreeItem(snapshotInfo.getName(), packageConfigData.getName(), PackageBuilderWidget.getDownloadLink(result)));
                                        }
                                    });

                        }
                    }
                });

        //if no rootItem, then add the node directly to the tree
        if (rootItem == null) {
            this.packageTree.addItem(packageItem);
        } else {
            rootItem.addItem(packageItem);
        }

    }

    private TreeItem createTreeItem(final String label, final String moduleName, final String link) {
        TreeItem treeItem = new TreeItem(new RadioButton("pkgResourceGroup", label));
        treeItem.setUserObject(new TreeItemData(moduleName, label, link));

        return treeItem;
    }

    private class TreeItemData {

        final String moduleName;
        final String label;
        final String link;

        TreeItemData(final String moduleName, final String label, final String link) {
            this.moduleName = moduleName;
            this.label = label;
            this.link = link;
        }
    }

    public void processSelectedPackage(final PackageReadyCommand command) {
        try {
            //source is mandatory!
            final TreeItem selectedPackageItem = this.packageTree.getSelectedItem();
            if (selectedPackageItem == null || selectedPackageItem.getChildCount() != 0) {
                throw new IllegalStateException(Constants.INSTANCE.NoPackageSeleced());
            }

            final TreeItemData treeItem = (TreeItemData) selectedPackageItem.getUserObject();
            command.onSuccess(treeItem.moduleName, treeItem.label, treeItem.link, txtName.getText(), txtDescription.getText());

        } catch (Throwable t) {
            command.onFailure(t);
        }
    }

}
