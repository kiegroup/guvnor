/*
 * Copyright 2005 JBoss Inc
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
package org.drools.guvnor.client.ruleeditor.toolbar;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * This contains the widgets used to action a rule asset
 * (ie checkin, change state, close window)
 * 
 * @author Michael Neale
 */
public class ActionToolbar extends Composite {

    private Constants constants = GWT.create( Constants.class );

    interface ActionToolbarBinder
        extends
        UiBinder<Widget, ActionToolbar> {
    }

    private static ActionToolbarBinder                uiBinder = GWT.create( ActionToolbarBinder.class );

    @UiField
    MenuItem                                          saveChanges;

    @UiField
    MenuItem                                          saveChangesAndClose;

    @UiField
    MenuItem                                          archive;

    @UiField
    MenuItem                                          delete;

    @UiField
    MenuItem                                          copy;

    @UiField
    MenuItem                                          promoteToGlobal;

    @UiField
    MenuItem                                          selectWorkingSets;

    @UiField
    MenuItem                                          validate;

    @UiField
    MenuItem                                          verify;

    @UiField
    MenuItem                                          viewSource;

    @UiField
    MenuItem                                          changeStatus;

    @UiField
    Label                                             status;

    private ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider;

    public ActionToolbar(ActionToolbarButtonsConfigurationProvider actionToolbarButtonsConfigurationProvider,
                         String status) {

        initWidget( uiBinder.createAndBindUi( this ) );

        this.actionToolbarButtonsConfigurationProvider = actionToolbarButtonsConfigurationProvider;

        setState( status );

        applyToolBarConfiguration();

        this.status.setVisible( this.actionToolbarButtonsConfigurationProvider.showStateLabel() );
    }

    /**
     * Sets the visible status display.
     */
    public void setState(String newStatus) {
        status.setText( Format.format( constants.statusIs(),
                                       newStatus ) );
    }

    private void applyToolBarConfiguration() {
        saveChanges.setVisible( actionToolbarButtonsConfigurationProvider.showSaveButton() );
        saveChangesAndClose.setVisible( actionToolbarButtonsConfigurationProvider.showSaveAndCloseButton() );
        validate.setVisible( actionToolbarButtonsConfigurationProvider.showValidateButton() );
        verify.setVisible( actionToolbarButtonsConfigurationProvider.showValidateButton() );
        viewSource.setVisible( actionToolbarButtonsConfigurationProvider.showViewSourceButton() );
        copy.setVisible( actionToolbarButtonsConfigurationProvider.showCopyButton() );
        promoteToGlobal.setVisible( actionToolbarButtonsConfigurationProvider.showPromoteToGlobalButton() );
        archive.setVisible( actionToolbarButtonsConfigurationProvider.showArchiveButton() );
        delete.setVisible( actionToolbarButtonsConfigurationProvider.showDeleteButton() );
        changeStatus.setVisible( actionToolbarButtonsConfigurationProvider.showChangeStatusButton() );
        selectWorkingSets.setVisible( actionToolbarButtonsConfigurationProvider.showSelectWorkingSetsButton() );
    }

    public void setSelectWorkingSetsCommand(Command command) {
        selectWorkingSets.setCommand( command );
    }

    public void setViewSourceCommand(Command command) {
        viewSource.setCommand( command );
    }

    public void setVerifyCommand(Command command) {
        verify.setCommand( command );
    }

    public void setValidateCommand(Command command) {
        validate.setCommand( command );
    }

    public void setSaveChangesCommand(Command command) {
        saveChanges.setCommand( command );
    }

    public void setSaveChangesAndCloseCommand(Command command) {
        saveChangesAndClose.setCommand( command );
    }

    public void setChangeStatusCommand(Command command) {
        changeStatus.setCommand( command );
    }

    public void setDeleteVisible(boolean b) {
        delete.setVisible( b );
    }

    public void setArchiveVisible(boolean b) {
        archive.setVisible( b );
    }

    public void setArciveCommand(final Command archiveCommand) {
        archive.setCommand( new Command() {

            public void execute() {
                if ( Window.confirm( constants.AreYouSureYouWantToArchiveThisItem() + "\n" + constants.ArchiveThisAssetThisWillNotPermanentlyDeleteIt() ) ) {
                    archiveCommand.execute();
                }
            }
        } );
    }

    public void setCopyCommand(Command command) {
        copy.setCommand( command );
    }

    public void setDeleteCommand(final Command deleteCommand) {
        delete.setCommand( new Command() {

            public void execute() {
                if ( Window.confirm( constants.DeleteAreYouSure() ) ) {
                    deleteCommand.execute();
                }
            }
        } );
    }

    public void setPromtToGlobalCommand(Command command) {
        promoteToGlobal.setCommand( command );
    }
}
