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

			addActions(column, currentRow, false);
		}
	}

	private void addActions(int column, final int currentRow, boolean newRow) {
		// the action magic
		final EditActions actions = new EditActions(currentRow, new RowClickListener() {
			public void onClick(Widget w, int row) {
				editRow(row);
			}
		}, new RowClickListener() {
			public void onClick(Widget w, int row) {
				updateRow(row);
			}
		});
		if (newRow) actions.makeEditable();
		table.setWidget(currentRow, column++, actions);

		final InsertAction insertAction = new InsertAction(currentRow, new RowClickListener() {
			public void onClick(Widget w, int row) {
				insertRow(row);
			}
		});
		table.setWidget(currentRow, column, insertAction);
	}
	
	/**
	 * Listener for click events that affect a whole row
	 */
	interface RowClickListener {
		void onClick(Widget w, int row);
	}
	
	//counter for dummy new row data 
	private int cellId;
	
	/**
	 * Add a new row after the specified row
	 * @param row the row to insert after
	 */
	private void insertRow(int row) {
		int newRow = table.insertRow(row + 1);
		table.setText(newRow, 0, Integer.toString(newRow));
		cellFormatter.setStyleName(newRow, 0, "dt-editor-CountColumn");
		int column = 1;
		for (; column < numCols() + 1; column++) {
			table.setText(newRow, column, "new " + cellId++);
			cellFormatter.setStyleName(newRow, column, "dt-editor-Cell");
		}
		addActions(column, newRow, true);
		editRow(newRow);
		for (int r = newRow + 1; r  < table.getRowCount(); r++) {
			table.setText(r, 0, Integer.toString(r));
			EditActions editActions = (EditActions) table.getWidget(r, column);
			editActions.setRow(r);
			InsertAction insertAction = (InsertAction) table.getWidget(r, column + 1);
			insertAction.setRow(r);
		}
			
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
		return 14;
	}

}
