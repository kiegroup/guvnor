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

package org.kie.guvnor.commons.ui.client.menu;

import com.google.gwt.core.client.Callback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DeletePopup;
import org.kie.guvnor.commons.ui.client.handlers.RenameCommand;
import org.kie.guvnor.commons.ui.client.handlers.RenamePopup;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.services.file.GenericFileService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceRenamedEvent;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.uberfire.client.workbench.widgets.menu.MenuFactory.newSimpleItem;

/**
 *
 */
@Dependent
public class FileMenuBuilderImpl
        implements FileMenuBuilder {

    @Inject
    private RestoreVersionCommandProvider restoreVersionCommandProvider;

    @Inject
    private Caller<GenericFileService> fileService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ResourceDeletedEvent> resourceDeletedEvent;

    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Inject
    private PlaceManager placeManager;

    private Command saveCommand     = null;
    private Command restoreCommand  = null;
    private Command validateCommand = null;
    private Command copyCommand     = null;
    private Command deleteCommand   = null;
    private Command renameCommand   = null;

    private Command moveCommand = null;

    public FileMenuBuilder addValidation( final Command command ) {
        this.validateCommand = command;
        return this;
    }

    public FileMenuBuilder addSave( final Command command ) {
        this.saveCommand = command;
        return this;
    }

    public FileMenuBuilder addRestoreVersion( final Path path ) {
        this.restoreCommand = restoreVersionCommandProvider.getCommand( path );
        return this;
    }

    public FileMenuBuilder addRestoreVersion( final Command command ) {
        this.restoreCommand = command;
        return this;
    }

    public FileMenuBuilder addCopy( final Command command ) {
        this.copyCommand = command;
        return this;
    }

    @Override
    public FileMenuBuilder addRename( final Path path ) {
        addRename( path, new Callback<Path, Void>() {
            @Override
            public void onFailure( Void reason ) {

            }

            @Override
            public void onSuccess( Path result ) {

            }
        } );

        return this;
    }

    @Override
    public FileMenuBuilder addRename( final Path path,
                                      final Callback<Path, Void> callback ) {
        this.copyCommand = new Command() {
            @Override
            public void execute() {
                RenamePopup popup = new RenamePopup( new RenameCommand() {
                    @Override
                    public void execute( final String newName,
                                         final String comment ) {
                        fileService.call( new RemoteCallback<Path>() {
                            @Override
                            public void callback( Path response ) {
                                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
                                resourceRenamedEvent.fire( new ResourceRenamedEvent( path,
                                                                                     response ) );
                                callback.onSuccess( response );
                            }
                        } ).rename( path,
                                    newName,
                                    comment );
                    }
                } );

                popup.show();
            }
        };

        return this;
    }

    @Override
    public FileMenuBuilder addCopy( final Path path ) {
        addCopy( path, new Callback<Path, Void>() {
            @Override
            public void onFailure( Void reason ) {

            }

            @Override
            public void onSuccess( Path result ) {

            }
        } );

        return this;
    }

    @Override
    public FileMenuBuilder addCopy( final Path path,
                                    final Callback<Path, Void> callback ) {
        this.copyCommand = new Command() {
            @Override
            public void execute() {
                RenamePopup popup = new RenamePopup( new RenameCommand() {
                    @Override
                    public void execute( final String newName,
                                         final String comment ) {
                        fileService.call(
                                new RemoteCallback<Path>() {
                                    @Override
                                    public void callback( Path result ) {
                                        notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
                                        resourceRenamedEvent.fire( new ResourceRenamedEvent( path,
                                                                                             result ) );

                                        callback.onSuccess( result );
                                    }
                                }
                                        ).copy( path, newName, comment );
                    }
                } );
                popup.show();
            }
        };

        return this;
    }

    public FileMenuBuilder addRename( final Command command ) {
        this.renameCommand = command;
        return this;
    }

    public FileMenuBuilder addDelete( final Command command ) {
        this.deleteCommand = command;
        return this;
    }

    @Override
    public FileMenuBuilder addDelete( final Path path ) {
        addDelete( path, new Callback<Void, Void>() {
            @Override
            public void onFailure( Void reason ) {

            }

            @Override
            public void onSuccess( Void result ) {

            }
        } );

        return this;
    }

    @Override
    public FileMenuBuilder addDelete( final Path path,
                                      final Callback<Void, Void> callback ) {
        this.deleteCommand = new Command() {
            @Override
            public void execute() {
                DeletePopup popup = new DeletePopup( new CommandWithCommitMessage() {
                    @Override
                    public void execute( final String comment ) {
                        fileService.call( new RemoteCallback<Path>() {
                            @Override
                            public void callback( Path response ) {
                                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
                                resourceDeletedEvent.fire( new ResourceDeletedEvent( path ) );
                                placeManager.closePlace( new PathPlaceRequest( path ) );
                                callback.onSuccess( null );
                            }
                        } ).delete( path, comment );
                    }
                } );

                popup.show();
            }
        };
        return this;
    }

    public FileMenuBuilder addMove( final Command command ) {
        this.moveCommand = command;
        return this;
    }

    public Menus build() {
        return MenuFactory
                .newTopLevelMenu( CommonConstants.INSTANCE.File() )
                .withItems( getItems() )
                .endMenu().build();
    }

    private List<MenuItem> getItems() {
        final List<MenuItem> menuItems = new ArrayList<MenuItem>();
        if ( validateCommand != null ) {
            menuItems.add( newSimpleItem( CommonConstants.INSTANCE.Validate() )
                                   .respondsWith( validateCommand )
                                   .endMenu().build().getItems().get( 0 ) );
        }

        if ( saveCommand != null ) {
            menuItems.add( newSimpleItem( CommonConstants.INSTANCE.Save() )
                                   .respondsWith( saveCommand )
                                   .endMenu().build().getItems().get( 0 ) );
        }

        if ( restoreCommand != null ) {
            menuItems.add( newSimpleItem( CommonConstants.INSTANCE.Restore() )
                                   .respondsWith( restoreCommand )
                                   .endMenu().build().getItems().get( 0 ) );
        }

        if ( copyCommand != null ) {
            menuItems.add( newSimpleItem( CommonConstants.INSTANCE.Copy() )
                                   .respondsWith( copyCommand )
                                   .endMenu().build().getItems().get( 0 ) );
        }

        if ( deleteCommand != null ) {
            menuItems.add( newSimpleItem( CommonConstants.INSTANCE.Delete() )
                                   .respondsWith( deleteCommand )
                                   .endMenu().build().getItems().get( 0 ) );
        }

        if ( renameCommand != null ) {
            menuItems.add( newSimpleItem( CommonConstants.INSTANCE.Rename() )
                                   .respondsWith( renameCommand )
                                   .endMenu().build().getItems().get( 0 ) );
        }

        if ( moveCommand != null ) {
            menuItems.add( newSimpleItem( CommonConstants.INSTANCE.Move() )
                                   .respondsWith( moveCommand )
                                   .endMenu().build().getItems().get( 0 ) );
        }

        return menuItems;
    }
}
