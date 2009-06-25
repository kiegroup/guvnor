package org.drools.guvnor.client.modeldriven.ui;
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



import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ActionRetractFact;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * This is used when you want to retract a fact. It will provide a list of
 * bound facts for you to retract.
 * @author Michael Neale
 */
public class ActionRetractFactWidget extends Composite {

    private FlexTable layout;


    public ActionRetractFactWidget(SuggestionCompletionEngine com, ActionRetractFact model, RuleModel ruleModel) {
        layout = new FlexTable();

        layout.setStyleName( "model-builderInner-Background" );

        layout.setWidget( 0, 0, new SmallLabel(HumanReadable.getActionDisplayName( "retract" ))  );

        String desc = ruleModel.getBoundFact(model.variableName).factType + " [" + model.variableName + "]";
        layout.setWidget( 0, 1, new SmallLabel("<b>"  + desc  + "</b>") );

        initWidget( layout );
    }


}