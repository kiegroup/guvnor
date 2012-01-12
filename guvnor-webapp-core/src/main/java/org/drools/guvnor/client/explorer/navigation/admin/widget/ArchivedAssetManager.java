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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEvent;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.client.widgets.tables.AdminArchivedPagedTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArchivedAssetManager extends Composite {

    private static Images           images    = (Images) GWT.create( Images.class );

    private AdminArchivedPagedTable table;
    private ListBox                 packages  = new ListBox( true );
    private Constants               constants = GWT.create( Constants.class );
    private Button                  btnRestorePackage;
    private Button                  btnDeletePackage;
    private EventBus                eventBus;

    public ArchivedAssetManager(ClientFactory clientFactory,
                                EventBus eventBus) {

        this.eventBus = eventBus;

        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel header = new VerticalPanel();
        header.add( new HTML( constants.ArchivedItems() ) );

        pf.addHeader( images.backupLarge(),
                      header );

        loadPackages();

        Command restoreSelectedAssetCommand = new Command() {

            public void execute() {
                if ( table.getSelectedRowUUIDs() == null ) {
                    Window.alert( constants.PleaseSelectAnItemToRestore() );
                    return;
                }
                RepositoryServiceFactory.getAssetService().archiveAssets( table.getSelectedRowUUIDs(),
                                                                          false,
                                                                          new GenericCallback<java.lang.Void>() {
                                                                              public void onSuccess(Void arg0) {
                                                                                  Window.alert( constants.ItemRestored() );
                                                                                  table.refresh();
                                                                              }
                                                                          } );
            }

        };

        Command deleteSelectedAssetCommand = new Command() {

            public void execute() {
                if ( table.getSelectedRowUUIDs() == null ) {
                    Window.alert( constants.PleaseSelectAnItemToPermanentlyDelete() );
                    return;
                }
                if ( !Window.confirm( constants.AreYouSureDeletingAsset() ) ) {
                    return;
                }
                RepositoryServiceFactory.getAssetService().removeAssets( table.getSelectedRowUUIDs(),
                                                                         new GenericCallback<java.lang.Void>() {
                                                                             public void onSuccess(Void arg0) {
                                                                                 Window.alert( constants.ItemDeleted() );
                                                                                 table.refresh();
                                                                             }
                                                                         } );
            }

        };

        table = new AdminArchivedPagedTable(
                                             restoreSelectedAssetCommand,
                                             deleteSelectedAssetCommand,
                                             clientFactory );
        HorizontalPanel packagesToolbar = new HorizontalPanel();
        btnRestorePackage = new Button( constants.RestoreSelectedPackage() );
        btnRestorePackage.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( packages.getSelectedIndex() == -1 ) {
                    Window.alert( constants.PleaseSelectAnItemToRestore() );
                    return;
                }
                restorePackage( packages.getValue( packages.getSelectedIndex() ) );
            }

        } );
        packagesToolbar.add( btnRestorePackage );

        btnDeletePackage = new Button( constants.PermanentlyDeletePackage() );
        btnDeletePackage.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( packages.getSelectedIndex() == -1 ) {
                    Window.alert( constants.PleaseSelectAnItemToPermanentlyDelete() );
                    return;
                }
                if ( Window.confirm( constants.AreYouSurePackageDelete() ) ) {
                    deletePackage( packages.getValue( packages.getSelectedIndex() ) );
                }
            }

        } );
        packagesToolbar.add( btnDeletePackage );

        pf.startSection( constants.ArchivedPackagesList() );
        pf.addRow( packagesToolbar );
        pf.addRow( packages );
        pf.endSection();

        pf.startSection( constants.ArchivedAssets() );
        pf.addRow( table );
        pf.endSection();

        initWidget( pf );
    }

    private void deletePackage(final String uuid) {
        RepositoryServiceFactory.getPackageService().removeModule( uuid,
                                                                    new GenericCallback<java.lang.Void>() {
                                                                        public void onSuccess(Void data) {
                                                                            Window.alert( constants.PackageDeleted() );
                                                                            packages.clear();
                                                                            loadPackages();
                                                                        }
                                                                    } );
    }

    private void restorePackage(String uuid) {
        RepositoryServiceFactory.getPackageService().loadModule( uuid,
                                                                        new GenericCallback<Module>() {
                                                                            public void onSuccess(Module cf) {
                                                                                cf.setArchived( false );
                                                                                RepositoryServiceFactory.getPackageService().saveModule( cf,
                                                                                                                                          new GenericCallback<ValidatedResponse>() {
                                                                                                                                              public void onSuccess(ValidatedResponse data) {
                                                                                                                                                  Window.alert( constants.PackageRestored() );
                                                                                                                                                  packages.clear();
                                                                                                                                                  loadPackages();
                                                                                                                                                  table.refresh();
                                                                                                                                                  eventBus.fireEvent( new RefreshModuleListEvent() );
                                                                                                                                              }
                                                                                                                                          } );
                                                                            }
                                                                        } );
    }

    private ListBox loadPackages() {

        RepositoryServiceFactory.getPackageService().listArchivedModules( new GenericCallback<Module[]>() {
            public void onSuccess(Module[] configs) {
                for ( int i = 0; i < configs.length; i++ ) {
                    packages.addItem( configs[i].getName(),
                                      configs[i].getUuid() );
                }
                if ( configs.length == 0 ) {
                    packages.addItem( constants.noArchivedPackages() );
                }
                boolean enabled = (configs.length != 0);
                packages.setEnabled( enabled );
                btnRestorePackage.setEnabled( enabled );
                btnDeletePackage.setEnabled( enabled );
            }
        } );

        return packages;
    }

}
