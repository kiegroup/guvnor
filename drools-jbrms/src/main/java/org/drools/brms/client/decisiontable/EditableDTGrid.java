package org.drools.brms.client.decisiontable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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
	
	private static final int EDIT_COLUMN_OFFSET = 1;
	private static final int INSERT_COLUMN_OFFSET = EDIT_COLUMN_OFFSET + 1;
	private static final int DELETE_COLUMN_OFFSET = INSERT_COLUMN_OFFSET + 1;
	private static final int SHUFFLE_UP_COLUMN_OFFSET = DELETE_COLUMN_OFFSET + 1;
	private static final int SHUFFLE_DOWN_COLUMN_OFFSET = SHUFFLE_UP_COLUMN_OFFSET + 1;

	private FlexTable table = new FlexTable();

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

		vert.add(header);
		vert.add(table);

		table.setStyleName("dt-editor-Grid");

		// set up the header
		populateHeader();

		// and the data follows
		populateDataGrid();

		// and this is how you span/merge things, FYI
		// table.getFlexCellFormatter().setColSpan( 2, 3, 4 );

		// needed for Composite
		initWidget(vert);
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
				table.setText(row, column, "boo " + column);
				cellFormatter.setStyleName(row, column, "dt-editor-Cell");
			}

			final int currentRow = row;

			addActions(currentRow, false);
		}
	}

	private void addActions(final int currentRow, boolean newRow) {
		// the action magic
		addEditActions(currentRow, newRow);

		addInsertAction(currentRow);
		addDeleteAction(currentRow);
		addShuffleUpAction(currentRow);
		addShuffleDownAction(currentRow);
	}

	private void addShuffleDownAction(final int currentRow) {
		final ShuffleAction shuffleDownAction;
		if (currentRow == numRows()) {
			shuffleDownAction = new ShuffleAction();
		} else {
		shuffleDownAction = new ShuffleDownAction(
				currentRow, new RowClickListener() {
					public void onClick(Widget w, int row) {
						shuffleDown(row);
					}
				});
		}
		table.setWidget(currentRow, numCols() + SHUFFLE_DOWN_COLUMN_OFFSET, shuffleDownAction);
	}

	private void addShuffleUpAction(final int currentRow) {
		final ShuffleAction shuffleUpAction;
		if (currentRow == START_DATA_ROW) {
			shuffleUpAction = new ShuffleAction();
		} else {

			shuffleUpAction = new ShuffleUpAction(currentRow,
					new RowClickListener() {
						public void onClick(Widget w, int row) {
							shuffleUp(row);
						}
					});
		}
		table.setWidget(currentRow, numCols() + SHUFFLE_UP_COLUMN_OFFSET, shuffleUpAction);
	}

	private void addDeleteAction(final int currentRow) {
		final DeleteAction deleteAction = new DeleteAction(currentRow,
				new RowClickListener() {
					public void onClick(Widget w, int row) {
						deleteRow(row);
					}
				});
		table.setWidget(currentRow, numCols() + DELETE_COLUMN_OFFSET, deleteAction);
	}

	private void addInsertAction(final int currentRow) {
		final InsertAction insertAction = new InsertAction(currentRow,
				new RowClickListener() {
					public void onClick(Widget w, int row) {
						insertRow(row);
					}
				});
		table.setWidget(currentRow, numCols() + INSERT_COLUMN_OFFSET, insertAction);
	}

	private void addEditActions(final int currentRow, boolean newRow) {
		final EditActions actions = new EditActions(currentRow,
				new RowClickListener() {
					public void onClick(Widget w, int row) {
						editRow(row);
					}
				}, new RowClickListener() {
					public void onClick(Widget w, int row) {
						updateRow(row);
					}
				});
		if (newRow)
			actions.makeEditable();
		table.setWidget(currentRow, numCols() + EDIT_COLUMN_OFFSET, actions);
	}

	/**
	 * Listener for click events that affect a whole row
	 */
	interface RowClickListener {
		void onClick(Widget w, int row);
	}

	// counter for dummy new row data
	private int cellId;

	private int numRows = 14;

	/**
	 * Shuffle the given row up one
	 * @param row
	 */
	private void shuffleUp(int row) {
		//create a new row above the given row
		//remember that insertRow will insert above the given row
		//so this is two above the original row
		int newRow = addNewRow(row - 1);
		
		copyRow(row + 1, newRow);
		addActions(newRow, false);
		
		//remove the original row
		table.removeRow(row + 1);
		
		//if the original row was the second row then we need to add a shuffle up
		//widget to the new second row (originally the first row)
		if (row == START_DATA_ROW + 1) {
			addShuffleUpAction(row);
		}
		//if the original row was the last row we need to remove the shuffle down
		//widget from the new last row
		if (row == numRows()) {
			removeShuffleDown();
		}
		renumberRow(row - 1);
		renumberRow(row);

	}

	/**
	 * Copy the data from the source row to the target row
	 * @param sourceRow
	 * @param targetRow
	 */
	private void copyRow(int sourceRow, int targetRow) {
		//TODO handle editable rows
		int column = 1;
		for (; column < numCols() + 1; column++) {
			table.setText(targetRow, column, table.getText(sourceRow, column));
			cellFormatter.setStyleName(targetRow, column, "dt-editor-Cell");
		}
	}
	/**
	 * Add a new row and set the row number
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
	 * @param row
	 */
	private void shuffleDown(int row) {
		//create a new row below the given row
		//remember that insertRow will insert above the given row
		//so this is two below the original row
		int newRow = addNewRow(row + 2);
		
		copyRow(row, newRow);
		//remove row before adding action widgets so that the row number
		//is correct (otherwise the shuffle down button may not be displayed
		table.removeRow(row);
		addActions(newRow - 1, false);
		//if the original row was the first row we need to remove the shuffle up
		//widget from the new first row
		if (row == START_DATA_ROW) {
			removeShuffleUp();
		}
		//if the original row was the second last row then we need to add a shuffle down
		//widget to the new second last row (originally the last row)
		if (row == numRows() - 1) {
			addShuffleDownAction(row);
		}
		renumberRow(row);
		renumberRow(row + 1);

	}

	private void removeShuffleUp() {
		table.setWidget(START_DATA_ROW, numCols() + SHUFFLE_UP_COLUMN_OFFSET, new ShuffleAction());
	}
	private void removeShuffleDown() {
		table.setWidget(numRows(), numCols() + SHUFFLE_DOWN_COLUMN_OFFSET, new ShuffleAction());
	}

	/**
	 * Delete the specified row
	 * 
	 * @param row
	 *            the row to delete
	 */
	private void deleteRow(int row) {
		numRows--;
		table.removeRow(row);
		reorderRows(row);
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
	 * @param row
	 */
	private void renumberRow(int row) {
		table.setText(row, 0, Integer.toString(row));
		int lastColumn = numCols();
		EditActions editActions = (EditActions) table.getWidget(row,
				lastColumn + EDIT_COLUMN_OFFSET);
		editActions.setRow(row);
		InsertAction insertAction = (InsertAction) table.getWidget(row,
				lastColumn + INSERT_COLUMN_OFFSET);
		insertAction.setRow(row);
		DeleteAction deleteAction = (DeleteAction) table.getWidget(row,
				lastColumn + DELETE_COLUMN_OFFSET);
		deleteAction.setRow(row);
		ShuffleAction shuffleUpAction = (ShuffleAction) table.getWidget(row,
				lastColumn + SHUFFLE_UP_COLUMN_OFFSET);
		shuffleUpAction.setRow(row);
		ShuffleAction shuffleDownAction = (ShuffleAction) table.getWidget(
				row, lastColumn + SHUFFLE_DOWN_COLUMN_OFFSET);
		shuffleDownAction.setRow(row);
	}

	/**
	 * Add a new row after the specified row
	 * 
	 * @param row
	 *            the row to insert after
	 */
	private void insertRow(int row) {
		numRows++;
		int newRow = addNewRow(row + 1);
		int column = 1;
		for (; column < numCols() + 1; column++) {
			table.setText(newRow, column, "new " + cellId++);
			cellFormatter.setStyleName(newRow, column, "dt-editor-Cell");
		}
		addActions(newRow, true);
		editRow(newRow);
		reorderRows(newRow);
	}

	/**
	 * Apply the changes to the row.
	 */
	private void updateRow(int row) {
		for (int column = 1; column < numCols() + 1; column++) {
			TextBox text = (TextBox) table.getWidget(row, column);
			table.setText(row, column, text.getText());
		}
	}

	/**
	 * This switches the given row into edit mode.
	 * 
	 * @param row
	 */
	private void editRow(int row) {
		for (int column = 1; column < numCols() + 1; column++) {
			String text = table.getText(row, column);
			TextBox box = new TextBox();
			box.setText(text);
			box.setStyleName("dsl-field-TextBox");
			box.setVisibleLength(3);
			table.setWidget(row, column, box);

		}
	}

	private int numCols() {
		return 6;
	}

	private int numRows() {
		return numRows;
	}

}
