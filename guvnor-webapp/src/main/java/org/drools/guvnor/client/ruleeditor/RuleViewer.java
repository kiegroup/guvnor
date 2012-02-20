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

import java.util.Set;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.common.StatusChangePopup;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.RefreshModuleEditorEvent;
import org.drools.guvnor.client.explorer.RefreshSuggestionCompletionEngineEvent;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.explorer.navigation.qa.VerifierResultWidget;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.RuleModelEditor;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.packages.PackageBuilderWidget;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.packages.WorkingSetManager;
import org.drools.guvnor.client.processeditor.BusinessProcessEditor;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.client.rpc.VerificationServiceAsync;
import org.drools.guvnor.client.ruleeditor.ShowMessageEvent.MessageType;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbar;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.client.ruleeditor.toolbar.DefaultActionToolbarButtonsConfigurationProvider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main layout parent/controller the rule viewer.
 */
public class RuleViewer extends GuvnorEditor {

    private Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );
    private final EventBus eventBus;

    interface RuleViewerBinder
            extends
            UiBinder<Widget, RuleViewer> {

    }

    private static RuleViewerBinder uiBinder = GWT.create( RuleViewerBinder.class );

    @UiField(provided = true)
    final Widget editor;

    @UiField(provided = true)
    final ActionToolbar toolbar;

    private Command afterCheckinEvent;

    protected RuleAsset asset;
    private boolean readOnly;
    private final RuleViewerSettings ruleViewerSettings;

    private final ClientFactory clientFactory;

    private long lastSaved = System.currentTimeMillis();
    private ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider;

    public RuleViewer(
            RuleAsset asset,
            ClientFactory clientFactory,
            EventBus eventBus) {
        this( asset,
                clientFactory,
                eventBus,
                false,
                null,
                null );
    }

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset,
                      ClientFactory clientFactory,
                      EventBus eventBus,
                      boolean historicalReadOnly) {
        this( asset,
                clientFactory,
                eventBus,
                historicalReadOnly,
                null,
                null );
    }

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     * @param actionToolbarButtonsConfigurationProvider
     *                           used to change the default button configuration provider.
     */
    public RuleViewer(RuleAsset asset,
                      ClientFactory clientFactory,
                      EventBus eventBus,
                      boolean historicalReadOnly,
                      ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider,
                      RuleViewerSettings ruleViewerSettings) {
        this.asset = asset;
        this.readOnly = historicalReadOnly || asset.isReadonly();
        this.eventBus = eventBus;

        this.clientFactory = clientFactory;

        if ( ruleViewerSettings == null ) {
            this.ruleViewerSettings = new RuleViewerSettings();
        } else {
            this.ruleViewerSettings = ruleViewerSettings;
        }

        this.actionToolbarButtonsConfigurationProvider = actionToolbarButtonsConfigurationProvider;

        //editor = EditorLauncher.getEditorViewer( asset, this );
        editor = clientFactory.getAssetEditorFactory().getAssetEditor( asset,
                this,
                clientFactory,
                eventBus);

        // for designer we need to give it more playing room
        if ( editor.getClass().getName().equals( "org.drools.guvnor.client.processeditor.BusinessProcessEditor" ) ) {
            if ( this.ruleViewerSettings.isStandalone() ) {
                // standalone bigger dimensions"
                editor.setWidth( "1600px" );
                editor.setHeight( "1000px" );
            } else {
                // normal dimensions inside guvnor
                editor.setWidth( "100%" );
                editor.setHeight( "580px" );
            }
        }

        toolbar = new ActionToolbar( getConfiguration(),
                asset.getState() );

        initWidget( uiBinder.createAndBindUi( this ) );
        setWidth( "100%" );

        initActionToolBar();
        LoadingPopup.close();
    }

    public ActionToolbar getActionToolbar() {
        return this.toolbar;
    }

    @Override
    public boolean isDirty() {
        return (System.currentTimeMillis() - lastSaved) > 3600000;
    }

    private ActionToolbarButtonsConfigurationProvider getConfiguration() {
        if ( actionToolbarButtonsConfigurationProvider == null ) {
            return new DefaultActionToolbarButtonsConfigurationProvider( asset,
                    (EditorWidget) editor );
        } else {
            return actionToolbarButtonsConfigurationProvider;
        }
    }

    /**
     * This will actually load up the data (this is called by the callback) when
     * we get the data back from the server, also determines what widgets to
     * load up).
     */
    private void initActionToolBar() {
        // the action widgets (checkin/close etc).
        if ( readOnly
                || asset.isReadonly() || this.ruleViewerSettings.isStandalone() ) {
            toolbar.setVisible( false );
        } else {
            toolbar.setPromtToGlobalCommand( new Command() {
                public void execute() {
                    doPromptToGlobal();
                }
            } );
            toolbar.setDeleteCommand( new Command() {
                public void execute() {
                    doDelete();
                }
            } );
            toolbar.setCopyCommand( new Command() {
                public void execute() {
                    doCopy();
                }
            } );
            toolbar.setRenameCommand( new Command() {
                public void execute() {
                    doRename();
                }
            } );
            toolbar.setArchiveCommand( new Command() {
                public void execute() {
                    doArchive();
                }
            } );
            this.afterCheckinEvent = new Command() {
                public void execute() {
                    toolbar.setDeleteVisible( false );
                    toolbar.setArchiveVisible( true );
                }
            };

            toolbar.setSelectWorkingSetsCommand( new Command() {
                public void execute() {
                    showWorkingSetsSelection( ((RuleModelEditor) editor).getRuleModeller() );
                }
            } );
            toolbar.setViewSourceCommand( new Command() {
                public void execute() {
                	if( editor.getClass().getName().equals( "org.drools.guvnor.client.processeditor.BusinessProcessEditor" ) ) { 
                    	if ( ((BusinessProcessEditor) editor).hasErrors()) {
                    		return;
                    	}
                	}
                    onSave();
                    LoadingPopup.showMessage( constants.CalculatingSource() );
                    RepositoryServiceFactory.getAssetService().buildAssetSource( asset,
                            new GenericCallback<String>() {

                                public void onSuccess(String src) {
                                    showSource( src );
                                }
                            } );

                }
            } );

            toolbar.setVerifyCommand( new Command() {
                public void execute() {
                    doVerify();
                }
            } );

            toolbar.setValidateCommand( new Command() {
                public void execute() {
                    onSave();
                    LoadingPopup.showMessage( constants.ValidatingItemPleaseWait() );
                    RepositoryServiceFactory.getAssetService().validateAsset( asset,
                            new GenericCallback<BuilderResult>() {

                                public void onSuccess(BuilderResult results) {
                                    RuleValidatorWrapper.showBuilderErrors( results );
                                }
                            } );

                }
            } );

            toolbar.setSaveChangesCommand( new Command() {
                public void execute() {
                    verifyAndDoCheckinConfirm( false );
                }
            } );

            toolbar.setSaveChangesAndCloseCommand( new Command() {
                public void execute() {
                    verifyAndDoCheckinConfirm( true );
                }
            } );

            toolbar.setChangeStatusCommand( new Command() {
                public void execute() {
                    showStatusChanger();
                }
            } );
        }
    }

    /**
     * Show the state change popup.
     */
    private void showStatusChanger() {
        final StatusChangePopup pop = new StatusChangePopup( asset.getUuid(),
                false );
        pop.setChangeStatusEvent( new Command() {

            public void execute() {
                toolbar.setState( pop.getState() );
            }
        } );

        pop.show();
    }

    protected void verifyAndDoCheckinConfirm(final boolean closeAfter) {
        if ( editor instanceof RuleModeller ) {
            ((RuleModeller) editor).verifyRule( new Command() {

                public void execute() {
                    if ( ((RuleModeller) editor).hasVerifierErrors()
                            || ((RuleModeller) editor).hasVerifierWarnings() ) {
                        if ( !Window.confirm( constants.theRuleHasErrorsOrWarningsDotDoYouWantToContinue() ) ) {
                            return;
                        }
                    }
                    doCheckinConfirm( closeAfter );
                }
            } );
        } else if( editor.getClass().getName().equals( "org.drools.guvnor.client.processeditor.BusinessProcessEditor" ) ) { 
        	if ( ((BusinessProcessEditor) editor).hasErrors()) {
        		return;
        	} else {
        		doCheckinConfirm( closeAfter );
        	}
        } else {
            doCheckinConfirm( closeAfter );
        }
    }

    /**
     * Called when user wants to checkin. set closeAfter to true if it should
     * close this whole thing after saving it.
     */
    protected void doCheckinConfirm(final boolean closeAfter) {
        final CheckinPopup pop = new CheckinPopup( constants.CheckInChanges() );
        pop.setCommand( new Command() {

            public void execute() {
                doCheckin( pop.getCheckinComment(),
                        closeAfter );
                if ( afterCheckinEvent != null ) {
                    afterCheckinEvent.execute();
                }
                if ( closeAfter ) {
                    close();
                }
            }
        } );
        pop.show();
    }

    public void doCheckin(String comment,
                          boolean closeAfter) {
        if ( editor instanceof SaveEventListener ) {
            ((SaveEventListener) editor).onSave();
        }
        performCheckIn( comment,
                closeAfter );
    }

    private void doVerify() {
        onSave();
        LoadingPopup.showMessage( constants.VerifyingItemPleaseWait() );
        Set<String> activeWorkingSets = null;
        activeWorkingSets = WorkingSetManager.getInstance().getActiveAssetUUIDs( asset.getMetaData().getPackageName() );

        VerificationServiceAsync verificationService = GWT.create( VerificationService.class );

        verificationService.verifyAsset( asset,
                activeWorkingSets,
                new GenericCallback<AnalysisReport>() {

                    public void onSuccess(AnalysisReport report) {
                        LoadingPopup.close();
                        final FormStylePopup form = new FormStylePopup( images.ruleAsset(),
                                constants.VerificationReport() );
                        ScrollPanel scrollPanel = new ScrollPanel( new VerifierResultWidget( report,
                                false ) );
                        scrollPanel.setWidth( "800px" );
                        scrollPanel.setHeight( "200px" );
                        form.addRow( scrollPanel );

                        LoadingPopup.close();
                        form.show();
                    }
                } );

    }

    private void showSource(String src) {
        PackageBuilderWidget.showSource( src,
                this.asset.getName() );
        LoadingPopup.close();
    }

    private void onSave() {
        if ( editor instanceof SaveEventListener ) {
            SaveEventListener el = (SaveEventListener) editor;
            el.onSave();
            // TODO: Use info-area

        }
    }

    protected void showWorkingSetsSelection(RuleModeller modeller) {
        new WorkingSetSelectorPopup( modeller,
                asset ).show();
    }

    protected boolean hasDirty() {
        // not sure how to implement this now.
        return false;
    }

    void doDelete() {
        readOnly = true; // set to not cause the extra confirm popup
        RepositoryServiceFactory.getService().deleteUncheckedRule( this.asset.getUuid(),
                new GenericCallback<Void>() {
                    public void onSuccess(Void o) {
                        close();
                    }
                } );
    }

    private void doArchive() {
        RepositoryServiceFactory.getAssetService().archiveAsset( asset.getUuid(),
                new GenericCallback<Void>() {
                    public void onSuccess(Void o) {
                        eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getPackageUUID() ) );
                        close();
                    }
                } );
    }

    private void performCheckIn(String comment,
                                final boolean closeAfter) {
        this.asset.setCheckinComment( comment );
        final boolean[] saved = {false};

        if ( !saved[0] ) LoadingPopup.showMessage( constants.SavingPleaseWait() );
        RepositoryServiceFactory.getAssetService().checkinVersion( this.asset,
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

                        flushSuggestionCompletionCache(asset.getMetaData().getPackageName(), uuid);

                        LoadingPopup.close();
                        saved[0] = true;

                        eventBus.fireEvent( new ShowMessageEvent( constants.SavedOK(),
                                                                  MessageType.INFO ) );
                        
                        if ( editor instanceof SaveEventListener ) {
                            ((SaveEventListener) editor).onAfterSave();
                        }

                        eventBus.fireEvent(new RefreshModuleEditorEvent(asset.getMetaData().getPackageUUID()));
                        lastSaved = System.currentTimeMillis();
                        resetDirty();
                    }
                } );
    }

    /**
     * In some cases we will want to flush the package dependency stuff for
     * suggestion completions. The user will still need to reload the asset
     * editor though.
     */
    public void flushSuggestionCompletionCache(final String packageName, String assetUUID) {
        if ( AssetFormats.isPackageDependency( this.asset.getFormat() ) ) {
            LoadingPopup.showMessage( constants.RefreshingContentAssistance() );
            SuggestionCompletionCache.getInstance().refreshPackage( packageName,
                    new Command() {
                        public void execute() {
                            //Some assets depend on the SuggestionCompletionEngine. This event is to notify them that the
                            //SuggestionCompletionEngine has been changed, they need to refresh their UI to represent the changes.
                           
                            //set assetUUID to null means to refresh all asset editors contained by the specified package. 
                            eventBus.fireEvent(new RefreshAssetEditorEvent(packageName, null));
                            LoadingPopup.close();
                        }
                    } );
        } else {
            //Refresh the current editor only.
            eventBus.fireEvent(new RefreshAssetEditorEvent(packageName, assetUUID));
        }
    }

    /**
     * Called when user wants to close, but there is "dirtyness".
     */
    protected void doCloseUnsavedWarning() {
        final FormStylePopup pop = new FormStylePopup( images.warningLarge(),
                constants.WARNINGUnCommittedChanges() );
        Button dis = new Button( constants.Discard() );
        Button can = new Button( constants.Cancel() );
        HorizontalPanel hor = new HorizontalPanel();

        hor.add( dis );
        hor.add( can );

        pop.addRow( new HTML( constants.AreYouSureYouWantToDiscardChanges() ) );
        pop.addRow( hor );

        dis.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                close();
                pop.hide();
            }
        } );

        can.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                pop.hide();
            }
        } );

        pop.show();
    }

    private void doCopy() {
        final FormStylePopup form = new FormStylePopup( images.ruleAsset(),
                constants.CopyThisItem() );
        final TextBox newName = new TextBox();
        form.addAttribute( constants.NewName(),
                newName );
        final RulePackageSelector sel = new RulePackageSelector();
        form.addAttribute( constants.NewPackage(),
                sel );

        Button ok = new Button( constants.CreateCopy() );

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if ( newName.getText() == null
                        || newName.getText().equals( "" ) ) {
                    Window.alert( constants.AssetNameMustNotBeEmpty() );
                    return;
                }
                String name = newName.getText().trim();
                RepositoryServiceFactory.getAssetService().copyAsset( asset.getUuid(),
                        sel.getSelectedPackage(),
                        name,
                        new GenericCallback<String>() {
                            public void onSuccess(String data) {
                                eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getPackageUUID() ) );
                                flushSuggestionCompletionCache(sel.getSelectedPackage(), null);
                                completedCopying( newName.getText(),
                                        sel.getSelectedPackage(),
                                        data );
                                form.hide();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if ( t.getMessage().indexOf( "ItemExistsException" ) > -1 ) { // NON-NLS
                                    Window.alert( constants.ThatNameIsInUsePleaseTryAnother() );
                                } else {
                                    super.onFailure( t );
                                }
                            }
                        } );
            }

        } );

        form.addAttribute( "",
                ok );

        form.show();
    }

    private void doRename() {
        final FormStylePopup pop = new FormStylePopup( images.packageLarge(),
                constants.RenameThisItem() );
        final TextBox box = new TextBox();
        box.setText( asset.getName() );
        pop.addAttribute( constants.NewNameAsset(),
                box );
        Button ok = new Button( constants.RenameItem() );
        pop.addAttribute( "",
                ok );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                RepositoryServiceFactory.getAssetService().renameAsset( asset.getUuid(),
                        box.getText(),
                        new GenericCallback<java.lang.String>() {
                            public void onSuccess(String data) {
                                Window.alert( constants.ItemHasBeenRenamed() );
                                eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getPackageUUID() ) );
                                eventBus.fireEvent(new RefreshAssetEditorEvent(asset.getMetaData().getPackageName(), asset.getUuid()));
                                pop.hide();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if ( t.getMessage().indexOf( "ItemExistsException" ) > -1 ) { // NON-NLS
                                    Window.alert( constants.ThatNameIsInUsePleaseTryAnother() );
                                } else {
                                    super.onFailure( t );
                                }
                            }
                        } );
            }
        } );

        pop.show();
    }

    private void doPromptToGlobal() {
        if ( asset.getMetaData().getPackageName().equals( "globalArea" ) ) {
            Window.alert( constants.ItemAlreadyInGlobalArea() );
            return;
        }
        if ( Window.confirm( constants.PromoteAreYouSure() ) ) {
            RepositoryServiceFactory.getAssetService().promoteAssetToGlobalArea( asset.getUuid(),
                    new GenericCallback<Void>() {
                        public void onSuccess(Void data) {
                            Window.alert( constants.Promoted() );

                            flushSuggestionCompletionCache(asset.getMetaData().getPackageName(), null);
                            flushSuggestionCompletionCache("globalArea", null);
                            eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getPackageUUID() ) );
                            eventBus.fireEvent(new RefreshAssetEditorEvent(asset.getMetaData().getPackageName(), asset.getUuid()));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            super.onFailure( t );
                        }
                    } );
        }
    }

    /**
     * closes itself
     */
    private void close() {
        eventBus.fireEvent( new ClosePlaceEvent( new AssetEditorPlace( asset.uuid ) ) );
    }

    private void completedCopying(String name,
                                  String pkg,
                                  String newAssetUUID) {
        Window.alert( constants.CreatedANewItemSuccess( name,
                pkg ) );
        clientFactory.getPlaceController().goTo( new AssetEditorPlace( newAssetUUID ) );
    }    

}
