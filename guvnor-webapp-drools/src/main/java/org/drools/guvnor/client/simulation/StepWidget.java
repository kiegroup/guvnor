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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfirebootstrap.client.widgets.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.simulation.command.AbstractCommandWidget;
import org.drools.guvnor.client.simulation.command.AddCommandWidget;
import org.drools.guvnor.shared.simulation.SimulationStepModel;
import org.drools.guvnor.shared.simulation.command.AbstractCommandModel;

import java.util.HashMap;
import java.util.Map;

public class StepWidget extends Composite {

    protected interface StepWidgetBinder extends UiBinder<Widget, StepWidget> {}
    private static StepWidgetBinder uiBinder = GWT.create(StepWidgetBinder.class);

    @UiField
    protected VerticalPanel verticalPanel;
    @UiField
    protected PushButton addCommandButton;

    private final SimulationStepModel step;
    private final SimulationTestEventHandler simulationTestEventHandler;

    private Map<AbstractCommandModel, Integer> commandRowIndexMap = new HashMap<AbstractCommandModel, Integer>();

    public StepWidget(SimulationStepModel step, SimulationTestEventHandler simulationTestEventHandler) {
        this.step = step;
        this.simulationTestEventHandler = simulationTestEventHandler;
        initWidget(uiBinder.createAndBindUi(this));
        addAllCommandWidgets();
    }

    private void refreshAllCommandWidgets() {
        for (AbstractCommandModel command : step.getCommands()) {
            removeCommandWidget(command);
        }
        addAllCommandWidgets();
    }

    private void addAllCommandWidgets() {
        for (AbstractCommandModel command : step.getCommands()) {
            addCommandWidget(command);
        }
    }

    private void addCommandWidget(AbstractCommandModel command) {
        int commandIndex = verticalPanel.getWidgetCount();
        AbstractCommandWidget commandWidget = AbstractCommandWidget.buildCommandWidget(command);
        CommandWrapperWidget commandWrapperWidget = new CommandWrapperWidget(commandWidget, simulationTestEventHandler);
        verticalPanel.add(commandWrapperWidget);
        commandRowIndexMap.put(command, commandIndex);
    }

    private void removeCommandWidget(AbstractCommandModel command) {
        int commandIndex = commandRowIndexMap.remove(command);
        verticalPanel.remove(commandIndex);
        for (Map.Entry<AbstractCommandModel, Integer> entry : commandRowIndexMap.entrySet()) {
            int otherCommandIndex = entry.getValue();
            if (otherCommandIndex > commandIndex) {
                entry.setValue(otherCommandIndex - 1);
            }
        }
    }

    @UiHandler("addCommandButton")
    protected void addCommand(ClickEvent event) {
        FormStylePopup addCommandPopup = new FormStylePopup();
        addCommandPopup.setTitle(Constants.INSTANCE.AddCommandPopupTitle());
        addCommandPopup.setWidth("400px");
        addCommandPopup.addRow(new AddCommandWidget(addCommandPopup, step, simulationTestEventHandler));
        addCommandPopup.show();
    }

    public void addedCommand(AbstractCommandModel command) {
        addCommandWidget(command);
    }

    public void movedUpCommand(AbstractCommandModel command) {
        refreshAllCommandWidgets();
    }

    public void movedDownCommand(AbstractCommandModel command) {
        refreshAllCommandWidgets();
    }

    public void removedCommand(AbstractCommandModel command) {
        removeCommandWidget(command);
    }

}
