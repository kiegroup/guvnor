/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.explorer.navigation.admin.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.GuvnorEventBus;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEvent;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.client.widgets.tables.AdminArchivedPagedTable;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "archiveManager")
public class ArchivedAssetManager extends Composite {

    private AssetServiceAsync assetService = GWT.create(AssetService.class);
    private ModuleServiceAsync moduleService = GWT.create(ModuleService.class);

    private AdminArchivedPagedTable table;
    private ListBox packages = new ListBox(true);
    private Button btnRestorePackage;
    private Button btnDeletePackage;
    private EventBus eventBus;

    @Inject
    public ArchivedAssetManager(ClientFactory clientFactory,
                                GuvnorEventBus eventBus) {

        this.eventBus = eventBus;

        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel header = new VerticalPanel();
        header.add(new HTML(ConstantsCore.INSTANCE.ArchivedItems()));

        pf.addHeader(GuvnorImages.INSTANCE.Backup(),
                header);

        loadPackages();

        Command restoreSelectedAssetCommand = new Command() {

            public void execute() {
                if (table.getSelectedRowUUIDs() == null) {
                    Window.alert(ConstantsCore.INSTANCE.PleaseSelectAnItemToRestore());
                    return;
                }
                assetService.archiveAssets(table.getSelectedRowUUIDs(),
                        false,
                        new GenericCallback<java.lang.Void>() {
                            public void onSuccess(Void arg0) {
                                Window.alert(ConstantsCore.INSTANCE.ItemRestored());
                                table.refresh();
                            }
                        });
            }

        };

        Command deleteSelectedAssetCommand = new Command() {

            public void execute() {
                if (table.getSelectedRowUUIDs() == null) {
                    Window.alert(ConstantsCore.INSTANCE.PleaseSelectAnItemToPermanentlyDelete());
                    return;
                }
                if (!Window.confirm(ConstantsCore.INSTANCE.AreYouSureDeletingAsset())) {
                    return;
                }
                assetService.removeAssets(table.getSelectedRowUUIDs(),
                        new GenericCallback<java.lang.Void>() {
                            public void onSuccess(Void arg0) {
                                Window.alert(ConstantsCore.INSTANCE.ItemDeleted());
                                table.refresh();
                            }
                        });
            }

        };

        table = new AdminArchivedPagedTable(
                restoreSelectedAssetCommand,
                deleteSelectedAssetCommand,
                clientFactory);
        HorizontalPanel packagesToolbar = new HorizontalPanel();
        btnRestorePackage = new Button(ConstantsCore.INSTANCE.RestoreSelectedPackage());
        btnRestorePackage.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (packages.getSelectedIndex() == -1) {
                    Window.alert(ConstantsCore.INSTANCE.PleaseSelectAnItemToRestore());
                    return;
                }
                restorePackage(packages.getValue(packages.getSelectedIndex()));
            }

        });
        packagesToolbar.add(btnRestorePackage);

        btnDeletePackage = new Button(ConstantsCore.INSTANCE.PermanentlyDeletePackage());
        btnDeletePackage.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (packages.getSelectedIndex() == -1) {
                    Window.alert(ConstantsCore.INSTANCE.PleaseSelectAnItemToPermanentlyDelete());
                    return;
                }
                if (Window.confirm(ConstantsCore.INSTANCE.AreYouSurePackageDelete())) {
                    deletePackage(packages.getValue(packages.getSelectedIndex()));
                }
            }

        });
        packagesToolbar.add(btnDeletePackage);

        pf.startSection(ConstantsCore.INSTANCE.ArchivedPackagesList());
        pf.addRow(packagesToolbar);
        pf.addRow(packages);
        pf.endSection();

        pf.startSection(ConstantsCore.INSTANCE.ArchivedAssets());
        pf.addRow(table);
        pf.endSection();

        initWidget(pf);
    }

    private void deletePackage(final String uuid) {
        moduleService.removeModule(uuid,
                new GenericCallback<java.lang.Void>() {
                    public void onSuccess(Void data) {
                        Window.alert(ConstantsCore.INSTANCE.PackageDeleted());
                        packages.clear();
                        loadPackages();
                    }
                });
    }

    private void restorePackage(String uuid) {
        moduleService.loadModule(uuid,
                new GenericCallback<Module>() {
                    public void onSuccess(Module cf) {
                        cf.setArchived(false);
                        moduleService.saveModule(cf,
                                new GenericCallback<ValidatedResponse>() {
                                    public void onSuccess(ValidatedResponse data) {
                                        Window.alert(ConstantsCore.INSTANCE.PackageRestored());
                                        packages.clear();
                                        loadPackages();
                                        table.refresh();
                                        eventBus.fireEvent(new RefreshModuleListEvent());
                                    }
                                });
                    }
                });
    }

    private ListBox loadPackages() {

        moduleService.listArchivedModules(new GenericCallback<Module[]>() {
            public void onSuccess(Module[] configs) {
                for (Module config : configs) {
                    packages.addItem(config.getName(),
                            config.getUuid());
                }
                if (configs.length == 0) {
                    packages.addItem(ConstantsCore.INSTANCE.noArchivedPackages());
                }
                boolean enabled = (configs.length != 0);
                packages.setEnabled(enabled);
                btnRestorePackage.setEnabled(enabled);
                btnDeletePackage.setEnabled(enabled);
            }
        });

        return packages;
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return this;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ConstantsCore.INSTANCE.ArchivedManager();
    }
}
