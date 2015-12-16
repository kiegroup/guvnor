/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.asset.management.client.editors.repository.structure.release;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
public class ReleaseScreenPopupViewImpl extends BaseModal {

    interface ReleaseScreenPopupWidgetBinder
            extends
            UiBinder<Widget, ReleaseScreenPopupViewImpl> {

    }

    private ReleaseScreenPopupWidgetBinder uiBinder = GWT.create( ReleaseScreenPopupWidgetBinder.class );

    @Inject
    private User identity;

    @UiField
    FormGroup repositoryTextGroup;

    @UiField
    TextBox repositoryText;

    @UiField
    HelpBlock repositoryTextHelpBlock;

    @UiField
    FormGroup sourceBranchTextGroup;

    @UiField
    TextBox sourceBranchText;

    @UiField
    HelpBlock sourceBranchTextHelpBlock;

    @UiField
    FormGroup userNameTextGroup;

    @UiField
    TextBox userNameText;

    @UiField
    HelpBlock userNameTextHelpBlock;

    @UiField
    FormGroup passwordTextGroup;

    @UiField
    Input passwordText;

    @UiField
    HelpBlock passwordTextHelpBlock;

    @UiField
    FormGroup serverURLTextGroup;

    @UiField
    TextBox serverURLText;

    @UiField
    HelpBlock serverURLTextHelpBlock;

    @UiField
    HelpBlock deployToRuntimeHelpBlock;

    @UiField
    FormGroup deployToRuntimeTextGroup;

    @UiField
    CheckBox deployToRuntimeCheck;

    @UiField
    HelpBlock versionTextHelpBlock;

    @UiField
    FormGroup versionTextGroup;

    @UiField
    TextBox versionText;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            if ( isEmpty( versionText.getText() ) ) {
                versionTextGroup.setValidationState( ValidationState.ERROR );
                versionTextHelpBlock.setText( Constants.INSTANCE.FieldMandatory0( "Version" ) );

                return;
            }
            if ( isSnapshot( versionText.getText() ) ) {
                versionTextGroup.setValidationState( ValidationState.ERROR );
                versionTextHelpBlock.setText( Constants.INSTANCE.SnapshotNotAvailableForRelease( "-SNAPSHOT" ) );

                return;
            }
            if ( deployToRuntimeCheck.getValue() ) {

                if ( isEmpty( userNameText.getText() ) ) {
                    userNameTextGroup.setValidationState( ValidationState.ERROR );
                    userNameTextHelpBlock.setText( Constants.INSTANCE.FieldMandatory0( "Username" ) );

                    return;
                }

                if ( isEmpty( passwordText.getText() ) ) {
                    passwordTextGroup.setValidationState( ValidationState.ERROR );
                    passwordTextHelpBlock.setText( Constants.INSTANCE.FieldMandatory0( "Password" ) );

                    return;
                }

                if ( isEmpty( serverURLText.getText() ) ) {
                    serverURLTextGroup.setValidationState( ValidationState.ERROR );
                    serverURLTextHelpBlock.setText( Constants.INSTANCE.FieldMandatory0( "ServerURL" ) );

                    return;
                }

            }

            if ( callbackCommand != null ) {
                callbackCommand.execute();
            }
            hide();
        }

        private boolean isEmpty( String value ) {
            if ( value == null || value.isEmpty() || value.trim().isEmpty() ) {
                return true;
            }

            return false;
        }

        private boolean isSnapshot( String value ) {
            if ( value != null && trim( value ).endsWith( "-SNAPSHOT" ) ) {
                return true;
            }
            return false;
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand, cancelCommand );

    public ReleaseScreenPopupViewImpl() {
        setTitle( Constants.INSTANCE.Release_Configuration() );
        setDataBackdrop( ModalBackdrop.STATIC );
        setDataKeyboard( true );
        setFade( true );
        setRemoveOnHide( true );

        setBody( uiBinder.createAndBindUi( this ) );
        add( footer );
    }

    public void configure( String repositoryAlias,
                           String branch,
                           String suggestedVersion,
                           String repositoryVersion,
                           Command command ) {
        this.callbackCommand = command;

        this.sourceBranchText.setText( branch );
        this.repositoryText.setText( repositoryAlias );
        this.sourceBranchText.setReadOnly( true );
        this.repositoryText.setReadOnly( true );
        // set default values for the fields
        userNameText.setText( identity.getIdentifier() );
        serverURLText.setText( GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" ) );
        this.versionTextHelpBlock.setText( "The current repository version is: " + repositoryVersion );
        this.versionText.setText( suggestedVersion );
        userNameText.setEnabled( false );
        passwordText.setEnabled( false );
        serverURLText.setEnabled( false );
        deployToRuntimeCheck.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                if ( event.getValue() ) {
                    userNameText.setEnabled( true );
                    passwordText.setEnabled( true );
                    serverURLText.setEnabled( true );
                } else {
                    userNameText.setEnabled( false );
                    passwordText.setEnabled( false );
                    serverURLText.setEnabled( false );
                }
            }
        } );
    }

    public String getUsername() {
        return trim( this.userNameText.getText() );
    }

    public String getPassword() {
        return trim( this.passwordText.getText() );
    }

    public String getServerURL() {
        return trim( this.serverURLText.getText() );
    }

    public String getVersion() {
        return trim( this.versionText.getText() );
    }

    public Boolean getDeployToRuntime() {
        return this.deployToRuntimeCheck.getValue();
    }

    private String trim( String value ) {
        return value != null ? value.trim() : value;
    }

}
