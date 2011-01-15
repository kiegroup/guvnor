package org.drools.guvnor.client.decisiontable.cells;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.guvnor.client.decisiontable.widget.DecisionTableWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * A Factory to provide the Cell specific to a given coordinate.
 * 
 * @author manstis
 * 
 */
public class CellFactory {

	// The Singleton
	private static CellFactory instance;

	// Singleton constructor
	public static synchronized CellFactory getInstance() {
		if (instance == null) {
			instance = new CellFactory();
		}
		return instance;
	}

	private CellFactory() {
	}

	/**
	 * Create a Cell for the given DTColumnConfig
	 * 
	 * @param column
	 *            The Decision Table model column
	 * @param dtable
	 *            The Decision Table
	 * @return A Cell
	 */
	public DecisionTableCellValueAdaptor<? extends Comparable<?>> getCell(
			DTColumnConfig column, DecisionTableWidget dtable) {

		DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = makeTextCell();

		if (column instanceof RowNumberCol) {
			cell = makeRowNumberCell();

		} else if (column instanceof AttributeCol) {
			AttributeCol attrCol = (AttributeCol) column;
			String attrName = attrCol.attr;
			if (attrName.equals("salience")) {
				if (attrCol.isUseRowNumber()) {
					cell = makeRowNumberCell();
				} else {
					cell = makeNumericCell();
				}
			} else if (attrName.equals("enabled")) {
				cell = makeBooleanCell();
			} else if (attrName.equals("no-loop")) {
				cell = makeBooleanCell();
			} else if (attrName.equals("duration")) {
				cell = makeNumericCell();
			} else if (attrName.equals("auto-focus")) {
				cell = makeBooleanCell();
			} else if (attrName.equals("lock-on-active")) {
				cell = makeBooleanCell();
			} else if (attrName.equals("date-effective")) {
				cell = makeDateCell();
			} else if (attrName.equals("date-expires")) {
				cell = makeDateCell();
			} else if (attrName.equals("dialect")) {
				cell = makeDialectCell();
			}

		} else if (column instanceof ConditionCol) {
			cell = makeNewCell(column, dtable);

		} else if (column instanceof ActionSetFieldCol) {
			cell = makeNewCell(column, dtable);

		} else if (column instanceof ActionInsertFactCol) {
			cell = makeNewCell(column, dtable);

		}

		cell.setDecisionTableWidget(dtable);
		return cell;

	}

	// Make a new Cell for Boolean columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeBooleanCell() {
		return new DecisionTableCellValueAdaptor<Boolean>(new CheckboxCell());
	}

	// Make a new Cell for Date columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeDateCell() {
		return new DecisionTableCellValueAdaptor<Date>(new PopupDateEditCell(
				DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));
	}

	// Make a new Cell for Dialect columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeDialectCell() {
		List<String> dialectOptions = new ArrayList<String>();
		dialectOptions.add("java");
		dialectOptions.add("mvel");
		SelectionCell sc = new SelectionCell(dialectOptions);
		return new DecisionTableCellValueAdaptor<String>(sc);
	}

	// Make a new Cell for Condition and Actions columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeNewCell(
			DTColumnConfig col, DecisionTableWidget dtable) {

		GuidedDecisionTable model = dtable.getModel();
		SuggestionCompletionEngine sce = dtable.getSCE();
		DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = makeTextCell();

		// Columns with lists of values, enums etc are always Text (for now)
		String[] vals = model.getValueList(col, sce);
		if (vals.length == 0) {
			if (model.isNumeric(col, sce)) {
				cell = makeNumericCell();
			} else if (model.isBoolean(col, sce)) {
				cell = makeBooleanCell();
			} else if (model.isDate(col, sce)) {
				cell = makeDateCell();
			}
		} else {
			PopupDropDownEditCell pudd = new PopupDropDownEditCell();
			pudd.setItems(vals);
			cell = new DecisionTableCellValueAdaptor<String>(pudd);
		}
		return cell;
	}

	// Make a new Cell for Numeric columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeNumericCell() {
		return new DecisionTableCellValueAdaptor<Integer>(
				new PopupNumericEditCell());
	}

	// Make a new Cell for String columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeRowNumberCell() {
		return new DecisionTableCellValueAdaptor<Integer>(new RowNumberCell());
	}

	// Make a new Cell for a RowNumberCol
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeTextCell() {
		return new DecisionTableCellValueAdaptor<String>(
				new PopupTextEditCell());
	}

}