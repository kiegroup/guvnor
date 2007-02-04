package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This builds on the FormStyleLayout for providing common popup features in a 
 * columnar form layout, with a title and a large (ish) icon.
 * 
 * @author Michael Neale
 */
public class FormStylePopup extends PopupPanel {

    
    private FormStyleLayout form;
 
    public FormStylePopup(String image,
                          String title) {
        super( true );
        form = new FormStyleLayout( image, title );
        this.setStyleName( "ks-popups-Popup" );

        
        add( form );
    }
    
    public void addAttribute(String label, Widget wid) {
        form.addAttribute( label, wid );
    }

    public void show() {        
        Image close = new Image("images/close.gif");
        close.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                hide();                
            }            
        });
        addAttribute("", close);
        
        super.show();
    }
    

}
