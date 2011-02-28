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
package org.drools.guvnor.client.decisiontable.cells;

import java.math.BigDecimal;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A Popup Text Editor.
 */
public class PopupNumericEditCell extends
        AbstractPopupEditCell<BigDecimal, BigDecimal> {

    private final TextBox       textBox;

    // A valid number
    private static final RegExp VALID = RegExp.compile( "(^[-+]?[0-9]*\\.?[0-9]*([eE][-+]?[0-9]*)?$)" );

    public PopupNumericEditCell() {
        super();
        this.textBox = new TextBox();

        // Tabbing out of the TextBox commits changes
        textBox.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                boolean keyTab = event.getNativeKeyCode() == KeyCodes.KEY_TAB;
                boolean keyEnter = event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
                if ( keyEnter
                     || keyTab ) {
                    commit();
                }
            }

        } );

        // Restrict entry to navigation and numerics
        textBox.addKeyPressHandler( new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {

                // Permit navigation
                int keyCode = event.getNativeEvent().getKeyCode();
                if ( event.isControlKeyDown()
                        || keyCode == KeyCodes.KEY_BACKSPACE
                        || keyCode == KeyCodes.KEY_DELETE
                        || keyCode == KeyCodes.KEY_LEFT
                        || keyCode == KeyCodes.KEY_RIGHT
                        || keyCode == KeyCodes.KEY_TAB ) {
                    return;
                }

                // Get new value and validate
                int charCode = event.getCharCode();
                String oldValue = textBox.getValue();
                String newValue = oldValue.substring( 0,
                                                      textBox.getCursorPos() );
                newValue = newValue
                           + ((char) charCode);
                newValue = newValue
                           + oldValue.substring( textBox.getCursorPos()
                                                 + textBox.getSelectionLength() );
                if ( !VALID.test( String.valueOf( newValue ) ) ) {
                    event.preventDefault();
                }
            }

        } );

        vPanel.add( textBox );

    }

    @Override
    public void render(Context context,
                       BigDecimal value,
                       SafeHtmlBuilder sb) {
        if ( value != null ) {
            sb.append( renderer.render( value.toPlainString() ) );
        }
    }

    // Commit the change
    @Override
    protected void commit() {

        // Update value
        String text = textBox.getValue();
        BigDecimal number = null;
        if ( text.length() > 0 ) {
            try {
                number = new BigDecimal( text );
            } catch ( NumberFormatException e ) {
                number = new BigDecimal( 0 );
            }
        }
        setValue( lastContext,
                  lastParent,
                  number );
        if ( valueUpdater != null ) {
            valueUpdater.update( number );
        }
        panel.hide();
    }

    // Start editing the cell
    @Override
    protected void startEditing(final Context context,
                                final Element parent,
                                final BigDecimal value) {

        textBox.setValue( (value == null ? "" : value.toPlainString()) );

        panel.setPopupPositionAndShow( new PositionCallback() {
            public void setPosition(int offsetWidth,
                                    int offsetHeight) {
                panel.setPopupPosition( parent.getAbsoluteLeft()
                                                + offsetX,
                                        parent.getAbsoluteTop()
                                                + offsetY );

                // Focus the first enabled control
                Scheduler.get().scheduleDeferred( new ScheduledCommand() {

                    public void execute() {
                        String text = textBox.getValue();
                        textBox.setFocus( true );
                        textBox.setCursorPos( text.length() );
                        textBox.setSelectionRange( 0,
                                                   text.length() );
                    }

                } );
            }
        } );

    }

}
