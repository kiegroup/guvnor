package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.DropDownData;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Editor for literal values. 
 * Normally shows the value as text, when clicked opens a custom editor for dates, 
 * enums etc or by default a textbox.
 * 
 * @author Toni Rikkola
 *
 */
public class LiteralEditor extends Composite {

    private final FactPattern                pattern;
    private final String                     fieldName;
    private final SuggestionCompletionEngine sce;
    private Constants                        constants    = ((Constants) GWT.create( Constants.class ));
    protected Panel                          panel        = new HorizontalPanel();
    protected Label                          labelWidget  = new Label();
    private ISingleFieldConstraint           constraint;
    private DropDownData                     dropDownData;
    private String                           fieldType;
    private final boolean                    numericValue;

    private final Button                     okButton     = new Button( constants.OK() );
    private final ValueChanged               valueChanged = new ValueChanged() {
                                                              public void valueChanged(String newValue) {
                                                                  constraint.value = newValue;
                                                                  okButton.click();
                                                              }
                                                          };

    public LiteralEditor(FactPattern pattern,
                         String fieldName,
                         SuggestionCompletionEngine sce,
                         ISingleFieldConstraint constraint,
                         DropDownData dropDownData,
                         String fieldType,
                         boolean numericValue) {
        this.pattern = pattern;
        this.fieldName = fieldName;
        this.sce = sce;
        this.constraint = constraint;
        this.dropDownData = dropDownData;
        this.fieldType = fieldType;
        this.numericValue = numericValue;

        if ( SuggestionCompletionEngine.TYPE_DATE.equals( this.fieldType ) ) {

            DatePickerLabel datePicker = new DatePickerLabel( constraint.value );

            // Set the default time
            this.constraint.value = datePicker.getDateString();

            datePicker.addValueChanged( valueChanged );

            initWidget( datePicker );
        } else {

            labelWidget.setStyleName( "x-form-field" );

            if ( constraint.value != null && !"".equals( constraint.value ) ) {
                labelWidget.setText( constraint.value );
            } else {
                labelWidget.setText( constants.Value() );
            }

            panel.add( labelWidget );

            labelWidget.addClickListener( new ClickListener() {
                public void onClick(Widget arg0) {
                    showPopup();
                }
            } );

            initWidget( panel );
        }
    }

    private void showPopup() {
        final PopupPanel popup = new PopupPanel();
        HorizontalPanel horizontalPanel = new HorizontalPanel();

        popup.setPopupPosition( this.getAbsoluteLeft(),
                                this.getAbsoluteTop() );

        okButton.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {

                if ( !isValueEmpty( constraint.value ) ) {
                    labelWidget.setText( constraint.value );

                    //                valueChanged();
                    panel.clear();
                    panel.add( labelWidget );
                    popup.hide();
                }
            }
        } );

        Widget w = getEditorWidget();

        horizontalPanel.add( w );
        horizontalPanel.add( okButton );

        popup.add( horizontalPanel );

        popup.show();

    }

    public Widget getEditorWidget() {

        //use a drop down if we have a fixed list
        if ( this.dropDownData != null ) {

            this.dropDownData = sce.getEnums( pattern,
                                              fieldName );

            ListBox box = ConstraintValueEditor.enumDropDown( constraint.value,
                                                              valueChanged,
                                                              this.dropDownData );

            box.setVisibleItemCount( 6 );

            return box;

        } else if ( SuggestionCompletionEngine.TYPE_DATE.equals( this.fieldType ) ) {

            DatePickerLabel datePicker = new DatePickerLabel( constraint.value );

            // Set the default time
            constraint.value = datePicker.getDateString();

            datePicker.addValueChanged( valueChanged );

            return datePicker;

        } else {

            final TextBox box = ConstraintValueEditor.boundTextBox( constraint );

            box.addKeyboardListener( new KeyboardListener() {

                public void onKeyDown(Widget arg0,
                                      char arg1,
                                      int arg2) {

                }

                public void onKeyPress(Widget w,
                                       char c,
                                       int i) {
                    if ( numericValue && Character.isLetter( c ) ) {
                        ((TextBox) w).cancelKey();
                    }
                }

                public void onKeyUp(Widget arg0,
                                    char c,
                                    int arg2) {
                    if ( '\r' == c || '\n' == c ) {
                        valueChanged.valueChanged( box.getText() );
                    } else {
                        constraint.value = box.getText();
                    }
                }

            } );

            box.setTitle( constants.LiteralValueTip() );
            return box;
        }
    }

    private boolean isValueEmpty(String s) {
        if ( s == null || "".equals( s.trim() ) ) {
            ErrorPopup.showMessage( constants.ValueCanNotBeEmpty() );
            return true;
        } else {
            return false;
        }
    }
}
