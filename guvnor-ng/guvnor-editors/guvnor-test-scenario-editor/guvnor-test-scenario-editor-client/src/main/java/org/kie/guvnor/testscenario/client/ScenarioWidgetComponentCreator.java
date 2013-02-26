/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.guvnor.testscenario.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.testscenario.model.CallFixtureMap;
import org.kie.guvnor.testscenario.model.ExecutionTrace;
import org.kie.guvnor.testscenario.model.FixtureList;
import org.kie.guvnor.testscenario.model.FixturesMap;
import org.kie.guvnor.testscenario.model.Scenario;
import org.uberfire.client.common.DirtyableFlexTable;
import org.uberfire.client.common.SmallLabel;

import java.util.List;

public class ScenarioWidgetComponentCreator {

    private final ScenarioEditorPresenter scenarioWidget;

    private boolean showResults;
    private final Metadata metadata;
    private Scenario scenario;
    private String packageName;

    protected ScenarioWidgetComponentCreator(Metadata metadata, String packageName, ScenarioEditorPresenter scenarioWidget) {
        this.metadata = metadata;
        this.scenarioWidget = scenarioWidget;
        this.packageName = packageName;
    }

    protected GlobalPanel createGlobalPanel(ScenarioHelper scenarioHelper, ExecutionTrace previousExecutionTrace) {
        return new GlobalPanel(
                scenarioHelper.lumpyMapGlobals(getScenario().getGlobals()),
                getScenario(),
                previousExecutionTrace,
                this.scenarioWidget.dmo,
                this.scenarioWidget);
    }

    protected HorizontalPanel createHorizontalPanel() {
        HorizontalPanel h = new HorizontalPanel();
        h.add(new GlobalButton(getScenario(), this.scenarioWidget));
        h.add(new SmallLabel(TestScenarioConstants.INSTANCE.globals()));
        return h;
    }

    protected SmallLabel createSmallLabel() {
        return new SmallLabel(TestScenarioConstants.INSTANCE.configuration());
    }

    protected ConfigWidget createConfigWidget() {
        return new ConfigWidget(getScenario(), packageName, this.scenarioWidget);
    }

    protected AddExecuteButton createAddExecuteButton() {
        return new AddExecuteButton(getScenario(), this.scenarioWidget);
    }

    protected VerifyRulesFiredWidget createVerifyRulesFiredWidget(FixtureList fixturesList) {
        return new VerifyRulesFiredWidget(fixturesList, getScenario(), isShowResults());
    }

    protected VerifyFactsPanel createVerifyFactsPanel(List<ExecutionTrace> listExecutionTrace, int executionTraceLine, FixtureList fixturesList) {
        return new VerifyFactsPanel(fixturesList, listExecutionTrace.get(executionTraceLine), getScenario(), this.scenarioWidget, isShowResults());
    }

    protected CallMethodLabelButton createCallMethodLabelButton(List<ExecutionTrace> listExecutionTrace, int executionTraceLine, ExecutionTrace previousExecutionTrace) {
        return new CallMethodLabelButton(previousExecutionTrace, getScenario(), listExecutionTrace.get(executionTraceLine), this.scenarioWidget);
    }

    protected GivenLabelButton createGivenLabelButton(List<ExecutionTrace> listExecutionTrace, int executionTraceLine, ExecutionTrace previousExecutionTrace) {
        return new GivenLabelButton(previousExecutionTrace, getScenario(), listExecutionTrace.get(executionTraceLine), this.scenarioWidget);
    }

    protected ExecutionWidget createExecutionWidget(ExecutionTrace currentExecutionTrace) {
        return new ExecutionWidget(currentExecutionTrace, isShowResults());
    }

    protected ExpectPanel createExpectPanel(ExecutionTrace currentExecutionTrace) {
        return new ExpectPanel(packageName, currentExecutionTrace, getScenario(), this.scenarioWidget);
    }

    protected DirtyableFlexTable createDirtyableFlexTable() {
        DirtyableFlexTable editorLayout = new DirtyableFlexTable();
        editorLayout.clear();
        editorLayout.setWidth("100%");
        editorLayout.setStyleName("model-builder-Background");
        return editorLayout;
    }

    protected Widget createGivenPanel(List<ExecutionTrace> listExecutionTrace, int executionTraceLine, FixturesMap given) {

        if (given.size() > 0) {
            return new GivenPanel(
                    listExecutionTrace,
                    executionTraceLine,
                    given,
                    getScenario(),
                    this.scenarioWidget.dmo,
                    this.scenarioWidget);

        } else {
            return new HTML("<i><small>" + TestScenarioConstants.INSTANCE.AddInputDataAndExpectationsHere() + "</small></i>");
        }
    }

    protected Widget createCallMethodOnGivenPanel(List<ExecutionTrace> listExecutionTrace, int executionTraceLine, CallFixtureMap given) {

        if (given.size() > 0) {
            return new CallMethodOnGivenPanel(listExecutionTrace, executionTraceLine, given, getScenario(), this.scenarioWidget);

        } else {
            return new HTML("<i><small>" + TestScenarioConstants.INSTANCE.AddInputDataAndExpectationsHere() + "</small></i>");
        }
    }

    protected TextBox createRuleNameTextBox() {
        final TextBox ruleNameTextBox = new TextBox();
        ruleNameTextBox.setTitle(TestScenarioConstants.INSTANCE.EnterRuleNameScenario());
        return ruleNameTextBox;
    }

    protected Button createOkButton(final RuleSelectionEvent selected, final TextBox ruleNameTextBox) {
        Button ok = new Button(TestScenarioConstants.INSTANCE.OK());
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                selected.ruleSelected(ruleNameTextBox.getText());
            }
        });
        return ok;
    }

    protected ChangeHandler createRuleChangeHandler(final TextBox ruleNameTextBox, final ListBox availableRulesBox) {
        final ChangeHandler ruleSelectionCL = new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                ruleNameTextBox.setText(availableRulesBox.getItemText(availableRulesBox.getSelectedIndex()));
            }
        };
        return ruleSelectionCL;
    }

    protected ListBox createAvailableRulesBox(String[] list) {
        final ListBox availableRulesBox = new ListBox();
        availableRulesBox.addItem(TestScenarioConstants.INSTANCE.pleaseChoose1());
        for (int i = 0; i < list.length; i++) {
            availableRulesBox.addItem(list[i]);
        }
        return availableRulesBox;
    }

    public Metadata getMetaData() {
        return metadata;
    }

    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    public boolean isShowResults() {
        return this.showResults;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

}
