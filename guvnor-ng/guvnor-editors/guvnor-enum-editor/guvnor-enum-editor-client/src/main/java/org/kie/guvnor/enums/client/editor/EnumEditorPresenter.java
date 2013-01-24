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

package org.kie.guvnor.enums.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.enums.service.EnumService;
import org.kie.guvnor.errors.client.widget.ShowBuilderErrorsWidget;
import org.kie.guvnor.metadata.client.resources.i18n.MetaDataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.kie.guvnor.viewsource.client.screen.ViewSourceView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
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
 * This is the default rule editor widget (just text editor based) - more to come later.
 */
@Dependent
@WorkbenchEditor(identifier = "EnumEditor", fileTypes = "*.enumeration")
public class EnumEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( String content );

        String getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    @Inject
    private View view;

    @Inject
    private ViewSourceView viewSource;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<VFSService> vfs;

    @Inject
    private Caller<EnumService> enumService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<MetadataService> metadataService;

    private final MetadataWidget metadataWidget = new MetadataWidget();

    private Path path;

    @PostConstruct
    public void init() {
        multiPage.addWidget( view,
                             CommonConstants.INSTANCE.EditTabTitle() );
        multiPage.addPage( new Page( viewSource,
                                     CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                viewSource.setContent( view.getContent() );
            }

            @Override
            public void onLostFocus() {
                viewSource.clear();
            }
        } );
        multiPage.addPage(new Page(metadataWidget, MetaDataConstants.INSTANCE.Metadata()) {
            @Override
            public void onFocus() {
                metadataService.call(
                        new RemoteCallback<Metadata>() {
                            @Override
                            public void callback(Metadata metadata) {
                                metadataWidget.setContent(metadata, false);
                            }
                        }
                ).getMetadata(path);
            }

            @Override
            public void onLostFocus() {
                // Nothing to do here
            }
        });
    }

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;
        vfs.call( new RemoteCallback<String>() {
            @Override
            public void callback( String response ) {
                if ( response == null || response.isEmpty() ) {
                    view.setContent( null );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString( path );
    }

    @OnSave
    public void onSave() {
        enumService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void response ) {
                view.setNotDirty();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            }
        } ).save( path,
                  view.getContent() );
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
        return "Enum Editor [" + path.getFileName() + "]";
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
                LoadingPopup.showMessage( "Wait while validating..." );
                enumService.call( new RemoteCallback<BuilderResult>() {
                    @Override
                    public void callback( BuilderResult response ) {
                        final ShowBuilderErrorsWidget pop = new ShowBuilderErrorsWidget( response );
                        LoadingPopup.close();
                        pop.show();
                    }
                } ).validate( path, view.getContent() );
            }
        } ).addSave( new Command() {
            @Override
            public void execute() {
                onSave();
            }
        } ).build();
    }
}
