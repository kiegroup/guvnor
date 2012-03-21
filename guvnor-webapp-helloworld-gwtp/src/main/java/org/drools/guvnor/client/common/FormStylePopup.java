package org.drools.guvnor.client.common;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

/**
 * This builds on the FormStyleLayout for providing common popup features in a
 * columnar form layout, with a title and a large (ish) icon.
 */
public class FormStylePopup extends Popup {

    private FormStyleLayout form;

    public FormStylePopup(ImageResource image,
                          final String title) {

        form = new FormStyleLayout( image,
                                    title );

        setModal( true );

        setTitle( title );

    }

    public FormStylePopup() {
        form = new FormStyleLayout();
    }

    public FormStylePopup(ImageResource image,
                          final String title,
                          Integer width) {
        this( image,
              title );
        setWidth( width + "px" );
    }

    @Override
    public Widget getContent() {
        return form;
    }

    public void clear() {
        this.form.clear();
    }

    public void addAttribute(String label,
                             Widget wid) {
        form.addAttribute( label,
                           wid );
    }

    public void addRow(Widget wid) {
        form.addRow( wid );
    }

}
