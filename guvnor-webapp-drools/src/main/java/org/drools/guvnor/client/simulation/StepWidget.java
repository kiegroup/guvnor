/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.simulation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.simulation.command.AbstractCommandWidget;
import org.drools.guvnor.client.simulation.command.AssertBulkDataCommandWidget;
import org.drools.guvnor.client.simulation.command.FireAllRulesCommandWidget;
import org.drools.guvnor.client.simulation.command.InsertBulkDataCommandWidget;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.client.simulation.resources.SimulationStyle;
import org.drools.guvnor.shared.simulation.SimulationStepModel;
import org.drools.guvnor.shared.simulation.command.AbstractCommandModel;
import org.drools.guvnor.shared.simulation.command.AssertBulkDataCommandModel;
import org.drools.guvnor.shared.simulation.command.FireAllRulesCommandModel;
import org.drools.guvnor.shared.simulation.command.InsertBulkDataCommandModel;

public class StepWidget extends Composite {

    protected interface StepWidgetBinder extends UiBinder<Widget, StepWidget> {}
    private static StepWidgetBinder uiBinder = GWT.create(StepWidgetBinder.class);

    @UiField
    protected VerticalPanel verticalPanel;

    private final SimulationStepModel step;
    private final SimulationTestEventHandler simulationTestEventHandler;

    public StepWidget(SimulationStepModel step, SimulationTestEventHandler simulationTestEventHandler) {
        this.step = step;
        this.simulationTestEventHandler = simulationTestEventHandler;
        initWidget(uiBinder.createAndBindUi(this));
        for (AbstractCommandModel command : step.getCommands()) {
            AbstractCommandWidget commandWidget = buildCommandWidget(command);
            verticalPanel.add(commandWidget);
        }
    }

    private AbstractCommandWidget buildCommandWidget(AbstractCommandModel command) {
        if (command instanceof InsertBulkDataCommandModel) {
            return new InsertBulkDataCommandWidget((InsertBulkDataCommandModel) command);
        } else if (command instanceof FireAllRulesCommandModel) {
            return new FireAllRulesCommandWidget((FireAllRulesCommandModel) command);
        } else if (command instanceof AssertBulkDataCommandModel) {
            return new AssertBulkDataCommandWidget((AssertBulkDataCommandModel) command);
        } else {
            throw new IllegalArgumentException("The AbstractCommandModel (" + command.getClass()
                    + ") is not supported yet as a CommandWidget.");
        }
    }

}
