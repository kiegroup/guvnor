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

import com.google.gwt.user.client.ui.Image;
import org.kie.uberfirebootstrap.client.widgets.ErrorPopup;
import org.kie.uberfirebootstrap.client.widgets.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.ImagesCore;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;

/**
 * This provides a popup for creating a workspace.
 */
public class WorkspaceEditor extends FormStylePopup {

    private TextBox          name      = new TextBox();
    private Command          refresh;

    public WorkspaceEditor(Command refresh) {
        super(getImage(),
               ConstantsCore.INSTANCE.CreateNewStatus() );
        this.refresh = refresh;

        addAttribute( ConstantsCore.INSTANCE.StatusName(),
                      name );

        Button ok = new Button( ConstantsCore.INSTANCE.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                ok();
            }

        } );
        addAttribute( "",
                      ok );
    }

    private static Image getImage() {
        Image image = new Image(ImagesCore.INSTANCE.editCategory());
        image.setAltText(ConstantsCore.INSTANCE.EditCategory());
        return image;
    }

    void ok() {
        if ( "".equals( this.name.getText() ) ) {
            ErrorPopup.showMessage( ConstantsCore.INSTANCE.CanTHaveAnEmptyWorkspaceName() );
        } else {
            createWorkspace( name );
        }
    }

    private void createWorkspace(final TextBox box) {
        LoadingPopup.showMessage( ConstantsCore.INSTANCE.CreatingStatus() );

        RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);

        repositoryService.createWorkspace( box.getText(),
                                                           new GenericCallback<Void>() {
                                                               public void onSuccess(Void v) {

                                                                       if ( refresh != null ) {
                                                                           box.setText( "" );
                                                                           LoadingPopup.close();
                                                                           hide();
                                                                           refresh.execute();
                                                                       }

                                                               }
                                                           } );

    }

    void cancel() {
        hide();
    }
}