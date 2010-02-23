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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.*;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import org.drools.guvnor.client.common.*;
import static org.drools.guvnor.client.common.AssetFormats.*;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.qa.VerifierResultWidget;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.explorer.ExplorerLayoutManager;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.packages.PackageBuilderWidget;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 * @author Michael Neale
 */
public class ActionToolbar extends Composite {

    static String[]         VALIDATING_FORMATS = new String[]{BUSINESS_RULE, DSL_TEMPLATE_RULE, DECISION_SPREADSHEET_XLS, DRL, ENUMERATION, DECISION_TABLE_GUIDED, DRL_MODEL, DSL, FUNCTION};

    static String[]         VERIFY_FORMATS     = new String[]{BUSINESS_RULE, DECISION_SPREADSHEET_XLS, DRL, DECISION_TABLE_GUIDED, DRL_MODEL};

    private Toolbar         toolbar;
    private CheckinAction   checkinAction;
    private CheckinAction   archiveAction;
    private Command         deleteAction;
    private ToolbarTextItem state;
    final private RuleAsset asset;
    private Command         afterCheckinEvent;
    private Constants       constants          = GWT.create( Constants.class );
    private SmallLabel      savedOK;
    private Widget          editor;
    private Command         closeCommand;
    private Command         copyCommand;
    private Command         promptCommand;

    public ActionToolbar(final RuleAsset asset,
                         boolean readOnly,
                         Widget editor,
                         final CheckinAction checkin,
                         final CheckinAction archiv,
                         final Command delete,
                         Command closeCommand,
                         Command copyCommand,
                         Command promptCommand) {

        this.checkinAction = checkin;
        this.archiveAction = archiv;
        this.deleteAction = delete;
        this.asset = asset;
        this.editor = editor;
        this.closeCommand = closeCommand;
        this.copyCommand = copyCommand;
        this.promptCommand = promptCommand;

        this.state = new ToolbarTextItem( constants.Status() + " " );

        toolbar = new Toolbar();

        String status = asset.metaData.status;

        setState( status );

        if ( !readOnly && !asset.isreadonly ) {
            controls();
        }

        toolbar.addItem( this.state );

        initWidget( toolbar );
    }

    /**
     * Show the saved OK message for a little while *.
     */
    public void showSavedConfirmation() {
        savedOK.setVisible( true );
        Timer t = new Timer() {
            public void run() {
                savedOK.setVisible( false );
            }
        };
        t.schedule( 1500 );
    }

    /**
     * Sets the visible status display.
     */
    private void setState(String status) {
        state.setText( Format.format( constants.statusIs(),
                                      status ) );
    }

    private void controls() {
        ToolbarButton save = new ToolbarButton();
        save.setText( constants.SaveChanges() );
        save.setTooltip( getTip( constants.CommitAnyChangesForThisAsset() ) );
        save.addListener( new ButtonListenerAdapter() {
            public void onClick(com.gwtext.client.widgets.Button button,
                                EventObject e) {
                doCheckinConfirm( button,
                                  false );
            }
        } );
        toolbar.addButton( save );

        ToolbarButton saveAndClose = new ToolbarButton();
        saveAndClose.setText( constants.SaveAndClose() );
        saveAndClose.setTooltip( getTip( constants.CommitAnyChangesForThisAsset() ) );
        saveAndClose.addListener( new ButtonListenerAdapter() {
            public void onClick(com.gwtext.client.widgets.Button button,
                                EventObject e) {
                doCheckinConfirm( button,
                                  true );
            }
        } );
        toolbar.addButton( saveAndClose );

        savedOK = new SmallLabel( "<font color='green'>" + constants.SavedOK() + "</font>" );
        savedOK.setVisible( false );
        toolbar.addElement( savedOK.getElement() );

        toolbar.addFill();
        toolbar.addSeparator();

        Menu moreMenu = new Menu();
        moreMenu.addItem( new Item( constants.Copy(),
                                    new BaseItemListenerAdapter() {
                                        @Override
                                        public void onClick(BaseItem baseItem,
                                                            EventObject eventObject) {
                                            copyCommand.execute();
                                        }
                                    } ) );
        moreMenu.addItem( new Item( constants.PromoteToGlobal(),
                                    new BaseItemListenerAdapter() {
                                        @Override
                                        public void onClick(BaseItem baseItem,
                                                            EventObject eventObject) {
                                            promptCommand.execute();
                                        }
                                    } ) );
        moreMenu.addItem( new Item( constants.Archive(),
                                    new BaseItemListenerAdapter() {
                                        @Override
                                        public void onClick(BaseItem baseItem,
                                                            EventObject eventObject) {
                                            if ( Window.confirm( constants.AreYouSureYouWantToArchiveThisItem() + "\n" + constants.ArchiveThisAssetThisWillNotPermanentlyDeleteIt() ) ) {
                                                archiveAction.doCheckin( constants.ArchivedItemOn() + new java.util.Date().toString() );
                                            }
                                        }
                                    } ) );

        final Item deleteItem = new Item( constants.Delete(),
                                          new BaseItemListenerAdapter() {
                                              @Override
                                              public void onClick(BaseItem baseItem,
                                                                  EventObject eventObject) {
                                                  if ( Window.confirm( constants.DeleteAreYouSure() ) ) {
                                                      deleteAction.execute();
                                                  }
                                              }
                                          } );
        moreMenu.addItem( deleteItem );
        deleteItem.setTitle( constants.DeleteAssetTooltip() );
        this.afterCheckinEvent = new Command() {
            public void execute() {
                deleteItem.setDisabled( true );
            }
        };

        if ( !notCheckedInYet() ) {
            deleteItem.setDisabled( true );
        }

        moreMenu.addItem( new Item( constants.ChangeStatus(),
                                    new BaseItemListenerAdapter() {
                                        @Override
                                        public void onClick(BaseItem baseItem,
                                                            EventObject eventObject) {
                                            showStatusChanger();
                                        }
                                    } ) );

        ToolbarMenuButton more = new ToolbarMenuButton( constants.Actions(),
                                                        moreMenu );

        if ( isValidatorTypeAsset() ) {
            ToolbarButton validate = new ToolbarButton();
            validate.setText( constants.Validate() );
            validate.addListener( new ButtonListenerAdapter() {
                public void onClick(com.gwtext.client.widgets.Button button,
                                    EventObject e) {
                    doValidate();
                }
            } );
            toolbar.addButton( validate );

            if ( isVerificationTypeAsset() ) {
                ToolbarButton verify = new ToolbarButton();
                verify.setText( constants.Verify() );
                verify.addListener( new ButtonListenerAdapter() {
                    public void onClick(com.gwtext.client.widgets.Button button,
                                        EventObject e) {
                        doVerify();
                    }
                } );
                toolbar.addButton( verify );

            }

            if ( shouldShowViewSource() ) {
                ToolbarButton viewSource = new ToolbarButton();
                viewSource.setText( constants.ViewSource() );
                viewSource.addListener( new ButtonListenerAdapter() {
                    public void onClick(com.gwtext.client.widgets.Button button,
                                        EventObject e) {
                        doViewsource();
                    }
                } );
                toolbar.addButton( viewSource );
            }
        }

        toolbar.addButton( more );
    }

    private boolean shouldShowViewSource() {
        return ExplorerLayoutManager.shouldShow( Capabilities.SHOW_PACKAGE_VIEW );
    }

    private void doViewsource() {
        onSave();
        LoadingPopup.showMessage( constants.CalculatingSource() );
        RepositoryServiceFactory.getService().buildAssetSource( this.asset,
                                                                new GenericCallback<String>() {
                                                                    public void onSuccess(String src) {
                                                                        showSource( src );
                                                                    }
                                                                } );
    }

    private void showSource(String src) {
        PackageBuilderWidget.showSource( src,
                                         this.asset.metaData.name );
        LoadingPopup.close();
    }

    private void doVerify() {
        onSave();
        LoadingPopup.showMessage( constants.VerifyingItemPleaseWait() );
        RepositoryServiceFactory.getService().verifyAsset( asset,
                                                           new AsyncCallback<AnalysisReport>() {

                                                               public void onSuccess(AnalysisReport report) {
                                                                   LoadingPopup.close();
                                                                   final FormStylePopup form = new FormStylePopup( "images/rule_asset.gif",
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

    private void doValidate() {
        onSave();
        LoadingPopup.showMessage( constants.ValidatingItemPleaseWait() );
        RepositoryServiceFactory.getService().buildAsset( asset,
                                                          new GenericCallback<BuilderResult>() {
                                                              public void onSuccess(BuilderResult results) {
                                                                  RuleValidatorWrapper.showBuilderErrors( results );
                                                              }
                                                          } );
    }

    public void onSave() {
        if ( editor instanceof SaveEventListener ) {
            SaveEventListener el = (SaveEventListener) editor;
            el.onSave();
        }
    }

    private boolean isValidatorTypeAsset() {
        String format = asset.metaData.format;
        for ( String fmt : VALIDATING_FORMATS ) {
            if ( fmt.equals( format ) ) return true;
        }
        return false;
    }

    private boolean isVerificationTypeAsset() {
        String format = asset.metaData.format;
        for ( String fmt : VERIFY_FORMATS ) {
            if ( fmt.equals( format ) ) return true;
        }
        return false;
    }

    private boolean notCheckedInYet() {
        return asset.metaData.versionNumber == 0;
    }

    private QuickTipsConfig getTip(final String t) {
        return new QuickTipsConfig() {
            {
                setText( t );
            }
        };
    }

    /**
     * Called when user wants to checkin.
     * set closeAfter to true if it should close this whole thing after saving it.
     */
    protected void doCheckinConfirm(Widget w,
                                    final boolean closeAfter) {
        final CheckinPopup pop = new CheckinPopup( w.getAbsoluteLeft(),
                                                   w.getAbsoluteTop(),
                                                   constants.CheckInChanges() );
        pop.setCommand( new Command() {
            public void execute() {
                checkinAction.doCheckin( pop.getCheckinComment() );
                if ( afterCheckinEvent != null ) afterCheckinEvent.execute();
                if ( closeAfter ) closeCommand.execute();
            }
        } );
        pop.show();
    }

    /**
     * Show the stats change popup.
     */
    private void showStatusChanger() {
        final StatusChangePopup pop = new StatusChangePopup( asset.uuid,
                                                             false );
        pop.setChangeStatusEvent( new Command() {
            public void execute() {
                setState( pop.getState() );
            }
        } );

        pop.show();
    }

    public static interface CheckinAction {
        void doCheckin(String comment);
    }
}