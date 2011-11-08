/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.common;

import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.TableDataResult;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A rule package selector widget.
 */
public class GlobalAreaAssetSelector extends Composite {

    /**
     * Used to remember what the "current asset" we are working in is... Should
     * be the one the user has most recently dealt with...
     */
    public static String currentlySelectedAsset;

    private ListBox      assetList;
    private String[]     formats;

    public GlobalAreaAssetSelector(String formatToImport) {
        assetList = new ListBox();
        //Null format implies the following... <sigh>
        if ( formatToImport == null ) {
            this.formats = new String[]{AssetFormats.BUSINESS_RULE, 
                                        AssetFormats.DSL_TEMPLATE_RULE, 
                                        AssetFormats.DRL, 
                                        AssetFormats.DECISION_SPREADSHEET_XLS, 
                                        AssetFormats.DECISION_TABLE_GUIDED};
        } else {
            this.formats = new String[]{formatToImport};
        }

        Scheduler.get().scheduleDeferred( new Command() {
            public void execute() {
                loadAssetList();
            }
        } );

        initWidget( assetList );
    }

    private void loadAssetList() {
        RepositoryServiceFactory.getAssetService().listAssetsWithPackageName( "globalArea",
                                                                              formats,
                                                                              0,
                                                                              -1,
                                                                              ExplorerNodeConfig.RULE_LIST_TABLE_ID,
                                                                              new GenericCallback<TableDataResult>() {

                                                                                  public void onSuccess(TableDataResult result) {

                                                                                      for ( int i = 0; i < result.data.length; i++ ) {
                                                                                          assetList.addItem( result.data[i].getDisplayName(),
                                                                                                             result.data[i].id );
                                                                                          if ( currentlySelectedAsset != null &&
                                                                                               result.data[i].equals( currentlySelectedAsset ) ) {
                                                                                              assetList.setSelectedIndex( i );
                                                                                          }
                                                                                      }

                                                                                      assetList.addChangeHandler( new ChangeHandler() {
                                                                                          public void onChange(ChangeEvent sender) {
                                                                                              currentlySelectedAsset = getSelectedAsset();
                                                                                          }
                                                                                      } );
                                                                                  }

                                                                                  public void onFailure(Throwable t) {
                                                                                      if ( t.getMessage().indexOf( "AuthorizationException" ) > -1 ) {
                                                                                          //Do nothing, just leave asset list empty.
                                                                                          //Window.alert( "No permission to access global area" );
                                                                                      } else {
                                                                                          super.onFailure( t );
                                                                                      }
                                                                                  }

                                                                              } );
    }

    /**
     * Returns the selected package.
     */
    public String getSelectedAsset() {
        return assetList.getItemText( assetList.getSelectedIndex() );
    }
}
