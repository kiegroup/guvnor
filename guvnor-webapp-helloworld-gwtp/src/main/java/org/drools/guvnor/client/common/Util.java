package org.drools.guvnor.client.common;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;

public class Util {

    /**
     * Get a string representation of the header that includes an image and some
     * text.
     * 
     * @param image the {@link ImageResource} to add next to the header
     * @param text the header text
     * @return the header as a string
     */
    public static String getHeader(ImageResource image,
                                   String text) {
        return AbstractImagePrototype.create( image ).getHTML() + " " + text;
    }

    /**
     * Get a string representation of the header that includes an image and some
     * text.
     * 
     * @param image the {@link ImageResource} to add next to the header
     * @param text the header text
     * @return the header as a string
     */
    public static HTML getHeaderHTML(ImageResource image,
                                     String text) {

        HeaderHTML headerHTML = new HeaderHTML();

        headerHTML.setText( text );
        headerHTML.setImageResource( image );

        return new HTML( headerHTML.getElement().getString() );
    }

    /**
     * The URL that will be used to open up assets in a feed.
     * (by tacking asset id on the end, of course !). 
     */
    public static String getSelfURL() {
        String selfURL = Window.Location.getHref();
        if ( selfURL.contains( "#" ) ) {
            selfURL = selfURL.substring( 0,
                                         selfURL.indexOf( "#" ) );
        }
        return selfURL;
    }
}