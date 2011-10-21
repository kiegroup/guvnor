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
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import java.math.BigDecimal;
import java.util.Date;

import org.drools.guvnor.client.util.DateConverter;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public abstract class AbstractCellValueFactory<T> {

    // Dates are serialised to Strings with the user-defined format, or dd-MMM-yyyy by default
    protected static DateConverter       DATE_CONVERTOR = null;

    // SuggestionCompletionEngine to aid data-type resolution etc
    protected SuggestionCompletionEngine sce;

    public AbstractCellValueFactory(SuggestionCompletionEngine sce) {
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.sce = sce;
    }

    /**
     * Override the default, GWT-centric, Date conversion utility class. Only
     * use to hook-in a JVM Compatible implementation for tests
     * 
     * @param dc
     */
    public static void injectDateConvertor(DateConverter dc) {
        DATE_CONVERTOR = dc;
    }

    /**
     * Make an empty CellValue applicable for the column
     * 
     * @param column
     *            The model column
     * @param iRow
     *            Row coordinate for initialisation
     * @param iCol
     *            Column coordinate for initialisation
     * @return A CellValue
     */
    public CellValue< ? extends Comparable< ? >> makeCellValue(T column,
                                                               int iRow,
                                                               int iCol) {
        DTDataTypes52 dataType = getDataType( column );
        CellValue< ? extends Comparable< ? >> cell = null;

        switch ( dataType ) {
            case BOOLEAN :
                cell = makeNewBooleanCellValue( iRow,
                                                iCol );
                break;
            case DATE :
                cell = makeNewDateCellValue( iRow,
                                             iCol );
                break;
            case NUMERIC :
                cell = makeNewNumericCellValue( iRow,
                                                iCol );
                break;
            default :
                cell = makeNewStringCellValue( iRow,
                                               iCol );
        }

        return cell;
    }

    // Get the Data Type corresponding to a given column
    protected abstract DTDataTypes52 getDataType(T column);

    protected CellValue<Boolean> makeNewBooleanCellValue(int iRow,
                                                         int iCol) {
        CellValue<Boolean> cv = new CellValue<Boolean>( Boolean.FALSE,
                                                        iRow,
                                                        iCol );
        return cv;
    }

    protected CellValue<Boolean> makeNewBooleanCellValue(int iRow,
                                                         int iCol,
                                                         Boolean initialValue) {
        CellValue<Boolean> cv = makeNewBooleanCellValue( iRow,
                                                         iCol );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Date> makeNewDateCellValue(int iRow,
                                                   int iCol) {
        CellValue<Date> cv = new CellValue<Date>( null,
                                                  iRow,
                                                  iCol );
        return cv;
    }

    protected CellValue<Date> makeNewDateCellValue(int iRow,
                                                   int iCol,
                                                   Date initialValue) {
        CellValue<Date> cv = makeNewDateCellValue( iRow,
                                                   iCol );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<String> makeNewDialectCellValue(int iRow,
                                                        int iCol) {
        CellValue<String> cv = new CellValue<String>( "java",
                                                      iRow,
                                                      iCol );
        return cv;
    }

    protected CellValue<String> makeNewDialectCellValue(int iRow,
                                                        int iCol,
                                                        String initialValue) {
        CellValue<String> cv = makeNewDialectCellValue( iRow,
                                                        iCol );
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<BigDecimal> makeNewNumericCellValue(int iRow,
                                                            int iCol) {
        CellValue<BigDecimal> cv = new CellValue<BigDecimal>( null,
                                                              iRow,
                                                              iCol );
        return cv;
    }

    protected CellValue<BigDecimal> makeNewNumericCellValue(int iRow,
                                                            int iCol,
                                                            BigDecimal initialValue) {
        CellValue<BigDecimal> cv = makeNewNumericCellValue( iRow,
                                                            iCol );
        if ( initialValue != null ) {
            cv.setValue( (BigDecimal) initialValue );
        }
        return cv;
    }

    protected CellValue<String> makeNewStringCellValue(int iRow,
                                                       int iCol) {
        CellValue<String> cv = new CellValue<String>( null,
                                                      iRow,
                                                      iCol );
        return cv;
    }

    protected CellValue<String> makeNewStringCellValue(int iRow,
                                                       int iCol,
                                                       Object initialValue) {
        CellValue<String> cv = makeNewStringCellValue( iRow,
                                                       iCol );
        if ( initialValue != null && !initialValue.equals( "" ) ) {
            cv.setValue( initialValue.toString() );
        }
        return cv;
    }

}
