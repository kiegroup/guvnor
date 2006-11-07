package org.drools.brms.client.decisiontable.model;

import java.util.ArrayList;
import java.util.List;

public class DecisionTable {
	private Row headerRow = new HeaderRow(this);
	private List rows = new ArrayList();
	private List columns = new ArrayList();
	
	void addRow() {
		
	}
	
	void addColumn(final Column column) {
		columns.add(column);
	}
	
	public Row createRow() {
		Row row = new Row(this);
		rows.add(row);
		return row;
	}
	
	public Column createColumn() {
		Column column = new Column(this);
		columns.add(column);
		return column;
	}

	public Cell createCell(Row row, Column column) {
		Cell cell = new Cell(row, column);
		row.addCell(cell);
		column.addCell(cell);
		return cell;
	}

	public Row getHeaderRow() {
		return headerRow;
	}

	public List getColumns() {
		return columns;
	}

	public List getRows() {
		return rows;
	}
	public Row getRow(int index) {
		return (Row) rows.get(index);
	}
	
	public int getRowIndex(Row row) {
		return rows.indexOf(row);
	}
	public int getColumnIndex(Column column) {
		return columns.indexOf(column);
	}
}
