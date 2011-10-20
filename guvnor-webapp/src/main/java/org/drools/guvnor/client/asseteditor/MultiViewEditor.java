/*
 * Copyright 2010 JBoss Inc
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
package org.drools.guvnor.client.asseteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.MultiAssetPlace;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.client.util.LazyStackPanel;
import org.drools.guvnor.client.util.LoadContentCommand;
import org.drools.guvnor.client.widgets.CheckinPopup;

import java.util.*;

public class MultiViewEditor extends GuvnorEditor {

    private Constants constants = GWT.create( Constants.class );

    private final ClientFactory clientFactory;
    private VerticalPanel viewsPanel = new VerticalPanel();
    private Command closeCommand;
    private final Set<MultiViewRow> rows = new HashSet<MultiViewRow>();
    private Map<String, RuleViewer> ruleViews = new HashMap<String, RuleViewer>();
    private ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider;

    private Map<String, RuleAsset> assets = new HashMap<String, RuleAsset>();

    private MultiViewEditorMenuBarCreator menuBarCreator;
    private final EventBus eventBus;

    public MultiViewEditor(MultiViewRow[] rows,
                           ClientFactory clientFactory,
                           EventBus eventBus) {
        this( rows,
                clientFactory,
                eventBus,
                null );
    }

    public MultiViewEditor(MultiViewRow[] rows,
                           ClientFactory clientFactory,
                           EventBus eventBus,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this( Arrays.asList( rows ),
                clientFactory,
                eventBus,
                individualActionToolbarButtonsConfigurationProvider );
    }

    public MultiViewEditor(List<MultiViewRow> rows,
                           ClientFactory clientFactory,
                           EventBus eventBus,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this.rows.addAll( rows );
        this.individualActionToolbarButtonsConfigurationProvider = individualActionToolbarButtonsConfigurationProvider;
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;

        init();
    }

    public MultiViewEditor(RuleAsset[] assets,
                           ClientFactory clientFactory,
                           EventBus eventBus,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this( assets,
                clientFactory,
                eventBus,
                individualActionToolbarButtonsConfigurationProvider,
                null );
    }

    public MultiViewEditor(RuleAsset[] assets,
                           ClientFactory clientFactory,
                           EventBus eventBus,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider,
                           MultiViewEditorMenuBarCreator menuBarCreator) {
        this.rows.addAll( createRows( assets ) );
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.individualActionToolbarButtonsConfigurationProvider = individualActionToolbarButtonsConfigurationProvider;
        this.menuBarCreator = menuBarCreator;
        addAssets( assets );
        init();
    }

    private void addAssets(RuleAsset[] assets) {
        for (RuleAsset ruleAsset : assets) {
            this.assets.put( ruleAsset.getUuid(),
                    ruleAsset );
        }
    }

    private static List<MultiViewRow> createRows(RuleAsset[] assets) {
        List<MultiViewRow> rows = new ArrayList<MultiViewRow>();
        for (RuleAsset ruleAsset : assets) {
            MultiViewRow row = new MultiViewRow(
                    ruleAsset.getUuid(),
                    ruleAsset.getName(),
                    AssetFormats.BUSINESS_RULE );
            rows.add( row );
        }
        return rows;
    }

    private void init() {
        VerticalPanel rootPanel = new VerticalPanel();

        rootPanel.setWidth( "100%" );

        rootPanel.add( createToolbar() );

        viewsPanel.setWidth( "100%" );
        rootPanel.add( viewsPanel );

        doViews();

        initWidget( rootPanel );
    }

    private MenuBar createToolbar() {

        //if no MultiViewEditorMenuBarCreator is set, then use the Default
        //implementation.
        if ( this.menuBarCreator == null ) {
            this.menuBarCreator = new DefaultMultiViewEditorMenuBarCreator();
        }

        return this.menuBarCreator.createMenuBar( this );
    }

    private void doViews() {

        viewsPanel.clear();
        ruleViews.clear();
        final LazyStackPanel panel = new LazyStackPanel();

        //the first row will be expanded
        int rowNumber = 1;
        for (final MultiViewRow row : rows) {

            panel.add( row.getName(),
                    new LoadContentCommand() {

                        public Widget load() {
                            final SimplePanel content = new SimplePanel();

                            if ( assets.containsKey( row.getUuid() ) ) {
                                addRuleViewInToSimplePanel( row,
                                        content,
                                        assets.get( row.getUuid() ) );
                            } else {
                                RepositoryServiceFactory.getAssetService().loadRuleAsset( row.getUuid(),
                                        new GenericCallback<RuleAsset>() {

                                            public void onSuccess(final RuleAsset asset) {
                                                assets.put( asset.getUuid(),
                                                        asset );

                                                addRuleViewInToSimplePanel( row,
                                                        content,
                                                        asset );
                                            }

                                        } );

                            }
                            return content;
                        }
                    },
                    rowNumber == 1 );

            rowNumber++;
        }

        viewsPanel.add( panel );

    }

    private void addRuleViewInToSimplePanel(final MultiViewRow row,
                                            final SimplePanel content,
                                            final RuleAsset asset) {
        SuggestionCompletionCache.getInstance().doAction( asset.getMetaData().getPackageName(),
                new Command() {

                    public void execute() {

                        RuleViewerSettings ruleViewerSettings = new RuleViewerSettings();
                        ruleViewerSettings.setDocoVisible( false );
                        ruleViewerSettings.setMetaVisible( false );
                        ruleViewerSettings.setStandalone( true );
                        Command closeCommand = new Command() {
                            public void execute() {
                                // TODO: No handle for this -Rikkola-
                                ruleViews.remove( row.getUuid() );
                                rows.remove( row );
                                doViews();
                            }
                        };
                        final RuleViewer ruleViewer = new RuleViewer( asset,
                                clientFactory,
                                eventBus,
                                false,
                                individualActionToolbarButtonsConfigurationProvider,
                                ruleViewerSettings );
                        //ruleViewer.setDocoVisible( showDescription );
                        //ruleViewer.setMetaVisible( showMetadata );

                        content.add( ruleViewer );
                        ruleViewer.setWidth( "100%" );
                        ruleViewer.setHeight( "100%" );
                        ruleViews.put( row.getUuid(),
                                ruleViewer );

                    }
                } );
    }

    public void checkin(final boolean closeAfter) {
        final CheckinPopup pop = new CheckinPopup( constants.CheckInChanges() );
        pop.setCommand( new Command() {

            public void execute() {
                String comment = pop.getCheckinComment();
                for (RuleViewer ruleViewer : ruleViews.values()) {
                    ruleViewer.doCheckin( comment, false );
                }
                if ( closeAfter ) {
                    close();
                }
            }
        } );
        pop.show();

    }

    public void close() {
        eventBus.fireEvent(new ClosePlaceEvent(new MultiAssetPlace(rows)));
        if ( closeCommand != null ) {
            closeCommand.execute();
        }
    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void makeDirty() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resetDirty() {
        // TODO Auto-generated method stub
    }

    public void setCloseCommand(Command command) {
        closeCommand = command;
    }
}
