/*
 * Copyright 2010 JBoss Inc
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

package org.kie.guvnor.testscenario.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.ide.common.client.modeldriven.testing.CallFixtureMap;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.FixturesMap;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.metadata.client.resources.ImageResources;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.testscenario.service.model.CallFixtureMap;
import org.kie.guvnor.testscenario.service.model.ExecutionTrace;
import org.kie.guvnor.testscenario.service.model.Fixture;
import org.kie.guvnor.testscenario.service.model.FixtureList;
import org.kie.guvnor.testscenario.service.model.FixturesMap;
import org.kie.guvnor.testscenario.service.model.Scenario;
import org.kie.guvnor.testscenario.service.model.VerifyFact;
import org.kie.guvnor.testscenario.service.model.VerifyRuleFired;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.common.DirtyableFlexTable;
import org.uberfire.client.common.SmallLabel;

import java.util.List;

@WorkbenchEditor(identifier = "ScenarioEditorPresenter", supportedTypes = { TestScenarioResourceType.class })
public class ScenarioEditorPresenter
        extends Composite
        implements ScenarioParentWidget {

    private String[] availableRules;
    protected DataModelOracle dmo;
    private final VerticalPanel layout = new VerticalPanel();

    private HandlerRegistration availableRulesHandlerRegistration;
    private ScenarioWidgetComponentCreator scenarioWidgetComponentCreator;






//    public ScenarioEditorPresenter(Asset asset) {
//        this.scenarioWidgetComponentCreator = new ScenarioWidgetComponentCreator(asset,
//                this);
//        this.setShowResults(false);
//
//        this.suggestionCompletionEngine = SuggestionCompletionCache.getInstance().getEngineFromCache(asset.getMetaData().getModuleName());
//
//        ifFixturesSizeZeroThenAddExecutionTrace();
//
//        if (!asset.isReadonly()) {
//            layout.add(new TestRunnerWidget(this,
//                    asset.getMetaData().getModuleName()));
//        }
//
//        renderEditor();
//
//        initWidget(layout);
//
//        setStyleName("scenario-Viewer");
//
//        layout.setWidth("100%");
//
//    }

    private void ifFixturesSizeZeroThenAddExecutionTrace() {
        if (getScenario().getFixtures().size() == 0) {
            getScenario().getFixtures().add(new ExecutionTrace());
        }
    }

    private void createWidgetForEditorLayout(DirtyableFlexTable editorLayout,
                                             int layoutRow,
                                             int layoutColumn,
                                             Widget widget) {
        editorLayout.setWidget(layoutRow,
                layoutColumn,
                widget);
    }

    public void renderEditor() {

        if (this.layout.getWidgetCount() == 2) {
            this.layout.remove(1);
        }

        DirtyableFlexTable editorLayout = scenarioWidgetComponentCreator.createDirtyableFlexTable();
        this.layout.add(editorLayout);
        ScenarioHelper scenarioHelper = new ScenarioHelper();

        List<Fixture> fixtures = scenarioHelper.lumpyMap(getScenario().getFixtures());
        List<ExecutionTrace> listExecutionTrace = scenarioHelper.getExecutionTraceFor(fixtures);

        int layoutRow = 1;
        int executionTraceLine = 0;
        ExecutionTrace previousExecutionTrace = null;
        for (final Fixture fixture : fixtures) {
            if (fixture instanceof ExecutionTrace) {
                ExecutionTrace currentExecutionTrace = (ExecutionTrace) fixture;
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        0,
                        scenarioWidgetComponentCreator.createExpectPanel(currentExecutionTrace));

                executionTraceLine++;
                if (executionTraceLine >= listExecutionTrace.size()) {
                    executionTraceLine = listExecutionTrace.size() - 1;
                }
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        1,
                        scenarioWidgetComponentCreator.createExecutionWidget(currentExecutionTrace));
                editorLayout.setHorizontalAlignmentForFlexCellFormatter(layoutRow,
                        2,
                        HasHorizontalAlignment.ALIGN_LEFT);

                previousExecutionTrace = currentExecutionTrace;

            } else if (fixture instanceof FixturesMap) {
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        0,
                        scenarioWidgetComponentCreator.createGivenLabelButton(listExecutionTrace,
                                executionTraceLine,
                                previousExecutionTrace));
                layoutRow++;
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        1,
                        scenarioWidgetComponentCreator.createGivenPanel(listExecutionTrace,
                                executionTraceLine,
                                (FixturesMap) fixture));
            } else if (fixture instanceof CallFixtureMap) {
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        0,
                        scenarioWidgetComponentCreator.createCallMethodLabelButton(listExecutionTrace,
                                executionTraceLine,
                                previousExecutionTrace));
                layoutRow++;
                createWidgetForEditorLayout(editorLayout,
                        layoutRow,
                        1,
                        scenarioWidgetComponentCreator.createCallMethodOnGivenPanel(listExecutionTrace,
                                executionTraceLine,
                                (CallFixtureMap) fixture));
            } else {
                FixtureList fixturesList = (FixtureList) fixture;
                Fixture first = fixturesList.get(0);

                if (first instanceof VerifyFact) {
                    createWidgetForEditorLayout(editorLayout,
                            layoutRow,
                            1,
                            scenarioWidgetComponentCreator.createVerifyFactsPanel(listExecutionTrace,
                                    executionTraceLine,
                                    fixturesList));
                } else if (first instanceof VerifyRuleFired) {
                    createWidgetForEditorLayout(editorLayout,
                            layoutRow,
                            1,
                            scenarioWidgetComponentCreator.createVerifyRulesFiredWidget(fixturesList));
                }

            }
            layoutRow++;
        }

        // add more execution sections.
        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                0,
                scenarioWidgetComponentCreator.createAddExecuteButton());
        layoutRow++;
        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                0,
                scenarioWidgetComponentCreator.createSmallLabel());

        // config section
        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                1,
                scenarioWidgetComponentCreator.createConfigWidget());

        layoutRow++;

        // global section
        HorizontalPanel horizontalPanel = scenarioWidgetComponentCreator.createHorizontalPanel();
        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                0,
                horizontalPanel);

        createWidgetForEditorLayout(editorLayout,
                layoutRow,
                1,
                scenarioWidgetComponentCreator.createGlobalPanel(scenarioHelper,
                        previousExecutionTrace));
    }

    public Widget getRuleSelectionWidget(final String packageName,
                                         final RuleSelectionEvent selected) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        final TextBox ruleNameTextBox = scenarioWidgetComponentCreator.createRuleNameTextBox();
        horizontalPanel.add(ruleNameTextBox);
        if (availableRules != null) {
            final ListBox availableRulesBox = scenarioWidgetComponentCreator.createAvailableRulesBox(availableRules);
            availableRulesBox.setSelectedIndex(0);
            if (availableRulesHandlerRegistration != null) {
                availableRulesHandlerRegistration.removeHandler();
            }
            final ChangeHandler ruleSelectionCL = scenarioWidgetComponentCreator.createRuleChangeHandler(ruleNameTextBox,
                    availableRulesBox);

            availableRulesHandlerRegistration = availableRulesBox.addChangeHandler(ruleSelectionCL);
            horizontalPanel.add(availableRulesBox);

        } else {

            final Button showList = new Button(TestScenarioConstants.INSTANCE.showListButton());
            horizontalPanel.add(showList);
            showList.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    horizontalPanel.remove(showList);
                    final Image busy = new Image(ImageResources.INSTANCE.searching());
                    final Label loading = new SmallLabel(TestScenarioConstants.INSTANCE.loadingList1());
                    horizontalPanel.add(busy);
                    horizontalPanel.add(loading);

//                    Scheduler scheduler = Scheduler.get();
//                    scheduler.scheduleDeferred(new Command() {
//                        public void execute() {
//                            ModuleServiceAsync moduleService = GWT.create(ModuleService.class);
//                            moduleService.listRulesInPackage(packageName,
//                                    createGenericCallback(horizontalPanel,
//                                            ruleNameTextBox,
//                                            busy,
//                                            loading));
//                        }

//                        private GenericCallback<String[]> createGenericCallback(final HorizontalPanel horizontalPanel,
//                                                                                final TextBox ruleNameTextBox,
//                                                                                final Image busy,
//                                                                                final Label loading) {
//                            return new GenericCallback<String[]>() {
//
//                                public void onSuccess(String[] list) {
//                                    availableRules = (list);
//                                    final ListBox availableRulesBox = scenarioWidgetComponentCreator.createAvailableRulesBox(list);
//
//                                    final ChangeHandler ruleSelectionCL = new ChangeHandler() {
//                                        public void onChange(ChangeEvent event) {
//                                            ruleNameTextBox.setText(availableRulesBox.getItemText(availableRulesBox.getSelectedIndex()));
//                                        }
//                                    };
//                                    availableRulesHandlerRegistration = availableRulesBox.addChangeHandler(ruleSelectionCL);
//                                    availableRulesBox.setSelectedIndex(0);
//                                    horizontalPanel.add(availableRulesBox);
//                                    horizontalPanel.remove(busy);
//                                    horizontalPanel.remove(loading);
//                                }
//
//                            };
//                        }
//                    });

                }
            });

        }

        Button ok = scenarioWidgetComponentCreator.createOkButton(selected,
                ruleNameTextBox);
        horizontalPanel.add(ok);
        return horizontalPanel;
    }

    void setShowResults(boolean showResults) {
        scenarioWidgetComponentCreator.setShowResults(showResults);
    }

    boolean isShowResults() {
        return scenarioWidgetComponentCreator.isShowResults();
    }

//    public MetaData getMetaData() {
//        return scenarioWidgetComponentCreator.getMetaData();
//    }

    public void setScenario(Scenario scenario) {
        scenarioWidgetComponentCreator.setScenario(scenario);
    }

    public Scenario getScenario() {
        return scenarioWidgetComponentCreator.getScenario();
    }
}
