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
package org.drools.guvnor.client.decisiontable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.AbstractRestrictedEntryTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericBigDecimalTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericBigIntegerTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericByteTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericDoubleTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericFloatTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericIntegerTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericLongTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericShortTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericTextBox;
import org.drools.guvnor.client.common.PopupDatePicker;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.decisiontable.widget.LimitedEntryDropDownManager;
import org.drools.guvnor.client.decisiontable.widget.LimitedEntryDropDownManager.Context;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.BaseColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Factory for Widgets to edit DTCellValues
 */
public class DTCellValueWidgetFactory {

    private final SuggestionCompletionEngine  sce;
    private final GuidedDecisionTable52       dtable;
    private final LimitedEntryDropDownManager dropDownManager;
    private final boolean                     isReadOnly;

    private static final String               DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat       format      = DateTimeFormat.getFormat( DATE_FORMAT );

    public DTCellValueWidgetFactory(GuidedDecisionTable52 dtable,
                                    SuggestionCompletionEngine sce,
                                    boolean isReadOnly) {
        this.sce = sce;
        this.dtable = dtable;
        this.dropDownManager = new LimitedEntryDropDownManager( dtable,
                                                                sce );
        this.isReadOnly = isReadOnly;
    }

    /**
     * Make a DTCellValue for a column
     * 
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue(DTColumnConfig52 c) {
        DTDataTypes52 type = dtable.getTypeSafeType( c,
                                                     sce );
        return new DTCellValue52( type );
    }

    /**
     * Make a DTCellValue for a column. This overloaded method takes a Pattern52
     * object as well since the pattern may be different to that to which the
     * column has been bound in the Decision Table model, i.e. when adding or
     * editing a column
     * 
     * @param p
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue(Pattern52 p,
                                      ConditionCol52 c) {
        DTDataTypes52 type = dtable.getTypeSafeType( p,
                                                     c,
                                                     sce );
        return new DTCellValue52( type );
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List"). This overloaded method takes a
     * Pattern52 object as well since the pattern may be different to that to
     * which the column has been bound in the Decision Table model, i.e. when
     * adding or editing a column
     * 
     * @param pattern
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget(Pattern52 pattern,
                            ConditionCol52 column,
                            DTCellValue52 value) {
        return getWidget( pattern,
                          column,
                          value,
                          false );
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List"). This overloaded method takes a
     * Pattern52 object as well since the pattern may be different to that to
     * which the column has been bound in the Decision Table model, i.e. when
     * adding or editing a column
     * 
     * @param pattern
     * @param column
     * @param value
     * @param allowEmptyValue
     * @return
     */
    public Widget getWidget(Pattern52 pattern,
                            ConditionCol52 column,
                            DTCellValue52 value,
                            boolean allowEmptyValue) {

        if ( sce.hasEnums( pattern.getFactType(),
                           column.getFactField() ) ) {
            final Context context = new Context( pattern,
                                                 column );
            final Map<String, String> currentValueMap = dropDownManager.getCurrentValueMap( context );
            final DropDownData dd = sce.getEnums( pattern.getFactType(),
                                                  column.getFactField(),
                                                  currentValueMap );
            if ( dd == null ) {
                return makeListBox( new String[0],
                                    pattern,
                                    column,
                                    value,
                                    false );
            }
            return makeListBox( dd.fixedList,
                                pattern,
                                column,
                                value,
                                allowEmptyValue );
        }

        DTDataTypes52 type = dtable.getTypeSafeType( pattern,
                                                     column,
                                                     sce );
        switch ( type ) {
            case NUMERIC :
                return makeNumericTextBox( value,
                                           allowEmptyValue );
            case NUMERIC_BIGDECIMAL :
                return makeNumericBigDecimalTextBox( value,
                                                     allowEmptyValue );
            case NUMERIC_BIGINTEGER :
                return makeNumericBigIntegerTextBox( value,
                                                     allowEmptyValue );
            case NUMERIC_BYTE :
                return makeNumericByteTextBox( value,
                                               allowEmptyValue );
            case NUMERIC_DOUBLE :
                return makeNumericDoubleTextBox( value,
                                                 allowEmptyValue );
            case NUMERIC_FLOAT :
                return makeNumericFloatTextBox( value,
                                                allowEmptyValue );
            case NUMERIC_INTEGER :
                return makeNumericIntegerTextBox( value,
                                                  allowEmptyValue );
            case NUMERIC_LONG :
                return makeNumericLongTextBox( value,
                                               allowEmptyValue );
            case NUMERIC_SHORT :
                return makeNumericShortTextBox( value,
                                                allowEmptyValue );
            case BOOLEAN :
                return makeBooleanSelector( value,
                                            allowEmptyValue );
            case DATE :
                return makeDateSelector( value,
                                         allowEmptyValue );
            default :
                return makeTextBox( value );
        }
    }

    /**
     * Make a DTCellValue for a column. This overloaded method takes a Pattern52
     * object as well since the ActionSetFieldCol52 column may be associated
     * with an unbound Pattern
     * 
     * @param p
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue(Pattern52 p,
                                      ActionSetFieldCol52 c) {
        DTDataTypes52 type = dtable.getTypeSafeType( p,
                                                     c,
                                                     sce );
        return new DTCellValue52( type );
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List"). This overloaded method takes a
     * Pattern52 object as well since the ActionSetFieldCol52 column may be
     * associated with an unbound Pattern
     * 
     * @param pattern
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget(Pattern52 pattern,
                            ActionSetFieldCol52 column,
                            DTCellValue52 value) {
        return getWidget( pattern,
                          column,
                          value,
                          false );
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List"). This overloaded method takes a
     * Pattern52 object as well since the ActionSetFieldCol52 column may be
     * associated with an unbound Pattern
     * 
     * @param pattern
     * @param column
     * @param value
     * @param allowEmptyValue
     * @return
     */
    public Widget getWidget(Pattern52 pattern,
                            ActionSetFieldCol52 column,
                            DTCellValue52 value,
                            boolean allowEmptyValue) {

        if ( sce.hasEnums( pattern.getFactType(),
                           column.getFactField() ) ) {
            final Context context = new Context( pattern,
                                                 column );
            final Map<String, String> currentValueMap = dropDownManager.getCurrentValueMap( context );
            final DropDownData dd = sce.getEnums( pattern.getFactType(),
                                                  column.getFactField(),
                                                  currentValueMap );
            if ( dd == null ) {
                return makeListBox( new String[0],
                                    pattern,
                                    column,
                                    value,
                                    false );
            }
            return makeListBox( dd.fixedList,
                                pattern,
                                column,
                                value,
                                allowEmptyValue );
        }

        DTDataTypes52 type = dtable.getTypeSafeType( pattern,
                                                     column,
                                                     sce );
        switch ( type ) {
            case NUMERIC :
                return makeNumericTextBox( value,
                                           allowEmptyValue );
            case NUMERIC_BIGDECIMAL :
                return makeNumericBigDecimalTextBox( value,
                                                     allowEmptyValue );
            case NUMERIC_BIGINTEGER :
                return makeNumericBigIntegerTextBox( value,
                                                     allowEmptyValue );
            case NUMERIC_BYTE :
                return makeNumericByteTextBox( value,
                                               allowEmptyValue );
            case NUMERIC_DOUBLE :
                return makeNumericDoubleTextBox( value,
                                                 allowEmptyValue );
            case NUMERIC_FLOAT :
                return makeNumericFloatTextBox( value,
                                                allowEmptyValue );
            case NUMERIC_INTEGER :
                return makeNumericIntegerTextBox( value,
                                                  allowEmptyValue );
            case NUMERIC_LONG :
                return makeNumericLongTextBox( value,
                                               allowEmptyValue );
            case NUMERIC_SHORT :
                return makeNumericShortTextBox( value,
                                                allowEmptyValue );
            case BOOLEAN :
                return makeBooleanSelector( value,
                                            allowEmptyValue );
            case DATE :
                return makeDateSelector( value,
                                         allowEmptyValue );
            default :
                return makeTextBox( value );
        }
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List").
     * 
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget(ActionInsertFactCol52 column,
                            DTCellValue52 value) {
        return getWidget( column,
                          value,
                          false );
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List").
     * 
     * @param column
     * @param value
     * @param allowEmptyValue
     * @return
     */
    public Widget getWidget(ActionInsertFactCol52 column,
                            DTCellValue52 value,
                            boolean allowEmptyValue) {
        if ( sce.hasEnums( column.getFactType(),
                           column.getFactField() ) ) {
            final Context context = new Context( column );
            final Map<String, String> currentValueMap = dropDownManager.getCurrentValueMap( context );
            final DropDownData dd = sce.getEnums( column.getFactType(),
                                                  column.getFactField(),
                                                  currentValueMap );
            if ( dd == null ) {
                return makeListBox( new String[0],
                                    column,
                                    value,
                                    false );
            }
            return makeListBox( dd.fixedList,
                                column,
                                value,
                                allowEmptyValue );
        }

        DTDataTypes52 type = dtable.getTypeSafeType( column,
                                                     sce );
        switch ( type ) {
            case NUMERIC :
                return makeNumericTextBox( value,
                                           allowEmptyValue );
            case NUMERIC_BIGDECIMAL :
                return makeNumericBigDecimalTextBox( value,
                                                     allowEmptyValue );
            case NUMERIC_BIGINTEGER :
                return makeNumericBigIntegerTextBox( value,
                                                     allowEmptyValue );
            case NUMERIC_BYTE :
                return makeNumericByteTextBox( value,
                                               allowEmptyValue );
            case NUMERIC_DOUBLE :
                return makeNumericDoubleTextBox( value,
                                                 allowEmptyValue );
            case NUMERIC_FLOAT :
                return makeNumericFloatTextBox( value,
                                                allowEmptyValue );
            case NUMERIC_INTEGER :
                return makeNumericIntegerTextBox( value,
                                                  allowEmptyValue );
            case NUMERIC_LONG :
                return makeNumericLongTextBox( value,
                                               allowEmptyValue );
            case NUMERIC_SHORT :
                return makeNumericShortTextBox( value,
                                                allowEmptyValue );
            case BOOLEAN :
                return makeBooleanSelector( value,
                                            allowEmptyValue );
            case DATE :
                return makeDateSelector( value,
                                         allowEmptyValue );
            default :
                return makeTextBox( value );
        }
    }

    private ListBox makeBooleanSelector(final DTCellValue52 value,
                                        final boolean allowEmptyValue) {
        final ListBox lb = new ListBox();
        int indexTrue = 0;
        int indexFalse = 1;

        if ( allowEmptyValue ) {
            indexTrue = 1;
            indexFalse = 2;
            lb.addItem( Constants.INSTANCE.Choose(),
                        "" );
        }

        lb.addItem( "true" );
        lb.addItem( "false" );
        Boolean currentItem = value.getBooleanValue();
        if ( currentItem == null ) {
            lb.setSelectedIndex( 0 );
        } else {
            lb.setSelectedIndex( currentItem ? indexTrue : indexFalse );

        }

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    final String txtValue = lb.getValue( lb.getSelectedIndex() );
                    Boolean boolValue = (txtValue.equals( "" ) ? null : txtValue.equals( "true" ));
                    value.setBooleanValue( boolValue );
                }

            } );
        }
        return lb;
    }

    private ListBox makeListBox(final String[] completions,
                                final Pattern52 basePattern,
                                final ConditionCol52 baseCondition,
                                final DTCellValue52 value,
                                final boolean allowEmptyValue) {
        final ListBox lb = makeListBox( completions,
                                        value,
                                        allowEmptyValue );

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    int index = lb.getSelectedIndex();
                    if ( index > -1 ) {
                        //Set base column value
                        value.setStringValue( lb.getValue( index ) );

                        //Update any dependent enumerations
                        final Context context = new Context( basePattern,
                                                             baseCondition );
                        Set<Integer> dependentColumnIndexes = dropDownManager.getDependentColumnIndexes( context );
                        for ( Integer iCol : dependentColumnIndexes ) {
                            BaseColumn column = dtable.getExpandedColumns().get( iCol );
                            if ( column instanceof LimitedEntryCol ) {
                                ((LimitedEntryCol) column).setValue( null );
                            }
                        }
                    } else {
                        value.setStringValue( null );
                    }
                }

            } );
        }
        return lb;
    }

    private ListBox makeListBox(final String[] completions,
                                final Pattern52 basePattern,
                                final ActionSetFieldCol52 baseAction,
                                final DTCellValue52 value,
                                final boolean allowEmptyValue) {
        final ListBox lb = makeListBox( completions,
                                        value,
                                        allowEmptyValue );

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    int index = lb.getSelectedIndex();
                    if ( index > -1 ) {
                        //Set base column value
                        value.setStringValue( lb.getValue( index ) );

                        //Update any dependent enumerations
                        final Context context = new Context( basePattern,
                                                             baseAction );
                        Set<Integer> dependentColumnIndexes = dropDownManager.getDependentColumnIndexes( context );
                        for ( Integer iCol : dependentColumnIndexes ) {
                            BaseColumn column = dtable.getExpandedColumns().get( iCol );
                            if ( column instanceof LimitedEntryCol ) {
                                ((LimitedEntryCol) column).setValue( null );
                            }
                        }
                    } else {
                        value.setStringValue( null );
                    }
                }

            } );
        }
        return lb;
    }

    private ListBox makeListBox(final String[] completions,
                                final ActionInsertFactCol52 baseAction,
                                final DTCellValue52 value,
                                final boolean allowEmptyValue) {
        final ListBox lb = makeListBox( completions,
                                        value,
                                        allowEmptyValue );

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    int index = lb.getSelectedIndex();
                    if ( index > -1 ) {
                        //Set base column value
                        value.setStringValue( lb.getValue( index ) );

                        //Update any dependent enumerations
                        final Context context = new Context( baseAction );
                        Set<Integer> dependentColumnIndexes = dropDownManager.getDependentColumnIndexes( context );
                        for ( Integer iCol : dependentColumnIndexes ) {
                            BaseColumn column = dtable.getExpandedColumns().get( iCol );
                            if ( column instanceof LimitedEntryCol ) {
                                ((LimitedEntryCol) column).setValue( null );
                            }
                        }
                    } else {
                        value.setStringValue( null );
                    }
                }

            } );
        }
        return lb;
    }

    private ListBox makeListBox(final String[] completions,
                                final DTCellValue52 value,
                                final boolean allowEmptyValue) {
        int selectedIndex = -1;
        final ListBox lb = new ListBox();

        if ( allowEmptyValue ) {
            lb.addItem( Constants.INSTANCE.Choose(),
                        "" );
        }

        String currentItem = value.getStringValue();
        int selectedIndexOffset = (allowEmptyValue ? 1 : 0);
        for ( int i = 0; i < completions.length; i++ ) {
            String item = completions[i].trim();
            String[] splut = ConstraintValueEditorHelper.splitValue( item );
            lb.addItem( splut[1],
                        splut[0] );
            if ( splut[0].equals( currentItem ) ) {
                lb.setSelectedIndex( i + selectedIndexOffset );
                selectedIndex = i + selectedIndexOffset;
            }
        }

        //If nothing has been selected, select the first value
        if ( selectedIndex == -1 ) {
            if ( lb.getItemCount() > 0 ) {
                lb.setSelectedIndex( 0 );
                value.setStringValue( lb.getValue( 0 ) );
            } else {
                value.setStringValue( null );
            }
        }

        return lb;
    }

    private AbstractRestrictedEntryTextBox makeNumericTextBox(final DTCellValue52 value,
                                                              final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericTextBox( allowEmptyValue );
        final BigDecimal numericValue = (BigDecimal) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toPlainString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new BigDecimal( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (BigDecimal) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( BigDecimal.ZERO );
                            tb.setValue( BigDecimal.ZERO.toPlainString() );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericBigDecimalTextBox(final DTCellValue52 value,
                                                                        final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericBigDecimalTextBox( allowEmptyValue );
        final BigDecimal numericValue = (BigDecimal) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toPlainString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new BigDecimal( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (BigDecimal) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( BigDecimal.ZERO );
                            tb.setValue( BigDecimal.ZERO.toPlainString() );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericBigIntegerTextBox(final DTCellValue52 value,
                                                                        final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericBigIntegerTextBox( allowEmptyValue );
        final BigInteger numericValue = (BigInteger) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new BigInteger( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (BigInteger) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( BigInteger.ZERO );
                            tb.setValue( BigInteger.ZERO.toString() );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericByteTextBox(final DTCellValue52 value,
                                                                  final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericByteTextBox( allowEmptyValue );
        final Byte numericValue = (Byte) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new Byte( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (Byte) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Byte( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericDoubleTextBox(final DTCellValue52 value,
                                                                    final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericDoubleTextBox( allowEmptyValue );
        final Double numericValue = (Double) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new Double( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (Double) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Double( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericFloatTextBox(final DTCellValue52 value,
                                                                   final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericFloatTextBox( allowEmptyValue );
        final Float numericValue = (Float) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new Float( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (Float) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Float( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericIntegerTextBox(final DTCellValue52 value,
                                                                     final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericIntegerTextBox( allowEmptyValue );
        final Integer numericValue = (Integer) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new Integer( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (Integer) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Integer( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericLongTextBox(final DTCellValue52 value,
                                                                  final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericLongTextBox( allowEmptyValue );
        final Long numericValue = (Long) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new Long( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (Long) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Long( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericShortTextBox(final DTCellValue52 value,
                                                                   final boolean allowEmptyValue) {
        final AbstractRestrictedEntryTextBox tb = new NumericShortTextBox( allowEmptyValue );
        final Short numericValue = (Short) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    try {
                        value.setNumericValue( new Short( event.getValue() ) );
                    }
                    catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValue ) {
                            value.setNumericValue( (Short) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Short( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private TextBox makeTextBox(final DTCellValue52 value) {
        TextBox tb = new TextBox();
        tb.setValue( value.getStringValue() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange(ValueChangeEvent<String> event) {
                    value.setStringValue( event.getValue() );
                }

            } );
        }
        return tb;
    }

    private Widget makeDateSelector(final DTCellValue52 value,
                                    final boolean allowEmptyValue) {
        //If read-only return a label
        if ( isReadOnly ) {
            Label dateLabel = new Label();
            dateLabel.setText( format.format( value.getDateValue() ) );
            return dateLabel;
        }

        PopupDatePicker dp = new PopupDatePicker();
        if ( value.getDateValue() != null ) {
            dp.setValue( value.getDateValue() );
        }

        // Wire up update handler
        dp.addValueChangeHandler( new ValueChangeHandler<Date>() {

            public void onValueChange(ValueChangeEvent<Date> event) {
                value.setDateValue( event.getValue() );
            }

        } );
        return dp;
    }

    /**
     * An editor for whether the column is hidden or not
     * 
     * @param col
     * @return
     */
    public static CheckBox getHideColumnIndicator(final DTColumnConfig52 col) {
        final CheckBox chkHide = new CheckBox();
        chkHide.setValue( col.isHideColumn() );
        chkHide.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent sender) {
                col.setHideColumn( chkHide.getValue() );
            }
        } );
        return chkHide;
    }

}
