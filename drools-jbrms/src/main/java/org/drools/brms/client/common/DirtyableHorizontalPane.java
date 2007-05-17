/**
 * 
 */
package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author fernandomeyer
 */

public class DirtyableHorizontalPane extends HorizontalPanel implements DirtyableContainer {
    public boolean hasDirty() {
        
        int widNumber = getWidgetCount();
        Widget element;
        
        for ( int i = 0; i < widNumber; i++ ) {
            element =  (Widget) getWidget(i);
            if (element instanceof DirtyableWidget) 
                if ( ((DirtyableWidget) element).isDirty() ) return true;
            if (element instanceof DirtyableContainer)
                if ( ((DirtyableContainer) element).hasDirty()) return true;
        }
        return false;
    }
}
