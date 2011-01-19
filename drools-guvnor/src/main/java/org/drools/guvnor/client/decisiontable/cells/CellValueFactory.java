package org.drools.guvnor.client.decisiontable.cells;

import java.util.Date;

import org.drools.guvnor.client.decisiontable.widget.CellValue;
import org.drools.guvnor.client.decisiontable.widget.DecisionTableWidget;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
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
            public CellValue<String> getNewCellValue(int iRow,
                                                     int iCol,
                                                     String initialValue) {
                CellValue<String> cv = new CellValue<String>( initialValue,
                                                              iRow,
                                                              iCol );
                return cv;
            }

        },
        NUMERIC() {
            @Override
            public CellValue<Integer> getNewCellValue(int iRow,
                                                      int iCol,
                                                      String initialValue) {
                CellValue<Integer> cv = new CellValue<Integer>( null,
                                                                iRow,
                                                                iCol );
                if ( initialValue != null ) {
                    try {
                        cv.setValue( Integer.valueOf( initialValue ) );
                    } catch ( Exception e ) {
                    }
                }
                return cv;
            }

        },
        ROW_NUMBER() {
            @Override
            public CellValue<Integer> getNewCellValue(int iRow,
                                                      int iCol,
                                                      String initialValue) {
                // Rows are 0-based internally but 1-based in the UI
                CellValue<Integer> cv = new CellValue<Integer>( iRow + 1,
                                                                iRow,
                                                                iCol );
                return cv;
            }

        },
        DATE() {
            @Override
            @SuppressWarnings("deprecation")
            public CellValue<Date> getNewCellValue(int iRow,
                                                   int iCol,
                                                   String initialValue) {
                CellValue<Date> cv = new CellValue<Date>( null,
                                                          iRow,
                                                          iCol );

                if ( initialValue != null ) {
                    // TODO Need to parse String into Date
                    Date d = new Date();
                    int year = d.getYear();
                    int month = d.getMonth();
                    int date = d.getDate();
                    Date nd = new Date( year,
                                        month,
                                        date );
                    cv.setValue( nd );
                }
                return cv;
            }

        },
        BOOLEAN() {
            @Override
            public CellValue<Boolean> getNewCellValue(int iRow,
                                                      int iCol,
                                                      String initialValue) {
                CellValue<Boolean> cv = new CellValue<Boolean>( Boolean.FALSE,
                                                                iRow,
                                                                iCol );
                if ( initialValue != null ) {
                    try {
                        cv.setValue( Boolean.valueOf( initialValue ) );
                    } catch ( Exception e ) {
                    }
                }
                return cv;
            }

        },
        DIALECT() {
            @Override
            public CellValue<String> getNewCellValue(int iRow,
                                                     int iCol,
                                                     String initialValue) {
                CellValue<String> cv = new CellValue<String>( "java",
                                                              iRow,
                                                              iCol );
                if ( initialValue != null ) {
                    cv.setValue( initialValue );
                }
                return cv;
            }

        };
        public abstract CellValue< ? > getNewCellValue(int iRow,
                                                       int iCol,
                                                       String initialValue);

    }

    // The Singleton
    private static CellValueFactory instance;

    // Singleton constructor
    public static synchronized CellValueFactory getInstance() {
        if ( instance == null ) {
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
    public CellValue< ? extends Comparable< ? >> getCellValue(
                                                              DTColumnConfig column,
                                                              int iRow,
                                                              int iCol,
                                                              String initialValue,
                                                              DecisionTableWidget dtable) {
        DATA_TYPES dataType = getDataType( column,
                                           dtable );
        CellValue< ? extends Comparable< ? >> cell = dataType.getNewCellValue(
                                                                               iRow,
                                                                               iCol,
                                                                               initialValue );
        return cell;
    }

    // Get the Data Type corresponding to a given column
    private DATA_TYPES getDataType(DTColumnConfig column,
                                   DecisionTableWidget dtable) {

        DATA_TYPES dataType = DATA_TYPES.STRING;

        if ( column instanceof RowNumberCol ) {
            dataType = DATA_TYPES.ROW_NUMBER;

        } else if ( column instanceof AttributeCol ) {
            AttributeCol attrCol = (AttributeCol) column;
            String attrName = attrCol.attr;
            if ( attrName.equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                if ( attrCol.isUseRowNumber() ) {
                    dataType = DATA_TYPES.ROW_NUMBER;
                } else {
                    dataType = DATA_TYPES.NUMERIC;
                }
            } else if ( attrName.equals( RuleAttributeWidget.ENABLED_ATTR ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( attrName.equals( RuleAttributeWidget.NO_LOOP_ATTR ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( attrName.equals( RuleAttributeWidget.DURATION_ATTR ) ) {
                dataType = DATA_TYPES.NUMERIC;
            } else if ( attrName.equals( RuleAttributeWidget.AUTO_FOCUS_ATTR ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( attrName.equals( RuleAttributeWidget.LOCK_ON_ACTIVE_ATTR ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( attrName.equals( RuleAttributeWidget.DATE_EFFECTIVE_ATTR ) ) {
                dataType = DATA_TYPES.DATE;
            } else if ( attrName.equals( RuleAttributeWidget.DATE_EXPIRES_ATTR ) ) {
                dataType = DATA_TYPES.DATE;
            } else if ( attrName.equals( RuleAttributeWidget.DIALECT_ATTR ) ) {
                dataType = DATA_TYPES.DIALECT;
            }

        } else if ( column instanceof ConditionCol ) {
            dataType = makeNewCellDataType( column,
                                            dtable );

        } else if ( column instanceof ActionSetFieldCol ) {
            dataType = makeNewCellDataType( column,
                                            dtable );

        } else if ( column instanceof ActionInsertFactCol ) {
            dataType = makeNewCellDataType( column,
                                            dtable );

        }

        return dataType;

    }

    // Derive the Data Type for a Condition or Action column
    private DATA_TYPES makeNewCellDataType(DTColumnConfig col,
                                           DecisionTableWidget dtable) {

        GuidedDecisionTable model = dtable.getModel();
        SuggestionCompletionEngine sce = dtable.getSCE();
        DATA_TYPES dataType = DATA_TYPES.STRING;

        // Columns with lists of values, enums etc are always Text (for now)
        String[] vals = model.getValueList( col,
                                            sce );
        if ( vals.length == 0 ) {
            if ( model.isNumeric( col,
                                  sce ) ) {
                dataType = DATA_TYPES.NUMERIC;
            } else if ( model.isBoolean( col,
                                         sce ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( model.isDate( col,
                                      sce ) ) {
                dataType = DATA_TYPES.DATE;
            }
        }
        return dataType;
    }

}
