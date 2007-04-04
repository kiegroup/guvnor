package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is handy for in-place context help.
 * 
 * @author Michael Neale
 */
public class InfoPopup extends Composite {

    public InfoPopup(final String title, final String message) {
        Image info = new Image("images/information.gif");
        info.setTitle( message );
        info.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                final FormStylePopup pop = new FormStylePopup("images/information.gif", title);
                pop.addRow( new Lbl(message, "small-Text") );
                pop.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
                pop.show();
            }
        } );
        initWidget( info );
    }
}
