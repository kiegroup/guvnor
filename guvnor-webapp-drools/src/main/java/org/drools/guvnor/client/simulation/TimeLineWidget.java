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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.client.simulation.resources.SimulationStyle;
import org.drools.guvnor.shared.simulation.SimulationModel;
import org.drools.guvnor.shared.simulation.SimulationPathModel;
import org.drools.guvnor.shared.simulation.SimulationStepModel;
import org.drools.guvnor.shared.simulation.SimulationTestUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimeLineWidget extends Composite {

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

    protected interface TimeLineWidgetBinder extends UiBinder<Widget, TimeLineWidget> {}
    private static TimeLineWidgetBinder uiBinder = GWT.create(TimeLineWidgetBinder.class);

    private final SimulationTestEventHandler simulationTestEventHandler;

    @UiField
    protected SimulationResources simulationResources = SimulationResources.INSTANCE;
    protected SimulationStyle simulationStyle = SimulationResources.INSTANCE.style();

    @UiField
    protected PushButton zoomInButton;
    @UiField
    protected PushButton zoomOutButton;
    @UiField
    protected ScrollPanel timeLineScrollPanel;
    @UiField
    protected LayoutPanel timeLineContent;
    @UiField
    protected FlowPanel addStepsPanel;

    private SimulationModel simulation;
    private int contentHeight;
    private int contentWidth;

    private double millisecondsPerPixel;
    private Map<Long, VerticalPanel> timeStoneMap = null;
    private Map<SimulationStepModel, Image> stepMap = null;

    public TimeLineWidget(SimulationTestEventHandler simulationTestEventHandler) {
        this.simulationTestEventHandler = simulationTestEventHandler;
        initWidget(uiBinder.createAndBindUi(this));
        contentHeight = simulationStyle.timeLineHeaderHeight() + simulationStyle.timeLineFooterHeight();
        timeLineScrollPanel.addScrollHandler(new ScrollHandler() {
            public void onScroll(ScrollEvent event) {
                refreshTimeLineContent();
            }
        });
        if (simulationResources.timeStone().getHeight() != simulationStyle.timeLinePathHeight()) {
            throw new IllegalStateException("The timeStone image height (" + simulationResources.timeStone().getHeight()
                    + ") must be equal to the PATH_HEIGHT (" + simulationStyle.timeLinePathHeight() + ").");
        }
    }

    public void setSimulation(SimulationModel simulation) {
        this.simulation = simulation;
        clearMaps();
        timeLineScrollPanel.setWidth(simulationStyle.timeLineScrollPanelWidth() + "px");
        contentHeight = simulationStyle.timeLineHeaderHeight()
                + (simulation.getPathsSize() * simulationStyle.timeLinePathHeight())
                + simulationStyle.timeLineFooterHeight();
        timeLineContent.setHeight(contentHeight + "px");
        addStepsPanel.setHeight(contentHeight + "px");
        addStepsPanel.addStyleName(simulationStyle.addStepsPanel());
        long maximumDistanceMillis = simulation.getMaximumDistanceMillis();
        millisecondsPerPixel = (double) maximumDistanceMillis /
                (simulationStyle.timeLineScrollPanelWidth() - simulationStyle.timeLineMarginWidth() * 2);
        adjustContentWidth(maximumDistanceMillis);
        refreshTimeLineContent(0, simulationStyle.timeLineScrollPanelWidth());
        refreshAddStepsPanel();
    }

    private void clearMaps() {
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
    }

    private void refreshAddStepsPanel() {
        Label addStepLabel = new Label("");
        addStepLabel.addStyleName(simulationStyle.addStepHeader());
        addStepsPanel.add(addStepLabel);
        for (SimulationPathModel path : simulation.getPaths().values()) {
            FlowPanel heightLimitingPanel = new FlowPanel();
            heightLimitingPanel.setHeight(simulationStyle.timeLinePathHeight() + "px");
            PushButton addStepButton = createAddStepButton(path);
            heightLimitingPanel.add(addStepButton);
            addStepsPanel.add(heightLimitingPanel);
        }
    }

    private PushButton createAddStepButton(final SimulationPathModel path) {
        PushButton addStepButton = new PushButton(new Image(simulationResources.addStep()));
        addStepButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                simulationTestEventHandler.addStep(path);
            }
        });
        return addStepButton;
    }

    public void addedStep(SimulationStepModel step) {
        adjustContentWidth(simulation.getMaximumDistanceMillis());
        scrollToDistanceMillis(step.getDistanceMillis());
    }

    public void removedStep(SimulationStepModel step) {
        Image stepWidget = stepMap.remove(step);
        if (stepWidget != null) {
            timeLineContent.remove(stepWidget);
        }
        adjustContentWidth(simulation.getMaximumDistanceMillis());
        scrollToDistanceMillis(step.getDistanceMillis());
    }

    public void scrollToDistanceMillis(long distanceMillis) {
        int x = calculateX(distanceMillis);
        int clientWidth = timeLineScrollPanel.getElement().getClientWidth();
        int scrollLeft = adjustScrollLeft(x - (clientWidth / 2), clientWidth);
        refreshTimeLineContent(scrollLeft, clientWidth);
    }

    private int adjustScrollLeft(int scrollLeft, int clientWidth) {
        scrollLeft = Math.max(0, scrollLeft);
        scrollLeft = Math.min(contentWidth - clientWidth, scrollLeft);
        timeLineScrollPanel.getElement().setScrollLeft(scrollLeft);
        return scrollLeft;
    }

    @UiHandler("zoomInButton")
    protected void zoomIn(ClickEvent event) {
        zoomIn();
    }

    public void zoomIn() {
        int scrollLeft = timeLineScrollPanel.getElement().getScrollLeft();
        int clientWidth = timeLineScrollPanel.getElement().getClientWidth();
        long distanceMillis = calculateDistanceMillis(scrollLeft, clientWidth);
        long maximumDistanceMillis = simulation.getMaximumDistanceMillis();
        millisecondsPerPixel = Math.max(1.0, millisecondsPerPixel / 2.0);
        adjustContentWidth(maximumDistanceMillis);
        scrollLeft = adjustScrollLeft(calculateX(distanceMillis, clientWidth), clientWidth);
        timeLineScrollPanel.getElement().setScrollLeft(scrollLeft);
        refreshTimeLineContent(scrollLeft, clientWidth);
    }

    @UiHandler("zoomOutButton")
    protected void zoomOut(ClickEvent event) {
        zoomOut();
    }

    public void zoomOut() {
        int scrollLeft = timeLineScrollPanel.getElement().getScrollLeft();
        int clientWidth = timeLineScrollPanel.getElement().getClientWidth();
        long distanceMillis = calculateDistanceMillis(scrollLeft, clientWidth);
        long maximumDistanceMillis = simulation.getMaximumDistanceMillis();
        double maximumMillisecondsPerPixel = (double) maximumDistanceMillis /
                (clientWidth - simulationStyle.timeLineMarginWidth() * 2);
        millisecondsPerPixel = Math.min(maximumMillisecondsPerPixel, millisecondsPerPixel * 2.0);
        adjustContentWidth(maximumDistanceMillis);
        scrollLeft = adjustScrollLeft(calculateX(distanceMillis, clientWidth), clientWidth);
        timeLineScrollPanel.getElement().setScrollLeft(scrollLeft);
        refreshTimeLineContent(scrollLeft, clientWidth);
    }

    private void adjustContentWidth(long maximumDistanceMillis) {
        contentWidth = (int) (maximumDistanceMillis / millisecondsPerPixel)
                + (simulationStyle.timeLineMarginWidth() * 2);
        timeLineContent.setWidth(contentWidth + "px");
    }

    private void refreshTimeLineContent() {
        int scrollLeft = timeLineScrollPanel.getElement().getScrollLeft();
        int clientWidth = timeLineScrollPanel.getElement().getClientWidth();
        refreshTimeLineContent(scrollLeft, clientWidth);
    }

    private void refreshTimeLineContent(int scrollLeft, int clientWidth) {
        refreshTimeStones(scrollLeft, clientWidth);
        refreshSteps(scrollLeft, clientWidth);
    }

    private void refreshTimeStones(int scrollLeft, int clientWidth) {
        long maximumDistanceMillis = simulation.getMaximumDistanceMillis();
        int itemWidth = simulationResources.timeStone().getWidth();
        long timeStoneIncrement = calculateTimeStoneIncrement();
        for (Iterator<Map.Entry<Long, VerticalPanel>> it = timeStoneMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, VerticalPanel> timeStoneEntry = it.next();
            long timeStoneValue = timeStoneEntry.getKey();
            int x = calculateX(timeStoneValue, itemWidth);
            if (!isWithinViewportBounds(scrollLeft, clientWidth, x, itemWidth)
                    || (timeStoneValue % timeStoneIncrement) != 0) {
                // Remove timestone
                timeLineContent.remove(timeStoneEntry.getValue());
                it.remove();
            }
        }
        for (long timeStoneValue = 0L; timeStoneValue <= maximumDistanceMillis; timeStoneValue += timeStoneIncrement) {
            int x = calculateX(timeStoneValue, itemWidth);
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
        timeStonePanel.addStyleName(simulationStyle.timeStonePanel());
        Label timeStoneLabel = new Label(SimulationTestUtils.formatMillis(timeStoneValue));
        timeStoneLabel.addStyleName(simulationStyle.timeStoneLabel());
        timeStonePanel.add(timeStoneLabel);
        int pathTop = simulationStyle.timeLineHeaderHeight();
        for (SimulationPathModel path : simulation.getPaths().values()) {
            Image timeStoneImage = new Image(simulationResources.timeStone());
            timeStonePanel.add(timeStoneImage);
            pathTop += simulationStyle.timeLinePathHeight();
        }
        return timeStonePanel;
    }

    private void refreshSteps(int scrollLeft, int clientWidth) {
        int itemWidth = simulationResources.stepEmpty().getWidth();
        for (Iterator<Map.Entry<SimulationStepModel, Image>> it = stepMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<SimulationStepModel, Image> stepEntry = it.next();
            long distanceMillis = stepEntry.getKey().getDistanceMillis();
            int x = calculateX(distanceMillis, itemWidth);
            if (!isWithinViewportBounds(scrollLeft, clientWidth, x, itemWidth)) {
                // Remove step
                timeLineContent.remove(stepEntry.getValue());
                it.remove();
            }
        }
        int pathTop = simulationStyle.timeLineHeaderHeight();
        for (SimulationPathModel path : simulation.getPaths().values()) {
            for (SimulationStepModel step : path.getSteps().values()) {
                int x = calculateX(step.getDistanceMillis(), itemWidth);
                if (!stepMap.containsKey(step)) {
                    // Add new step
                    if (isWithinViewportBounds(scrollLeft, clientWidth, x, itemWidth)) {
                        Image stepWidget = createStepWidget(path, step);
                        timeLineContent.add(stepWidget);
                        timeLineContent.setWidgetLeftWidth(stepWidget,
                                x, Style.Unit.PX,
                                stepWidget.getWidth(), Style.Unit.PX);
                        timeLineContent.setWidgetTopHeight(stepWidget,
                                pathTop + (simulationStyle.timeLinePathHeight() - stepWidget.getHeight()) / 2,
                                Style.Unit.PX,
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
            pathTop += simulationStyle.timeLinePathHeight();
        }
    }

    private Image createStepWidget(SimulationPathModel path, final SimulationStepModel step) {
        ImageResource imageResource = simulationResources.stepEmpty();
        final Image image = new Image(imageResource);
        final PopupPanel popupPanel = new PopupPanel(true);
        popupPanel.setWidget(new Label(path.getName()
                + " @ " + SimulationTestUtils.formatMillis(step.getDistanceMillis())));
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
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                simulationTestEventHandler.selectStep(step);
            }
        });
        return image;
    }

    private int calculateX(long distanceMillis, int itemWidth) {
        return calculateX(distanceMillis) - (itemWidth / 2);
    }

    private int calculateX(long distanceMillis) {
        return simulationStyle.timeLineMarginWidth() + (int) (distanceMillis / millisecondsPerPixel);
    }

    private long calculateDistanceMillis(int x, int itemWidth) {
        return calculateDistanceMillis(x + (itemWidth / 2));
    }

    private long calculateDistanceMillis(int x) {
        return (long) ((x - simulationStyle.timeLineMarginWidth()) * millisecondsPerPixel);
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

}
