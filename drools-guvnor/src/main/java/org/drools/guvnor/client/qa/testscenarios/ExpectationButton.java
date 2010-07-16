/**
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

package org.drools.guvnor.client.qa.testscenarios;

import java.util.ArrayList;

import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author rikkola
 *
 */
public class ExpectationButton extends TestScenarioButton {

    private final String packageName;

    public ExpectationButton(final String packageName,
                             final ExecutionTrace previousEx,
                             final Scenario scenario,
                             ScenarioWidget scenarioWidget) {
        super( "images/new_item.gif",
               constants.AddANewExpectation(),
               previousEx,
               scenario,
               scenarioWidget );

        this.packageName = packageName;
    }

    @Override
    protected TestScenarioButtonPopup getPopUp() {
        return new NewExpectationPopup();
    }

    class NewExpectationPopup extends TestScenarioButtonPopup {
        public NewExpectationPopup() {
            super( "images/rule_asset.gif",
                   constants.NewExpectation() );

            Widget selectRule = parent.getRuleSelectionWidget( packageName,
                                                               new RuleSelectionEvent() {

                                                                   public void ruleSelected(String name) {
                                                                       VerifyRuleFired verifyRuleFired = new VerifyRuleFired( name,
                                                                                                                              null,
                                                                                                                              new Boolean( true ) );
                                                                       scenario.insertBetween( previousEx,
                                                                                               verifyRuleFired );
                                                                       parent.renderEditor();
                                                                       hide();
                                                                   }
                                                               } );

            addAttribute( constants.Rule(),
                          selectRule );

            addAttribute( constants.FactValue(),
                          new FactsPanel() );

            //add in list box for anon facts
            addAttribute( constants.AnyFactThatMatches(),
                          new AnyFactThatMatchesPanel() );

        }

        class AnyFactThatMatchesPanel extends ListBoxBasePanel {
            public AnyFactThatMatchesPanel() {
                super( suggestionCompletionEngine.getFactTypes() );
            }

            @Override
            public Fixture getFixture() {
                String factName = valueWidget.getItemText( valueWidget.getSelectedIndex() );
                return new VerifyFact( factName,
                                       new ArrayList<VerifyField>(),
                                       true );
            }
        }

        class FactsPanel extends ListBoxBasePanel {

            public FactsPanel() {
                super( scenario.getFactNamesInScope( previousEx,
                                                     true ) );
            }

            @Override
            public Fixture getFixture() {
                String factName = valueWidget.getItemText( valueWidget.getSelectedIndex() );
                return new VerifyFact( factName,
                                       new ArrayList<VerifyField>() );
            }

        }
    }
}
