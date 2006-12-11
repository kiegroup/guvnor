package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.ActionRetractFact;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * This is used when you want to retract a fact. It will provide a list of 
 * bound facts for you to retract.
 * @author Michael Neale
 */
public class ActionRetractFactWidget extends Composite {

    private FlexTable layout;

    
    public ActionRetractFactWidget(SuggestionCompletionEngine com, ActionRetractFact model) {
        layout = new FlexTable();
        
        layout.setStyleName( "model-builderInner-Background" );
        
        layout.setWidget( 0, 0, new Label(com.getActionDisplayName( "retract" )) );
        layout.setWidget( 0, 1, new Label(model.variableName) );
        
        initWidget( layout );
    }

    
    
}
