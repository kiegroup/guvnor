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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.client.simulation.resources.SimulationStyle;
import org.drools.guvnor.shared.simulation.SimulationPathModel;
import org.drools.guvnor.shared.simulation.SimulationStepModel;

public class PathTableWidget extends Composite {

    protected interface PathTableWidgetBinder extends UiBinder<Widget, PathTableWidget> {}
    private static PathTableWidgetBinder uiBinder = GWT.create(PathTableWidgetBinder.class);

    private SimulationResources simulationResources = SimulationResources.INSTANCE;
    private SimulationStyle simulationStyle = SimulationResources.INSTANCE.style();

    @UiField
    protected FlexTable flexTable;

    private SimulationPathModel path;

    public PathTableWidget(SimulationPathModel path) {
        this.path = path;
        initWidget(uiBinder.createAndBindUi(this));
        setWidth("100%");

        // TODO improve me
        int i = 0;
        for (SimulationStepModel step : path.getSteps().values()) {
            Label stepLabel = new Label(step.getDistanceMillis() + "ms");
            flexTable.setWidget(i, 0, stepLabel);
            VerticalPanel stepActions = new VerticalPanel();
            int r = 1 + Random.nextInt(4);
            for (int j = 0; j < r; j++) {
                stepActions.add(new TextBox());
            }
            flexTable.setWidget(i, 1, stepActions);
            i++;
        }
    }

}
