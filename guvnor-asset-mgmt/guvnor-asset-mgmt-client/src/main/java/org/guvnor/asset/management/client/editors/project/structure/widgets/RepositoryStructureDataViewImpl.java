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

package org.guvnor.asset.management.client.editors.project.structure.widgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;

public class RepositoryStructureDataViewImpl extends Composite
        implements RepositoryStructureDataView {

    interface NewRepositoryStructureDataViewImplUIBinder
            extends UiBinder<Widget, RepositoryStructureDataViewImpl> {

    }

    private static NewRepositoryStructureDataViewImplUIBinder uiBinder = GWT.create(NewRepositoryStructureDataViewImplUIBinder.class );

    @UiField
    RadioButton isSingleModuleRadioButton;

    @UiField
    RadioButton isMultiModuleRadioButton;

    @UiField
    TextBox groupIdTextBox;

    @UiField
    TextBox artifactIdTextBox;

    @UiField
    TextBox versionTextBox;

    @UiField
    HelpInline isSingleModuleRadioButtonHelpInline;

    @UiField
    HelpInline isMultiModuleRadioButtonHelpInline;

    @UiField
    HelpInline groupIdTextBoxHelpInline;

    @UiField
    HelpInline artifactIdTextBoxHelpInline;

    @UiField
    HelpInline versionTextBoxHelpInline;

    @UiField
    Button initRepositoryStructureButton;

    @UiField
    Label projectTypeLabel;

    @UiField
    com.google.gwt.user.client.ui.Label singleProjectGroupId;

    @UiField
    TextBox singleProjectGroupIdTextBox;

    @UiField
    com.google.gwt.user.client.ui.Label singleProjectArtifactId;

    @UiField
    TextBox singleProjectArtifactIdTextBox;

    @UiField
    com.google.gwt.user.client.ui.Label singleProjectVersion;

    @UiField
    TextBox singleProjectVersionTextBox;

    @UiField
    RadioButton isUnmanagedRepositoryRadioButton;

    @UiField
    HelpInline isUnmanagedRepositoryButtonHelpInline;

    private Presenter presenter;

    private ViewMode mode;

    public RepositoryStructureDataViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

        clear();
        setCurrentSingleProjectInfoVisible( false );
        setMode( ViewMode.CREATE_STRUCTURE );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setGroupId( String groupId ) {
        groupIdTextBox.setText( groupId );
    }

    @Override
    public String getGroupId() {
        return groupIdTextBox.getText();
    }

    @Override
    public void setArtifactId( String artifactId ) {
        artifactIdTextBox.setText( artifactId );
    }

    @Override
    public String getArtifactId() {
        return artifactIdTextBox.getText();
    }

    @Override
    public void setVersion( String version ) {
        versionTextBox.setText( version );
    }

    @Override
    public String getVersionId() {
        return versionTextBox.getText();
    }

    @Override
    public void setMultiModule() {
        enableMultiModeParams();
    }

    @Override
    public void setSingleModule() {
        enableSingleModeParams();
    }

    @Override
    public boolean isSingleModule() {
        return isSingleModuleRadioButton.getValue();
    }

    @Override
    public boolean isMultiModule() {
        return isMultiModuleRadioButton.getValue();
    }

    @Override
    public boolean isUnmanagedRepository() {
        return isUnmanagedRepositoryRadioButton.getValue();
    }

    @Override
    public void setSingleProjectGroupId( String groupId ) {
        singleProjectGroupIdTextBox.setText( groupId );
    }

    @Override
    public void setSingleProjectArtifactId( String artifactId ) {
        singleProjectArtifactIdTextBox.setText( artifactId );
    }

    @Override
    public void setSingleProjectVersion( String version ) {
        singleProjectVersionTextBox.setText( version );
    }

    @Override
    public void setMode( ViewMode mode ) {
        this.mode = mode;

        if ( mode == ViewMode.CREATE_STRUCTURE ) {

            projectTypeLabel.setText( Constants.INSTANCE.Repository_structure_view_create_projectTypeLabel() );

            isSingleModuleRadioButton.setText( Constants.INSTANCE.Repository_structure_view_create_isSingleModuleRadioButton() );
            isSingleModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_isSingleModuleRadioButtonHelpInline() );
            isSingleModuleRadioButton.setEnabled( true );

            isMultiModuleRadioButton.setText( Constants.INSTANCE.Repository_structure_view_create_isMultiModuleRadioButton() );
            isMultiModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_isMultiModuleRadioButtonHelpInline() );
            isMultiModuleRadioButton.setEnabled( true );

            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_versionTextBoxHelpInline() );

            isUnmanagedRepositoryRadioButton.setText( Constants.INSTANCE.Repository_structure_view_create_isUnmanagedRepositoryRadioButton() );
            isUnmanagedRepositoryButtonHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_isUnmanagedRepositoryButtonHelpInline() );
            isUnmanagedRepositoryRadioButton.setEnabled( true );

            initRepositoryStructureButton.setText( Constants.INSTANCE.InitRepositoryStructure() );
            initRepositoryStructureButton.setVisible( true );

            enableSingleModeParams();
            setCurrentSingleProjectInfoVisible( false );

        } else if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {

            projectTypeLabel.setText( Constants.INSTANCE.Repository_structure_view_edit_single_projectTypeLabel() );

            isSingleModuleRadioButton.setText( Constants.INSTANCE.Repository_structure_view_edit_single_isSingleModuleRadioButton() );
            isSingleModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_single_isSingleModuleRadioButtonHelpInline() );
            isSingleModuleRadioButton.setEnabled( true );

            isMultiModuleRadioButton.setText( Constants.INSTANCE.Repository_structure_view_edit_single_isMultiModuleRadioButton() );
            isMultiModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_single_isMultiModuleRadioButtonHelpInline() );
            isMultiModuleRadioButton.setEnabled( true );

            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_single_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_single_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_single_versionTextBoxHelpInline() );

            enableSingleModeParams();

            isUnmanagedRepositoryButtonHelpInline.setVisible( false );
            isUnmanagedRepositoryRadioButton.setVisible( false );

            initRepositoryStructureButton.setText( Constants.INSTANCE.EditProject() );
            initRepositoryStructureButton.setVisible( true );

            setCurrentSingleProjectInfoVisible( true );

        } else if ( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT ) {

            projectTypeLabel.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_projectTypeLabel() );

            enableMultiModeParams();

            isMultiModuleRadioButton.setEnabled( false );

            isSingleModuleRadioButton.setVisible( false );
            isSingleModuleRadioButtonHelpInline.setVisible( false );

            isMultiModuleRadioButton.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_isMultiModuleRadioButton() );
            isMultiModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_isMultiModuleRadioButtonHelpInline() );
            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_versionTextBoxHelpInline() );

            initRepositoryStructureButton.setText( Constants.INSTANCE.SaveChanges() );

            isUnmanagedRepositoryRadioButton.setVisible( false );
            isUnmanagedRepositoryButtonHelpInline.setVisible( false );

            setCurrentSingleProjectInfoVisible( false );

        } else if ( mode == ViewMode.EDIT_UNMANAGED_REPOSITORY ) {

            projectTypeLabel.setText( Constants.INSTANCE.Repository_structure_view_edit_unmanaged_projectTypeLabel() );

            //enable unmanaged mode fields
            isUnmanagedRepositoryRadioButton.setVisible( true );
            isUnmanagedRepositoryRadioButton.setValue( true );
            isUnmanagedRepositoryRadioButton.setEnabled( false );
            isUnmanagedRepositoryButtonHelpInline.setVisible( true );

            isUnmanagedRepositoryRadioButton.setText( Constants.INSTANCE.Repository_structure_view_edit_unmanaged_isUnmanagedRepositoryRadioButton() );
            isUnmanagedRepositoryButtonHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_unmanaged_isUnmanagedRepositoryButtonHelpInline() );


            //disable single mode fields
            isSingleModuleRadioButton.setVisible( false );
            isSingleModuleRadioButtonHelpInline.setVisible( false );
            setCurrentSingleProjectInfoVisible( false );

            //disable multi mode fields.
            isMultiModuleRadioButton.setVisible( false );
            isMultiModuleRadioButtonHelpInline.setVisible( false );
            groupIdTextBox.setVisible( false );
            groupIdTextBoxHelpInline.setVisible( false );
            artifactIdTextBox.setVisible( false );
            artifactIdTextBoxHelpInline.setVisible( false );
            versionTextBox.setVisible( false );
            versionTextBoxHelpInline.setVisible( false );

            initRepositoryStructureButton.setVisible( false );
        }
    }

    public void enableMultiModeParams() {
        enableModeParams( ViewMode.EDIT_MULTI_MODULE_PROJECT );
    }

    public void enableSingleModeParams() {
        enableModeParams( ViewMode.EDIT_SINGLE_MODULE_PROJECT );
    }

    public void enableUnmanagedStructureMode() {
        //TODO
    }

    public void clear() {
        singleProjectGroupIdTextBox.setText( null );
        singleProjectArtifactIdTextBox.setText( null );
        singleProjectVersionTextBox.setText( null );
        groupIdTextBox.setText( null );
        artifactIdTextBox.setText( null );
        versionTextBox.setText( null );
    }

    @Override
    public void enableActions( boolean value ) {
        isSingleModuleRadioButton.setEnabled( value );
        isMultiModuleRadioButton.setEnabled( value );
        initRepositoryStructureButton.setEnabled( value );
    }

    @Override public void setReadonly( boolean readonly ) {

        groupIdTextBox.setReadOnly( readonly );
        artifactIdTextBox.setReadOnly( readonly );
        versionTextBox.setReadOnly( readonly );
        singleProjectGroupIdTextBox.setReadOnly( readonly );
        singleProjectArtifactIdTextBox.setReadOnly( readonly );
        singleProjectVersionTextBox.setReadOnly( readonly );

        enableActions( !readonly );
    }

    private void enableModeParams( ViewMode mode ) {

        //single mode fields
        isSingleModuleRadioButton.setVisible( true );
        isSingleModuleRadioButtonHelpInline.setVisible( true );
        isSingleModuleRadioButton.setValue( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT );

        //multi mode fields.
        isMultiModuleRadioButton.setVisible( true );
        isMultiModuleRadioButtonHelpInline.setVisible( true );
        isMultiModuleRadioButton.setValue( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT );

        isUnmanagedRepositoryRadioButton.setVisible( true );
        isUnmanagedRepositoryButtonHelpInline.setVisible( true );
        isUnmanagedRepositoryRadioButton.setValue( mode == ViewMode.EDIT_UNMANAGED_REPOSITORY );

        groupIdTextBox.setVisible( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT );
        groupIdTextBoxHelpInline.setVisible( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT );
        artifactIdTextBox.setVisible( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT );
        artifactIdTextBoxHelpInline.setVisible( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT );
        versionTextBox.setVisible( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT );
        versionTextBoxHelpInline.setVisible( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT );
    }

    private void setCurrentSingleProjectInfoVisible( boolean visible ) {
        singleProjectGroupId.setVisible( visible );
        singleProjectGroupIdTextBox.setVisible( visible );
        singleProjectGroupIdTextBox.setReadOnly( true );
        singleProjectArtifactId.setVisible( visible );
        singleProjectArtifactIdTextBox.setVisible( visible );
        singleProjectArtifactIdTextBox.setReadOnly( true );
        singleProjectVersion.setVisible( visible );
        singleProjectVersionTextBox.setVisible( visible );
        singleProjectVersionTextBox.setReadOnly( true );
    }

    //UI handlers.

    @UiHandler( "initRepositoryStructureButton" )
    void onInitRepositoryStructureClick( final ClickEvent e ) {
        if ( mode == ViewMode.CREATE_STRUCTURE ) {
            presenter.onInitRepositoryStructure();
        } else if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {
            if ( isSingleModuleRadioButton.getValue() ) {
                presenter.onOpenSingleProject();
            } else {
                presenter.onConvertToMultiModule();
            }
        } else if ( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT ) {
            presenter.onSaveRepositoryStructure();
        }
    }

    @UiHandler( "isMultiModuleRadioButton" )
    void multiModuleCheckBoxClicked( final ClickEvent event ) {
        enableMultiModeParams();
        if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {
            initRepositoryStructureButton.setVisible( true );
            initRepositoryStructureButton.setText( Constants.INSTANCE.ConvertToMultiModule() );

            groupIdTextBox.setText( singleProjectGroupIdTextBox.getText() );
            versionTextBox.setText( singleProjectVersionTextBox.getText() );

            isUnmanagedRepositoryRadioButton.setVisible( false );
            isUnmanagedRepositoryButtonHelpInline.setVisible( false );
        }
        presenter.onProjectModeChange();
    }

    @UiHandler( "isSingleModuleRadioButton" )
    void singleModuleCheckBoxClicked( final ClickEvent event ) {
        enableSingleModeParams();
        if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {
            initRepositoryStructureButton.setVisible( true );
            initRepositoryStructureButton.setText( Constants.INSTANCE.EditProject() );

            isUnmanagedRepositoryRadioButton.setVisible( false );
            isUnmanagedRepositoryButtonHelpInline.setVisible( false );
        }
        presenter.onProjectModeChange();
    }

    @UiHandler( "isUnmanagedRepositoryRadioButton" )
    void setUnmanagedRepositoryRadioButtonClicked( final ClickEvent event ) {
        enableModeParams( ViewMode.EDIT_UNMANAGED_REPOSITORY );
        presenter.onProjectModeChange();
    }
}