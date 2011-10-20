/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.explorer.navigation.admin.widget;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BackupManager extends Composite {

    private static Images images    = (Images) GWT.create( Images.class );

    private Constants     constants = GWT.create( Constants.class );

    public BackupManager() {

        PrettyFormLayout widtab = new PrettyFormLayout();
        widtab.addHeader( images.backupLarge(),
                          new HTML( constants.ImportOrExport() ) );

        widtab.startSection( constants.ImportFromAnXmlFile() );
        widtab.addAttribute( "",
                             newImportWidget() );
        widtab.endSection();

        widtab.startSection( constants.ExportToAZipFile() );
        widtab.addAttribute( "",
                             newExportWidget() );

        widtab.endSection();

        initWidget( widtab );

    }

    private Widget newExportWidget() {
        HorizontalPanel horiz = new HorizontalPanel();

        Button create = new Button( constants.Export() );
        create.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                exportRepository();
            }
        } );

        horiz.add( create );
        return horiz;
    }

    private Widget newImportWidget() {

        final FormPanel uploadFormPanel = new FormPanel();
        uploadFormPanel.setAction( GWT.getModuleBaseURL() + "backup" );
        uploadFormPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
        uploadFormPanel.setMethod( FormPanel.METHOD_POST );

        HorizontalPanel panel = new HorizontalPanel();
        uploadFormPanel.setWidget( panel );

        final FileUpload upload = new FileUpload();
        upload.setName( HTMLFileManagerFields.FILE_UPLOAD_FIELD_NAME_IMPORT );
        panel.add( upload );

        Button ok = new Button( constants.Import() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent sender) {
                doImportFile( uploadFormPanel );
            }

            private void doImportFile(final FormPanel uploadFormPanel) {
                if ( Window.confirm( constants.ImportConfirm() ) ) {
                    LoadingPopup.showMessage( constants.ImportingInProgress() );
                    uploadFormPanel.submit();
                }
            }
        } );

        panel.add( ok );

        uploadFormPanel.addSubmitCompleteHandler( new SubmitCompleteHandler() {

            public void onSubmitComplete(SubmitCompleteEvent event) {
                if ( event.getResults().indexOf( "OK" ) > -1 ) {
                    Window.alert( constants.ImportDone() );
                    History.newItem( " " );
                    Window.Location.reload();
                } else {
                    ErrorPopup.showMessage( constants.ImportFailed() );
                }
                LoadingPopup.close();
            }
        } );

        uploadFormPanel.addSubmitHandler( new SubmitHandler() {

            public void onSubmit(SubmitEvent event) {
                String fileName = upload.getFilename();
                if ( fileName.length() == 0 ) {
                    Window.alert( constants.NoExportFilename() );
                    event.cancel();
                } else {
                    String lowerCaseFileName = fileName.toLowerCase();
                    if ( !lowerCaseFileName.endsWith( ".xml" ) && !lowerCaseFileName.endsWith( ".zip" ) ) {
                        Window.alert( constants.PleaseSpecifyAValidRepositoryXmlFile() );
                        event.cancel();
                    }
                }
            }
        } );

        return uploadFormPanel;
    }

    private void exportRepository() {

        if ( Window.confirm( constants.ExportRepoWarning() ) ) {
            LoadingPopup.showMessage( constants.ExportRepoWait() );

            Window.open( GWT.getModuleBaseURL() + "backup?" + HTMLFileManagerFields.FORM_FIELD_REPOSITORY + "=true",
                         "downloading",
                         "resizable=no,scrollbars=yes,status=no" );

            LoadingPopup.close();
        }
    }

}
