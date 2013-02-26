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

package org.kie.guvnor.testscenario.client;

import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.explorer.navigation.qa.VerifyFactWidget;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.commons.ui.client.resources.CommonAltedImages;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.testscenario.model.ExecutionTrace;
import org.kie.guvnor.testscenario.model.Fixture;
import org.kie.guvnor.testscenario.model.FixtureList;
import org.kie.guvnor.testscenario.model.Scenario;
import org.kie.guvnor.testscenario.model.VerifyFact;
import org.uberfire.client.common.ImageButton;

public class VerifyFactsPanel extends VerticalPanel {

    private final Scenario scenario;

    private final ScenarioEditorPresenter parent;

    public VerifyFactsPanel(FixtureList verifyFacts,
                            ExecutionTrace executionTrace,
                            final Scenario scenario,
                            ScenarioEditorPresenter scenarioWidget,
                            boolean showResults) {

        this.scenario = scenario;
        this.parent = scenarioWidget;

        DataModelOracle dmo = scenarioWidget.dmo;

        for ( Fixture fixture : verifyFacts ) {
            if ( fixture instanceof VerifyFact ) {
                VerifyFact verifyFact = (VerifyFact) fixture;

                HorizontalPanel column = new HorizontalPanel();
                column.add( new VerifyFactWidget( verifyFact,
                                                  scenario,
                                                  dmo,
                                                  executionTrace,
                                                  showResults ) );

                column.add( new DeleteButton( verifyFact ) );

                add( column );
            }
        }
    }

    class DeleteButton extends ImageButton {
        public DeleteButton(final VerifyFact verifyFact) {
            super(CommonAltedImages.INSTANCE.DeleteItemSmall(),
                   TestScenarioConstants.INSTANCE.DeleteTheExpectationForThisFact() );

            addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    if ( Window.confirm( TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveThisExpectation() ) ) {
                        scenario.removeFixture( verifyFact );
                        parent.renderEditor();
                    }
                }
            } );
        }
    }

}
