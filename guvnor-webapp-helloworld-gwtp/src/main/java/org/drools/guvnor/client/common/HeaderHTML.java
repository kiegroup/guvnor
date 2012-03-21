package org.drools.guvnor.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HeaderHTML extends Composite {

    interface HeaderHTMLBinder
        extends
        UiBinder<Widget, HeaderHTML> {
    }

    private static HeaderHTMLBinder uiBinder = GWT.create( HeaderHTMLBinder.class );

    @UiField
    Label                           textLabel;

    @UiField
    Image                           image;

    public HeaderHTML() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setText(String text) {
        textLabel.setText( text );
    }

    public void setImageResource(ImageResource imageResource) {
        image.setResource( imageResource );
    }
}
