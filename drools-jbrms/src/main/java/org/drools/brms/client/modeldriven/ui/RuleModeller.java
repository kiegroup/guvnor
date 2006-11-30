package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.FactPattern;
import org.drools.brms.client.modeldriven.model.IPattern;
import org.drools.brms.client.modeldriven.model.RuleModel;

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
public class RuleModeller extends Composite {

    private FlexTable layout;
    private SuggestionCompletionEngine completions;
    private RuleModel model;
    
    public RuleModeller(SuggestionCompletionEngine com, RuleModel model) {
        this.model = model;
        this.completions = com;
        
        layout = new FlexTable();
        layout.setStyleName( "model-builder-Background" );
        
        initWidget( layout );
        
        Image addPattern = new Image( "images/new_item.gif" );
        addPattern.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showFactTypeSelector();               
            }            
        });
        
        layout.setWidget( 0, 0, new Label("IF") );
        layout.setWidget( 0, 2, addPattern );
        
        
        
        layout.setWidget( 1, 1, renderLhs(this.model) );
        layout.setWidget( 2, 0, new Label("THEN") );
        layout.setWidget( 3, 1, new Label("<Rhs here>") );
        
        
        
    }

    protected void showFactTypeSelector() {
        // TODO Auto-generated method stub
        
    }

    private Widget renderLhs(RuleModel model) {
        VerticalPanel vert = new VerticalPanel();
        for ( int i = 0; i < model.lhs.length; i++ ) {
            IPattern pattern = model.lhs[i];
            if (pattern instanceof FactPattern) {                
                vert.add( new FactPatternWidget(pattern, completions) );
            }
            //TODO: add stuff for removing pattern here.
            
        }
        
        return vert;
    }


    
    
    
    
}
