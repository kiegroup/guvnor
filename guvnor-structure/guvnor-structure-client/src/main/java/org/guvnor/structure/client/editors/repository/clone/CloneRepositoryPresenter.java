/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.client.editors.repository.clone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import org.guvnor.structure.client.editors.repository.RepositoryPreferences;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryAlreadyExistsException;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.util.URIUtil;

@Dependent
public class CloneRepositoryPresenter implements CloneRepositoryView.Presenter {

    private RepositoryPreferences repositoryPreferences;

    private CloneRepositoryView view;

    private Caller<RepositoryService> repositoryService;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private PlaceManager placeManager;

    private Map<String, OrganizationalUnit> availableOrganizationalUnits = new HashMap<String, OrganizationalUnit>();

    @Inject
    public CloneRepositoryPresenter( final RepositoryPreferences repositoryPreferences,
                                     final CloneRepositoryView view,
                                     final Caller<RepositoryService> repositoryService,
                                     final Caller<OrganizationalUnitService> organizationalUnitService,
                                     final PlaceManager placeManager ) {
        this.repositoryPreferences = repositoryPreferences;
        this.view = view;
        this.repositoryService = repositoryService;
        this.organizationalUnitService = organizationalUnitService;
        this.placeManager = placeManager;
    }

    @PostConstruct
    public void init() {
        view.init( this,
                   isOuMandatory() );
    }

    @AfterInitialization
    public void load() {
        populateOrganizationalUnits();
    }

    @Override
    public void handleCancelClick() {
        view.hide();
    }

    @Override
    public void handleCloneClick() {
        if ( view.isGitUrlEmpty() ) {
            view.setUrlGroupType( ControlGroupType.ERROR );
            view.showUrlHelpMandatoryMessage();
            return;

        } else if ( !URIUtil.isValid( view.getGitUrl() ) ) {
            view.setUrlGroupType( ControlGroupType.ERROR );
            view.showUrlHelpInvalidFormatMessage();
            return;

        } else {
            view.setUrlGroupType( ControlGroupType.NONE );
        }

        final String organizationalUnit = view.getOrganizationalUnit( view.getSelectedOrganizationalUnit() );

        if ( isOuMandatory() && !availableOrganizationalUnits.containsKey( organizationalUnit ) ) {
            view.setOrganizationalUnitGroupType( ControlGroupType.ERROR );
            view.showOrganizationalUnitHelpMandatoryMessage();
            return;

        } else {
            view.setOrganizationalUnitGroupType( ControlGroupType.NONE );
        }

        if ( view.isNameEmpty() ) {
            view.setNameGroupType( ControlGroupType.ERROR );
            view.showNameHelpMandatoryMessage();
            return;

        } else {
            repositoryService.call( new RemoteCallback<String>() {
                @Override
                public void callback( final String normalizedName ) {
                    if ( !view.getName().equals( normalizedName ) ) {
                        if ( !view.showAgreeNormalizeNameWindow( normalizedName ) ) {
                            return;
                        }
                        view.setName( normalizedName );
                    }

                    lockScreen();

                    final String scheme = "git";
                    final String alias = view.getName().trim();
                    final String origin = view.getGitUrl();
                    final String username = view.getUsername().trim();
                    final String password = view.getPassword().trim();
                    final Map<String, Object> env = new HashMap<String, Object>( 3 );
                    env.put( "username", username );
                    env.put( "crypt:password", password );
                    env.put( "origin", origin );

                    repositoryService.call( new RemoteCallback<Repository>() {
                                                @Override
                                                public void callback( final Repository o ) {
                                                    view.alertRepositoryCloned();
                                                    unlockScreen();
                                                    view.hide();
                                                    placeManager.goTo( new DefaultPlaceRequest( "RepositoryEditor" ).addParameter( "alias",
                                                                                                                                   o.getAlias() ) );
                                                }
                                            },
                                            new ErrorCallback<Message>() {
                                                @Override
                                                public boolean error( final Message message,
                                                                      final Throwable throwable ) {
                                                    try {
                                                        throw throwable;
                                                    } catch ( RepositoryAlreadyExistsException ex ) {
                                                        view.errorRepositoryAlreadyExist();
                                                    } catch ( Throwable ex ) {
                                                        view.errorCloneRepositoryFail( ex );
                                                    }
                                                    unlockScreen();
                                                    return true;
                                                }
                                            } ).createRepository( availableOrganizationalUnits.get( organizationalUnit ),
                                                                  scheme,
                                                                  alias,
                                                                  env );
                }
            } ).normalizeRepositoryName( view.getName() );
        }
    }

    public void showForm() {
        view.show();
    }

    private void populateOrganizationalUnits() {
        //populate Organizational Units list box
        organizationalUnitService.call( new RemoteCallback<Collection<OrganizationalUnit>>() {
                                            @Override
                                            public void callback( final Collection<OrganizationalUnit> organizationalUnits ) {
                                                view.addOrganizationalUnitSelectEntry();
                                                if ( organizationalUnits != null && !organizationalUnits.isEmpty() ) {
                                                    for ( OrganizationalUnit organizationalUnit : organizationalUnits ) {
                                                        view.addOrganizationalUnit( organizationalUnit.getName(),
                                                                                    organizationalUnit.getName() );
                                                        availableOrganizationalUnits.put( organizationalUnit.getName(),
                                                                                          organizationalUnit );
                                                    }
                                                }
                                            }
                                        },
                                        new ErrorCallback<Message>() {
                                            @Override
                                            public boolean error( final Message message,
                                                                  final Throwable throwable ) {
                                                view.errorLoadOrganizationalUnitsFail( throwable );
                                                return false;
                                            }
                                        } ).getOrganizationalUnits();
    }

    private void lockScreen() {
        view.showBusyPopupMessage();
        view.setPopupCloseVisible( false );
        view.setCloneEnabled( false );
        view.setCancelEnabled( false );
        view.setPasswordEnabled( false );
        view.setUsernameEnabled( false );
        view.setGitUrlEnabled( false );
        view.setOrganizationalUnitEnabled( false );
        view.setNameEnabled( false );
    }

    private void unlockScreen() {
        view.closeBusyPopup();
        view.setPopupCloseVisible( true );
        view.setCloneEnabled( true );
        view.setCancelEnabled( true );
        view.setPasswordEnabled( true );
        view.setUsernameEnabled( true );
        view.setGitUrlEnabled( true );
        view.setOrganizationalUnitEnabled( true );
        view.setNameEnabled( true );
    }

    private boolean isOuMandatory() {
        return repositoryPreferences == null || repositoryPreferences.isOUMandatory();
    }
}
