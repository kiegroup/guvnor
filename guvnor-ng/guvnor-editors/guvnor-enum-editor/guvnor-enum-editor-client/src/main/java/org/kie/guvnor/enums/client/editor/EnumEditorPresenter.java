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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.DefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.menu.FileMenuBuilder;
import org.kie.guvnor.commons.ui.client.popups.file.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.popups.file.SaveOperationService;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.enums.client.type.EnumResourceType;
import org.kie.guvnor.enums.model.EnumModelContent;
import org.kie.guvnor.enums.service.EnumService;
import org.kie.guvnor.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.guvnor.metadata.client.resources.i18n.MetadataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.MetadataService;
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
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Enum Editor Presenter
 */
@Dependent
@WorkbenchEditor(identifier = "EnumEditor", supportedTypes = { EnumResourceType.class })
public class EnumEditorPresenter {

    @Inject
    private EnumEditorView view;

    @Inject
    private ViewSourceView viewSource;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<EnumService> enumService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;
    private Menus menus;

    private final MetadataWidget metadataWidget = new MetadataWidget();

    private Path path;
    private PlaceRequest place;
    private boolean isReadOnly;

    @OnStart
    public void onStart( final Path path,
                         final PlaceRequest place ) {
        this.path = path;
        this.place = place;
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        makeMenuBar();

        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

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

        multiPage.addWidget( metadataWidget,
                             MetadataConstants.INSTANCE.Metadata() );

        enumService.call( getModelSuccessCallback(),
                          new DefaultErrorCallback() ).loadContent( path );

        metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                           isReadOnly ),
                              new DefaultErrorCallback() ).getMetadata( path );
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

    private RemoteCallback<EnumModelContent> getModelSuccessCallback() {
        return new RemoteCallback<EnumModelContent>() {

            @Override
            public void callback( final EnumModelContent response ) {
                view.setContent( response.getModel().getDRL() );
                view.hideBusyIndicator();
            }
        };
    }

    @OnSave
    public void onSave() {
        if ( isReadOnly ) {
            view.alertReadOnly();
            return;
        }

        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 enumService.call( new RemoteCallback<Path>() {
                                                     @Override
                                                     public void callback( final Path response ) {
                                                         view.setNotDirty();
                                                         view.hideBusyIndicator();
                                                         notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
                                                     }
                                                 } ).save( path,
                                                           view.getContent(),
                                                           metadataWidget.getContent(),
                                                           commitMessage );
                                             }
                                         } );
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
    public Menus getMenus() {
        return menus;
    }

}
