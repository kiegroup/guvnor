package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * This presents validation error messages to the user, from the server side.
 * Such as validation or compile errors.
 * 
 * @author Michael Neale
 */
public class ValidationMessageWidget extends FormStylePopup {
    
    /**
     * The heading is the short description in bold at the top.
     */
    public ValidationMessageWidget(String heading, String body) {

        super("images/attention_needed.png", heading);
        addAttribute( "Detail:", details(body) );
        //addRow( close() );
        
    }

//    private Widget close() {
//        Image img = new Image("images/close.gif");
//        img.addClickListener( new ClickListener() {
//            public void onClick(Widget w) {
//                hide();
//            }
//        } );
//        return img;
//    }

    private Widget details(String body) {
        TextArea area = new TextArea();
        area.setStyleName( "editable-Surface" );
        area.setVisibleLines( 12 );
        area.setText( body );
        area.setWidth( "100%" );       
        return area;
    }
}
