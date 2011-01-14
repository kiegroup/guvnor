package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.table.SortDirection;

/**
 * Container for sort information. Encapsulated in a single class to avoid the
 * need for multiple ValueChangeHandlers for all attributes affecting sorting on
 * a Column.
 * 
 * @author manstis
 * 
 */
public class SortConfiguration {
	private SortDirection sortDirection = SortDirection.NONE;
	private Boolean isSortable = true;
	private int sortIndex = -1;

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public Boolean isSortable() {
		return isSortable;
	}

	public void setSortable(Boolean isSortable) {
		this.isSortable = isSortable;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

}
