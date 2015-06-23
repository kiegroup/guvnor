/*
 * Copyright 2015 JBoss Inc
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

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class ReleaseScreenPopupViewImpl extends BaseModal {

    interface ReleaseScreenPopupWidgetBinder
            extends
            UiBinder<Widget, ReleaseScreenPopupViewImpl> {

    }

    private ReleaseScreenPopupWidgetBinder uiBinder = GWT.create( ReleaseScreenPopupWidgetBinder.class );

    @Inject
    private User identity;

    @UiField
    ControlGroup repositoryTextGroup;

    @UiField
    TextBox repositoryText;

    @UiField
    HelpInline repositoryTextHelpInline;

    @UiField
    ControlGroup sourceBranchTextGroup;

    @UiField
    TextBox sourceBranchText;

    @UiField
    HelpInline sourceBranchTextHelpInline;

    @UiField
    ControlGroup userNameTextGroup;

    @UiField
    TextBox userNameText;

    @UiField
    HelpInline userNameTextHelpInline;

    @UiField
    ControlGroup passwordTextGroup;

    @UiField
    PasswordTextBox passwordText;

    @UiField
    HelpInline passwordTextHelpInline;

    @UiField
    ControlGroup serverURLTextGroup;

    @UiField
    TextBox serverURLText;

    @UiField
    HelpInline serverURLTextHelpInline;

    @UiField
    HelpInline deployToRuntimeHelpInline;

    @UiField
    ControlGroup deployToRuntimeTextGroup;

    @UiField
    CheckBox deployToRuntimeCheck;

    @UiField
    HelpInline versionTextHelpInline;

    @UiField
    ControlGroup versionTextGroup;

    @UiField
    TextBox versionText;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            if ( isEmpty( versionText.getText() ) ) {
                versionTextGroup.setType( ControlGroupType.ERROR );
                versionTextHelpInline.setText( Constants.INSTANCE.FieldMandatory0( "Version" ) );

                return;
            }
            if ( isSnapshot( versionText.getText() ) ) {
                versionTextGroup.setType( ControlGroupType.ERROR );
                versionTextHelpInline.setText( Constants.INSTANCE.SnapshotNotAvailableForRelease( "-SNAPSHOT" ) );

                return;
            }
            if ( deployToRuntimeCheck.getValue() ) {

                if ( isEmpty( userNameText.getText() ) ) {
                    userNameTextGroup.setType( ControlGroupType.ERROR );
                    userNameTextHelpInline.setText( Constants.INSTANCE.FieldMandatory0( "Username" ) );

                    return;
                }

                if ( isEmpty( passwordText.getText() ) ) {
                    passwordTextGroup.setType( ControlGroupType.ERROR );
                    passwordTextHelpInline.setText( Constants.INSTANCE.FieldMandatory0( "Password" ) );

                    return;
                }

                if ( isEmpty( serverURLText.getText() ) ) {
                    serverURLTextGroup.setType( ControlGroupType.ERROR );
                    serverURLTextHelpInline.setText( Constants.INSTANCE.FieldMandatory0( "ServerURL" ) );

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
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );

        add( uiBinder.createAndBindUi( this ) );
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
        this.versionTextHelpInline.setText( "The current repository version is: " + repositoryVersion );
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
