package org.drools.guvnor.client.decisiontable.cells;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.decisiontable.widget.CellValue;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.DescriptionCol;
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
		datatypeCache.put(AttributeCol.class.getName() + "#salience",
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
		datatypeCache.put(ConditionCol.class.getName(), DATA_TYPES.STRING);
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
	 * @return A CellValue
	 */
	public CellValue<? extends Comparable<?>> getCellValue(
			DTColumnConfig column, int iRow, int iCol, String initialValue) {
		DATA_TYPES dataType = getDataType(column);
		CellValue<? extends Comparable<?>> cell = dataType.getNewCellValue(
				iRow, iCol, initialValue);
		return cell;
	}

	// DataTypes are cached at different levels of precedence; key[0]
	// contains the most specific through to key[2] which contains
	// the most generic. Should no match be found the default is provided
	private DATA_TYPES getDataType(DTColumnConfig column) {

		String[] keys = new String[3];

		if (column instanceof RowNumberCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = RowNumberCol.class.getName();
		} else if (column instanceof DescriptionCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = DescriptionCol.class.getName();
		} else if (column instanceof MetadataCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = MetadataCol.class.getName();
		} else if (column instanceof AttributeCol) {
			keys[2] = null;
			keys[1] = AttributeCol.class.getName();
			keys[0] = keys[1] + "#" + ((AttributeCol) column).attr;
		} else if (column instanceof ConditionCol) {
			keys[2] = ConditionCol.class.getName();
			keys[1] = keys[2] + "#" + ((ConditionCol) column).getFactType();
			keys[0] = keys[1] + "#" + ((ConditionCol) column).getFactField();
		} else if (column instanceof ActionCol) {
			keys[2] = null;
			keys[1] = null;
			keys[0] = ActionCol.class.getName();
		}

		return lookupDataType(keys);

	}

	// Try the keys to find a data-type in the cache
	private DATA_TYPES lookupDataType(String[] keys) {
		DATA_TYPES dataType = DATA_TYPES.STRING;
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

}
