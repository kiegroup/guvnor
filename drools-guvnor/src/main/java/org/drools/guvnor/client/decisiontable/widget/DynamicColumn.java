package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.decisiontable.cells.DecisionTableCellValueAdaptor;
import org.drools.guvnor.client.table.SortDirection;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;

/**
 * A column that retrieves it's cell value from an indexed position in a List
 * holding the row data. Normally the row type is defined as a statically typed
 * Class and columns retrieve their cell values from discrete members. A
 * Decision Table's row contains dynamic (i.e. a List) of elements.
 * 
 * @author manstis
 * 
 */
public class DynamicColumn extends DynamicBaseColumn {

	private int columnIndex = 0;
	private DTColumnConfig modelColumn;
	private Boolean isVisible = new Boolean(true);
	private Boolean isSystemControlled = new Boolean(false);
	private SortDirection sortDirection = SortDirection.NONE;
	private Boolean isSortable = true;
	private int sortIndex = -1;
	private int width = 100;

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
		this.isSortable = isSortable;
		this.isSystemControlled = isSystemControlled;
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
				&& c.sortDirection == this.sortDirection
				&& c.isSortable == this.isSortable
				&& c.sortIndex == this.sortIndex && c.width == this.width;
	}

	public int getColumnIndex() {
		return this.columnIndex;
	}

	public DTColumnConfig getModelColumn() {
		return this.modelColumn;
	}

	public SortDirection getSortDirection() {
		return this.sortDirection;
	}

	public int getSortIndex() {
		return sortIndex;
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
		hash = 31 * hash + sortDirection.hashCode();
		hash = 31 * hash + isSortable.hashCode();
		hash = 31 * hash + sortIndex;
		hash = 31 * hash + width;
		return hash;
	}

	public boolean isSortable() {
		return isSortable;
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
		this.isSortable = isSortable;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
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
