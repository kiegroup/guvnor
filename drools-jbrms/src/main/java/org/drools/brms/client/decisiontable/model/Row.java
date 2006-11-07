package org.drools.brms.client.decisiontable.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Row {
	private Map cells = new HashMap();
	private DecisionTable parent;
	
	Row(DecisionTable dt) {
		this.parent = dt;
	}
	void addCell(final Cell cell) {
		cells.put(cell.getColumn(), cell);
	}
	int getIndex() {
		return parent.getRowIndex(this);
	}
	public Cell getCell(Column column) {
		return (Cell) cells.get(column);
	}
	public Cell getCell(int col) {
		Iterator columns = parent.getColumns().iterator();
		int i = 0;
		Column column = null;
		while (i <= col) {
			column = (Column) columns.next();
			Cell cell = (Cell) cells.get(column);
			if (cell != null) {
				i++;
			}
		}
		return getCell(column);
	}
	public void removeColumn(Column nextColumn) {
		cells.remove(nextColumn);
	}
}
