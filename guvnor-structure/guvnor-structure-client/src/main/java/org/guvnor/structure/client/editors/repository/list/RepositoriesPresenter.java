/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.list;

import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchScreen(identifier = "RepositoriesEditor")
public class RepositoriesPresenter {

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private Event<RepositoryUpdatedEvent> repositoryUpdatedEvent;

    @Inject
    private SyncBeanManager iocManager;

    public interface View
            extends
            UberView<RepositoriesPresenter> {

        void addRepository( Repository repository );

        boolean confirmDeleteRepository( Repository repository );

        void removeIfExists( Repository repository );

        void clear();

        void updateRepository( final Repository old,
                               final Repository updated );
    }

    @Inject
    public View view;

    public RepositoriesPresenter() {
    }

    @OnStartup
    public void onStartup() {
        view.init( this );
        loadContent();
    }

    private void loadContent() {
        view.clear();

        repositoryService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> response ) {
                view.clear();
                for ( final Repository repo : response ) {
                    view.addRepository( repo );
                }
            }
        } ).getRepositories();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CoreConstants.INSTANCE.RepositoryEditor();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public void updateRepository( final Repository repository,
                                  final Map<String, Object> config ) {
        repositoryService.call( new RemoteCallback<Repository>() {

            @Override
            public void callback( Repository updatedRepository ) {
                view.updateRepository( repository, updatedRepository );
                repositoryUpdatedEvent.fire( new RepositoryUpdatedEvent( repository, updatedRepository ) );
            }
        } ).updateRepository( repository, config );

    }

    public void removeRepository( final Repository repository ) {
        if ( view.confirmDeleteRepository( repository ) ) {
            repositoryService.call().removeRepository( repository.getAlias() );
        }
    }

    public void newRepository( @Observes final NewRepositoryEvent event ) {
        vfsService.call( new RemoteCallback<Map>() {
            @Override
            public void callback( Map response ) {
                view.addRepository( event.getNewRepository() );
            }
        } ).readAttributes( event.getNewRepository().getRoot() );
    }

    public void removeRootDirectory( @Observes RepositoryRemovedEvent event ) {
        view.removeIfExists( event.getRepository() );
    }

    public void onSystemRepositoryChanged( @Observes SystemRepositoryChangedEvent event ) {
        loadContent();
    }

}