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
package org.guvnor.asset.management.client.editors.conf;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import org.guvnor.asset.management.client.editors.common.BaseAssetsMgmtPresenter;
import org.guvnor.asset.management.client.editors.common.BaseAssetsMgmtView;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.common.services.project.model.POM;
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
@WorkbenchScreen( identifier = "Repository Configuration" )
public class RepositoryConfigurationPresenter extends BaseAssetsMgmtPresenter {

    public interface RepositoryConfigurationView extends UberView<RepositoryConfigurationPresenter>, BaseAssetsMgmtView {

        Button getConfigureButton();

        TextBox getReleaseBranchText();

        TextBox getDevBranchText();

        TextBox getCurrentVersionText();

        TextBox getVersionText();

    }

    @Inject
    RepositoryConfigurationView view;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Repository_Configuration();
    }

    @WorkbenchPartView
    public UberView<RepositoryConfigurationPresenter> getView() {
        return view;
    }

    public RepositoryConfigurationPresenter() {
    }

    @PostConstruct
    public void init() {
        baseView = view;
    }

    public void loadRepositoryStructure( String value ) {
        if ( !value.equals( constants.Select_Repository() ) ) {
            for ( Repository r : getRepositories() ) {
                if ( ( r.getAlias() ).equals( value ) ) {
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
    }

    public void configureRepository( String repository, String sourceBranch, String devBranch, String releaseBranch, String version ) {
        assetManagementServices.call( new RemoteCallback<Long>() {
                                          @Override
                                          public void callback( Long taskId ) {
                                              view.displayNotification( "Repository Configuration Started!" );
                                          }
                                      }, new ErrorCallback<Message>() {
                                          @Override
                                          public boolean error( Message message, Throwable throwable ) {
                                              ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      }
        ).configureRepository( repository, sourceBranch, devBranch, releaseBranch, version );

    }

    @OnOpen
    public void onOpen() {
        view.getChooseRepositoryBox().setFocus( true );

    }

}
