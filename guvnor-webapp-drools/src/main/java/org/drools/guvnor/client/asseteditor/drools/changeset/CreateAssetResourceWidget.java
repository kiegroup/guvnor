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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.widgets.RESTUtil;
import org.drools.guvnor.client.widgets.tables.AssetPagedTable;

/**
 * Widget in charge of create the XML representation of a <Resource> to be added
 * to a change-set
 */
public class CreateAssetResourceWidget extends AbstractXMLResourceDefinitionCreatorWidget {
    private Constants               constants = GWT.create( Constants.class );
    private boolean                 globalArea;

    //Services
    private ModuleServiceAsync     packageService;
    private final AssetServiceAsync assetService;

    private ClientFactory           clientFactory;

    //Package Info
    private String                  packageUUID;

    //UI Elements

    // UI
    interface CreateAssetResourceWidgetBinder
        extends
        UiBinder<Widget, CreateAssetResourceWidget> {
    }

    private static CreateAssetResourceWidgetBinder uiBinder = GWT.create( CreateAssetResourceWidgetBinder.class );

    @UiField
    protected TextBox                              txtName;

    @UiField
    protected TextBox                              txtDescription;

    @UiField
    protected ListBox                              lstPackage;

    @UiField
    protected ListBox                              lstFormat;

    @UiField
    protected ScrollPanel                          sclTreePanel;

    protected AssetPagedTable                      assetsTable;

    public CreateAssetResourceWidget(String packageUUID,
                                     String packageName,
                                     ClientFactory clientFactory) {

        this.initWidget( uiBinder.createAndBindUi( this ) );

        this.globalArea = packageName.equals( "globalArea" );

        this.packageService = clientFactory.getModuleService();
        this.assetService = clientFactory.getAssetService();

        this.clientFactory = clientFactory;

        //store data
        this.packageUUID = packageUUID;

        this.initializeFormatList();

        this.initializePackageList();

    }

    private void initializePackageList() {

        //if we are in globalArea, then package list must be visible
        //If not, the current package is fixed

        if ( this.globalArea ) {
            //Global Area Data
            this.packageService.loadGlobalModule( new AsyncCallback<Module>() {

                public void onFailure(Throwable caught) {
                    ErrorPopup.showMessage( "Error listing Global Area information!" );
                }

                public void onSuccess(Module result) {
                    populatePackageList( result );
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
                        populatePackageList( packageConfigData );
                    }

                    //once packages are loaded is time to load the asset table
                    loadAssetTable();
                }
            } );
        } else {
            this.packageService.loadModule( this.packageUUID,
                                                   new AsyncCallback<Module>() {

                                                       public void onFailure(Throwable caught) {
                                                           ErrorPopup.showMessage( "Error listing package information!" );
                                                       }

                                                       public void onSuccess(Module result) {
                                                           populatePackageList( result );

                                                           //once packages are loaded is time to load the asset table
                                                           loadAssetTable();
                                                       }

                                                   } );
        }

        this.lstPackage.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                handleListChanges( event );
            }
        } );

    }

    private void initializeFormatList() {
        for ( String format : AssetFormats.CHANGE_SET_RESOURCE ) {
            //TODO: I18N the label!
            this.lstFormat.addItem( format,
                                    format );
        }

        this.lstFormat.setSelectedIndex( 0 );

        this.lstFormat.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                handleListChanges( event );
            }
        } );
    }

    private void loadAssetTable() {
        //remove any child of sclTreePanel. This allows us to call this 
        //method to refresh the table changing the format and/or package
        this.sclTreePanel.clear();

        //get the selected package from the list-box
        String selectedPackageUUID = lstPackage.getValue( lstPackage.getSelectedIndex() );

        //get the selected format from the list-box
        List<String> selectedFormats = new ArrayList<String>();
        selectedFormats.add( lstFormat.getValue( lstFormat.getSelectedIndex() ) );

        this.assetsTable = new AssetPagedTable( selectedPackageUUID,
                                                selectedFormats,
                                                null,
                                                this.clientFactory );

        this.sclTreePanel.add( this.assetsTable );

        this.makeDirty();
    }

    /**
     * Handles {@link #lstFormat} and {@link #lstPackage} changes reloading the
     * asset's table
     * 
     * @param event
     */
    private void handleListChanges(ChangeEvent event) {
        this.loadAssetTable();
    }

    private void populatePackageList(Module packageConfigData) {
        this.lstPackage.addItem( packageConfigData.getName(),
                                 packageConfigData.getUuid() );
        this.lstPackage.setSelectedIndex( 0 );
    }

    public void getResourceElement(final ResourceElementReadyCommand resourceElementReadyCommand) {
        //source is mandatory!
        final String[] selectedRowUUIDs = this.assetsTable.getSelectedRowUUIDs();
        if ( selectedRowUUIDs == null || selectedRowUUIDs.length == 0 ) {
            throw new IllegalStateException( constants.NoPackageSeleced() );
        }

        //load asset information
        this.assetService.loadRuleAssets( selectedRowUUIDs,
                                          new AsyncCallback<Asset[]>() {

                                              public void onFailure(Throwable caught) {
                                                  resourceElementReadyCommand.onFailure( caught );
                                              }

                                              public void onSuccess(Asset[] assets) {
                                                  //for each selcted resource we are going to add a xml entry
                                                  String result = "";
                                                  int i = 1;
                                                  for ( Asset asset : assets ) {
                                                      String partialResult = resourceXMLElementTemplate;

                                                      String nameString = "";
                                                      if ( txtName.getText().length() != 0 ) {
                                                          if ( selectedRowUUIDs.length == 1 ) {
                                                              nameString = "name=\"" + txtName.getText().trim() + "\"";
                                                          } else {
                                                              //add index to the name to avoid duplication
                                                              nameString = "name=\"" + txtName.getText().trim() + i + "\"";
                                                          }
                                                      }
                                                      partialResult = partialResult.replace( "{name}",
                                                                                             nameString );

                                                      String descriptionString = "";
                                                      if ( txtDescription.getText().length() != 0 ) {
                                                          descriptionString = "description=\"" + txtDescription.getText().trim() + "\"";
                                                      }
                                                      partialResult = partialResult.replace( "{description}",
                                                                                             descriptionString );

                                                      String format = lstFormat.getValue( lstFormat.getSelectedIndex() );
                                                      String type = convertFromAssetFormatToResourceType( format );
                                                      if ( type == null ) {
                                                          throw new IllegalArgumentException( constants.UnknownResourceFormat( format ) );
                                                      }

                                                      partialResult = partialResult.replace( "{type}",
                                                                                             type );

                                                      partialResult = partialResult.replace( "{source}",
                                                                                             getDownloadLink( asset.name ) );

                                                      result += partialResult + "\n";
                                                      i++;
                                                  }

                                                  resourceElementReadyCommand.onSuccess( result );
                                              }
                                          } );

    }

    private String convertFromAssetFormatToResourceType(String format) {
        if ( format.equals( AssetFormats.BUSINESS_RULE )
                || format.equals( AssetFormats.DRL )
                || format.equals( AssetFormats.DECISION_TABLE_GUIDED )
                || format.equals( AssetFormats.RULE_TEMPLATE ) ) {
            return "DRL";
        } else if ( format.equals( AssetFormats.DSL ) ) {
            return "DSL";
        } else if ( format.equals( AssetFormats.BPMN2_PROCESS ) ) {
            return "BPMN2";
        } else if ( format.equals( AssetFormats.CHANGE_SET ) ) {
            return "CHANGE_SET";
        }

        return null;
    }

    private String getDownloadLink(String assetName) {
        String url = RESTUtil.getRESTBaseURL();
        url += "packages/";
        url += this.lstPackage.getItemText( this.lstPackage.getSelectedIndex() );
        url += "/assets/";
        url += assetName;
        url += "/source";

        return url;
    }
}
