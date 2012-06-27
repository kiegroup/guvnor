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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
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

    private static final int PATH_HEIGHT = 30;

    private SimulationResources simulationResources = GWT.create(SimulationResources.class);

    private SimulationModel simulation;

    private LayoutPanel timeLineContent;

    private Map<SimulationStepModel, Image> stepMap = null;
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
            for (Image image : stepMap.values()) {
                timeLineContent.remove(image);
            }
        }
        stepMap = new LinkedHashMap<SimulationStepModel, Image>();
        int pathTop = 0;
        for (SimulationPathModel path : simulation.getPaths().values()) {
            for (SimulationStepModel step : path.getSteps().values()) {
                ImageResource imageResource = simulationResources.stepEmpty();
                final Image image = new Image(imageResource);
                final PopupPanel popupPanel = new PopupPanel(true);
                popupPanel.setWidget(new Label("Path " + path.getName() + ", step " + step.getDistanceMillis()));
                image.addMouseOverHandler(new MouseOverHandler() {
                    public void onMouseOver(MouseOverEvent event) {
                        popupPanel.showRelativeTo(image);
                    }
                });
                image.addMouseOutHandler(new MouseOutHandler() {
                    public void onMouseOut(MouseOutEvent event) {
                        popupPanel.hide();
                    }
                });
                popupPanel.setAutoHideOnHistoryEventsEnabled(true);
                timeLineContent.add(image);
                timeLineContent.setWidgetLeftWidth(image, step.getDistanceMillis() / millisecondsPerPixel,
                        Style.Unit.PX, image.getWidth(), Style.Unit.PX);
                timeLineContent.setWidgetTopHeight(image,
                        pathTop + (PATH_HEIGHT - image.getHeight()) / 2, Style.Unit.PX,
                        image.getHeight(), Style.Unit.PX);
                stepMap.put(step, image);
            }
            pathTop += PATH_HEIGHT;
        }
    }

    private void refreshTimeLineContent() {
        for (Map.Entry<SimulationStepModel, Image> stepEntry : stepMap.entrySet()) {
            SimulationStepModel step = stepEntry.getKey();
            Image image = stepEntry.getValue();
            timeLineContent.setWidgetLeftWidth(image, step.getDistanceMillis() / millisecondsPerPixel,
                    Style.Unit.PX, image.getOffsetWidth(), Style.Unit.PX);
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
