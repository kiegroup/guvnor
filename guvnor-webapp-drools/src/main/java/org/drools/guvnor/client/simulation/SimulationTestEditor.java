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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.shared.simulation.SimulationModel;
import org.drools.guvnor.shared.simulation.SimulationPathModel;

public class SimulationTestEditor extends Composite
        implements EditorWidget {

    protected interface SimulationTestEditorBinder extends UiBinder<Widget, SimulationTestEditor> {}
    private static SimulationTestEditorBinder uiBinder = GWT.create(SimulationTestEditorBinder.class);

    @UiField
    protected PushButton runSimulationButton;

    @UiField
    protected PushButton debugSimulationButton;

    @UiField
    protected TabPanel pathTableTabPanel;

    @UiField
    protected PushButton zoomInButton;

    @UiField
    protected PushButton zoomOutButton;

    @UiField
    protected TimeLineWidget timeLineWidget;

    private final Asset asset;

    public SimulationTestEditor(Asset asset, RuleViewer ruleViewer, ClientFactory clientFactory, EventBus eventBus) {
        this( asset );
    }

    public SimulationTestEditor(Asset asset) {
        this.asset = asset;
        SimulationModel simulation = (SimulationModel) asset.getContent();
        initWidget(uiBinder.createAndBindUi(this));
        for (SimulationPathModel path : simulation.getPaths().values()) {
            PathTableWidget pathTableWidget = new PathTableWidget(path);
            pathTableTabPanel.add(pathTableWidget, path.getName());
        }
        pathTableTabPanel.selectTab(0);
        timeLineWidget.setSimulation(simulation);
    }

    @UiHandler("zoomInButton")
    void zoomIn(ClickEvent event) {
        timeLineWidget.zoomIn();
    }

    @UiHandler("zoomOutButton")
    void zoomOut(ClickEvent event) {
        timeLineWidget.zoomOut();
    }

}
