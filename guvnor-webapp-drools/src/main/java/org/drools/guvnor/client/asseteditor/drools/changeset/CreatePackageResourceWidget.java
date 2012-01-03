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
package org.drools.guvnor.client.asseteditor.drools.changeset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;

/**
 * Widget in charge of create the XML representation of a <Resource> to be added
 * to a change-set
 */
public class CreatePackageResourceWidget extends AbstractXMLResourceDefinitionCreatorWidget {

    private Constants           constants = GWT.create( Constants.class );
    private boolean             globalArea;

    //Services
    private ModuleServiceAsync packageService;

    //Package Info
    private String              packageUUID;

    //UI Elements

    // UI
    interface CreatePackageResourceWidgetBinder
        extends
        UiBinder<Widget, CreatePackageResourceWidget> {
    }

    private static CreatePackageResourceWidgetBinder uiBinder = GWT.create( CreatePackageResourceWidgetBinder.class );

    @UiField
    protected TextBox                                txtName;

    @UiField
    protected TextBox                                txtDescription;

    @UiField
    protected Tree                                   packageTree;

    public CreatePackageResourceWidget(String packageUUID,
                                       String packageName,
                                       ClientFactory clientFactory) {

        this.initWidget( uiBinder.createAndBindUi( this ) );

        this.globalArea = packageName.equals( "globalArea" );

        this.packageService = clientFactory.getModuleService();

        //store data
        this.packageUUID = packageUUID;

        this.initializePackageTree();

    }

    private void initializePackageTree() {

        //if we are in globalArea, then we need to add all pakcages
        //including globalArea

        if ( this.globalArea ) {
            //Global Area Data
            this.packageService.loadGlobalModule( new AsyncCallback<Module>() {

                public void onFailure(Throwable caught) {
                    ErrorPopup.showMessage( "Error listing Global Area information!" );
                }

                public void onSuccess(Module result) {
                    populatePackageTree( result,
                                         null );
                }
            } );

            //Packages Data
            this.packageService.listModules( new AsyncCallback<Module[]>() {

                public void onFailure(Throwable caught) {
                    ErrorPopup.showMessage( "Error listing package information!" );
                }

                public void onSuccess(Module[] result) {
                    for ( int i = 0; i < result.length; i++ ) {
                        final Module packageConfigData = result[i];
                        populatePackageTree( packageConfigData,
                                             null );
                    }
                }
            } );
        } else {
            this.packageService.loadModule( this.packageUUID,
                                                   new AsyncCallback<Module>() {

                                                       public void onFailure(Throwable caught) {
                                                           ErrorPopup.showMessage( "Error listing package information!" );
                                                       }

                                                       public void onSuccess(Module result) {
                                                           populatePackageTree( result,
                                                                                null );
                                                       }

                                                   } );
        }

        this.packageTree.setStyleName( "category-explorer-Tree" ); //NON-NLS

    }

    private void populatePackageTree(final Module packageConfigData,
                                     final TreeItem rootItem) {

        final TreeItem packageItem = new TreeItem( packageConfigData.getName() );

        packageItem.addItem( createTreeItem( "LATEST",
                                             PackageBuilderWidget.getDownloadLink( packageConfigData ) ) );

        this.packageService.listSnapshots( packageConfigData.getName(),
                                           new AsyncCallback<SnapshotInfo[]>() {

                                               public void onFailure(Throwable caught) {
                                                   ErrorPopup.showMessage( "Error listing snapshots information!" );
                                               }

                                               public void onSuccess(SnapshotInfo[] result) {
                                                   for ( int j = 0; j < result.length; j++ ) {
                                                       final SnapshotInfo snapshotInfo = result[j];
                                                       RepositoryServiceFactory.getPackageService().loadModule( snapshotInfo.getUuid(),
                                                                                                                       new AsyncCallback<Module>() {

                                                                                                                           public void onFailure(Throwable caught) {
                                                                                                                               ErrorPopup.showMessage( "Error listing snapshots information!" );
                                                                                                                           }

                                                                                                                           public void onSuccess(Module result) {
                                                                                                                               packageItem.addItem( createTreeItem( snapshotInfo.getName(),
                                                                                                                                                                    PackageBuilderWidget.getDownloadLink( result ) ) );
                                                                                                                           }
                                                                                                                       } );

                                                   }
                                               }
                                           } );

        //if no rootItem, then add the node directly to the tree
        if ( rootItem == null ) {
            this.packageTree.addItem( packageItem );
        } else {
            rootItem.addItem( packageItem );
        }

    }

    private TreeItem createTreeItem(String label,
                                    String link) {
        TreeItem treeItem = new TreeItem( new RadioButton( "pkgResourceGroup",
                                                           label ) );
        treeItem.setUserObject( link );

        return treeItem;
    }

    public void getResourceElement(final ResourceElementReadyCommand resourceElementReadyCommand) {
        try {
            //source is mandatory!
            TreeItem selectedPackageItem = this.packageTree.getSelectedItem();
            if ( selectedPackageItem == null || selectedPackageItem.getChildCount() != 0 ) {
                throw new IllegalStateException( constants.NoPackageSeleced() );
            }

            String result = resourceXMLElementTemplate;

            String nameString = "";
            if ( this.txtName.getText().length() != 0 ) {
                nameString = "name=\"" + this.txtName.getText().trim() + "\"";
            }
            result = result.replace( "{name}",
                                     nameString );

            String descriptionString = "";
            if ( this.txtDescription.getText().length() != 0 ) {
                descriptionString = "description=\"" + this.txtDescription.getText().trim() + "\"";
            }
            result = result.replace( "{description}",
                                     descriptionString );

            result = result.replace( "{type}",
                                     "PKG" );

            String source = (String) selectedPackageItem.getUserObject();
            result = result.replace( "{source}",
                                     source );

            resourceElementReadyCommand.onSuccess( result );

        } catch ( Throwable t ) {
            resourceElementReadyCommand.onFailure( t );
        }
    }

}
