package org.drools.brms.client.table;

import java.util.ArrayList;
import java.util.List;

/** This is used to hold the data for sorting in a grid */
public class RowData implements Comparable {
	
	List columnValues = new ArrayList();
	int sortColIndex = 0;

	public void addColumnValue(Comparable value){
		this.columnValues.add(value);
	}
	
	public void addColumnValue(int index, Comparable value){
		if(index >= this.columnValues.size()){
			addNullColumns(index);
		}
		this.columnValues.set(index, value);
	}	

	public Object getColumnValue(int index){
		return this.columnValues.get(index);
	}	
	
	public List getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(List columnValues) {
		this.columnValues = columnValues;
	}

	public int getSortColIndex() {
		return sortColIndex;
	}

	public void setSortColIndex(int sortColIndex) {
		this.sortColIndex = sortColIndex;
	}

	public int compareTo(Object other) {
		if(null == other){
			return -1;
		}
		RowData otherRow = (RowData)other;
		Comparable obj1 = (Comparable)this.getColumnValue(this.sortColIndex);
		Comparable obj2 = (Comparable)otherRow.getColumnValue(this.sortColIndex);
		return obj1.compareTo(obj2);
	}
	
	private void addNullColumns(int index){
		for(int nullIndex=this.columnValues.size(); nullIndex<=index; nullIndex++){
			columnValues.add(null);
		}
	}
}
