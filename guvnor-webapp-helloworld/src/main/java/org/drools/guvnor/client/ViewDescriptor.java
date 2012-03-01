package org.drools.guvnor.client;

import com.google.gwt.resources.client.ImageResource;

public interface ViewDescriptor {
    String getTitle();

    ViewPart getWidget();
}
