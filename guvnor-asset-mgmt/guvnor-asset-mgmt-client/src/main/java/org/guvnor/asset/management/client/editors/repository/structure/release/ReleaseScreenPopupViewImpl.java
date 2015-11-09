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

    private Command callbackCommand;

    private final ModalFooterOKCancelButtons footer;

    private ReleaseScreenPopupPresenter presenter;

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
    }

    //Defer delegation to Presenter until after it has been set
    private Command getOkCommand() {
        return new Command() {
            @Override
            public void execute() {
                if ( presenter != null ) {
                    presenter.getOkCommand().execute();
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
                    presenter.getCancelCommand().execute();
                }
            }
        };
    }

    public ReleaseScreenPopupPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void show() {
        super.show(); //To change body of generated methods, choose Tools | Templates.
        clearWidgetsState();
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
    public void setVersionStatus( ControlGroupType status ) {
        versionTextGroup.setType( status );
    }

    @Override
    public void setVersionHelpText( String helpText ) {
        versionTextHelpInline.setText( helpText );
    }

    @Override
    public String getSourceBranch() {
        return sourceBranchText.getText();
    }

    @Override
    public void setSourceBranchStatus( ControlGroupType status ) {
        sourceBranchTextGroup.setType( status );
    }

    @Override
    public void setSourceBranchHelpText( String helpText ) {
        sourceBranchTextHelpInline.setText( helpText );
    }

    @Override
    public boolean isDeployToRuntime() {
        return deployToRuntimeCheck.getValue();
    }

    @Override
    public void setUserNameStatus( ControlGroupType status ) {
        userNameTextGroup.setType( status );
    }

    @Override
    public void setUserNameTextHelp( String helpText ) {
        userNameTextHelpInline.setText( helpText );
    }

    @Override
    public void setPasswordStatus( ControlGroupType status ) {
        passwordTextGroup.setType( status );
    }

    @Override
    public void setPasswordHelpText( String helpText ) {
        passwordTextHelpInline.setText( helpText );
    }

    @Override
    public void setServerURLStatus( ControlGroupType status ) {
        serverURLTextGroup.setType( status );
    }

    @Override
    public void setServerURLHelpText( String helpText ) {
        serverURLTextHelpInline.setText( helpText );
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
    public void init( ReleaseScreenPopupPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setUserName( String username ) {
        userNameText.setText( username );
    }

    @Override
    public void setDeployToRuntimeValueChangeHandler( ValueChangeHandler<Boolean> valueChangeHandler ) {
        deployToRuntimeCheck.addValueChangeHandler( valueChangeHandler );
    }

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
