/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.TextBoxFactory;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Default editor for literal values, a text box.
 */
public class DefaultLiteralEditor extends Composite {

    private final BaseSingleFieldConstraint constraint;
    private final String                    dataType;

    private final Label                     textWidget   = new Label();

    private final Button                    okButton     = new Button( Constants.INSTANCE.OK() );
    private final ValueChanged              valueChanged = new ValueChanged() {
                                                             public void valueChanged(String newValue) {
                                                                 constraint.setValue( newValue );
                                                                 if ( onValueChangeCommand != null ) {
                                                                     onValueChangeCommand.execute();
                                                                 }
                                                                 okButton.click();
                                                             }
                                                         };

    private Command                         onValueChangeCommand;

    public DefaultLiteralEditor(final BaseSingleFieldConstraint constraint,
                                final String dataType) {
        this.constraint = constraint;
        this.dataType = dataType;

        textWidget.setStyleName( "form-field" );
        textWidget.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                showPopup();
            }
        } );

        if ( constraint.getValue() != null && !"".equals( constraint.getValue() ) ) {
            textWidget.setText( constraint.getValue() );
        } else {
            textWidget.setText( Constants.INSTANCE.Value() );
        }

        initWidget( textWidget );
    }

    private void showPopup() {
        final PopupPanel popup = new PopupPanel();
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        popup.setGlassEnabled( true );
        popup.setPopupPosition( this.getAbsoluteLeft(),
                                this.getAbsoluteTop() );

        okButton.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {

                if ( !isValueEmpty( constraint.getValue() ) ) {
                    if ( onValueChangeCommand != null ) {
                        onValueChangeCommand.execute();
                    }
                    textWidget.setText( constraint.getValue() );
                    popup.hide();
                }
            }
        } );

        horizontalPanel.add( getTextBox() );
        horizontalPanel.add( okButton );

        popup.add( horizontalPanel );

        popup.show();

    }

    private TextBox getTextBox() {
        final TextBox box = TextBoxFactory.getTextBox( dataType );
        box.setTitle( Constants.INSTANCE.LiteralValueTip() );
        box.setStyleName( "constraint-value-Editor" );
        if ( constraint.getValue() == null ) {
            box.setText( "" );
        } else {
            box.setText( constraint.getValue() );
        }

        String v = constraint.getValue();
        if ( v == null || v.length() < 7 ) {
            box.setVisibleLength( 8 );
        } else {
            box.setVisibleLength( v.length() + 1 );
        }

        box.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                constraint.setValue( box.getText() );
            }

        } );

        box.addKeyUpHandler( new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {

                //Alter visible size
                int length = box.getText().length();
                box.setVisibleLength( length > 0 ? length : 1 );

                //Commit change if enter is pressed
                final int keyCode = event.getNativeKeyCode();
                if ( keyCode == KeyCodes.KEY_ENTER ) {
                    valueChanged.valueChanged( box.getText() );
                }
            }
        } );

        return box;
    }

    private boolean isValueEmpty(String s) {
        if ( s == null || "".equals( s.trim() ) ) {
            ErrorPopup.showMessage( Constants.INSTANCE.ValueCanNotBeEmpty() );
            return true;
        } else {
            return false;
        }
    }

    public void setOnValueChangeCommand(Command onValueChangeCommand) {
        this.onValueChangeCommand = onValueChangeCommand;
    }
}
