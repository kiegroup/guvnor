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

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * This provides a popup for editing a status (name etc).
 * Mainly this is for creating a new status.
 */
public class StatusEditor extends FormStylePopup {

    private static Images    images    = (Images) GWT.create( Images.class );
    private static Constants constants = ((Constants) GWT.create( Constants.class ));

    private TextBox          name      = new TextBox();
    private Command          refresh;

    public StatusEditor(Command refresh) {
        super( images.editCategory(),
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

    void ok() {

        if ( "".equals( this.name.getText() ) ) {
            ErrorPopup.showMessage( constants.CanTHaveAnEmptyStatusName() );
        } else {
            createStatus( name );
        }
    }

    private void createStatus(final TextBox box) {
        LoadingPopup.showMessage( constants.CreatingStatus() );
        RepositoryServiceFactory.getService().createState( box.getText(),
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