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

package org.drools.guvnor.client.simulation.command;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.shared.simulation.command.AssertRuleFiredCommandModel;
import org.drools.guvnor.shared.simulation.command.FireAllRulesCommandModel;

public class FireAllRulesCommandWidget extends AbstractCommandWidget<FireAllRulesCommandModel> {

    protected interface FireAllRulesCommandWidgetBinder extends UiBinder<Widget, FireAllRulesCommandWidget> {}
    private static FireAllRulesCommandWidgetBinder uiBinder = GWT.create(FireAllRulesCommandWidgetBinder.class);

    @UiField
    protected VerticalPanel verticalPanel;
    @UiField
    protected PushButton addAssertRuleFiredCommandButton;

    public FireAllRulesCommandWidget(FireAllRulesCommandModel command) {
        super(command);
        initWidget(uiBinder.createAndBindUi(this));
        for (AssertRuleFiredCommandModel assertRuleFiredCommand : command.getAssertRuleFiredCommands()) {
            addSubCommandWidget(assertRuleFiredCommand);
        }
    }

    private void addSubCommandWidget(AssertRuleFiredCommandModel assertRuleFiredCommand) {
        verticalPanel.add(new AssertRuleFiredCommandWidget(assertRuleFiredCommand));
    }

    @UiHandler("addAssertRuleFiredCommandButton")
    protected void addAssertRuleFiredCommand(ClickEvent event) {
        AssertRuleFiredCommandModel subCommand = new AssertRuleFiredCommandModel();
        command.addAssertRuleFiredCommandModel(subCommand);
        addSubCommandWidget(subCommand);
    }

}
