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

package org.guvnor.m2repo.client.upload;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.guvnor.m2repo.client.event.M2RepoSearchEvent;
import org.guvnor.m2repo.model.HTMLFileManagerFields;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.FileUpload;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;

@Dependent
public class UploadForm
        extends PopupPanel {

    @Inject
    private Event<M2RepoSearchEvent> searchEvent;

    private Modal popup = new Modal();
    private WellForm form;

    private final TextBox hiddenGroupIdField = new TextBox();
    private final TextBox hiddenArtifactIdField = new TextBox();
    private final TextBox hiddenVersionIdField = new TextBox();

    private final FormStyleLayout hiddenFieldsPanel = new FormStyleLayout();

    @PostConstruct
    public void init() {
        popup.add( new ModalFooter( new Button( "Cancel" ) {{
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    hide();
                }
            } );
        }} ) );
        popup.setTitle( "Artifact Upload" );
        popup.add( doUploadForm() );
    }

    public void hide() {
        popup.hide();
        super.hide();
    }

    public void show() {
        popup.show();
    }

    public WellForm doUploadForm() {
        form = new WellForm();
        form.setAction( GWT.getModuleBaseURL() + "m2repo/file" );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        final FileUpload up = new FileUpload( new Command() {
            @Override
            public void execute() {
                showUploadingBusy();
                form.submit();
            }
        } );
        up.setName( HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH );

        form.addSubmitHandler( new Form.SubmitHandler() {
            @Override
            public void onSubmit( final Form.SubmitEvent event ) {
                String fileName = up.getFilename();
                if ( fileName == null || "".equals( fileName ) ) {
                    BusyPopup.close();
                    Window.alert( "Please select a file to upload" );
                    event.cancel();
                }
            }
        } );

        form.addSubmitCompleteHandler( new Form.SubmitCompleteHandler() {
            public void onSubmitComplete( final Form.SubmitCompleteEvent event ) {
                if ( "OK".equalsIgnoreCase( event.getResults() ) ) {
                    BusyPopup.close();
                    Window.alert( "Uploaded successfully" );
                    hiddenFieldsPanel.setVisible( false );
                    hiddenArtifactIdField.setText( null );
                    hiddenGroupIdField.setText( null );
                    hiddenVersionIdField.setText( null );

                    searchEvent.fire( new M2RepoSearchEvent() );

                    up.getElement().setPropertyString( "value", "" );
                    hide();
                } else if ( "NO VALID POM".equalsIgnoreCase( event.getResults() ) ) {
                    BusyPopup.close();
                    Window.alert( "The Jar does not contain a valid POM file. Please specify GAV info manually." );
                    hiddenFieldsPanel.setVisible( true );

                } else {
                    BusyPopup.close();
                    ErrorPopup.showMessage( "Upload failed:" + event.getResults() );

                    hiddenFieldsPanel.setVisible( false );
                    hiddenArtifactIdField.setText( null );
                    hiddenGroupIdField.setText( null );
                    hiddenVersionIdField.setText( null );
                    hide();
                }
            }
        } );

        HorizontalPanel fields = new HorizontalPanel();
        fields.add( up );

        hiddenGroupIdField.setName( HTMLFileManagerFields.GROUP_ID );
        hiddenGroupIdField.setText( null );
        //hiddenGroupIdField.setVisible(false);

        hiddenArtifactIdField.setName( HTMLFileManagerFields.ARTIFACT_ID );
        hiddenArtifactIdField.setText( null );
        //hiddenArtifactIdField.setVisible(false);

        hiddenVersionIdField.setName( HTMLFileManagerFields.VERSION_ID );
        hiddenVersionIdField.setText( null );
        //hiddenVersionIdField.setVisible(false);

        hiddenFieldsPanel.setVisible( false );
        hiddenFieldsPanel.addAttribute( "GroupID:", hiddenGroupIdField );
        hiddenFieldsPanel.addAttribute( "ArtifactID:", hiddenArtifactIdField );
        hiddenFieldsPanel.addAttribute( "VersionID:", hiddenVersionIdField );

        VerticalPanel allFields = new VerticalPanel();
        allFields.add( fields );
        allFields.add( hiddenFieldsPanel );

        form.add( allFields );

        return form;
    }

    protected void showUploadingBusy() {
        // LoadingPopup.showMessage( "Uploading...");
    }

}
