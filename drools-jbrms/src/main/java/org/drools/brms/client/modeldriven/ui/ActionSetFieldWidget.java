package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.ActionFieldValue;
import org.drools.brms.client.modeldriven.model.ActionSetField;
import org.drools.brms.client.modeldriven.model.FactPattern;
import org.drools.brms.client.modeldriven.model.RuleModel;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is for setting fields on a bound fact or global variable.
 * 
 * @author Michael Neale
 */
public class ActionSetFieldWidget extends Composite {

    final private ActionSetField model;
    final private SuggestionCompletionEngine completions;
    final private FlexTable layout;
    private boolean isBoundFact = false;
    final private String[] fieldCompletions;
    
    
    public ActionSetFieldWidget(RuleModel rule, ActionSetField set, SuggestionCompletionEngine com) {
        this.model = set;
        this.completions = com;
        this.layout = new FlexTable();
        layout.setStyleName( "model-builderInner-Background" );
        if (completions.isGlobalVariable( set.variable )) {
            this.fieldCompletions = completions.getFieldCompletionsForGlobalVariable( set.variable );            
        } else {
            FactPattern pattern = rule.getBoundFact( set.variable );
            this.fieldCompletions = completions.getFieldCompletions( pattern.factType );
            this.isBoundFact = true;
        }
        
        doLayout();
        
        initWidget( this.layout );
    }


    private void doLayout() {
        layout.clear();
        layout.setWidget( 0, 0, getSetterLabel() );
        
        FlexTable inner = new FlexTable();
        
        
        for ( int i = 0; i < model.fieldValues.length; i++ ) {
            ActionFieldValue val = model.fieldValues[i];
            
            inner.setWidget( i, 0, fieldSelector(val) );
            inner.setWidget( i, 1, valueEditor(val) );
        }
        
        layout.setWidget( 0, 1, inner );
        
        
    }


    private Label getSetterLabel() {
        return new Label("Set " + model.variable);
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
    
}
