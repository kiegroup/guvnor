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
