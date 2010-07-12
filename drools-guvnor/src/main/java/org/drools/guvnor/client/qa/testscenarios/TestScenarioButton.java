package org.drools.guvnor.client.qa.testscenarios;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author rikkola
 *
 */
abstract class TestScenarioButton extends ImageButton {

    protected static Constants                 constants = ((Constants) GWT.create( Constants.class ));

    protected final Scenario                   scenario;
    protected final ScenarioWidget             parent;
    protected final SuggestionCompletionEngine suggestionCompletionEngine;
    protected final ExecutionTrace             previousEx;

    public TestScenarioButton(String img,
                              String tooltip,
                              final ExecutionTrace previousEx,
                              final Scenario scenario,
                              ScenarioWidget scenarioWidget) {
        super( img,
               tooltip );
        this.previousEx = previousEx;
        this.scenario = scenario;
        this.parent = scenarioWidget;
        this.suggestionCompletionEngine = scenarioWidget.suggestionCompletionEngine;

        addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                final FormStylePopup pop = getPopUp();
                pop.show();
            }
        } );
    }

    protected abstract TestScenarioButtonPopup getPopUp();

    protected abstract class TestScenarioButtonPopup extends FormStylePopup {
        public TestScenarioButtonPopup(String image,
                                       String text) {
            super( image,
                   text );
        }

        protected abstract class BasePanel<T extends Widget> extends HorizontalPanel {
            protected final T      valueWidget;
            protected final Button add = new Button( constants.Add() );

            public BasePanel() {
                valueWidget = getWidget();

                addAddButtonClickHandler();

                initWidgets();
            }

            protected void initWidgets() {
                add( valueWidget );
                add( add );
            }

            protected void addAddButtonClickHandler() {
                add.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {

                        scenario.insertBetween( previousEx,
                                                getFixture() );
                        parent.renderEditor();
                        hide();
                    }
                } );
            }

            public abstract T getWidget();

            public abstract Fixture getFixture();

        }

        protected abstract class ListBoxBasePanel extends BasePanel<ListBox> {

            public ListBoxBasePanel(List<String> listItems) {
                super();
                fillWidget( listItems );
            }

            public ListBoxBasePanel(String[] listItems) {
                super();
                List<String> list = new ArrayList<String>();
                for ( String string : listItems ) {
                    list.add( string );
                }
                fillWidget( list );
            }

            protected void fillWidget(List<String> listItems) {
                for ( String item : listItems ) {
                    valueWidget.addItem( item );
                }
            }

            @Override
            public ListBox getWidget() {
                return new ListBox();
            }
        }
    }

}
