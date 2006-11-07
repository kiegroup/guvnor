package org.drools.brms.client.decisiontable.model;

public 
class Cell {
	private int rowspan;
	private int colspan;
	private String value;
	private Row row;
	private Column column;
	
	Cell(Row row, Column col) {
		this(row, col, 1, 1);
	}
	Cell(final Row r, final Column c, final int colspan, final int rowspan) {
		this.row = r;
		this.column = c;
		this.colspan = colspan;
		this.rowspan = rowspan;
	}
	public void setValue(final String value) {
		this.value = value;
	}
	public int getRowIndex() {
		return row.getIndex();
	}
	public int getColumnIndex() {
		return column.getIndex();
	}
	public Row getRow() {
		return row;
	}
	public void setRow(Row row) {
		this.row = row;
	}
	public int getRowspan() {
		return rowspan;
	}
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
	public int getColspan() {
		return colspan;
	}
	public Column getColumn() {
		return column;
	}
	public String getValue() {
		return value;
	}
	public String toString() {
		return "Cell[" + getRowIndex() + ", " + getColumnIndex() + ", " + getValue() + "]";
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	
	
}
