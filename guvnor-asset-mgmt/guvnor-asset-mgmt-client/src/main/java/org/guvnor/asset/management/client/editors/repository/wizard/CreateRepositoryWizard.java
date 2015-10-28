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
import java.util.Set;
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
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryAlreadyExistsException;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.asset.management.security.AssetsMgmtFeatures.*;

@Dependent
public class CreateRepositoryWizard extends AbstractWizard {

    private final List<WizardPage> pages = new ArrayList<WizardPage>();

    private RepositoryInfoPage infoPage;

    private RepositoryStructurePage structurePage;

    private CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();

    private Caller<RepositoryService> repositoryService;

    private Caller<RepositoryStructureService> repositoryStructureService;

    private Caller<AssetManagementService> assetManagementService;

    private Event<NotificationEvent> notification;

    private KieWorkbenchACL kieACL;

    private SessionInfo sessionInfo;

    private Callback<Void> onCloseCallback = null;

    private boolean assetsManagementIsGranted = false;

    @Inject
    public CreateRepositoryWizard( RepositoryInfoPage infoPage,
            RepositoryStructurePage structurePage,
            CreateRepositoryWizardModel model,
            Caller<RepositoryService> repositoryService,
            Caller<RepositoryStructureService> repositoryStructureService,
            Caller<AssetManagementService> assetManagementService,
            Event<NotificationEvent> notification,
            KieWorkbenchACL kieACL,
            SessionInfo sessionInfo ) {
        this.infoPage = infoPage;
        this.structurePage = structurePage;
        this.model = model;
        this.repositoryService = repositoryService;
        this.repositoryStructureService = repositoryStructureService;
        this.assetManagementService = assetManagementService;
        this.notification = notification;
        this.kieACL = kieACL;
        this.sessionInfo = sessionInfo;
    }

    @PostConstruct
    public void setupPages() {
        pages.add( infoPage );

        infoPage.initialise();
        structurePage.initialise();

        infoPage.setModel( model );
        structurePage.setModel( model );

        infoPage.setHandler( new RepositoryInfoPage.RepositoryInfoPageHandler() {
            @Override
            public void managedRepositoryStatusChanged( boolean status ) {
                managedRepositorySelected( status );
            }
        } );
        setAssetsManagementGrant();
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

        final int[] unCompletedPages = {this.pages.size()};
        final boolean[] completed = {false};

        //only when all pages are complete we can say the wizard is complete.
        for ( WizardPage page : this.pages ) {
            page.isComplete( new Callback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {

                    if ( Boolean.TRUE.equals( result ) ) {
                        unCompletedPages[0]--;
                        if ( unCompletedPages[0] == 0 ) {
                            completed[0] = true;
                        }
                    }
                }
            } );
        }

        callback.callback( completed[0] );
    }

    @Override
    public void complete() {
        doComplete();
    }

    @Override
    public void close() {
        super.close();
        invokeOnCloseCallback();
    }

    public void onCloseCallback( final Callback<Void> callback ) {
        this.onCloseCallback = callback;
    }

    private void managedRepositorySelected( boolean selected ) {
        if ( assetsManagementIsGranted ) {
            boolean updateDefaultValues = false;
            if ( selected && !pages.contains( structurePage ) ) {
                pages.add( structurePage );
                updateDefaultValues = true;
            } else {
                pages.remove( structurePage );
            }
            super.start();
            if ( updateDefaultValues ) {
                setStructureDefaultValues();
            }
        }
    }

    public void pageSelected( final int pageNumber ) {
        super.pageSelected( pageNumber );
        if ( pageNumber == 1 ) {
            infoPage.setStructurePageWasVisited( true );
            structurePage.setStructurePageWasVisited( true );
            setStructureDefaultValues();
        }
    }

    private void doComplete() {

        repositoryService.call( new RemoteCallback<String>() {
            @Override
            public void callback( String normalizedName ) {
                if ( !model.getRepositoryName().equals( normalizedName ) ) {
                    if ( !Window.confirm( CoreConstants.INSTANCE.RepositoryNameInvalid() + " \"" + normalizedName + "\". " + CoreConstants.INSTANCE.DoYouAgree() ) ) {
                        return;
                    }
                    String unNormalizedName = model.getRepositoryName();
                    model.setRepositoryName( normalizedName );
                    if ( unNormalizedName.equals( model.getProjectName() ) ) {
                        model.setProjectName( normalizedName );
                    }
                    if ( unNormalizedName.equals( model.getArtifactId() ) ) {
                        model.setArtifactId( normalizedName );
                    }
                }
                parentComplete();

                final String scheme = "git";
                final String alias = model.getRepositoryName().trim();
                final Map<String, Object> env = new HashMap<String, Object>( 3 );
                env.put( EnvironmentParameters.MANAGED,
                         assetsManagementIsGranted && model.isManged() );
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
                                                invokeOnCloseCallback();
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
                    pom.setDescription( model.getProjectDescription() );
                    pom.getGav().setGroupId( model.getGroupId() );
                    pom.getGav().setArtifactId( model.getArtifactId() );
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
                                    invokeOnCloseCallback();
                                    return true;
                                }
                            }
                                                   ).initRepositoryStructure( pom, baseUrl, repository, model.isMultiModule() );
                } else {
                    invokeOnCloseCallback();
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
                                                         invokeOnCloseCallback();
                                                     }
                                                 },
                                                 new ErrorCallback<Message>() {
                                                     @Override
                                                     public boolean error( Message message,
                                                                           Throwable throwable ) {
                                                         showErrorPopup( Constants.INSTANCE.RepoConfigurationStartFailed() + " \n" + throwable.getMessage() );
                                                         invokeOnCloseCallback();
                                                         return true;
                                                     }
                                                 }
                                               ).configureRepository( pair.getK1().getAlias(), "master", "dev", "release", normalizeVersionNumber( model.getVersion() ) );
                } else {
                    invokeOnCloseCallback();
                }
            }
        };
    }

    private String normalizeVersionNumber( String version ) {
        version = version != null ? version.trim() : null;
        if ( version != null && version.contains( "-SNAPSHOT" ) ) {
            return version.replace( "-SNAPSHOT", "" );
        } else {
            return version;
        }
    }

    private void invokeOnCloseCallback() {
        if ( onCloseCallback != null ) {
            onCloseCallback.callback( null );
        }
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

    private void setStructureDefaultValues() {

        if ( model.getRepositoryName() != null ) {
            structurePage.setProjectName( model.getRepositoryName() );
            structurePage.setArtifactId( model.getRepositoryName() );
        }

        if ( model.getOrganizationalUnit() != null ) {
            structurePage.setGroupId( model.getOrganizationalUnit().getDefaultGroupId() );
        }

        structurePage.setProjectDescription( null );
        structurePage.setVersion( "1.0.0-SNAPSHOT" );
        structurePage.fireEvent();
    }

    private void setAssetsManagementGrant() {

        Set<String> grantedRoles = kieACL.getGrantedRoles( CONFIGURE_REPOSITORY );
        assetsManagementIsGranted = false;

        if ( sessionInfo != null && sessionInfo.getIdentity() != null && sessionInfo.getIdentity().getRoles() != null ) {
            for ( Role role : sessionInfo.getIdentity().getRoles() ) {
                if ( grantedRoles.contains( role.getName() ) ) {
                    assetsManagementIsGranted = true;
                    break;
                }
            }
        }
        infoPage.enableManagedRepoCreation( assetsManagementIsGranted );
    }

}
