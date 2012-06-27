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
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.resources.decisiontable.DecisionTableResources;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.shared.simulation.SimulationModel;
import org.drools.guvnor.shared.simulation.SimulationPathModel;
import org.drools.guvnor.shared.simulation.SimulationStepModel;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimeLineWidget extends ResizeComposite {

    private static final int PATH_HEIGHT = 20;

    private SimulationModel simulation;

    private LayoutPanel timeLineContent;

    private Map<SimulationStepModel, PushButton> stepMap = null;
    private double millisecondsPerPixel;

    public TimeLineWidget() {
        timeLineContent = new LayoutPanel();
        timeLineContent.setWidth("100%");
        timeLineContent.setHeight(PATH_HEIGHT + "px");
        initWidget(timeLineContent);
    }

    public void setSimulation(SimulationModel simulation) {
        this.simulation = simulation;
        setHeight(simulation.getPaths().size() * PATH_HEIGHT + "px");
        millisecondsPerPixel = 10.0;
        if (stepMap != null) {
            for (PushButton pushButton : stepMap.values()) {
                timeLineContent.remove(pushButton);
            }
        }
        stepMap = new LinkedHashMap<SimulationStepModel, PushButton>();
        int pathTop = 0;
        for (SimulationPathModel path : simulation.getPaths().values()) {
            for (SimulationStepModel step : path.getSteps().values()) {
                PushButton pushButton = new PushButton(path.getName());
                timeLineContent.add(pushButton);
                timeLineContent.setWidgetLeftWidth(pushButton, step.getDistanceMillis() / millisecondsPerPixel,
                        Style.Unit.PX, 50, Style.Unit.PX);
                timeLineContent.setWidgetTopHeight(pushButton, pathTop, Style.Unit.PX,
                        PATH_HEIGHT, Style.Unit.PX);
                stepMap.put(step, pushButton);
            }
            pathTop += PATH_HEIGHT;
        }
    }

    private void refreshTimeLineContent() {
        for (Map.Entry<SimulationStepModel, PushButton> stepEntry : stepMap.entrySet()) {
            SimulationStepModel step = stepEntry.getKey();
            PushButton pushButton = stepEntry.getValue();
            timeLineContent.setWidgetLeftWidth(pushButton, step.getDistanceMillis() / millisecondsPerPixel,
                    Style.Unit.PX, 50, Style.Unit.PX);
        }
        timeLineContent.animate(500);
    }

    public void zoomIn() {
        millisecondsPerPixel = Math.max(1.0, millisecondsPerPixel / 2.0);
        refreshTimeLineContent();
    }

    public void zoomOut() {
        // TODO limit based on maximum panel width and widget width
        millisecondsPerPixel *= 2.0;
        refreshTimeLineContent();
    }

}
