package org.drools.guvnor.client.qa.testscenarios;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.qa.VerifyRulesFiredWidget;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.FixtureList;
import org.drools.ide.common.client.modeldriven.testing.FixturesMap;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
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

public class ScenarioWidget extends Composite {

    private String[]                           availableRules;
    protected final SuggestionCompletionEngine suggestionCompletionEngine;
    private ChangeListener                     ruleSelectionCL;
    final RuleAsset                            asset;
    private final Scenario                     scenario;
    final VerticalPanel                        layout;
    boolean                                    showResults;

    private Constants                          constants = ((Constants) GWT.create( Constants.class ));

    public ScenarioWidget(RuleAsset asset,
                          RuleViewer viewer) {
        this( asset );
    }

    public ScenarioWidget(RuleAsset asset) {
        this.asset = asset;
        this.layout = new VerticalPanel();
        this.showResults = false;

        this.suggestionCompletionEngine = SuggestionCompletionCache.getInstance().getEngineFromCache( asset.metaData.packageName );

        scenario = (Scenario) asset.content;
        if ( scenario.fixtures.size() == 0 ) {
            scenario.fixtures.add( new ExecutionTrace() );
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

    void renderEditor() {

        if ( this.layout.getWidgetCount() == 2 ) {
            this.layout.remove( 1 );
        }

        DirtyableFlexTable editorLayout = new DirtyableFlexTable();
        editorLayout.clear();
        editorLayout.setWidth( "100%" );
        editorLayout.setStyleName( "model-builder-Background" );
        this.layout.add( editorLayout );

        ScenarioHelper scenarioHelper = new ScenarioHelper();
        List<Fixture> fixtures = scenarioHelper.lumpyMap( scenario.fixtures );
        List<ExecutionTrace> listExecutionTrace = new ArrayList<ExecutionTrace>();
        for ( int i = 0; i < fixtures.size(); i++ ) {
            final Object fixture = fixtures.get( i );
            if ( fixture instanceof ExecutionTrace ) {
                listExecutionTrace.add( (ExecutionTrace) fixture );
            }
        }
        int layoutRow = 1;
        int executionTraceLine = 0;
        ExecutionTrace previousEx = null;
        for ( int i = 0; i < fixtures.size(); i++ ) {
            final Fixture fixture = fixtures.get( i );
            if ( fixture instanceof ExecutionTrace ) {
                previousEx = (ExecutionTrace) fixture;
                editorLayout.setWidget( layoutRow,
                                        0,
                                        new ExpectPanel( asset.metaData.packageName,
                                                         previousEx,
                                                         scenario,
                                                         this ) );

                executionTraceLine++;
                if ( executionTraceLine >= listExecutionTrace.size() ) {
                    executionTraceLine = listExecutionTrace.size() - 1;
                }
                editorLayout.setWidget( layoutRow,
                                        1,
                                        new ExecutionWidget( previousEx,
                                                             showResults ) );
                editorLayout.getFlexCellFormatter().setHorizontalAlignment( layoutRow,
                                                                            2,
                                                                            HasHorizontalAlignment.ALIGN_LEFT );

            } else if ( fixture instanceof FixturesMap ) {
                editorLayout.setWidget( layoutRow,
                                        0,
                                        new GivenLabelButton( previousEx,
                                                              scenario,
                                                              listExecutionTrace.get( executionTraceLine ),
                                                              this ) );

                layoutRow++;

                if ( fixture instanceof FixturesMap ) {
                    editorLayout.setWidget( layoutRow,
                                            1,
                                            newGivenPanel( listExecutionTrace,
                                                           executionTraceLine,
                                                           (FixturesMap) fixture ) );
                }
            } else {
                FixtureList fixturesList = (FixtureList) fixture;
                Fixture first = fixturesList.get( 0 );
                if ( first instanceof VerifyFact ) {

                    editorLayout.setWidget( layoutRow,
                                            1,
                                            new VerifyFactsPanel( fixturesList,
                                                                  listExecutionTrace.get( executionTraceLine ),
                                                                  scenario,
                                                                  this,
                                                                  showResults ) );
                } else if ( first instanceof VerifyRuleFired ) {
                    editorLayout.setWidget( layoutRow,
                                            1,
                                            new VerifyRulesFiredWidget( fixturesList,
                                                                        scenario,
                                                                        showResults ) );
                }

            }
            layoutRow++;
        }

        //add more execution sections.
        editorLayout.setWidget( layoutRow,
                                0,
                                new AddExecuteButton( scenario,
                                                      this ) );
        layoutRow++;

        editorLayout.setWidget( layoutRow,
                                0,
                                new SmallLabel( constants.configuration() ) );

        //config section
        editorLayout.setWidget( layoutRow,
                                1,
                                new ConfigWidget( scenario,
                                                  asset.metaData.packageName,
                                                  this ) );

        layoutRow++;

        //global section
        HorizontalPanel h = new HorizontalPanel();
        h.add( new GlobalButton( scenario,
                                 this ) );
        h.add( new SmallLabel( constants.globals() ) );
        editorLayout.setWidget( layoutRow,
                                0,
                                h );

        editorLayout.setWidget( layoutRow,
                                1,
                                new GlobalPanel( scenarioHelper.lumpyMapGlobals( scenario.globals ),
                                                 scenario,
                                                 previousEx,
                                                 this ) );
    }

    private Widget newGivenPanel(List<ExecutionTrace> listExecutionTrace,
                                 int executionTraceLine,
                                 FixturesMap given) {

        if ( given.size() > 0 ) {
            return new GivenPanel( listExecutionTrace,
                                   executionTraceLine,
                                   given,
                                   scenario,
                                   this );

        } else {
            return new HTML( "<i><small>" + constants.AddInputDataAndExpectationsHere() + "</small></i>" );
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
            availableRulesBox.removeChangeListener( ruleSelectionCL );
            ruleSelectionCL = new ChangeListener() {

                public void onChange(Widget w) {
                    ruleNameTextBox.setText( availableRulesBox.getItemText( availableRulesBox.getSelectedIndex() ) );
                }
            };

            availableRulesBox.addChangeListener( ruleSelectionCL );
            horizontalPanel.add( availableRulesBox );

        } else {

            final Button showList = new Button( constants.showListButton() );
            horizontalPanel.add( showList );
            showList.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    horizontalPanel.remove( showList );
                    final Image busy = new Image( "images/searching.gif" ); //NON-NLS
                    final Label loading = new SmallLabel( constants.loadingList1() );
                    horizontalPanel.add( busy );
                    horizontalPanel.add( loading );

                    DeferredCommand.addCommand( new Command() {

                        public void execute() {
                            RepositoryServiceFactory.getService().listRulesInPackage( packageName,
                                                                                      new GenericCallback<String[]>() {

                                                                                          public void onSuccess(String[] list) {
                                                                                              availableRules = (list);
                                                                                              final ListBox availableRulesBox = new ListBox();
                                                                                              availableRulesBox.addItem( constants.pleaseChoose1() );
                                                                                              for ( int i = 0; i < list.length; i++ ) {
                                                                                                  availableRulesBox.addItem( list[i] );
                                                                                              }
                                                                                              ruleSelectionCL = new ChangeListener() {

                                                                                                  public void onChange(Widget w) {
                                                                                                      ruleNameTextBox.setText( availableRulesBox.getItemText( availableRulesBox.getSelectedIndex() ) );
                                                                                                  }
                                                                                              };
                                                                                              availableRulesBox.addChangeListener( ruleSelectionCL );
                                                                                              availableRulesBox.setSelectedIndex( 0 );
                                                                                              horizontalPanel.add( availableRulesBox );
                                                                                              horizontalPanel.remove( busy );
                                                                                              horizontalPanel.remove( loading );
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
        String html = "<div class=\"smallish-progress-wrapper\" style=\"width: " + width + "px\">" + "<div class=\"smallish-progress-bar\" style=\"width: " + pixels + "px; background-color: " + colour + ";\"></div>"
                      + "<div class=\"smallish-progress-text\" style=\"width: " + width + "px\">" + (int) percent + "%</div></div>"; //NON-NLS
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
}
