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

package org.drools.guvnor.client.qa.testscenarios;

import java.util.List;

import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.qa.VerifyRulesFiredWidget;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.EditorWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.CallFixtureMap;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.FixturesMap;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScenarioWidget extends Composite
    implements
    EditorWidget {

    private Constants                          constants = GWT.create( Constants.class );
    private static Images                      images    = GWT.create( Images.class );

    private String[]                           availableRules;
    protected final SuggestionCompletionEngine suggestionCompletionEngine;
    private ChangeHandler                      ruleSelectionCL;
    private final RuleAsset                    asset;
    private final VerticalPanel                layout;
    private boolean                            showResults;

    private HandlerRegistration                availableRulesHandlerRegistration;

    public ScenarioWidget(RuleAsset asset,
                          RuleViewer viewer) {
        this( asset );
    }

    public ScenarioWidget(RuleAsset asset) {
        this.asset = asset;
        this.layout = new VerticalPanel();
        this.setShowResults( false );

        this.suggestionCompletionEngine = SuggestionCompletionCache
                .getInstance().getEngineFromCache( asset.metaData.packageName );

        if ( getScenario().fixtures.size() == 0 ) {
            getScenario().fixtures.add( new ExecutionTrace() );
        }

        if ( !asset.isreadonly ) {
            layout.add( new TestRunnerWidget( this,
                                              asset.metaData.packageName ) );
        }

        renderEditor();

        initWidget( layout );

        setStyleName( "scenario-Viewer" );

        layout.setWidth( "100%" );

    }

    private void createWidgetForEditorLayout(DirtyableFlexTable editorLayout,
                                             int layoutRow,
                                             int layoutColumn,
                                             Widget widget) {
        editorLayout.setWidget( layoutRow,
                                layoutColumn,
                                widget );
    }

    void renderEditor() {

        if ( this.layout.getWidgetCount() == 2 ) {
            this.layout.remove( 1 );
        }

        DirtyableFlexTable editorLayout = createDirtyableFlexTable();
        this.layout.add( editorLayout );
        ScenarioHelper scenarioHelper = new ScenarioHelper();
        List<Fixture> fixtures = getScenario().fixtures;
        List<ExecutionTrace> listExecutionTrace = scenarioHelper
                .getExecutionTraceFor( fixtures );

        int layoutRow = 1;
        int executionTraceLine = 0;
        ExecutionTrace previousExecutionTrace = null;
        for ( final Fixture fixture : fixtures ) {
            if ( fixture instanceof ExecutionTrace ) {
                ExecutionTrace currentExecutionTrace = (ExecutionTrace) fixture;
                createWidgetForEditorLayout( editorLayout,
                                             layoutRow,
                                             0,
                                             createExpectPanel( currentExecutionTrace ) );

                executionTraceLine++;
                if ( executionTraceLine >= listExecutionTrace.size() ) {
                    executionTraceLine = listExecutionTrace.size() - 1;
                }
                createWidgetForEditorLayout( editorLayout,
                                             layoutRow,
                                             1,
                                             createExecutionWidget( currentExecutionTrace ) );
                editorLayout.setHorizontalAlignmentForFlexCellFormatter(
                                                                         layoutRow,
                                                                         2,
                                                                         HasHorizontalAlignment.ALIGN_LEFT );

                previousExecutionTrace = currentExecutionTrace;

            } else if ( fixture instanceof FixturesMap ) {
                createWidgetForEditorLayout(
                                             editorLayout,
                                             layoutRow,
                                             0,
                                             createGivenLabelButton( listExecutionTrace,
                                                                     executionTraceLine,
                                                                     previousExecutionTrace ) );
                layoutRow++;
                createWidgetForEditorLayout(
                                             editorLayout,
                                             layoutRow,
                                             1,
                                             createGivenPanel( listExecutionTrace,
                                                               executionTraceLine,
                                                               (FixturesMap) fixture ) );
            } else if ( fixture instanceof CallFixtureMap ) {
                createWidgetForEditorLayout(
                                             editorLayout,
                                             layoutRow,
                                             0,
                                             createCallMethodLabelButton( listExecutionTrace,
                                                                          executionTraceLine,
                                                                          previousExecutionTrace ) );
                layoutRow++;
                createWidgetForEditorLayout(
                                             editorLayout,
                                             layoutRow,
                                             1,
                                             createCallMethodOnGivenPanel( listExecutionTrace,
                                                                           executionTraceLine,
                                                                           (CallFixtureMap) fixture ) );
            } else {
                FixtureList fixturesList = (FixtureList) fixture;
                Fixture first = fixturesList.get( 0 );
                if ( first instanceof VerifyFact ) {
                    createWidgetForEditorLayout(
                                                 editorLayout,
                                                 layoutRow,
                                                 1,
                                                 createVerifyFactsPanel( listExecutionTrace,
                                                                         executionTraceLine,
                                                                         fixturesList ) );
                } else if ( first instanceof VerifyRuleFired ) {
                    createWidgetForEditorLayout( editorLayout,
                                                 layoutRow,
                                                 1,
                                                 createVerifyRulesFiredWidget( fixturesList ) );
                }

            }
            layoutRow++;
        }

        // add more execution sections.
        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     0,
                                     createAddExecuteButton() );
        layoutRow++;
        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     0,
                                     createSmallLabel() );

        // config section
        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     1,
                                     createConfigWidget() );

        layoutRow++;

        // global section
        HorizontalPanel horizontalPanel = createHorizontalPanel();
        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     0,
                                     horizontalPanel );

        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     1,
                                     createGlobalPanel( scenarioHelper,
                                                        previousExecutionTrace ) );
    }

    private GlobalPanel createGlobalPanel(ScenarioHelper scenarioHelper,
                                          ExecutionTrace previousExecutionTrace) {
        return new GlobalPanel(
                                scenarioHelper.lumpyMapGlobals( getScenario().globals ),
                                getScenario(),
                                previousExecutionTrace,
                                this );
    }

    private HorizontalPanel createHorizontalPanel() {
        HorizontalPanel h = new HorizontalPanel();
        h.add( new GlobalButton( getScenario(),
                                 this ) );
        h.add( new SmallLabel( constants.globals() ) );
        return h;
    }

    private SmallLabel createSmallLabel() {
        return new SmallLabel( constants.configuration() );
    }

    private ConfigWidget createConfigWidget() {
        return new ConfigWidget( getScenario(),
                                 asset.metaData.packageName,
                                 this );
    }

    private AddExecuteButton createAddExecuteButton() {
        return new AddExecuteButton( getScenario(),
                                     this );
    }

    private VerifyRulesFiredWidget createVerifyRulesFiredWidget(
                                                                FixtureList fixturesList) {
        return new VerifyRulesFiredWidget( fixturesList,
                                           getScenario(),
                                           isShowResults() );
    }

    private VerifyFactsPanel createVerifyFactsPanel(
                                                    List<ExecutionTrace> listExecutionTrace,
                                                    int executionTraceLine,
                                                    FixtureList fixturesList) {
        return new VerifyFactsPanel( fixturesList,
                                     listExecutionTrace.get( executionTraceLine ),
                                     getScenario(),
                                     this,
                                     isShowResults() );
    }

    private CallMethodLabelButton createCallMethodLabelButton(
                                                              List<ExecutionTrace> listExecutionTrace,
                                                              int executionTraceLine,
                                                              ExecutionTrace previousExecutionTrace) {
        return new CallMethodLabelButton( previousExecutionTrace,
                                          getScenario(),
                                          listExecutionTrace.get( executionTraceLine ),
                                          this );
    }

    private GivenLabelButton createGivenLabelButton(
                                                    List<ExecutionTrace> listExecutionTrace,
                                                    int executionTraceLine,
                                                    ExecutionTrace previousExecutionTrace) {
        return new GivenLabelButton( previousExecutionTrace,
                                     getScenario(),
                                     listExecutionTrace.get( executionTraceLine ),
                                     this );
    }

    private ExecutionWidget createExecutionWidget(
                                                  ExecutionTrace currentExecutionTrace) {
        return new ExecutionWidget( currentExecutionTrace,
                                    isShowResults() );
    }

    private ExpectPanel createExpectPanel(ExecutionTrace currentExecutionTrace) {
        return new ExpectPanel( asset.metaData.packageName,
                                currentExecutionTrace,
                                getScenario(),
                                this );
    }

    private DirtyableFlexTable createDirtyableFlexTable() {
        DirtyableFlexTable editorLayout = new DirtyableFlexTable();
        editorLayout.clear();
        editorLayout.setWidth( "100%" );
        editorLayout.setStyleName( "model-builder-Background" );
        return editorLayout;
    }

    private Widget createGivenPanel(List<ExecutionTrace> listExecutionTrace,
                                    int executionTraceLine,
                                    FixturesMap given) {

        if ( given.size() > 0 ) {
            return new GivenPanel( listExecutionTrace,
                                   executionTraceLine,
                                   given,
                                   getScenario(),
                                   this );

        } else {
            return new HTML( "<i><small>"
                             + constants.AddInputDataAndExpectationsHere()
                             + "</small></i>" );
        }
    }

    private Widget createCallMethodOnGivenPanel(
                                                List<ExecutionTrace> listExecutionTrace,
                                                int executionTraceLine,
                                                CallFixtureMap given) {

        if ( given.size() > 0 ) {
            return new CallMethodOnGivenPanel( listExecutionTrace,
                                               executionTraceLine,
                                               given,
                                               getScenario(),
                                               this );

        } else {
            return new HTML( "<i><small>"
                             + constants.AddInputDataAndExpectationsHere()
                             + "</small></i>" );
        }
    }

    public Widget getRuleSelectionWidget(final String packageName,
                                         final RuleSelectionEvent selected) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        final TextBox ruleNameTextBox = new TextBox();
        ruleNameTextBox.setTitle( constants.EnterRuleNameScenario() );
        horizontalPanel.add( ruleNameTextBox );
        if ( !(availableRules == null) ) {
            final ListBox availableRulesBox = new ListBox();

            availableRulesBox.addItem( constants.pleaseChoose1() );
            for ( int i = 0; i < availableRules.length; i++ ) {
                availableRulesBox.addItem( availableRules[i] );
            }
            availableRulesBox.setSelectedIndex( 0 );
            if ( availableRulesHandlerRegistration != null ) {
                availableRulesHandlerRegistration.removeHandler();
            }
            ruleSelectionCL = new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    ruleNameTextBox.setText( availableRulesBox
                            .getItemText( availableRulesBox.getSelectedIndex() ) );
                }
            };

            availableRulesHandlerRegistration = availableRulesBox
                    .addChangeHandler( ruleSelectionCL );
            horizontalPanel.add( availableRulesBox );

        } else {

            final Button showList = new Button( constants.showListButton() );
            horizontalPanel.add( showList );
            showList.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    horizontalPanel.remove( showList );
                    final Image busy = new Image( images.searching() );
                    final Label loading = new SmallLabel( constants
                            .loadingList1() );
                    horizontalPanel.add( busy );
                    horizontalPanel.add( loading );

                    Scheduler scheduler = Scheduler.get();
                    scheduler.scheduleDeferred( new Command() {

                        public void execute() {
                            RepositoryServiceFactory.getService()
                                    .listRulesInPackage( packageName,
                                                         new GenericCallback<String[]>() {

                                                             public void onSuccess(
                                                                                   String[] list) {
                                                                 availableRules = (list);
                                                                 final ListBox availableRulesBox = new ListBox();
                                                                 availableRulesBox.addItem( constants
                                                                         .pleaseChoose1() );
                                                                 for ( int i = 0; i < list.length; i++ ) {
                                                                     availableRulesBox
                                                                             .addItem( list[i] );
                                                                 }
                                                                 ruleSelectionCL = new ChangeHandler() {
                                                                     public void onChange(
                                                                                          ChangeEvent event) {
                                                                         ruleNameTextBox
                                                                                 .setText( availableRulesBox
                                                                                         .getItemText( availableRulesBox
                                                                                                 .getSelectedIndex() ) );
                                                                     }
                                                                 };
                                                                 availableRulesHandlerRegistration = availableRulesBox
                                                                         .addChangeHandler( ruleSelectionCL );
                                                                 availableRulesBox
                                                                         .setSelectedIndex( 0 );
                                                                 horizontalPanel
                                                                         .add( availableRulesBox );
                                                                 horizontalPanel
                                                                         .remove( busy );
                                                                 horizontalPanel
                                                                         .remove( loading );
                                                             }
                                                         } );
                        }
                    } );

                }
            } );

        }

        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                selected.ruleSelected( ruleNameTextBox.getText() );
            }
        } );
        horizontalPanel.add( ok );
        return horizontalPanel;
    }

    /**
     * Use some CSS trickery to get a percent bar.
     */
    public static Widget getBar(String colour,
                                int width,
                                float percent) {
        int pixels = (int) (width * (percent / 100));
        String html = "<div class=\"smallish-progress-wrapper\" style=\"width: "
                      + width
                      + "px\">"
                      + "<div class=\"smallish-progress-bar\" style=\"width: "
                      + pixels
                      + "px; background-color: "
                      + colour
                      + ";\"></div>"
                      + "<div class=\"smallish-progress-text\" style=\"width: "
                      + width + "px\">" + (int) percent + "%</div></div>"; // NON-NLS
        return new HTML( html );

    }

    public static Widget getBar(String colour,
                                int width,
                                int numerator,
                                int denominator) {
        int percent = 0;

        if ( denominator != 0 ) {
            percent = (int) ((((float) denominator - (float) numerator) / (float) denominator) * 100);
        }
        return getBar( colour,
                       width,
                       percent );
    }

    void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    boolean isShowResults() {
        return showResults;
    }

    public MetaData getMetaData() {
        return asset.metaData;
    }

    public void setScenario(Scenario scenario) {
        asset.content = scenario;
    }

    public Scenario getScenario() {
        return (Scenario) asset.content;
    }
}
