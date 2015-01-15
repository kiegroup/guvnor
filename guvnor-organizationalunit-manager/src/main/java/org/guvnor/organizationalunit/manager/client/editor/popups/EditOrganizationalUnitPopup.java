/*
 * Copyright 2013 JBoss Inc
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

package org.guvnor.organizationalunit.manager.client.editor.popups;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.organizationalunit.manager.client.editor.OrganizationalUnitManagerPresenter;
import org.guvnor.organizationalunit.manager.client.resources.i18n.OrganizationalUnitManagerConstants;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class EditOrganizationalUnitPopup extends BaseModal implements UberView<OrganizationalUnitManagerPresenter> {

    interface EditOrganizationalUnitPopupBinder
            extends
            UiBinder<Widget, EditOrganizationalUnitPopup> {

    }

    private static EditOrganizationalUnitPopupBinder uiBinder = GWT.create( EditOrganizationalUnitPopupBinder.class );

    @UiField
    TextBox nameTextBox;

    @UiField
    TextBox ownerTextBox;

    @UiField
    ControlGroup defaultGroupIdGroup;

    @UiField
    TextBox defaultGroupIdTextBox;

    @UiField
    HelpInline defaultGroupIdHelpInline;

    @UiField
    Icon groupIdHelpIcon;

    private OrganizationalUnit organizationalUnit;

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

    public EditOrganizationalUnitPopup() {
        setTitle( OrganizationalUnitManagerConstants.INSTANCE.EditOrganizationalUnitPopupTitle() );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        groupIdHelpIcon.getElement().getStyle().setPaddingLeft( 5, Style.Unit.PX );
        groupIdHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
    }

    @Override
    public void init( final OrganizationalUnitManagerPresenter presenter ) {
        this.presenter = presenter;
    }

    private void onOKButtonClick() {
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
                        presenter.saveOrganizationalUnit( nameTextBox.getText(),
                                ownerTextBox.getText(),
                                defaultGroupIdTextBox.getText() );
                        hide();
                    }
                }
            } );
        }
    }

    public void setOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    @Override
    public void show() {
        defaultGroupIdGroup.setType( ControlGroupType.NONE );
        defaultGroupIdHelpInline.setText( "" );

        if ( organizationalUnit == null ) {
            nameTextBox.setText( "" );
            defaultGroupIdTextBox.setText( "" );
            ownerTextBox.setText( "" );
            super.show();
        } else {
            presenter.getSanitizedGroupId( organizationalUnit.getName(), new RemoteCallback<String>() {
                @Override
                public void callback( final String sanitizedGroupId ) {
                    nameTextBox.setText( organizationalUnit.getName() );

                    if ( organizationalUnit.getDefaultGroupId() == null || organizationalUnit.getDefaultGroupId().trim().isEmpty() ) {
                        defaultGroupIdTextBox.setText( sanitizedGroupId );
                    } else {
                        defaultGroupIdTextBox.setText( organizationalUnit.getDefaultGroupId() );
                    }

                    ownerTextBox.setText( organizationalUnit.getOwner() );
                    EditOrganizationalUnitPopup.super.show();
                }
            } );
        }
    }

}
