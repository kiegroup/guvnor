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
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.QuickTipsConfig;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.StatusChangePopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;


/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 * @author Michael Neale
 */
public class ActionToolbar extends Composite {

	private Toolbar		toolbar;
    private CheckinAction checkinAction;
    private CheckinAction archiveAction;
    private Command deleteAction;
    private ToolbarTextItem state;
	final private RuleAsset asset;
	private Command afterCheckinEvent;
    private Constants constants = GWT.create(Constants.class);
    private SmallLabel savedOK;

    public ActionToolbar(final RuleAsset asset,
                         final CheckinAction checkin,
                         final CheckinAction archiv,
                         final Command delete, boolean readOnly) {

        this.checkinAction = checkin;
        this.archiveAction = archiv;
        this.deleteAction = delete;
        this.asset = asset;

        this.state = new ToolbarTextItem(constants.Status() + " ");

        toolbar = new Toolbar();


        String status = asset.metaData.status;

        setState(status);

        if (!readOnly && !asset.isreadonly) {
        	controls();
        }

        toolbar.addItem(this.state);

        initWidget( toolbar );
    }


    /**
     * Show the saved OK message for a little while *.
     */
    public void showSavedConfirmation() {
        savedOK.setVisible(true);
        Timer t = new Timer() {
            public void run() {
                savedOK.setVisible(false);
            }
        };
        t.schedule(1500);
    }

    /**
     * Sets the visible status display.
     */
    private void setState(String status) {
        state.setText(Format.format(constants.statusIs(), status));
    }

    private void controls() {

	    	ToolbarButton save = new ToolbarButton();
	    	save.setText(constants.SaveChanges());
			save.setTooltip(getTip(constants.CommitAnyChangesForThisAsset()));
			save.addListener(new ButtonListenerAdapter() {
		        			public void onClick(
		        					com.gwtext.client.widgets.Button button,
		        					EventObject e) {
		                        	doCheckinConfirm(button);
	        				}
		        			});
			toolbar.addButton(save);


        savedOK = new SmallLabel("<font color='green'>" + constants.SavedOK() + "</font>");
        savedOK.setVisible(false);
        toolbar.addElement(savedOK.getElement());

        toolbar.addFill();
        toolbar.addSeparator();

		ToolbarButton copy = new ToolbarButton();
		copy.setText(constants.Copy());
		copy.setTooltip(constants.CopyThisAsset());
		copy.addListener(new ButtonListenerAdapter() {
			public void onClick(
					com.gwtext.client.widgets.Button button,
					EventObject e) {
                	doCopyDialog(button);
			}
			});
		toolbar.addButton(copy);


		ToolbarButton archive = new ToolbarButton();
		archive.setText(constants.Archive());
		archive.setTooltip(getTip(constants.ArchiveThisAssetThisWillNotPermanentlyDeleteIt()));
		archive.addListener(new ButtonListenerAdapter() {
			public void onClick(
					com.gwtext.client.widgets.Button button,
					EventObject e) {
                        if (Window.confirm(constants.AreYouSureYouWantToArchiveThisItem())) {
                            archiveAction.doCheckin(constants.ArchivedItemOn() + new java.util.Date().toString());
                        }
			}
			});
		toolbar.addButton(archive);




        if (notCheckedInYet()) {

        	final ToolbarButton delete = new ToolbarButton();
        	delete.setText(constants.Delete());
    		delete.setTooltip(getTip(constants.DeleteAssetTooltip()));
    		delete.addListener(new ButtonListenerAdapter() {
    			public void onClick(
    					com.gwtext.client.widgets.Button button,
    					EventObject e) {
                            if (Window.confirm(constants.DeleteAreYouSure()) ) {
                                deleteAction.execute();
                            }
				}
    			});
    		toolbar.addButton(delete);

    		this.afterCheckinEvent = new Command() {

				public void execute() {
					delete.setVisible(false);
				}

    		};

        }





        ToolbarButton stateChange = new ToolbarButton();
        stateChange.setText(constants.ChangeStatus());
		stateChange.setTooltip(getTip(constants.ChangeStatusTip()));
		stateChange.addListener(new ButtonListenerAdapter() {
			public void onClick(
					com.gwtext.client.widgets.Button button,
					EventObject e) {
				showStatusChanger(button);
			}
			});

		toolbar.addButton(stateChange);
    }

	private boolean notCheckedInYet() {
		return asset.metaData.versionNumber == 0;
	}

	private QuickTipsConfig getTip(final String t) {
		return new QuickTipsConfig() {
			{
				setText(t);
			}

		};
	}



    protected void doCopyDialog(Widget w) {
        final FormStylePopup form = new FormStylePopup("images/rule_asset.gif", constants.CopyThisItem());
        final TextBox newName = new TextBox();
        form.addAttribute(constants.NewName(), newName );

        Button ok = new Button(constants.CreateCopy());
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	if (newName.getText() == null || newName.getText().equals("")) {
            		Window.alert(constants.AssetNameMustNotBeEmpty());
            		return;
            	}
                String name = newName.getText().trim();
                if (!NewAssetWizard.validatePathPerJSR170(name)) return;
                RepositoryServiceFactory.getService().copyAsset( asset.uuid, asset.metaData.packageName, name,
                                                                 new GenericCallback<String>() {
                                                                    public void onSuccess(String data) {
                                                                        completedCopying(newName.getText(), asset.metaData.packageName);
                                                                        form.hide();
                                                                    }

                                                                     @Override
                                                                     public void onFailure(Throwable t) {
                                                                         if (t.getMessage().indexOf("ItemExistsException") > -1) { //NON-NLS
                                                                             Window.alert(constants.ThatNameIsInUsePleaseTryAnother());
                                                                         } else {
                                                                             super.onFailure(t);
                                                                         }
                                                                     }
                                                                 });
            }
        } );
        form.addAttribute( "", ok );

		//form.setPopupPosition((DirtyableComposite.getWidth() - form.getOffsetWidth()) / 2, 100);
		form.show();

    }

    private void completedCopying(String name, String pkg) {
        Window.alert( Format.format(constants.CreatedANewItemSuccess(), name, pkg) );

    }

    /**
     * Called when user wants to checkin.
     */
    protected void doCheckinConfirm(Widget w) {
        final CheckinPopup pop = new CheckinPopup(w.getAbsoluteLeft(), w.getAbsoluteTop(), constants.CheckInChanges());
        pop.setCommand( new Command() {
            public void execute() {
                checkinAction.doCheckin(pop.getCheckinComment());
                if (afterCheckinEvent != null) afterCheckinEvent.execute();
            }
        });
        pop.show();

    }



	/**
     * Show the stats change popup.
     */
    private void showStatusChanger(Widget w) {
        final StatusChangePopup pop = new StatusChangePopup(asset.uuid, false);
        pop.setChangeStatusEvent(new Command() {
            public void execute() {
                setState( pop.getState() );
            }
        });

        pop.show();
    }



    public static interface CheckinAction {
    	void doCheckin(String comment);
    }


}