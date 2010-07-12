package org.drools.guvnor.client.qa.testscenarios;

import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.qa.VerifyFactWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author rikkola
 *
 */
public class VerifyFactsPanel extends VerticalPanel {

    private Constants            constants = ((Constants) GWT.create( Constants.class ));

    private final Scenario       scenario;

    private final ScenarioWidget parent;

    public VerifyFactsPanel(FixtureList verifyFacts,
                            ExecutionTrace executionTrace,
                            final Scenario scenario,
                            ScenarioWidget scenarioWidget,
                            boolean showResults) {

        this.scenario = scenario;
        this.parent = scenarioWidget;

        SuggestionCompletionEngine suggestionCompletionEngine = scenarioWidget.suggestionCompletionEngine;

        for ( Fixture fixture : verifyFacts ) {
            if ( fixture instanceof VerifyFact ) {
                VerifyFact verifyFact = (VerifyFact) fixture;

                HorizontalPanel column = new HorizontalPanel();
                column.add( new VerifyFactWidget( verifyFact,
                                                  scenario,
                                                  suggestionCompletionEngine,
                                                  executionTrace,
                                                  showResults ) );

                column.add( new DeleteButton( verifyFact ) );

                add( column );
            }
        }
    }

    class DeleteButton extends ImageButton {
        public DeleteButton(final VerifyFact verifyFact) {
            super( "images/delete_item_small.gif",
                   constants.DeleteTheExpectationForThisFact() );

            addClickHandler( new ClickHandler() { //NON-NLS

                public void onClick(ClickEvent event) {
                    if ( Window.confirm( constants.AreYouSureYouWantToRemoveThisExpectation() ) ) {
                        scenario.removeFixture( verifyFact );
                        parent.renderEditor();
                    }
                }
            } );
        }
    }

}
