package org.drools.brms.client.decisiontable;

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

	private FlexTable table = new FlexTable();

	private int currentRow;

	private int currentCol;

	private FlexCellFormatter cellFormatter = table.getFlexCellFormatter();

	public EditableDTGrid(String dtName) {
		// for if I switch to a Grid
		// table.resizeColumns( numCols() + 1 );
		// table.resizeRows( numRows() );

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

	protected void setCurrentCell(int row, int col) {

		if (col > 0 && col <= numCols() && row >= START_DATA_ROW
				&& (row != currentRow || col != currentCol)) {
			if (currentRow > START_DATA_ROW) {
				TextBox text = (TextBox) table
						.getWidget(currentRow, currentCol);
				newCell(currentRow, currentCol, text.getText());
			}
			if (currentRow != row) {
				if (currentRow > START_DATA_ROW) {
					cellFormatter.setStyleName(currentRow, 0,
							"dt-editor-CountColumn");
				}
				cellFormatter.setStyleName(row, 0,
						"dt-editor-CountColumn-selected");
			}
			if (currentCol != col) {
				if (currentCol > 0) {
					cellFormatter.setStyleName(0, currentCol,
							"dt-editor-DescriptionCell");
				}
				cellFormatter.setStyleName(0, col,
						"dt-editor-DescriptionCell-selected");
			}
			cellFormatter.setStyleName(row, col, "dt-editor-Cell-selected");
			currentRow = row;
			currentCol = col;
			editColumn(row, col);
		}
	}

	private void populateHeader() {

		// for the count column
		cellFormatter.setStyleName(0, 0, "dt-editor-DescriptionCell");

		for (int col = 1; col < numCols() + 1; col++) {
			table.setText(0, col, "some header " + col);
			cellFormatter.setStyleName(0, col, "dt-editor-DescriptionCell");
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

		for (int i = 0; i < numRows(); i++) {

			int rowCount = i + 1;

			int column = 1;
			int row = i + START_DATA_ROW;

			// now do the count column
			table.setText(row, 0, Integer.toString(rowCount));
			cellFormatter.setStyleName(row, 0, "dt-editor-CountColumn");
			for (; column < numCols() + 1; column++) {
				String text = "boo " + column;
				newCell(row, column, text);
			}

		}
	}

	private void newCell(int row, int column, String text) {
		table.setText(row, column, text);
		cellFormatter.setStyleName(row, column, "dt-editor-Cell");
	}



	public void mergeCell(int row, int col) {
		int currentSpan = cellFormatter.getColSpan(row, col);
		if (col + currentSpan <= numCols()) {
			int nextSpan = cellFormatter.getColSpan(row, col + 1);
			table.removeCell(row, col + 1);
			cellFormatter.setColSpan(row, col, currentSpan + nextSpan);
		}
	}

	public void splitCell(int row, int col) {
		int currentSpan = cellFormatter.getColSpan(row, col);
		if (currentSpan > 1) {
			cellFormatter.setColSpan(row, col++, currentSpan - 1);
			table.insertCell(row, col);
			newCell(row, col, "");
			cellFormatter.setStyleName(row, col, "dt-editor-Cell");
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
		// create a new row below the given row
		// remember that insertRow will insert above the given row
		// so this is two below the original row
		int newRow = addNewRow(currentRow + 2);

		copyRow(currentRow, newRow);
		// remove row before adding action widgets so that the row number
		// is correct (otherwise the shuffle down button may not be displayed
		table.removeRow(currentRow);
		renumberRow(currentRow);
		renumberRow(currentRow + 1);
		currentRow = -1;
		setCurrentCell(newRow - 1, currentCol);

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
		box.setStyleName("dsl-field-TextBox");
		table.setWidget(row, column, box);
		box.setFocus(true);
	}

	private int numCols() {
		return 6;
	}

	private int numRows() {
		return numRows;
	}

}
