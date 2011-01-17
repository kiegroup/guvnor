package org.drools.guvnor.client.decisiontable.widget;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a column resize event.
 * 
 * @author manstis
 */
public class ColumnResizeEvent extends GwtEvent<ColumnResizeHandler> {

	/**
	 * Handler type.
	 */
	private static Type<ColumnResizeHandler> TYPE;

	/**
	 * Fires a value change event on all registered handlers in the handler
	 * manager. If no such handlers exist, this method will do nothing.
	 * 
	 * @param <T>
	 *            the old value type
	 * @param source
	 *            the source of the handlers
	 * @param value
	 *            the value
	 */
	public static void fire(HasColumnResizeHandlers source,
			DynamicColumn column, int width) {
		if (TYPE != null) {
			ColumnResizeEvent event = new ColumnResizeEvent(column, width);
			source.fireEvent(event);
		}
	}

	/**
	 * Gets the type associated with this event.
	 * 
	 * @return returns the handler type
	 */
	public static Type<ColumnResizeHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ColumnResizeHandler>();
		}
		return TYPE;
	}

	private final DynamicColumn column;
	private final int width;

	/**
	 * Creates a value change event.
	 * 
	 * @param column
	 *            The column on which the resize event was triggered
	 * @param width
	 *            The new width of the column
	 */
	protected ColumnResizeEvent(DynamicColumn column, int width) {
		if (column == null) {
			throw new IllegalArgumentException("column cannot be null");
		}
		this.column = column;
		this.width = width;
	}

	// The instance knows its BeforeSelectionHandler is of type I, but the TYPE
	// field itself does not, so we have to do an unsafe cast here.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public final Type<ColumnResizeHandler> getAssociatedType() {
		return (Type) TYPE;
	}

	/**
	 * Gets the column to which the resize event relates.
	 * 
	 * @return the column
	 */
	public DynamicColumn getColumn() {
		return this.column;
	}

	/**
	 * Gets the width of the column
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return this.width;
	}

	@Override
	public String toDebugString() {
		return super.toDebugString() + "column = " + getColumn().toString()
				+ ", width = " + getWidth();
	}

	@Override
	protected void dispatch(ColumnResizeHandler handler) {
		handler.onColumnResize(this);
	}
}
