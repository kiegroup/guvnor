/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.asseteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.ArtifactEditor;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.Asset;

/**
 * The main layout parent/controller the rule viewer.
 */
public class RuleViewerWrapper extends GuvnorEditor implements RefreshAssetEditorEvent.Handler  {
    private Constants constants = GWT.create(Constants.class);

    private Asset asset;
    private boolean isHistoricalReadOnly = false;
    private RuleViewerSettings ruleViewerSettings = null;

    VerticalPanel layout = new VerticalPanel();
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public RuleViewerWrapper(ClientFactory clientFactory,
                             EventBus eventBus,
                             Asset asset) {
        this(clientFactory,
                eventBus,
                asset,
                false);
    }

    public RuleViewerWrapper(ClientFactory clientFactory,
                             EventBus eventBus,
                             final Asset asset,
                             boolean isHistoricalReadOnly) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.asset = asset;
        this.isHistoricalReadOnly = isHistoricalReadOnly;
        
        eventBus.addHandler(
                RefreshAssetEditorEvent.TYPE,
                this);

        initWidget(layout);
        render();
        setWidth("100%");
    }

    private void render() {
        ArtifactEditor artifactEditor = new ArtifactEditor(
                asset,                
                clientFactory,
                eventBus,
                this.isHistoricalReadOnly);

        RuleViewer ruleViewer = new RuleViewer(
                asset,
                clientFactory,
                eventBus,
                ruleViewerSettings);
        
        boolean readOnly = isHistoricalReadOnly || asset.isReadonly() || (this.ruleViewerSettings !=null && this.ruleViewerSettings.isStandalone());
        Widget actionToolBar = clientFactory.getPerspectiveFactory().getAssetEditorActionToolbar("author", asset, ruleViewer.getAssetEditor(), clientFactory, eventBus, readOnly);

        layout.clear();
        layout.add(actionToolBar);

        TabPanel tabPanel = new TabPanel();
        tabPanel.setWidth("100%");

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(artifactEditor);
        tabPanel.add(scrollPanel, constants.Attributes());

        scrollPanel = new ScrollPanel();
        scrollPanel.add(ruleViewer);
        tabPanel.add(scrollPanel, constants.Edit());
        tabPanel.selectTab(1);

        layout.add(tabPanel);
    }

    public void onRefreshAsset(RefreshAssetEditorEvent refreshAssetEditorEvent) {
        if (refreshAssetEditorEvent.getUuid().equals(asset.getUuid())) {
            LoadingPopup.showMessage(constants.RefreshingItem());
            RepositoryServiceFactory.getAssetService().loadRuleAsset(asset.getUuid(),
                    new GenericCallback<Asset>() {
                        public void onSuccess(Asset a) {
                            asset = a;
                            render();
                            LoadingPopup.close();
                        }
                    });
        }
    }
}
