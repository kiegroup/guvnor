package org.drools.guvnor.client.ruleeditor;

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

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

/**
 * The main layout parent/controller the rule viewer.
 *
 * @author Michael Neale
 */
public class RuleViewer extends GuvnorEditor {

    private Command                    closeCommand;
    public Command                     checkedInCommand;
    public ActionToolbar.CheckinAction checkInCommand;
    protected RuleAsset                asset;

    private boolean                    readOnly;

    private MetaDataWidget             metaWidget;
    private RuleDocumentWidget         doco;
    private Widget                     editor;

    private ActionToolbar              toolbar;
    private VerticalPanel              layout;
    private HorizontalPanel            hsp;

    private long                       lastSaved = System.currentTimeMillis();
    private Constants                  constants = ((Constants) GWT.create( Constants.class ));

    private final EditItemEvent        editEvent;

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset,
                      final EditItemEvent event) {
        this( asset,
              event,
              false );
    }

    RuleDocumentWidget getDoco() {
        return doco;
    }

    MetaDataWidget getMetaWidget() {
        return metaWidget;
    }

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset,
                      final EditItemEvent event,
                      boolean historicalReadOnly) {
        this.editEvent = event;
        this.asset = asset;
        this.readOnly = historicalReadOnly && asset.isreadonly;

        this.layout = new VerticalPanel();

        layout.setWidth( "100%" );
        layout.setHeight( "100%" );

        this.checkInCommand = new ActionToolbar.CheckinAction() {
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
        };

        initWidget( layout );

        doWidgets( null );

        LoadingPopup.close();
    }

    public boolean isDirty() {
        return (System.currentTimeMillis() - lastSaved) > 3600000;
    }

    /**
     * This will actually load up the data (this is called by the callback)
     * when we get the data back from the server,
     * also determines what widgets to load up).
     */
    private void doWidgets(Widget messageWidget) {
        layout.clear();

        editor = EditorLauncher.getEditorViewer( asset,
                                                 this );

        //the action widgets (checkin/close etc).
        toolbar = new ActionToolbar( asset,
                                     readOnly,
                                     editor,                                     
                                     checkInCommand,
                                     new ActionToolbar.CheckinAction() {
                                         public void doCheckin(String comment) {
                                             doArchive( comment );
                                         }
                                     },
                                     new Command() {
                                         public void execute() {
                                             doDelete();
                                         }
                                     },
                                     new Command() {
                                         public void execute() {
                                             close();
                                         }
                                     },
                                     new Command() {
                                         public void execute() {
                                             doCopy();
                                         }
                                     }, 
                                     new Command() {
                                         public void execute() {
                                             doPromptToGlobal();
                                         }
                                     });

        //layout.add(toolbar, DockPanel.NORTH);
        layout.add( toolbar );
        layout.setCellHeight( toolbar,
                              "30px" );
        layout.setCellHorizontalAlignment( toolbar,
                                           HasHorizontalAlignment.ALIGN_LEFT );
        layout.setCellWidth( toolbar,
                             "100%" );

        if ( messageWidget != null ) {
            layout.add( messageWidget );
        }

        doMetaWidget();

        hsp = new HorizontalPanel();

        layout.add( hsp );

        //the document widget
        doco = new RuleDocumentWidget( asset );

        VerticalPanel vert = new VerticalPanel();
        vert.add( editor );
        editor.setHeight( "100%" );
        //vert.add( doco );

        vert.setWidth( "100%" );
        vert.setHeight( "100%" );

        hsp.add( vert );

        //hsp.addStyleName("HorizontalSplitPanel");

        hsp.add( metaWidget );

        hsp.setCellWidth( metaWidget,
                          "25%" );

        //hsp.setSplitPosition("80%");
        hsp.setHeight( "100%" );

        layout.add( doco );
    }

    private void doMetaWidget() {
        metaWidget = new MetaDataWidget( this.asset.metaData,
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
                                                                   new GenericCallback() {
                                                                       public void onSuccess(Object o) {
                                                                           close();
                                                                       }
                                                                   } );
    }

    /**
     * This responds to the checkin command.
     */

    private void doArchive(String comment) {
        this.readOnly = true; //set to read only to not bother with the close warning
        this.asset.archived = true;
        this.performCheckIn( comment );
        this.closeCommand.execute();
    }

    private void performCheckIn(String comment) {
        //layout.clear();
        this.asset.metaData.checkinComment = comment;
        final boolean[] saved = {false};
        Timer t = new Timer() {
            public void run() {
                if ( !saved[0] ) LoadingPopup.showMessage( constants.SavingPleaseWait() );
            }
        };
        t.schedule( 500 );

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

                                                                      doco.resetDirty();

                                                                      // No need to refresh if we are archiving
                                                                      if ( asset.archived ) {
                                                                          LoadingPopup.close();
                                                                      } else {
                                                                          refreshMetaWidgetOnly( false );
                                                                      }
                                                                      LoadingPopup.close();
                                                                      saved[0] = true;

                                                                      toolbar.showSavedConfirmation();
                                                                  }
                                                              } );
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

    /**
     * This will reload the contents from the database, and refresh the widgets.
     */
    public void refreshDataAndView() {
        refreshDataAndView( null );
    }

    public void refreshDataAndView(final Widget messageWidget) {
        LoadingPopup.showMessage( constants.RefreshingItem() );
        RepositoryServiceFactory.getService().loadRuleAsset( asset.uuid,
                                                             new GenericCallback<RuleAsset>() {
                                                                 public void onSuccess(RuleAsset asset_) {
                                                                     asset = asset_;
                                                                     doWidgets( messageWidget );
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
                                                                     hsp.remove( metaWidget );
                                                                     doMetaWidget();
                                                                     hsp.add( metaWidget );
                                                                     hsp.setCellWidth( metaWidget,
                                                                                       "25%" );
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

    /**
     * Called when user wants to close, but there is "dirtyness".
     */
    protected void doCloseUnsavedWarning() {
        final FormStylePopup pop = new FormStylePopup( "images/warning-large.png", //NON-NLS
                                                       constants.WARNINGUnCommittedChanges() );
        Button dis = new Button( constants.Discard() );
        Button can = new Button( constants.Cancel() );
        HorizontalPanel hor = new HorizontalPanel();

        hor.add( dis );
        hor.add( can );

        pop.addRow( new HTML( constants.AreYouSureYouWantToDiscardChanges() ) );
        pop.addRow( hor );

        dis.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                closeCommand.execute();
                pop.hide();
            }
        } );

        can.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                pop.hide();
            }
        } );

        pop.show();
    }

    private void doCopy() {
        final FormStylePopup form = new FormStylePopup( "images/rule_asset.gif",
                                                        constants.CopyThisItem() );
        final TextBox newName = new TextBox();
        form.addAttribute( constants.NewName(),
                           newName );

        Button ok = new Button( constants.CreateCopy() );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                if ( newName.getText() == null || newName.getText().equals( "" ) ) {
                    Window.alert( constants.AssetNameMustNotBeEmpty() );
                    return;
                }
                String name = newName.getText().trim();
                if ( !NewAssetWizard.validatePathPerJSR170( name ) ) {
                    return;
                }
                RepositoryServiceFactory.getService().copyAsset( asset.uuid,
                                                                 asset.metaData.packageName,
                                                                 name,
                                                                 new GenericCallback<String>() {
                                                                     public void onSuccess(String data) {
                                                                         completedCopying( newName.getText(),
                                                                                           asset.metaData.packageName,
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

        //form.setPopupPosition((DirtyableComposite.getWidth() - form.getOffsetWidth()) / 2, 100);
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
        if (asset.metaData.packageName.equals("globalArea")) {
            Window.alert( constants.ItemAlreadyInGlobalArea() );
            return;
        }
        if (Window.confirm(constants.PromoteAreYouSure()) ) {
            RepositoryServiceFactory.getService().promoteAssetToGlobalArea(asset.uuid,
                    new GenericCallback<String>() {
                        public void onSuccess(String data) {
                            Window.alert(constants.Promoted());
                            refreshMetaWidgetOnly();
                        }

                        @Override
                        public void onFailure(Throwable t) {                           
                            super.onFailure( t );
                        }
                    } );

} ;
 	
    }
}