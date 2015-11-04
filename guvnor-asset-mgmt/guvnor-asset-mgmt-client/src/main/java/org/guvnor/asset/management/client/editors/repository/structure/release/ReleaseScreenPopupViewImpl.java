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

    private final ModalFooterOKCancelButtons footer;
    
    private ReleaseScreenPopupPresenter presenter;

    public ReleaseScreenPopupViewImpl() {
        setTitle( Constants.INSTANCE.Release_Configuration() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        presenter = new ReleaseScreenPopupPresenter(this, callbackCommand);
        footer = new ModalFooterOKCancelButtons( presenter.getOkCommand(), presenter.getCancelCommand() );
        add( uiBinder.createAndBindUi( this ) );
        add( footer );
    }

    public void configure( String repositoryAlias,
                           String branch,
                           String suggestedVersion,
                           String repositoryVersion,
                           Command command ) {
        clearWidgetsState();
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

    public String trim( String value ) {
        return value != null ? value.trim() : value;
    }

    public ControlGroup getRepositoryTextGroup() {
        return repositoryTextGroup;
    }

    public HelpInline getRepositoryTextHelpInline() {
        return repositoryTextHelpInline;
    }

    public ControlGroup getSourceBranchTextGroup() {
        return sourceBranchTextGroup;
    }

    public HelpInline getSourceBranchTextHelpInline() {
        return sourceBranchTextHelpInline;
    }

    public ControlGroup getUserNameTextGroup() {
        return userNameTextGroup;
    }

    public HelpInline getUserNameTextHelpInline() {
        return userNameTextHelpInline;
    }

    public ControlGroup getPasswordTextGroup() {
        return passwordTextGroup;
    }

    public HelpInline getPasswordTextHelpInline() {
        return passwordTextHelpInline;
    }

    public ControlGroup getServerURLTextGroup() {
        return serverURLTextGroup;
    }

    public HelpInline getServerURLTextHelpInline() {
        return serverURLTextHelpInline;
    }

    public HelpInline getDeployToRuntimeHelpInline() {
        return deployToRuntimeHelpInline;
    }

    public ControlGroup getDeployToRuntimeTextGroup() {
        return deployToRuntimeTextGroup;
    }

    public HelpInline getVersionTextHelpInline() {
        return versionTextHelpInline;
    }

    public ControlGroup getVersionTextGroup() {
        return versionTextGroup;
    }

    public TextBox getRepositoryText() {
        return repositoryText;
    }

    public TextBox getSourceBranchText() {
        return sourceBranchText;
    }

    public TextBox getUserNameText() {
        return userNameText;
    }

    public PasswordTextBox getPasswordText() {
        return passwordText;
    }

    public TextBox getServerURLText() {
        return serverURLText;
    }

    public TextBox getVersionText() {
        return versionText;
    }

    public CheckBox getDeployToRuntimeCheck() {
        return deployToRuntimeCheck;
    }

    public Command getCallbackCommand() {
        return callbackCommand;
    }
    
    

    public void clearWidgetsState() {
        repositoryTextGroup.setType(ControlGroupType.NONE);
        repositoryTextHelpInline.setText("");
        sourceBranchTextGroup.setType(ControlGroupType.NONE);
        sourceBranchTextHelpInline.setText("");
        userNameTextGroup.setType(ControlGroupType.NONE);
        userNameTextHelpInline.setText("");
        passwordTextGroup.setType(ControlGroupType.NONE);
        passwordTextHelpInline.setText("");
        serverURLTextGroup.setType(ControlGroupType.NONE);
        serverURLTextHelpInline.setText("");
        deployToRuntimeTextGroup.setType(ControlGroupType.NONE);
        deployToRuntimeHelpInline.setText("");
        versionTextGroup.setType(ControlGroupType.NONE);
        versionTextHelpInline.setText("");
    }
    
    
    
}
