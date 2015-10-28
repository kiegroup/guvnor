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

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryStructurePageViewImpl extends Composite
        implements RepositoryStructurePageView {

    interface RepositoryStructurePageBinder extends UiBinder<Widget, RepositoryStructurePageViewImpl> {

    }

    private static RepositoryStructurePageBinder uiBinder = GWT.create( RepositoryStructurePageBinder.class );

    private Presenter presenter;

    @UiField
    TextBox projectNameTextBox;

    @UiField
    HelpInline projectNameTextBoxHelpInline;

    @UiField
    TextBox projectDescriptionTextBox;

    @UiField
    TextBox groupIdTextBox;

    @UiField
    HelpInline groupIdTextBoxHelpInline;

    @UiField
    TextBox artifactIdTextBox;

    @UiField
    HelpInline artifactIdTextBoxHelpInline;

    @UiField
    TextBox versionTextBox;

    @UiField
    HelpInline versionTextBoxHelpInline;

    @UiField
    RadioButton isSingleModuleRadioButton;


    @UiField
    HelpInline isSingleModuleRadioButtonHelpInline;


    @UiField
    RadioButton isMultiModuleRadioButton;


    @UiField
    HelpInline isMultiModuleRadioButtonHelpInline;


    @UiField
    CheckBox isConfigureRepositoryCheckBox;

    public RepositoryStructurePageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        initializeFields();
    }

    @Override
    public String getProjectName() {
        return projectNameTextBox.getText();
    }

    @Override
    public void setProjectName( String projectName ) {
        projectNameTextBox.setText( projectName );
    }

    @Override
    public void setProjectNameErrorMessage( String errorMessage ) {
        projectNameTextBoxHelpInline.setText( errorMessage );
    }

    @Override
    public void clearProjectNameErrorMessage( ) {
        projectNameTextBoxHelpInline.setText( null );
    }

    @Override
    public void setGroupId( String groupId ) {
        groupIdTextBox.setText( groupId );
    }

    @Override
    public void setGroupIdErrorMessage( String errorMessage ) {
        groupIdTextBoxHelpInline.setText( errorMessage );
    }

    @Override
    public void clearGroupIdErrorMessage() {
        groupIdTextBoxHelpInline.setText( null );
    }

    @Override
    public void setArtifactId( String artifactId ) {
        artifactIdTextBox.setText( artifactId );
    }

    @Override
    public void setArtifactIdErrorMessage( String errorMessage ) {
        artifactIdTextBoxHelpInline.setText( errorMessage );
    }

    @Override
    public void clearArtifactIdErrorMessage() {
        artifactIdTextBoxHelpInline.setText( null );
    }

    @Override
    public void setVersion( String version ) {
        versionTextBox.setText( version );
    }

    @Override
    public void setVersionErrorMessage( String errorMessage ) {
        versionTextBoxHelpInline.setText( errorMessage );
    }

    @Override
    public void clearVersionErrorMessage() {
        versionTextBoxHelpInline.setText( null );
    }

    @Override
    public void setConfigureRepository( boolean configureRepository ) {
        isConfigureRepositoryCheckBox.setValue( configureRepository );
    }

    @Override
    public String getProjectDescription() {
        return projectDescriptionTextBox.getText();
    }

    @Override
    public void setProjectDescription( String projectDescription ) {
        projectDescriptionTextBox.setText( projectDescription );
    }

    @Override
    public String getGroupId() {
        return groupIdTextBox.getText();
    }

    @Override
    public String getArtifactId() {
        return artifactIdTextBox.getText();
    }

    @Override
    public String getVersion() {
        return versionTextBox.getText();
    }

    @Override
    public boolean isMultiModule() {
        return isMultiModuleRadioButton.getValue();
    }

    @Override
    public boolean isSingleModule() {
        return isSingleModuleRadioButton.getValue();
    }

    @Override
    public boolean isConfigureRepository() {
        return isConfigureRepositoryCheckBox.getValue();
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    private void initializeFields() {
        isMultiModuleRadioButton.setValue( true );
        isConfigureRepositoryCheckBox.setValue( true );

        projectNameTextBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onProjectNameChange();
            }
        } );

        projectDescriptionTextBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onProjectDescriptionChange();
            }
        } );

        groupIdTextBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onGroupIdChange();
            }
        } );

        artifactIdTextBox.addChangeHandler( new ChangeHandler() {
            @Override public void onChange( ChangeEvent event ) {
                presenter.onArtifactIdChange();
            }
        } );

        versionTextBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onVersionChange();
            }
        } );

        isSingleModuleRadioButton.addClickHandler( new ClickHandler() {
            @Override public void onClick( ClickEvent event ) {
                presenter.onSingleModuleChange();
            }
        } );

        isMultiModuleRadioButton.addClickHandler( new ClickHandler() {
            @Override public void onClick( ClickEvent event ) {
                presenter.onMultiModuleChange();
            }
        } );

        isConfigureRepositoryCheckBox.addClickHandler( new ClickHandler() {
            @Override public void onClick( ClickEvent event ) {
                presenter.onConfigureRepositoryChange();
            }
        } );

    }
}
