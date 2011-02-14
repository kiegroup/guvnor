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

package org.drools.guvnor.client.admin;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.rulelist.OpenItemCommand;
import org.drools.guvnor.client.util.TabOpener;
import org.drools.guvnor.client.widgets.tables.AdminArchivedPagedTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

    public ArchivedAssetManager() {

        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel header = new VerticalPanel();
        header.add( new HTML( constants.ArchivedItems() ) );

        pf.addHeader( images.backupLarge(),
                      header );

        final TabOpener tabOpener = TabOpener.getInstance();

        OpenItemCommand openSelectedCommand = new OpenItemCommand() {
            public void open(String key) {
                tabOpener.openAsset( key );
            }

            public void open(MultiViewRow[] rows) {
                for ( MultiViewRow row : rows ) {
                    tabOpener.openAsset( row.uuid );
                }
            }
        };

        loadPackages();

        Command restoreSelectedAssetCommand = new Command() {

            @Override
            public void execute() {
                if ( table.getSelectedRowUUIDs() == null ) {
                    Window.alert( constants.PleaseSelectAnItemToRestore() );
                    return;
                }
                RepositoryServiceFactory.getService().archiveAssets( table.getSelectedRowUUIDs(),
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

            @Override
            public void execute() {
                if ( table.getSelectedRowUUIDs() == null ) {
                    Window.alert( constants.PleaseSelectAnItemToPermanentlyDelete() );
                    return;
                }
                if ( !Window.confirm( constants.AreYouSureDeletingAsset() ) ) {
                    return;
                }
                RepositoryServiceFactory.getService().removeAssets( table.getSelectedRowUUIDs(),
                                                                    new GenericCallback<java.lang.Void>() {
                                                                        public void onSuccess(Void arg0) {
                                                                            Window.alert( constants.ItemDeleted() );
                                                                            table.refresh();
                                                                        }
                                                                    } );
            }
            
        };
        
        table = new AdminArchivedPagedTable( restoreSelectedAssetCommand, deleteSelectedAssetCommand, openSelectedCommand );
        HorizontalPanel packagesToolbar = new HorizontalPanel();
        btnRestorePackage = new Button( constants.RestoreSelectedPackage() );
        btnRestorePackage.addClickHandler( new ClickHandler() {

            @Override
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

            @Override
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
        RepositoryServiceFactory.getService().removePackage( uuid,
                                                             new GenericCallback<java.lang.Void>() {
                                                                 public void onSuccess(Void data) {
                                                                     Window.alert( constants.PackageDeleted() );
                                                                     packages.clear();
                                                                     loadPackages();
                                                                 }
                                                             } );
    }

    private void restorePackage(String uuid) {
        RepositoryServiceFactory.getService().loadPackageConfig( uuid,
                                                                 new GenericCallback<PackageConfigData>() {
                                                                     public void onSuccess(PackageConfigData cf) {
                                                                         cf.archived = false;
                                                                         RepositoryServiceFactory.getService().savePackage( cf,
                                                                                                                            new GenericCallback<ValidatedResponse>() {
                                                                                                                                public void onSuccess(ValidatedResponse data) {
                                                                                                                                    Window.alert( constants.PackageRestored() );
                                                                                                                                    packages.clear();
                                                                                                                                    loadPackages();
                                                                                                                                    table.refresh();
                                                                                                                                }
                                                                                                                            } );
                                                                     }
                                                                 } );
    }

    private ListBox loadPackages() {

        RepositoryServiceFactory.getService().listArchivedPackages( new GenericCallback<PackageConfigData[]>() {
            public void onSuccess(PackageConfigData[] configs) {
                for ( int i = 0; i < configs.length; i++ ) {
                    packages.addItem( configs[i].name,
                                      configs[i].uuid );
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
