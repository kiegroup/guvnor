package org.drools.guvnor.client.decisiontable.widget;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * A widget that implements this interface is a public source of
 * {@link ColumnResizeEvent} events.
 * 
 * @author manstis
 */
public interface HasColumnResizeHandlers extends HasHandlers {

	/**
	 * Adds a {@link ColumnResizeEvent} handler.
	 * 
	 * @param handler
	 *            the handler
	 * @return the handler registration
	 */
	HandlerRegistration addColumnResizeHandler(ColumnResizeHandler handler);

}
