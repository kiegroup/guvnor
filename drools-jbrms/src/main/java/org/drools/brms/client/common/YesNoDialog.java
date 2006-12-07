package org.drools.brms.client.common;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple confirmation dialog. 
 * 
 * @author Michael Neale
 *
 */
public class YesNoDialog extends DialogBox {

    public YesNoDialog(String message, final Command yes) {
        setText( message );
        
        Button y = new Button("Yes");
        Button n = new Button("No");
        
        y.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                yes.execute();
                hide();
            }            
        });
        
        n.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                hide();                
            }
            
        });
        
        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( y );
        horiz.add( n );
        
        setWidget( horiz );
    }
    
}
