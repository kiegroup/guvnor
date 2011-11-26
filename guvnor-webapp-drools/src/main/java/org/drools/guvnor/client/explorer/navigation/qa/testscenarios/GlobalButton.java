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

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

class GlobalButton extends ImageButton {

    private static Constants                 constants = GWT.create( Constants.class );
    private static Images                    images    = GWT.create( Images.class );

    private final Scenario                   scenario;
    private final ScenarioWidget             parent;

    private final SuggestionCompletionEngine suggestionCompletionEngine;

    public GlobalButton(final Scenario scenario,
                        final ScenarioWidget parent) {
        super( images.newItem(),
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

        final ListBox  factTypes;
        private Button addButton;
        private Widget warning;

        public NewGlobalPopup() {
            super( images.ruleAsset(),
                   constants.NewGlobal() );

            factTypes = new ListBox();
            addButton = new AddButton();
            warning = getMissingGlobalsWarning();

            fillFactTypes();

            addRow( warning );

            addAttribute( constants.GlobalColon(),
                          getHorizontalPanel() );
        }

        private HorizontalPanel getHorizontalPanel() {
            HorizontalPanel insertFact = new HorizontalPanel();
            insertFact.add( factTypes );
            insertFact.add( addButton );
            return insertFact;
        }

        private void fillFactTypes() {
            if ( suggestionCompletionEngine.getGlobalVariables().length == 0 ) {
                addButton.setEnabled( false );
                factTypes.setEnabled( false );
                warning.setVisible( true );
            } else {
                addButton.setEnabled( true );
                factTypes.setEnabled( true );
                warning.setVisible( false );
                for ( String globals : suggestionCompletionEngine.getGlobalVariables() ) {
                    factTypes.addItem( globals );
                }
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
                        if ( scenario.isFactNameReserved( text ) ) {
                            Window.alert( constants.TheName0IsAlreadyInUsePleaseChooseAnotherName( text ) );
                        } else {
                            FactData factData = new FactData( suggestionCompletionEngine.getGlobalVariable( text ),
                                                              text,
                                                              false );
                            scenario.getGlobals().add( factData );
                            parent.renderEditor();

                            hide();
                        }
                    }
                } );
            }
        }

    }

    //A simple banner to alert users that no Globals have been defined
    private Widget getMissingGlobalsWarning() {
        HTML warning = new HTML( constants.missingGlobalsWarning() );
        warning.getElement().setClassName( "missingGlobalsWarning" );
        return warning;
    }
}
