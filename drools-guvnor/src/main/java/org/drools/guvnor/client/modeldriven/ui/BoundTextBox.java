/**
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

package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.FieldEditListener;
import org.drools.guvnor.client.common.IDirtyable;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Toni Rikkola
 *
 */
public class BoundTextBox extends TextBox
    implements
    IDirtyable {

    public BoundTextBox(final BaseSingleFieldConstraint c) {
        setStyleName( "constraint-value-Editor" ); //NON-NLS
        if ( c.getValue() == null ) {
            setText( "" );
        } else {
            setText( c.getValue() );
        }

        String v = c.getValue();
        if ( c.getValue() == null || v.length() < 7 ) {
            setVisibleLength( 8 );
        } else {
            setVisibleLength( v.length() + 1 );
        }

        addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                c.setValue(getText());
            }
        } );

        addKeyboardListener( new FieldEditListener( new Command() {
            public void execute() {
                setVisibleLength( getText().length() );
            }
        } ) );
    }
}
