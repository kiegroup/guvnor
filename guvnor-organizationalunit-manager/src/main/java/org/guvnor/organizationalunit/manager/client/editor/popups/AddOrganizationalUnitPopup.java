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
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.organizationalunit.manager.client.editor.OrganizationalUnitManagerPresenter;
import org.guvnor.organizationalunit.manager.client.resources.i18n.OrganizationalUnitManagerConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.client.mvp.UberView;

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

    public AddOrganizationalUnitPopup() {
        setTitle( OrganizationalUnitManagerConstants.INSTANCE.AddOrganizationalUnitPopupTitle() );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

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

    private void onOKButtonClick() {
        nameGroup.setType( ControlGroupType.NONE );
        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( OrganizationalUnitManagerConstants.INSTANCE.OrganizationalUnitNameIsMandatory() );
            return;
        }

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

    private void onOKSuccess() {
        presenter.createNewOrganizationalUnit( nameTextBox.getText(),
                                               ownerTextBox.getText() );
        hide();
    }

    @Override
    public void show() {
        nameTextBox.setText( "" );
        nameGroup.setType( ControlGroupType.NONE );
        nameHelpInline.setText( "" );
        ownerTextBox.setText( "" );
        super.show();
    }
}
