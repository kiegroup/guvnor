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

package org.drools.guvnor.client.ruleeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget shows a list of versions.
 *
 */
public class VersionChooser extends Composite {

    private Constants     constants = GWT.create( Constants.class );
    private static Images images    = GWT.create( Images.class );

    private String        packageUUID;
    private String        assetName;
    private Command       refreshCommand;
    private ListBox versionChooser;
    private List<TableDataRow> versionInfo = new ArrayList<TableDataRow>();
    private String currentVersion;

   
    public VersionChooser(String currentVersion,
    		String pacakgeUUID, 
    		String assetName,
            Command ref) {
        this.packageUUID = pacakgeUUID;
        this.assetName = assetName;
        this.refreshCommand = ref;
        this.currentVersion = currentVersion;

        VerticalPanel verticalPanel = new VerticalPanel();
        versionChooser = new ListBox();
        loadHistoryData();
        verticalPanel.add(versionChooser);

        Button open = new Button( "View selected version" );
        open.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                showVersion( versionChooser.getValue( versionChooser.getSelectedIndex() ) );
            }

        } );
        verticalPanel.add(open);       

        initWidget( verticalPanel );
    }

    /**
     * Actually load the history data, as demanded.
     */
    protected void loadHistoryData() {
        RepositoryServiceFactory.getAssetService().loadAssetHistory( packageUUID,
        		                                                assetName,
                                                                new GenericCallback<TableDataResult>() {
                                                                    public void onSuccess(TableDataResult table) {
                                                                        if ( table == null ) {
                                                                            //Should not get here
                                                                            return;
                                                                        }
                                                                        TableDataRow[] rows = table.data;
                                                                        Arrays.sort( rows,
                                                                                     new Comparator<TableDataRow>() {
                                                                                         public int compare(TableDataRow r1,
                                                                                                            TableDataRow r2) {
                                                                                             if("LATEST".equals(r2.values[0])) {
                                                                                                 return 1;
                                                                                             }
                                                                                             if("LATEST".equals(r1.values[0])) {
                                                                                                 return -1;
                                                                                             }                                                                                             
                                                                                             Integer v2 = Integer.valueOf( r2.values[0] );
                                                                                             Integer v1 = Integer.valueOf( r1.values[0] );

                                                                                             return v2.compareTo( v1 );
                                                                                         }
                                                                                     } );

                                                                        for ( int i = 0; i < rows.length; i++ ) {
                                                                            TableDataRow row = rows[i];                                                     
                                                                            
                                                                            String s = Format.format( constants.property0ModifiedOn12(),
                                                                                                      row.values[0],
                                                                                                      row.values[2],
                                                                                                      row.values[1] );
                                                                            versionChooser.addItem( s,
                                                                                    row.id );
                                                                            versionInfo.add(row);
                                                                        }
                                                                        selectCurrentVersoin(currentVersion);
                                                                    }
                                                                } );

    }
    
    public String getSelectedVersionUUID() {
    	if(versionChooser != null) {
    	    return versionChooser.getValue(versionChooser.getSelectedIndex());
    	} else {
    		return null;
    	}
    }
    
    private void selectCurrentVersoin(String currentVersion) {
        for(int i=0;i<versionInfo.size(); i++) {
            TableDataRow row = versionInfo.get(i);
            if(row.values[0].equals(currentVersion)) {
                versionChooser.setSelectedIndex(i);
                break;
            }
        }
    }
    
    public String getSelectedVersionName() {
        if(versionChooser != null) {
            return versionInfo.get(versionChooser.getSelectedIndex()).values[0];
        } else {
            return null;
        }
    }

    /**
     * This should popup a view of the chosen historical version.
     */
    private void showVersion(final String versionUUID) {
        LoadingPopup.showMessage( constants.LoadingVersionFromHistory() );

        RepositoryServiceFactory.getAssetService().loadRuleAsset( versionUUID,
                                                             new GenericCallback<RuleAsset>() {

                                                                 public void onSuccess(RuleAsset asset) {
                                                                     asset.isreadonly = true;
                                                                     final FormStylePopup pop = new FormStylePopup( images.snapshot(),
                                                                                                                    Format.format( constants.VersionNumber0Of1(),
                                                                                                                                   "" + asset.metaData.versionNumber,
                                                                                                                                   asset.metaData.name ),
                                                                                                                    new Integer( 800 ) );

                                                                     RuleViewer viewer = new RuleViewer( asset,
                                                                                                         null,
                                                                                                         true );
                                                                     viewer.setWidth( "100%" );
                                                                     viewer.setHeight( "100%" );

                                                                     pop.addRow( viewer );
                                                                     pop.show();
                                                                 }
                                                             } );
    }

}
