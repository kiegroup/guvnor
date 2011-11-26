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
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.MultiAssetPlace;
import org.drools.guvnor.client.explorer.RefreshModuleDataModelEvent;
import org.drools.guvnor.client.explorer.RefreshModuleEditorEvent;
import org.drools.guvnor.client.explorer.RefreshSuggestionCompletionEngineEvent;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.util.LazyStackPanel;
import org.drools.guvnor.client.util.LoadContentCommand;
import org.drools.guvnor.client.widgets.CheckinPopup;
import org.drools.guvnor.client.widgets.toolbar.ActionToolbarButtonsConfigurationProvider;

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
    	eventBus.fireEvent(new RefreshModuleDataModelEvent(asset.getMetaData().getPackageName(),
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
                                ruleViewerSettings );
                        //ruleViewer.setDocoVisible( showDescription );
                        //ruleViewer.setMetaVisible( showMetadata );

                        content.add( ruleViewer );
                        ruleViewer.setWidth( "100%" );
                        ruleViewer.setHeight( "100%" );
                        ruleViews.put( row.getUuid(),
                                ruleViewer );

                    }
                } ));
    }

    public void checkin(final boolean closeAfter) {
        final CheckinPopup pop = new CheckinPopup( constants.CheckInChanges() );
        pop.setCommand( new Command() {

            public void execute() {
                String comment = pop.getCheckinComment();
                for (RuleViewer ruleViewer : ruleViews.values()) {
                	 doCheckin(ruleViewer.getAssetEditor(), ruleViewer.asset, comment, false );
                }
                if ( closeAfter ) {
                    close();
                }
            }
        } );
        pop.show();

    }

	public void doCheckin(Widget editor, RuleAsset asset, String comment, boolean closeAfter) {
		if (editor instanceof SaveEventListener) {
			((SaveEventListener) editor).onSave();
		}
		performCheckIn(comment, closeAfter, asset);
		if (editor instanceof SaveEventListener) {
			((SaveEventListener) editor).onAfterSave();
		}

		eventBus.fireEvent(new RefreshModuleEditorEvent(asset.getMetaData()
				.getPackageUUID()));
		// lastSaved = System.currentTimeMillis();
		// resetDirty();
	}

    private void performCheckIn(String comment,
                                final boolean closeAfter, final RuleAsset asset) {
        asset.setCheckinComment( comment );
        final boolean[] saved = {false};

        if ( !saved[0] ) LoadingPopup.showMessage( constants.SavingPleaseWait() );
        RepositoryServiceFactory.getAssetService().checkinVersion( asset,
                new GenericCallback<String>() {

                    public void onSuccess(String uuid) {
                        if ( uuid == null ) {
                            ErrorPopup.showMessage( constants.FailedToCheckInTheItemPleaseContactYourSystemAdministrator() );
                            return;
                        }

                        if ( uuid.startsWith( "ERR" ) ) { // NON-NLS
                            ErrorPopup.showMessage( uuid.substring( 5 ) );
                            return;
                        }

                        flushSuggestionCompletionCache(asset.getMetaData().getPackageName(), asset);
/*                        if ( editor instanceof DirtyableComposite ) {
                            ((DirtyableComposite) editor).resetDirty();
                        }*/

                        LoadingPopup.close();
                        saved[0] = true;

                        //showInfoMessage( constants.SavedOK() );
                        if ( !closeAfter ) {
                            eventBus.fireEvent( new RefreshAssetEditorEvent( uuid ) );
                        }
                    }
                } );
    }
    
    /**
     * In some cases we will want to flush the package dependency stuff for
     * suggestion completions. The user will still need to reload the asset
     * editor though.
     */
    public void flushSuggestionCompletionCache(final String packageName, RuleAsset asset) {
        if ( AssetFormats.isPackageDependency( asset.getFormat() ) ) {
            LoadingPopup.showMessage( constants.RefreshingContentAssistance() );
            eventBus.fireEvent(new RefreshModuleDataModelEvent(packageName,
                    new Command() {
                        public void execute() {
                            //Some assets depend on the SuggestionCompletionEngine. This event is to notify them that the 
                            //SuggestionCompletionEngine has been changed, they need to refresh their UI to represent the changes.
                            eventBus.fireEvent(new RefreshSuggestionCompletionEngineEvent(packageName));
                            LoadingPopup.close();
                        }
                    }));
        }
    }
    public void close() {
        eventBus.fireEvent(new ClosePlaceEvent(new MultiAssetPlace(rows)));
        if ( closeCommand != null ) {
            closeCommand.execute();
        }
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void makeDirty() {
    }

    @Override
    public void resetDirty() {
    }

    public void setCloseCommand(Command command) {
        closeCommand = command;
    }
}
