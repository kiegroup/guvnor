package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Default editor for literal values, a text box. 
 * 
 * @author Toni Rikkola
 *
 */
public class DefaultLiteralEditor extends Composite {

    private Constants              constants    = ((Constants) GWT.create( Constants.class ));
    private ISingleFieldConstraint constraint;
    private final boolean          numericValue;

    private Label                  textWidget   = new Label();

    private final Button           okButton     = new Button( constants.OK() );
    private final ValueChanged     valueChanged = new ValueChanged() {
                                                    public void valueChanged(String newValue) {
                                                        constraint.value = newValue;
                                                        okButton.click();
                                                    }
                                                };

    public DefaultLiteralEditor(ISingleFieldConstraint constraint,
                                boolean numericValue) {
        this.constraint = constraint;
        this.numericValue = numericValue;

        textWidget.setStyleName( "x-form-field" );

        textWidget.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                showPopup();
            }
        } );

        if ( constraint.value != null && !"".equals( constraint.value ) ) {
            textWidget.setText( constraint.value );
        } else {
            textWidget.setText( constants.Value() );
        }

        initWidget( textWidget );
    }

    private void showPopup() {
        final PopupPanel popup = new PopupPanel();
        HorizontalPanel horizontalPanel = new HorizontalPanel();

        popup.setPopupPosition( this.getAbsoluteLeft(),
                                this.getAbsoluteTop() );

        okButton.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {

                if ( !isValueEmpty( constraint.value ) ) {
                    textWidget.setText( constraint.value );

                    popup.hide();
                }
            }
        } );

        horizontalPanel.add( getTextBox() );
        horizontalPanel.add( okButton );

        popup.add( horizontalPanel );

        popup.show();

    }

    public TextBox getTextBox() {

        final TextBox box = new BoundTextBox( constraint );

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

    private boolean isValueEmpty(String s) {
        if ( s == null || "".equals( s.trim() ) ) {
            ErrorPopup.showMessage( constants.ValueCanNotBeEmpty() );
            return true;
        } else {
            return false;
        }
    }
}
