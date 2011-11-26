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

import java.util.List;

import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.testing.CallMethod;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;

/**
 * 
 * This button gives a choice of modifying data, based on the positional
 * context.
 */
public class CallMethodOnNewDataButton extends TestScenarioButton {

    private static Images        images = GWT.create( Images.class );

    private final ExecutionTrace currentEx;

    public CallMethodOnNewDataButton(final ExecutionTrace previousEx,
                                     final Scenario scenario,
                                     final ExecutionTrace currentEx,
                                     ScenarioWidget scenarioWidget) {
        super( images.newItem(),
               constants.AddANewDataInputToThisScenario(),
               previousEx,
               scenario,
               scenarioWidget );

        this.currentEx = currentEx;
    }

    @Override
    protected TestScenarioButtonPopup getPopUp() {
        return new NewInputPopup();
    }

    class NewInputPopup extends TestScenarioButtonPopup {
        public NewInputPopup() {
            super( images.ruleAsset(),
                   constants.NewInput() );
            List<String> varsInScope = scenario.getFactNamesInScope( currentEx,
                                                                     false );
            // now we do modifies & retracts
            if ( varsInScope.size() > 0 ) {
                addAttribute( constants.CallAMethodOnAFactScenario(),
                              new CallMethodFactPanel( varsInScope ) );
            }
        }

        class CallMethodFactPanel extends ListBoxBasePanel {

            public CallMethodFactPanel(List<String> listItems) {
                super( listItems );
            }

            @Override
            public Fixture getFixture() {
                String factName = valueWidget.getItemText( valueWidget.getSelectedIndex() );
                return new CallMethod( factName );
            }
        }

    }

}
