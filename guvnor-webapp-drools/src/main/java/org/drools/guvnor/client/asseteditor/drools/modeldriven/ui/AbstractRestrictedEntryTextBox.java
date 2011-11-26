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
package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A TextBox to handle restricted entry
 */
public abstract class AbstractRestrictedEntryTextBox extends TextBox {

    public AbstractRestrictedEntryTextBox() {
        setup();
    }

    protected void setup() {
        final TextBox me = this;

        this.addKeyPressHandler( new KeyPressHandler() {

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
                String oldValue = me.getValue();
                String newValue = oldValue.substring( 0,
                                                      me.getCursorPos() );
                newValue = newValue
                           + ((char) charCode);
                newValue = newValue
                           + oldValue.substring( me.getCursorPos() + me.getSelectionLength() );
                if ( !isValidValue( newValue ) ) {
                    event.preventDefault();
                }

            }

        } );

    }

    /**
     * Validate value of TextBox
     * 
     * @param value
     * @return True if valid
     */
    protected abstract boolean isValidValue(String value);

}
