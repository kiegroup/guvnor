package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.FieldEditListener;
import org.drools.guvnor.client.common.IDirtyable;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;

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

    public BoundTextBox(final ISingleFieldConstraint c) {
        setStyleName( "constraint-value-Editor" ); //NON-NLS
        if ( c.value == null ) {
            setText( "" );
        } else {
            setText( c.value );
        }

        String v = c.value;
        if ( c.value == null || v.length() < 7 ) {
            setVisibleLength( 8 );
        } else {
            setVisibleLength( v.length() + 1 );
        }

        addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                c.value = getText();
            }
        } );

        addKeyboardListener( new FieldEditListener( new Command() {
            public void execute() {
                setVisibleLength( getText().length() );
            }
        } ) );
    }
}
