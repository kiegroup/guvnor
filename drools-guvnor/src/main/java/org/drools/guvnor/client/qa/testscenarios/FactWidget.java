package org.drools.guvnor.client.qa.testscenarios;

import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
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
public abstract class FactWidget extends HorizontalPanel {

    protected static Constants     constants = ((Constants) GWT.create( Constants.class ));

    protected final ScenarioWidget parent;
    protected final Scenario       scenario;
    protected final FixtureList    definitionList;

    public FactWidget(String factType,
                      FixtureList definitionList,
                      Scenario scenario,
                      ScenarioWidget parent,
                      ExecutionTrace executionTrace,
                      String headerText) {
        this.parent = parent;
        this.scenario = scenario;
        this.definitionList = definitionList;

        add( new DataInputWidget( factType,
                                  definitionList,
                                  scenario,
                                  parent,
                                  executionTrace,
                                  headerText ) );
        add( new DeleteButton( definitionList ) );
    }

    protected void onDelete() {
        if ( Window.confirm( constants.AreYouSureYouWantToRemoveThisBlockOfData() ) ) {
            scenario.removeFixture( definitionList );
            parent.renderEditor();
        }
    }

    class DeleteButton extends ImageButton {
        public DeleteButton(final FixtureList definitionList) {
            super( "images/delete_item_small.gif",
                   constants.RemoveThisBlockOfData() );

            addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    onDelete();
                }
            } );
        }
    }
}
