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
package org.drools.guvnor.client.widgets.soa.toolbar;


import org.drools.guvnor.client.asseteditor.RefreshAssetEditorEvent;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
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
//import org.drools.guvnor.client.explorer.RefreshSuggestionCompletionEngineEvent;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
//import org.drools.guvnor.client.rpc.AnalysisReport;
//import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.Asset;
//import org.drools.guvnor.client.rpc.VerificationService;
//import org.drools.guvnor.client.rpc.VerificationServiceAsync;
import org.drools.guvnor.client.widgets.CheckinPopup;
import org.drools.guvnor.client.widgets.toolbar.ActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.client.widgets.toolbar.DefaultActionToolbarButtonsConfigurationProvider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 */
public class AssetEditorActionToolbar extends Composite {

    private Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create( Images.class );
    
    interface ActionToolbarBinder
            extends
            UiBinder<Widget, AssetEditorActionToolbar> {
    }


    private static ActionToolbarBinder uiBinder = GWT.create(ActionToolbarBinder.class);

    @UiField
    MenuItem saveChanges;

    @UiField
    MenuItem saveChangesAndClose;

    @UiField
    MenuItem archive;

    @UiField
    MenuItem delete;

    @UiField
    MenuItem copy;
    
    @UiField
    MenuItem rename;
    
    @UiField
    MenuItem promoteToGlobal;

    @UiField
    MenuItem selectWorkingSets;

    @UiField
    MenuItem validate;

    @UiField
    MenuItem verify;

    @UiField
    MenuItem viewSource;

    @UiField
    MenuItem changeStatus;

    @UiField
    Label status;

    @UiField
    MenuItem sourceMenu;

    private ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider;
    protected Asset asset;
    final Widget editor;
    private final EventBus eventBus;
    private final ClientFactory clientFactory;
    private Command afterCheckinEvent;
    private boolean readOnly;
    
    public AssetEditorActionToolbar(Asset asset, 
                         final Widget editor,
                         ClientFactory clientFactory,
                         EventBus eventBus,
                         boolean readOnly) {
        this.asset = asset;
        this.editor = editor;
        this.eventBus = eventBus;
        this.clientFactory = clientFactory;
        this.readOnly = readOnly;
        
        actionToolbarButtonsConfigurationProvider = new DefaultActionToolbarButtonsConfigurationProvider(asset);
        initWidget(uiBinder.createAndBindUi(this));

        setState(asset.getState());
        applyToolBarConfiguration();
        this.status.setVisible(this.actionToolbarButtonsConfigurationProvider.showStateLabel());
        
        initActionToolBar();
        setRefreshHandler();
    }

    /**
     * Sets the visible status display.
     */
    public void setState(String newStatus) {
        status.setText(constants.statusIs(newStatus));
    }

    private void applyToolBarConfiguration() {
        saveChanges.setVisible(actionToolbarButtonsConfigurationProvider.showSaveButton());
        saveChangesAndClose.setVisible(actionToolbarButtonsConfigurationProvider.showSaveAndCloseButton());
        validate.setVisible(actionToolbarButtonsConfigurationProvider.showValidateButton());
        verify.setVisible(actionToolbarButtonsConfigurationProvider.showVerifyButton());
        viewSource.setVisible(actionToolbarButtonsConfigurationProvider.showViewSourceButton());
        copy.setVisible(actionToolbarButtonsConfigurationProvider.showCopyButton());
        rename.setVisible(actionToolbarButtonsConfigurationProvider.showRenameButton());
        promoteToGlobal.setVisible(actionToolbarButtonsConfigurationProvider.showPromoteToGlobalButton());
        archive.setVisible(actionToolbarButtonsConfigurationProvider.showArchiveButton());
        delete.setVisible(actionToolbarButtonsConfigurationProvider.showDeleteButton());
        changeStatus.setVisible(actionToolbarButtonsConfigurationProvider.showChangeStatusButton());
        selectWorkingSets.setVisible(actionToolbarButtonsConfigurationProvider.showSelectWorkingSetsButton());

        sourceMenu.setVisible(areSourceMenuChildrenVisible());
    }

    private boolean areSourceMenuChildrenVisible() {
        return validate.isVisible() || verify.isVisible() || viewSource.isVisible();
    }

    public void setSelectWorkingSetsCommand(Command command) {
        selectWorkingSets.setCommand(command);
    }

    public void setViewSourceCommand(Command command) {
        viewSource.setCommand(command);
    }

    public void setVerifyCommand(Command command) {
        verify.setCommand(command);
    }

    public void setValidateCommand(Command command) {
        validate.setCommand(command);
    }

    public void setSaveChangesCommand(Command command) {
        saveChanges.setCommand(command);
    }

    public void setSaveChangesAndCloseCommand(Command command) {
        saveChangesAndClose.setCommand(command);
    }

    public void setChangeStatusCommand(Command command) {
        changeStatus.setCommand(command);
    }

    public void setDeleteVisible(boolean b) {
        delete.setVisible(b);
    }

    public void setArchiveVisible(boolean b) {
        archive.setVisible(b);
    }

    public void setArchiveCommand(final Command archiveCommand) {
        archive.setCommand(new Command() {

            public void execute() {
                if (Window.confirm(constants.AreYouSureYouWantToArchiveThisItem())) {
                    archiveCommand.execute();
                }
            }
        });
    }

    public void setCopyCommand(Command command) {
        copy.setCommand(command);
    }
    
    public void setRenameCommand(Command command) {
        rename.setCommand(command);
    }
    
    public void setDeleteCommand(final Command deleteCommand) {
        delete.setCommand(new Command() {

            public void execute() {
                if (Window.confirm(constants.DeleteAreYouSure())) {
                    deleteCommand.execute();
                }
            }
        });
    }

    public void setPromtToGlobalCommand(Command command) {
        promoteToGlobal.setCommand(command);
    }
    
    /**
     * This will actually load up the data (this is called by the callback) when
     * we get the data back from the server, also determines what widgets to
     * load up).
     */
    private void initActionToolBar() {
        // the action widgets (checkin/close etc).
        if (readOnly) {
            setVisible( false );
        } else {
            setPromtToGlobalCommand( new Command() {
                public void execute() {
                    doPromptToGlobal();
                }
            } );
            setDeleteCommand( new Command() {
                public void execute() {
                    doDelete();
                }
            } );
            setCopyCommand( new Command() {
                public void execute() {
                    doCopy();
                }
            } );
            setRenameCommand( new Command() {
                public void execute() {
                    doRename();
                }
            } );
            setArchiveCommand( new Command() {
                public void execute() {
                    doArchive();
                }
            } );
            this.afterCheckinEvent = new Command() {
                public void execute() {
                    setDeleteVisible( false );
                    setArchiveVisible( true );
                }
            };

/*            setSelectWorkingSetsCommand( new Command() {
                public void execute() {
                    showWorkingSetsSelection( ((RuleModelEditor) editor).getRuleModeller() );
                }
            } )*/;
/*            setViewSourceCommand( new Command() {
                public void execute() {
                    onSave();
                    LoadingPopup.showMessage( constants.CalculatingSource() );
                    RepositoryServiceFactory.getAssetService().buildAssetSource( asset,
                            new GenericCallback<String>() {

                                public void onSuccess(String src) {
                                    showSource( src );
                                }
                            } );

                }
            } );*/

/*            setVerifyCommand( new Command() {
                public void execute() {
                    doVerify();
                }
            } );*/

/*            setValidateCommand( new Command() {
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
            } );*/

            setSaveChangesCommand( new Command() {
                public void execute() {
                    verifyAndDoCheckinConfirm( false );
                }
            } );

            setSaveChangesAndCloseCommand( new Command() {
                public void execute() {
                    verifyAndDoCheckinConfirm( true );
                }
            } );

            setChangeStatusCommand( new Command() {
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
                setState( pop.getState() );
            }
        } );

        pop.show();
    }

    protected void verifyAndDoCheckinConfirm(final boolean closeAfter) {
/*        if ( editor instanceof RuleModeller ) {
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
        } else {*/
            doCheckinConfirm( closeAfter );
        //}
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
        if ( editor instanceof SaveEventListener ) {
            ((SaveEventListener) editor).onAfterSave();
        }

        eventBus.fireEvent(new RefreshModuleEditorEvent(asset.getMetaData().getModuleUUID()));
        //TODO: JLIU
        //lastSaved = System.currentTimeMillis();
        //resetDirty();
    }

/*    private void doVerify() {
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

    }*/

/*    private void showSource(String src) {
        PackageBuilderWidget.showSource( src,
                this.asset.getName() );
        LoadingPopup.close();
    }*/

    private void onSave() {
        if ( editor instanceof SaveEventListener ) {
            SaveEventListener el = (SaveEventListener) editor;
            el.onSave();
            // TODO: Use info-area

        }
    }
/*
    protected void showWorkingSetsSelection(RuleModeller modeller) {
        new WorkingSetSelectorPopup( modeller,
                asset ).show();
    }*/

    protected boolean hasDirty() {
        // not sure how to implement this now.
        return false;
    }

    /**
     * closes itself
     */
    private void close() {
        eventBus.fireEvent( new ClosePlaceEvent( new AssetEditorPlace( asset.uuid ) ) );
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
                        eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getModuleUUID() ) );
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

                        //flushSuggestionCompletionCache(asset.getMetaData().getPackageName());
                        if ( editor instanceof DirtyableComposite ) {
                            ((DirtyableComposite) editor).resetDirty();
                        }

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
/*    public void flushSuggestionCompletionCache(final String packageName) {
        if ( AssetFormats.isPackageDependency( this.asset.getFormat() ) ) {
            LoadingPopup.showMessage( constants.RefreshingContentAssistance() );
            SuggestionCompletionCache.getInstance().refreshPackage( packageName,
                    new Command() {
                        public void execute() {
                            //Some assets depend on the SuggestionCompletionEngine. This event is to notify them that the 
                            //SuggestionCompletionEngine has been changed, they need to refresh their UI to represent the changes.
                            eventBus.fireEvent(new RefreshSuggestionCompletionEngineEvent(packageName));
                            LoadingPopup.close();
                        }
                    } );
        }
    }*/

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
                                eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getModuleUUID() ) );
                                //flushSuggestionCompletionCache(sel.getSelectedPackage());
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
                                eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getModuleUUID() ) );
                                eventBus.fireEvent(new RefreshAssetEditorEvent(asset.getUuid()));
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
        if ( asset.getMetaData().getModuleName().equals( "globalArea" ) ) {
            Window.alert( constants.ItemAlreadyInGlobalArea() );
            return;
        }
        if ( Window.confirm( constants.PromoteAreYouSure() ) ) {
            RepositoryServiceFactory.getAssetService().promoteAssetToGlobalArea( asset.getUuid(),
                    new GenericCallback<Void>() {
                        public void onSuccess(Void data) {
                            Window.alert( constants.Promoted() );

                            //flushSuggestionCompletionCache(asset.getMetaData().getPackageName());
                            //flushSuggestionCompletionCache("globalArea");
                            eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getModuleUUID() ) );
                            eventBus.fireEvent(new RefreshAssetEditorEvent(asset.getUuid()));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            super.onFailure( t );
                        }
                    } );
        }
    }

    private void completedCopying(String name,
                                  String pkg,
                                  String newAssetUUID) {
        Window.alert( constants.CreatedANewItemSuccess( name,
                pkg ) );
        clientFactory.getPlaceController().goTo( new AssetEditorPlace( newAssetUUID ) );
    }    
    
    private void setRefreshHandler() {
/*        eventBus.addHandler(RefreshSuggestionCompletionEngineEvent.TYPE,
                new RefreshSuggestionCompletionEngineEvent.Handler() {
                    public void onRefreshModule(
                            RefreshSuggestionCompletionEngineEvent refreshSuggestionCompletionEngineEvent) {
                        String moduleName = refreshSuggestionCompletionEngineEvent.getModuleName();
                        if(moduleName!=null && moduleName.equals(asset.getMetaData().getPackageName())) {
                            closeAndReopen(asset.getUuid());                                
                        }
                    
                    }
                });*/
    }
}
