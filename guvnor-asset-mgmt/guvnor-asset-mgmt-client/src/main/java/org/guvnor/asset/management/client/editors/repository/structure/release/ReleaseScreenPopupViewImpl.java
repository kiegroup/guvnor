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

import javax.enterprise.context.Dependent;

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
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
public class ReleaseScreenPopupViewImpl extends BaseModal implements ReleaseScreenPopupView {

    interface ReleaseScreenPopupWidgetBinder
            extends
            UiBinder<Widget, ReleaseScreenPopupViewImpl> {

    }

    private ReleaseScreenPopupWidgetBinder uiBinder = GWT.create( ReleaseScreenPopupWidgetBinder.class );

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

    private final ModalFooterOKCancelButtons footer;

    private ReleaseScreenPopupView.Presenter presenter;

    public ReleaseScreenPopupViewImpl() {
        setTitle( Constants.INSTANCE.Release_Configuration() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        footer = new ModalFooterOKCancelButtons( getOkCommand(),
                                                 getCancelCommand() );
        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        deployToRuntimeCheck.addValueChangeHandler( new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                presenter.onDeployToRuntimeStateChanged( event.getValue() );
            }
        } );
    }

    //Defer delegation to Presenter until after it has been set
    private Command getOkCommand() {
        return new Command() {
            @Override
            public void execute() {
                if ( presenter != null ) {
                    presenter.onSubmit();
                }
            }
        };
    }

    //Defer delegation to Presenter until after it has been set
    private Command getCancelCommand() {
        return new Command() {
            @Override
            public void execute() {
                if ( presenter != null ) {
                    presenter.onCancel();
                }
            }
        };
    }

    @Override
    public String getUserName() {
        return trim( this.userNameText.getText() );
    }

    @Override
    public String getPassword() {
        return trim( this.passwordText.getText() );
    }

    @Override
    public String getServerURL() {
        return trim( this.serverURLText.getText() );
    }

    @Override
    public String getVersion() {
        return trim( this.versionText.getText() );
    }

    public Boolean getDeployToRuntime() {
        return this.deployToRuntimeCheck.getValue();
    }

    public String trim( String value ) {
        return value != null ? value.trim() : value;
    }

    @Override
    public void showErrorVersionEmpty() {
        versionTextGroup.setType( ControlGroupType.ERROR );
        versionTextHelpInline.setText( Constants.INSTANCE.FieldMandatory0( Constants.INSTANCE.ReleaseVersion() ) );
    }

    @Override
    public void showErrorVersionSnapshot() {
        versionTextGroup.setType( ControlGroupType.ERROR );
        versionTextHelpInline.setText( Constants.INSTANCE.SnapshotNotAvailableForRelease( "-SNAPSHOT" ) );
    }

    @Override
    public void showCurrentVersionHelpText( String currentRepositoryVersion ) {
        versionTextHelpInline.setText( Constants.INSTANCE.CurrentRepositoryVersion( currentRepositoryVersion ) );
    }

    @Override
    public String getSourceBranch() {
        return sourceBranchText.getText();
    }

    @Override
    public void showErrorSourceBranchNotRelease() {
        sourceBranchTextGroup.setType( ControlGroupType.ERROR );
        sourceBranchTextHelpInline.setText( Constants.INSTANCE.ReleaseCanOnlyBeDoneFromAReleaseBranch() );
    }

    @Override
    public boolean isDeployToRuntime() {
        return deployToRuntimeCheck.getValue();
    }

    @Override
    public void showErrorUserNameEmpty() {
        userNameTextGroup.setType( ControlGroupType.ERROR );
        userNameTextHelpInline.setText( Constants.INSTANCE.FieldMandatory0( Constants.INSTANCE.User_Name() ) );
    }

    @Override
    public void showErrorPasswordEmpty() {
        passwordTextGroup.setType( ControlGroupType.ERROR );
        passwordTextHelpInline.setText( Constants.INSTANCE.FieldMandatory0( Constants.INSTANCE.Password() ) );
    }

    @Override
    public void showErrorServerUrlEmpty() {
        serverURLTextGroup.setType( ControlGroupType.ERROR );
        serverURLTextHelpInline.setText( Constants.INSTANCE.FieldMandatory0( Constants.INSTANCE.Server_URL() ) );
    }

    @Override
    public void setSourceBranch( String branch ) {
        sourceBranchText.setText( branch );
    }

    @Override
    public void setRepository( String repositoryAlias ) {
        repositoryText.setText( repositoryAlias );
    }

    @Override
    public void setSourceBranchReadOnly( boolean b ) {
        sourceBranchText.setReadOnly( b );
    }

    @Override
    public void setRepositoryReadOnly( boolean b ) {
        repositoryText.setReadOnly( b );
    }

    @Override
    public void setServerURL( String serverUrl ) {
        serverURLText.setText( serverUrl );
    }

    @Override
    public void setVersion( String version ) {
        versionText.setText( version );
    }

    @Override
    public void setDeployToRuntime( boolean b ) {
        deployToRuntimeCheck.setValue( b, true );
    }

    @Override
    public void setUserNameEnabled( boolean b ) {
        userNameText.setEnabled( b );
    }

    @Override
    public void setPasswordEnabled( boolean b ) {
        passwordText.setEnabled( b );
    }

    @Override
    public void setServerURLEnabled( boolean b ) {
        serverURLText.setEnabled( b );
    }

    @Override
    public void init( ReleaseScreenPopupView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setUserName( String username ) {
        userNameText.setText( username );
    }

    @Override
    public void clearWidgetsState() {
        repositoryTextGroup.setType( ControlGroupType.NONE );
        repositoryTextHelpInline.setText( "" );
        sourceBranchTextGroup.setType( ControlGroupType.NONE );
        sourceBranchTextHelpInline.setText( "" );
        userNameTextGroup.setType( ControlGroupType.NONE );
        userNameTextHelpInline.setText( "" );
        passwordTextGroup.setType( ControlGroupType.NONE );
        passwordTextHelpInline.setText( "" );
        serverURLTextGroup.setType( ControlGroupType.NONE );
        serverURLTextHelpInline.setText( "" );
        deployToRuntimeTextGroup.setType( ControlGroupType.NONE );
        deployToRuntimeHelpInline.setText( "" );
        versionTextGroup.setType( ControlGroupType.NONE );
        versionTextHelpInline.setText( "" );
    }

}
