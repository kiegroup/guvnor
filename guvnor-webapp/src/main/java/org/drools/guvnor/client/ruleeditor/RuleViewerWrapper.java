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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.ArtifactEditor;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbar;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbarButtonsConfigurationProvider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The main layout parent/controller the rule viewer.
 */
public class RuleViewerWrapper extends GuvnorEditor
    implements
    RefreshAssetEditorEvent.Handler,
    ShowMessageEvent.Handler {

    private Constants                         constants            = GWT.create( Constants.class );

    private RuleAsset                         asset;
    private boolean                           isHistoricalReadOnly = false;
    private final RuleViewerSettings          ruleViewerSettings;
    private final MessageWidget               messageWidget        = new MessageWidget();

    ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider;

    VerticalPanel                             layout               = new VerticalPanel();
    private final ClientFactory               clientFactory;
    private final EventBus                    eventBus;

    public RuleViewerWrapper(ClientFactory clientFactory,
                             EventBus eventBus,
                             RuleAsset asset) {
        this( clientFactory,
                eventBus,
                asset,
                false,
                null );
    }

    public RuleViewerWrapper(ClientFactory clientFactory,
                             EventBus eventBus,
                             final RuleAsset asset,
                             boolean isHistoricalReadOnly,
                             RuleViewerSettings ruleViewerSettings) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.asset = asset;
        this.isHistoricalReadOnly = isHistoricalReadOnly;
        this.ruleViewerSettings = ruleViewerSettings;

        //Wire-up event handlers
        eventBus.addHandler( RefreshAssetEditorEvent.TYPE,
                             this );
        eventBus.addHandler( ShowMessageEvent.TYPE,
                             this );

        initWidget( layout );
        setWidth( "100%" );

        render();
    }

    private void render() {
        ArtifactEditor artifactEditor = new ArtifactEditor(
                                                            clientFactory,
                                                            eventBus,
                                                            asset,
                                                            this.isHistoricalReadOnly );

        RuleViewer ruleViewer = new RuleViewer(
                                                asset,
                                                clientFactory,
                                                eventBus,
                                                this.isHistoricalReadOnly,
                                                actionToolbarButtonsConfigurationProvider,
                                                ruleViewerSettings );
        ActionToolbar actionToolBar = ruleViewer.getActionToolbar();

        layout.clear();
        layout.add( actionToolBar );
        layout.add( messageWidget );

        TabPanel tabPanel = new TabPanel();
        tabPanel.setWidth( "100%" );

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add( artifactEditor );
        tabPanel.add( scrollPanel,
                      constants.Attributes() );

        scrollPanel = new ScrollPanel();
        scrollPanel.add( ruleViewer );
        tabPanel.add( scrollPanel,
                      constants.Edit() );
        tabPanel.selectTab( 1 );

        layout.add( tabPanel );
    }

    public void onRefreshAsset(RefreshAssetEditorEvent refreshAssetEditorEvent) {
    	//AssetUUID == null means to refresh all asset editors contained by the specified package. 
        if ((refreshAssetEditorEvent.getAssetUUID() == null && asset.getMetaData().getPackageName().equals(refreshAssetEditorEvent.getPackageName())) || asset.getUuid().equals( refreshAssetEditorEvent.getAssetUUID() ) ) {
            LoadingPopup.showMessage( constants.RefreshingItem() );

            //When refreshing the Asset ensure the SuggestionCompletionEngine has been cached.
            //RefreshAssetEditorEvents are fired after an Asset has been moved to a different
            //Module. Such an operation flushes the SuggestionCompletionCache of engines
            //for both the 'source' and 'target' Modules (as presumably the Asset could
            //be a 'Model').
            RepositoryServiceFactory.getAssetService().loadRuleAsset( asset.getUuid(),
                                                                      new GenericCallback<RuleAsset>() {
                                                                          public void onSuccess(final RuleAsset a) {
                                                                              final String packageName = a.getMetaData().packageName;
                                                                              SuggestionCompletionCache.getInstance().doAction( packageName,
                                                                                                                                new Command() {

                                                                                                                                    public void execute() {
                                                                                                                                        asset = a;
                                                                                                                                        render();
                                                                                                                                        LoadingPopup.close();
                                                                                                                                    }

                                                                                                                                } );

                                                                          }
                                                                      } );
        }
    }

    public void onShowMessage(ShowMessageEvent event) {
        messageWidget.showMessage( event.getMessage(),
                                   event.getMessageType() );
    }

}
