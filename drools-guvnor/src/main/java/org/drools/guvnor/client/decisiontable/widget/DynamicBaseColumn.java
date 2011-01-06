package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.decisiontable.cells.DecisionTableCellValueAdaptor;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ProvidesKey;

/**
 * A representation of a column in a table. The column may maintain view data
 * for each cell on demand. New view data, if needed, is created by the cell's
 * onBrowserEvent method, stored in the Column, and passed to future calls to
 * Cell's {@link Cell#onBrowserEvent} and {@link Cell#render} methods.
 * 
 * Forked GWT2.1's Column<T, C> class to make Cell<C> non-final.
 * 
 */
public abstract class DynamicBaseColumn implements HasCell<DynamicDataRow, CellValue<? extends Comparable<?>>> {

	/**
	 * The {@link Cell} responsible for rendering items in the column.
	 */
	protected DecisionTableCellValueAdaptor<? extends Comparable<?>> cell;

	/**
	 * The {@link FieldUpdater} used for updating values in the column.
	 */
	protected FieldUpdater<DynamicDataRow, CellValue<? extends Comparable<?>>> fieldUpdater;

	/**
	 * Construct a new Column with a given {@link Cell}.
	 * 
	 * @param cell
	 *            the Cell used by this Column
	 */
	public DynamicBaseColumn(DecisionTableCellValueAdaptor<? extends Comparable<?>> cell) {
		this.cell = cell;
	}

	/**
	 * Returns the {@link Cell} responsible for rendering items in the column.
	 * 
	 * @return a Cell
	 */
	public DecisionTableCellValueAdaptor<? extends Comparable<?>> getCell() {
		return cell;
	}

	public void setCell(DecisionTableCellValueAdaptor<? extends Comparable<?>> cell) {
		this.cell=cell;
	}
	
	/**
	 * Returns the {@link FieldUpdater} used for updating values in the column.
	 * 
	 * @return an instance of FieldUpdater<T, C>
	 * @see #setFieldUpdater(FieldUpdater)
	 */
	public FieldUpdater<DynamicDataRow, CellValue<? extends Comparable<?>>> getFieldUpdater() {
		return fieldUpdater;
	}

	/**
	 * Returns the column value from within the underlying data object.
	 */
	public abstract CellValue<? extends Comparable<?>> getValue(DynamicDataRow object);

	/**
	 * Handle a browser event that took place within the column.
	 * 
	 * @param elem
	 *            the parent Element
	 * @param index
	 *            the current row index of the object
	 * @param object
	 *            the base object to be updated
	 * @param event
	 *            the native browser event
	 * @param keyProvider
	 *            an instance of ProvidesKey<T>, or null if the record object
	 *            should act as its own key.
	 */
	public void onBrowserEvent(Element elem, final int index, final DynamicDataRow object,
			NativeEvent event, ProvidesKey<DynamicDataRow> keyProvider) {
		Object key = getKey(object, keyProvider);
		ValueUpdater<CellValue<? extends Comparable<?>>> valueUpdater = (fieldUpdater == null) ? null
				: new ValueUpdater<CellValue<? extends Comparable<?>>>() {
					public void update(CellValue<? extends Comparable<?>> value) {
						fieldUpdater.update(index, object, value);
					}
				};
		cell.onBrowserEvent(elem, getValue(object), key, event, valueUpdater);
	}

	/**
	 * Render the object into the cell.
	 * 
	 * @param object
	 *            the object to render
	 * @param keyProvider
	 *            the {@link ProvidesKey} for the object
	 * @param sb
	 *            the buffer to render into
	 */
	public void render(DynamicDataRow object, ProvidesKey<DynamicDataRow> keyProvider, SafeHtmlBuilder sb) {
		Object key = getKey(object, keyProvider);
		cell.render(getValue(object), key, sb);
	}

	/**
	 * Set the {@link FieldUpdater} used for updating values in the column.
	 * 
	 * @param fieldUpdater
	 *            the field updater
	 * @see #getFieldUpdater()
	 */
	public void setFieldUpdater(FieldUpdater<DynamicDataRow, CellValue<? extends Comparable<?>>> fieldUpdater) {
		this.fieldUpdater = fieldUpdater;
	}

	/**
	 * Get the view key for the object given the {@link ProvidesKey}. If the
	 * {@link ProvidesKey} is null, the object is used as the key.
	 * 
	 * @param object
	 *            the row object
	 * @param keyProvider
	 *            the {@link ProvidesKey}
	 * @return the key for the object
	 */
	private Object getKey(DynamicDataRow object, ProvidesKey<DynamicDataRow> keyProvider) {
		return keyProvider == null ? object : keyProvider.getKey(object);
	}
}
