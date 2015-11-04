/*
 * Copyright 2015 JBoss Inc
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
package org.guvnor.asset.management.client.editors.repository.structure.release;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.guvnor.asset.management.client.i18n.Constants;
import org.jboss.errai.security.shared.api.identity.User;

@Dependent
public class ReleaseScreenPopupPresenter implements ReleaseScreenPopupView.Presenter{

    private ReleaseScreenPopupView view;

    private Command callbackCommand;

    @Inject
    private User identity;
    
    @Inject
    public ReleaseScreenPopupPresenter(ReleaseScreenPopupView view) {    
        this.view = view;
    }

    public void show() {
        view.show();
    }

    public void hide() {
        view.hide();
    }

    public ReleaseScreenPopupView getView() {
        return view;
    }

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            if (isEmpty(view.getVersion())) {
                view.setVersionStatus(ControlGroupType.ERROR);
                view.setVersionHelpText(Constants.INSTANCE.FieldMandatory0("Version"));

                return;
            }
            if (isSnapshot(view.getVersion())) {
                view.setVersionStatus(ControlGroupType.ERROR);
                view.setVersionHelpText(Constants.INSTANCE.SnapshotNotAvailableForRelease("-SNAPSHOT"));

                return;
            }
            if (!view.getSourceBranch().startsWith("release")) {
                view.setSourceBranchStatus(ControlGroupType.ERROR);
                view.setSourceBranchHelpText(Constants.INSTANCE.ReleaseCanOnlyBeDoneFromAReleaseBranch());
                return;
            }
            if (view.isDeployToRuntime()) {

                if (isEmpty(view.getUserName())) {
                    view.setUserNameStatus(ControlGroupType.ERROR);
                    view.setUserNameTextHelp(Constants.INSTANCE.FieldMandatory0("Username"));

                    return;
                }

                if (isEmpty(view.getPassword())) {
                    view.setPasswordStatus(ControlGroupType.ERROR);
                    view.setPasswordHelpText(Constants.INSTANCE.FieldMandatory0("Password"));

                    return;
                }

                if (isEmpty(view.getServerURL())) {
                    view.setServerURLStatus(ControlGroupType.ERROR);
                    view.setServerURLHelpText(Constants.INSTANCE.FieldMandatory0("ServerURL"));

                    return;
                }

            }

            if (callbackCommand != null) {
                callbackCommand.execute();
            }
            view.hide();
        }

        private boolean isEmpty(String value) {
            return value == null || value.isEmpty() || value.trim().isEmpty();
        }

        private boolean isSnapshot(String value) {
            return value != null && value.trim().endsWith("-SNAPSHOT");
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            view.hide();
        }
    };

    public Command getOkCommand() {
        return okCommand;
    }

    public Command getCancelCommand() {
        return cancelCommand;
    }

    public void setIdentity(User identity) {
        this.identity = identity;
    }
    
    
    
    public void configure( String repositoryAlias,
                           String branch,
                           String suggestedVersion,
                           String repositoryVersion,
                           Command command ) {
        
        this.callbackCommand = command;

        view.setSourceBranch( branch );
        view.setRepository( repositoryAlias );
        view.setSourceBranchReadOnly( true );
        view.setRepositoryReadOnly( true );
        // set default values for the fields
        view.setUserName( identity.getIdentifier() );
        view.setServerURL( GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" ) );
        view.setVersionHelpText( "The current repository version is: " + repositoryVersion );
        view.setVersion( suggestedVersion );
        view.setUserNameEnabled( false );
        view.setPasswordEnabled( false );
        view.setServerURLEnabled( false );
        view.setDeployToRuntimeValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                if ( event.getValue() ) {
                    view.setUserNameEnabled( true );
                    view.setPasswordEnabled( true );
                    view.setServerURLEnabled( true );
                } else {
                    view.setUserNameEnabled( false );
                    view.setPasswordEnabled( false );
                    view.setServerURLEnabled( false );
                }
            }
        } );
    }

}
