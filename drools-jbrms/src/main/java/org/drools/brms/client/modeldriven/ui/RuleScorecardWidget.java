package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the parent widget that contains the model based rule builder.
 * 
 * @author Michael Neale
 *
 */
public class RuleScorecardWidget extends Composite {

    private FlexTable layout;
    private SuggestionCompletionEngine completions;
    
    public RuleScorecardWidget(SuggestionCompletionEngine com) {
        this.completions = com;
        
        layout = new FlexTable();
        layout.setStyleName( "model-builder-Background" );
        
        initWidget( layout );
        final VerticalPanel lhsPanel = new VerticalPanel();
        
        Image addPattern = new Image( "images/new_item.gif" );
        addPattern.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                lhsPanel.add( new PatternWidget(completions, "") );
                
            }
            
        });
        
        
        
        layout.setWidget( 0, 0, new Label("IF") );
        layout.setWidget( 0, 2, addPattern );
        
        layout.setWidget( 1, 1, lhsPanel );
        
        
    }
    
    
    
    
}
