/*
 * Copyright 2015 JBoss Inc
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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.guvnor.m2repo.model.HTMLFileManagerFields;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FormStyleLayout;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;

public class UploadFormViewImpl
        extends BaseModal implements UploadFormView {

    private WellForm form;

    private final TextBox hiddenGroupIdField = new TextBox();
    private final TextBox hiddenArtifactIdField = new TextBox();
    private final TextBox hiddenVersionIdField = new TextBox();

    private Presenter presenter;

    private final FormStyleLayout hiddenFieldsPanel = new FormStyleLayout();

    private FileUpload uploader;

    public UploadFormViewImpl() {
        this.add(new ModalFooter(new Button(M2RepoEditorConstants.INSTANCE.Cancel()) {{
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
        }}));
        this.setTitle(M2RepoEditorConstants.INSTANCE.ArtifactUpload());
        this.add(doUploadForm());
    }

    private WellForm doUploadForm() {
        form = new WellForm();
        form.setAction( getWebContext() + "/maven2wb" );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        form.addSubmitHandler(new Form.SubmitHandler() {
            @Override
            public void onSubmit(final Form.SubmitEvent event) {
                presenter.handleSubmit(event);
            }
        });

        uploader = new FileUpload(new Command() {
            @Override
            public void execute() {
                form.submit();
            }
        });

        uploader.setName(HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH);

        form.addSubmitCompleteHandler(new Form.SubmitCompleteHandler() {
            public void onSubmitComplete(final Form.SubmitCompleteEvent event) {
                presenter.handleSubmitComplete(event);
            }
        });

        HorizontalPanel fields = new HorizontalPanel();
        fields.add(uploader);

        hiddenGroupIdField.setName( HTMLFileManagerFields.GROUP_ID );
        hiddenArtifactIdField.setName( HTMLFileManagerFields.ARTIFACT_ID );
        hiddenVersionIdField.setName( HTMLFileManagerFields.VERSION_ID );
        hideGAVInputs();

        hiddenFieldsPanel.addAttribute( "GroupID:", hiddenGroupIdField );
        hiddenFieldsPanel.addAttribute( "ArtifactID:", hiddenArtifactIdField );
        hiddenFieldsPanel.addAttribute( "VersionID:", hiddenVersionIdField );

        VerticalPanel allFields = new VerticalPanel();
        allFields.add( fields );
        allFields.add( hiddenFieldsPanel );

        form.add( allFields );

        return form;
    }

    private String getWebContext() {
        String context = GWT.getModuleBaseURL().replace( GWT.getModuleName() + "/", "" );
        if ( context.endsWith( "/" ) ) {
            context = context.substring( 0, context.length() - 1 );
        }
        return context;
    }

    public void showUploadingBusy() {
        BusyPopup.showMessage(M2RepoEditorConstants.INSTANCE.Uploading());
    }

    public void hideUploadingBusy() {
        BusyPopup.close();
    }

    private void showMessage(String message) {
        Window.alert(message);
    }

    public void showErrorMessage(String message) {
        ErrorPopup.showMessage(message);
    }

    @Override
    public void showSelectFileUploadWarning() {
        showMessage(M2RepoEditorConstants.INSTANCE.SelectFileUpload());
    }

    @Override
    public void showUnsupportedFileTypeWarning() {
        showMessage(M2RepoEditorConstants.INSTANCE.UnsupportedFileType());
    }

    @Override
    public void showUploadedSuccessfullyMessage() {
        showMessage(M2RepoEditorConstants.INSTANCE.UploadedSuccessfully());
    }

    @Override
    public void showInvalidJarNoPomWarning() {
        showMessage(M2RepoEditorConstants.INSTANCE.InvalidJarNotPom());
    }

    @Override
    public void showInvalidPomWarning() {
        showMessage(M2RepoEditorConstants.INSTANCE.InvalidPom());
    }

    @Override
    public void showUploadFailedError(String message) {
        showErrorMessage(M2RepoEditorConstants.INSTANCE.UploadFailed() + message);
    }

    @Override
    public void showGAVInputs() {
        hiddenFieldsPanel.setVisible(true);
    }

    @Override
    public void hideGAVInputs() {
        hiddenFieldsPanel.setVisible( false );
        hiddenArtifactIdField.setText( null );
        hiddenGroupIdField.setText( null );
        hiddenVersionIdField.setText( null );
    }

    @Override
    public String getFileName() {
        return uploader.getFilename();
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }
}
