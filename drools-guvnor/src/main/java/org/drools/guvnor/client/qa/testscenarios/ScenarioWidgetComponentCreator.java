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
package org.drools.guvnor.client.qa.testscenarios;

import java.util.List;

import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.qa.VerifyRulesFiredWidget;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.ide.common.client.modeldriven.testing.CallFixtureMap;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.FixturesMap;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScenarioWidgetComponentCreator {

    private Constants            constants = GWT.create( Constants.class );
    private final ScenarioWidget scenarioWidget;
    private final RuleAsset      asset;

    private boolean              showResults;

    protected ScenarioWidgetComponentCreator(RuleAsset asset,
                                             ScenarioWidget scenarioWidget) {
        this.asset = asset;
        this.scenarioWidget = scenarioWidget;
    }

    protected GlobalPanel createGlobalPanel(ScenarioHelper scenarioHelper,
                                            ExecutionTrace previousExecutionTrace) {
        return new GlobalPanel(
                                scenarioHelper.lumpyMapGlobals( getScenario().globals ),
                                getScenario(),
                                previousExecutionTrace,
                                scenarioWidget );
    }

    protected HorizontalPanel createHorizontalPanel() {
        HorizontalPanel h = new HorizontalPanel();
        h.add( new GlobalButton( getScenario(),
                                 scenarioWidget ) );
        h.add( new SmallLabel( constants.globals() ) );
        return h;
    }

    protected SmallLabel createSmallLabel() {
        return new SmallLabel( constants.configuration() );
    }

    protected ConfigWidget createConfigWidget() {
        return new ConfigWidget( getScenario(),
                                 asset.metaData.packageName,
                                 scenarioWidget );
    }

    protected AddExecuteButton createAddExecuteButton() {
        return new AddExecuteButton( getScenario(),
                                     scenarioWidget );
    }

    protected VerifyRulesFiredWidget createVerifyRulesFiredWidget(
                                                                  FixtureList fixturesList) {
        return new VerifyRulesFiredWidget( fixturesList,
                                           getScenario(),
                                           isShowResults() );
    }

    protected VerifyFactsPanel createVerifyFactsPanel(
                                                      List<ExecutionTrace> listExecutionTrace,
                                                      int executionTraceLine,
                                                      FixtureList fixturesList) {
        return new VerifyFactsPanel( fixturesList,
                                     listExecutionTrace.get( executionTraceLine ),
                                     getScenario(),
                                     scenarioWidget,
                                     isShowResults() );
    }

    protected CallMethodLabelButton createCallMethodLabelButton(
                                                                List<ExecutionTrace> listExecutionTrace,
                                                                int executionTraceLine,
                                                                ExecutionTrace previousExecutionTrace) {
        return new CallMethodLabelButton( previousExecutionTrace,
                                          getScenario(),
                                          listExecutionTrace.get( executionTraceLine ),
                                          scenarioWidget );
    }

    protected GivenLabelButton createGivenLabelButton(
                                                      List<ExecutionTrace> listExecutionTrace,
                                                      int executionTraceLine,
                                                      ExecutionTrace previousExecutionTrace) {
        return new GivenLabelButton( previousExecutionTrace,
                                     getScenario(),
                                     listExecutionTrace.get( executionTraceLine ),
                                     scenarioWidget );
    }

    protected ExecutionWidget createExecutionWidget(
                                                    ExecutionTrace currentExecutionTrace) {
        return new ExecutionWidget( currentExecutionTrace,
                                    isShowResults() );
    }

    protected ExpectPanel createExpectPanel(ExecutionTrace currentExecutionTrace) {
        return new ExpectPanel( asset.metaData.packageName,
                                currentExecutionTrace,
                                getScenario(),
                                scenarioWidget );
    }

    protected DirtyableFlexTable createDirtyableFlexTable() {
        DirtyableFlexTable editorLayout = new DirtyableFlexTable();
        editorLayout.clear();
        editorLayout.setWidth( "100%" );
        editorLayout.setStyleName( "model-builder-Background" );
        return editorLayout;
    }

    protected Widget createGivenPanel(List<ExecutionTrace> listExecutionTrace,
                                      int executionTraceLine,
                                      FixturesMap given) {

        if ( given.size() > 0 ) {
            return new GivenPanel( listExecutionTrace,
                                   executionTraceLine,
                                   given,
                                   getScenario(),
                                   scenarioWidget );

        } else {
            return new HTML( "<i><small>"
                             + constants.AddInputDataAndExpectationsHere()
                             + "</small></i>" );
        }
    }

    protected Widget createCallMethodOnGivenPanel(
                                                  List<ExecutionTrace> listExecutionTrace,
                                                  int executionTraceLine,
                                                  CallFixtureMap given) {

        if ( given.size() > 0 ) {
            return new CallMethodOnGivenPanel( listExecutionTrace,
                                               executionTraceLine,
                                               given,
                                               getScenario(),
                                               scenarioWidget );

        } else {
            return new HTML( "<i><small>"
                             + constants.AddInputDataAndExpectationsHere()
                             + "</small></i>" );
        }
    }

    public MetaData getMetaData() {
        return asset.metaData;
    }

    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    public boolean isShowResults() {
        return showResults;
    }

    public Scenario getScenario() {
        return (Scenario) asset.content;
    }

    public void setScenario(Scenario scenario) {
        this.asset.content = scenario;
    }

}
