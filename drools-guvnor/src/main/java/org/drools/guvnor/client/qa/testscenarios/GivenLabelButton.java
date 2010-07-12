package org.drools.guvnor.client.qa.testscenarios;

import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * 
 * @author rikkola
 *
 */
public class GivenLabelButton extends HorizontalPanel {

    private Constants constants = ((Constants) GWT.create( Constants.class ));

    public GivenLabelButton(ExecutionTrace previousEx,
                      Scenario scenario,
                      ExecutionTrace executionTrace,
                      ScenarioWidget scenarioWidget) {

        add( new NewDataButton( previousEx,
                                scenario,
                                executionTrace,
                                scenarioWidget ) );
        add( new SmallLabel( constants.GIVEN() ) );

    }

}
