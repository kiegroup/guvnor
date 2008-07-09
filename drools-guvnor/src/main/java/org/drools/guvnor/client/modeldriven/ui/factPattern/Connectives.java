package org.drools.guvnor.client.modeldriven.ui.factPattern;

import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.ui.ConstraintValueEditor;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class Connectives {
    private FactPattern                pattern;
    private SuggestionCompletionEngine completions;
    private RuleModeller               modeller;

    
    /**
     * Returns the pattern.
     */
    public FactPattern getPattern() {
        return pattern;
    }

    /**
     * Sets the pattern.
     */
    public void setPattern(FactPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Returns the completions.
     */
    public SuggestionCompletionEngine getCompletions() {
        return completions;
    }

    /**
     * Sets completions.
     */
    public void setCompletions(SuggestionCompletionEngine completions) {
        this.completions = completions;
    }

    /**
     * Returns the modeller.
     */
    public RuleModeller getModeller() {
        return modeller;
    }

    /**
     * Sets the modeller.
     */
    public void setModeller(RuleModeller modeller) {
        this.modeller = modeller;
    }

    public Widget connectives(SingleFieldConstraint c, String factClass) {
        if ( c.connectives != null && c.connectives.length > 0 ) {
            DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
            for ( int i = 0; i < c.connectives.length; i++ ) {
                ConnectiveConstraint con = c.connectives[i];
                horiz.add( connectiveOperatorDropDown( con, c.fieldName ) );
                horiz.add( connectiveValueEditor( con, factClass, c.fieldName ) );
            }
            return horiz;
        } else {
            //nothing to do
            return null;
        }

    }

    private Widget connectiveValueEditor(final ISingleFieldConstraint con, String factClass, String fieldName) {
        String typeNumeric = this.modeller.getSuggestionCompletions().getFieldType( factClass, fieldName );
        return new ConstraintValueEditor(pattern, fieldName, con, this.modeller, typeNumeric);
    }

    private Widget connectiveOperatorDropDown(final ConnectiveConstraint con, String fieldName) {
        String[] ops = completions.getConnectiveOperatorCompletions( pattern.factType, fieldName );
        final ListBox box = new ListBox();
        box.addItem( "--- please choose ---" );
        for ( int i = 0; i < ops.length; i++ ) {
            String op = ops[i];
            box.addItem( HumanReadable.getOperatorDisplayName( op ), op );
            if ( op.equals( con.operator ) ) {
                box.setSelectedIndex( i + 1 );
            }

        }

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                con.operator = box.getValue( box.getSelectedIndex() );
            }
        } );

        return box;
    }
}
