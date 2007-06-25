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