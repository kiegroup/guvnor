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
