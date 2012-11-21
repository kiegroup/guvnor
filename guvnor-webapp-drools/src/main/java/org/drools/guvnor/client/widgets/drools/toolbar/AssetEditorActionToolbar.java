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
package org.drools.guvnor.client.widgets.drools.toolbar;

import java.util.Set;

import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.asseteditor.*;
import org.drools.guvnor.client.asseteditor.drools.RuleValidatorWrapper;
import org.drools.guvnor.client.asseteditor.drools.WorkingSetSelectorPopup;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModelEditor;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.kie.uberfirebootstrap.client.widgets.ErrorPopup;
import org.kie.uberfirebootstrap.client.widgets.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.common.StatusChangePopup;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.RefreshModuleEditorEvent;
import org.drools.guvnor.client.explorer.navigation.qa.VerifierResultWidget;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.PackageBuilderWidget;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.moduleeditor.drools.WorkingSetManager;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.rpc.*;
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
import org.drools.guvnor.shared.api.Valid;
import org.drools.guvnor.client.rpc.Path;
import org.drools.guvnor.client.rpc.PathImpl;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 */
public class AssetEditorActionToolbar extends Composite {

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
    Image validIndicator;

    @UiField
    MenuItem sourceMenu;

    private AssetServiceAsync assetService = GWT.create(AssetService.class);

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
        setValidIndicator(GuvnorImages.INSTANCE.getValidImage(asset.getMetaData().getValid()));
        applyToolBarConfiguration();
        this.status.setVisible(this.actionToolbarButtonsConfigurationProvider.showStateLabel());

        initActionToolBar();
    }

    /**
     * Sets the visible status display.
     */
    public void setState(String newStatus) {
        status.setText(Constants.INSTANCE.statusIs(newStatus));
    }

    public void setValidIndicator(Image valid) {

        validIndicator = valid;
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
                if (Window.confirm(Constants.INSTANCE.AreYouSureYouWantToArchiveThisItem())) {
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
                if (Window.confirm(Constants.INSTANCE.DeleteAreYouSure())) {
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

            setSelectWorkingSetsCommand( new Command() {
                public void execute() {
                    showWorkingSetsSelection( ((RuleModelEditor) editor).getRuleModeller() );
                }
            } );
            setViewSourceCommand( new Command() {
                public void execute() {
                	if( editor instanceof BusinessProcessEditor ) {
                        if ( ((BusinessProcessEditor) editor).hasErrors()) {
                        	return;
                        }
                    }
                    onSave();
                    LoadingPopup.showMessage( Constants.INSTANCE.CalculatingSource() );
                    assetService.buildAssetSource( asset,
                            new GenericCallback<String>() {

                                public void onSuccess(String src) {
                                    showSource( src );
                                }
                            } );

                }
            } );

            setVerifyCommand( new Command() {
                public void execute() {
                    doVerify();
                }
            } );

            setValidateCommand( new Command() {
                public void execute() {
                    onSave();
                    LoadingPopup.showMessage( Constants.INSTANCE.ValidatingItemPleaseWait() );
                    assetService.validateAsset( asset,
                            new GenericCallback<BuilderResult>() {

                                public void onSuccess(BuilderResult results) {
                                    RuleValidatorWrapper.showBuilderErrors( results );
                                    setValidIndicator(GuvnorImages.INSTANCE.getValidImage(
                                            Valid.fromBoolean(results == null || !results.hasLines())));
                                }
                            } );

                }
            } );

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
        if ( editor instanceof RuleModeller ) {
            ((RuleModeller) editor).verifyRule( new Command() {

                public void execute() {
                    if ( ((RuleModeller) editor).hasVerifierErrors()
                            || ((RuleModeller) editor).hasVerifierWarnings() ) {
                        if ( !Window.confirm( Constants.INSTANCE.theRuleHasErrorsOrWarningsDotDoYouWantToContinue() ) ) {
                            return;
                        }
                    }
                    doCheckinConfirm( closeAfter );
                }
            } );
        } else if( editor instanceof BusinessProcessEditor ) {
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
        final CheckinPopup pop = new CheckinPopup( Constants.INSTANCE.CheckInChanges() );
        pop.setCommand( new Command() {

            public void execute() {
                doCheckin( pop.getCheckinComment(),
                        closeAfter );
            }
        } );
        pop.show();
    }

    public void doCheckin(final String comment,
                          final boolean closeAfter) {
        if ( editor instanceof SaveEventListener ) {
            ((SaveEventListener) editor).onSave(new SaveCommand() {
                @Override
                public void save() {
                   AssetEditorActionToolbar.this.save(comment, closeAfter);
                }

                @Override
                public void cancel() {
                    // Do nothing at this point.
                }
            });
        }else{
            save(comment, closeAfter);
        }
    }

    private void save(String comment, boolean closeAfter) {
        performCheckIn( comment,
                closeAfter );
        if ( closeAfter ) {
            close();
        }
    }

    private void doVerify() {
        onSave();
        LoadingPopup.showMessage( Constants.INSTANCE.VerifyingItemPleaseWait() );
        Set<String> activeWorkingSets = null;
        activeWorkingSets = WorkingSetManager.getInstance().getActiveAssetUUIDs( asset.getMetaData().getModuleName() );

        VerificationServiceAsync verificationService = GWT.create( VerificationService.class );

        verificationService.verifyAsset( asset,
                activeWorkingSets,
                new GenericCallback<AnalysisReport>() {

                    public void onSuccess(AnalysisReport report) {
                        LoadingPopup.close();
                        final FormStylePopup form = new FormStylePopup(DroolsGuvnorImages.INSTANCE.RuleAsset(),
                                Constants.INSTANCE.VerificationReport() );
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
            el.onSave(new SaveCommand() {
                @Override
                public void save() {
                    // No need to do anything.
                }

                @Override
                public void cancel() {
                    // No need to do anything.
                }
            });
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

    /**
     * closes itself
     */
    private void close() {
//        eventBus.fireEvent( new ClosePlaceEvent( new AssetEditorPlace( asset.getUuid() ) ) );
    }

    void doDelete() {
        readOnly = true; // set to not cause the extra confirm popup
        RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);
        Path path = new PathImpl();
        path.setUUID(this.asset.getUuid());        
        repositoryService.deleteUncheckedRule( path,
                new GenericCallback<Void>() {
                    public void onSuccess(Void o) {
                        eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getModuleUUID() ) );
                        close();
                    }
                } );
    }

    private void doArchive() {
    	Path path = new PathImpl();
    	path.setUUID(asset.getUuid());
        assetService.archiveAsset( path,
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

        if ( !saved[0] ) LoadingPopup.showMessage( Constants.INSTANCE.SavingPleaseWait() );
        assetService.checkinVersion( this.asset,
                new GenericCallback<String>() {

                    public void onSuccess(String uuid) {
                        if ( uuid == null ) {
                            ErrorPopup.showMessage( Constants.INSTANCE.FailedToCheckInTheItemPleaseContactYourSystemAdministrator() );
                            return;
                        }

                        if ( uuid.startsWith( "ERR" ) ) { // NON-NLS
                            ErrorPopup.showMessage( uuid.substring( 5 ) );
                            return;
                        }

                        flushSuggestionCompletionCache(asset.getMetaData().getModuleName(), uuid);
                        if ( editor instanceof DirtyableComposite ) {
                            ((DirtyableComposite) editor).resetDirty();
                        }

                        LoadingPopup.close();
                        saved[0] = true;

                        //showInfoMessage( Constants.INSTANCE.SavedOK() );

                        //fire after check-in event
                        if (editor instanceof GuvnorEditor){
                            eventBus.fireEvent(new AfterAssetEditorCheckInEvent(uuid, (GuvnorEditor) editor));
                        }

                        if ( editor instanceof SaveEventListener ) {
                            ((SaveEventListener) editor).onAfterSave();
                        }

                        eventBus.fireEvent(new RefreshModuleEditorEvent(asset.getMetaData().getModuleUUID()));

                        if ( afterCheckinEvent != null ) {
                            afterCheckinEvent.execute();
                        }
                    }
                } );
    }

    /**
     * In some cases we will want to flush the package dependency stuff for
     * suggestion completions. The user will still need to reload the asset
     * editor though.
     */
    public void flushSuggestionCompletionCache(final String moduleName, String uuid) {
        if ( AssetFormats.isPackageDependency( this.asset.getFormat() ) ) {
            LoadingPopup.showMessage( Constants.INSTANCE.RefreshingContentAssistance() );
            SuggestionCompletionCache.getInstance().loadPackage( moduleName,
                    new Command() {
                        public void execute() {
                            //Some assets depend on the SuggestionCompletionEngine. This event is to notify them that the 
                            //SuggestionCompletionEngine has been changed, they need to refresh their UI to represent the changes.

                            //set assetUUID to null means to refresh all asset editors contained by the specified package. 
                            clientFactory.getRefreshAssetEditorEvents().fire(new RefreshAssetEditorEvent(moduleName, null));
                            LoadingPopup.close();
                        }
                    } );
        } else {
            //No need to refresh other asset editors, refresh the current asset editor only.
            clientFactory.getRefreshAssetEditorEvents().fire( new RefreshAssetEditorEvent(moduleName, uuid));
        }
    }

    /**
     * Called when user wants to close, but there is "dirtyness".
     */
    protected void doCloseUnsavedWarning() {
        final FormStylePopup pop = new FormStylePopup(DroolsGuvnorImages.INSTANCE.WarningImage(),
                Constants.INSTANCE.WARNINGUnCommittedChanges() );
        Button dis = new Button( Constants.INSTANCE.Discard() );
        Button can = new Button( Constants.INSTANCE.Cancel() );
        HorizontalPanel hor = new HorizontalPanel();

        hor.add( dis );
        hor.add( can );

        pop.addRow( new HTML( Constants.INSTANCE.AreYouSureYouWantToDiscardChanges() ) );
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
        final FormStylePopup form = new FormStylePopup(DroolsGuvnorImages.INSTANCE.RuleAsset(),
                Constants.INSTANCE.CopyThisItem() );
        final TextBox newName = new TextBox();
        form.addAttribute( Constants.INSTANCE.NewName(),
                newName );
        final RulePackageSelector sel = new RulePackageSelector();
        form.addAttribute( Constants.INSTANCE.NewPackage(),
                sel );

        Button ok = new Button( Constants.INSTANCE.CreateCopy() );

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if ( newName.getText() == null
                        || newName.getText().equals( "" ) ) {
                    Window.alert( Constants.INSTANCE.AssetNameMustNotBeEmpty() );
                    return;
                }
                String name = newName.getText().trim();
            	Path path = new PathImpl();
            	path.setUUID(asset.getUuid());
                assetService.copyAsset( path,
                        sel.getSelectedPackage(),
                        name,
                        new GenericCallback<Path>() {
                            public void onSuccess(Path data) {
                                eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getModuleUUID() ) );
                                flushSuggestionCompletionCache(sel.getSelectedPackage(), null);
                                completedCopying( newName.getText(),
                                        sel.getSelectedPackage(),
                                        data );
                                form.hide();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if ( t.getMessage().indexOf( "ItemExistsException" ) > -1 ) { // NON-NLS
                                    Window.alert( Constants.INSTANCE.ThatNameIsInUsePleaseTryAnother() );
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
        Image image = new Image(DroolsGuvnorImageResources.INSTANCE.packageLarge());
        image.setAltText(Constants.INSTANCE.Package());
        final FormStylePopup pop = new FormStylePopup(image,
                Constants.INSTANCE.RenameThisItem() );
        final TextBox box = new TextBox();
        box.setText( asset.getName() );
        pop.addAttribute( Constants.INSTANCE.NewNameAsset(),
                box );
        Button ok = new Button( Constants.INSTANCE.RenameItem() );
        pop.addAttribute( "",
                ok );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
            	Path path = new PathImpl();
            	path.setUUID(asset.getUuid());
                assetService.renameAsset( path,
                        box.getText(),
                        new GenericCallback<Path>() {
                            public void onSuccess(Path path) {
                                Window.alert( Constants.INSTANCE.ItemHasBeenRenamed() );
                                eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getModuleUUID() ) );
                                clientFactory.getRefreshAssetEditorEvents().fire(new RefreshAssetEditorEvent(asset.getMetaData().getModuleName(), asset.getUuid()));
                                pop.hide();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if ( t.getMessage().indexOf( "ItemExistsException" ) > -1 ) { // NON-NLS
                                    Window.alert( Constants.INSTANCE.ThatNameIsInUsePleaseTryAnother() );
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
            Window.alert( Constants.INSTANCE.ItemAlreadyInGlobalArea() );
            return;
        }
        if ( Window.confirm( Constants.INSTANCE.PromoteAreYouSure() ) ) {
        	Path path = new PathImpl();
        	path.setUUID(asset.getUuid());
            assetService.promoteAssetToGlobalArea( path,
                    new GenericCallback<Void>() {
                        public void onSuccess(Void data) {
                            Window.alert( Constants.INSTANCE.Promoted() );

                            flushSuggestionCompletionCache(asset.getMetaData().getModuleName(), null);
                            flushSuggestionCompletionCache("globalArea", null);
                            eventBus.fireEvent( new RefreshModuleEditorEvent( asset.getMetaData().getModuleUUID() ) );
                            clientFactory.getRefreshAssetEditorEvents().fire(new RefreshAssetEditorEvent(asset.getMetaData().getModuleName(), asset.getUuid()));
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
                                  Path newAssetPath) {
        Window.alert( Constants.INSTANCE.CreatedANewItemSuccess( name,
                pkg ) );
        clientFactory.getPlaceManager().goTo( new AssetEditorPlace( newAssetPath.getUUID() ) );
    }
}
