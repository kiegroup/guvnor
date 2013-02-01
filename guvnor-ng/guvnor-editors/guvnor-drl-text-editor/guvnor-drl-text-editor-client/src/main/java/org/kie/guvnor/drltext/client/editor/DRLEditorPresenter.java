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

package org.kie.guvnor.drltext.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.save.SaveCommand;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
import org.kie.guvnor.configresource.client.widget.ResourceConfigWidget;
import org.kie.guvnor.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.drltext.client.resources.i18n.DRLTextEditorConstants;
import org.kie.guvnor.drltext.service.DRLTextEditorService;
import org.kie.guvnor.errors.client.widget.ShowBuilderErrorsWidget;
import org.kie.guvnor.metadata.client.resources.i18n.MetadataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.config.ResourceConfigService;
import org.kie.guvnor.services.config.model.ResourceConfig;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
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

import static org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder.*;

/**
 * This is the default rule editor widget (just text editor based).
 */
@Dependent
@WorkbenchEditor(identifier = "DRLEditor", fileTypes = "*.drl")
public class DRLEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final String content,
                         final DataModelOracle dataModel );

        String getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    @Inject
    private Caller<DRLTextEditorService> drlTextEditorService;

    @Inject
    private Caller<DataModelService> dataModelService;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private Caller<ResourceConfigService> resourceConfigService;

    @Inject
    private View view;

    private final MetadataWidget metadataWidget = new MetadataWidget();

    private final ResourceConfigWidget resourceConfigWidget = new ResourceConfigWidget();

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<InvalidateDMOProjectCacheEvent> invalidateProjectCache;

    private Path path;

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;

        multiPage.addWidget( view, DRLTextEditorConstants.INSTANCE.DRL() );

        dataModelService.call( new RemoteCallback<DataModelOracle>() {
            @Override
            public void callback( final DataModelOracle model ) {
                drlTextEditorService.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( String response ) {
                        if ( response == null || response.isEmpty() ) {
                            view.setContent( null,
                                             model );
                        } else {
                            view.setContent( response,
                                             model );
                        }
                    }
                } ).load( path );
            }
        } ).getDataModel( path );

        multiPage.addPage( new Page( resourceConfigWidget,
                                     CommonConstants.INSTANCE.ConfigTabTitle() ) {
            @Override
            public void onFocus() {
                resourceConfigService.call( new RemoteCallback<ResourceConfig>() {
                    @Override
                    public void callback( final ResourceConfig config ) {
                        resourceConfigWidget.setContent( config,
                                                         false );
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
                                                   false );
                    }
                } ).getMetadata( path );
            }

            @Override
            public void onLostFocus() {
                // Nothing to do here
            }
        } );
    }

    @OnSave
    public void onSave() {
        new SaveOperationService().save(path, new SaveCommand() {
            @Override
            public void execute(final String commitMessage) {
                drlTextEditorService.call(new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path response) {
                        view.setNotDirty();
                        resourceConfigWidget.resetDirty();
                        metadataWidget.resetDirty();
                        notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
                    }
                }).save(path,
                        view.getContent(),
                        resourceConfigWidget.getContent(),
                        metadataWidget.getContent(),
                        commitMessage);
            }
        });
    }
    
    public void onDelete() {
        new SaveOperationService().save(path, new SaveCommand() {
            @Override
            public void execute(final String commitMessage) {
                drlTextEditorService.call(new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path response) {
                        view.setNotDirty();
                        resourceConfigWidget.resetDirty();
                        metadataWidget.resetDirty();
                        notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemDeletedSuccessfully()));
                    }
                }).delete(path,
                          commitMessage);
            }
        });
    }
    
    public void onRename() {
        new SaveOperationService().save(path, new SaveCommand() {
            @Override
            public void execute(final String commitMessage) {
                drlTextEditorService.call(new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path response) {
                        view.setNotDirty();
                        resourceConfigWidget.resetDirty();
                        metadataWidget.resetDirty();
                        notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemRenamedSuccessfully()));
                    }
                }).rename(path,
                          "newName",
                          commitMessage);
            }
        });
    }
    
    public void onCopy() {
        new SaveOperationService().save(path, new SaveCommand() {
            @Override
            public void execute(final String commitMessage) {
                drlTextEditorService.call(new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path response) {
                        view.setNotDirty();
                        resourceConfigWidget.resetDirty();
                        metadataWidget.resetDirty();
                        notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCopiedSuccessfully()));
                    }
                }).copy(path,
                        "newName",
                        commitMessage);
            }
        });
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
        return "DRL Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        return newResourceMenuBuilder().addValidation( new Command() {
            @Override
            public void execute() {
                LoadingPopup.showMessage( CommonConstants.INSTANCE.WaitWhileValidating() );
                drlTextEditorService.call( new RemoteCallback<BuilderResult>() {
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
        } ).addDelete( new Command() {
            @Override
            public void execute() {
                onDelete();
            }
        } ).addRename( new Command() {
            @Override
            public void execute() {
                onRename();
            }
        } ).addCopy( new Command() {
            @Override
            public void execute() {
                onCopy();
            }
        } ).addMove( new Command() {
            @Override
            public void execute() {
                onSave();
            }
        } ).build();
    }

}
