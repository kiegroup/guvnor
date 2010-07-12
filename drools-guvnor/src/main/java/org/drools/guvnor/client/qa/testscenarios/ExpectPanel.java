package org.drools.guvnor.client.qa.testscenarios;

import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * 
 * @author rikkola
 *
 */
public class ExpectPanel extends HorizontalPanel {

    private Constants              constants = ((Constants) GWT.create( Constants.class ));

    protected final Scenario       scenario;
    protected final ScenarioWidget parent;
    protected final ExecutionTrace previousEx;

    public ExpectPanel(String packageName,
                       ExecutionTrace previousEx,
                       final Scenario scenario,
                       final ScenarioWidget parent) {
        this.scenario = scenario;
        this.parent = parent;
        this.previousEx = previousEx;

        add( new ExpectationButton( packageName,
                                    previousEx,
                                    scenario,
                                    parent ) );
        add( new SmallLabel( constants.EXPECT() ) );
        add( new DeleteButton() );
    }

    class DeleteButton extends ImageButton {
        public DeleteButton() {
            super( "images/delete_item_small.gif",
                   constants.DeleteItem() );
            addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    if ( Window.confirm( constants.AreYouSureYouWantToRemoveThisItem() ) ) {
                        scenario.removeExecutionTrace( previousEx );
                        parent.renderEditor();
                    }
                }
            } );
        }
    }
}
