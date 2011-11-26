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
import java.util.Date;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericTextBox;
import org.drools.guvnor.client.common.PopupDatePicker;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Factory for Widgets to edit DTCellValues
 */
public class DTCellValueWidgetFactory {

    private GuidedDecisionTable52      dtable;
    private SuggestionCompletionEngine sce;

    public DTCellValueWidgetFactory(GuidedDecisionTable52 dtable,
                                    SuggestionCompletionEngine sce) {
        this.sce = sce;
        this.dtable = dtable;
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
        switch ( type ) {
            case BOOLEAN :
                return new DTCellValue52( false );
            case DATE :
                return new DTCellValue52( new Date() );
            case NUMERIC :
                return new DTCellValue52( 0 );
            default :
                return new DTCellValue52( "" );
        }
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
        switch ( type ) {
            case BOOLEAN :
                return new DTCellValue52( false );
            case DATE :
                return new DTCellValue52( new Date() );
            case NUMERIC :
                return new DTCellValue52( 0 );
            default :
                return new DTCellValue52( "" );
        }
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
        switch ( type ) {
            case BOOLEAN :
                return new DTCellValue52( false );
            case DATE :
                return new DTCellValue52( new Date() );
            case NUMERIC :
                return new DTCellValue52( 0 );
            default :
                return new DTCellValue52( "" );
        }
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
        lb.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                value.setBooleanValue( lb.getValue( lb.getSelectedIndex() ).equals( "true" ) );
            }

        } );
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

        //If nothing has been selected, select the first value
        if ( selectedIndex == -1 ) {
            lb.setSelectedIndex( 0 );
            value.setStringValue( lb.getValue( 0 ) );
        }

        return lb;
    }

    private NumericTextBox makeNumericTextBox(final DTCellValue52 value) {
        final NumericTextBox tb = new NumericTextBox();
        tb.setValue( value.getNumericValue() == null ? "" : value.getNumericValue().toPlainString() );

        // Wire up update handler
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
        return tb;
    }

    private TextBox makeTextBox(final DTCellValue52 value) {
        TextBox tb = new TextBox();
        tb.setValue( value.getStringValue() );

        // Wire up update handler
        tb.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                value.setStringValue( event.getValue() );
            }

        } );
        return tb;
    }

    private PopupDatePicker makeDateSelector(final DTCellValue52 value) {
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
    public static TextBox getDefaultEditor(final DTColumnConfig52 col) {
        final TextBox txtDefaultValue = new TextBox();
        txtDefaultValue.setText( col.getDefaultValue() );
        txtDefaultValue.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                col.setDefaultValue( txtDefaultValue.getText() );
            }
        } );
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
