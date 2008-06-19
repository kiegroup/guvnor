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
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.common.StatusChangePopup;
import org.drools.guvnor.client.rpc.MetaData;
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
    private MetaData      metaData;
    private Command checkinAction;
    private Command archiveAction;
    private Command deleteAction;
    private String uuid;
    private ToolbarTextItem state;

    public ActionToolbar(final RuleAsset asset,
                         final Command checkin,
                         final Command archiv,
                         final Command delete, boolean readOnly) {

        this.metaData = asset.metaData;
        this.checkinAction = checkin;
        this.uuid = asset.uuid;
        this.archiveAction = archiv;
        this.deleteAction = delete;
        this.state = new ToolbarTextItem("Status: ");


        toolbar = new Toolbar();


        String status = metaData.status;

        setState(status);

        if (!readOnly) {
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
                            metaData.checkinComment = "Archived Item on " + new java.util.Date().toString();
                            archiveAction.execute();
                        }
			}
			});
		toolbar.addButton(archive);




        if (this.metaData.versionNumber == 0) {

        	ToolbarButton delete = new ToolbarButton();
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

        }


        toolbar.addFill();
        toolbar.addSeparator();


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
        final RulePackageSelector newPackage = new RulePackageSelector();
        form.addAttribute( "New name:", newName );
        form.addAttribute( "New package:", newPackage );

        Button ok = new Button("Create copy");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	if (newName.getText() == null || newName.equals("")) {
            		Window.alert("Asset name must not be empty.");
            		return;
            	}
                RepositoryServiceFactory.getService().copyAsset( uuid, newPackage.getSelectedPackage(), newName.getText(),
                                                                 new GenericCallback() {
                                                                    public void onSuccess(Object data) {
                                                                        completedCopying(newName.getText(), newPackage.getSelectedPackage());
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
                metaData.checkinComment = pop.getCheckinComment();
                checkinAction.execute();
            }
        });
        pop.show();
    }

    /**
     * Show the stats change popup.
     */
    private void showStatusChanger(Widget w) {
        final StatusChangePopup pop = new StatusChangePopup(uuid, false);
        pop.setChangeStatusEvent(new Command() {
            public void execute() {
                setState( pop.getState() );
            }
        });

        pop.show();
    }



}