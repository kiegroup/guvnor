/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

/**
 * A utility class to expand Condition column definitions into rows. Action
 * columns are not expanded, as the use-case is that a user-determined action
 * should be specified for each combination of Conditons. Where a column is
 * defined as having multiple values (Guvnor enum, Java enum or Decision Table
 * Value List) the number of rows is the Cartesian Product of all combinations.
 */
public class RowExpander {

    private Map<DTColumnConfig52, ColumnValues> expandedColumns = new IdentityHashMap<DTColumnConfig52, ColumnValues>();
    private List<ColumnValues>                  columns;
    private GuidedDecisionTable52               dtable;
    private SuggestionCompletionEngine          sce;

    private static final String[]               EMPTY_VALUES    = new String[0];

    /**
     * Constructor
     * 
     * @param dtable
     * @param sce
     */
    RowExpander(GuidedDecisionTable52 dtable,
                SuggestionCompletionEngine sce) {
        this.columns = new ArrayList<ColumnValues>();
        this.dtable = dtable;
        this.sce = sce;
        addRowNumberColumn();
        addRowDescriptionColumn();
        addConditionColumns();
        addActionColumns();
        addAnalysisColumn();
    }

    List<ColumnValues> getColumns() {
        return this.columns;
    }

    private void addRowNumberColumn() {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUES,
                                            null );
        cv.setExpandColumn( false );
        this.expandedColumns.put( dtable.getRowNumberCol(),
                                  cv );
        this.columns.add( cv );
    }

    private void addRowDescriptionColumn() {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUES,
                                            null );
        cv.setExpandColumn( false );
        this.expandedColumns.put( dtable.getDescriptionCol(),
                                  cv );
        this.columns.add( cv );
    }

    private void addConditionColumns() {
        for ( Pattern52 p : dtable.getConditionPatterns() ) {
            addColumn( p );
        }
    }

    private void addActionColumns() {
        for ( ActionCol52 a : dtable.getActionCols() ) {
            if ( a instanceof ActionSetFieldCol52 ) {
                ActionSetFieldCol52 afc = (ActionSetFieldCol52) a;
                addColumn( afc );
            } else if ( a instanceof ActionInsertFactCol52 ) {
                ActionInsertFactCol52 aif = (ActionInsertFactCol52) a;
                addColumn( aif );
            }
        }
    }

    private void addAnalysisColumn() {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUES,
                                            null );
        cv.setExpandColumn( false );
        this.expandedColumns.put( dtable.getAnalysisCol(),
                                  cv );
        this.columns.add( cv );
    }

    private void addColumn(Pattern52 p) {
        for ( ConditionCol52 c : p.getConditions() ) {
            addColumn( c );
        }
    }

    private void addColumn(ConditionCol52 c) {
        String[] values = new String[]{};
        switch ( dtable.getTableFormat() ) {
            case EXTENDED_ENTRY :
                values = dtable.getValueList( c,
                                              sce );
                values = getValues( values );
                break;
            case LIMITED_ENTRY :
                values = new String[]{"true", "false"};
        }
        ColumnValues cv = new ColumnValues( columns,
                                            values,
                                            c.getDefaultValue() );
        this.expandedColumns.put( c,
                                  cv );
        this.columns.add( cv );
    }

    private String[] getValues(String[] values) {
        String[] splitValues = new String[values.length];
        for ( int i = 0; i < values.length; i++ ) {
            String v = values[i];
            String[] splut = ConstraintValueEditorHelper.splitValue( v );
            splitValues[i] = splut[0];
        }
        return splitValues;
    }

    private void addColumn(ActionSetFieldCol52 a) {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUES,
                                            a.getDefaultValue() );
        cv.setExpandColumn( false );
        this.expandedColumns.put( a,
                                  cv );
        this.columns.add( cv );
    }

    private void addColumn(ActionInsertFactCol52 a) {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUES,
                                            a.getDefaultValue() );
        cv.setExpandColumn( false );
        this.expandedColumns.put( a,
                                  cv );
        this.columns.add( cv );
    }

    /**
     * Rather than return a List of rows as the expanded form we expose an
     * Iterator with which the expanded form can be retrieved. This decision was
     * to avoid potentially hugh transient Lists being created; as the results
     * from this class will be transformed into other representations.
     * 
     * @return
     */
    RowIterator iterator() {
        return new RowIterator();
    }

    /**
     * Indicate whether the provided column should be expanded or not. If the
     * column was not part of the Decision Table used in the Constructor no
     * action is taken.
     * 
     * @param column
     * @param expand
     */
    void setExpandColumn(DTColumnConfig52 column,
                         boolean expandColumn) {
        ColumnValues cv = this.expandedColumns.get( column );
        if ( cv == null ) {
            return;
        }
        cv.setExpandColumn( expandColumn );
    }

    /**
     * An iterator that retrieves the expanded rows one at a time
     */
    class RowIterator
        implements
        Iterator<List<String>> {

        //Check if all columns have had their value lists consumed
        public boolean hasNext() {
            for ( ColumnValues cv : columns ) {
                if ( !cv.isAllValuesUsed() ) {
                    return true;
                }
            }
            return false;
        }

        //Build a row from the columns current values and advance the first column. Columns 
        //check whether all their values have been used and advance the subsequent column
        //so a ripple effect can be observed, with one column advancing the next, which
        //advances the next and so on...
        public List<String> next() {
            List<String> row = new ArrayList<String>();
            for ( ColumnValues cv : columns ) {
                row.add( cv.getCurrentValue() );
            }
            columns.get( 0 ).advanceColumnValue();
            return row;
        }

        public void remove() {
            throw new UnsupportedOperationException( "remove is not supported on RowIterator" );
        }

    }

    /**
     * Container for a columns values
     */
    static class ColumnValues {

        List<String>       values;
        List<String>       originalValues;
        List<ColumnValues> columns;
        String             value;
        String             defaultValue;
        Iterator<String>   iterator;
        boolean            expandColumn    = true;
        boolean            isAllValuesUsed = false;

        ColumnValues(List<ColumnValues> columns,
                     String[] values,
                     String defaultValue) {
            this.columns = columns;
            this.defaultValue = defaultValue;
            this.values = Arrays.asList( values );
            this.originalValues = this.values;

            //If no values were provided add the default and record that all values have been used
            if ( this.values.size() == 0 ) {
                this.values = new ArrayList<String>();
                this.values.add( defaultValue );
                this.isAllValuesUsed = true;
            }

            //Initialise value to the first in the list
            this.iterator = this.values.iterator();
            advanceColumnValue();
        }

        void setExpandColumn(boolean expandColumn) {
            if ( expandColumn ) {
                this.values = this.originalValues;
                this.isAllValuesUsed = false;
            } else {
                this.values = new ArrayList<String>();
                this.values.add( defaultValue );
                this.isAllValuesUsed = true;
            }
            this.iterator = this.values.iterator();
            advanceColumnValue();
        }

        /**
         * Get the current value of the column
         * 
         * @return
         */
        String getCurrentValue() {
            return this.value;
        }

        /**
         * Advance to the next value for the column, resetting to the beginning
         * of the list if all values have been used. The reset operation also
         * advances the next columns value.
         * 
         * @return
         */
        void advanceColumnValue() {
            if ( !this.expandColumn ) {
                this.isAllValuesUsed = true;
                this.value = this.defaultValue;
                return;
            }

            if ( iterator.hasNext() ) {
                value = iterator.next();
            } else {
                isAllValuesUsed = true;
                this.iterator = values.iterator();
                value = iterator.next();
                int myIndex = columns.indexOf( this );
                if ( myIndex < columns.size() - 1 ) {
                    columns.get( myIndex + 1 ).advanceColumnValue();
                }
            }
        }

        /**
         * Have all values in the columns list been used
         * 
         * @return
         */
        boolean isAllValuesUsed() {
            return this.isAllValuesUsed;
        }

    }
}
