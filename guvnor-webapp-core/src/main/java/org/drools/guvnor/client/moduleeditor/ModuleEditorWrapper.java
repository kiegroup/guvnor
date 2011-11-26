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

package org.drools.guvnor.client.moduleeditor;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.RefreshModuleEditorEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

/**
 * This is the module editor.
 */
public class ModuleEditorWrapper extends Composite {
    private Constants constants = GWT.create(Constants.class);

    private PackageConfigData packageConfigData;
    private boolean isHistoricalReadOnly = false;

    VerticalPanel layout = new VerticalPanel();
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public ModuleEditorWrapper(PackageConfigData data,
                               ClientFactory clientFactory,
                               EventBus eventBus) {
        this(data, clientFactory, eventBus, false);
    }

    public ModuleEditorWrapper(PackageConfigData data,
                               ClientFactory clientFactory,
                               EventBus eventBus,
                               boolean isHistoricalReadOnly) {
        this.packageConfigData = data;
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.isHistoricalReadOnly = isHistoricalReadOnly;

        initWidget(layout);        
        setRefreshHandler();
        render();
        setWidth("100%");
    }

    private void render() {
        final TabPanel tPanel = new TabPanel();
        tPanel.setWidth("100%");

        ArtifactEditor artifactEditor = new ArtifactEditor(packageConfigData, clientFactory, eventBus, this.isHistoricalReadOnly);

        Command refreshCommand = new Command() {
            public void execute() {
                refresh();
            }
        };        
        AbstractModuleEditor moduleEditor = clientFactory.getPerspectiveFactory().getModuleEditor(packageConfigData, clientFactory, eventBus, this.isHistoricalReadOnly, refreshCommand);
        
        layout.clear();
        
        Widget actionToolBar = clientFactory.getPerspectiveFactory().getModuleEditorActionToolbar(packageConfigData, clientFactory, eventBus, this.isHistoricalReadOnly, refreshCommand );
        layout.add(actionToolBar);

        AssetViewerActivity assetViewerActivity = new AssetViewerActivity(packageConfigData.uuid,
                clientFactory);
        assetViewerActivity.start(new AcceptItem() {
                    public void add(String tabTitle, IsWidget widget) {
                        ScrollPanel pnl = new ScrollPanel();
                        pnl.setWidth("100%");
                        pnl.add(widget);
                        tPanel.add(pnl, constants.Assets());
                    }
                }, null);

        ScrollPanel pnl = new ScrollPanel();
        pnl.setWidth("100%");
        pnl.add(artifactEditor);
        tPanel.add(pnl, constants.AttributeForModuleEditor());
        tPanel.selectTab(0);

        pnl = new ScrollPanel();
        pnl.setWidth("100%");
        pnl.add(moduleEditor);
        tPanel.add(pnl, constants.Edit());
        tPanel.selectTab(0);

        tPanel.setHeight("100%");
        layout.add(tPanel);
        layout.setHeight("100%");
    }
    
    /**
     * Will refresh all the data.
     */
    public void refresh() {
        LoadingPopup.showMessage(constants.RefreshingPackageData());
        RepositoryServiceFactory.getPackageService().loadPackageConfig(this.packageConfigData.getUuid(),
                new GenericCallback<PackageConfigData>() {
                    public void onSuccess(PackageConfigData data) {
                        LoadingPopup.close();
                        packageConfigData = data;
                        render();
                    }
                });
    }
    
    private void setRefreshHandler() {
        eventBus.addHandler(RefreshModuleEditorEvent.TYPE,
                new RefreshModuleEditorEvent.Handler() {
                    public void onRefreshModule(
                            RefreshModuleEditorEvent refreshModuleEditorEvent) {
                        String moduleUUID = refreshModuleEditorEvent.getUuid();
                        if(moduleUUID!=null && moduleUUID.equals(packageConfigData.getUuid())) {
                            refresh();                                
                        }
                    
                    }
                });
    }
}
