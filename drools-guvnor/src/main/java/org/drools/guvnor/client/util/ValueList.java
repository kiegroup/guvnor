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
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author rikkola
 *
 */
public class ValueList extends Composite
    implements
    HasValue<String[]> {

    private final ListBox layout              = new ListBox();
    private int           maxVisibleItemCount = -1;

    public ValueList() {
        initWidget( layout );
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String[]> handler) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }

    public String[] getValue() {
        String[] value = new String[layout.getItemCount()];

        for ( int i = 0; i < layout.getItemCount(); i++ ) {
            value[i] = layout.getItemText( i );
        }

        return value;
    }

    public void setValue(String[] value) {
        setValue( value,
                  false );
    }

    public void setValue(String[] value,
                         boolean fireEvents) {

        for ( String string : value ) {
            layout.addItem( string );
        }

        setVisibleItems();

        if ( fireEvents ) {
            ValueChangeEvent.fire( this,
                                   value );
        }
    }

    private void setVisibleItems() {
        int itemCount = layout.getItemCount();

        if ( maxVisibleItemCount > 0 && itemCount > maxVisibleItemCount ) {
            layout.setVisibleItemCount( maxVisibleItemCount );
        } else {
            layout.setVisibleItemCount( itemCount );
        }
    }

    public void setMaxVisibleItemCount(int maxVisibleItemCount) {
        this.maxVisibleItemCount = maxVisibleItemCount;
    }
}
