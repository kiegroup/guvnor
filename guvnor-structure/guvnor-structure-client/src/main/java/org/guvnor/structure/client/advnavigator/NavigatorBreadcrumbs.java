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

package org.guvnor.structure.client.advnavigator;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.guvnor.structure.client.resources.NavigatorResources;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.UberBreadcrumbs;
import org.uberfire.mvp.ParameterizedCommand;

public class NavigatorBreadcrumbs extends Composite {

    public static enum Mode {
        REGULAR, HEADER, SECOND_LEVEL
    }

    private final UberBreadcrumbs breadcrumbs = new UberBreadcrumbs();

    public NavigatorBreadcrumbs() {
        this( Mode.REGULAR );
    }

    public NavigatorBreadcrumbs( final Mode mode ) {
        initWidget( breadcrumbs );
        if ( mode != null ) {
            switch ( mode ) {
                case HEADER:
                    breadcrumbs.removeStyleName( Constants.BREADCRUMB );
                    breadcrumbs.setStyleName( NavigatorResources.INSTANCE.css().breadcrumb() );
                    break;
                case SECOND_LEVEL:
                    breadcrumbs.addStyleName( NavigatorResources.INSTANCE.css().breadcrumb2ndLevel() );
                    break;
            }
        }
    }

    public void build( final Path root,
                       final List<Path> path,
                       final Path file,
                       final ParameterizedCommand<Path> onPathClick,
                       final Dropdown... headers ) {

        build( headers );

        if ( path != null && !path.isEmpty() ) {
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

        if ( file != null && !file.equals( root ) ) {
            breadcrumbs.add( new ListItem( new InlineLabel( file.getFileName() ) ) {{
                setStyleName( NavigatorResources.INSTANCE.css().directory() );
            }} );
        }
    }

    public void build( final Dropdown... headers ) {
        breadcrumbs.clear();

        for ( int i = 0; i < headers.length; i++ ) {
            final Dropdown header = headers[ i ];
            header.addStyleName( NavigatorResources.INSTANCE.css().breadcrumbHeader() );
            if ( i + 1 == headers.length ) {
                header.setRightDropdown( true );
            }
            breadcrumbs.add( header );
        }
    }
}

