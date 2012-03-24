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
package org.drools.guvnor.client.common;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.user.client.ui.TextBox;

/**
 * A Factory for Text Boxes relevant to the specified data-type
 */
public class TextBoxFactory {

    /**
     * Get a TextBox relevant to the specified data-type
     * 
     * @param dataType
     * @return
     */
    public static TextBox getTextBox(final String dataType) {

        if ( SuggestionCompletionEngine.TYPE_NUMERIC.equals( dataType ) ) {
            return new NumericTextBox();
        } else if ( SuggestionCompletionEngine.TYPE_NUMERIC_BIGDECIMAL.equals( dataType ) ) {
            return new NumericBigDecimalTextBox();
        } else if ( SuggestionCompletionEngine.TYPE_NUMERIC_BIGINTEGER.equals( dataType ) ) {
            return new NumericBigIntegerTextBox();
        } else if ( SuggestionCompletionEngine.TYPE_NUMERIC_BYTE.equals( dataType ) ) {
            return new NumericByteTextBox();
        } else if ( SuggestionCompletionEngine.TYPE_NUMERIC_DOUBLE.equals( dataType ) ) {
            return new NumericDoubleTextBox();
        } else if ( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT.equals( dataType ) ) {
            return new NumericFloatTextBox();
        } else if ( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER.equals( dataType ) ) {
            return new NumericIntegerTextBox();
        } else if ( SuggestionCompletionEngine.TYPE_NUMERIC_LONG.equals( dataType ) ) {
            return new NumericLongTextBox();
        } else if ( SuggestionCompletionEngine.TYPE_NUMERIC_SHORT.equals( dataType ) ) {
            return new NumericShortTextBox();
        } else {
            return new TextBox();
        }
    }

}
