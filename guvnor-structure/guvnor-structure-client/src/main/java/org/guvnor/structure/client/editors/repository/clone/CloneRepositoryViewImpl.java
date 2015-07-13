/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.client.editors.repository.clone;

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

@Dependent
public class CloneRepositoryViewImpl
        extends BaseModal implements CloneRepositoryView {

    interface CloneRepositoryFormBinder
            extends
            UiBinder<Widget, CloneRepositoryViewImpl> {

    }

    private CloneRepositoryView.Presenter presenter;

    private static CloneRepositoryFormBinder uiBinder = GWT.create( CloneRepositoryFormBinder.class );

    @UiField
    Button clone;

    @UiField
    Button cancel;

    @UiField
    ControlGroup organizationalUnitGroup;

    @UiField
    ListBox organizationalUnitDropdown;

    @UiField
    HelpInline organizationalUnitHelpInline;

    @UiField
    ControlGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    ControlGroup urlGroup;

    @UiField
    TextBox gitURLTextBox;

    @UiField
    HelpInline urlHelpInline;

    @UiField
    TextBox usernameTextBox;

    @UiField
    PasswordTextBox passwordTextBox;

    @UiField
    InlineHTML isOUMandatory;

    @UiHandler("clone")
    public void onCloneClick( final ClickEvent e ) {
        presenter.handleCloneClick();
    }

    @UiHandler("cancel")
    public void onCancelClick( final ClickEvent e ) {
        presenter.handleCancelClick();
    }

    @Override
    public void init( final CloneRepositoryView.Presenter presenter,
                      final boolean isOuMandatory ) {
        this.presenter = presenter;

        add( uiBinder.createAndBindUi( this ) );
        setTitle( CoreConstants.INSTANCE.CloneRepository() );

        if ( !isOuMandatory ) {
            isOUMandatory.removeFromParent();
        }

        nameTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                nameGroup.setType( ControlGroupType.NONE );
                nameHelpInline.setText( "" );
            }
        } );
        gitURLTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                urlGroup.setType( ControlGroupType.NONE );
                urlHelpInline.setText( "" );
            }
        } );
    }

    @Override
    public void addOrganizationalUnitSelectEntry() {
        organizationalUnitDropdown.addItem( CoreConstants.INSTANCE.SelectEntry() );
    }

    @Override
    public void addOrganizationalUnit( final String item,
                                       final String value ) {
        organizationalUnitDropdown.addItem( item,
                                            value );
    }

    @Override
    public int getSelectedOrganizationalUnit() {
        return organizationalUnitDropdown.getSelectedIndex();
    }

    @Override
    public String getOrganizationalUnit( final int index ) {
        return organizationalUnitDropdown.getValue( index );
    }

    @Override
    public boolean isGitUrlEmpty() {
        return gitURLTextBox.getText() == null || gitURLTextBox.getText().trim().isEmpty();
    }

    @Override
    public boolean isNameEmpty() {
        return nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty();
    }

    @Override
    public String getGitUrl() {
        return gitURLTextBox.getText().trim();
    }

    @Override
    public String getUsername() {
        return usernameTextBox.getText();
    }

    @Override
    public String getPassword() {
        return passwordTextBox.getText();
    }

    @Override
    public String getName() {
        return nameTextBox.getText();
    }

    @Override
    public void setName( final String name ) {
        nameTextBox.setText( name );
    }

    @Override
    public void showUrlHelpMandatoryMessage() {
        urlHelpInline.setText( CoreConstants.INSTANCE.URLMandatory() );
    }

    @Override
    public void showUrlHelpInvalidFormatMessage() {
        urlHelpInline.setText( CoreConstants.INSTANCE.InvalidUrlFormat() );
    }

    @Override
    public void setUrlGroupType( final ControlGroupType type ) {
        urlGroup.setType( type );
    }

    @Override
    public void showNameHelpMandatoryMessage() {
        nameHelpInline.setText( CoreConstants.INSTANCE.RepositoryNaneMandatory() );
    }

    @Override
    public void setNameGroupType( final ControlGroupType type ) {
        nameGroup.setType( type );
    }

    @Override
    public void showOrganizationalUnitHelpMandatoryMessage() {
        organizationalUnitHelpInline.setText( CoreConstants.INSTANCE.OrganizationalUnitMandatory() );
    }

    @Override
    public void setOrganizationalUnitGroupType( final ControlGroupType type ) {
        organizationalUnitGroup.setType( type );
    }

    @Override
    public void setNameEnabled( final boolean enabled ) {
        nameTextBox.setEnabled( enabled );
    }

    @Override
    public void setOrganizationalUnitEnabled( final boolean enabled ) {
        organizationalUnitDropdown.setEnabled( enabled );
    }

    @Override
    public void setGitUrlEnabled( final boolean enabled ) {
        gitURLTextBox.setEnabled( enabled );
    }

    @Override
    public void setUsernameEnabled( final boolean enabled ) {
        usernameTextBox.setEnabled( enabled );
    }

    @Override
    public void setPasswordEnabled( final boolean enabled ) {
        passwordTextBox.setEnabled( enabled );
    }

    @Override
    public void setCloneEnabled( final boolean enabled ) {
        clone.setEnabled( enabled );
    }

    @Override
    public void setCancelEnabled( final boolean enabled ) {
        cancel.setEnabled( enabled );
    }

    @Override
    public void setPopupCloseVisible( final boolean closeVisible ) {
        setCloseVisible( closeVisible );
    }

    @Override
    public void showBusyPopupMessage() {
        BusyPopup.showMessage( CoreConstants.INSTANCE.Cloning() );
    }

    @Override
    public void closeBusyPopup() {
        BusyPopup.close();
    }

    @Override
    public boolean showAgreeNormalizeNameWindow( final String normalizedName ) {
        return Window.confirm( CoreConstants.INSTANCE.RepositoryNameInvalid() + " \"" + normalizedName + "\". " + CoreConstants.INSTANCE.DoYouAgree() );
    }

    @Override
    public void alertRepositoryCloned() {
        Window.alert( CoreConstants.INSTANCE.RepoCloneSuccess() + "\n\n" + CommonConstants.INSTANCE.IndexClonedRepositoryWarning() );
    }

    @Override
    public void errorRepositoryAlreadyExist() {
        ErrorPopup.showMessage( CoreConstants.INSTANCE.RepoAlreadyExists() );
    }

    @Override
    public void errorCloneRepositoryFail( final Throwable cause ) {
        ErrorPopup.showMessage( CoreConstants.INSTANCE.RepoCloneFail() + " \n" + cause.getMessage() );
    }

    @Override
    public void errorLoadOrganizationalUnitsFail( final Throwable cause ) {
        ErrorPopup.showMessage( CoreConstants.INSTANCE.CantLoadOrganizationalUnits() + " \n" + cause.getMessage() );
    }

}
