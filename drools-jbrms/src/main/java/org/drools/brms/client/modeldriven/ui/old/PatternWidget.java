package org.drools.brms.client.modeldriven.ui.old;

import java.util.ArrayList;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * This is the containing widgets for applying constraints to a pattern. 
 * 
 * eg: <code>Person(age < 42)</code> would be covered by this widget.
 *  
 * @author Michael Neale
 */
public class PatternWidget extends Composite {

    private final FlexTable layout;
    private String factTypeSelected;
    private FactTypeWidget factWidget;
    private SuggestionCompletionEngine completions;
    
    public PatternWidget(SuggestionCompletionEngine com, String factType) {
        
        this.completions = com;
        layout = new FlexTable();
        this.factTypeSelected = factType;
                             
        layout.setStyleName( "model-builder-Background" );
        
        
        factWidget = new FactTypeWidget(factTypeSelected, com, new Command() {
            public void execute() {
                selectedFactType();
            }            
        });
        layout.setWidget( 0, 0, factWidget );
        layout.getFlexCellFormatter().setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_MIDDLE );
       
        //TODO: add loading up of existing data if its passed in here.
        
        
        initWidget( layout );
        
    }


    protected void selectedFactType() {
        factTypeSelected = factWidget.getFactType();   
        layout.setWidget(0, 1, new ConstraintWidget(factTypeSelected, completions, new ArrayList()) );
    }
    
}
