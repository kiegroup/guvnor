package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.common.Lbl;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.ActionRetractFact;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

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
        
        layout.setWidget( 0, 0, new Lbl(HumanReadable.getActionDisplayName( "retract" ), "modeller-action-Label") );
        layout.setWidget( 0, 1, new Lbl( "[" + model.variableName + "]", "modeller-action-Label") );
        
        initWidget( layout );
    }

    
    
}
