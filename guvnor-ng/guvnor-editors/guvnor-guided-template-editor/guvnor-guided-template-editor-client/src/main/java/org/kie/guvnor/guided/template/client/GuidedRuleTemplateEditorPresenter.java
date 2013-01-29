/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.guided.template.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.save.SaveCommand;
import org.kie.guvnor.commons.ui.client.save.SaveOpWrapper;
import org.kie.guvnor.configresource.client.widget.ResourceConfigWidget;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.errors.client.widget.ShowBuilderErrorsWidget;
import org.kie.guvnor.guided.template.model.GuidedTemplateEditorContent;
import org.kie.guvnor.guided.template.model.TemplateModel;
import org.kie.guvnor.guided.template.service.GuidedRuleTemplateEditorService;
import org.kie.guvnor.metadata.client.resources.i18n.MetadataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.config.ResourceConfigService;
import org.kie.guvnor.services.config.model.ResourceConfig;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.kie.guvnor.viewsource.client.screen.ViewSourceView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.LoadingPopup;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.MenuBar;

@Dependent
@WorkbenchEditor(identifier = "GuidedRuleTemplateEditor", fileTypes = "*.template")
public class GuidedRuleTemplateEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final Path path,
                         final TemplateModel model,
                         final DataModelOracle dataModel,
                         final EventBus eventBus,
                         final boolean isReadOnly );

        TemplateModel getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    public interface DataView
            extends
            IsWidget {

        void setContent( final TemplateModel model,
                         final DataModelOracle dataModel,
                         final EventBus eventBus,
                         final boolean isReadOnly );

        void clear();
    }

    @Inject
    private View view;

    @Inject
    private ViewSourceView viewSource;

    @Inject
    private DataView dataView;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<GuidedRuleTemplateEditorService> service;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<ResourceConfigService> resourceConfigService;

    @Inject
    private Caller<MetadataService> metadataService;

    private final ResourceConfigWidget resourceConfigWidget = new ResourceConfigWidget();

    private final MetadataWidget metadataWidget = new MetadataWidget();

    private EventBus eventBus = new SimpleEventBus();

    private boolean isReadOnly = false;

    private TemplateModel model;
    private DataModelOracle oracle;

    private Path path = null;

    @PostConstruct
    public void init() {
        multiPage.addWidget( view,
                             CommonConstants.INSTANCE.EditTabTitle() );
        multiPage.addPage( new Page( viewSource,
                                     CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                service.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response ) {
                        viewSource.setContent( response );
                    }
                } ).toSource( view.getContent() );
            }

            @Override
            public void onLostFocus() {
                viewSource.clear();
            }
        } );

        multiPage.addPage( new Page( dataView,
                                     "Data" ) {

            @Override
            public void onFocus() {
                dataView.setContent( model,
                                     oracle,
                                     eventBus,
                                     isReadOnly );
            }

            @Override
            public void onLostFocus() {
                dataView.clear();
            }
        } );
        multiPage.addPage( new Page( resourceConfigWidget,
                                     CommonConstants.INSTANCE.ConfigTabTitle() ) {
            @Override
            public void onFocus() {
                resourceConfigService.call( new RemoteCallback<ResourceConfig>() {
                    @Override
                    public void callback( final ResourceConfig config ) {
                        resourceConfigWidget.setContent( config,
                                                         isReadOnly );
                    }
                } ).getConfig( path );
            }

            @Override
            public void onLostFocus() {
            }
        } );

        multiPage.addPage( new Page( metadataWidget,
                                     MetadataConstants.INSTANCE.Metadata() ) {
            @Override
            public void onFocus() {
                metadataService.call( new RemoteCallback<Metadata>() {
                    @Override
                    public void callback( Metadata metadata ) {
                        metadataWidget.setContent( metadata,
                                                   isReadOnly );
                    }
                } ).getMetadata( path );
            }

            @Override
            public void onLostFocus() {
                // Nothing to do here
            }
        } );

    }

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;

        service.call( new RemoteCallback<GuidedTemplateEditorContent>() {
            @Override
            public void callback( final GuidedTemplateEditorContent response ) {
                model = response.getRuleModel();
                oracle = response.getDataModel();
                view.setContent( path,
                                 model,
                                 oracle,
                                 eventBus,
                                 isReadOnly );
                dataView.setContent( model,
                                     oracle,
                                     eventBus,
                                     isReadOnly );
            }
        } ).loadContent( path );
    }

    @OnSave
    public void onSave() {
        new SaveOpWrapper( path, new SaveCommand() {
            @Override
            public void execute( final String commitMessage ) {
                service.call( new RemoteCallback<Path>() {
                    @Override
                    public void callback( Path response ) {
                        view.setNotDirty();
                        resourceConfigWidget.resetDirty();
                        metadataWidget.resetDirty();
                        notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
                    }
                } ).save( path,
                          view.getContent(),
                          resourceConfigWidget.getContent(),
                          metadataWidget.getContent(),
                          commitMessage );
            }
        } ).save();
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Guided Template [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        return ResourceMenuBuilder.newResourceMenuBuilder().addValidation( new Command() {
            @Override
            public void execute() {
                LoadingPopup.showMessage( CommonConstants.INSTANCE.WaitWhileValidating() );
                service.call( new RemoteCallback<BuilderResult>() {
                    @Override
                    public void callback( BuilderResult response ) {
                        final ShowBuilderErrorsWidget pop = new ShowBuilderErrorsWidget( response );
                        LoadingPopup.close();
                        pop.show();
                    }
                } ).validate( path,
                              view.getContent() );
            }
        } ).addSave( new Command() {
            @Override
            public void execute() {
                onSave();
            }
        } ).build();
    }

}
