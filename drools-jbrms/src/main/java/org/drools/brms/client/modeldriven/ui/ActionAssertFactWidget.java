package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.ActionAssertFact;
import org.drools.brms.client.modeldriven.model.ActionFieldValue;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is used when asserting a new fact into working memory. 
 * 
 * @author Michael Neale
 *
 */
public class ActionAssertFactWidget extends Composite {

    private FlexTable layout;
    private ActionAssertFact model;
    private SuggestionCompletionEngine completions;
    private String[] fieldCompletions;
    
    public ActionAssertFactWidget(ActionAssertFact set, SuggestionCompletionEngine com) {
        this.model = set;
        this.completions = com;
        this.layout = new FlexTable();
        this.fieldCompletions = this.completions.getFieldCompletions( set.factType );
        
        layout.setStyleName( "model-builderInner-Background" );
        
        doLayout();
        
        initWidget(this.layout);
    }

    private void doLayout() {
        layout.clear();
        layout.setWidget( 0, 0, getAssertLabel() );
        
        FlexTable inner = new FlexTable();
        
        
        for ( int i = 0; i < model.fieldValues.length; i++ ) {
            ActionFieldValue val = model.fieldValues[i];
            
            inner.setWidget( i, 0, fieldSelector(val) );
            inner.setWidget( i, 1, valueEditor(val) );
        }
        
        layout.setWidget( 0, 1, inner );
        
                
    }

    private Widget valueEditor(final ActionFieldValue val) {
        final TextBox box = new TextBox();
        box.setText( val.value );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                val.value = box.getText();
            }            
        });
        return box;
    }

    private Widget fieldSelector(final ActionFieldValue val) {
        final ListBox box = new ListBox();
        for ( int i = 0; i < this.fieldCompletions.length; i++ ) {
            box.addItem( this.fieldCompletions[i] );
            if (this.fieldCompletions[i].equals( val.field )) {
                box.setSelectedIndex( i );
            }

        }
        
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                val.field = box.getItemText( box.getSelectedIndex() );                
            }            
        });
        
        return box;    
    }

    private Widget getAssertLabel() {        
        return new Label("Assert new "  + this.model.factType);
    }
    
}
