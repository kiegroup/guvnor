package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;

/**
 * This shows a list of options for the left hand side of a rule.
 * @author Michael Neale
 */
public class LeftHandSideSelector {
    
    private FormStylePopup form;
    
    public LeftHandSideSelector(SuggestionCompletionEngine completions, ClickListener okClick) {
        final ListBox box = new ListBox();
        String[] facts = completions.getFactTypes();
        for ( int i = 0; i < facts.length; i++ ) {
            box.addItem( facts[i] );
        }
        final FormStylePopup popup = new FormStylePopup("images/new_fact.gif", "New fact pattern...");
        popup.addAttribute( "choose type", box );
        Button ok = new Button("OK");
        popup.addAttribute( "", ok );
        
        ok.addClickListener( okClick );
        popup.setStyleName( "ks-popups-Popup" );
   
    }
    
    public void show(int left, int top) {
        form.setPopupPosition( left, top );
        form.show();     
    }

}
