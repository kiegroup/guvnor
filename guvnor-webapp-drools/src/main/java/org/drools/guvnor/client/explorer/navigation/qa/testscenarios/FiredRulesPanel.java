/*
 * Copyright 2010 JBoss Inc
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
package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

public class FiredRulesPanel extends HorizontalPanel {

    private final ExecutionTrace executionTrace;

    public FiredRulesPanel(ExecutionTrace executionTrace) {
        this.executionTrace = executionTrace;

        add( createText() );
        add( createShowButton() );
    }

    private HTML createText() {
        return new HTML( "<i><small>" + Constants.INSTANCE.property0RulesFiredIn1Ms(
                executionTrace.getNumberOfRulesFired(), executionTrace.getExecutionTimeResult() ) + "</small></i>" );
    }

    private Button createShowButton() {
        final Button show = new Button( Constants.INSTANCE.ShowRulesFired() );
        show.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                ListBox rules = new ListBox( true );
                for ( String ruleName : executionTrace.getRulesFired() ) {
                    rules.addItem( ruleName );
                }
                add( new SmallLabel( "&nbsp:" + Constants.INSTANCE.RulesFired() ) );
                add( rules );
                show.setVisible( false );
            }
        } );

        return show;
    }
}
