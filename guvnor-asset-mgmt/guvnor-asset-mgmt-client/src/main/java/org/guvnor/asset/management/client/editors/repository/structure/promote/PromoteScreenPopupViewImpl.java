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

package org.guvnor.asset.management.client.editors.repository.structure.promote;

import java.util.Collection;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class PromoteScreenPopupViewImpl extends BaseModal {

    interface PromoteScreenPopupWidgetBinder
            extends
            UiBinder<Widget, PromoteScreenPopupViewImpl> {

    }

    private PromoteScreenPopupWidgetBinder uiBinder = GWT.create( PromoteScreenPopupWidgetBinder.class );

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
    FormGroup targetBranchListBoxGroup;

    @UiField
    ListBox targetBranchListBox;

    @UiField
    HelpBlock targetBranchListBoxHelpBlock;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {

            if ( targetBranchListBox.getSelectedValue().equals( Constants.INSTANCE.Select_A_Branch() )
                    || targetBranchListBox.getSelectedValue().equals( sourceBranchText.getText() ) ) {
                targetBranchListBoxGroup.setValidationState( ValidationState.ERROR );
                targetBranchListBoxHelpBlock.setText( Constants.INSTANCE.FieldMandatory0( "Target Branch" ) );

                return;
            }

            if ( callbackCommand != null ) {
                callbackCommand.execute();
            }
            hide();
        }

    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand, cancelCommand );

    public PromoteScreenPopupViewImpl() {
        setTitle( Constants.INSTANCE.Promote_Assets() );
        setDataBackdrop( ModalBackdrop.STATIC );
        setDataKeyboard( true );
        setFade( true );
        setRemoveOnHide( true );

        setBody( uiBinder.createAndBindUi( this ) );
        add( footer );
    }

    public void configure( String repositoryAlias,
                           String branch,
                           Collection<String> branches,
                           Command command ) {
        this.callbackCommand = command;
        this.sourceBranchText.setText( branch );
        this.repositoryText.setText( repositoryAlias );
        this.sourceBranchText.setReadOnly( true );
        this.repositoryText.setReadOnly( true );
        targetBranchListBox.clear();
        this.targetBranchListBox.addItem( Constants.INSTANCE.Select_A_Branch() );

        for ( String b : branches ) {
            if ( !b.equals( branch ) ) {
                targetBranchListBox.addItem( b, b );
            }
        }
    }

    public String getTargetBranch() {
        return this.targetBranchListBox.getSelectedValue();
    }

}
