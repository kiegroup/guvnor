package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/** 
 * Generic warning message popup.
 * This is also applicable for input validation messages.
 */
public class WarningPopup extends PopupPanel {
    
    Label errorMessage = new Label();
    Panel panel = new HorizontalPanel();
    Button ok = new Button("OK");
    
    public WarningPopup(int x, int y) {        
        super(true);
        this.setPopupPosition( x, y );
        panel.add( errorMessage );
        panel.add( ok );
        final PopupPanel self = this;
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {                
                self.hide();
            }            
        });
        this.add( panel );
        this.setStyleName( "rule-warning-Popup" );
    }
    
    public void setMessage(String message) {
        errorMessage.setText( message );        
    }
    
    public void hide() {
        errorMessage.setText( "" );
        super.hide();
    }
    

    
    /** Convenience method to popup the message in the given position. */
    public static void showMessage(String message, int x, int y) {
        WarningPopup p = new WarningPopup(x, y);       
        p.errorMessage.setText( message );
        p.show();
    }
    

    
     
    
    
}
