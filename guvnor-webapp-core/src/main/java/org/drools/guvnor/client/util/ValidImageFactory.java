package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.shared.api.Valid;

public class ValidImageFactory {
    private static ImagesCore images = GWT.create(ImagesCore.class);

    public static ImageResource getImage(Valid valid) {
        switch (valid) {
            case INVALID:
                return images.validationError();
            case VALID:
                return images.greenTick();
            default:
                return images.warning();

        }
    }
}
