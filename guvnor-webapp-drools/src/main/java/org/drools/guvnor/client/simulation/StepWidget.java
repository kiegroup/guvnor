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
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.client.simulation.resources.SimulationStyle;
import org.drools.guvnor.shared.simulation.SimulationStepModel;

public class StepWidget extends Composite {

    protected interface StepWidgetBinder extends UiBinder<Widget, StepWidget> {}
    private static StepWidgetBinder uiBinder = GWT.create(StepWidgetBinder.class);

    private SimulationResources simulationResources = SimulationResources.INSTANCE;
    private SimulationStyle simulationStyle = SimulationResources.INSTANCE.style();

    @UiField
    protected VerticalPanel verticalPanel;

    private SimulationStepModel step;

    public StepWidget(SimulationStepModel step) {
        this.step = step;
        initWidget(uiBinder.createAndBindUi(this));

        // TODO mock code
        int r = 1 + Random.nextInt(4);
        for (int j = 0; j < r; j++) {
            verticalPanel.add(new TextBox());
        }
    }

}
