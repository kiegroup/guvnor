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
package org.guvnor.asset.management.client.editors.promote;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ListBox;
import org.guvnor.asset.management.client.editors.common.BaseAssetsMgmtPresenter;
import org.guvnor.asset.management.client.editors.common.BaseAssetsMgmtView;
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
@WorkbenchScreen( identifier = "Promote Changes" )
public class PromoteChangesPresenter extends BaseAssetsMgmtPresenter {

    public interface PromoteChangesView extends UberView<PromoteChangesPresenter>, BaseAssetsMgmtView {

        ListBox getChooseSourceBranchBox();

        ListBox getChooseTargetBranchBox();

    }

    @Inject
    PromoteChangesView view;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Promote_Assets();
    }

    @WorkbenchPartView
    public UberView<PromoteChangesPresenter> getView() {
        return view;
    }

    public PromoteChangesPresenter() {
    }

    @PostConstruct
    public void init() {
        baseView = view;
    }

    public void loadBranches( String alias ) {
        view.getChooseSourceBranchBox().clear();
        view.getChooseTargetBranchBox().clear();

        view.getChooseSourceBranchBox().addItem( constants.Select_A_Branch() );
        view.getChooseTargetBranchBox().addItem( constants.Select_A_Branch() );

        Repository repository = getRepository( alias );
        if ( repository != null ) {
            for ( String branch : repository.getBranches() ) {
                view.getChooseSourceBranchBox().addItem( branch, branch );
                view.getChooseTargetBranchBox().addItem( branch, branch );
            }
        }
    }

    public void promoteChanges( String repository, String sourceBranch, String destinationBranch ) {
        assetManagementServices.call( new RemoteCallback<Long>() {
                                          @Override
                                          public void callback( Long taskId ) {
                                              view.displayNotification( "Promote Changes Process Started!" );
                                          }
                                      }, new ErrorCallback<Message>() {
                                          @Override
                                          public boolean error( Message message, Throwable throwable ) {
                                              ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      }
        ).promoteChanges( repository, sourceBranch, destinationBranch );

    }

    @OnOpen
    public void onOpen() {
        view.getChooseRepositoryBox().setFocus( true );

    }

}
