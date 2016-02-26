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

package org.guvnor.structure.client.editors.repository.list;

import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.structure.client.editors.context.GuvnorStructureContext;
import org.guvnor.structure.client.editors.context.GuvnorStructureContextChangeHandler;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchScreen(identifier = "RepositoriesEditor")
public class RepositoriesPresenter
        implements GuvnorStructureContextChangeHandler {

    @Inject
    private Caller<RepositoryService> repositoryService;

    private GuvnorStructureContext guvnorStructureContext;


    public interface View
            extends
            UberView<RepositoriesPresenter> {

        void addRepository( Repository repository,
                            String branch);

        boolean confirmDeleteRepository( Repository repository );

        void removeIfExists( Repository repository );

        void clear();
    }

    @Inject
    public View view;

    public RepositoriesPresenter() {
    }

    @Inject
    public RepositoriesPresenter( final GuvnorStructureContext guvnorStructureContext ) {
        this.guvnorStructureContext = guvnorStructureContext;
        guvnorStructureContext.addGuvnorStructureContextChangeHandler( this );
    }

    @OnStartup
    public void onStartup() {
        view.init( this );
        loadContent();
    }

    private void loadContent() {
        view.clear();

        guvnorStructureContext.getRepositories( new Callback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> result ) {
                view.clear();
                for ( final Repository repo : result ) {
                    view.addRepository( repo,
                                        guvnorStructureContext.getCurrentBranch( repo.getAlias() ) );
                }
            }
        } );
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CoreConstants.INSTANCE.RepositoryEditor();
    }

    @Override
    public void onNewRepositoryAdded( final Repository repository ) {
        view.addRepository( repository,
                            guvnorStructureContext.getCurrentBranch( repository.getAlias() ));
    }

    public void removeRepository( final Repository repository ) {
        if ( view.confirmDeleteRepository( repository ) ) {
            repositoryService.call().removeRepository( repository.getAlias() );
        }
    }

    @Override
    public void onRepositoryDeleted( final Repository repository ) {
        view.removeIfExists( repository );
    }

    public void onSystemRepositoryChanged( @Observes SystemRepositoryChangedEvent event ) {
        loadContent();
    }

}