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

package org.kie.guvnor.testscenario.client;

import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.kie.guvnor.commons.ui.client.resources.CommonAltedImages;
import org.kie.guvnor.commons.ui.client.resources.ItemAltedImages;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.testscenario.service.model.ExecutionTrace;
import org.kie.guvnor.testscenario.service.model.Scenario;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.SmallLabel;

public class ExpectPanel extends HorizontalPanel {

    protected final Scenario scenario;
    protected final ScenarioEditorPresenter parent;
    protected final ExecutionTrace previousEx;

    public ExpectPanel(String packageName,
                       ExecutionTrace previousEx,
                       final Scenario scenario,
                       final ScenarioEditorPresenter parent) {
        this.scenario = scenario;
        this.parent = parent;
        this.previousEx = previousEx;

        add( new ExpectationButton( packageName,
                                    previousEx,
                                    scenario,
                                    parent ) );
        add( new SmallLabel( TestScenarioConstants.INSTANCE.EXPECT() ) );
        add( new DeleteButton() );
    }

    class DeleteButton
            extends ImageButton {

        public DeleteButton() {
            super(CommonAltedImages.INSTANCE.DeleteItemSmall(),
                   CommonConstants.INSTANCE.DeleteItem() );
            addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    if ( Window.confirm( TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveThisItem() ) ) {
                        scenario.removeExecutionTrace( previousEx );
                        parent.renderEditor();
                    }
                }
            } );
        }
    }
}
