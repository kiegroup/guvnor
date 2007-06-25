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