package org.drools.guvnor.client.decisiontable.cells;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.decisiontable.widget.DecisionTableWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.DescriptionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
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

	// Row number
	private static DecisionTableCellValueAdaptor<Integer> ROW_NUMBER_CELL = new DecisionTableCellValueAdaptor<Integer>(
			new RowNumberCell());

	// Text editor
	private static DecisionTableCellValueAdaptor<String> TEXT_CELL = new DecisionTableCellValueAdaptor<String>(
			new PopupTextEditCell());

	// Numeric editor
	private static DecisionTableCellValueAdaptor<Integer> NUMERIC_CELL = new DecisionTableCellValueAdaptor<Integer>(
			new PopupNumericEditCell());

	// Date editor (DatePickerCell)
	private static DecisionTableCellValueAdaptor<Date> DATE_CELL = new DecisionTableCellValueAdaptor<Date>(
			new PopupDateEditCell(
					DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));

	// Boolean editor
	private static DecisionTableCellValueAdaptor<Boolean> BOOLEAN_CELL = new DecisionTableCellValueAdaptor<Boolean>(
			new CheckboxCell());

	// Setup the cache. GWT's Cells are wrapped with an adaptor which
	// casts the value of the CellValue to the type required for the GWT Cell
	{

		// Dialect editor
		List<String> dialectOptions = new ArrayList<String>();
		dialectOptions.add("java");
		dialectOptions.add("mvel");
		SelectionCell sc = new SelectionCell(dialectOptions);
		DecisionTableCellValueAdaptor<String> DIALECT_CELL = new DecisionTableCellValueAdaptor<String>(
				sc);

		cellCache.put(RowNumberCol.class.getName(), ROW_NUMBER_CELL);
		cellCache.put(DescriptionCol.class.getName(), TEXT_CELL);
		cellCache.put(MetadataCol.class.getName(), TEXT_CELL);
		cellCache.put(AttributeCol.class.getName(), TEXT_CELL);
		cellCache.put(AttributeCol.class.getName() + "#salience#true",
				ROW_NUMBER_CELL);
		cellCache.put(AttributeCol.class.getName() + "#salience#false",
				NUMERIC_CELL);
		cellCache.put(AttributeCol.class.getName() + "#enabled", BOOLEAN_CELL);
		cellCache.put(AttributeCol.class.getName() + "#no-loop", BOOLEAN_CELL);
		cellCache.put(AttributeCol.class.getName() + "#duration", NUMERIC_CELL);
		cellCache.put(AttributeCol.class.getName() + "#auto-focus",
				BOOLEAN_CELL);
		cellCache.put(AttributeCol.class.getName() + "#lock-on-active",
				BOOLEAN_CELL);
		cellCache.put(AttributeCol.class.getName() + "#date-effective",
				DATE_CELL);
		cellCache
				.put(AttributeCol.class.getName() + "#date-expires", DATE_CELL);
		cellCache.put(AttributeCol.class.getName() + "#dialect", DIALECT_CELL);
		cellCache.put(ActionCol.class.getName(), TEXT_CELL);
	}

	// Default cell should a specific one not be configured
	private static final DecisionTableCellValueAdaptor<String> DEFAULT_CELL = new DecisionTableCellValueAdaptor<String>(
			new PopupTextEditCell());

	// The cache
	private static Map<String, DecisionTableCellValueAdaptor<? extends Comparable<?>>> cellCache = new HashMap<String, DecisionTableCellValueAdaptor<? extends Comparable<?>>>();

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
	 * Lookup a Cell for the given DTColumnConfig. Cells are cached at different
	 * levels of precedence; key[0] contains the most specific through to key[2]
	 * which contains the most generic. Should no match be found the default is
	 * provided
	 * 
	 * @param column
	 *            The Decision Table model column
	 * @param dtable
	 *            The Decision Table
	 * @return A Cell
	 */
	public DecisionTableCellValueAdaptor<? extends Comparable<?>> getCell(
			DTColumnConfig column, DecisionTableWidget dtable) {

		String[] keys = new String[3];
		DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = DEFAULT_CELL;

		if (column instanceof RowNumberCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = RowNumberCol.class.getName();
			cell = lookupCell(keys);

		} else if (column instanceof DescriptionCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = DescriptionCol.class.getName();
			cell = lookupCell(keys);

		} else if (column instanceof MetadataCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = MetadataCol.class.getName();
			cell = lookupCell(keys);

		} else if (column instanceof AttributeCol) {
			AttributeCol attrCol = (AttributeCol) column;
			keys[2] = AttributeCol.class.getName();
			keys[1] = keys[2] + "#" + attrCol.attr;
			keys[0] = keys[1] + "#" + attrCol.isUseRowNumber();
			cell = lookupCell(keys);

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

	// Try the keys to find a renderer in the cache
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> lookupCell(
			String[] keys) {
		DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = null;
		for (String key : keys) {
			if (key != null) {
				if (cellCache.containsKey(key)) {
					cell = cellCache.get(key);
					break;
				}
			}
		}
		return cell;

	}

	// Make a new Cell cache entry for the DTColumnConfig
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeNewCell(
			DTColumnConfig col, DecisionTableWidget dtable) {

		GuidedDecisionTable model = dtable.getModel();
		SuggestionCompletionEngine sce = dtable.getSCE();
		DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = DEFAULT_CELL;

		// Columns with lists of values, enums etc are always Text (for now)
		String[] vals = model.getValueList(col, sce);
		if (vals.length == 0) {
			if (model.isNumeric(col, sce)) {
				cell = NUMERIC_CELL;
			} else if (model.isBoolean(col, sce)) {
				cell = BOOLEAN_CELL;
			} else if (model.isDate(col, sce)) {
				cell = DATE_CELL;
			}
		} else {
			PopupDropDownEditCell pudd = new PopupDropDownEditCell();
			pudd.setItems(vals);
			cell = new DecisionTableCellValueAdaptor<String>(pudd);
		}
		return cell;
	}

}