/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.asset.management.client.editors.repository.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryInfoPage;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryStructurePage;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryWizardPage;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryAlreadyExistsException;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.kie.uberfire.client.resources.i18n.CoreConstants;
import org.kie.uberfire.client.wizards.AbstractWizard;
import org.kie.uberfire.client.wizards.WizardPage;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class CreateRepositoryWizard extends AbstractWizard {

    private final List<WizardPage> pages = new ArrayList<WizardPage>();

    @Inject
    private RepositoryInfoPage infoPage;

    @Inject
    private RepositoryStructurePage structurePage;

    private CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private Caller<RepositoryStructureService> repositoryStructureService;

    @Inject
    private Caller<AssetManagementService> assetManagementService;

    @Inject
    private Event<NotificationEvent> notification;

    public static final String MANAGED = "managed";

    @PostConstruct
    public void setupPages() {
        pages.add( infoPage );

        infoPage.initialise();
        structurePage.initialise();

        infoPage.setModel( model );
        structurePage.setModel( model );

        infoPage.setHandler( new RepositoryInfoPage.RepositoryInfoPageHandler() {
            @Override public void managedRepositoryStatusChanged( boolean status ) {
                managedRepositorySelected( status );
            }
        } );
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget( int pageNumber ) {

        final RepositoryWizardPage page = (RepositoryWizardPage) this.pages.get( pageNumber );
        final Widget w = page.asWidget();
        return w;
    }

    @Override
    public String getTitle() {
        return Constants.INSTANCE.NewRepository();
    }

    @Override
    public int getPreferredHeight() {
        return 600;
    }

    @Override
    public int getPreferredWidth() {
        return 700;
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {

        callback.callback( true );

        //only when all pages are complete we can say the wizard is complete.
        for ( WizardPage page : this.pages ) {
            page.isComplete( new Callback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {
                    if ( Boolean.FALSE.equals( result ) ) {
                        callback.callback( false );
                    }
                }
            } );
        }
    }

    @Override
    public void complete() {
        doComplete();
    }

    @Override
    public void close() {
        super.close();
    }

    private void managedRepositorySelected( boolean selected ) {
        if ( selected && !pages.contains( structurePage )) {
            pages.add( structurePage );
        } else {
            pages.remove( structurePage );
        }
        super.start();
    }

    private void doComplete() {

        repositoryService.call( new RemoteCallback<String>() {
            @Override
            public void callback( String normalizedName ) {
                if ( !model.getRepositoryName().equals( normalizedName ) ) {
                    if ( !Window.confirm( CoreConstants.INSTANCE.RepositoryNameInvalid() + " \"" + normalizedName + "\". " + CoreConstants.INSTANCE.DoYouAgree() ) ) {
                        return;
                    }
                    model.setRepositoryName( normalizedName );
                }
                parentComplete();

                final String scheme = "git";
                final String alias = model.getRepositoryName().trim();
                final Map<String, Object> env = new HashMap<String, Object>( 3 );
                env.put( MANAGED, model.isManged() );

                showBusyIndicator( Constants.INSTANCE.CreatingRepository() );

                repositoryService.call( new RemoteCallback<Repository>() {
                                            @Override
                                            public void callback( Repository repository ) {
                                                hideBusyIndicator();
                                                notification.fire( new NotificationEvent( Constants.INSTANCE.RepoCreationSuccess() ) );
                                                getRepositoryCreatedSuccessCallback().callback( repository );
                                            }
                                        },
                        new ErrorCallback<Message>() {
                            @Override
                            public boolean error( final Message message,
                                    final Throwable throwable ) {
                                try {
                                    hideBusyIndicator();
                                    throw throwable;
                                } catch ( RepositoryAlreadyExistsException ex ) {
                                    showErrorPopup( CoreConstants.INSTANCE.RepoAlreadyExists() );
                                } catch ( Throwable ex ) {
                                    showErrorPopup( CoreConstants.INSTANCE.RepoCreationFail() + " \n" + throwable.getMessage() );
                                }
                                return true;
                            }
                        }
                ).createRepository( model.getOrganizationalUnit(), scheme, alias, env );
            }
        } ).normalizeRepositoryName( model.getRepositoryName() );
    }

    private void parentComplete() {
        super.complete();
    }

    private RemoteCallback<Repository> getRepositoryCreatedSuccessCallback() {
        return new RemoteCallback<Repository>() {
            @Override
            public void callback( final Repository repository ) {
                if ( model.isManged() ) {

                    showBusyIndicator( Constants.INSTANCE.InitializingRepository() );

                    POM pom = new POM();
                    pom.setName( model.getProjectName() );
                    pom.getGav().setGroupId( model.getGroupId() );
                    pom.getGav().setArtifactId(model.getArtifactId() );
                    pom.getGav().setVersion( model.getVersion() );
                    final String url = GWT.getModuleBaseURL();
                    final String baseUrl = url.replace( GWT.getModuleName() + "/", "" );

                    repositoryStructureService.call(
                            new RemoteCallback<Path>() {
                                @Override
                                public void callback( Path path ) {
                                    hideBusyIndicator();
                                    notification.fire( new NotificationEvent( Constants.INSTANCE.RepoInitializationSuccess() ) );
                                    getRepositoryInitializedSuccessCallback().callback( new Pair<Repository, Path>( repository, path ) );
                                }
                            },

                            new ErrorCallback<Message>() {
                                @Override
                                public boolean error( final Message message,
                                        final Throwable throwable ) {

                                    hideBusyIndicator();
                                    showErrorPopup( Constants.INSTANCE.RepoInitializationFail() + " \n" + throwable.getMessage() );
                                    return true;
                                }
                            }
                    ).initRepositoryStructure( pom, baseUrl, repository, model.isMultiModule() );
                }
            }
        };
    }

    private RemoteCallback<Pair<Repository, Path>> getRepositoryInitializedSuccessCallback() {

        return new RemoteCallback<Pair<Repository, Path>>() {
            @Override
            public void callback( Pair<Repository, Path> pair ) {
                if ( model.isConfigureRepository() ) {
                    assetManagementService.call( new RemoteCallback<Void>() {
                        @Override
                        public void callback( Void o ) {
                            notification.fire( new NotificationEvent( Constants.INSTANCE.RepoConfigurationStarted() ) );
                        }
                    },
                    new ErrorCallback<Message>() {
                        @Override
                        public boolean error( Message message, Throwable throwable ) {
                            showErrorPopup( Constants.INSTANCE.RepoConfigurationStartFailed() + " \n" + throwable.getMessage() );
                            return true;
                        }
                    }).configureRepository( pair.getK1().getAlias() , "master", "dev", "release", model.getVersion() );
                }
            }
        };
    }

    private void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    private void hideBusyIndicator() {
        BusyPopup.close();
    }

    private void showErrorPopup( final String message ) {
        ErrorPopup.showMessage( message );
    }
}
