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
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.RuleModelEditor;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.packages.PackageBuilderWidget;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.packages.WorkingSetManager;
import org.drools.guvnor.client.qa.VerifierResultWidget;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.client.rpc.VerificationServiceAsync;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbar;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.client.ruleeditor.toolbar.DefaultActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main layout parent/controller the rule viewer.
 *
 * @author Michael Neale
 */
public class RuleViewer extends GuvnorEditor {

    private Constants     constants = GWT.create( Constants.class );
    private static Images images    = GWT.create( Images.class );

    interface RuleViewerBinder
        extends
        UiBinder<Widget, RuleViewer> {
    }

    private static RuleViewerBinder                   uiBinder  = GWT.create( RuleViewerBinder.class );

    @UiField(provided = true)
    final MetaDataWidget                              metaWidget;

    @UiField(provided = true)
    final RuleDocumentWidget                          ruleDocumentWidget;

    @UiField(provided = true)
    final EditorWidget                                editor;

    @UiField(provided = true)
    final ActionToolbar                               toolbar;

    @UiField
    MessageWidget                                     messageWidget;

    private Command                                   afterCheckinEvent;
    private Command                                   closeCommand;
    private Command                                   archiveCommand;
    public Command                                    checkedInCommand;
    protected RuleAsset                               asset;

    private boolean                                   readOnly;

    private final RuleViewerSettings                  ruleViewerSettings;

    private long                                      lastSaved = System.currentTimeMillis();

    private final EditItemEvent                       editEvent;

    private ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider;

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset,
                      final EditItemEvent event) {
        this( asset,
              event,
              false,
              null,
              null );
    }

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset,
                      final EditItemEvent event,
                      boolean historicalReadOnly) {
        this( asset,
              event,
              historicalReadOnly,
              null,
              null );
    }

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     * @param actionToolbarButtonsConfigurationProvider used to change the default
     * button configuration provider.
     */
    public RuleViewer(RuleAsset asset,
                      final EditItemEvent event,
                      boolean historicalReadOnly,
                      ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider,
                      RuleViewerSettings ruleViewerSettings) {
        this.editEvent = event;
        this.asset = asset;
        this.readOnly = historicalReadOnly && asset.isreadonly;

        if ( ruleViewerSettings == null ) {
            this.ruleViewerSettings = new RuleViewerSettings();
        } else {
            this.ruleViewerSettings = ruleViewerSettings;
        }

        this.actionToolbarButtonsConfigurationProvider = actionToolbarButtonsConfigurationProvider;

        ruleDocumentWidget = new RuleDocumentWidget( asset,
                                                     this.ruleViewerSettings.isDocoVisible() );

        metaWidget = createMetaWidget();

        metaWidget.setVisible( this.ruleViewerSettings.isMetaVisible() );

        editor = EditorLauncher.getEditorViewer( asset,
                                                 this );

        toolbar = new ActionToolbar( getConfiguration(),
                                     asset.metaData.status );

        initWidget( uiBinder.createAndBindUi( this ) );

        doWidgets();

        LoadingPopup.close();
    }

    public void setDocoVisible(boolean docoVisible) {
        this.ruleViewerSettings.setDocoVisible( docoVisible );
        this.ruleDocumentWidget.setVisible( docoVisible );
    }

    public void setMetaVisible(boolean metaVisible) {
        this.ruleViewerSettings.setMetaVisible( metaVisible );
        this.metaWidget.setVisible( metaVisible );
    }

    @Override
    public boolean isDirty() {
        return (System.currentTimeMillis() - lastSaved) > 3600000;
    }

    private ActionToolbarButtonsConfigurationProvider getConfiguration() {
        if ( actionToolbarButtonsConfigurationProvider == null ) {
            return new DefaultActionToolbarButtonsConfigurationProvider( asset,
                                                                         this );
        } else {
            return actionToolbarButtonsConfigurationProvider;
        }

    }

    /**
     * This will actually load up the data (this is called by the callback)
     * when we get the data back from the server,
     * also determines what widgets to load up).
     */
    private void doWidgets() {

        //the action widgets (checkin/close etc).
        if ( readOnly || asset.isreadonly ) {
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
            toolbar.setArciveCommand( new Command() {
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
                    onSave();
                    LoadingPopup.showMessage( constants.CalculatingSource() );
                    RepositoryServiceFactory.getService().buildAssetSource( asset,
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
                    RepositoryServiceFactory.getService().buildAsset( asset,
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
     * Show the stats change popup.
     */
    private void showStatusChanger() {
        final StatusChangePopup pop = new StatusChangePopup( asset.uuid,
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
                    if ( ((RuleModeller) editor).hasVerifierErrors() || ((RuleModeller) editor).hasVerifierWarnings() ) {
                        if ( !Window.confirm( constants.theRuleHasErrorsOrWarningsDotDoYouWantToContinue() ) ) {
                            return;
                        }
                    }
                    doCheckinConfirm( closeAfter );
                }
            } );
        } else {
            doCheckinConfirm( closeAfter );
        }
    }

    /**
     * Called when user wants to checkin.
     * set closeAfter to true if it should close this whole thing after saving it.
     */
    protected void doCheckinConfirm(final boolean closeAfter) {
        final CheckinPopup pop = new CheckinPopup( constants.CheckInChanges() );
        pop.setCommand( new Command() {

            public void execute() {
                doCheckin( pop.getCheckinComment() );
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

    public void doCheckin(String comment) {
        if ( editor instanceof SaveEventListener ) {
            ((SaveEventListener) editor).onSave();
        }
        performCheckIn( comment );
        if ( editor instanceof SaveEventListener ) {
            ((SaveEventListener) editor).onAfterSave();
        }
        if ( checkedInCommand != null ) {
            checkedInCommand.execute();
        }
        lastSaved = System.currentTimeMillis();
        resetDirty();
    }

    private void doVerify() {
        onSave();
        LoadingPopup.showMessage( constants.VerifyingItemPleaseWait() );
        Set<String> activeWorkingSets = null;
        activeWorkingSets = WorkingSetManager.getInstance().getActiveAssetUUIDs( asset.metaData.packageName );

        VerificationServiceAsync verificationService = GWT.create( VerificationService.class );

        verificationService.verifyAsset( asset,
                                         activeWorkingSets,
                                         new AsyncCallback<AnalysisReport>() {

                                             public void onSuccess(AnalysisReport report) {
                                                 LoadingPopup.close();
                                                 final FormStylePopup form = new FormStylePopup( images.ruleAsset(),
                                                                                                 constants.VerificationReport() );
                                                 ScrollPanel scrollPanel = new ScrollPanel( new VerifierResultWidget( report,
                                                                                                                      false ) );
                                                 scrollPanel.setWidth( "100%" );
                                                 form.addRow( scrollPanel );

                                                 LoadingPopup.close();
                                                 form.show();
                                             }

                                             public void onFailure(Throwable arg0) {
                                                 // TODO Auto-generated method stub
                                             }
                                         } );

    }

    private void showSource(String src) {
        PackageBuilderWidget.showSource( src,
                                         this.asset.metaData.name );
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

    private MetaDataWidget createMetaWidget() {
        return new MetaDataWidget( this.asset.metaData,
                                   readOnly,
                                   this.asset.uuid,
                                   new Command() {
                                       public void execute() {
                                           refreshMetaWidgetOnly();
                                       }
                                   },
                                   new Command() {
                                       public void execute() {
                                           refreshDataAndView();
                                       }
                                   } );

    }

    protected boolean hasDirty() {
        //not sure how to implement this now.
        return false;
    }

    /** closes itself */
    private void close() {
        closeCommand.execute();
    }

    void doDelete() {
        readOnly = true; //set to not cause the extra confirm popup
        RepositoryServiceFactory.getService().deleteUncheckedRule( this.asset.uuid,
                                                                   this.asset.metaData.packageName,
                                                                   new GenericCallback<Void>() {
                                                                       public void onSuccess(Void o) {
                                                                           close();
                                                                       }
                                                                   } );
    }

    private void doArchive() {
        RepositoryServiceFactory.getService().archiveAsset( asset.uuid,
                                                            new GenericCallback<Void>() {
                                                                public void onSuccess(Void o) {
                                                                    if ( archiveCommand != null ) {
                                                                        archiveCommand.execute();
                                                                    }
                                                                    close();
                                                                }
                                                            } );
    }

    private void performCheckIn(String comment) {
        this.asset.metaData.checkinComment = comment;
        final boolean[] saved = {false};

        if ( !saved[0] ) LoadingPopup.showMessage( constants.SavingPleaseWait() );

        RepositoryServiceFactory.getService().checkinVersion( this.asset,
                                                              new GenericCallback<String>() {

                                                                  public void onSuccess(String uuid) {
                                                                      if ( uuid == null ) {
                                                                          ErrorPopup.showMessage( constants.FailedToCheckInTheItemPleaseContactYourSystemAdministrator() );
                                                                          return;
                                                                      }

                                                                      if ( uuid.startsWith( "ERR" ) ) { //NON-NLS
                                                                          ErrorPopup.showMessage( uuid.substring( 5 ) );
                                                                          return;
                                                                      }

                                                                      flushSuggestionCompletionCache();

                                                                      if ( editor instanceof DirtyableComposite ) {
                                                                          ((DirtyableComposite) editor).resetDirty();
                                                                      }

                                                                      ruleDocumentWidget.resetDirty();

                                                                      refreshMetaWidgetOnly( false );

                                                                      LoadingPopup.close();
                                                                      saved[0] = true;

                                                                      showInfoMessage( constants.SavedOK() );
                                                                  }
                                                              } );
    }

    public void showInfoMessage(String message) {
        messageWidget.showMessage( message );
    }

    /**
     * In some cases we will want to flush the package dependency stuff for suggestion completions.
     * The user will still need to reload the asset editor though.
     */
    public void flushSuggestionCompletionCache() {
        if ( AssetFormats.isPackageDependency( this.asset.metaData.format ) ) {
            LoadingPopup.showMessage( constants.RefreshingContentAssistance() );
            SuggestionCompletionCache.getInstance().refreshPackage( this.asset.metaData.packageName,
                                                                    new Command() {
                                                                        public void execute() {
                                                                            LoadingPopup.close();
                                                                        }
                                                                    } );
        }
    }

    public void refreshDataAndView() {
        LoadingPopup.showMessage( constants.RefreshingItem() );
        RepositoryServiceFactory.getService().loadRuleAsset( asset.uuid,
                                                             new GenericCallback<RuleAsset>() {
                                                                 public void onSuccess(RuleAsset asset_) {
                                                                     asset = asset_;
                                                                     doWidgets();
                                                                     LoadingPopup.close();
                                                                 }
                                                             } );
    }

    /**
     * This will only refresh the meta data widget if necessary.
     */
    public void refreshMetaWidgetOnly() {
        refreshMetaWidgetOnly( true );
    }

    private void refreshMetaWidgetOnly(final boolean showBusy) {

        if ( showBusy ) LoadingPopup.showMessage( constants.RefreshingItem() );
        RepositoryServiceFactory.getService().loadRuleAsset( asset.uuid,
                                                             new GenericCallback<RuleAsset>() {
                                                                 public void onSuccess(RuleAsset asset_) {
                                                                     asset.metaData = asset_.metaData;
                                                                     metaWidget.refresh();
                                                                     if ( showBusy ) LoadingPopup.close();
                                                                 }
                                                             } );
    }

    /**
     * This needs to be called to allow the opened viewer to close itself.
     * @param c
     */
    public void setCloseCommand(Command c) {
        this.closeCommand = c;
    }

    /**
     * This is called when this viewer saves something.
     * @param c
     */
    public void setCheckedInCommand(Command c) {
        this.checkedInCommand = c;
    }

    public void setArchiveCommand(Command c) {
        this.archiveCommand = c;
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
                if ( newName.getText() == null || newName.getText().equals( "" ) ) {
                    Window.alert( constants.AssetNameMustNotBeEmpty() );
                    return;
                }
                String name = newName.getText().trim();
                if ( !NewAssetWizard.validatePathPerJSR170( name ) ) {
                    return;
                }
                RepositoryServiceFactory.getService().copyAsset( asset.uuid,
                                                                 sel.getSelectedPackage(),
                                                                 name,
                                                                 new GenericCallback<String>() {
                                                                     public void onSuccess(String data) {
                                                                         completedCopying( newName.getText(),
                                                                                           sel.getSelectedPackage(),
                                                                                           data );
                                                                         form.hide();
                                                                     }

                                                                     @Override
                                                                     public void onFailure(Throwable t) {
                                                                         if ( t.getMessage().indexOf( "ItemExistsException" ) > -1 ) { //NON-NLS
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

    private void completedCopying(String name,
                                  String pkg,
                                  String newAssetUUID) {
        Window.alert( Format.format( constants.CreatedANewItemSuccess(),
                                     name,
                                     pkg ) );
        if ( editEvent != null ) {
            editEvent.open( newAssetUUID );
        }
    }

    private void doPromptToGlobal() {
        if ( asset.metaData.packageName.equals( "globalArea" ) ) {
            Window.alert( constants.ItemAlreadyInGlobalArea() );
            return;
        }
        if ( Window.confirm( constants.PromoteAreYouSure() ) ) {
            RepositoryServiceFactory.getService().promoteAssetToGlobalArea( asset.uuid,
                                                                            new GenericCallback<Void>() {
                                                                                public void onSuccess(Void data) {
                                                                                    Window.alert( constants.Promoted() );
                                                                                    refreshMetaWidgetOnly();
                                                                                }

                                                                                @Override
                                                                                public void onFailure(Throwable t) {
                                                                                    super.onFailure( t );
                                                                                }
                                                                            } );

        }

    }
}