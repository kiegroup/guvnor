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

import com.github.gwtbootstrap.client.ui.Breadcrumbs;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.guvnor.structure.client.resources.NavigatorResources;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.ParameterizedCommand;

public class Breadcrumb extends Composite {

    private final ParameterizedCommand<Path> onPathClick;
    private final ParameterizedCommand<Path> onAddClick;

    private final Breadcrumbs breadcrumbs = new Breadcrumbs();

    public Breadcrumb() {
        this( null, null );
    }

    public Breadcrumb( final ParameterizedCommand<Path> onPathClick,
                       final ParameterizedCommand<Path> onAddClick ) {
        initWidget( breadcrumbs );

        this.onPathClick = onPathClick;
        this.onAddClick = onAddClick;

        breadcrumbs.removeStyleName( Constants.BREADCRUMB );
        breadcrumbs.setStyleName( NavigatorResources.INSTANCE.css().breadcrumb() );
    }

    public void build( final String repoName,
                       final Path root,
                       final List<Path> path,
                       final Path file ) {
        breadcrumbs.clear();

        if ( repoName != null ) {
            if ( onPathClick == null ) {
                breadcrumbs.add( new ListItem( new InlineLabel( repoName ) ) {{
                    setStyleName( NavigatorResources.INSTANCE.css().repoName() );
                }} );
            } else {
                breadcrumbs.add( new NavLink( repoName ) {{
                    setStyleName( NavigatorResources.INSTANCE.css().repoName() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            onPathClick.execute( root );
                        }
                    } );
                }} );
            }
        } else if ( root != null ) {
            if ( onPathClick == null ) {
                breadcrumbs.add( new ListItem( new InlineLabel( file.getFileName() ) ) {{
                    setStyleName( NavigatorResources.INSTANCE.css().repoName() );
                }} );
            } else {
                breadcrumbs.add( new NavLink( root.getFileName() ) {{
                    setStyleName( NavigatorResources.INSTANCE.css().repoName() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            onPathClick.execute( root );
                        }
                    } );
                }} );
            }
        }

        if ( path != null && !path.isEmpty() ) {
            if ( onPathClick == null ) {
                for ( final Path activePath : path ) {
                    breadcrumbs.add( new ListItem( new InlineLabel( activePath.getFileName() ) {{
                        setStyleName( NavigatorResources.INSTANCE.css().directory() );
                    }} ) );
                }
            } else {
                for ( final Path activePath : path ) {
                    breadcrumbs.add( new NavLink( activePath.getFileName() ) {{
                        setStyleName( NavigatorResources.INSTANCE.css().directory() );
                        addClickHandler( new ClickHandler() {
                            @Override
                            public void onClick( ClickEvent event ) {
                                onPathClick.execute( activePath );
                            }
                        } );
                    }} );
                }
            }
        }

        if ( file != null && !file.equals( root ) ) {
            breadcrumbs.add( new ListItem( new InlineLabel( file.getFileName() ) ) {{
                setStyleName( NavigatorResources.INSTANCE.css().directory() );
            }} );
        }

        if ( onAddClick != null ) {
            //here
        }
    }
}