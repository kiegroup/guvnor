package org.drools.guvnor.client.admin;

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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StateManager extends Composite {

    private ListBox currentStatuses;

    public StateManager() {
        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader( "images/status_large.png",
                        new HTML( "<b>Manage statuses</b>" ) );
        form.startSection( "Status tags are for the lifecycle of an asset." );

        currentStatuses = new ListBox();
        currentStatuses.setVisibleItemCount( 7 );
        currentStatuses.setWidth( "50%" );

        refreshList();

        form.addAttribute( "Current statuses:",
                           currentStatuses );

        HorizontalPanel hPanel = new HorizontalPanel();
        Button create = new Button( "New status" );
        create.setTitle( "Create a new category" );
        create.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                StatusEditor newCat = new StatusEditor( new Command() {
                    public void execute() {
                        refreshList();
                    }
                } );

                newCat.show();
            }
        } );

        Button edit = new Button( "Rename selected" );
        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {

                if ( !currentStatuses.isItemSelected( currentStatuses.getSelectedIndex() ) ) {
                    Window.alert( "Please select a status to rename." );
                    return;
                }
                renameSelected();

            }
        } );

        Button remove = new Button( "Delete selected" );
        remove.addClickListener( new ClickListener() {
            public void onClick(Widget w) {

                if ( !currentStatuses.isItemSelected( currentStatuses.getSelectedIndex() ) ) {
                    Window.alert( "Please select a status to remove." );
                    return;
                }
                
                removeStatus();

            }

        } );
        hPanel.add( create );
        hPanel.add( edit );
        hPanel.add( remove );

        form.addAttribute( "Add new status:",
                           hPanel );

        form.endSection();
        initWidget( form );
    }

    private void removeStatus() {
        String name = currentStatuses.getItemText( currentStatuses.getSelectedIndex() );

        RepositoryServiceFactory.getService().removeState( name,
                                                           new GenericCallback() {
                                                               public void onSuccess(Object data) {
                                                                   Window.alert( "Status removed." );
                                                                   refreshList();
                                                               }
                                                           } );
    }
    
    private void renameSelected() {

        String newName = Window.prompt( "Please enter the name you would like to change this status to",
                                        "" );

        String oldName = currentStatuses.getItemText( currentStatuses.getSelectedIndex() );

        if ( newName != null ) {
            RepositoryServiceFactory.getService().renameState( oldName,
                                                               newName,
                                                               new GenericCallback() {
                                                                   public void onSuccess(Object data) {
                                                                       Window.alert( "Status renamed." );
                                                                       refreshList();
                                                                   }
                                                               } );
        }
    }

    private void refreshList() {
        LoadingPopup.showMessage( "Loading statuses..." );
        RepositoryServiceFactory.getService().listStates( new GenericCallback() {
            public void onSuccess(Object data) {
                currentStatuses.clear();
                String[] statii = (String[]) data;
                for ( int i = 0; i < statii.length; i++ ) {
                    currentStatuses.addItem( statii[i] );
                }
                LoadingPopup.close();
            }
        } );
    }

}