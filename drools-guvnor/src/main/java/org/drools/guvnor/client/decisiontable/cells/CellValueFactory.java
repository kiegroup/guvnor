package org.drools.guvnor.client.decisiontable.cells;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.decisiontable.widget.CellValue;
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

/**
 * A Factory to create CellValues applicable to given columns.
 * 
 * @author manstis
 * 
 */
public class CellValueFactory {

	// Recognised data-types
	private enum DATA_TYPES {
		STRING() {
			@Override
			public CellValue<String> getNewCellValue(int iRow, int iCol,
					String initialValue) {
				CellValue<String> cv = new CellValue<String>(initialValue,
						iRow, iCol);
				return cv;
			}

		},
		NUMERIC() {
			@Override
			public CellValue<Integer> getNewCellValue(int iRow, int iCol,
					String initialValue) {
				CellValue<Integer> cv = new CellValue<Integer>(null, iRow, iCol);
				if (initialValue != null) {
					cv.setValue(Integer.valueOf(initialValue));
				}
				return cv;
			}

		},
		ROW_NUMBER() {
			@Override
			public CellValue<Integer> getNewCellValue(int iRow, int iCol,
					String initialValue) {
				// Rows are 0-based internally but 1-based in the UI
				CellValue<Integer> cv = new CellValue<Integer>(iRow + 1, iRow,
						iCol);
				return cv;
			}

		},
		DATE() {
			@Override
			@SuppressWarnings("deprecation")
			public CellValue<Date> getNewCellValue(int iRow, int iCol,
					String initialValue) {
				CellValue<Date> cv = new CellValue<Date>(null, iRow, iCol);

				if (initialValue != null) {
					// TODO Need to parse String into Date
					Date d = new Date();
					int year = d.getYear();
					int month = d.getMonth();
					int date = d.getDate();
					Date nd = new Date(year, month, date);
					cv.setValue(nd);
				}
				return cv;
			}

		},
		BOOLEAN() {
			@Override
			public CellValue<Boolean> getNewCellValue(int iRow, int iCol,
					String initialValue) {
				CellValue<Boolean> cv = new CellValue<Boolean>(Boolean.FALSE,
						iRow, iCol);
				if (initialValue != null) {
					cv.setValue(Boolean.valueOf(initialValue));
				}
				return cv;
			}

		},
		DIALECT() {
			@Override
			public CellValue<String> getNewCellValue(int iRow, int iCol,
					String initialValue) {
				CellValue<String> cv = new CellValue<String>("java", iRow, iCol);
				if (initialValue != null) {
					cv.setValue(initialValue);
				}
				return cv;
			}

		};
		public abstract CellValue<?> getNewCellValue(int iRow, int iCol,
				String initialValue);
	}

	// Setup the cache
	{
		datatypeCache.put(RowNumberCol.class.getName(), DATA_TYPES.ROW_NUMBER);
		datatypeCache.put(DescriptionCol.class.getName(), DATA_TYPES.STRING);
		datatypeCache.put(MetadataCol.class.getName(), DATA_TYPES.STRING);
		datatypeCache.put(AttributeCol.class.getName(), DATA_TYPES.STRING);
		datatypeCache.put(AttributeCol.class.getName() + "#salience#true",
				DATA_TYPES.NUMERIC);
		datatypeCache.put(AttributeCol.class.getName() + "#salience#false",
				DATA_TYPES.NUMERIC);
		datatypeCache.put(AttributeCol.class.getName() + "#enabled",
				DATA_TYPES.BOOLEAN);
		datatypeCache.put(AttributeCol.class.getName() + "#no-loop",
				DATA_TYPES.BOOLEAN);
		datatypeCache.put(AttributeCol.class.getName() + "#duration",
				DATA_TYPES.NUMERIC);
		datatypeCache.put(AttributeCol.class.getName() + "#auto-focus",
				DATA_TYPES.BOOLEAN);
		datatypeCache.put(AttributeCol.class.getName() + "#lock-on-active",
				DATA_TYPES.BOOLEAN);
		datatypeCache.put(AttributeCol.class.getName() + "#date-effective",
				DATA_TYPES.DATE);
		datatypeCache.put(AttributeCol.class.getName() + "#date-expires",
				DATA_TYPES.DATE);
		datatypeCache.put(AttributeCol.class.getName() + "#dialect",
				DATA_TYPES.DIALECT);
		datatypeCache.put(ActionCol.class.getName(), DATA_TYPES.STRING);
	}

	// The cache
	private static Map<String, DATA_TYPES> datatypeCache = new HashMap<String, DATA_TYPES>();

	// The Singleton
	private static CellValueFactory instance;

	// Singleton constructor
	public static synchronized CellValueFactory getInstance() {
		if (instance == null) {
			instance = new CellValueFactory();
		}
		return instance;
	}

	private CellValueFactory() {
	}

	/**
	 * Make a CellValue applicable for the column
	 * 
	 * @param column
	 *            The model column
	 * @param iRow
	 *            Row coordinate for initialisation
	 * @param iCol
	 *            Column coordinate for initialisation
	 * @param initialValue
	 *            The initial value of the cell
	 * @param dtable
	 *            The Decision Table
	 * @return A CellValue
	 */
	public CellValue<? extends Comparable<?>> getCellValue(
			DTColumnConfig column, int iRow, int iCol, String initialValue,
			DecisionTableWidget dtable) {
		DATA_TYPES dataType = getDataType(column, dtable);
		CellValue<? extends Comparable<?>> cell = dataType.getNewCellValue(
				iRow, iCol, initialValue);
		return cell;
	}

	// DataTypes are cached at different levels of precedence; key[0]
	// contains the most specific through to key[2] which contains
	// the most generic. Should no match be found the default is provided
	private DATA_TYPES getDataType(DTColumnConfig column,
			DecisionTableWidget dtable) {

		String[] keys = new String[3];
		DATA_TYPES dataType = DATA_TYPES.STRING;

		if (column instanceof RowNumberCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = RowNumberCol.class.getName();
			dataType = lookupDataType(keys);

		} else if (column instanceof DescriptionCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = DescriptionCol.class.getName();
			dataType = lookupDataType(keys);

		} else if (column instanceof MetadataCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = MetadataCol.class.getName();
			dataType = lookupDataType(keys);

		} else if (column instanceof AttributeCol) {
			AttributeCol attrCol = (AttributeCol) column;
			keys[2] = AttributeCol.class.getName();
			keys[1] = keys[2] + "#" + attrCol.attr;
			keys[0] = keys[1] + "#" + attrCol.isUseRowNumber();
			dataType = lookupDataType(keys);

		} else if (column instanceof ConditionCol) {
			dataType = makeNewCellDataType(column, dtable);

		} else if (column instanceof ActionSetFieldCol) {
			dataType = makeNewCellDataType(column, dtable);

		} else if (column instanceof ActionInsertFactCol) {
			dataType = makeNewCellDataType(column, dtable);

		}

		return dataType;

	}

	// Try the keys to find a data-type in the cache
	private DATA_TYPES lookupDataType(String[] keys) {
		DATA_TYPES dataType = null;
		for (String key : keys) {
			if (key != null) {
				if (datatypeCache.containsKey(key)) {
					dataType = datatypeCache.get(key);
					break;
				}
			}
		}
		return dataType;

	}

	// Make a new Data Type cache entry for the DTColumnConfig
	private DATA_TYPES makeNewCellDataType(DTColumnConfig col,
			DecisionTableWidget dtable) {

		GuidedDecisionTable model = dtable.getModel();
		SuggestionCompletionEngine sce = dtable.getSCE();
		DATA_TYPES dataType = DATA_TYPES.STRING;
		if (model.isNumeric(col, sce)) {
			dataType = DATA_TYPES.NUMERIC;
		} else if (model.isBoolean(col, sce)) {
			dataType = DATA_TYPES.BOOLEAN;
		} else if (model.isDate(col, sce)) {
			dataType = DATA_TYPES.DATE;
		}
		return dataType;
	}

}
