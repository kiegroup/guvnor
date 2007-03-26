package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.Image;

/**
 * Really just an image, but tacks on the image-Button style name.
 * @author Michael Neale
 *
 */
public class ImageButton extends Image {

    public ImageButton(String img) {
        super(img);
        setStyleName( "image-Button" );
    }
    
}
