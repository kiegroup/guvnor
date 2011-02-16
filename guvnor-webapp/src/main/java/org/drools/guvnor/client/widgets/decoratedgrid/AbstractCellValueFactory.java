/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.widgets.decoratedgrid;

import java.util.Date;


import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public abstract class AbstractCellValueFactory<T> {

    // Recognised data-types
    public enum DATA_TYPES {
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

            @Override
            public String serialiseValue(CellValue< ? > value) {
                return (value.getValue() == null ? null : (String) value.getValue());
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

            @Override
            public String serialiseValue(CellValue< ? > value) {
                return (value.getValue() == null ? null : ((Integer) value.getValue()).toString());
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

            @Override
            public String serialiseValue(CellValue< ? > value) {
                return (value.getValue() == null ? null : ((Integer) value.getValue()).toString());
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
                    Date d;
                    try {
                        d = DATE_FORMAT.parse( initialValue );
                    } catch ( IllegalArgumentException iae ) {
                        Date nd = new Date();
                        int year = nd.getYear();
                        int month = nd.getMonth();
                        int date = nd.getDate();
                        d = new Date( year,
                                        month,
                                        date );
                    }
                    cv.setValue( d );
                }
                return cv;
            }

            @Override
            public String serialiseValue(CellValue< ? > value) {
                String result = null;
                if ( value.getValue() != null ) {
                    result = DATE_FORMAT.format( (Date) value.getValue() );
                }
                return result;
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

            @Override
            public String serialiseValue(CellValue< ? > value) {
                return (value.getValue() == null ? null : ((Boolean) value.getValue()).toString());
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

            @Override
            public String serialiseValue(CellValue< ? > value) {
                return (value.getValue() == null ? null : (String) value.getValue());
            }

        };
        public abstract CellValue< ? > getNewCellValue(int iRow,
                                                       int iCol,
                                                       String initialValue);

        public abstract String serialiseValue(CellValue< ? > value);

    }

    // Dates are serialised and de-serialised to locale-independent format
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat( "dd-MMM-yyyy" );

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
    public CellValue< ? extends Comparable< ? >> getCellValue(
                                                              T column,
                                                              int iRow,
                                                              int iCol,
                                                              String initialValue) {
        DATA_TYPES dataType = getDataType( column );
        CellValue< ? extends Comparable< ? >> cell = dataType.getNewCellValue(
                                                                               iRow,
                                                                               iCol,
                                                                               initialValue );
        return cell;
    }

    /**
     * Serialise value to a String
     * 
     * @param column
     *            The model column
     * @param cv
     *            CellValue for which value will be serialised
     * @return String representation of value
     */
    public String serialiseValue(T column,
                                 CellValue< ? > cv) {
        DATA_TYPES dataType = getDataType( column );
        return dataType.serialiseValue( cv );

    }

    // Get the Data Type corresponding to a given column
    protected abstract DATA_TYPES getDataType(T column);

}
