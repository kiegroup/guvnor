/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.util;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class ToggleLabel extends Composite
    implements
    HasValue<Boolean> {

    private Label  label = new Label();

    private String successText;
    private String failureText;

    public ToggleLabel() {
        label.setText( failureText );

        initWidget( label );
    }

    public void setFailureText(String failureText) {
        this.failureText = failureText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }

    public Boolean getValue() {
        return label.getText().equals( successText );
    }

    public void setValue(Boolean value) {
        setValue( value,
                  false );
    }

    public void setValue(Boolean value,
                         boolean fireEvents) {
        if ( value ) {
            label.setText( successText );
        } else {
            label.setText( failureText );
        }

        if ( fireEvents ) {
            ValueChangeEvent.fire( this,
                                   value );
        }

    }

}
