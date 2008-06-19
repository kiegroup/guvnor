package org.drools.guvnor.client.common;
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



import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Well this one should be pretty obvious what it does.
 * I feel like I have wasted valuable time writing this comment, but I hope
 * you enjoyed reading it.
 *
 * @author Michael Neale
 *
 */
public class StatusChangePopup extends FormStylePopup {

    private boolean isPackage;
    private String uuid;
    private String newStatus;
    private Command changedStatus;

    public StatusChangePopup(String uuid, boolean isPackage) {

        this.uuid = uuid;
        this.isPackage = isPackage;

        super.addRow(new HTML( "<img src='images/status_small.gif'/><b>Change status</b>" ));

        HorizontalPanel horiz = new HorizontalPanel();
        final ListBox box = new ListBox();

        LoadingPopup.showMessage( "Please wait..." );
        RepositoryServiceFactory.getService().listStates( new GenericCallback() {
            public void onSuccess(Object data) {
                String[] list = (String[]) data;
                box.addItem( "-- Choose one --" );
                for ( int i = 0; i < list.length; i++ ) {
                    box.addItem( list[i] );
                }
                LoadingPopup.close();
            }
        });

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                newStatus = box.getItemText( box.getSelectedIndex() );
            }
        });

        horiz.add(box);
        Button ok = new Button("Change status");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                String newState = box.getItemText( box.getSelectedIndex() );
                changeState(newState);
                hide();
            }
        });
        horiz.add( ok );


        Button close = new Button("Cancel");
        close.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                hide();
            }
        });
        horiz.add( close );


        addRow(horiz);



    }

    /** Apply the state change */
    private void changeState(String newState) {
        LoadingPopup.showMessage( "Updating status..." );
        RepositoryServiceFactory.getService().changeState( uuid, newStatus, isPackage, new GenericCallback() {
            public void onSuccess(Object data) {
                changedStatus.execute();
                LoadingPopup.close();
            }
        });
    }

    /**
     * Get what the state was changed to.
     */
    public String getState() {
        return this.newStatus;
    }

    /**
     * set the status change event
     */
    public void setChangeStatusEvent(Command command) {
        this.changedStatus = command;
    }
}