package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.Label;

/**
 * Little helper class to decorate vanilla labels with style.
 * 
 * @author Michael Neale
 */
public class Lbl extends Label {
    
    public Lbl(String label, String style) {
        super(label);
        super.setStyleName( style );
    }
}
