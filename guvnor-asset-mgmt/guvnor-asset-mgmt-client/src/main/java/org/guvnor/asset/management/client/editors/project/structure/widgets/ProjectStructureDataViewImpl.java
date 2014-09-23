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

public class ProjectStructureDataViewImpl extends Composite
        implements ProjectStructureDataView {

    interface NewProjectStructureDataViewImplUIBinder
            extends UiBinder<Widget, ProjectStructureDataViewImpl> {

    }

    private static NewProjectStructureDataViewImplUIBinder uiBinder = GWT.create( NewProjectStructureDataViewImplUIBinder.class );

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
    Button initProjectStructureButton;

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

    private Presenter presenter;

    private ViewMode mode;

    public ProjectStructureDataViewImpl() {
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

            projectTypeLabel.setText( Constants.INSTANCE.Project_structure_view_create_projectTypeLabel() );

            isSingleModuleRadioButton.setText( Constants.INSTANCE.Project_structure_view_create_isSingleModuleRadioButton() );
            isSingleModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Project_structure_view_create_isSingleModuleRadioButtonHelpInline() );

            isMultiModuleRadioButton.setText( Constants.INSTANCE.Project_structure_view_create_isMultiModuleRadioButton() );
            isMultiModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Project_structure_view_create_isMultiModuleRadioButtonHelpInline() );

            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_create_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_create_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_create_versionTextBoxHelpInline() );

            initProjectStructureButton.setText( Constants.INSTANCE.InitProjectStructure() );
            initProjectStructureButton.setVisible( true );

            enableSingleModeParams();
            setCurrentSingleProjectInfoVisible( false );

        } else if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {

            projectTypeLabel.setText( Constants.INSTANCE.Project_structure_view_edit_single_projectTypeLabel() );

            isSingleModuleRadioButton.setText( Constants.INSTANCE.Project_structure_view_edit_single_isSingleModuleRadioButton() );
            isSingleModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_single_isSingleModuleRadioButtonHelpInline() );

            isMultiModuleRadioButton.setText( Constants.INSTANCE.Project_structure_view_edit_single_isMultiModuleRadioButton() );
            isMultiModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_single_isMultiModuleRadioButtonHelpInline() );
            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_single_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_single_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_single_versionTextBoxHelpInline() );

            enableSingleModeParams();

            initProjectStructureButton.setText( Constants.INSTANCE.EditProject() );
            initProjectStructureButton.setVisible( true );

            setCurrentSingleProjectInfoVisible( true );

        } else if ( mode == ViewMode.EDIT_MULTI_MODULE_PROJECT ) {

            projectTypeLabel.setText( Constants.INSTANCE.Project_structure_view_edit_multi_projectTypeLabel() );

            enableMultiModeParams();

            isSingleModuleRadioButton.setVisible( false );
            isSingleModuleRadioButtonHelpInline.setVisible( false );

            isMultiModuleRadioButton.setText( Constants.INSTANCE.Project_structure_view_edit_multi_isMultiModuleRadioButton() );
            isMultiModuleRadioButtonHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_multi_isMultiModuleRadioButtonHelpInline() );
            groupIdTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_multi_groupIdTextBoxHelpInline() );
            artifactIdTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_multi_artifactIdTextBoxHelpInline() );
            versionTextBoxHelpInline.setText( Constants.INSTANCE.Project_structure_view_edit_multi_versionTextBoxHelpInline() );

            initProjectStructureButton.setText( Constants.INSTANCE.SaveChanges() );

            setCurrentSingleProjectInfoVisible( false );

        } else if ( mode == ViewMode.EDIT_UNMANAGED_REPOSITORY ) {

            projectTypeLabel.setText( Constants.INSTANCE.Project_structure_view_edit_unmanaged_projectTypeLabel() );

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

            initProjectStructureButton.setVisible( false );
        }
    }

    public void enableMultiModeParams() {
        enableModeParams( false );
    }

    public void enableSingleModeParams() {
        enableModeParams( true );
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
        initProjectStructureButton.setEnabled( value );
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

    private void enableModeParams( boolean isSingle ) {

        //single mode fields
        isSingleModuleRadioButton.setVisible( true );
        isSingleModuleRadioButtonHelpInline.setVisible( true );
        isSingleModuleRadioButton.setValue( isSingle );

        //multi mode fields.
        isMultiModuleRadioButton.setVisible( true );
        isMultiModuleRadioButtonHelpInline.setVisible( true );
        isMultiModuleRadioButton.setValue( !isSingle );

        groupIdTextBox.setVisible( !isSingle );
        groupIdTextBoxHelpInline.setVisible( !isSingle );
        artifactIdTextBox.setVisible( !isSingle );
        artifactIdTextBoxHelpInline.setVisible( !isSingle );
        versionTextBox.setVisible( !isSingle );
        versionTextBoxHelpInline.setVisible( !isSingle );
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

    @UiHandler( "initProjectStructureButton" )
    void onInitProjectStructureClick( final ClickEvent e ) {
        if ( mode == ViewMode.CREATE_STRUCTURE ) {
            presenter.onInitProjectStructure();
        } else if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {
            if ( isSingleModuleRadioButton.getValue() ) {
                presenter.onOpenSingleProject();
            } else {
                presenter.onConvertToMultiModule();
            }
        } else {
            presenter.onSaveProjectStructure();
        }
    }

    @UiHandler( "isMultiModuleRadioButton" )
    void multiModuleCheckBoxClicked( final ClickEvent event ) {
        presenter.onProjectModeChange( false );
        enableMultiModeParams();
        if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {
            initProjectStructureButton.setVisible( true );
            initProjectStructureButton.setText( Constants.INSTANCE.ConvertToMultiModule() );

            groupIdTextBox.setText( singleProjectGroupIdTextBox.getText() );
            versionTextBox.setText( singleProjectVersionTextBox.getText() );
        }
    }

    @UiHandler( "isSingleModuleRadioButton" )
    void singleModuleCheckBoxClicked( final ClickEvent event ) {
        presenter.onProjectModeChange( true );
        enableSingleModeParams();
        if ( mode == ViewMode.EDIT_SINGLE_MODULE_PROJECT ) {
            initProjectStructureButton.setVisible( true );
            initProjectStructureButton.setText( Constants.INSTANCE.EditProject() );
        }
    }
}