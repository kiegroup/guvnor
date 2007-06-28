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



import org.drools.brms.client.common.Lbl;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.ActionRetractFact;

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