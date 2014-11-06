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

import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
    Label groupIdTextBox;

    @UiField
    Label artifactIdTextBox;

    @UiField
    Label versionTextBox;


    @UiField
    HelpInline groupIdTextBoxHelpInline;

    @UiField
    HelpInline artifactIdTextBoxHelpInline;

    @UiField
    HelpInline versionTextBoxHelpInline;


    @UiField
    Label projectTypeLabel;


    private Presenter presenter;

    private ViewMode mode;

    public RepositoryStructureDataViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

        clear();
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
    public void setMode( ViewMode mode ) {
        this.mode = mode;

        if ( mode == ViewMode.CREATE_STRUCTURE ) {

            projectTypeLabel.setText( Constants.INSTANCE.Repository_structure_view_create_projectTypeLabel() );



            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_create_versionTextBoxHelpInline() );



        } else if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {

            projectTypeLabel.setText( Constants.INSTANCE.Repository_structure_view_edit_single_projectTypeLabel() );



            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_single_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_single_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_single_versionTextBoxHelpInline() );

            groupIdTextBox.setVisible( true );
            groupIdTextBoxHelpInline.setVisible( true );
            artifactIdTextBox.setVisible( true );
            artifactIdTextBoxHelpInline.setVisible( true );
            versionTextBox.setVisible( true );
            versionTextBoxHelpInline.setVisible( true );

            

        } else if ( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT ) {

            projectTypeLabel.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_projectTypeLabel() );


            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Repository_structure_view_edit_multi_versionTextBoxHelpInline() );


            groupIdTextBox.setVisible( true );
            groupIdTextBoxHelpInline.setVisible( true );
            artifactIdTextBox.setVisible( true );
            artifactIdTextBoxHelpInline.setVisible( true );
            versionTextBox.setVisible( true );
            versionTextBoxHelpInline.setVisible( true );

        } else if ( mode == ViewMode.EDIT_UNMANAGED_REPOSITORY ) {

            projectTypeLabel.setText( Constants.INSTANCE.Repository_structure_view_edit_unmanaged_projectTypeLabel() );

 
            groupIdTextBox.setVisible( false );
            groupIdTextBoxHelpInline.setVisible( false );
            artifactIdTextBox.setVisible( false );
            artifactIdTextBoxHelpInline.setVisible( false );
            versionTextBox.setVisible( false );
            versionTextBoxHelpInline.setVisible( false );


        }
    }

   

    public void enableUnmanagedStructureMode() {
        //TODO
    }

    public void clear() {
        groupIdTextBox.setText( null );
        artifactIdTextBox.setText( null );
        versionTextBox.setText( null );
    }



    private void enableModeParams( ViewMode mode ) {


        if(mode == ViewMode.EDIT_MULTI_MODULE_PROJECT || mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT){
            groupIdTextBox.setVisible( true  );
            groupIdTextBoxHelpInline.setVisible(true );
            artifactIdTextBox.setVisible( true );
            artifactIdTextBoxHelpInline.setVisible(true );
            versionTextBox.setVisible( true );
            versionTextBoxHelpInline.setVisible(true );
        }
    }


}