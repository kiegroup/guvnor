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
import java.util.List;

import org.drools.guvnor.client.util.DateConverter;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicDataRow;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public abstract class AbstractCellValueFactory<C, V> {

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
     * Construct a new row of data for the underlying model
     * 
     * @return
     */
    public abstract List<V> makeRowData();

    /**
     * Construct a new row of data for the MergableGridWidget
     * 
     * @return
     */
    public abstract DynamicDataRow makeUIRowData();

    /**
     * Construct a new column of data for the underlying model
     * 
     * @param column
     * @return
     */
    public abstract List<V> makeColumnData(C column);

    /**
     * Convert a column of domain data to that suitable for the UI
     * 
     * @param column
     * @param columnData
     * @return
     */
    public abstract List<CellValue< ? extends Comparable< ? >>> convertColumnData(C column,
                                                                                  List<V> columnData);

    /**
     * Make a Model cell for the given column
     * 
     * @param column
     * @return
     */
    protected abstract V makeModelCellValue(C column);

    /**
     * Convert a Model cell to one that can be used in the UI
     * 
     * @param cell
     * @return
     */
    protected abstract CellValue< ? extends Comparable< ? >> convertModelCellValue(C column,
                                                                                   V cell);

    /**
     * Get the data-type for a column
     * 
     * @param column
     * @return
     */
    protected abstract DTDataTypes52 getDataType(C column);

    protected CellValue<Boolean> makeNewBooleanCellValue() {
        CellValue<Boolean> cv = new CellValue<Boolean>( Boolean.FALSE );
        return cv;
    }

    protected CellValue<Boolean> makeNewBooleanCellValue(Boolean initialValue) {
        CellValue<Boolean> cv = makeNewBooleanCellValue();
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<Date> makeNewDateCellValue() {
        CellValue<Date> cv = new CellValue<Date>( null );
        return cv;
    }

    protected CellValue<Date> makeNewDateCellValue(Date initialValue) {
        CellValue<Date> cv = makeNewDateCellValue();
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<String> makeNewDialectCellValue() {
        CellValue<String> cv = new CellValue<String>( "java" );
        return cv;
    }

    protected CellValue<String> makeNewDialectCellValue(String initialValue) {
        CellValue<String> cv = makeNewDialectCellValue();
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<BigDecimal> makeNewNumericCellValue() {
        CellValue<BigDecimal> cv = new CellValue<BigDecimal>( null );
        return cv;
    }

    protected CellValue<BigDecimal> makeNewNumericCellValue(BigDecimal initialValue) {
        CellValue<BigDecimal> cv = makeNewNumericCellValue();
        if ( initialValue != null ) {
            cv.setValue( initialValue );
        }
        return cv;
    }

    protected CellValue<String> makeNewStringCellValue() {
        CellValue<String> cv = new CellValue<String>( null );
        return cv;
    }

    protected CellValue<String> makeNewStringCellValue(Object initialValue) {
        CellValue<String> cv = makeNewStringCellValue();
        if ( initialValue != null && !initialValue.equals( "" ) ) {
            cv.setValue( initialValue.toString() );
        }
        return cv;
    }

}
