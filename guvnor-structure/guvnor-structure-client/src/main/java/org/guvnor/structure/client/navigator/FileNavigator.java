/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.client.navigator;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.resources.NavigatorResources;
import org.guvnor.structure.navigator.DataContent;
import org.guvnor.structure.navigator.FileNavigatorService;
import org.guvnor.structure.navigator.NavigatorContent;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
public class FileNavigator extends Composite {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<FileNavigatorService> navigatorService;

    private FlowPanel container = new FlowPanel();
    private FlexTable navigator = null;
    private NavigatorOptions options = NavigatorOptions.DEFAULT;
    private boolean isListingRepos = false;
    private ParameterizedCommand<Path> fileActionCommand = null;

    public FileNavigator() {
        initWidget( container );
    }

    public void setOptions( final NavigatorOptions options ) {
        this.options = options;
    }

    public void setFileActionCommand( final ParameterizedCommand<Path> fileActionCommand ) {
        this.fileActionCommand = fileActionCommand;
    }

    public void loadContent( final Path path ) {
        if ( path == null && options.listRepositories() ) {
            navigatorService.call( new RemoteCallback<List<Repository>>() {
                @Override
                public void callback( final List<Repository> response ) {
                    container.clear();
                    navigator = new FlexTable();
                    navigator.setStyleName( NavigatorResources.INSTANCE.css().navigator() );

                    isListingRepos = true;
                    setupContent( response );
                }
            } ).listRepositories();
        } else if ( path != null ) {
            navigatorService.call( new RemoteCallback<NavigatorContent>() {
                @Override
                public void callback( final NavigatorContent response ) {

                    container.clear();
                    navigator = new FlexTable();
                    navigator.setStyleName( NavigatorResources.INSTANCE.css().navigator() );

                    isListingRepos = false;
                    setupBreadcrumb( response, path );

                    if ( !path.equals( response.getRoot() ) ) {
                        setupUpFolder( response );
                    } else if ( options.listRepositories() ) {
                        setupUpFolder();
                    }

                    setupContent( response );
                }
            } ).listContent( path );
        }
    }

    public boolean isListingRepos() {
        return this.isListingRepos;
    }

    private void setupBreadcrumb( final NavigatorContent response,
                                  final Path path ) {
        if ( !options.showBreadcrumb() ) {
            return;
        }

        final ParameterizedCommand<Path> command;
        if ( options.breadcrumbWithLink() ) {
            command = new ParameterizedCommand<Path>() {
                @Override
                public void execute( final Path path ) {
                    loadContent( path );
                }
            };
        } else {
            command = null;
        }

        container.add( new Breadcrumb( command, null ) {{
            build( response.getRepoName(), response.getRoot(), response.getBreadcrumbs(), path );
        }} );
    }

    private void setupContent( final NavigatorContent content ) {
        int base = navigator.getRowCount();
        for ( int i = 0; i < content.getContent().size(); i++ ) {
            final DataContent dataContent = content.getContent().get( i );
            if ( dataContent.isDirectory() && options.showDirectories() ) {
                createDirectory( base + i, dataContent );
            } else if ( options.showFiles() ) {
                createFile( base + i, dataContent );
            }
        }

        container.add( navigator );
    }

    private void setupContent( final List<Repository> response ) {
        for ( int i = 0; i < response.size(); i++ ) {
            final Repository repository = response.get( i );
            createElement( i, repository, IconType.BOOK, NavigatorResources.INSTANCE.css().navigatorFolderIcon(), new Command() {
                @Override
                public void execute() {
                    loadContent( repository.getRoot() );
                }
            } );
        }
        container.add( navigator );
    }

    private void setupUpFolder( final NavigatorContent content ) {
        if ( options.allowUpLink() ) {
            if ( content.getBreadcrumbs().size() == 0 ) {
                createUpFolder( content.getRoot() );
            } else {
                createUpFolder( content.getBreadcrumbs().get( content.getBreadcrumbs().size() - 1 ) );
            }
        }
    }

    private void setupUpFolder() {
        if ( options.allowUpLink() ) {
            createUpFolder( null );
        }
    }

    private void createFile( final int row,
                             final DataContent dataContent ) {

        createElement( row, dataContent, IconType.FILE_ALT, NavigatorResources.INSTANCE.css().navigatoFileIcon(), new Command() {
            @Override
            public void execute() {
                if ( fileActionCommand != null ) {
                    fileActionCommand.execute( dataContent.getPath() );
                } else {
                    placeManager.goTo( new PathPlaceRequest( dataContent.getPath() ) );
                }
            }
        } );
    }

    private void createDirectory( final int row,
                                  final DataContent dataContent ) {
        createElement( row, dataContent, IconType.FOLDER_CLOSE, NavigatorResources.INSTANCE.css().navigatorFolderIcon(), new Command() {
            @Override
            public void execute() {
                loadContent( dataContent.getPath() );
            }
        } );
    }

    private void createUpFolder( final Path path ) {
        int col = 0;
        navigator.setText( 0, col, "" );

        navigator.setWidget( 0, ++col, new Anchor( ".." ) {{
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    loadContent( path );
                }
            } );
        }} );

        if ( options.showItemAge() ) {
            navigator.setText( 0, ++col, "" );
        }
        if ( options.showItemMessage() ) {
            navigator.setText( 0, ++col, "" );
        }
    }

    private void createElement( final int row,
                                final DataContent dataContent,
                                final IconType iconType,
                                final String style,
                                final Command onClick ) {
        int col = 0;
        navigator.setWidget( row, col, new Icon( iconType ) {{
            addStyleName( style );
        }} );
        navigator.setWidget( row, ++col, new Anchor( dataContent.getPath().getFileName() ) {{
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    onClick.execute();
                }
            } );
        }} );

        if ( options.showItemAge() ) {
            navigator.setText( row, ++col, dataContent.getAge() );
        }

        if ( options.showItemMessage() ) {
            final Element messageCol = DOM.createSpan();
            messageCol.addClassName( NavigatorResources.INSTANCE.css().navigatorMessage() );
            {
                final Element message = DOM.createSpan();
                message.addClassName( NavigatorResources.INSTANCE.css().message() );
                message.setInnerText( dataContent.getLastMessage() );
                messageCol.appendChild( message );

                if ( options.showItemLastUpdater() ) {
                    final Element colOpen = DOM.createSpan();
                    colOpen.addClassName( NavigatorResources.INSTANCE.css().message() );
                    colOpen.setInnerText( " [" );
                    messageCol.appendChild( colOpen );

                    final Anchor commiterRef = new Anchor( dataContent.getLastCommiter() );
                    DOM.sinkEvents( commiterRef.getElement(), Event.ONCLICK );
                    DOM.setEventListener( commiterRef.getElement(), new EventListener() {
                        public void onBrowserEvent( Event event ) {
                        }
                    } );
                    messageCol.appendChild( commiterRef.getElement() );

                    final Element colClose = DOM.createSpan();
                    colClose.addClassName( NavigatorResources.INSTANCE.css().message() );
                    colClose.setInnerText( "]" );
                    messageCol.appendChild( colClose );
                }
            }

            navigator.setWidget( row, ++col, new Widget() {{
                setElement( messageCol );
            }} );
        }
    }

    private void createElement( final int row,
                                final Repository repository,
                                final IconType iconType,
                                final String style,
                                final Command onClick ) {
        int col = 0;
        navigator.setWidget( row, col, new Icon( iconType ) {{
            addStyleName( style );
        }} );
        navigator.setWidget( row, ++col, new Anchor( repository.getAlias() ) {{
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    onClick.execute();
                }
            } );
        }} );
    }
}