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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.client.simulation.resources.SimulationStyle;
import org.drools.guvnor.shared.simulation.SimulationPathModel;
import org.drools.guvnor.shared.simulation.SimulationStepModel;
import org.drools.guvnor.shared.simulation.SimulationTestUtils;

import java.util.HashMap;
import java.util.Map;

public class PathWidget extends Composite {

    protected interface PathWidgetBinder extends UiBinder<Widget, PathWidget> {}
    private static PathWidgetBinder uiBinder = GWT.create(PathWidgetBinder.class);

    private final SimulationPathModel path;
    private final SimulationTestEventHandler simulationTestEventHandler;

    @UiField
    protected SimulationResources simulationResources = SimulationResources.INSTANCE;
    protected SimulationStyle simulationStyle = SimulationResources.INSTANCE.style();

    @UiField
    protected FlexTable flexTable;
    @UiField
    protected PushButton addStepButton;

    private Map<SimulationStepModel, Integer> stepRowIndexMap = new HashMap<SimulationStepModel, Integer>();

    public PathWidget(SimulationPathModel path, SimulationTestEventHandler simulationTestEventHandler) {
        this.path = path;
        this.simulationTestEventHandler = simulationTestEventHandler;
        initWidget(uiBinder.createAndBindUi(this));
        flexTable.getColumnFormatter().addStyleName(0, simulationStyle.distanceMillisColumn());
        flexTable.getColumnFormatter().addStyleName(1, simulationStyle.stepWidgetColumn());
        flexTable.getColumnFormatter().addStyleName(2, simulationStyle.removeStepColumn());
        int stepIndex = 0;
        for (SimulationStepModel step : path.getSteps().values()) {
            addStepWidget(stepIndex, step);
            stepIndex++;
        }
    }

    // TODO remove stepIndex parameter
    private void addStepWidget(int stepIndex, SimulationStepModel step) {
        Label distanceMillisLabel = new Label(SimulationTestUtils.formatMillis(step.getDistanceMillis()));
        flexTable.setWidget(stepIndex, 0, distanceMillisLabel);
        StepWidget stepWidget = new StepWidget(step, simulationTestEventHandler);
        flexTable.setWidget(stepIndex, 1, stepWidget);
        flexTable.setWidget(stepIndex, 2, createRemoveStepButton(step));
        stepRowIndexMap.put(step, stepIndex);
    }

    private PushButton createRemoveStepButton(final SimulationStepModel step) {
        PushButton removeStepButton = new PushButton(new Image(simulationResources.removeStep()));
        removeStepButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                simulationTestEventHandler.removeStep(step);
            }
        });
        return removeStepButton;
    }

    private void removeStepWidget(SimulationStepModel step) {
        int stepIndex = stepRowIndexMap.remove(step);
        flexTable.removeRow(stepIndex);
        for (Map.Entry<SimulationStepModel, Integer> entry : stepRowIndexMap.entrySet()) {
            int otherStepIndex = entry.getValue();
            if (otherStepIndex > stepIndex) {
                entry.setValue(otherStepIndex - 1);
            }
        }
    }

    @UiHandler("addStepButton")
    protected void addStep(ClickEvent event) {
        simulationTestEventHandler.addStep(path);
    }

    public void addedStep(SimulationStepModel step) {
        addStepWidget(flexTable.getRowCount(), step);
    }

    public void removedStep(SimulationStepModel step) {
        removeStepWidget(step);
    }

}
