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

import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.drools.guvnor.models.testscenarios.shared.CallFixtureMap;
import org.drools.guvnor.models.testscenarios.shared.ExecutionTrace;
import org.drools.guvnor.models.testscenarios.shared.Fixture;
import org.drools.guvnor.models.testscenarios.shared.FixtureList;
import org.drools.guvnor.models.testscenarios.shared.FixturesMap;
import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.drools.guvnor.models.testscenarios.shared.VerifyFact;
import org.drools.guvnor.models.testscenarios.shared.VerifyRuleFired;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.menu.FileMenuBuilder;
import org.kie.guvnor.commons.ui.client.popups.file.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.popups.file.SaveOperationService;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.widget.BusyIndicatorView;
import org.kie.guvnor.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.guvnor.metadata.client.resources.ImageResources;
import org.kie.guvnor.metadata.client.resources.i18n.MetadataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.testscenario.service.ScenarioTestEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.DirtyableFlexTable;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.common.SmallLabel;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.PlaceRequest;

@WorkbenchEditor(identifier = "ScenarioEditorPresenter", supportedTypes = { TestScenarioResourceType.class })
public class ScenarioEditorPresenter
        implements ScenarioParentWidget {

    private final FileMenuBuilder menuBuilder;
    private final Event<NotificationEvent> notification;
    private Menus menus;
    private String[] availableRules;
    protected DataModelOracle dmo;
    private final VerticalPanel layout = new VerticalPanel();

    private HandlerRegistration availableRulesHandlerRegistration;
    private ScenarioWidgetComponentCreator scenarioWidgetComponentCreator;
    private final Caller<ScenarioTestEditorService> service;
    private final Caller<DataModelService> dataModelService;
    private boolean isReadOnly;
    private final Caller<ProjectService> projectService;
    private Caller<MetadataService> metadataService;

    private final ImportsWidgetPresenter importsWidget;

    private MetadataWidget metadataWidget;

    private MultiPageEditor multiPage;
    private Path path;

    private BusyIndicatorView busyIndicatorView;

    @Inject
    public ScenarioEditorPresenter( final Caller<ScenarioTestEditorService> service,
                                    final Caller<DataModelService> dataModelService,
                                    final Caller<ProjectService> projectService,
                                    final Caller<MetadataService> metadataService,
                                    final ImportsWidgetPresenter importsWidget,
                                    final MultiPageEditor multiPage,
                                    final MetadataWidget metadataWidget,
                                    final @New FileMenuBuilder menuBuilder,
                                    final Event<NotificationEvent> notification,
                                    final BusyIndicatorView busyIndicatorView ) {
        this.service = service;
        this.projectService = projectService;
        this.dataModelService = dataModelService;
        this.metadataService = metadataService;
        this.importsWidget = importsWidget;
        this.multiPage = multiPage;
        this.metadataWidget = metadataWidget;
        this.menuBuilder = menuBuilder;
        this.notification = notification;
        this.busyIndicatorView = busyIndicatorView;
    }

    private void makeMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addSave( new Command() {
                        @Override
                        public void execute() {
                            onSave();
                        }
                    } )
                    .addCopy( path )
                    .addRename( path )
                    .addDelete( path )
                    .build();
        }
    }

    private void onSave() {
        if ( isReadOnly ) {
            Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
            return;
        }

        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 service.call( getSaveSuccessCallback(),
                                                               new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).save( path,
                                                                                                                                     scenarioWidgetComponentCreator.getScenario(),
                                                                                                                                     metadataWidget.getContent(),
                                                                                                                                     commitMessage );
                                             }
                                         } );
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                metadataWidget.resetDirty();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Test Scenario";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @OnStart
    public void onStart( final Path path,
                         final PlaceRequest place ) {

        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        this.path = path;

        multiPage.addWidget( layout, "Test Scenario" );

        multiPage.addWidget( importsWidget,
                CommonConstants.INSTANCE.ConfigTabTitle() );

        if ( !isReadOnly ) {
            multiPage.addPage( new Page( metadataWidget,
                                         MetadataConstants.INSTANCE.Metadata() ) {
                @Override
                public void onFocus() {
                    metadataWidget.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                    metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                                       isReadOnly ),
                                          new HasBusyIndicatorDefaultErrorCallback( metadataWidget ) ).getMetadata( path );
                }

                @Override
                public void onLostFocus() {
                    // Nothing to do here.
                }
            } );
        }

        dataModelService.call(
                new RemoteCallback<DataModelOracle>() {
                    @Override
                    public void callback( DataModelOracle dataModelOracle ) {
                        dmo = dataModelOracle;
                        projectService.call( new RemoteCallback<String>() {
                            @Override
                            public void callback( final String packageName ) {
                                service.call( new RemoteCallback<Scenario>() {
                                    @Override
                                    public void callback( Scenario scenario ) {
                                        scenarioWidgetComponentCreator = new ScenarioWidgetComponentCreator( packageName, ScenarioEditorPresenter.this );

                                        setScenario( scenario );

                                        setShowResults( false );

                                        ifFixturesSizeZeroThenAddExecutionTrace();

                                        if ( !isReadOnly ) {
                                            layout.add( new TestRunnerWidget( ScenarioEditorPresenter.this, service, path ) );
                                        }

                                        renderEditor();

                                        importsWidget.setContent( dmo,
                                                scenario.getImports(),
                                                isReadOnly );

                                        layout.setWidth( "100%" );
                                    }
                                } ).load( path );
                            }
                        } ).resolvePackageName( path );
                    }
                }
                             ).getDataModel( path );

        makeMenuBar();
    }

    private void ifFixturesSizeZeroThenAddExecutionTrace() {
        if ( getScenario().getFixtures().size() == 0 ) {
            getScenario().getFixtures().add( new ExecutionTrace() );
        }
    }

    private void createWidgetForEditorLayout( DirtyableFlexTable editorLayout,
                                              int layoutRow,
                                              int layoutColumn,
                                              Widget widget ) {
        editorLayout.setWidget( layoutRow,
                                layoutColumn,
                                widget );
    }

    public void renderEditor() {

        if ( this.layout.getWidgetCount() == 2 ) {
            this.layout.remove( 1 );
        }

        DirtyableFlexTable editorLayout = scenarioWidgetComponentCreator.createDirtyableFlexTable();
        this.layout.add( editorLayout );
        ScenarioHelper scenarioHelper = new ScenarioHelper();

        List<Fixture> fixtures = scenarioHelper.lumpyMap( getScenario().getFixtures() );
        List<ExecutionTrace> listExecutionTrace = scenarioHelper.getExecutionTraceFor( fixtures );

        int layoutRow = 1;
        int executionTraceLine = 0;
        ExecutionTrace previousExecutionTrace = null;
        for ( final Fixture fixture : fixtures ) {
            if ( fixture instanceof ExecutionTrace ) {
                ExecutionTrace currentExecutionTrace = (ExecutionTrace) fixture;
                createWidgetForEditorLayout( editorLayout,
                                             layoutRow,
                                             0,
                                             scenarioWidgetComponentCreator.createExpectPanel( currentExecutionTrace ) );

                executionTraceLine++;
                if ( executionTraceLine >= listExecutionTrace.size() ) {
                    executionTraceLine = listExecutionTrace.size() - 1;
                }
                createWidgetForEditorLayout( editorLayout,
                                             layoutRow,
                                             1,
                                             scenarioWidgetComponentCreator.createExecutionWidget( currentExecutionTrace ) );
                editorLayout.setHorizontalAlignmentForFlexCellFormatter( layoutRow,
                                                                         2,
                                                                         HasHorizontalAlignment.ALIGN_LEFT );

                previousExecutionTrace = currentExecutionTrace;

            } else if ( fixture instanceof FixturesMap ) {
                createWidgetForEditorLayout( editorLayout,
                                             layoutRow,
                                             0,
                                             scenarioWidgetComponentCreator.createGivenLabelButton( listExecutionTrace,
                                                                                                    executionTraceLine,
                                                                                                    previousExecutionTrace ) );
                layoutRow++;
                createWidgetForEditorLayout( editorLayout,
                                             layoutRow,
                                             1,
                                             scenarioWidgetComponentCreator.createGivenPanel( listExecutionTrace,
                                                                                              executionTraceLine,
                                                                                              (FixturesMap) fixture ) );
            } else if ( fixture instanceof CallFixtureMap ) {
                createWidgetForEditorLayout( editorLayout,
                                             layoutRow,
                                             0,
                                             scenarioWidgetComponentCreator.createCallMethodLabelButton( listExecutionTrace,
                                                                                                         executionTraceLine,
                                                                                                         previousExecutionTrace ) );
                layoutRow++;
                createWidgetForEditorLayout( editorLayout,
                                             layoutRow,
                                             1,
                                             scenarioWidgetComponentCreator.createCallMethodOnGivenPanel( listExecutionTrace,
                                                                                                          executionTraceLine,
                                                                                                          (CallFixtureMap) fixture ) );
            } else {
                FixtureList fixturesList = (FixtureList) fixture;
                Fixture first = fixturesList.get( 0 );

                if ( first instanceof VerifyFact ) {
                    createWidgetForEditorLayout( editorLayout,
                                                 layoutRow,
                                                 1,
                                                 scenarioWidgetComponentCreator.createVerifyFactsPanel( listExecutionTrace,
                                                                                                        executionTraceLine,
                                                                                                        fixturesList ) );
                } else if ( first instanceof VerifyRuleFired ) {
                    createWidgetForEditorLayout( editorLayout,
                                                 layoutRow,
                                                 1,
                                                 scenarioWidgetComponentCreator.createVerifyRulesFiredWidget( fixturesList ) );
                }

            }
            layoutRow++;
        }

        // add more execution sections.
        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     0,
                                     scenarioWidgetComponentCreator.createAddExecuteButton() );
        layoutRow++;
        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     0,
                                     scenarioWidgetComponentCreator.createSmallLabel() );

        // config section
        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     1,
                                     scenarioWidgetComponentCreator.createConfigWidget() );

        layoutRow++;

        // global section
        HorizontalPanel horizontalPanel = scenarioWidgetComponentCreator.createHorizontalPanel();
        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     0,
                                     horizontalPanel );

        createWidgetForEditorLayout( editorLayout,
                                     layoutRow,
                                     1,
                                     scenarioWidgetComponentCreator.createGlobalPanel( scenarioHelper,
                                                                                       previousExecutionTrace ) );
    }

    public Widget getRuleSelectionWidget( final String packageName,
                                          final RuleSelectionEvent selected ) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        final TextBox ruleNameTextBox = scenarioWidgetComponentCreator.createRuleNameTextBox();
        horizontalPanel.add( ruleNameTextBox );
        if ( availableRules != null ) {
            final ListBox availableRulesBox = scenarioWidgetComponentCreator.createAvailableRulesBox( availableRules );
            availableRulesBox.setSelectedIndex( 0 );
            if ( availableRulesHandlerRegistration != null ) {
                availableRulesHandlerRegistration.removeHandler();
            }
            final ChangeHandler ruleSelectionCL = scenarioWidgetComponentCreator.createRuleChangeHandler( ruleNameTextBox,
                                                                                                          availableRulesBox );

            availableRulesHandlerRegistration = availableRulesBox.addChangeHandler( ruleSelectionCL );
            horizontalPanel.add( availableRulesBox );

        } else {

            final Button showList = new Button( TestScenarioConstants.INSTANCE.showListButton() );
            horizontalPanel.add( showList );
            showList.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    horizontalPanel.remove( showList );
                    final Image busy = new Image( ImageResources.INSTANCE.searching() );
                    final Label loading = new SmallLabel( TestScenarioConstants.INSTANCE.loadingList1() );
                    horizontalPanel.add( busy );
                    horizontalPanel.add( loading );

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
            } );

        }

        Button ok = scenarioWidgetComponentCreator.createOkButton( selected,
                                                                   ruleNameTextBox );
        horizontalPanel.add( ok );
        return horizontalPanel;
    }

    void setShowResults( boolean showResults ) {
        scenarioWidgetComponentCreator.setShowResults( showResults );
    }

    boolean isShowResults() {
        return scenarioWidgetComponentCreator.isShowResults();
    }

    public void setScenario( Scenario scenario ) {
        scenarioWidgetComponentCreator.setScenario( scenario );
    }

    public Scenario getScenario() {
        return scenarioWidgetComponentCreator.getScenario();
    }
}
