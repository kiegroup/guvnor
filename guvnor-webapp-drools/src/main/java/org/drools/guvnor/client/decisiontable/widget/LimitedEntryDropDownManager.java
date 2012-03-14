/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.decisiontable.widget;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.decisiontable.widget.LimitedEntryDropDownManager.Context;
import org.drools.guvnor.client.util.DateConverter;
import org.drools.guvnor.client.util.GWTDateConverter;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DropDownDataValueMapProvider;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.BaseColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

/**
 * Specific implementation for Limited Entry Decision Table definition (i.e. for
 * use with NewGuidedDecisionTableWizard or GuidedDecisionTableWidget)
 */
public class LimitedEntryDropDownManager
    implements
    DropDownDataValueMapProvider<Context> {

    private final SuggestionCompletionEngine sce;
    private final GuidedDecisionTable52      model;

    // Dates are serialised to Strings with the user-defined format, or dd-MMM-yyyy by default
    protected static DateConverter           DATE_CONVERTOR = GWTDateConverter.getInstance();

    public LimitedEntryDropDownManager(final GuidedDecisionTable52 model,
                                       final SuggestionCompletionEngine sce) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        this.model = model;
        this.sce = sce;
    }

    /**
     * The Context contains the Pattern and Column for which the enumerations
     * need to be ascertained. It is not possible to lookup the Pattern from the
     * Model as, when editing column definitions in GuidedDecisionTableWidget
     * the column is cloned (for editing) and hence is not the same
     * (identity-wise) as that held in the model
     */
    public static class Context {

        private final Pattern52  basePattern;

        private final BaseColumn baseColumn;

        public Context(final Pattern52 basePattern,
                       final ConditionCol52 baseColumn) {
            this.basePattern = basePattern;
            this.baseColumn = baseColumn;
        }

        public Context(final Pattern52 basePattern,
                       final ActionSetFieldCol52 baseColumn) {
            this.basePattern = basePattern;
            this.baseColumn = baseColumn;
        }

        public Context(final ActionInsertFactCol52 baseColumn) {
            this.basePattern = null;
            this.baseColumn = baseColumn;
        }

        public Pattern52 getBasePattern() {
            return basePattern;
        }

        public BaseColumn getBaseColumn() {
            return baseColumn;
        }

    }

    @Override
    public Map<String, String> getCurrentValueMap(Context context) {
        Map<String, String> currentValueMap = new HashMap<String, String>();

        final Pattern52 basePattern = context.getBasePattern();
        final BaseColumn baseColumn = context.getBaseColumn();

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if ( baseColumn instanceof ConditionCol52 ) {
            for ( ConditionCol52 cc : basePattern.getChildColumns() ) {
                if ( cc instanceof LimitedEntryCol ) {
                    currentValueMap.put( cc.getFactField(),
                                         getValue( (LimitedEntryCol) cc ) );
                }
            }

        } else if ( baseColumn instanceof ActionSetFieldCol52 ) {
            ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionSetFieldCol52 ) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if ( asf.getBoundName().equals( binding ) ) {
                        if ( asf instanceof LimitedEntryCol ) {
                            currentValueMap.put( asf.getFactField(),
                                                 getValue( (LimitedEntryCol) asf ) );
                        }
                    }
                }
            }

        } else if ( baseColumn instanceof ActionInsertFactCol52 ) {
            ActionInsertFactCol52 baseActionColumn = (ActionInsertFactCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionInsertFactCol52 ) {
                    final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                    if ( aif.getBoundName().equals( binding ) ) {
                        if ( aif instanceof LimitedEntryCol ) {
                            currentValueMap.put( aif.getFactField(),
                                                 getValue( (LimitedEntryCol) aif ) );
                        }
                    }
                }
            }

        }
        return currentValueMap;
    }

    private String getValue(LimitedEntryCol lec) {
        if ( lec == null || lec.getValue() == null ) {
            return "";
        }
        final DTCellValue52 lev = lec.getValue();
        switch ( lev.getDataType() ) {
            case BOOLEAN :
                return convertBooleanValueToString( lev );
            case DATE :
                return convertDateValueToString( lev );
            case NUMERIC :
                return convertNumericValueToString( lev );
            case NUMERIC_BIGDECIMAL :
                return convertBigDecimalValueToString( lev );
            case NUMERIC_BIGINTEGER :
                return convertBigIntegerValueToString( lev );
            case NUMERIC_BYTE :
                return convertByteValueToString( lev );
            case NUMERIC_DOUBLE :
                return convertDoubleValueToString( lev );
            case NUMERIC_FLOAT :
                return convertFloatValueToString( lev );
            case NUMERIC_INTEGER :
                return convertIntegerValueToString( lev );
            case NUMERIC_LONG :
                return convertLongValueToString( lev );
            case NUMERIC_SHORT :
                return convertShortValueToString( lev );
        }
        return convertStringValueToString( lev );
    }

    //Convert a Boolean value to a String
    private String convertBooleanValueToString(DTCellValue52 dcv) {
        final Boolean value = dcv.getBooleanValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Date value to a String
    private String convertDateValueToString(DTCellValue52 dcv) {
        final Date value = dcv.getDateValue();
        String result = "";
        if ( value != null ) {
            result = DATE_CONVERTOR.format( (Date) value );
        }
        return result;
    }

    //Convert a Generic Numeric (BigDecimal) value to a String
    private String convertNumericValueToString(DTCellValue52 dcv) {
        final BigDecimal value = (BigDecimal) dcv.getNumericValue();
        return (value == null ? "" : value.toPlainString());
    }

    //Convert a BigDecimal value to a String
    private String convertBigDecimalValueToString(DTCellValue52 dcv) {
        final BigDecimal value = (BigDecimal) dcv.getNumericValue();
        return (value == null ? "" : value.toPlainString());
    }

    //Convert a BigInteger value to a String
    private String convertBigIntegerValueToString(DTCellValue52 dcv) {
        final BigInteger value = (BigInteger) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Byte value to a String
    private String convertByteValueToString(DTCellValue52 dcv) {
        final Byte value = (Byte) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Double value to a String
    private String convertDoubleValueToString(DTCellValue52 dcv) {
        final Double value = (Double) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Float value to a String
    private String convertFloatValueToString(DTCellValue52 dcv) {
        final Float value = (Float) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Integer value to a String
    private String convertIntegerValueToString(DTCellValue52 dcv) {
        final Integer value = (Integer) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Long value to a String
    private String convertLongValueToString(DTCellValue52 dcv) {
        final Long value = (Long) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Short value to a String
    private String convertShortValueToString(DTCellValue52 dcv) {
        final Short value = (Short) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a String value to a String
    private String convertStringValueToString(DTCellValue52 dcv) {
        final String value = dcv.getStringValue();
        return (value == null ? "" : value);
    }

    @Override
    public Set<Integer> getDependentColumnIndexes(Context context) {

        final Set<Integer> dependentColumnIndexes = new HashSet<Integer>();

        final Pattern52 basePattern = context.getBasePattern();
        final BaseColumn baseColumn = context.getBaseColumn();

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if ( baseColumn instanceof ConditionCol52 ) {
            final ConditionCol52 baseConditionColumn = (ConditionCol52) baseColumn;
            for ( ConditionCol52 cc : basePattern.getChildColumns() ) {
                if ( sce.isDependentEnum( basePattern.getFactType(),
                                          baseConditionColumn.getFactField(),
                                          cc.getFactField() ) ) {
                    dependentColumnIndexes.add( model.getExpandedColumns().indexOf( cc ) );
                }
            }

        } else if ( baseColumn instanceof ActionSetFieldCol52 ) {
            final ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionSetFieldCol52 ) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if ( asf.getBoundName().equals( binding ) ) {
                        if ( sce.isDependentEnum( basePattern.getFactType(),
                                                  baseActionColumn.getFactField(),
                                                  asf.getFactField() ) ) {
                            dependentColumnIndexes.add( model.getExpandedColumns().indexOf( ac ) );
                        }
                    }
                }
            }

        } else if ( baseColumn instanceof ActionInsertFactCol52 ) {
            final ActionInsertFactCol52 baseActionColumn = (ActionInsertFactCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionInsertFactCol52 ) {
                    final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                    if ( aif.getBoundName().equals( binding ) ) {
                        if ( sce.isDependentEnum( baseActionColumn.getFactType(),
                                                  baseActionColumn.getFactField(),
                                                  aif.getFactField() ) ) {
                            dependentColumnIndexes.add( model.getExpandedColumns().indexOf( ac ) );
                        }
                    }
                }
            }

        }

        return dependentColumnIndexes;
    }

}
