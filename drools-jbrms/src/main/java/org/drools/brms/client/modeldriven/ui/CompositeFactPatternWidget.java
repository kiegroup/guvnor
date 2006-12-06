package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.model.CompositeFactPattern;
import org.drools.brms.client.modeldriven.model.FactPattern;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CompositeFactPatternWidget extends Composite {

    private final SuggestionCompletionEngine completions;
    private CompositeFactPattern pattern;
    private Grid layout;
    private RuleModeller modeller;

    public CompositeFactPatternWidget(RuleModeller modeller, CompositeFactPattern pattern,
                                      SuggestionCompletionEngine completions) {
        this.completions = completions;
        this.pattern = pattern;
        this.modeller = modeller;
        
        
        this.layout = new Grid(1, 2);
        this.layout.setStyleName( "model-builderInner-Background" );
        
        doLayout( );
        
        
        initWidget( layout );        
     }

    private void doLayout() {
        VerticalPanel vert = new VerticalPanel();
        
        FactPattern[] facts = pattern.patterns;
        for ( int i = 0; i < facts.length; i++ ) {
            vert.add( new FactPatternWidget(modeller, facts[i], this.completions) );
        }
        
        this.layout.setWidget( 0, 0, new Label(pattern.type) );
        this.layout.setWidget( 0, 1, vert );
    }

    
    
}
