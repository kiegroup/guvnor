package org.drools.guvnor.client.table;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Geoffrey De Smet
 */
public interface SortableHeaderResources extends ClientBundle {

    @Source("downArrow.png")
    ImageResource downArrow();

    @Source("smallDownArrow.png")
    ImageResource smallDownArrow();

    @Source("upArrow.png")
    ImageResource upArrow();

    @Source("smallUpArrow.png")
    ImageResource smallUpArrow();

}
