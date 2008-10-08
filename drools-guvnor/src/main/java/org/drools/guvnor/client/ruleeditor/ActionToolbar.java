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




import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.StatusChangePopup;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.QuickTipsConfig;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;

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

    public ActionToolbar(final RuleAsset asset,
                         final CheckinAction checkin,
                         final CheckinAction archiv,
                         final Command delete, boolean readOnly) {

        this.checkinAction = checkin;
        this.archiveAction = archiv;
        this.deleteAction = delete;
        this.asset = asset;

        this.state = new ToolbarTextItem("Status: ");
;

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
     * Sets the visible status display.
     */
    private void setState(String status) {
        state.setText("Status: [" + status + "]");
    }

    private void controls() {

	    	ToolbarButton save = new ToolbarButton();
	    	save.setText("Save changes");
			save.setTooltip(getTip("Commit any changes for this asset."));
			save.addListener(new ButtonListenerAdapter() {
		        			public void onClick(
		        					com.gwtext.client.widgets.Button button,
		        					EventObject e) {
		                        	doCheckinConfirm(button);
	        				}
		        			});
			toolbar.addButton(save);

        toolbar.addFill();
        toolbar.addSeparator();

		ToolbarButton copy = new ToolbarButton();
		copy.setText("Copy");
		copy.setTooltip("Copy this asset.");
		copy.addListener(new ButtonListenerAdapter() {
			public void onClick(
					com.gwtext.client.widgets.Button button,
					EventObject e) {
                	doCopyDialog(button);
			}
			});
		toolbar.addButton(copy);


		ToolbarButton archive = new ToolbarButton();
		archive.setText("Archive");
		archive.setTooltip(getTip("Archive this asset. This will not permanently delete it."));
		archive.addListener(new ButtonListenerAdapter() {
			public void onClick(
					com.gwtext.client.widgets.Button button,
					EventObject e) {
                        if (Window.confirm( "Are you sure you want to archive this item?" )) {
                            archiveAction.doCheckin("Archived Item on " + new java.util.Date().toString());
                        }
			}
			});
		toolbar.addButton(archive);




        if (notCheckedInYet()) {

        	final ToolbarButton delete = new ToolbarButton();
        	delete.setText("Delete");
    		delete.setTooltip(getTip("Permanently delete this asset. This will only be shown before the asset is checked in."));
    		delete.addListener(new ButtonListenerAdapter() {
    			public void onClick(
    					com.gwtext.client.widgets.Button button,
    					EventObject e) {
                            if (Window.confirm( "Are you sure you want to permanently delete this (unversioned) item?" ) ) {
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
        stateChange.setText("Change state");
		stateChange.setTooltip(getTip("Change the status of this asset."));
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
        final FormStylePopup form = new FormStylePopup("images/rule_asset.gif", "Copy this item");
        final TextBox newName = new TextBox();
        form.addAttribute( "New name:", newName );

        Button ok = new Button("Create copy");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	if (newName.getText() == null || newName.equals("")) {
            		Window.alert("Asset name must not be empty.");
            		return;
            	}
                RepositoryServiceFactory.getService().copyAsset( asset.uuid, asset.metaData.packageName, newName.getText(),
                                                                 new GenericCallback() {
                                                                    public void onSuccess(Object data) {
                                                                        completedCopying(newName.getText(), asset.metaData.packageName);
                                                                        form.hide();
                                                                    }


                });
            }
        } );
        form.addAttribute( "", ok );

		//form.setPopupPosition((DirtyableComposite.getWidth() - form.getOffsetWidth()) / 2, 100);
		form.show();

    }

    private void completedCopying(String name, String pkg) {
        Window.alert( "Created a new item called [" + name + "] in package: [" + pkg + "] successfully." );

    }

    /**
     * Called when user wants to checkin.
     */
    protected void doCheckinConfirm(Widget w) {
        final CheckinPopup pop = new CheckinPopup(w.getAbsoluteLeft(), w.getAbsoluteTop(), "Check in changes.");
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