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

package org.guvnor.structure.client.editors.filenavigator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.guvnor.structure.client.navigator.FileNavigator;
import org.guvnor.structure.client.navigator.NavigatorOptions;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.vfs.VFSService;

@Dependent
public class FileNavigatorView
        extends Composite
        implements FileNavigatorPresenter.View {

    private FileNavigatorPresenter presenter = null;

    @Inject
    private Caller<VFSService> vfsServices;

    @Inject
    private FileNavigator fileNavigator;

    @PostConstruct
    public void init() {
        initWidget( fileNavigator );
        this.fileNavigator.setOptions( new NavigatorOptions() {
            @Override
            public boolean showFiles() {
                return true;
            }

            @Override
            public boolean showDirectories() {
                return true;
            }

            @Override
            public boolean listRepositories() {
                return true;
            }

            @Override
            public boolean allowUpLink() {
                return true;
            }

            @Override
            public boolean showBreadcrumb() {
                return true;
            }

            @Override
            public boolean breadcrumbWithLink() {
                return true;
            }

            @Override
            public boolean allowAddIconOnBreadcrumb() {
                return true;
            }

            @Override
            public boolean showItemAge() {
                return true;
            }

            @Override
            public boolean showItemMessage() {
                return true;
            }

            @Override
            public boolean showItemLastUpdater() {
                return true;
            }
        } );
        this.fileNavigator.loadContent( null );
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void removeIfExists( final Repository repo ) {
        if ( fileNavigator.isListingRepos() ) {
            this.fileNavigator.loadContent( null );
        }
    }

    @Override
    public void addNewRepository( final Repository repo ) {
        if ( fileNavigator.isListingRepos() ) {
            this.fileNavigator.loadContent( null );
        }
    }
}