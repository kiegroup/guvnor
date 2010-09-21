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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.*;
import static org.drools.guvnor.client.common.AssetFormats.*;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.qa.VerifierResultWidget;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.client.rpc.VerificationServiceAsync;
import org.drools.guvnor.client.explorer.ExplorerLayoutManager;
import org.drools.guvnor.client.modeldriven.ui.RuleModelEditor;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.util.Format;
import org.drools.guvnor.client.packages.PackageBuilderWidget;
import org.drools.guvnor.client.packages.WorkingSetManager;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 * @author Michael Neale
 */
public class ActionToolbar extends Composite {

    private Constants       constants          = GWT.create( Constants.class );

    static String[]         VALIDATING_FORMATS = new String[]{BUSINESS_RULE, DSL_TEMPLATE_RULE, DECISION_SPREADSHEET_XLS, DRL, ENUMERATION, DECISION_TABLE_GUIDED, DRL_MODEL, DSL, FUNCTION, RULE_TEMPLATE};

    static String[]         VERIFY_FORMATS     = new String[]{BUSINESS_RULE, DECISION_SPREADSHEET_XLS, DRL, DECISION_TABLE_GUIDED, DRL_MODEL, RULE_TEMPLATE};

    private MenuBar         toolbar            = new MenuBar();
    private CheckinAction   checkinAction;
    private Command         archiveAction;
    private Command         deleteAction;
    private MenuItem        state              = new MenuItem( constants.Status() + " ",
                                                               new Command() {
                                                                   public void execute() {
                                                                       // Why can't there be a simple text menu item.
                                                                   }
                                                               } );
    final private RuleAsset asset;
    private Command         afterCheckinEvent;
    private MenuItem        savedOK;
    private Widget          editor;
    private Command         closeCommand;
    private Command         copyCommand;
    private Command         promptCommand;

    public ActionToolbar(final RuleAsset asset,
                         boolean readOnly,
                         Widget editor,
                         final CheckinAction checkin,
                         final Command archiv,
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
        MenuItem save = new MenuItem( constants.SaveChanges(),
                                      new Command() {
                                          public void execute() {
                                              verifyAndDoCheckinConfirm( false );
                                          }
                                      } );

        save.setTitle( constants.CommitAnyChangesForThisAsset() );
        toolbar.addItem( save );

        MenuItem saveAndClose = new MenuItem( constants.SaveAndClose(),
                                              new Command() {
                                                  public void execute() {
                                                      verifyAndDoCheckinConfirm( true );
                                                  }
                                              } );
        saveAndClose.setTitle( constants.CommitAnyChangesForThisAsset() );
        toolbar.addItem( saveAndClose );

        savedOK = new MenuItem( constants.SavedOK(),
                                new Command() {
                                    public void execute() {
                                        // Nothing here
                                        // TODO : MOVE THIS TEXT TO INFO AREA
                                    }
                                } );
        savedOK.setVisible( false );

        toolbar.addItem( savedOK );

        toolbar.addSeparator();

        MenuBar moreMenu = new MenuBar( true );
        moreMenu.addItem( constants.Copy(),
                          new Command() {
                              public void execute() {
                                  copyCommand.execute();
                              }
                          } );
        moreMenu.addItem( constants.PromoteToGlobal(),
                          new Command() {
                              public void execute() {
                                  promptCommand.execute();
                              }
                          } );
        moreMenu.addItem( constants.Archive(),
                          new Command() {
                              public void execute() {
                                  if ( Window.confirm( constants.AreYouSureYouWantToArchiveThisItem() + "\n" + constants.ArchiveThisAssetThisWillNotPermanentlyDeleteIt() ) ) {
                                      archiveAction.execute();
                                  }
                              }
                          } );

        final MenuItem deleteItem = new MenuItem( constants.Delete(),
                                                  new Command() {
                                                      public void execute() {
                                                          if ( Window.confirm( constants.DeleteAreYouSure() ) ) {
                                                              deleteAction.execute();
                                                          }
                                                      }
                                                  } );
        moreMenu.addItem( deleteItem );
        deleteItem.setTitle( constants.DeleteAssetTooltip() );
        this.afterCheckinEvent = new Command() {
            public void execute() {
                deleteItem.setVisible( true );
            }
        };

        if ( !notCheckedInYet() ) {
            deleteItem.setVisible( true );
        }

        moreMenu.addItem( constants.ChangeStatus(),
                          new Command() {
                              public void execute() {
                                  showStatusChanger();
                              }
                          } );

        if ( isValidatorTypeAsset() ) {

            if ( editor instanceof RuleModelEditor ) {
                toolbar.addItem( new MenuItem( constants.SelectWorkingSets(),
                                               new Command() {
                                                   public void execute() {
                                                       showWorkingSetsSelection( ((RuleModelEditor) editor).getRuleModeller() );
                                                   }
                                               } ) );
            }

            toolbar.addItem( constants.Validate(),
                             new Command() {
                                 public void execute() {
                                     doValidate();
                                 }
                             } );

            if ( isVerificationTypeAsset() ) {
                toolbar.addItem( constants.Verify(),
                                 new Command() {
                                     public void execute() {
                                         doVerify();
                                     }
                                 } );
            }

            if ( shouldShowViewSource() ) {
                toolbar.addItem( constants.ViewSource(),
                                 new Command() {
                                     public void execute() {
                                         doViewsource();
                                     }
                                 } );
            }
        }

        toolbar.addItem( constants.MoreDotDot(),
                         moreMenu );
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
        Set<String> activeWorkingSets = null;
        activeWorkingSets = WorkingSetManager.getInstance().getActiveAssetUUIDs( asset.metaData.packageName );

        VerificationServiceAsync verificationService = GWT.create( VerificationService.class );

        verificationService.verifyAsset( asset,
                                         activeWorkingSets,
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

    protected void showWorkingSetsSelection(RuleModeller modeller) {
        new WorkingSetSelectorPopup( modeller,
                                     asset ).show();
    }

    public static interface CheckinAction {
        void doCheckin(String comment);
    }
}