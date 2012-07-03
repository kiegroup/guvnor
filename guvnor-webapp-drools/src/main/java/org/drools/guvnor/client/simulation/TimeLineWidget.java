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
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.shared.simulation.SimulationModel;
import org.drools.guvnor.shared.simulation.SimulationPathModel;
import org.drools.guvnor.shared.simulation.SimulationStepModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimeLineWidget extends ResizeComposite {

    private static final int HEADER_HEIGHT = 30;
    private static final int FOOTER_HEIGHT = 30; // Must have enough space for the scrollbar
    private static final int MARGIN_WIDTH = 20;
    private static final int PATH_HEIGHT = 30;
    // A timeStone is a milestone of time
    private static final int TIME_STONE_THRESHOLD_IN_PIXELS = 80;
    private static final long[] TIME_STONE_INCREMENT_OPTIONS = new long[]{
            100L, // 100ms
            500L, // 500ms
            1000L, // 1s
            5000L, // 5s
            10000L, // 10s
            30000L, // 30s
            60000L, // 1m
            300000L, // 5m
            600000L, // 10m
            3600000L, // 1h
            21600000L, // 6h
            43200000L, // 12h
            86400000L, // 1d
            432000000L, // 5d
    };

    private SimulationResources simulationResources = GWT.create(SimulationResources.class);

    private SimulationModel simulation;

    private ScrollPanel timeLineScrollPanel;
    private LayoutPanel timeLineContent;

    private double millisecondsPerPixel;

    public TimeLineWidget() {
        timeLineContent = new LayoutPanel();
        timeLineScrollPanel = new ScrollPanel(timeLineContent);
        timeLineScrollPanel.addScrollHandler(new ScrollHandler() {
            public void onScroll(ScrollEvent event) {
                refreshTimeLineContent();
            }
        });
        initWidget(timeLineScrollPanel);
    }

    public void setSimulation(SimulationModel simulation) {
        this.simulation = simulation;
        int scrollPanelWidth = 800;
        timeLineScrollPanel.setWidth(scrollPanelWidth + "px");
        timeLineContent.setHeight((HEADER_HEIGHT + (simulation.getPaths().size() * PATH_HEIGHT) + FOOTER_HEIGHT) + "px");
        millisecondsPerPixel = 10.0;
        if (simulationResources.timeStone().getHeight() != PATH_HEIGHT) {
            throw new IllegalStateException("The timeStone image height (" + simulationResources.timeStone().getHeight()
                    + ") must be equal to the PATH_HEIGHT (" + PATH_HEIGHT + ").");
        }
        refreshTimeLineContent(0, scrollPanelWidth);
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

    private void refreshTimeLineContent() {
        int scrollLeft = timeLineScrollPanel.getElement().getScrollLeft();
        int clientWidth = timeLineScrollPanel.getElement().getClientWidth();
        refreshTimeLineContent(scrollLeft, clientWidth);
    }

    private void refreshTimeLineContent(int scrollLeft, int clientWidth) {
        long maximumDistanceMillis = simulation.getMaximumDistanceMillis();
        double width = (((double) maximumDistanceMillis) / millisecondsPerPixel) + (MARGIN_WIDTH * 2);
        timeLineContent.setWidth(width + "px");
        // Remove all widgets
        for (Iterator<Widget> it = timeLineContent.iterator(); it.hasNext(); ) {
            it.next();
            it.remove();
        }

        // Add new widgets
        long timeStoneIncrement = calculateTimeStoneIncrement();
        for (long timeStoneValue = 0L; timeStoneValue <= maximumDistanceMillis; timeStoneValue += timeStoneIncrement) {
            double x = MARGIN_WIDTH + (((double) timeStoneValue) / millisecondsPerPixel);
            if (x >= scrollLeft && x < scrollLeft + clientWidth) {
                Label timeStoneLabel = new Label(formatTimeStoneValue(timeStoneValue));
                timeLineContent.add(timeStoneLabel);
                timeLineContent.setWidgetLeftWidth(timeStoneLabel,
                        x, Style.Unit.PX, TIME_STONE_THRESHOLD_IN_PIXELS, Style.Unit.PX);
                timeLineContent.setWidgetTopHeight(timeStoneLabel,
                        5, Style.Unit.PX, HEADER_HEIGHT - 10, Style.Unit.PX);
                int pathTop = HEADER_HEIGHT;
                for (SimulationPathModel path : simulation.getPaths().values()) {
                    Image timeStoneImage = new Image(simulationResources.timeStone());
                    timeLineContent.add(timeStoneImage);
                    timeLineContent.setWidgetLeftWidth(timeStoneImage,
                            x, Style.Unit.PX, timeStoneImage.getWidth(), Style.Unit.PX);
                    timeLineContent.setWidgetTopHeight(timeStoneImage,
                            pathTop, Style.Unit.PX, timeStoneImage.getHeight(), Style.Unit.PX);
                    pathTop += PATH_HEIGHT;
                }
            }
        }
        int pathTop = HEADER_HEIGHT;
        for (SimulationPathModel path : simulation.getPaths().values()) {
            for (SimulationStepModel step : path.getSteps().values()) {
                double x = MARGIN_WIDTH + (((double) step.getDistanceMillis()) / millisecondsPerPixel);
                if (x >= scrollLeft && x < scrollLeft + clientWidth) {
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
                    timeLineContent.setWidgetLeftWidth(image,
                            x, Style.Unit.PX,
                            image.getWidth(), Style.Unit.PX);
                    timeLineContent.setWidgetTopHeight(image,
                            pathTop + (PATH_HEIGHT - image.getHeight()) / 2, Style.Unit.PX,
                            image.getHeight(), Style.Unit.PX);
                }
            }
            pathTop += PATH_HEIGHT;
        }
    }

    private long calculateTimeStoneIncrement() {
        long timeStoneIncrement;
        int i = 0;
        do {
            timeStoneIncrement = TIME_STONE_INCREMENT_OPTIONS[i];
            i++;
        } while ((((double) timeStoneIncrement) / millisecondsPerPixel) < TIME_STONE_THRESHOLD_IN_PIXELS
                && i < TIME_STONE_INCREMENT_OPTIONS.length);
        return timeStoneIncrement;
    }

    private String formatTimeStoneValue(long timeStoneValue) {
        if (timeStoneValue == 0L) {
            return "0";
        }
        StringBuilder timeStoneString = new StringBuilder();
        long leftover = timeStoneValue;
        if (leftover >= 86400000L) {
            timeStoneString.append(leftover / 86400000L).append("d");
            leftover %= 86400000L;
        }
        if (leftover >= 3600000L) {
            timeStoneString.append(leftover / 3600000L).append("h");
            leftover %= 3600000L;
        }
        if (leftover >= 60000L) {
            timeStoneString.append(leftover / 60000L).append("m");
            leftover %= 60000L;
        }
        if (leftover >= 1000L) {
            timeStoneString.append(leftover / 1000L).append("s");
            leftover %= 1000L;
        }
        if (leftover >= 1L) {
            timeStoneString.append(leftover).append("ms");
        }
        return timeStoneString.toString();
    }

}
