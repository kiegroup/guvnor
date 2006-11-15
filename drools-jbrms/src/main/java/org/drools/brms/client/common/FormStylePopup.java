package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This builds on the FormStyleLayout for providing common popup features in a 
 * columnar form layout, with a title and a large (ish) icon.
 * 
 * @author Michael Neale
 */
public class FormStylePopup extends PopupPanel {

    private FormStyleLayout form;
    private VerticalPanel   vert;

    public FormStylePopup(String image,
                          String title) {
        super( true );
        form = new FormStyleLayout( image, title );
        vert = new VerticalPanel();
        
        vert.add( form );
        
        Button close = new Button("Close");
        
        close.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                hide();                
            }            
        });
        
        vert.add( close );
        
        add( vert );
    }
    
    public void addAttribute(String label, Widget wid) {
        form.addAttribute( label, wid );
    }

}
