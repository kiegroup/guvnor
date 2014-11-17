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
package org.guvnor.asset.management.client.editors.release;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import org.guvnor.asset.management.client.editors.common.BaseAssetsMgmtPresenter;
import org.guvnor.asset.management.client.editors.common.BaseAssetsMgmtView;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen( identifier = "Release Management" )
public class ReleaseConfigurationPresenter extends BaseAssetsMgmtPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface ReleaseConfigurationView extends UberView<ReleaseConfigurationPresenter>, BaseAssetsMgmtView {

        ListBox getChooseBranchBox();

        TextBox getCurrentVersionText();

        TextBox getVersionText();

        void showHideDeployToRuntimeSection( boolean show );
    }

    @Inject
    ReleaseConfigurationView view;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private boolean supportRuntimeDeployment;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Release_Configuration();
    }

    @WorkbenchPartView
    public UberView<ReleaseConfigurationPresenter> getView() {
        return view;
    }

    public ReleaseConfigurationPresenter() {
    }

    @PostConstruct
    public void init() {
        baseView = view;
    }

    public void releaseProject( String repository, String branch,
            String userName, String password, String serverURL, Boolean deployToRuntime, String version ) {

        if ( serverURL != null && !serverURL.isEmpty() && serverURL.endsWith( "/" ) ) {
            serverURL = serverURL.substring( 0, serverURL.length() - 1 );
        }

        assetManagementServices.call( new RemoteCallback<Long>() {
                                          @Override
                                          public void callback( Long taskId ) {
                                              view.displayNotification( "Release project process started" );
                                          }
                                      }, new ErrorCallback<Message>() {
                                          @Override
                                          public boolean error( Message message, Throwable throwable ) {
                                              ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      }
        ).releaseProject( repository, branch, userName, password, serverURL, deployToRuntime, version );

    }

    public void loadServerSetting() {
        assetManagementServices.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean supportRuntimeDeployment ) {
                view.showHideDeployToRuntimeSection( supportRuntimeDeployment );
            }
        } ).supportRuntimeDeployment();
    }

    public void loadBranches( String alias ) {
        Repository repository = getRepository( alias );
        if ( repository != null ) {
            view.getChooseBranchBox().clear();
            view.getChooseBranchBox().addItem( constants.Select_A_Branch() );
            for ( String branch : repository.getBranches() ) {
                view.getChooseBranchBox().addItem( branch, branch );
            }
        }
    }

    public void loadRepositoryStructure( String value ) {
        if ( !value.equals( constants.Select_Repository() ) ) {
            Repository r = getRepository( value );
            if ( r != null ) {
                repositoryStructureServices.call(new RemoteCallback<RepositoryStructureModel>() {
                    @Override
                    public void callback( RepositoryStructureModel model ) {

                        POM pom = null;
                        if ( model != null && ( model.isSingleProject() || model.isMultiModule() ) ) {
                            pom = model.isMultiModule() ? model.getPOM() : model.getSingleProjectPOM();
                        }

                        if ( pom != null ) {
                            // don't include snapshot for branch names
                            view.getCurrentVersionText().setText( pom.getGav().getVersion().replace( "-SNAPSHOT", "" ) );
                            view.getVersionText().setText( pom.getGav().getVersion().replace( "-SNAPSHOT", "" ) );

                        } else {
                            view.getCurrentVersionText().setText( constants.No_Project_Structure_Available() );
                            view.getVersionText().setText( "1.0.0" );
                        }
                    }
                } ).load( r );
                return;
            }
        }
    }

    protected void setSupportRuntimeDeployment( boolean b ) {
        this.supportRuntimeDeployment = b;
    }

    public boolean getSupportRuntimeDeployment() {
        return this.supportRuntimeDeployment;
    }

    @OnOpen
    public void onOpen() {
        view.getChooseRepositoryBox().setFocus( true );

    }

}
