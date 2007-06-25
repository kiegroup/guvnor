package org.drools.brms.client.modeldriven.ui;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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