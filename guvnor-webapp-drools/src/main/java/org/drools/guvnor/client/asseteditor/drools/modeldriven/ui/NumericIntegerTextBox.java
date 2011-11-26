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

import com.google.gwt.regexp.shared.RegExp;

/**
 * A TextBox to handle integer values
 */
public class NumericIntegerTextBox extends AbstractRestrictedEntryTextBox {

    // A valid integer
    private static final RegExp VALID = RegExp.compile( "(^[-]?\\d+$)" );

    @Override
    protected boolean isValidValue(String value) {
        boolean isValid = VALID.test( value );
        if ( !isValid ) {
            return isValid;
        }
        try {
            Integer.parseInt( value );
        } catch ( NumberFormatException nfe ) {
            isValid = false;
        }
        return isValid;
    }

}
