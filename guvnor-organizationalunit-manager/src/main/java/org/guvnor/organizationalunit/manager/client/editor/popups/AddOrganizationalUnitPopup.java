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

package org.guvnor.organizationalunit.manager.client.editor.popups;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.organizationalunit.manager.client.editor.OrganizationalUnitManagerPresenter;
import org.guvnor.organizationalunit.manager.client.resources.i18n.OrganizationalUnitManagerConstants;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class AddOrganizationalUnitPopup extends BaseModal implements UberView<OrganizationalUnitManagerPresenter> {

    interface AddOrganizationalUnitPopupBinder
            extends
            UiBinder<Widget, AddOrganizationalUnitPopup> {

    }

    private static AddOrganizationalUnitPopupBinder uiBinder = GWT.create( AddOrganizationalUnitPopupBinder.class );

    @UiField
    ControlGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    ControlGroup defaultGroupIdGroup;

    @UiField
    TextBox defaultGroupIdTextBox;

    @UiField
    HelpInline defaultGroupIdHelpInline;

    @UiField
    Icon groupIdHelpIcon;

    @UiField
    TextBox ownerTextBox;

    private OrganizationalUnitManagerPresenter presenter;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            onOKButtonClick();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand,
                                                                                      cancelCommand );

    private boolean groupIdManuallyChanged = false;

    public AddOrganizationalUnitPopup() {
        setTitle( OrganizationalUnitManagerConstants.INSTANCE.AddOrganizationalUnitPopupTitle() );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        groupIdHelpIcon.getElement().getStyle().setPaddingLeft( 5, Style.Unit.PX );
        groupIdHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );

        nameTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                nameGroup.setType( ControlGroupType.NONE );
                nameHelpInline.setText( "" );
            }
        } );
    }

    @Override
    public void init( final OrganizationalUnitManagerPresenter presenter ) {
        this.presenter = presenter;
    }

    @UiHandler("nameTextBox")
    void nameChanged( final ValueChangeEvent<String> event ) {
        // Only change the value of the default group id of it hasn't been modified manually already
        if ( !groupIdManuallyChanged ) {
            if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
                defaultGroupIdTextBox.setText( "" );
            } else {
                presenter.getSanitizedGroupId( nameTextBox.getText(), new RemoteCallback<String>() {
                    @Override
                    public void callback( final String sanitizedGroupId ) {
                        defaultGroupIdTextBox.setText( sanitizedGroupId );
                    }
                } );
            }
        }
    }

    @UiHandler("defaultGroupIdTextBox")
    void groupIdChangedChanged( final ValueChangeEvent<String> event ) {
        String input = defaultGroupIdTextBox.getText();
        if ( input == null || input.trim().isEmpty() || input.trim().equals( nameTextBox.getText() ) ) {
            groupIdManuallyChanged = false;
        } else {
            groupIdManuallyChanged = true;
        }
    }

    private void onOKButtonClick() {
        nameGroup.setType( ControlGroupType.NONE );
        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( OrganizationalUnitManagerConstants.INSTANCE.OrganizationalUnitNameIsMandatory() );
            return;
        }

        if ( defaultGroupIdTextBox.getText() == null || defaultGroupIdTextBox.getText().trim().isEmpty() ) {
            defaultGroupIdGroup.setType( ControlGroupType.ERROR );
            defaultGroupIdHelpInline.setText( OrganizationalUnitManagerConstants.INSTANCE.DefaultGroupIdIsMandatory() );
            return;
        } else {
            presenter.checkValidGroupId( defaultGroupIdTextBox.getText(), new RemoteCallback<Boolean>() {
                @Override
                public void callback( Boolean valid ) {
                    if ( !valid ) {
                        defaultGroupIdGroup.setType( ControlGroupType.ERROR );
                        defaultGroupIdHelpInline.setText( OrganizationalUnitManagerConstants.INSTANCE.InvalidGroupId() );
                        return;
                    } else {
                        presenter.checkIfOrganizationalUnitExists( nameTextBox.getText(),
                                new Command() {
                                    @Override
                                    public void execute() {
                                        onOKSuccess();
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        nameGroup.setType( ControlGroupType.ERROR );
                                        nameHelpInline.setText( OrganizationalUnitManagerConstants.INSTANCE.OrganizationalUnitAlreadyExists() );
                                    }

                                }
                        );
                    }
                }
            } );
        }
    }

    private void onOKSuccess() {
        presenter.createNewOrganizationalUnit( nameTextBox.getText(),
                                               ownerTextBox.getText(),
                                               defaultGroupIdTextBox.getText() );
        hide();
    }

    @Override
    public void show() {
        nameTextBox.setText( "" );
        nameGroup.setType( ControlGroupType.NONE );
        nameHelpInline.setText( "" );
        defaultGroupIdTextBox.setText( "" );
        defaultGroupIdGroup.setType( ControlGroupType.NONE );
        defaultGroupIdHelpInline.setText( "" );
        this.groupIdManuallyChanged = false;
        ownerTextBox.setText( "" );
        super.show();
    }
}
