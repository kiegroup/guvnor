package org.drools.guvnor.client.common;


import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;


/**
 * This is handy for in-place context help.
 */
public class InfoPopup extends Composite {

    private static Images images = (Images) GWT.create( Images.class );

    public InfoPopup(final String title,
                     final String message) {
        Image info = new Image( images.information() );
        info.setTitle( message );
        info.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                final FormStylePopup pop = new FormStylePopup( images.information(),
                                                               title );
                pop.addRow( new SmallLabel( message ) );
                pop.show();
            }
        } );
        initWidget( info );
    }
}
