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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.decisiontable.widget.LimitedEntryDropDownManager.Context;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.BaseColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
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

    private Map<BaseColumn, ColumnValues>    expandedColumns = new IdentityHashMap<BaseColumn, ColumnValues>();
    private List<ColumnValues>               columns;

    private final GuidedDecisionTable52      dtable;
    private final SuggestionCompletionEngine sce;

    private static final List<DTCellValue52> EMPTY_VALUE     = new ArrayList<DTCellValue52>();
    {
        EMPTY_VALUE.add( new DTCellValue52() );
    }

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

        //Add all columns to Expander to generate row data. The AnalysisCol is not added 
        //as its data is transient, not held in the underlying Decision Table's data
        addRowNumberColumn();
        addRowDescriptionColumn();
        addConditionColumns();
        addActionColumns();
    }

    List<ColumnValues> getColumns() {
        return this.columns;
    }

    private void addRowNumberColumn() {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUE,
                                            new DTCellValue52() );
        cv.setExpandColumn( false );
        this.expandedColumns.put( dtable.getRowNumberCol(),
                                  cv );
        this.columns.add( cv );
    }

    private void addRowDescriptionColumn() {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUE,
                                            new DTCellValue52() );
        cv.setExpandColumn( false );
        this.expandedColumns.put( dtable.getDescriptionCol(),
                                  cv );
        this.columns.add( cv );
    }

    private void addConditionColumns() {
        for ( Pattern52 p : dtable.getPatterns() ) {
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

    private void addColumn(Pattern52 p) {
        for ( ConditionCol52 c : p.getChildColumns() ) {
            addColumn( p,
                       c );
        }
    }

    private void addColumn(Pattern52 p,
                           ConditionCol52 c) {
        switch ( dtable.getTableFormat() ) {
            case EXTENDED_ENTRY :
                addExtendedEntryColumn( p,
                                        c );
                break;
            case LIMITED_ENTRY :
                addLimitedEntryColumn( c );
                break;
        }
    }

    private void addExtendedEntryColumn(Pattern52 p,
                                        ConditionCol52 c) {
        ColumnValues cv = null;
        String[] values = new String[]{};
        if ( dtable.hasValueList( c ) ) {
            values = dtable.getValueList( c,
                                          sce );
            values = getSplitValues( values );
            cv = new ColumnValues( columns,
                                   convertValueList( values ),
                                   c.getDefaultValue() );

        } else if ( sce.hasEnums( p.getFactType(),
                                  c.getFactField() ) ) {
            final Context context = new Context( p,
                                                 c );
            cv = new ColumnDynamicValues( columns,
                                          sce,
                                          context,
                                          c.getDefaultValue() );

        } else {
            cv = new ColumnValues( columns,
                                   convertValueList( values ),
                                   c.getDefaultValue() );
        }

        if ( cv != null ) {
            this.expandedColumns.put( c,
                                      cv );
            this.columns.add( cv );
        }
    }

    private void addLimitedEntryColumn(ConditionCol52 c) {
        List<DTCellValue52> values = new ArrayList<DTCellValue52>();
        values.add( new DTCellValue52( Boolean.TRUE ) );
        values.add( new DTCellValue52( Boolean.FALSE ) );

        ColumnValues cv = new ColumnValues( columns,
                                            values,
                                            c.getDefaultValue() );
        this.expandedColumns.put( c,
                                  cv );
        this.columns.add( cv );
    }

    private String[] getSplitValues(String[] values) {
        String[] splitValues = new String[values.length];
        for ( int i = 0; i < values.length; i++ ) {
            String v = values[i];
            String[] splut = ConstraintValueEditorHelper.splitValue( v );
            splitValues[i] = splut[0];
        }
        return splitValues;
    }

    private static List<DTCellValue52> convertValueList(String[] values) {
        List<DTCellValue52> convertedValues = new ArrayList<DTCellValue52>();
        for ( String value : values ) {
            convertedValues.add( new DTCellValue52( value ) );
        }
        return convertedValues;
    }

    private void addColumn(ActionSetFieldCol52 a) {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUE,
                                            dtable.getTableFormat() == TableFormat.EXTENDED_ENTRY ? a.getDefaultValue() : new DTCellValue52( Boolean.FALSE ) );
        cv.setExpandColumn( false );
        this.expandedColumns.put( a,
                                  cv );
        this.columns.add( cv );
    }

    private void addColumn(ActionInsertFactCol52 a) {
        ColumnValues cv = new ColumnValues( columns,
                                            EMPTY_VALUE,
                                            dtable.getTableFormat() == TableFormat.EXTENDED_ENTRY ? a.getDefaultValue() : new DTCellValue52( Boolean.FALSE ) );
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
    void setExpandColumn(BaseColumn column,
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
        Iterator<List<DTCellValue52>> {

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
        public List<DTCellValue52> next() {

            //We have a row that is potentially partially populated as the dependent enum data has not been set
            //So ask columns to update their value lists based on the current row definition. This will force
            //the dependent enumeration value lists to be populated.
            boolean refreshRow = false;
            List<DTCellValue52> row;
            do {
                refreshRow = false;
                row = new ArrayList<DTCellValue52>();
                for ( ColumnValues cv : columns ) {
                    row.add( cv.getCurrentValue() );
                }
                for ( ColumnValues cv : columns ) {
                    if ( cv instanceof ColumnDynamicValues ) {
                        final ColumnDynamicValues cdv = (ColumnDynamicValues) cv;
                        refreshRow = refreshRow || cdv.assertValueList( row );
                    }
                }
            } while ( refreshRow );

            //Advance the first column to the next value
            columns.get( columns.size() - 1 ).advanceColumnValue();
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

        List<DTCellValue52>     values;
        List<DTCellValue52>     originalValues;
        List<ColumnValues>      columns;
        DTCellValue52           value;
        DTCellValue52           defaultValue;
        Iterator<DTCellValue52> iterator;
        boolean                 expandColumn    = true;
        boolean                 isAllValuesUsed = false;

        ColumnValues(List<ColumnValues> columns,
                     List<DTCellValue52> values,
                     DTCellValue52 defaultValue) {
            this.columns = columns;
            this.defaultValue = defaultValue;
            this.values = values;
            this.originalValues = this.values;

            //If no values were provided add the default and record that all values have been used
            if ( this.values.size() == 0 ) {
                this.values = new ArrayList<DTCellValue52>();
                this.values.add( defaultValue );
                this.originalValues = this.values;
                this.isAllValuesUsed = true;
            }

            //Initialise value to the first in the list
            this.iterator = this.values.iterator();
            this.value = iterator.next();
        }

        void setExpandColumn(boolean expandColumn) {
            this.expandColumn = expandColumn;
            if ( expandColumn ) {
                this.values = this.originalValues;
                this.isAllValuesUsed = false;
            } else {
                this.values = new ArrayList<DTCellValue52>();
                this.values.add( defaultValue );
                this.isAllValuesUsed = true;
            }
            //Initialise value to the first in the list
            this.iterator = this.values.iterator();
            this.value = iterator.next();
        }

        /**
         * Get the current value of the column
         * 
         * @return
         */
        DTCellValue52 getCurrentValue() {
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
            if ( iterator.hasNext() ) {
                value = iterator.next();
            } else {
                isAllValuesUsed = true;
                this.iterator = this.values.iterator();
                this.value = iterator.next();
                int myIndex = columns.indexOf( this );
                if ( myIndex > 0 ) {
                    columns.get( myIndex - 1 ).advanceColumnValue();
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

    /**
     * Container for a columns values that are dynamically generated
     */
    static class ColumnDynamicValues extends ColumnValues {

        private final Context                    context;
        private final SuggestionCompletionEngine sce;
        private boolean                          initialiseValueList = true;

        ColumnDynamicValues(List<ColumnValues> columns,
                            SuggestionCompletionEngine sce,
                            Context context,
                            DTCellValue52 defaultValue) {
            super( columns,
                   EMPTY_VALUE,
                   defaultValue );
            this.sce = sce;
            this.context = context;

            //Check if there is an enumeration
            final DropDownData dd = sce.getEnums( context.getBasePattern().getFactType(),
                                                  ((ConditionCol52) context.getBaseColumn()).getFactField(),
                                                  new HashMap<String, String>() );
            if ( dd != null ) {
                this.values = convertValueList( getSplitValues( dd.fixedList ) );
                this.originalValues = this.values;
                this.initialiseValueList = false;
                this.isAllValuesUsed = false;
            }

            //Initialise value to the first in the list
            this.iterator = this.values.iterator();
            this.value = iterator.next();
        }

        private String[] getSplitValues(String[] values) {
            String[] splitValues = new String[values.length];
            for ( int i = 0; i < values.length; i++ ) {
                String v = values[i];
                String[] splut = ConstraintValueEditorHelper.splitValue( v );
                splitValues[i] = splut[0];
            }
            return splitValues;
        }

        /**
         * Assert that the Value List is correct for data contained in the row
         * 
         * @param row
         * @returns true if the Value List has changed and the row should be
         *          refreshed
         */
        boolean assertValueList(List<DTCellValue52> row) {
            if ( !this.expandColumn ) {
                return false;
            }
            final boolean refreshRow = this.initialiseValueList;
            if ( refreshRow ) {
                Map<String, String> currentValueMap = new HashMap<String, String>();
                for ( int iCol = 0; iCol < this.columns.size(); iCol++ ) {
                    ColumnValues cv = this.columns.get( iCol );
                    if ( cv instanceof ColumnDynamicValues ) {
                        final ColumnDynamicValues cdv = (ColumnDynamicValues) cv;
                        if ( cdv.context.getBasePattern().equals( this.context.getBasePattern() ) ) {
                            final ConditionCol52 cc = (ConditionCol52) cdv.context.getBaseColumn();
                            final DTCellValue52 value = row.get( iCol );
                            currentValueMap.put( cc.getFactField(),
                                                 value.getStringValue() );
                        }
                    }
                }
                this.initialiseValueList = false;
                final DropDownData dd = sce.getEnums( context.getBasePattern().getFactType(),
                                                      ((ConditionCol52) context.getBaseColumn()).getFactField(),
                                                      currentValueMap );
                if ( dd != null ) {
                    this.values = convertValueList( getSplitValues( dd.fixedList ) );
                    this.originalValues = this.values;
                    this.isAllValuesUsed = false;
                } else {
                    this.values = new ArrayList<DTCellValue52>();
                    this.values.add( defaultValue );
                    this.originalValues = this.values;
                    this.isAllValuesUsed = true;
                }

                //Initialise value to the first in the list
                this.iterator = this.values.iterator();
                this.value = iterator.next();
            }
            return refreshRow;
        }

        /**
         * Advance to the next value for the column, resetting to the beginning
         * of the list if all values have been used. The reset operation also
         * advances the next columns value.
         * 
         * @return
         */
        void advanceColumnValue() {
            if ( iterator.hasNext() ) {
                value = iterator.next();
            } else {
                isAllValuesUsed = true;
                this.initialiseValueList = true;
                this.iterator = values.iterator();
                value = iterator.next();
                int myIndex = columns.indexOf( this );
                if ( myIndex > 0 ) {
                    columns.get( myIndex - 1 ).advanceColumnValue();
                }
            }
        }

    }

}
