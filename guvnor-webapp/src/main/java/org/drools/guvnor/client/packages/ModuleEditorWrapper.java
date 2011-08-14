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

package org.drools.guvnor.client.packages;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.AcceptTabItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbar;
import org.drools.guvnor.client.widgets.assetviewer.AssetViewerActivity;

/**
 * This is the module editor.
 */
public class ModuleEditorWrapper extends Composite {
    private Constants constants = GWT.create( Constants.class );

    private ArtifactEditor artifactEditor;
    private AbstractModuleEditor moduleEditor;
    private ActionToolbar actionToolBar;
    private PackageConfigData packageConfigData;
    private boolean isHistoricalReadOnly = false;

    VerticalPanel layout = new VerticalPanel();
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public ModuleEditorWrapper( PackageConfigData data,
                                 ClientFactory clientFactory,
                                 EventBus eventBus) {
        this( data, clientFactory, eventBus, false );
    }

    public ModuleEditorWrapper( PackageConfigData data,
                                 ClientFactory clientFactory,
                                 EventBus eventBus,
                                 boolean isHistoricalReadOnly ) {
        this.packageConfigData = data;
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.isHistoricalReadOnly = isHistoricalReadOnly;

        initWidget( layout );
        render();
        setWidth( "100%" );
    }

    private void render() {
        final TabPanel tPanel = new TabPanel();
        tPanel.setWidth( "100%" );

        this.artifactEditor = new ArtifactEditor( clientFactory, eventBus, packageConfigData, this.isHistoricalReadOnly );
/*        this.moduleEditor = new PackageEditor(
                packageConfigData,
                clientFactory,
                eventBus,
                this.isHistoricalReadOnly,
                new Command() {
                    public void execute() {
                        refresh();
                    }
                } );   */     
        this.moduleEditor = clientFactory.getModuleEditor(packageConfigData, clientFactory, eventBus, this.isHistoricalReadOnly, new Command() {
            public void execute() {
                refresh();
            }
        } );
        
        this.actionToolBar = this.moduleEditor.getActionToolbar();    
        layout.clear();
        layout.add( this.actionToolBar );       
        
        AssetViewerActivity assetViewerActivity = new AssetViewerActivity(packageConfigData.uuid,
                clientFactory);
        assetViewerActivity.start(new AcceptTabItem() {
            public void addTab(String tabTitle, IsWidget widget) {                
                ScrollPanel pnl = new ScrollPanel();
                pnl.setWidth( "100%" );
                pnl.add( widget );                
                tPanel.add(pnl, constants.Assets());
            }
        }, null);
 
        ScrollPanel pnl = new ScrollPanel();
        pnl.setWidth( "100%" );
        pnl.add( this.artifactEditor );
        tPanel.add( pnl, constants.AttributeForModuleEditor() );
        tPanel.selectTab( 0 );        
          
        pnl = new ScrollPanel();
        pnl.setWidth( "100%" );
        pnl.add( this.moduleEditor );
        tPanel.add( pnl, constants.Edit() );
        tPanel.selectTab( 0 );

        tPanel.setHeight( "100%" );
        layout.add( tPanel );
        layout.setHeight( "100%" );
    }

    /**
     * Will refresh all the data.
     */
    public void refresh() {
        LoadingPopup.showMessage( constants.RefreshingPackageData() );
        RepositoryServiceFactory.getPackageService().loadPackageConfig( this.packageConfigData.getUuid(),
                new GenericCallback<PackageConfigData>() {
                    public void onSuccess( PackageConfigData data ) {
                        LoadingPopup.close();
                        packageConfigData = data;
                        render();
                    }
                } );
    }
}
