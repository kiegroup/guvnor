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
package org.guvnor.organizationalunit.manager.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import org.guvnor.organizationalunit.manager.client.editor.popups.AddOrganizationalUnitPopup;
import org.guvnor.organizationalunit.manager.client.editor.popups.EditOrganizationalUnitPopup;
import org.guvnor.organizationalunit.manager.client.resources.i18n.OrganizationalUnitManagerConstants;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;

@ApplicationScoped
//The identifier has been preserved from kie-wb-common so existing .niogit System repositories are not broken
@WorkbenchScreen(identifier = "org.guvnor.organizationalunit.manager.OrganizationalUnitManager")
public class OrganizationalUnitManagerPresenterImpl implements OrganizationalUnitManagerPresenter {

    @Inject
    private OrganizationalUnitManagerView view;

    @Inject
    private Caller<OrganizationalUnitService> organizationalUnitService;

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private AddOrganizationalUnitPopup addOrganizationalUnitPopup;

    @Inject
    private EditOrganizationalUnitPopup editOrganizationalUnitPopup;

    private Collection<Repository> allRepositories;

    private Collection<OrganizationalUnit> allOrganizationalUnits;

    @PostConstruct
    public void setup() {
        addOrganizationalUnitPopup.init( this );
        editOrganizationalUnitPopup.init( this );
    }

    @OnStartup
    public void onStartup() {
        view.reset();
        view.showBusyIndicator( OrganizationalUnitManagerConstants.INSTANCE.Wait() );
        repositoryService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                OrganizationalUnitManagerPresenterImpl.this.allRepositories = repositories;
                loadOrganizationalUnits();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories();
    }

    @OnOpen
    public void onOpen() {
        view.reset();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return OrganizationalUnitManagerConstants.INSTANCE.OrganizationalUnitManagerTitle();
    }

    @WorkbenchPartView
    public UberView<OrganizationalUnitManagerPresenter> getView() {
        return view;
    }

    @Override
    public void loadOrganizationalUnits() {
        view.showBusyIndicator( OrganizationalUnitManagerConstants.INSTANCE.Wait() );
        organizationalUnitService.call( new RemoteCallback<Collection<OrganizationalUnit>>() {
            @Override
            public void callback( final Collection<OrganizationalUnit> organizationalUnits ) {
                OrganizationalUnitManagerPresenterImpl.this.allOrganizationalUnits = organizationalUnits;
                view.setOrganizationalUnits( organizationalUnits );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getOrganizationalUnits();
    }

    @Override
    public void organizationalUnitSelected( final OrganizationalUnit organizationalUnit ) {
        //Reload rather than using cached Object as it could have been changed server-side (adding/deleting Repositories)
        view.showBusyIndicator( OrganizationalUnitManagerConstants.INSTANCE.Wait() );
        organizationalUnitService.call( new RemoteCallback<OrganizationalUnit>() {
            @Override
            public void callback( final OrganizationalUnit organizationalUnit ) {
                view.setOrganizationalUnitRepositories( organizationalUnit.getRepositories(),
                                                        getAvailableRepositories() );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getOrganizationalUnit( organizationalUnit.getName() );
    }

    private Collection<Repository> getAvailableRepositories() {
        final Collection<Repository> availableRepositories = new ArrayList<Repository>();
        availableRepositories.addAll( allRepositories );
        for ( OrganizationalUnit ou : allOrganizationalUnits ) {
            availableRepositories.removeAll( ou.getRepositories() );
        }
        return availableRepositories;
    }

    @Override
    public void addNewOrganizationalUnit() {
        addOrganizationalUnitPopup.show();
    }

    @Override
    public void checkIfOrganizationalUnitExists( final String organizationalUnitName,
                                                 final Command onSuccessCommand,
                                                 final Command onFailureCommand ) {
        //Check the Organizational Unit doesn't already exist
        organizationalUnitService.call( new RemoteCallback<OrganizationalUnit>() {

            @Override
            public void callback( final OrganizationalUnit organizationalUnit ) {
                if ( organizationalUnit == null ) {
                    onSuccessCommand.execute();
                } else {
                    onFailureCommand.execute();
                }
            }
        } ).getOrganizationalUnit( organizationalUnitName );
    }

    @Override
    public void createNewOrganizationalUnit( final String organizationalUnitName,
                                             final String organizationalUnitOwner ) {
        final Collection<Repository> repositories = new ArrayList<Repository>();
        view.showBusyIndicator( OrganizationalUnitManagerConstants.INSTANCE.Wait() );
        organizationalUnitService.call( new RemoteCallback<OrganizationalUnit>() {

            @Override
            public void callback( final OrganizationalUnit newOrganizationalUnit ) {
                allOrganizationalUnits.add( newOrganizationalUnit );
                view.addOrganizationalUnit( newOrganizationalUnit );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).createOrganizationalUnit( organizationalUnitName,
                                                                                        organizationalUnitOwner,
                                                                                        repositories );
    }

    @Override
    public void editOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        editOrganizationalUnitPopup.setOrganizationalUnit( organizationalUnit );
        editOrganizationalUnitPopup.show();
    }

    @Override
    public void saveOrganizationalUnit( final String organizationalUnitName,
                                        final String organizationalUnitOwner ) {
        view.showBusyIndicator( OrganizationalUnitManagerConstants.INSTANCE.Wait() );
        organizationalUnitService.call( new RemoteCallback<Void>() {

            @Override
            public void callback( final Void response ) {
                loadOrganizationalUnits();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).updateOrganizationalUnitOwner( organizationalUnitName,
                                                                                             organizationalUnitOwner );
    }

    @Override
    public void deleteOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        view.showBusyIndicator( OrganizationalUnitManagerConstants.INSTANCE.Wait() );
        organizationalUnitService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void v ) {
                allOrganizationalUnits.remove( organizationalUnit );
                view.deleteOrganizationalUnit( organizationalUnit );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).removeOrganizationalUnit( organizationalUnit.getName() );
    }

    @Override
    public void addOrganizationalUnitRepository( final OrganizationalUnit organizationalUnit,
                                                 final Repository repository ) {
        view.showBusyIndicator( OrganizationalUnitManagerConstants.INSTANCE.Wait() );
        organizationalUnit.getRepositories().add( repository );
        organizationalUnitService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void v ) {
                view.setOrganizationalUnitRepositories( organizationalUnit.getRepositories(),
                                                        getAvailableRepositories() );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).addRepository( organizationalUnit,
                                                                             repository );
    }

    @Override
    public void removeOrganizationalUnitRepository( final OrganizationalUnit organizationalUnit,
                                                    final Repository repository ) {
        view.showBusyIndicator( OrganizationalUnitManagerConstants.INSTANCE.Wait() );
        organizationalUnit.getRepositories().remove( repository );
        organizationalUnitService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void v ) {
                view.setOrganizationalUnitRepositories( organizationalUnit.getRepositories(),
                                                        getAvailableRepositories() );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).removeRepository( organizationalUnit,
                                                                                repository );
    }

    public void onRepositoryAddedEvent( @Observes NewRepositoryEvent event ) {
        onStartup();
    }

    public void onRepositoryRemovedEvent( @Observes RepositoryRemovedEvent event ) {
        onStartup();
    }

}
