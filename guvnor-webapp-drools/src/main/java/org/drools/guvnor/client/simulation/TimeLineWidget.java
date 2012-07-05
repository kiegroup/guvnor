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
import com.google.gwt.user.client.ui.VerticalPanel;
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

    private SimulationResources simulationResources = SimulationResources.INSTANCE;

    private ScrollPanel timeLineScrollPanel;
    private LayoutPanel timeLineContent;

    private SimulationModel simulation;
    private int contentHeight;

    private double millisecondsPerPixel;
    private Map<Long, VerticalPanel> timeStoneMap = null;
    private Map<SimulationStepModel, Image> stepMap = null;

    public TimeLineWidget() {
        contentHeight = HEADER_HEIGHT + FOOTER_HEIGHT;
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
        contentHeight = HEADER_HEIGHT + (simulation.getPathsSize() * PATH_HEIGHT) + FOOTER_HEIGHT;
        timeLineContent.setHeight(contentHeight + "px");
        millisecondsPerPixel = 10.0;
        if (timeStoneMap != null) {
            for (VerticalPanel timeStonePanel : timeStoneMap.values()) {
                timeLineContent.remove(timeStonePanel);
            }
        }
        timeStoneMap = new LinkedHashMap<Long, VerticalPanel>();
        if (stepMap != null) {
            for (Image stepWidget : stepMap.values()) {
                timeLineContent.remove(stepWidget);
            }
        }
        stepMap = new LinkedHashMap<SimulationStepModel, Image>();
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

        refreshTimeStones(scrollLeft, clientWidth, maximumDistanceMillis);
        refreshSteps(scrollLeft, clientWidth);
    }

    private void refreshTimeStones(int scrollLeft, int clientWidth, long maximumDistanceMillis) {
        int itemWidth = simulationResources.timeStone().getWidth();
        long timeStoneIncrement = calculateTimeStoneIncrement();
        for (Iterator<Map.Entry<Long, VerticalPanel>> it = timeStoneMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, VerticalPanel> timeStoneEntry = it.next();
            long timeStoneValue = timeStoneEntry.getKey();
            double x = calculateX(timeStoneValue, itemWidth);
            if (!isWithinViewportBounds(scrollLeft, clientWidth, x, itemWidth)
                    || (timeStoneValue % timeStoneIncrement) != 0) {
                // Remove timestone
                timeLineContent.remove(timeStoneEntry.getValue());
                it.remove();
            }
        }
        for (long timeStoneValue = 0L; timeStoneValue <= maximumDistanceMillis; timeStoneValue += timeStoneIncrement) {
            double x = calculateX(timeStoneValue, itemWidth);
            if (!timeStoneMap.containsKey(timeStoneValue)) {
                // Add new timestone
                if (isWithinViewportBounds(scrollLeft, clientWidth, x, itemWidth)) {
                    VerticalPanel timeStonePanel = createTimeStonePanel(timeStoneValue);
                    timeLineContent.add(timeStonePanel);
                    timeLineContent.setWidgetLeftWidth(timeStonePanel,
                            x, Style.Unit.PX, TIME_STONE_THRESHOLD_IN_PIXELS, Style.Unit.PX);
                    timeLineContent.setWidgetTopHeight(timeStonePanel,
                            0, Style.Unit.PX, contentHeight, Style.Unit.PX);
                    timeStoneMap.put(timeStoneValue, timeStonePanel);
                }
            } else {
                // Adjust existing timestone
                VerticalPanel timeStonePanel = timeStoneMap.get(timeStoneValue);
                timeLineContent.setWidgetLeftWidth(timeStonePanel,
                        x, Style.Unit.PX, TIME_STONE_THRESHOLD_IN_PIXELS, Style.Unit.PX);
            }
        }
    }

    private VerticalPanel createTimeStonePanel(long timeStoneValue) {
        VerticalPanel timeStonePanel = new VerticalPanel();
        Label timeStoneLabel = new Label(formatTimeStoneValue(timeStoneValue));
        timeStoneLabel.setStyleName(simulationResources.style().timeStoneLabel());
        timeStonePanel.add(timeStoneLabel);
        int pathTop = HEADER_HEIGHT;
        for (SimulationPathModel path : simulation.getPaths().values()) {
            Image timeStoneImage = new Image(simulationResources.timeStone());
            timeStonePanel.add(timeStoneImage);
            pathTop += PATH_HEIGHT;
        }
        return timeStonePanel;
    }

    private void refreshSteps(int scrollLeft, int clientWidth) {
        int itemWidth = simulationResources.stepEmpty().getWidth();
        for (Iterator<Map.Entry<SimulationStepModel, Image>> it = stepMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<SimulationStepModel, Image> stepEntry = it.next();
            long distanceMillis = stepEntry.getKey().getDistanceMillis();
            double x = calculateX(distanceMillis, itemWidth);
            if (!isWithinViewportBounds(scrollLeft, clientWidth, x, itemWidth)) {
                // Remove step
                timeLineContent.remove(stepEntry.getValue());
                it.remove();
            }
        }
        int pathTop = HEADER_HEIGHT;
        for (SimulationPathModel path : simulation.getPaths().values()) {
            for (SimulationStepModel step : path.getSteps().values()) {
                double x = calculateX(step.getDistanceMillis(), itemWidth);
                if (!stepMap.containsKey(step)) {
                    // Add new step
                    if (isWithinViewportBounds(scrollLeft, clientWidth, x, itemWidth)) {
                        Image stepWidget = createStepWidget(path, step);
                        timeLineContent.add(stepWidget);
                        timeLineContent.setWidgetLeftWidth(stepWidget,
                                x, Style.Unit.PX,
                                stepWidget.getWidth(), Style.Unit.PX);
                        timeLineContent.setWidgetTopHeight(stepWidget,
                                pathTop + (PATH_HEIGHT - stepWidget.getHeight()) / 2, Style.Unit.PX,
                                stepWidget.getHeight(), Style.Unit.PX);
                        stepMap.put(step, stepWidget);
                    }
                } else {
                    // Adjust existing step
                    Image stepWidget = stepMap.get(step);
                    timeLineContent.setWidgetLeftWidth(stepWidget,
                            x, Style.Unit.PX, stepWidget.getWidth(), Style.Unit.PX);
                }
            }
            pathTop += PATH_HEIGHT;
        }
    }

    private Image createStepWidget(SimulationPathModel path, SimulationStepModel step) {
        ImageResource imageResource = simulationResources.stepEmpty();
        final Image image = new Image(imageResource);
        final PopupPanel popupPanel = new PopupPanel(true);
        popupPanel.setWidget(new Label(path.getName()
                + " @ " + formatTimeStoneValue(step.getDistanceMillis())));
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
        return image;
    }

    private double calculateX(long distanceMillis, int itemWidth) {
        return calculateX(distanceMillis) - (itemWidth / 2.0);
    }

    private double calculateX(long distanceMillis) {
        return MARGIN_WIDTH + (distanceMillis / millisecondsPerPixel);
    }

    private boolean isWithinViewportBounds(int scrollLeft, int clientWidth, double x, int itemWidth) {
        return x + itemWidth > scrollLeft
                && x < scrollLeft + clientWidth;
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
