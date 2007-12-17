package org.drools.brms.client.ruleeditor;
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




import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.RulePackageSelector;
import org.drools.brms.client.common.StatusChangePopup;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 * @author Michael Neale
 */
public class ActionToolbar extends Composite {

    private FlexTable layout = new FlexTable();
    private Command closeCommand;

    private MetaData      metaData;
    private Command checkinAction;
    private Command archiveAction;
    private Command deleteAction;
    private String uuid;
    private HTML state;

    public ActionToolbar(final RuleAsset asset,

                         final Command checkin,
                         final Command archiv,
                         final Command minimiseMaximise,
                         final Command delete, boolean readOnly) {

        this.metaData = asset.metaData;
        this.checkinAction = checkin;
        this.uuid = asset.uuid;
        this.archiveAction = archiv;
        this.deleteAction = delete;
        this.state = new HTML();
        String status = metaData.status;

        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        HorizontalPanel saveControls = new HorizontalPanel();
        setState(status);


        saveControls.add( state );

        if (!readOnly) {
        controls(
                  formatter,
                  saveControls );

        }

        windowControls( minimiseMaximise, formatter );

        initWidget( layout );
        setWidth( "100%" );
    }

    /**
     * Sets the visible status display.
     */
    private void setState(String status) {
        state.setHTML( "Status: <b>[" + status + "]</b>");
    }

    private void controls(FlexCellFormatter formatter,
                          HorizontalPanel saveControls) {
        Image editState = new ImageButton("images/edit.gif");
        editState.setTitle( "Change status." );
        editState.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showStatusChanger(w);
            }


        } );
        saveControls.add( editState );


        layout.setWidget( 0, 0, saveControls );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );



        //Image save = new Image("images/save_edit.gif");
        Button save = new Button("Save changes");
        save.setTitle( "Check in changes." );
        save.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                doCheckinConfirm(w);
            }
        });

        saveControls.add( save );

        Button copy = new Button("Copy");
        copy.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                doCopyDialog(w);
            }
        } );

        saveControls.add( copy );

        Button archive = new Button("Archive");
        archive.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                if (Window.confirm( "Are you sure you want to archive this item?" )) {
                    metaData.checkinComment = "Archived Item on " + new java.util.Date().toString();
                    archiveAction.execute();
                }
            }
        });
        saveControls.add(archive);

        if (this.metaData.versionNumber == 0) {
        Button delete = new Button( "Delete" );
            delete.addClickListener( new ClickListener() {

                public void onClick(Widget w) {
                    if (Window.confirm( "Are you sure you want to permanently delete this (unversioned) item?" ) ) {
                        deleteAction.execute();
                    }
                }
            } );
            saveControls.add( delete );
        }

    }

    private void windowControls(final Command minimiseMaximise,
                                FlexCellFormatter formatter) {
        HorizontalPanel windowControls = new HorizontalPanel();

        Image maxMinImage = new ImageButton("images/max_min.gif");
        maxMinImage.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                minimiseMaximise.execute();
            }
        });

        windowControls.add( maxMinImage );

        Image closeImg = new ImageButton("images/close.gif");
        closeImg.setTitle( "Close." );
        closeImg.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                closeCommand.execute(  );
            }
        });

        windowControls.add( closeImg );

        layout.setWidget( 0, 1, windowControls );
        formatter.setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP );

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

		form.setPopupPosition((DirtyableComposite.getWidth() - form.getOffsetWidth()) / 2, 100);
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
        pop.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
        pop.show();
    }

    /**
     * This needs to be set to allow the current viewer to be closed.
     */
    public void setCloseCommand(Command c) {
        this.closeCommand = c;
    }

}