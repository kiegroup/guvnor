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