package org.drools.guvnor.client.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Image;

/**
 * Really just an image, but tacks on the image-Button style name.
 */
public class ImageButton extends Image {
	ImageResource img;
	ImageResource selectedImg;
	
    public @UiConstructor
    ImageButton(ImageResource img) {
        super( img );
        this.img = img;
        this.selectedImg = img;
        setStyleName( "image-Button" );
    }

    public ImageButton(ImageResource img,
                       ImageResource selectedImg,
                       String tooltip,
                       ClickHandler action) {
        super( img );
        this.img = img;
        this.selectedImg = selectedImg;
        setStyleName( "image-Button" );
        setTitle( tooltip );        
        this.addClickHandler( action );
    }
    
    public void setSelected(boolean selected) {
    	if(selected) {
    		super.setResource(selectedImg);
    	} else {
     	    super.setResource(img);   		
    	}
    }
}
