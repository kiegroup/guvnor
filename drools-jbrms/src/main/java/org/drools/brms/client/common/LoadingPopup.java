package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/** 
 * Generic "busy" dialog popup.
 * This is a lazy singleton, only really need one to be shown at time. 
 */
public class LoadingPopup extends PopupPanel {
    
    public static LoadingPopup instance = null;
    
    Label errorMessage = new Label();
    Panel panel = new HorizontalPanel();
    Image ok = new Image("images/close.gif");
    
    public LoadingPopup() {        
        super(true);
        panel.add( errorMessage );
        panel.add( ok );
        panel.add( new Image("images/searching.gif") );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {                
                hide();
            }            
        });
        this.add( panel );
        this.setPopupPosition( 0, 0 );
        setStyleName( "loading-Popup" );        
    }
    

    /**
     * Close the single instance of this dialog...
     */
    public static void close() {
        getInstance().hide();
    }
    
    public void hide() {
        errorMessage.setText( "" );
        super.hide();
    }
    
    public static LoadingPopup getInstance() {
        if (instance == null) {
            instance = new LoadingPopup();            
        }
        return instance;
    }
    
    /** Convenience method to popup the message. */
    public static void showMessage(String message) {
        LoadingPopup p = getInstance();
        
        p.errorMessage.setText( message );
        p.show();
    }
    

    
     
    
    
}
