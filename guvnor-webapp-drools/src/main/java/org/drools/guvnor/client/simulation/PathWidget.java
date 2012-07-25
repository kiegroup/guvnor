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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.shared.simulation.SimulationPathModel;
import org.drools.guvnor.shared.simulation.SimulationStepModel;

public class PathWidget extends Composite {

    protected interface PathWidgetBinder extends UiBinder<Widget, PathWidget> {}
    private static PathWidgetBinder uiBinder = GWT.create(PathWidgetBinder.class);

    @UiField
    protected FlexTable flexTable;
    @UiField
    protected PushButton addStepButton;

    private final SimulationPathModel path;
    private final SimulationTestEventHandler simulationTestEventHandler;

    public PathWidget(SimulationPathModel path, SimulationTestEventHandler simulationTestEventHandler) {
        this.path = path;
        this.simulationTestEventHandler = simulationTestEventHandler;
        initWidget(uiBinder.createAndBindUi(this));
        int stepIndex = 0;
        for (SimulationStepModel step : path.getSteps().values()) {
            addStepWidget(stepIndex, step);
            stepIndex++;
        }
    }

    private void addStepWidget(int stepIndex, SimulationStepModel step) {
        Label stepLabel = new Label(step.getDistanceMillis() + " ms");
        flexTable.setWidget(stepIndex, 0, stepLabel);
        StepWidget stepWidget = new StepWidget(step, simulationTestEventHandler);
        flexTable.setWidget(stepIndex, 1, stepWidget);
    }

    @UiHandler("addStepButton")
    protected void addStep(ClickEvent event) {
        simulationTestEventHandler.addStep(path);
    }

    public void addedStep(SimulationStepModel step) {
        addStepWidget(flexTable.getRowCount(), step);
    }

}
