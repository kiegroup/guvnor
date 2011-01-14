package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.decisiontable.cells.DecisionTableCellValueAdaptor;
import org.drools.guvnor.client.table.SortDirection;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * A column that retrieves it's cell value from an indexed position in a List
 * holding the row data. Normally the row type is defined as a statically typed
 * Class and columns retrieve their cell values from discrete members. A
 * Decision Table's row contains dynamic (i.e. a List) of elements.
 * 
 * @author manstis
 * 
 */
public class DynamicColumn extends DynamicBaseColumn implements
		HasValueChangeHandlers<SortConfiguration> {

	private int columnIndex = 0;
	private DTColumnConfig modelColumn;
	private Boolean isVisible = new Boolean(true);
	private Boolean isSystemControlled = new Boolean(false);
	private SortConfiguration sortConfig = new SortConfiguration();
	private int width = 100;

	// Event handling using GWT's EventBus
	private SimpleEventBus seb = new SimpleEventBus();

	public DynamicColumn(DTColumnConfig modelColumn,
			DecisionTableCellValueAdaptor<? extends Comparable<?>> cell,
			int columnIndex) {
		this(modelColumn, cell, columnIndex, false, true);
	}

	public DynamicColumn(DTColumnConfig modelColumn,
			DecisionTableCellValueAdaptor<? extends Comparable<?>> cell,
			int columnIndex, boolean isSystemControlled, boolean isSortable) {
		super(cell);
		this.modelColumn = modelColumn;
		this.columnIndex = columnIndex;
		this.sortConfig.setSortable(isSortable);
		this.isSystemControlled = isSystemControlled;
	}

	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<SortConfiguration> handler) {
		return seb.addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		DynamicColumn c = (DynamicColumn) o;
		return c.columnIndex == this.columnIndex
				&& c.modelColumn == this.modelColumn
				&& c.isVisible == this.isVisible
				&& c.isSystemControlled == this.isSystemControlled
				&& c.sortConfig.getSortDirection() == this.sortConfig
						.getSortDirection()
				&& c.sortConfig.isSortable() == this.sortConfig.isSortable()
				&& c.sortConfig.getSortIndex() == this.sortConfig
						.getSortIndex() && c.width == this.width;
	}

	public void fireEvent(GwtEvent<?> event) {
		seb.fireEvent(event);
	}

	public int getColumnIndex() {
		return this.columnIndex;
	}

	public DTColumnConfig getModelColumn() {
		return this.modelColumn;
	}

	public SortDirection getSortDirection() {
		return this.sortConfig.getSortDirection();
	}

	public int getSortIndex() {
		return this.sortConfig.getSortIndex();
	}

	@Override
	public CellValue<?> getValue(DynamicDataRow object) {
		return (CellValue<?>) object.get(columnIndex);
	}

	public int getWidth() {
		return width;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + columnIndex;
		hash = 31 * hash + modelColumn.hashCode();
		hash = 31 * hash + isVisible.hashCode();
		hash = 31 * hash + isSystemControlled.hashCode();
		hash = 31 * hash + sortConfig.getSortDirection().hashCode();
		hash = 31 * hash + sortConfig.isSortable().hashCode();
		hash = 31 * hash + sortConfig.getSortIndex();
		hash = 31 * hash + width;
		return hash;
	}

	public boolean isSortable() {
		return this.sortConfig.isSortable();
	}

	public boolean isSystemControlled() {
		return isSystemControlled;
	}
	
	public boolean isVisible() {
		return isVisible;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	
	public void setSortable(boolean isSortable) {
		this.sortConfig.setSortable(isSortable);
		ValueChangeEvent.fire(this, sortConfig);
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortConfig.setSortDirection(sortDirection);
		if (sortDirection == SortDirection.NONE) {
			this.sortConfig.setSortIndex(-1);
		}
		ValueChangeEvent.fire(this, sortConfig);
	}
	
	public void setSortIndex(int sortIndex) {
		this.sortConfig.setSortIndex(sortIndex);
		ValueChangeEvent.fire(this, sortConfig);
	}

	public void setSystemControlled(boolean isSystemControlled) {
		this.isSystemControlled = isSystemControlled;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
