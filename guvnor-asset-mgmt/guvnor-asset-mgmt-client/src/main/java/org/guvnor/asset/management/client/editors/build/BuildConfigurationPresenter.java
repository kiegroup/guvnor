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
package org.guvnor.asset.management.client.editors.build;

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ListBox;
import org.guvnor.asset.management.client.editors.common.BaseAssetsMgmtPresenter;
import org.guvnor.asset.management.client.editors.common.BaseAssetsMgmtView;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen( identifier = "Build Management" )
public class BuildConfigurationPresenter extends BaseAssetsMgmtPresenter {

    public interface BuildConfigurationView extends UberView<BuildConfigurationPresenter>, BaseAssetsMgmtView {

        ListBox getChooseBranchBox();

        ListBox getChooseProjectBox();

        void showHideDeployToRuntimeSection( boolean show );

    }

    @Inject
    BuildConfigurationView view;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Build_Configuration();
    }

    @WorkbenchPartView
    public UberView<BuildConfigurationPresenter> getView() {
        return view;
    }

    public BuildConfigurationPresenter() {
    }

    @PostConstruct
    public void init() {
        baseView = view;
    }

    public void buildProject( String repository,
            String branch,
            String project,
            String userName,
            String password,
            String serverURL,
            Boolean deployToMaven ) {

        if ( serverURL != null && !serverURL.isEmpty() && serverURL.endsWith( "/" ) ) {
            serverURL = serverURL.substring( 0, serverURL.length() - 1 );
        }

        assetManagementServices.call( new RemoteCallback<Long>() {
                                          @Override
                                          public void callback( Long taskId ) {
                                              view.displayNotification( "Building Process Started" );
                                          }
                                      }, new ErrorCallback<Message>() {
                                          @Override
                                          public boolean error( Message message,
                                                  Throwable throwable ) {
                                              ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      }
        ).buildProject( repository, branch, project, userName, password, serverURL, deployToMaven );

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
            view.getChooseProjectBox().clear();
            view.getChooseProjectBox().addItem( constants.Select_Project() );
            for ( String branch : repository.getBranches() ) {
                view.getChooseBranchBox().addItem( branch, branch );
            }
        }
    }

    public void loadProjects( String alias,
            String branch ) {
        Repository repository = getRepository( alias );
        view.getChooseProjectBox().clear();
        view.getChooseProjectBox().addItem( constants.Select_Project() );

        assetManagementServices.call( new RemoteCallback<Set<Project>>() {

            @Override
            public void callback( final Set<Project> projectSetResults ) {

                for ( Project project : projectSetResults ) {
                    view.getChooseProjectBox().addItem( project.getProjectName(), project.getProjectName() );
                }

            }
        } ).getProjects( repository, branch );
    }

    @OnOpen
    public void onOpen() {
        view.getChooseRepositoryBox().setFocus( true );
    }
}