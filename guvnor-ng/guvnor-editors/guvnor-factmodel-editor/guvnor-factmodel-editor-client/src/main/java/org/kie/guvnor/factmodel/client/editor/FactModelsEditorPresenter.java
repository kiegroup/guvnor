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

package org.kie.guvnor.factmodel.client.editor;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.configresource.client.widget.ResourceConfigWidget;
import org.kie.guvnor.errors.client.widget.ShowBuilderErrorsWidget;
import org.kie.guvnor.factmodel.model.FactMetaModel;
import org.kie.guvnor.factmodel.model.FactModelContent;
import org.kie.guvnor.factmodel.model.FactModels;
import org.kie.guvnor.factmodel.service.FactModelService;
import org.kie.guvnor.metadata.client.events.RestoreEvent;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.config.ResourceConfigService;
import org.kie.guvnor.services.config.model.ResourceConfig;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.kie.guvnor.services.version.VersionService;
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
import org.uberfire.shared.mvp.PlaceRequest;

import static org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder.*;

@Dependent
@WorkbenchEditor(identifier = "FactModelsEditor", fileTypes = "*.model.drl")
public class FactModelsEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final FactModels content,
                         final List<FactMetaModel> superTypeFactModels,
                         final ModelNameHelper modelNameHelper );

        FactModels getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();

        void alertReadOnly();
    }

    @Inject
    private Caller<FactModelService> factModelService;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private Caller<ResourceConfigService> resourceConfigService;

    @Inject
    private Caller<VersionService> versionService;

    @Inject
    private View view;

    @Inject
    private ViewSourceView viewSource;

    private final ResourceConfigWidget resourceConfigWidget = new ResourceConfigWidget();

    private final MetadataWidget metadataWidget = new MetadataWidget();

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<RestoreEvent> restoreEvent;

    private Path    path;
    private boolean isReadOnly;

    @PostConstruct
    public void init() {
        multiPage.addWidget( view,
                             CommonConstants.INSTANCE.EditTabTitle() );

        multiPage.addPage( new Page( viewSource,
                                     CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                factModelService.call( new RemoteCallback<String>() {
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

        multiPage.addPage( new Page( resourceConfigWidget,
                                     CommonConstants.INSTANCE.ConfigTabTitle() ) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        } );

        multiPage.addPage( new Page( metadataWidget,
                                     CommonConstants.INSTANCE.MetadataTabTitle() ) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        } );
    }

    @OnStart
    public void onStart( final Path path,
                         final PlaceRequest request ) {
        this.path = path;
        this.isReadOnly = (Boolean) request.getParameter( "readOnly", false );

        loadContent();
    }

    private void loadContent() {
        factModelService.call( new RemoteCallback<FactModelContent>() {
            @Override
            public void callback( final FactModelContent content ) {

                final ModelNameHelper modelNameHelper = new ModelNameHelper();

                for ( final FactMetaModel currentModel : content.getSuperTypes() ) {
                    modelNameHelper.getTypeDescriptions().put( currentModel.getName(),
                                                               currentModel.getName() );
                }

                view.setContent( content.getFactModels(),
                                 content.getSuperTypes(),
                                 modelNameHelper );
            }
        } ).loadContent( path );

        metadataService.call( new RemoteCallback<Metadata>() {
            @Override
            public void callback( final Metadata metadata ) {
                metadataWidget.setContent( metadata, isReadOnly );
            }
        } ).getMetadata( path );

        resourceConfigService.call( new RemoteCallback<ResourceConfig>() {
            @Override
            public void callback( final ResourceConfig config ) {
                resourceConfigWidget.setContent( config, isReadOnly );
            }
        } ).getConfig( path );
    }

    @OnSave
    public void onSave() {
        if ( isReadOnly ) {
            view.alertReadOnly();
            return;
        }

        factModelService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path response ) {
                view.setNotDirty();
                resourceConfigWidget.resetDirty();
                metadataWidget.resetDirty();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            }
        } ).save( path, view.getContent(), resourceConfigWidget.getContent(), metadataWidget.getContent() );
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @IsDirty
    public boolean isDirty() {
        if ( isReadOnly ) {
            return false;
        }
        return ( view.isDirty() || resourceConfigWidget.isDirty() || metadataWidget.isDirty() );
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
        if ( isReadOnly ) {
            return "Read Only Fact Models Viewer [" + path.getFileName() + "]";
        }
        return "Fact Models Editor [" + path.getFileName() + "]";
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        final ResourceMenuBuilder builder = newResourceMenuBuilder().addValidation( new Command() {
            @Override
            public void execute() {
                LoadingPopup.showMessage( CommonConstants.INSTANCE.WaitWhileValidating() );
                factModelService.call( new RemoteCallback<BuilderResult>() {
                    @Override
                    public void callback( BuilderResult response ) {
                        final ShowBuilderErrorsWidget pop = new ShowBuilderErrorsWidget( response );
                        LoadingPopup.close();
                        pop.show();
                    }
                } ).validate( path, view.getContent() );
            }
        } );

        if ( isReadOnly ) {
            builder.addRestoreVersion( new Command() {
                @Override
                public void execute() {
                    versionService.call( new RemoteCallback<Path>() {
                        @Override
                        public void callback( final Path restored ) {
                            //TODO {porcelli} howto close current?
                            restoreEvent.fire( new RestoreEvent( restored ) );
                        }
                    } ).restore( path );
                }
            } );
        } else {
            builder.addSave( new Command() {
                @Override
                public void execute() {
                    onSave();
                }
            } );
        }

        return builder.build();
    }

    public void onRestore( @Observes RestoreEvent restore ) {
        if ( path.equals( restore.getPath() ) ) {
            loadContent();
            notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRestored() ) );
        }
    }

}
