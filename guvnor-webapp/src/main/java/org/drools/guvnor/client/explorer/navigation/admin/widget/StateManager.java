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

package org.drools.guvnor.client.explorer.navigation.admin.widget;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

public class StateManager extends Composite {

    private static Images images    = (Images) GWT.create( Images.class );
    private Constants     constants = ((Constants) GWT.create( Constants.class ));

    private ListBox       currentStatuses;

    public StateManager() {
        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader( images.statusLarge(),
                        new HTML( "<b>" + constants.ManageStatuses() + "</b>" ) );
        form.startSection( constants.StatusTagsAreForTheLifecycleOfAnAsset() );

        currentStatuses = new ListBox();
        currentStatuses.setVisibleItemCount( 7 );
        currentStatuses.setWidth( "50%" );

        refreshList();

        form.addAttribute( constants.CurrentStatuses(),
                           currentStatuses );

        HorizontalPanel hPanel = new HorizontalPanel();
        Button create = new Button( constants.NewStatus() );
        //create.setTitle( constants.CreateANewCategory() );
        create.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                StatusEditor newCat = new StatusEditor( new Command() {
                    public void execute() {
                        refreshList();
                    }
                } );

                newCat.show();
            }
        } );

        Button edit = new Button( constants.RenameSelected() );
        edit.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {

                if ( !currentStatuses.isItemSelected( currentStatuses.getSelectedIndex() ) ) {
                    Window.alert( constants.PleaseSelectAStatusToRename() );
                    return;
                }
                renameSelected();

            }
        } );

        Button remove = new Button( constants.DeleteSelected() );
        remove.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {

                if ( !currentStatuses.isItemSelected( currentStatuses.getSelectedIndex() ) ) {
                    Window.alert( constants.PleaseSelectAStatusToRemove() );
                    return;
                }

                removeStatus();

            }

        } );
        hPanel.add( create );
        hPanel.add( edit );
        hPanel.add( remove );

        form.addAttribute("",
                           hPanel );

        form.endSection();
        initWidget( form );
    }

    private void removeStatus() {
        String name = currentStatuses.getItemText( currentStatuses.getSelectedIndex() );

        RepositoryServiceFactory.getService().removeState( name,
                                                           new GenericCallback<java.lang.Void>() {
                                                               public void onSuccess(Void v) {
                                                                   Window.alert( constants.StatusRemoved() );
                                                                   refreshList();
                                                               }
                                                           } );
    }

    private void renameSelected() {

        String newName = Window.prompt( constants.PleaseEnterTheNameYouWouldLikeToChangeThisStatusTo(),
                                        "" );

        String oldName = currentStatuses.getItemText( currentStatuses.getSelectedIndex() );

        if ( newName != null ) {
            RepositoryServiceFactory.getService().renameState( oldName,
                                                               newName,
                                                               new GenericCallback<Void>() {
                                                                   public void onSuccess(Void data) {
                                                                       Window.alert( constants.StatusRenamed() );
                                                                       refreshList();
                                                                   }
                                                               } );
        }
    }

    private void refreshList() {
        LoadingPopup.showMessage( constants.LoadingStatuses() );
        RepositoryServiceFactory.getService().listStates( new GenericCallback<String[]>() {
            public void onSuccess(String[] statii) {
                currentStatuses.clear();
                for ( int i = 0; i < statii.length; i++ ) {
                    currentStatuses.addItem( statii[i] );
                }
                LoadingPopup.close();
            }
        } );
    }

}