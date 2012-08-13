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
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.simulation.command.AbstractCommandWidget;
import org.drools.guvnor.client.simulation.command.AddCommandWidget;
import org.drools.guvnor.shared.simulation.SimulationStepModel;
import org.drools.guvnor.shared.simulation.command.AbstractCommandModel;

public class CommandWrapperWidget extends Composite {

    protected interface CommandWrapperWidgetBinder extends UiBinder<Widget, CommandWrapperWidget> {}
    private static CommandWrapperWidgetBinder uiBinder = GWT.create(CommandWrapperWidgetBinder.class);

    @UiField(provided = true)
    protected AbstractCommandWidget commandWidget;
    @UiField
    protected PushButton moveUpCommandButton;
    @UiField
    protected PushButton moveDownCommandButton;
    @UiField
    protected PushButton removeCommandButton;

    private final SimulationTestEventHandler simulationTestEventHandler;

    public CommandWrapperWidget(AbstractCommandWidget commandWidget,
                                SimulationTestEventHandler simulationTestEventHandler) {
        this.commandWidget = commandWidget;
        this.simulationTestEventHandler = simulationTestEventHandler;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("moveUpCommandButton")
    protected void moveUpCommand(ClickEvent event) {
        simulationTestEventHandler.moveUpCommand(commandWidget.getCommand());
    }

    @UiHandler("moveDownCommandButton")
    protected void moveDownCommand(ClickEvent event) {
        simulationTestEventHandler.moveDownCommand(commandWidget.getCommand());
    }

    @UiHandler("removeCommandButton")
    protected void removeCommand(ClickEvent event) {
        simulationTestEventHandler.removeCommand(commandWidget.getCommand());
    }

}
