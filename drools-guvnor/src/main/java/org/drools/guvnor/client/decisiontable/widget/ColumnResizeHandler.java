package org.drools.guvnor.client.decisiontable.widget;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link ColumnResizeEvent} events.
 * 
 * @author manstis
 */
public interface ColumnResizeHandler
    extends
    EventHandler {

    /**
     * Called when {@link ColumnResizeEvent} is fired.
     * 
     * @param event
     *            the {@link ColumnResizeEvent} that was fired
     */
    void onColumnResize(ColumnResizeEvent event);
}
