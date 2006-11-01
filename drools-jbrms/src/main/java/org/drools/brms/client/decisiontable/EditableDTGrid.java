package org.drools.brms.client.decisiontable;

import com.google.gwt.user.client.ui.ClickListener;
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

	private static final int EDIT_COLUMN_OFFSET = 2;

	private static final int INSERT_COLUMN_OFFSET = EDIT_COLUMN_OFFSET + 1;

	private static final int DELETE_COLUMN_OFFSET = INSERT_COLUMN_OFFSET + 1;

	// private static final int SHUFFLE_UP_COLUMN_OFFSET = DELETE_COLUMN_OFFSET
	// + 1;
	//
	// private static final int SHUFFLE_DOWN_COLUMN_OFFSET =
	// SHUFFLE_UP_COLUMN_OFFSET + 1;

	// private static final int MERGE_COLUMN_OFFSET = SHUFFLE_DOWN_COLUMN_OFFSET
	// + 1;

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

		// and this is how you span/merge things, FYI
		// table.getFlexCellFormatter().setColSpan( 2, 3, 4 );

		// needed for Composite
		initWidget(vert);
	}

	protected void setCurrentCell(int row, int col) {

		if (col > 0 && col <= numCols() && row > 0 && (row != currentRow || col != currentCol)) {
			if (currentRow > 0) {
				cellFormatter.setStyleName(currentRow, currentCol,
						"dt-editor-Cell");
			}
			if (currentRow != row) {
				if (currentRow > 0) {
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

	// private class DTCell extends Composite {
	// private HorizontalPanel panel = new HorizontalPanel();
	//
	// private Image merge;
	//
	// private Label content;
	//
	// private MergeListener mergeListener;
	//
	// private SplitListener splitListener;
	//
	// DTCell(String value, final MergeListener mergeListener,
	// final SplitListener splitListener) {
	// this.mergeListener = mergeListener;
	// this.splitListener = splitListener;
	// content = new Label(value);
	// merge = new Image("images/refresh.gif");
	// merge.setTitle("Merge cells in this row");
	// merge.addClickListener(new ClickListener() {
	//
	// public void onClick(Widget w) {
	// MyPopup popup = new MyPopup(mergeListener, splitListener);
	// popup.setPopupPosition(w.getAbsoluteLeft(), w
	// .getAbsoluteTop() + 20);
	// popup.show();
	// }
	// });
	// panel.add(content);
	// panel.add(merge);
	// initWidget(panel);
	// }
	//
	// public void setColumn(int col) {
	// mergeListener.setColumn(col);
	// splitListener.setColumn(col);
	// }
	//
	// public void setRow(int row) {
	// mergeListener.setRow(row);
	// splitListener.setRow(row);
	// }
	// }

	// private static class MyPopup extends PopupPanel {
	//
	// public MyPopup(ClickListener mergeListener, ClickListener splitListener)
	// {
	// // PopupPanel's constructor takes 'auto-hide' as its boolean
	// // parameter.
	// // If this is set, the panel closes itself automatically when the
	// // user
	// // clicks outside of it.
	// super(true);
	//
	// // PopupPanel is a SimplePanel, so you have to set it's widget
	// // property to
	// // whatever you want its contents to be.
	// HorizontalPanel panel = new HorizontalPanel();
	// Image mergeLeft = new Image("images/shuffle_up.gif");
	// mergeLeft.addClickListener(mergeListener);
	// Image mergeRight = new Image("images/shuffle_down.gif");
	// mergeRight.addClickListener(splitListener);
	// panel.add(mergeLeft);
	// panel.add(mergeRight);
	// panel.setBorderWidth(1);
	// setWidget(panel);
	// }
	// }

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
				// table.setText(row, column, "boo " + column);
				String text = "boo " + column;
				newCell(row, column, text);
				cellFormatter.setStyleName(row, column, "dt-editor-Cell");
			}

			final int currentRow = row;

			addActions(column, currentRow, false);
		}
	}

	private void newCell(int row, int column, String text) {
		table.setText(row, column, text);
		// table.setWidget(row, column, new DTCell(text,
		// new MergeListener(row, column), new SplitListener(row,
		// column)));
	}

	// private class MergeListener implements ClickListener {
	//
	// private int row;
	//
	// private int col;
	//
	// MergeListener(int row, int col) {
	// this.row = row;
	// this.col = col;
	// }
	//
	// public void onClick(Widget w) {
	// mergeCell(row, col);
	// }
	//
	// public void setColumn(int col) {
	// this.col = col;
	//			
	// }
	//
	// public void setRow(int row) {
	// this.row = row;
	// }
	//
	// }
	//
	// private class SplitListener implements ClickListener {
	//
	// private int row;
	//
	// private int col;
	//
	// SplitListener(int row, int col) {
	// this.row = row;
	// this.col = col;
	// }
	//
	// public void onClick(Widget w) {
	// splitCell(row, col);
	// }
	//
	// public void setColumn(int col) {
	// this.col = col;
	//			
	// }
	//
	// public void setRow(int row) {
	// this.row = row;
	// }
	//
	// }

	private void addActions(final int startCol, final int currentRow,
			boolean newRow) {
		// the action magic
		addEditActions(startCol, currentRow, newRow);

		// addInsertAction(startCol, currentRow);
		// addDeleteAction(startCol, currentRow);
		// addShuffleUpAction(startCol, currentRow);
		// addShuffleDownAction(startCol, currentRow);
		// addMergeAction(currentRow);
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

	// private void addShuffleDownAction(final int numCols, final int
	// currentRow) {
	// final ShuffleAction shuffleDownAction;
	// if (currentRow == numRows()) {
	// shuffleDownAction = new ShuffleAction();
	// } else {
	// shuffleDownAction = new ShuffleDownAction(currentRow,
	// new RowClickListener() {
	// public void onClick(Widget w, int row) {
	// shuffleDown(row);
	// }
	// });
	// }
	// table.setWidget(currentRow, numCols + SHUFFLE_DOWN_COLUMN_OFFSET,
	// shuffleDownAction);
	// }

	// private void addShuffleUpAction(final int numCols, final int currentRow)
	// {
	// final ShuffleAction shuffleUpAction;
	// if (currentRow == START_DATA_ROW) {
	// shuffleUpAction = new ShuffleAction();
	// } else {
	//
	// shuffleUpAction = new ShuffleUpAction(currentRow,
	// new RowClickListener() {
	// public void onClick(Widget w, int row) {
	// shuffleUp(row);
	// }
	// });
	// }
	// table.setWidget(currentRow, numCols + SHUFFLE_UP_COLUMN_OFFSET,
	// shuffleUpAction);
	// }

	// private void addDeleteAction(final int numCols, final int currentRow) {
	// final DeleteAction deleteAction = new DeleteAction(currentRow,
	// new RowClickListener() {
	// public void onClick(Widget w, int row) {
	// deleteRow(row);
	// }
	// });
	// table.setWidget(currentRow, numCols + DELETE_COLUMN_OFFSET,
	// deleteAction);
	// }

	// private void addInsertAction(final int numCols, final int currentRow) {
	// final InsertAction insertAction = new InsertAction(currentRow,
	// new RowClickListener() {
	// public void onClick(Widget w, int row) {
	// insertRow(row);
	// }
	// });
	// table.setWidget(currentRow, numCols + INSERT_COLUMN_OFFSET,
	// insertAction);
	// }

	private void addEditActions(final int numCols, final int currentRow,
			boolean newRow) {
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
		table.setWidget(currentRow, numCols + EDIT_COLUMN_OFFSET, actions);
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
	 * 
	 * @param row
	 */
	void moveUp() {
		// create a new row above the given row
		// remember that insertRow will insert above the given row
		// so this is two above the original row
		int newRow = addNewRow(currentRow - 1);

		int lastCol = copyRow(currentRow + 1, newRow);
		addActions(lastCol, newRow, false);

		// remove the original row
		table.removeRow(currentRow + 1);

		// if the original row was the second row then we need to add a shuffle
		// up
		// widget to the new second row (originally the first row)
		// if (currentRow == START_DATA_ROW + 1) {
		// addShuffleUpAction(lastCol, currentRow);
		// }
		// if the original row was the last row we need to remove the shuffle
		// down
		// widget from the new last row
		// if (currentRow == numRows()) {
		// removeShuffleDown();
		// }
		// renumberRow(currentRow - 1);
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
	private int copyRow(int sourceRow, int targetRow) {
		// TODO handle editable rows
		int column = 1;
		int colIndex = 1;
		while (column <= numCols()) {
			newCell(targetRow, colIndex, table.getText(sourceRow, colIndex));
			cellFormatter.setStyleName(targetRow, colIndex, "dt-editor-Cell");
			int colSpan = cellFormatter.getColSpan(sourceRow, colIndex);
			column = column + colSpan;
			cellFormatter.setColSpan(targetRow, colIndex++, colSpan);
		}
		return colIndex;
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

		int lastCol = copyRow(currentRow, newRow);
		// remove row before adding action widgets so that the row number
		// is correct (otherwise the shuffle down button may not be displayed
		table.removeRow(currentRow);
		addActions(lastCol, newRow - 1, false);
		// if the original row was the first row we need to remove the shuffle
		// up
		// widget from the new first row
		// if (row == START_DATA_ROW) {
		// removeShuffleUp();
		// }
		// if the original row was the second last row then we need to add a
		// shuffle down
		// widget to the new second last row (originally the last row)
		// if (row == numRows() - 1) {
		// addShuffleDownAction(lastCol, row);
		// }
		System.err.println("currentRow: " + currentRow);
		renumberRow(currentRow);
		renumberRow(currentRow + 1);
		currentRow = -1;
		setCurrentCell(newRow - 1, currentCol);

	}

	// private void removeShuffleUp() {
	// table.setWidget(START_DATA_ROW, numCols() + SHUFFLE_UP_COLUMN_OFFSET,
	// new ShuffleAction());
	// }
	//
	// private void removeShuffleDown() {
	// table.setWidget(numRows(), numCols() + SHUFFLE_DOWN_COLUMN_OFFSET,
	// new ShuffleAction());
	// }

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
	 * 
	 * @param row
	 */
	private void renumberRow(int row) {
		table.setText(row, 0, Integer.toString(row));
		// for (int i = 1; i < numCols(); i++) {
		// DTCell cell = (DTCell) table.getWidget(row, i);
		// cell.setRow(row);
		// }
		// int lastColumn = numCols();
		// System.err.println("lastColumn: " + lastColumn);
		// System.err.println("row: " + row);
		// System.err.println("table.getHTML(row, lastColumn): "
		// + table.getHTML(row, lastColumn + EDIT_COLUMN_OFFSET));
		// EditActions editActions = (EditActions) table.getWidget(row,
		// lastColumn
		// + EDIT_COLUMN_OFFSET);
		// editActions.setRow(row);
		// InsertAction insertAction = (InsertAction) table.getWidget(row,
		// lastColumn + INSERT_COLUMN_OFFSET);
		// insertAction.setRow(row);
		// DeleteAction deleteAction = (DeleteAction) table.getWidget(row,
		// lastColumn + DELETE_COLUMN_OFFSET);
		// deleteAction.setRow(row);
		// ShuffleAction shuffleUpAction = (ShuffleAction) table.getWidget(row,
		// lastColumn + SHUFFLE_UP_COLUMN_OFFSET);
		// shuffleUpAction.setRow(row);
		// ShuffleAction shuffleDownAction = (ShuffleAction)
		// table.getWidget(row,
		// lastColumn + SHUFFLE_DOWN_COLUMN_OFFSET);
		// shuffleDownAction.setRow(row);
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
				table.setText(newRow, column, "new " + cellId++);
				cellFormatter.setStyleName(newRow, column, "dt-editor-Cell");
			}
			addActions(column, newRow, true);
			editRow(newRow);
			reorderRows(newRow);
		}
	}

	/**
	 * Apply the changes to the row.
	 */
	private void updateRow(int row) {
		for (int column = 1; column < numCols() + 1; column++) {
			TextBox text = (TextBox) table.getWidget(row, column);
			newCell(row, column, text.getText());
		}
	}

	/**
	 * This switches the given row into edit mode.
	 * 
	 * @param row
	 */
	private void editRow(int row) {
		for (int column = 1; column < numCols() + 1; column++) {
			editColumn(row, column);
		}
	}

	private void editColumn(int row, int column) {
		String text = table.getText(row, column);
		TextBox box = new TextBox();
		box.setText(text);
		box.setStyleName("dsl-field-TextBox");
		//box.setVisibleLength(3);
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
