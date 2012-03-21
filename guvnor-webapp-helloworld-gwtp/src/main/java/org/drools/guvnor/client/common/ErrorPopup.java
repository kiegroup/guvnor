package org.drools.guvnor.client.common;


import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Generic error dialog popup.
 */
public class ErrorPopup extends Popup {

    private static Images    images    = (Images) GWT.create( Images.class );
    private Constants        constants = ((Constants) GWT.create( Constants.class ));

    public static ErrorPopup instance  = null;
    private VerticalPanel    body;

    private ErrorPopup(String message,
                       String longMessage) {

        setTitle( constants.Error() );
        setWidth( 450 + "px" );
        setModal( true );

        body = new VerticalPanel();

        addMessage( message,
                    longMessage );

        body.setWidth( "100%" );

        show();

        addCloseHandler( new CloseHandler<PopupPanel>() {

            public void onClose(CloseEvent<PopupPanel> event) {
                instance = null;
            }
        } );
    }

    @Override
    public Widget getContent() {
        return body;
    }

    private void addMessage(String message,
                            String longMessage) {
/*        if ( message != null && message.contains( "ItemExistsException" ) ) { //NON-NLS
            longMessage = message;
            message = constants.SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother();

        }*/

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

                    Label lng = new Label( longDescription );
                    lng.setStyleName( "error-long-message" );
                    vp.add( lng );
                    detailPanel.add( vp );
                }
            } );
            detailPanel.add( showD );
        }

        detailPanel.setWidth( "100%" );
        body.add( detailPanel );
    }

    /** Convenience method to popup the message. */
    public static void showMessage(String message) {
        if ( instance != null ) {
            instance.addMessage( message,
                                 null );
        } else {
            instance = new ErrorPopup( message,
                                       null );
        }

        LoadingPopup.close();
    }

    /**
     * For showing a more detailed report.
     */
/*    public static void showMessage(DetailedSerializationException exception) {

        if ( instance != null ) {
            instance.addMessage( exception.getMessage(),
                                 exception.getLongDescription() );
        } else {
            instance = new ErrorPopup( exception.getMessage(),
                                       exception.getLongDescription() );
        }

        LoadingPopup.close();
    }*/

}