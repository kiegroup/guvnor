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
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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

    private final GuidedDecisionTable52      dtable;
    private final SuggestionCompletionEngine sce;
    private final boolean                    isReadOnly;

    private static final String              DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat      format      = DateTimeFormat.getFormat( DATE_FORMAT );

    public DTCellValueWidgetFactory(GuidedDecisionTable52 dtable,
                                    SuggestionCompletionEngine sce,
                                    boolean isReadOnly) {
        this.sce = sce;
        this.dtable = dtable;
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
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List")
     * 
     * @param c
     * @param value
     * @return
     */
    public Widget getWidget(DTColumnConfig52 c,
                            DTCellValue52 value) {
        DTDataTypes52 type = dtable.getTypeSafeType( c,
                                                     sce );
        String[] completions = dtable.getValueList( c,
                                                    sce );

        if ( completions != null && completions.length > 0 ) {
            return makeListBox( completions,
                                value );
        }

        switch ( type ) {
            case NUMERIC :
                return makeNumericTextBox( value );
            case NUMERIC_BIGDECIMAL :
                return makeNumericBigDecimalTextBox( value );
            case NUMERIC_BIGINTEGER :
                return makeNumericBigIntegerTextBox( value );
            case NUMERIC_BYTE :
                return makeNumericByteTextBox( value );
            case NUMERIC_DOUBLE :
                return makeNumericDoubleTextBox( value );
            case NUMERIC_FLOAT :
                return makeNumericFloatTextBox( value );
            case NUMERIC_INTEGER :
                return makeNumericIntegerTextBox( value );
            case NUMERIC_LONG :
                return makeNumericLongTextBox( value );
            case NUMERIC_SHORT :
                return makeNumericShortTextBox( value );
            case BOOLEAN :
                return makeBooleanSelector( value );
            case DATE :
                return makeDateSelector( value );
            default :
                return makeTextBox( value );
        }
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

        DTDataTypes52 type = dtable.getTypeSafeType( pattern,
                                                     column,
                                                     sce );
        String[] completions = dtable.getValueList( pattern,
                                                    column,
                                                    sce );

        if ( completions != null && completions.length > 0 ) {
            return makeListBox( completions,
                                value );
        }

        switch ( type ) {
            case NUMERIC :
                return makeNumericTextBox( value );
            case NUMERIC_BIGDECIMAL :
                return makeNumericBigDecimalTextBox( value );
            case NUMERIC_BIGINTEGER :
                return makeNumericBigIntegerTextBox( value );
            case NUMERIC_BYTE :
                return makeNumericByteTextBox( value );
            case NUMERIC_DOUBLE :
                return makeNumericDoubleTextBox( value );
            case NUMERIC_FLOAT :
                return makeNumericFloatTextBox( value );
            case NUMERIC_INTEGER :
                return makeNumericIntegerTextBox( value );
            case NUMERIC_LONG :
                return makeNumericLongTextBox( value );
            case NUMERIC_SHORT :
                return makeNumericShortTextBox( value );
            case BOOLEAN :
                return makeBooleanSelector( value );
            case DATE :
                return makeDateSelector( value );
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

        DTDataTypes52 type = dtable.getTypeSafeType( pattern,
                                                     column,
                                                     sce );
        String[] completions = dtable.getValueList( pattern,
                                                    column,
                                                    sce );

        if ( completions != null && completions.length > 0 ) {
            return makeListBox( completions,
                                value );
        }

        switch ( type ) {
            case NUMERIC :
                return makeNumericTextBox( value );
            case NUMERIC_BIGDECIMAL :
                return makeNumericBigDecimalTextBox( value );
            case NUMERIC_BIGINTEGER :
                return makeNumericBigIntegerTextBox( value );
            case NUMERIC_BYTE :
                return makeNumericByteTextBox( value );
            case NUMERIC_DOUBLE :
                return makeNumericDoubleTextBox( value );
            case NUMERIC_FLOAT :
                return makeNumericFloatTextBox( value );
            case NUMERIC_INTEGER :
                return makeNumericIntegerTextBox( value );
            case NUMERIC_LONG :
                return makeNumericLongTextBox( value );
            case NUMERIC_SHORT :
                return makeNumericShortTextBox( value );
            case BOOLEAN :
                return makeBooleanSelector( value );
            case DATE :
                return makeDateSelector( value );
            default :
                return makeTextBox( value );
        }
    }

    private ListBox makeBooleanSelector(final DTCellValue52 value) {
        final ListBox lb = new ListBox();
        Boolean currentItem = value.getBooleanValue();
        lb.addItem( "true" );
        lb.addItem( "false" );
        lb.setSelectedIndex( currentItem ? 0 : 1 );

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    value.setBooleanValue( lb.getValue( lb.getSelectedIndex() ).equals( "true" ) );
                }

            } );
        }
        return lb;
    }

    private ListBox makeListBox(final String[] completions,
                                final DTCellValue52 value) {
        int selectedIndex = -1;
        final ListBox lb = new ListBox();
        String currentItem = value.getStringValue();
        for ( int i = 0; i < completions.length; i++ ) {
            String item = completions[i].trim();
            String[] splut = ConstraintValueEditorHelper.splitValue( item );
            lb.addItem( splut[1],
                        splut[0] );
            if ( splut[0].equals( currentItem ) ) {
                lb.setSelectedIndex( i );
                selectedIndex = i;
            }
        }

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    int index = lb.getSelectedIndex();
                    if ( index > -1 ) {
                        value.setStringValue( lb.getValue( index ) );
                    } else {
                        value.setStringValue( null );
                    }
                }

            } );
        }

        //If nothing has been selected, select the first value
        if ( selectedIndex == -1 ) {
            lb.setSelectedIndex( 0 );
            value.setStringValue( lb.getValue( 0 ) );
        }

        return lb;
    }

    private AbstractRestrictedEntryTextBox makeNumericTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericTextBox();
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
                        value.setNumericValue( BigDecimal.ZERO );
                        tb.setValue( BigDecimal.ZERO.toPlainString() );
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericBigDecimalTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericBigDecimalTextBox();
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
                        value.setNumericValue( BigDecimal.ZERO );
                        tb.setValue( BigDecimal.ZERO.toPlainString() );
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericBigIntegerTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericBigIntegerTextBox();
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
                        value.setNumericValue( BigInteger.ZERO );
                        tb.setValue( BigInteger.ZERO.toString() );
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericByteTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericByteTextBox();
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
                        value.setNumericValue( new Byte( "0" ) );
                        tb.setValue( "0" );
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericDoubleTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericDoubleTextBox();
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
                        value.setNumericValue( new Double( "0" ) );
                        tb.setValue( "0" );
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericFloatTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericFloatTextBox();
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
                        value.setNumericValue( new Float( "0" ) );
                        tb.setValue( "0" );
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericIntegerTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericIntegerTextBox();
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
                        value.setNumericValue( new Integer( "0" ) );
                        tb.setValue( "0" );
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericLongTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericLongTextBox();
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
                        value.setNumericValue( new Long( "0" ) );
                        tb.setValue( "0" );
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericShortTextBox(final DTCellValue52 value) {
        final AbstractRestrictedEntryTextBox tb = new NumericShortTextBox();
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
                        value.setNumericValue( new Short( "0" ) );
                        tb.setValue( "0" );
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

    private Widget makeDateSelector(final DTCellValue52 value) {
        //If read-only return a label
        if ( isReadOnly ) {
            Label dateLabel = new Label();
            dateLabel.setText( format.format( value.getDateValue() ) );
            return dateLabel;
        }

        PopupDatePicker dp = new PopupDatePicker();
        dp.setValue( value.getDateValue() );

        // Wire up update handler
        dp.addValueChangeHandler( new ValueChangeHandler<Date>() {

            public void onValueChange(ValueChangeEvent<Date> event) {
                value.setDateValue( event.getValue() );
            }

        } );
        return dp;
    }

    /**
     * An editor for 'Default Value'
     * 
     * @param col
     * @return
     */
    public static TextBox getDefaultEditor(final DTColumnConfig52 col,
                                           boolean isReadOnly) {
        final TextBox txtDefaultValue = new TextBox();
        txtDefaultValue.setText( col.getDefaultValue() );

        // Wire up update handler
        txtDefaultValue.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            txtDefaultValue.addChangeHandler( new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    col.setDefaultValue( txtDefaultValue.getText() );
                }
            } );
        }
        return txtDefaultValue;
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
