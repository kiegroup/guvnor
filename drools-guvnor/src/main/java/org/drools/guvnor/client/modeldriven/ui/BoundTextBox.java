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
