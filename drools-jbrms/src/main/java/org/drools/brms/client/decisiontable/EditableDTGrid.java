package org.drools.brms.client.decisiontable;
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



import java.util.Iterator;

import org.drools.brms.client.decisiontable.model.Cell;
import org.drools.brms.client.decisiontable.model.Column;
import org.drools.brms.client.decisiontable.model.DecisionTable;
import org.drools.brms.client.decisiontable.model.Row;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * The decision table viewer and editor.
 * 
 * @author Michael Neale
 * @author Steven Williams
 * 
 * TODO: add editors for header stuff, and ability to add rows/cols and shift
 * rows around This probably can be done from a seperate "editor" such that it
 * is re-rendered when you need to add/move a col or row.
 * 
 * Should be able to add/shift stuff around by entering a row number to deal
 * with.
 * 
 */
public class EditableDTGrid extends Composite {

	private static final int START_DATA_ROW = 1;

	private DecisionTable dt;

	private FlexTable table = new FlexTable();

	private int currentRow;

	private int currentCol;

	private Cell currentCell;

	private FlexCellFormatter cellFormatter = table.getFlexCellFormatter();

	public EditableDTGrid(String dtName) {
		this(dtName, createDecisionTable());
	}

	private static DecisionTable createDecisionTable() {
		DecisionTable dt = new DecisionTable();
		Column[] columns = { dt.createColumn(), dt.createColumn(),
				dt.createColumn(), dt.createColumn(), dt.createColumn(),
				dt.createColumn() };
		createHeader(dt, columns);
		for (int i = 0; i < 15; i++) {
			Row row = dt.createRow();
			for (int j = 0; j < columns.length; j++) {
				Cell cell = dt.createCell(row, columns[j]);
				cell.setValue("boo " + i + ":" + j);
			}
		}
		return dt;
	}

	private static void createHeader(DecisionTable dt, Column[] columns) {
		Row row = dt.getHeaderRow();
		for (int i = 0; i < 6; i++) {
			Cell cell = dt.createCell(row, columns[i]);
			cell.setValue("some header " + i);
		}
	}

	public EditableDTGrid(final String dtName, final DecisionTable dt) {
		this.dt = dt;
		VerticalPanel vert = new VerticalPanel();

		Label title = new Label(dtName);
		title.setStyleName("dt-editor-Title");

		HorizontalPanel header = new HorizontalPanel();
		header.add(new Image("images/decision_table.gif"));
		header.add(title);

		Toolbar toolbar = new Toolbar(this);

		vert.add(header);
		vert.add(toolbar);
		vert.add(table);

		table.setStyleName("dt-editor-Grid");
		table.addTableListener(new TableListener() {

			public void onCellClicked(SourcesTableEvents ste, int row, int col) {
				setCurrentCell(row, col);
			}
		});

		// set up the header
		populateHeader();

		// and the data follows
		populateDataGrid();

		// needed for Composite
		initWidget(vert);
	}

	protected void setCurrentCell(int row, int column) {
		if (column > 0 && column <= numCols() && row >= START_DATA_ROW) {
			Cell selectedCell = dt.getRow(row - 1).getCell(column - 1);
			if (selectedCell != currentCell) {
				int col = selectedCell.getColumnIndex() + 1;

				if (currentRow >= START_DATA_ROW) {
					TextBox text = (TextBox) table.getWidget(currentRow,
							currentCol);
					newCell(currentRow, currentCol, text.getText());
				}
				if (currentCell == null || currentRow != row) {
					if (currentCell != null) {
						cellFormatter.setStyleName(currentRow, 0,
								"dt-editor-CountColumn");
					}
					cellFormatter.setStyleName(row, 0,
							"dt-editor-CountColumn-selected");
				}
				if (currentCell == null || (currentCell.getColumnIndex() + 1) != col) {
					if (currentCell != null) {
						cellFormatter.setStyleName(0, currentCell
								.getColumnIndex() + 1,
								"dt-editor-DescriptionCell");
					}
					cellFormatter.setStyleName(0, col,
							"dt-editor-DescriptionCell-selected");
				}
				cellFormatter.setStyleName(row, column,
						"dt-editor-Cell-selected");
				currentRow = row;
				currentCol = column;
				currentCell = selectedCell;
				editColumn(row, column);
			}
		}
	}

	private void populateHeader() {
		Row row = dt.getHeaderRow();
		// for the count column
		cellFormatter.setStyleName(0, 0, "dt-editor-DescriptionCell");

		table.setText(0, 0, "");
		cellFormatter.setStyleName(0, 0, "dt-editor-CountColumn");

		for (Iterator it = dt.getColumns().iterator(); it.hasNext();) {
			Column column = (Column) it.next();
			newHeaderCell(row.getCell(column));
		}

	}

	/**
	 * This populates the "data" part of the decision table (not the header
	 * bits). It starts at the row offset.
	 * 
	 * @param cellFormatter
	 *            So it can set the style of each cell that is created.
	 */
	private void populateDataGrid() {
		int i = 1;
		for (Iterator it = dt.getRows().iterator(); it.hasNext(); i++) {
			Row row = (Row) it.next();
			table.setText(i, 0, Integer.toString(i));
			cellFormatter.setStyleName(i, 0, "dt-editor-CountColumn");
			for (Iterator it2 = dt.getColumns().iterator(); it2.hasNext();) {
				Column column = (Column) it2.next();
				newCell(row.getCell(column));
			}

		}
	}

	private void newCell(Cell cell) {
		int rowIndex = cell.getRowIndex() + START_DATA_ROW;
		int columnIndex = cell.getColumnIndex() + 1;
		newCell(cell, rowIndex, columnIndex, "dt-editor-Cell");
	}

	private void newHeaderCell(Cell cell) {
		int columnIndex = cell.getColumnIndex() + 1;
		newCell(cell, 0, columnIndex, "dt-editor-DescriptionCell");
	}

	private void newCell(Cell cell, int rowIndex, int columnIndex, String style) {
		table.setText(rowIndex, columnIndex, cell.getValue());
		cellFormatter.setStyleName(rowIndex, columnIndex, style);
		cellFormatter.setColSpan(rowIndex, columnIndex, cell.getColspan());
		cellFormatter.setRowSpan(rowIndex, columnIndex, cell.getRowspan());
	}

	private void newCell(int row, int column, String text) {
		table.setText(row, column, text);
		cellFormatter.setStyleName(row, column, "dt-editor-Cell");
	}

	public void mergeCol() {
		if (currentCell != null) {
			dt.mergeCol(currentCell);
			// if (currentRow >= START_DATA_ROW && currentCol > 0) {
			int currentSpan = cellFormatter.getColSpan(currentRow, currentCol);
			if (currentCol + currentSpan <= numCols()) {
				int nextSpan = cellFormatter.getColSpan(currentRow,
						currentCol + 1);
				int currentRowSpan = cellFormatter.getRowSpan(currentRow,
						currentCol);
				int nextRowSpan = cellFormatter.getRowSpan(currentRow,
						currentCol + 1);
				while (nextRowSpan < currentRowSpan) {
					mergeRow(currentRow, currentCol + 1);
					nextRowSpan++;
				}
				table.removeCell(currentRow, currentCol + 1);
				cellFormatter.setColSpan(currentRow, currentCol, currentSpan
						+ nextSpan);
			}
		}
	}

	public void mergeRow() {
		if (currentCell != null) {
			// if (currentRow >= START_DATA_ROW && currentCol > 0) {
			mergeRow(currentRow, currentCol);
		}
	}

	private void mergeRow(int row, int col) {
		dt.mergeRow(currentCell);
		int currentSpan = cellFormatter.getRowSpan(row, col);
		if (row + currentSpan <= numRows) {
			int nextSpan = cellFormatter.getRowSpan(row + currentSpan, col);
			table.removeCell(row + currentSpan, col);
			cellFormatter.setRowSpan(row, col, currentSpan + nextSpan);
		}
	}

	public void splitCol() {
		if (currentRow >= START_DATA_ROW && currentCol > 0) {
			int currentSpan = cellFormatter.getColSpan(currentRow, currentCol);
			if (currentSpan > 1) {
				cellFormatter.setColSpan(currentRow, currentCol,
						currentSpan - 1);
				int newCol = currentCol + 1;
				table.insertCell(currentRow, newCol);
				newCell(currentRow, newCol, "");
				cellFormatter
						.setStyleName(currentRow, newCol, "dt-editor-Cell");
			}
		}
	}

	public void splitRow() {
		if (currentRow >= START_DATA_ROW && currentCol > 0) {
			int currentSpan = cellFormatter.getRowSpan(currentRow, currentCol);
			if (currentSpan > 1) {
				cellFormatter.setRowSpan(currentRow, currentCol,
						currentSpan - 1);
				int newRow = currentRow + currentSpan - 1;
				table.insertCell(newRow, currentCol);
				newCell(newRow, currentCol, "");
				cellFormatter
						.setStyleName(newRow, currentCol, "dt-editor-Cell");
			}
		}
	}

	/**
	 * Listener for click events that affect a whole row
	 */
	interface RowClickListener {
		void onClick(Widget w, int row);
	}

	private int numRows = 14;

	/**
	 * Shuffle the given row up one
	 * 
	 * @param row
	 */
	void moveUp() {
		if (currentRow > START_DATA_ROW) {
			// create a new row above the given row
			// remember that insertRow will insert above the given row
			// so this is two above the original row
			int newRow = addNewRow(currentRow - 1);

			copyRow(currentRow + 1, newRow);

			// remove the original row
			table.removeRow(currentRow + 1);

			renumberRow(currentRow);
			currentRow = -1;
			setCurrentCell(newRow, currentCol);
		}

	}

	/**
	 * Copy the data from the source row to the target row
	 * 
	 * @param sourceRow
	 * @param targetRow
	 */
	private void copyRow(int sourceRow, int targetRow) {
		int column = 1;
		int colIndex = 1;
		while (column <= numCols()) {
			if (column == currentCol) {
				TextBox box = (TextBox) table.getWidget(sourceRow, column);
				newCell(targetRow, colIndex, box.getText());
			} else {
				newCell(targetRow, colIndex, table.getText(sourceRow, colIndex));
			}
			int colSpan = cellFormatter.getColSpan(sourceRow, colIndex);
			column = column + colSpan;
			cellFormatter.setColSpan(targetRow, colIndex++, colSpan);
		}
	}

	/**
	 * Add a new row and set the row number
	 * 
	 * @param row
	 * @return
	 */
	private int addNewRow(int row) {
		int newRow = table.insertRow(row);
		table.setText(newRow, 0, Integer.toString(newRow));
		cellFormatter.setStyleName(newRow, 0, "dt-editor-CountColumn");
		return newRow;
	}

	/**
	 * Shuffle the given row down one
	 * 
	 * @param row
	 */
	void moveDown() {
		if (currentRow < numRows) {
			// create a new row below the given row
			// remember that insertRow will insert above the given row
			// so this is two below the original row
			int newRow = addNewRow(currentRow + 2);

			copyRow(currentRow, newRow);
			// remove row before adding action widgets so that the row number
			// is correct (otherwise the shuffle down button may not be
			// displayed
			table.removeRow(currentRow);
			renumberRow(currentRow);
			renumberRow(currentRow + 1);
			currentRow = -1;
			setCurrentCell(newRow - 1, currentCol);
		}
	}

	/**
	 * Delete the specified row
	 */
	void deleteRow() {
		numRows--;
		table.removeRow(currentRow);
		reorderRows(currentRow);
		currentRow = -1;
		clearCurrentCell();
	}

	private void clearCurrentCell() {
		if (currentRow > 0) {
			cellFormatter
					.setStyleName(currentRow, currentCol, "dt-editor-Cell");
			currentRow = -1;
		}
		if (currentCol > 0) {
			cellFormatter.setStyleName(0, currentCol,
					"dt-editor-DescriptionCell");
			currentCol = -1;
		}
	}

	/**
	 * Renumber the rows from the start row and change the row the actions will
	 * act on
	 * 
	 * @param startRow
	 */
	private void reorderRows(final int startRow) {
		for (int r = startRow; r < table.getRowCount(); r++) {
			renumberRow(r);
		}
	}

	/**
	 * Renumber the given row and change the row the actions will act on
	 * 
	 * @param row
	 */
	private void renumberRow(int row) {
		table.setText(row, 0, Integer.toString(row));
	}

	/**
	 * Add a new row after the specified row
	 */
	void insertRow() {
		if (currentRow >= START_DATA_ROW) {
			numRows++;
			int newRow = addNewRow(currentRow + 1);
			int column = 1;
			for (; column < numCols() + 1; column++) {
				newCell(newRow, column, "");
			}
			// addActions(column, newRow, true);
			reorderRows(newRow);
			setCurrentCell(newRow, 1);
		}
	}

	private void editColumn(int row, int column) {
		String text = table.getText(row, column);
		editColumn(row, column, text);
	}

	private void editColumn(int row, int column, String text) {
		TextBox box = new TextBox();
		box.setText(text);
		box.setStyleName("dt-field-TextBox");
		table.setWidget(row, column, box);
		box.setFocus(true);
	}

	private int numCols() {
		return 6;
	}

}