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

package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.ArtifactEditor;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbar;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbarButtonsConfigurationProvider;

/**
 * The main layout parent/controller the rule viewer.
 */
public class RuleViewerWrapper extends GuvnorEditor {
    private Constants constants = GWT.create(Constants.class);

    private RuleAsset asset;
    private boolean isHistoricalReadOnly = false;
    private final RuleViewerSettings ruleViewerSettings;

    ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider;

    VerticalPanel layout = new VerticalPanel();
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public RuleViewerWrapper(ClientFactory clientFactory,
                             EventBus eventBus,
                             RuleAsset asset) {
        this(clientFactory,
                eventBus,
                asset,
                false,
                null,
                null);
    }

    public RuleViewerWrapper(ClientFactory clientFactory,
                             EventBus eventBus,
                             final RuleAsset asset,
                             boolean isHistoricalReadOnly,
                             ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider,
                             RuleViewerSettings ruleViewerSettings) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.asset = asset;
        this.isHistoricalReadOnly = isHistoricalReadOnly;
        this.ruleViewerSettings = ruleViewerSettings;

        eventBus.addHandler(
                RefreshAssetEditorEvent.TYPE,
                new RefreshAssetEditorEvent.Handler() {
                    public void onRefreshAsset(RefreshAssetEditorEvent refreshAssetEditorEvent) {
                        if (refreshAssetEditorEvent.getUuid().equals(asset.getUuid())) {
                            refresh();
                        }
                    }
                });

        initWidget(layout);
        render();
        setWidth("100%");
    }

    private void render() {
        ArtifactEditor artifactEditor = new ArtifactEditor(
                clientFactory,
                eventBus,
                asset,
                this.isHistoricalReadOnly);

        RuleViewer ruleViewer = new RuleViewer(
                asset,
                clientFactory,
                eventBus,
                this.isHistoricalReadOnly,
                actionToolbarButtonsConfigurationProvider,
                ruleViewerSettings);
        ActionToolbar actionToolBar = ruleViewer.getActionToolbar();

        layout.clear();
        layout.add(actionToolBar);

        TabPanel tPanel = new TabPanel();
        tPanel.setWidth("100%");

        ScrollPanel pnl = new ScrollPanel();
        pnl.add(artifactEditor);
        tPanel.add(pnl,
                "Attributes");
        // tPanel.selectTab(0);

        pnl = new ScrollPanel();
        // pnl1.setWidth("100%");
        pnl.add(ruleViewer);
        tPanel.add(pnl,
                "Edit");
        tPanel.selectTab(1);

        layout.add(tPanel);
    }

    public void refresh() {
        LoadingPopup.showMessage(constants.RefreshingItem());
        RepositoryServiceFactory.getAssetService().loadRuleAsset(asset.getUuid(),
                new GenericCallback<RuleAsset>() {
                    public void onSuccess(RuleAsset a) {
                        asset = a;
                        render();
                        LoadingPopup.close();
                    }
                });
    }
}
