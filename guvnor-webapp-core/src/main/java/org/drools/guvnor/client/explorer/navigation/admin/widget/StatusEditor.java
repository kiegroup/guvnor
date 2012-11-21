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
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.messages.ConstantsCore;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;

/**
 * This provides a popup for editing a status (name etc).
 * Mainly this is for creating a new status.
 */
public class StatusEditor extends FormStylePopup {

    private static ImagesCore images    = (ImagesCore) GWT.create( ImagesCore.class );
    private static ConstantsCore constants = ((ConstantsCore) GWT.create( ConstantsCore.class ));

    private TextBox          name      = new TextBox();
    private Command          refresh;

    public StatusEditor(Command refresh) {
        super(getImage(),
               constants.CreateNewStatus() );
        this.refresh = refresh;

        addAttribute( constants.StatusName(),
                      name );

        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                ok();
            }

        } );
        addAttribute( "",
                      ok );
    }

    private static Image getImage() {
        Image image = new Image(images.editCategory());
        image.setAltText(ConstantsCore.INSTANCE.EditCategory());
        return image;
    }

    void ok() {

        if ( "".equals( this.name.getText() ) ) {
            ErrorPopup.showMessage( constants.CanTHaveAnEmptyStatusName() );
        } else {
            createStatus( name );
        }
    }

    private void createStatus(final TextBox box) {
        LoadingPopup.showMessage( constants.CreatingStatus() );
        RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);
        repositoryService.createState( box.getText(),
                                                           new GenericCallback<String>() {
                                                               public void onSuccess(String data) {
                                                                   if ( data != null ) {
                                                                       if ( refresh != null ) {
                                                                           box.setText( "" );
                                                                           LoadingPopup.close();
                                                                           hide();
                                                                           refresh.execute();
                                                                       }
                                                                   } else {

                                                                       ErrorPopup.showMessage( constants.StatusWasNotSuccessfullyCreated() );

                                                                   }
                                                               }
                                                           } );

    }

    void cancel() {
        hide();
    }
}