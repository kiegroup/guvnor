package org.drools.guvnor.client.common;


import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Generic "busy" dialog popup.
 * This is a lazy singleton, only really need one to be shown at time.
 */
public class LoadingPopup extends PopupPanel {

    private static LoadingPopup instance     = new LoadingPopup();

    private final Label         messageLabel = new Label();

    private LoadingPopup() {
        add( messageLabel );

        setWidth( "200px" );
        center();
    }

    /** Convenience method to popup the message. */
    public static void showMessage(final String message) {
        instance.messageLabel.setText( message );
        instance.show();
    }

    public static void close() {
        instance.hide();
    }

}