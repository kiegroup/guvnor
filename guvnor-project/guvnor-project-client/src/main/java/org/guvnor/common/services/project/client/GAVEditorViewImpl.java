/*
 * Copyright 2014 JBoss Inc
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
package org.guvnor.common.services.project.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.client.resources.ProjectResources;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;

public class GAVEditorViewImpl
        extends Composite
        implements GAVEditorView {

    interface Binder
            extends UiBinder<Widget, GAVEditorViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    FormGroup groupIdGroup;

    @UiField
    TextBox groupIdTextBox;

    @UiField
    HelpBlock groupIdHelpBlock;

    @UiField
    FormGroup artifactIdGroup;

    @UiField
    TextBox artifactIdTextBox;

    @UiField
    HelpBlock artifactIdHelpBlock;

    @UiField
    FormGroup versionGroup;

    @UiField
    TextBox versionTextBox;

    @UiField
    HelpBlock versionHelpBlock;

    private Presenter presenter;

    public GAVEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setGroupId( String id ) {
        groupIdTextBox.setText( id );
    }

    @Override
    public void setArtifactId( String id ) {
        artifactIdTextBox.setText( id );
    }

    @Override
    public void setVersion( String version ) {
        versionTextBox.setText( version );
    }

    @Override
    public void disableGroupID( String reason ) {
        groupIdTextBox.setEnabled( false );
        groupIdTextBox.setTitle( reason );
    }

    @Override
    public void disableArtifactID( String reason ) {
        artifactIdTextBox.setEnabled( false );
        artifactIdTextBox.setTitle( reason );
    }

    @Override
    public void disableVersion( String reason ) {
        versionTextBox.setEnabled( false );
        versionTextBox.setTitle( reason );
    }

    @Override
    public void setReadOnly() {
        groupIdTextBox.setReadOnly( true );
        artifactIdTextBox.setReadOnly( true );
        versionTextBox.setReadOnly( true );
    }

    @Override
    public void enableGroupID() {
        groupIdTextBox.setEnabled( true );
        groupIdTextBox.setTitle( "" );
    }

    @Override
    public void enableVersion() {
        versionTextBox.setEnabled( true );
        versionTextBox.setTitle( "" );
    }

    @Override
    public void setValidGroupID( final boolean isValid ) {
        if ( isValid ) {
            groupIdGroup.setValidationState( ValidationState.NONE );
            groupIdHelpBlock.setText( "" );
        } else {
            groupIdGroup.setValidationState( ValidationState.ERROR );
            groupIdHelpBlock.setText( ProjectResources.CONSTANTS.invalidGroupId() );
        }
    }

    @Override
    public void setValidArtifactID( final boolean isValid ) {
        if ( isValid ) {
            artifactIdGroup.setValidationState( ValidationState.NONE );
            artifactIdHelpBlock.setText( "" );
        } else {
            artifactIdGroup.setValidationState( ValidationState.ERROR );
            artifactIdHelpBlock.setText( ProjectResources.CONSTANTS.invalidArtifactId() );
        }
    }

    @Override
    public void setValidVersion( final boolean isValid ) {
        if ( isValid ) {
            versionGroup.setValidationState( ValidationState.NONE );
            versionHelpBlock.setText( "" );
        } else {
            versionGroup.setValidationState( ValidationState.ERROR );
            versionHelpBlock.setText( ProjectResources.CONSTANTS.invalidVersion() );
        }
    }

    @UiHandler("groupIdTextBox")
    //Use KeyUpEvent as ValueChangeEvent is only fired when the focus is lost
    public void onGroupIdChange( KeyUpEvent event ) {
        presenter.onGroupIdChange( groupIdTextBox.getText() );
    }

    @UiHandler("artifactIdTextBox")
    //Use KeyUpEvent as ValueChangeEvent is only fired when the focus is lost
    public void onArtifactIdChange( KeyUpEvent event ) {
        presenter.onArtifactIdChange( artifactIdTextBox.getText() );
    }

    @UiHandler("versionTextBox")
    //Use KeyUpEvent as ValueChangeEvent is only fired when the focus is lost
    public void onVersionChange( KeyUpEvent event ) {
        presenter.onVersionChange( versionTextBox.getText() );
    }

}
