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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.widgets.tables.AssetPagedTable;
import org.uberfire.backend.vfs.Path;

import static org.drools.guvnor.client.widgets.drools.explorer.ExplorerRenderMode.*;
import static org.drools.guvnor.client.widgets.drools.explorer.PackageDisplayMode.*;

/**
 * Widget in charge of create the XML representation of a <Resource> to be added
 * to a change-set
 */
public class AssetResourceExplorerWidget extends AbstractResourceDefinitionExplorerWidget {

    private boolean globalArea;

    //Services
    private ModuleServiceAsync packageService;
    private final AssetServiceAsync assetService;

    private ClientFactory clientFactory;

    //Package Info
    private String packageUUID;
    private final PackageDisplayMode packageDisplayMode;

    //UI Elements

    // UI
    interface CreateAssetResourceWidgetBinder
            extends
            UiBinder<Widget, AssetResourceExplorerWidget> {

    }

    private static CreateAssetResourceWidgetBinder uiBinder = GWT.create(CreateAssetResourceWidgetBinder.class);

    @UiField
    protected TextBox txtName;

    @UiField
    protected TextBox txtDescription;

    @UiField
    protected ListBox lstPackage;

    @UiField
    protected ListBox lstFormat;

    @UiField
    protected ScrollPanel sclTreePanel;

    @UiField
    protected Label labelName;

    @UiField
    protected Label labelDescr;

    protected AssetPagedTable assetsTable;

    public AssetResourceExplorerWidget(final String packageUUID,
            final String packageName,
            final ClientFactory clientFactory,
            final String[] formatList,
            final ExplorerRenderMode mode,
            final PackageDisplayMode packageDisplayMode) {

        this.initWidget(uiBinder.createAndBindUi(this));

        this.globalArea = packageName.equals("globalArea");

        this.packageService = clientFactory.getModuleService();
        this.assetService = clientFactory.getAssetService();

        this.clientFactory = clientFactory;

        this.packageDisplayMode = packageDisplayMode;

        //store data
        this.packageUUID = packageUUID;

        this.initializeFormatList(formatList);

        this.initializePackageList();

        if (mode.equals(HIDE_NAME_AND_DESCRIPTION)) {
            txtName.setVisible(false);
            txtDescription.setVisible(false);
            labelName.setVisible(false);
            labelDescr.setVisible(false);
        }
    }

    private void initializePackageList() {

        if (this.globalArea || packageDisplayMode.equals(ALL_PACKAGES)) {
            //Global Area Data
            this.packageService.loadGlobalModule(new AsyncCallback<Module>() {

                public void onFailure(Throwable caught) {
                    ErrorPopup.showMessage("Error listing Global Area information!");
                }

                public void onSuccess(Module result) {
                    populatePackageList(result, packageUUID);
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
                        populatePackageList(packageConfigData, packageUUID);
                    }

                    //once packages are loaded is time to load the asset table
                    loadAssetTable();
                }
            });
        } else {
            this.packageService.loadModule(this.packageUUID,
                    new AsyncCallback<Module>() {

                        public void onFailure(Throwable caught) {
                            ErrorPopup.showMessage("Error listing package information!");
                        }

                        public void onSuccess(Module result) {
                            populatePackageList(result, null);

                            //once packages are loaded is time to load the asset table
                            loadAssetTable();
                        }

                    });
        }

        this.lstPackage.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                handleListChanges(event);
            }
        });

    }

    private void initializeFormatList(final String[] formatList) {
        for (String format : formatList) {
            this.lstFormat.addItem(format, format);
        }

        this.lstFormat.setSelectedIndex(0);

        this.lstFormat.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                handleListChanges(event);
            }
        });
    }

    private void loadAssetTable() {
        //remove any child of sclTreePanel. This allows us to call this 
        //method to refresh the table changing the format and/or package
        this.sclTreePanel.clear();

        //get the selected package from the list-box
        String selectedPackageUUID = lstPackage.getValue(lstPackage.getSelectedIndex());

        //get the selected format from the list-box
        List<String> selectedFormats = new ArrayList<String>();
        selectedFormats.add(lstFormat.getValue(lstFormat.getSelectedIndex()));

        this.assetsTable = new AssetPagedTable(selectedPackageUUID,
                selectedFormats,
                null,
                this.clientFactory);

        this.sclTreePanel.add(this.assetsTable);

        this.makeDirty();
    }

    /**
     * Handles {@link #lstFormat} and {@link #lstPackage} changes reloading the
     * asset's table
     * @param event
     */
    private void handleListChanges(ChangeEvent event) {
        this.loadAssetTable();
    }

    private void populatePackageList(final Module packageConfigData, final String uuidToBeSelected) {
        this.lstPackage.addItem(packageConfigData.getName(),
                packageConfigData.getUuid());
        if (uuidToBeSelected == null) {
            this.lstPackage.setSelectedIndex(0);
        } else if (packageConfigData.getUuid().equals(uuidToBeSelected)) {
            this.lstPackage.setSelectedIndex(this.lstPackage.getItemCount() - 1);
        }
    }

    public void processSelectedResources(final ResourceElementReadyCommand command) {
        //source is mandatory!
        final Path[] selectedRowUUIDs = this.assetsTable.getSelectedRowUUIDs();
        if (selectedRowUUIDs == null || selectedRowUUIDs.length == 0) {
            throw new IllegalStateException(Constants.INSTANCE.NoPackageSeleced());
        }

        //load asset information
        this.assetService.loadRuleAssets(selectedRowUUIDs, new AsyncCallback<Asset[]>() {
            public void onFailure(Throwable caught) {
                command.onFailure(caught);
            }

            public void onSuccess(Asset[] assets) {
                command.onSuccess(
                        lstPackage.getItemText(lstPackage.getSelectedIndex()),
                        assets,
                        txtName.getText(),
                        txtDescription.getText());
            }
        });
    }

}
