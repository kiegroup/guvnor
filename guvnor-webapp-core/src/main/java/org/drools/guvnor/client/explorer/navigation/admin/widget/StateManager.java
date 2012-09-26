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

import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.ImagesCore;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen(identifier = "statusManager")
public class StateManager extends Composite {

    private RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);

    private ListBox       currentStatuses;

    public StateManager() {
        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader( GuvnorImages.INSTANCE.Status(),
                        new HTML( "<b>" + ConstantsCore.INSTANCE.ManageStatuses() + "</b>" ) );
        form.startSection( ConstantsCore.INSTANCE.StatusTagsAreForTheLifecycleOfAnAsset() );

        currentStatuses = new ListBox();
        currentStatuses.setVisibleItemCount( 7 );
        currentStatuses.setWidth( "50%" );

        refreshList();

        form.addAttribute( ConstantsCore.INSTANCE.CurrentStatuses(),
                           currentStatuses );

        HorizontalPanel hPanel = new HorizontalPanel();
        Button create = new Button( ConstantsCore.INSTANCE.NewStatus() );
        //create.setTitle( ConstantsCore.INSTANCE.CreateANewCategory() );
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

        Button edit = new Button( ConstantsCore.INSTANCE.RenameSelected() );
        edit.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {

                if ( !currentStatuses.isItemSelected( currentStatuses.getSelectedIndex() ) ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseSelectAStatusToRename() );
                    return;
                }
                renameSelected();

            }
        } );

        Button remove = new Button( ConstantsCore.INSTANCE.DeleteSelected() );
        remove.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {

                if ( !currentStatuses.isItemSelected( currentStatuses.getSelectedIndex() ) ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseSelectAStatusToRemove() );
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

        repositoryService.removeState(name,
                new GenericCallback<java.lang.Void>() {
                    public void onSuccess(Void v) {
                        Window.alert(ConstantsCore.INSTANCE.StatusRemoved());
                        refreshList();
                    }
                });
    }

    private void renameSelected() {

        String newName = Window.prompt( ConstantsCore.INSTANCE.PleaseEnterTheNameYouWouldLikeToChangeThisStatusTo(),
                                        "" );

        String oldName = currentStatuses.getItemText( currentStatuses.getSelectedIndex() );

        if ( newName != null ) {
            repositoryService.renameState( oldName,
                                                               newName,
                                                               new GenericCallback<Void>() {
                                                                   public void onSuccess(Void data) {
                                                                       Window.alert( ConstantsCore.INSTANCE.StatusRenamed() );
                                                                       refreshList();
                                                                   }
                                                               } );
        }
    }

    private void refreshList() {
        LoadingPopup.showMessage( ConstantsCore.INSTANCE.LoadingStatuses() );
        repositoryService.listStates( new GenericCallback<String[]>() {
            public void onSuccess(String[] statii) {
                currentStatuses.clear();
                for (String aStatii : statii) {
                    currentStatuses.addItem(aStatii);
                }
                LoadingPopup.close();
            }
        } );
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return this;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ConstantsCore.INSTANCE.StateManager();
    }
}