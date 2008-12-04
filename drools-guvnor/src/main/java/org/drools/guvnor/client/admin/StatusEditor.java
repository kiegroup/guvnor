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

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.ruleeditor.NewAssetWizard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This provides a popup for editing a status (name etc).
 * Mainly this is for creating a new status.
 */
public class StatusEditor extends FormStylePopup {

    private TextBox name = new TextBox();
    private Command refresh;

    public StatusEditor(Command refresh) {
        super( "images/edit_category.gif",
               "Create new status" );
        this.refresh = refresh;

        addAttribute( "Status name",
                      name );

        Button ok = new Button( "OK" );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                ok();
            }

        } );
        addAttribute( "",
                      ok );
    }

    void ok() {

        if ( "".equals( this.name.getText() ) ) {
            ErrorPopup.showMessage( "Can't have an empty status name." );
        } else {
    		if (!NewAssetWizard.validatePathPerJSR170(this.name.getText())) return;
    		createStatus( name );
        }
    }

    private void createStatus(final TextBox box) {
        LoadingPopup.showMessage( "Creating status" );
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

                                                                       ErrorPopup.showMessage( "Status was not successfully created. " );

                                                                   }
                                                               }
                                                           } );

    }

    void cancel() {
        hide();
    }
}