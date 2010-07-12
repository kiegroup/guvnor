package org.drools.guvnor.client.qa.testscenarios;

import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * 
 * @author rikkola
 *
 */
public class AddExecuteButton extends Button {

    private final static Constants constants = ((Constants) GWT.create( Constants.class ));

    public AddExecuteButton(final Scenario scenario,
                            final ScenarioWidget parent) {
        super( constants.MoreDotDot() );

        setTitle( constants.AddAnotherSectionOfDataAndExpectations() );

        addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                scenario.fixtures.add( new ExecutionTrace() );
                parent.renderEditor();
            }
        } );
    }
}
