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

package org.drools.guvnor.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.DetailedSerializationException;

/**
 * Generic error dialog popup.
 */
public class ErrorPopup extends Popup {

    private static ImagesCore images =  GWT.create( ImagesCore.class );
    private ConstantsCore constants = GWT.create( ConstantsCore.class );
    private VerticalPanel body = new VerticalPanel();

    private static final String WIDTH= 400 + "px";;

    private ErrorPopup() {

        setTitle( constants.Error() );
        setWidth( WIDTH );
        setModal( true );

        body.setWidth( "100%" );
    }

    @Override
    public Widget getContent() {
        return body;
    }

    private void addMessage(String message,
                            String longMessage) {
        
        body.clear();
        if ( message != null && message.contains( "ItemExistsException" ) ) { //NON-NLS
            longMessage = message;
            message = constants.SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother();

        }

        final String longDescription = longMessage;

        HorizontalPanel hp = new HorizontalPanel();
        hp.add( new Image( images.validationError() ) );
        Label msg = new Label( message );
        msg.setStyleName( "error-title" );
        hp.add( msg );
        body.add( hp );

        final SimplePanel detailPanel = new SimplePanel();
        if ( longMessage != null && !"".equals( longMessage ) ) {
            Button showD = new Button( constants.ShowDetail() );
            showD.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    detailPanel.clear();
                    VerticalPanel vp = new VerticalPanel();
                    vp.add( new HTML( "<hr/>" ) );

                    ScrollPanel longMessageLabel = new ScrollPanel(new Label( longDescription ));
                    longMessageLabel.setWidth(WIDTH);
                    longMessageLabel.setStyleName("error-long-message" );
                    vp.add( longMessageLabel );
                    detailPanel.add( vp );
                }
            } );
            detailPanel.add( showD );
        }

        detailPanel.setWidth( "100%" );
        body.add( detailPanel );
        show();
    }

    /**
     * Convenience method to popup the message.
     */
    public static void showMessage(String message) {
        ErrorPopup instance = new ErrorPopup();
        instance.addMessage( message, 
                             null );

        LoadingPopup.close();
    }

    /**
     * For showing a more detailed report.
     */
    public static void showMessage(DetailedSerializationException exception) {
        ErrorPopup instance = new ErrorPopup();
        instance.addMessage( exception.getMessage(),
                             exception.getLongDescription() );

        LoadingPopup.close();
    }

}