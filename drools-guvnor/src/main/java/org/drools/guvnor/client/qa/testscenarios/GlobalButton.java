package org.drools.guvnor.client.qa.testscenarios;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.gwtext.client.util.Format;

/**
 * 
 * @author rikkola
 *
 */
class GlobalButton extends ImageButton {

    private static Constants                 constants = ((Constants) GWT.create( Constants.class ));

    private final Scenario                   scenario;
    private final ScenarioWidget             parent;

    private final SuggestionCompletionEngine suggestionCompletionEngine;

    public GlobalButton(final Scenario scenario,
                        final ScenarioWidget parent) {
        super( "images/new_item.gif",
               constants.AddANewGlobalToThisScenario() );

        this.scenario = scenario;
        this.parent = parent;
        this.suggestionCompletionEngine = parent.suggestionCompletionEngine;

        addGlobalButtonClickHandler();
    }

    private void addGlobalButtonClickHandler() {
        addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                final FormStylePopup popup = new NewGlobalPopup();
                popup.show();
            }
        } );
    }

    class NewGlobalPopup extends FormStylePopup {

        final ListBox factTypes;

        public NewGlobalPopup() {
            super( "images/rule_asset.gif",
                   constants.NewGlobal() );

            factTypes = new ListBox();
            fillFactTypes();

            addAttribute( constants.GlobalColon(),
                          getHorizontalPanel() );
        }

        private HorizontalPanel getHorizontalPanel() {
            HorizontalPanel insertFact = new HorizontalPanel();
            insertFact.add( factTypes );
            insertFact.add( new AddButton() );
            return insertFact;
        }

        private void fillFactTypes() {
            for ( String globals : suggestionCompletionEngine.getGlobalVariables() ) {
                factTypes.addItem( globals );
            }
        }

        class AddButton extends Button {

            public AddButton() {
                super( constants.Add() );
                addAddClickHandler();
            }

            private void addAddClickHandler() {
                addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        String text = factTypes.getItemText( factTypes.getSelectedIndex() );
                        if ( scenario.isFactNameExisting( text ) ) {
                            Window.alert( Format.format( constants.TheName0IsAlreadyInUsePleaseChooseAnotherName(),
                                                         text ) );
                        } else {
                            FactData factData = new FactData( suggestionCompletionEngine.getGlobalVariable( text ),
                                                              text,
                                                              false );
                            scenario.globals.add( factData );
                            parent.renderEditor();

                            hide();
                        }
                    }
                } );
            }
        }

    }
}
