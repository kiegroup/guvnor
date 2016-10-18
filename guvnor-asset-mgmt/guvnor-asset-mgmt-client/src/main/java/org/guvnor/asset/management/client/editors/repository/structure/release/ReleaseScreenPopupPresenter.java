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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.guvnor.asset.management.client.editors.repository.structure.ReleaseCommand;
import org.guvnor.asset.management.client.editors.repository.structure.ReleaseInfo;
import org.jboss.errai.security.shared.api.identity.User;

@Dependent
public class ReleaseScreenPopupPresenter implements ReleaseScreenPopupView.Presenter {

    private final ReleaseScreenPopupView view;
    private final User identity;

    private ReleaseCommand command;
    private String repositoryVersion;

    @Inject
    public ReleaseScreenPopupPresenter( ReleaseScreenPopupView view, User identity ) {
        this.view = view;
        this.identity = identity;
    }

    @PostConstruct
    public void setup() {
        this.view.init( this );
    }

    public void show() {
        view.show();
    }

    public void hide() {
        view.hide();
    }

    @Override
    public void onSubmit() {
        boolean isValid = true;
        view.clearWidgetsState();
        view.showCurrentVersionHelpText( repositoryVersion );

        if ( isEmpty( view.getVersion() ) ) {
            view.showErrorVersionEmpty();
            isValid = false;
        }
        if ( isSnapshot( view.getVersion() ) ) {
            view.showErrorVersionSnapshot();
            isValid = false;
        }
        if ( !view.getSourceBranch().startsWith( "release" ) ) {
            view.showErrorSourceBranchNotRelease();
            isValid = false;
        }
        if ( view.isDeployToRuntime() ) {
            if ( isEmpty( view.getUserName() ) ) {
                view.showErrorUserNameEmpty();
                isValid = false;
            }
            if ( isEmpty( view.getPassword() ) ) {
                view.showErrorPasswordEmpty();
                isValid = false;
            }
            if ( isEmpty( view.getServerURL() ) ) {
                view.showErrorServerUrlEmpty();
                isValid = false;
            }
        }

        if ( isValid ) {
            if ( command != null ) {
                command.execute( new ReleaseInfo( view.getVersion(),
                                                  view.isDeployToRuntime(),
                                                  view.getUserName(),
                                                  view.getPassword(),
                                                  view.getServerURL() )
                );
            }
            view.hide();
        }
    }

    private boolean isEmpty( String value ) {
        return value == null || value.isEmpty() || value.trim().isEmpty();
    }

    private boolean isSnapshot( String value ) {
        return value != null && value.trim().endsWith( "-SNAPSHOT" );
    }

    @Override
    public void onCancel() {
        view.hide();
    }

    @Override
    public void onDeployToRuntimeStateChanged( boolean checked ) {
        view.setUserNameEnabled( checked );
        view.setPasswordEnabled( checked );
        view.setServerURLEnabled( checked );
    }

    public void configure( String repositoryAlias,
                           String branch,
                           String suggestedVersion,
                           String repositoryVersion,
                           ReleaseCommand command ) {

        this.command = command;
        this.repositoryVersion = repositoryVersion;

        view.clearWidgetsState();
        view.setSourceBranch( branch );
        view.setRepository( repositoryAlias );
        view.setSourceBranchReadOnly( true );
        view.setRepositoryReadOnly( true );
        // set default values for the fields
        view.setUserName( identity.getIdentifier() );
        view.setServerURL( GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" ) );
        view.showCurrentVersionHelpText( repositoryVersion );
        view.setVersion( suggestedVersion );
        view.setDeployToRuntime( false );
        onDeployToRuntimeStateChanged( false );
    }

}
