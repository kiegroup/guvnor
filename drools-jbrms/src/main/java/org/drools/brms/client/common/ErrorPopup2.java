package org.drools.brms.client.common;
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



import org.drools.brms.client.rpc.DetailedSerializableException;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Generic error dialog popup.
 */
public class ErrorPopup2 extends DialogBox {

    public static ErrorPopup2 instance = null;

    Label errorMessage = new Label();
    Panel panel = new HorizontalPanel();
    Image ok = new ImageButton("images/close.gif");


    private ErrorPopup2(String message, String longMessage) {
        super(true);



        this.errorMessage.setText( message );

        panel.add( new Image("images/error_dialog.png") );
        VerticalPanel vert = new VerticalPanel();
        vert.add( errorMessage );
        panel.add( vert );

        if (longMessage != null) {
            addDetail(vert, longMessage);
        }

        panel.add( ok );
        final PopupPanel self = this;
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                self.hide();
            }
        });
        this.setWidget( panel );
        this.setPopupPosition( 40, 40 );
        //setHeight( "150px" );
        setStyleName( "rule-error-Popup" );
    }





    /**
     * Add a detailed report section (which is hidden by default).
     */
    private void addDetail(Panel panel,
                           final String detailedError) {
        VerticalPanel vert = new VerticalPanel();
        panel.add( vert );
        final Button show = new Button("Details");
        vert.add( show );

        final Label detail = new Label(detailedError);
        detail.setVisible( false );


        vert.add( detail );

        show.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                detail.setVisible( true );
                show.setVisible( false );
            }

        });

    }

    public void setMessage(String message) {
        errorMessage.setText( message );
    }


    public void hide() {
        errorMessage.setText( "" );
        super.hide();
    }


    /** Convenience method to popup the message. */
    public static void showMessage(String message) {
        ErrorPopup2 p = new ErrorPopup2(message, null);
        LoadingPopup.close();

        p.show();
    }

    /**
     * For showing a more detailed report.
     */
    public static void showMessage(DetailedSerializableException exception) {
        ErrorPopup2 p = new ErrorPopup2(exception.getMessage(), exception.getLongDescription());
        LoadingPopup.close();
        p.show();
    }






}