package org.drools.brms.client.table;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.ruleeditor.EditorLauncher;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * A sortable table widget. Extends the GWT Grid widget.
 * (more performant then FlexTable).
 */
public class SortableTable extends Grid implements TableListener {
	
    
    /** The stylez */
    public static String styleListHeader = "rule-ListHeader";
    public static String styleSelectedRow = "rule-SelectedRow";
    public static String styleEvenRow = "rule-ListEvenRow";
    
    public static String styleList = "rule-List";
    
    
    private static int SORT_ASCENDING = 0;
    private static int SORT_DESCENDING = 1;    
    
    /** vars for current storting state */
	private int sortColIndex		=	-1;
	private int sortDirection		=	-1;

    /** Icons for sorting status */
	private String ascendingIcon 	= "images/shuffle_up.gif";
	private String descendingIcon	= "images/shuffle_down.gif";
	private String blankImage		= "images/up_down.gif";

	// Holds the data rows of the table
	// This is a list of RowData Object
	private List tableRows 			= new ArrayList();
	
	// Holds the data for the column headers
	private List tableHeader 		= new ArrayList();
    private int hideColumnIndex;
    private int selectedRow;
    private int RowNumbers;
    private boolean hasIcons = true;

    
	/** 
     * Create a sortable table widget, of the specified proportions.
     * The number of rows does NOT include the header. 
	 */
	public SortableTable(int rows, int cols){       
		super(rows + 1, cols);		
        
		this.addTableListener(this);
        setStyleName( styleList );
        
	}
    
    /**
     * This will return a sortable table ready to go.
     * @param rows The data.
     * @param header Headers.
     * @param fillRows The number of rows to pad out, if needed
     * @param hasIcons 
     * @return A SortableTable ready to go !
     */
    public static SortableTable createTableWidget(DataModel data, String[] header, int fillRows, boolean hasIcons) {
        SortableTable tableWidget = null;
        
        if (fillRows > data.getNumberOfRows()) {
            tableWidget = new SortableTable(fillRows, header.length + 1);
            tableWidget.setValue( 1, 1, "", null );
        } else {
            tableWidget = new SortableTable(data.getNumberOfRows() + 1, header.length + 1);    
        }        
        
        tableWidget.setColumnHeader( "", 0 );
        
        for ( int i = 0; i < header.length; i++ ) {
            tableWidget.setColumnHeader( header[i], i + 1 );
        }
        
        
        tableWidget.setHiddenColumn( 0 );
        for ( int i = 0; i < data.getNumberOfRows(); i++ ) {
            tableWidget.setValue( i + 1, 0, data.getRowId( i ), null );
            for ( int j = 0; j < header.length; j++ ) {
                //tableWidget.setValue( i + 1, j + 1,  cols[j], null );
                tableWidget.setValue( i + 1, j + 1,  data.getValue( i, j ), data.getWidget( i, j ) );
            }
        }
        
        tableWidget.setHasIcons(hasIcons);
        
        return tableWidget;
    }    

	private void setHasIcons(boolean hasIcons) {
	    this.hasIcons = hasIcons;
	}

    /** 
     * Adds a header, which will be at the zero index in the table.
	 */
	public void setColumnHeader(String name, int index){               
		tableHeader.add(index, name);
		this.renderTableHeader(name, index);
	}

    
    /** 
     * This can be used to ensure that a column is invisible.
     * This will also include the header (first row)
     * You would use this to allow a "key" column to be stored with the data.
     * For example, a UUID for a rule. 
     */
    public void setHiddenColumn(int colIndex) {
        this.hideColumnIndex = colIndex;
        this.getCellFormatter().setVisible( 0, colIndex, false );
    }
    
	/**
     * This will store the value in the x,y position.
     * Values must be comparable for sorting to work of course.
     * Start with a row index of 1 otherwise as zero means header.
	 */
	public void setValue(int row, int col, Comparable val, Widget w){
	    
        
		if(row == 0)return;
        
        //for alternate for highlighting.
        resetStyle( row,  col );
        
		if((row-1) >= this.tableRows.size() || null == tableRows.get(row-1)){
			tableRows.add(row-1, new RowData());
		}
		
		RowData rowData = (RowData)this.tableRows.get(row-1); 
		rowData.addColumnValue(col, val);
        if (w == null) {
            this.setText(row, col, "" + val.toString()+ "");
        } else {
            this.setWidget( row, col, w );
        }
        
        //and hiding the required column
        if (col == hideColumnIndex) {
            getCellFormatter().setVisible( row, col, false );
        }
	}

    private void resetStyle(int rowIndex,
                            int colIndex) {
        if (rowIndex % 2 == 0) {           
            getCellFormatter().setStyleName( rowIndex, colIndex, styleEvenRow  );
        } 
    }
    

    /** This performs the sorting */
	public void sort(int columnIndex){
		Collections.sort(this.tableRows);
		if(this.sortColIndex != columnIndex){
			// New Pattern Header clicked
			// Reset the sortDirection to ASC
			this.sortDirection = SORT_ASCENDING;
		}else{
			// Reverse the sortDirection
			this.sortDirection = (this.sortDirection == SORT_ASCENDING)? SORT_DESCENDING:SORT_ASCENDING; 
		}
		this.sortColIndex = columnIndex;
	}
	
	/** 
     * When a cell is clicked, the selected row is styled, and 
     * the currently selected row is remembered.
     * 
     * If it was in-fact a header that was clicked, then it will sort the 
     * data and redisplay the grid.
	 */
	public void onCellClicked(SourcesTableEvents sender, int row, int col) {
	    
	    if ( row <= tableRows.size()) {
	        styleSelectedRow(row);
	        clickSort( row, col );
	    }
	    
	}
    
    /**
     * This will apply the "highlight" for the selected row, and remove it from the previous
     * one, and set the selectedRow.
     */
    private void styleSelectedRow(int row) {
        if (row != 0 ) {
            CellFormatter formatter = getCellFormatter();
            for (int i=1; i < this.getColumnCount(); i++ ) {
                formatter.setStyleName( row, i, styleSelectedRow );
                
                if (selectedRow % 2 == 0 && selectedRow != 0) {
                    formatter.setStyleName( selectedRow, i, styleEvenRow );
                } else {
                    formatter.removeStyleName( selectedRow, i, styleSelectedRow );
                }
            }
            
            selectedRow = row;
        }
    }
    
    /**
     * @return The selected row index.
     */
    public int getSelectedRow() {
        return this.selectedRow;
    }
    
    /**
     * This will return the key of the selected row.
     */
    public String getSelectedKey() {
        return this.getText( selectedRow, this.hideColumnIndex );
    }
         
    /**
     * This actually kicks off the sorting.
     */
    private void clickSort(int row,
                           int col) {
        if(row != 0){
			return;
		}
		this.setSortColIndex(col);
		this.sort(col);
		this.drawTable();
    }	

	/*
	 * getSortAscImage
	 * 
	 * Getter for Sort Ascending Image
	 * 
	 * @return String
	 */
	public String getSortAscImage() {
		return ascendingIcon;
	}

	/*
	 * setSortAscImage
	 * 
	 * Setter for Sort Ascending Image
	 * 
	 * @param relative path + image name (String)
	 * e.g. images/asc.gif
	 */
	public void setSortAscImage(String sortAscImage) {
		this.ascendingIcon = sortAscImage;
	}

	/*
	 * getSortDescImage
	 * 
	 * Getter for Sort Descending Image
	 * 
	 * @return String
	 */
	public String getSortDescImage() {
		return descendingIcon;
	}

	/*
	 * setSortDescImgage
	 * 
	 * Setter for Sort Descending Image
	 * 
	 * @param relative path + image name (String)
	 * e.g. images/desc.gif
	 */
	public void setSortDescImgage(String sortDescImgage) {
		this.descendingIcon = sortDescImgage;
	}
	
	/*
	 * getBlankImage
	 * 
	 * Getter for blank Image
	 * 
	 * @return String
	 */
	public String getBlankImage() {
		return blankImage;
	}

	/*
	 * setBlankImage
	 * 
	 * Setter for the blank Image
	 * 
	 * @param relative path + image name (String)
	 * e.g. images/blank.gif
	 */
	public void setBlankImage(String blankImage) {
		this.blankImage = blankImage;
	}
	
	/*
	 * drawTable
	 * 
	 * Renders the header as well as the body 
	 * of the table
	 */
	protected void drawTable(){
		this.displayTableHeader();
		this.displayTableBody();
	}
	
	/*
	 * displayTableHeader
	 * 
	 * Renders only the table header
	 */
	private void displayTableHeader(){
		int colIndex=0;
		for(Iterator colHeaderIter = this.tableHeader.iterator(); colHeaderIter.hasNext();){
			String colHeader = (String)colHeaderIter.next();
			this.renderTableHeader(colHeader, colIndex++);
		}
        
    }
	
	/*
	 * displayTableBody
	 * 
	 * Renders the body or the remaining rows of the table
	 * except the header.
	 * It checks the sort direction and displays the rows 
	 * accordingly
	 */
	private void displayTableBody(){
		if(this.sortDirection == SORT_ASCENDING || this.sortDirection == -1){
			// Ascending order and Default Display
			for(int rowIndex=0; rowIndex<tableRows.size(); rowIndex++){
				RowData columns = (RowData)tableRows.get(rowIndex);
				for(int colIndex=0; colIndex<columns.getColumnValues().size(); colIndex++){
					Object value = columns.getColumnValue(colIndex);
	                setCell(rowIndex+1, colIndex, value.toString());
				}
			}
		}else{
			// Descending Order Display
			for(int rowIndex=tableRows.size()-1, rowNum = 1; rowIndex>=0; rowIndex--, rowNum++){
				RowData columns = (RowData)tableRows.get(rowIndex);
				for(int colIndex=0; colIndex<columns.getColumnValues().size(); colIndex++){
					Object value = columns.getColumnValue(colIndex);
					setCell(rowNum, colIndex, value.toString());
				}
			}
		}
	}
	
	private void setCell(int rowNum, int colIndex, String value) {
	    if(null != value){
            if (colIndex == 1 && hasIcons )
                this.setWidget( rowNum, colIndex, new Image("images/" + EditorLauncher.getAssetFormatIcon( value.toString() ) ) );
            else 
                this.setText(rowNum, colIndex, value.toString());
	    }
	}
	
	/*
	 * setSortColIndex
	 * 
	 * Sets the current column index being sorted
	 * 
	 * @param column index being sorted (int)
	 */
	private void setSortColIndex(int sortIndex){
		for(int rowIndex=0; rowIndex<tableRows.size(); rowIndex++){
			RowData row = (RowData)tableRows.get(rowIndex);
			row.setSortColIndex(sortIndex);
		}
	}
	
	/*
	 * renderTableHeader
	 * Renders a particular column in the Table Header
	 * 
	 * @param Pattern Name (String)
	 * @param Pattern Index (int) 
	 */
	private void renderTableHeader(String name, int index){
		StringBuffer headerText = new StringBuffer();
		headerText.append(name);
		headerText.append("&nbsp;<img border='0' src=");
		if(this.sortColIndex == index){
			if(this.sortDirection == SORT_ASCENDING){
				headerText.append("'" + this.ascendingIcon + "' alt='Ascending' ");	
			}else{
				headerText.append("'" + this.descendingIcon + "' alt='Descending' ");
			}
		}else{
			headerText.append("'" + this.blankImage + "'");
		}
		headerText.append("/>");

		this.setHTML(0, index, headerText.toString());
        getRowFormatter().setStyleName( 0, styleListHeader );
    
	}

    public int getRowNumbers() {
        return RowNumbers;
    }

    public void setRowNumbers(int rowNumbers) {
        RowNumbers = rowNumbers;
    }
    
    
}