package org.drools.brms.client.decisiontable.model;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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

	public void mergeCol(Cell currentCell) {
		Row row = currentCell.getRow();
		int currentColspan = currentCell.getColspan();
		int nextCellColumnIndex = currentCell.getColumnIndex() + currentColspan;
		Column nextColumn = (Column) columns.get(nextCellColumnIndex);
		Cell nextCell = row.getCell(nextColumn);
		currentCell.setColspan(currentColspan + nextCell.getColspan());
		row.removeColumn(nextColumn);
		nextColumn.removeCell(nextCell);
	}
	
	public void mergeRow(Cell currentCell) {
		Column column = currentCell.getColumn();
		int currentRowspan = currentCell.getRowspan();
		int nextCellRowIndex = currentCell.getRowIndex() + currentRowspan;
		Row nextRow = (Row) rows.get(nextCellRowIndex);
		Cell nextCell = nextRow.getCell(column);
		currentCell.setRowspan(currentRowspan + nextCell.getRowspan());
		nextRow.removeColumn(column);
		column.removeCell(nextCell);
	}
}