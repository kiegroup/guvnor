package org.drools.guvnor.client.admin;

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

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.RulePackageSelector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Fernando Meyer
 */
public class BackupManager extends Composite {

    public BackupManager() {

        PrettyFormLayout widtab = new PrettyFormLayout();
        widtab.addHeader( "images/backup_large.png",
                          new HTML( "<b>Import/Export</b>" ) );

        widtab.startSection( "Import from an xml file" );
        widtab.addAttribute( "",
                             newImportWidget() );
        widtab.endSection();

        widtab.startSection( "Export to a zip file" );
        widtab.addAttribute( "",
                             newExportWidget() );

        widtab.endSection();

        /*
         * Package import/export
         */
        widtab.startSection( "Import package from an xml file" );
        CheckBox overWriteCheckBox = new CheckBox();
        widtab.addAttribute( "Overwrite existing package",
                             overWriteCheckBox );
        widtab.addAttribute( "",
                             newImportPackageWidget( overWriteCheckBox ) );
        widtab.endSection();

        widtab.startSection( "Export package to a zip file" );
        final RulePackageSelector rps = new RulePackageSelector();
        widtab.addAttribute( "Package name",
                             rps );
        widtab.addAttribute( "",
                             newExportPackageWidget( rps ) );

        widtab.endSection();

        initWidget( widtab );

    }

    private Widget newExportWidget() {
        HorizontalPanel horiz = new HorizontalPanel();

        Button create = new Button( "Export" );
        create.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                exportRepository();
            }
        } );

        horiz.add( create );
        return horiz;
    }

    private Widget newExportPackageWidget(final RulePackageSelector box) {
        final HorizontalPanel horiz = new HorizontalPanel();

        final Button create = new Button( "Export" );
        create.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                exportPackageFromRepository( box.getSelectedPackage() );
            }
        } );

        horiz.add( create );
        return horiz;
    }

    //    private Widget cleanRepository() {
    //        HorizontalPanel horiz = new HorizontalPanel();
    //
    //        Button delete = new Button( "Execute" );
    //        delete.addClickListener( new ClickListener() {
    //            public void onClick(Widget w) {
    //                if ( Window.confirm( "Are you REALLY REALLY sure you want to erase you repository contents?" ) ) {
    //                    RepositoryServiceFactory.getService().clearRulesRepository( new GenericCallback() {
    //                        public void onSuccess(Object data) {
    //                            Window.alert( "Rules repository deleted." );
    //                        }
    //                    });
    //                } else {
    //                    Window.alert( "Operation cancelled" );
    //                }
    //            }
    //        } );
    //
    //        horiz.add( delete );
    //        return horiz;
    //    }

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

        panel.add( new Label( "import:" ) );
        ImageButton ok = new ImageButton( "images/upload.gif" );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget sender) {
                doImportFile( uploadFormPanel );
            }

            private void doImportFile(final FormPanel uploadFormPanel) {
                if ( Window.confirm( "Are you sure you want to import? This will erase any content in the " + "repository currently?" ) ) {
                    LoadingPopup.showMessage( "Importing repository, please wait, as this could take some time..." );
                    uploadFormPanel.submit();
                }
            }
        } );

        panel.add( ok );

        uploadFormPanel.addFormHandler( new FormHandler() {
            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                if ( event.getResults().indexOf( "OK" ) > -1 ) {
                    Window.alert( "Rules repository imported successfully. Please refresh your browser (F5) to show the new content. " );
                } else {
                    ErrorPopup.showMessage( "Unable to import into the repository. Consult the server logs for error messages." );
                }
                LoadingPopup.close();
            }

            public void onSubmit(FormSubmitEvent event) {
                if ( upload.getFilename().length() == 0 ) {
                    Window.alert( "You did not specify an exported repository filename !" );
                    event.setCancelled( true );
                } else if ( !upload.getFilename().endsWith( ".xml" ) ) {
                    Window.alert( "Please specify a valid repository xml file." );
                    event.setCancelled( true );
                }

            }
        } );

        return uploadFormPanel;
    }

    private Widget newImportPackageWidget(final CheckBox overWriteCheckBox) {

        final FormPanel uploadFormPanel = new FormPanel();
        uploadFormPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
        uploadFormPanel.setMethod( FormPanel.METHOD_POST );

        HorizontalPanel panel = new HorizontalPanel();
        uploadFormPanel.setWidget( panel );

        final FileUpload upload = new FileUpload();
        upload.setName( HTMLFileManagerFields.FILE_UPLOAD_FIELD_NAME_IMPORT );
        panel.add( upload );

        panel.add( new Label( "import:" ) );
        ImageButton ok = new ImageButton( "images/upload.gif" );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget sender) {
                uploadFormPanel.setAction( GWT.getModuleBaseURL() + "backup?packageImport=true&importAsNew=" + !overWriteCheckBox.isChecked() );
                doImportFile( uploadFormPanel );
            }

            private void doImportFile(final FormPanel uploadFormPanel) {
                if ( (overWriteCheckBox.isChecked() && Window.confirm( "Are you sure you want to import? This will erase any content in the package currently?" )) || !overWriteCheckBox.isChecked() ) {
                    LoadingPopup.showMessage( "Importing package to repository, please wait, as this could take some time..." );
                    uploadFormPanel.submit();
                }
            }
        } );

        panel.add( ok );

        uploadFormPanel.addFormHandler( new FormHandler() {
            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                if ( event.getResults().indexOf( "OK" ) > -1 ) {
                    Window.alert( "Rules package imported successfully. Please refresh your browser (F5) to show the new content. " );
                } else {
                    ErrorPopup.showMessage( "Unable to import into the repository. Consult the server logs for error messages." );
                }
                LoadingPopup.close();
            }

            public void onSubmit(FormSubmitEvent event) {
                if ( upload.getFilename().length() == 0 ) {
                    Window.alert( "You did not specify an exported repository package filename !" );
                    event.setCancelled( true );
                } else if ( !upload.getFilename().endsWith( ".xml" ) ) {
                    Window.alert( "Please specify a valid repository package xml file." );
                    event.setCancelled( true );
                }

            }
        } );

        return uploadFormPanel;
    }

    private void exportRepository() {

        if ( Window.confirm( "Export the repository? This may take some time." ) ) {
            LoadingPopup.showMessage( "Exporting repository, please wait, as this could take some time..." );

            Window.open( GWT.getModuleBaseURL() + "backup?" + HTMLFileManagerFields.FORM_FIELD_REPOSITORY + "=true",
                         "downloading",
                         "resizable=no,scrollbars=yes,status=no" );

            LoadingPopup.close();
        }
    }

    private void exportPackageFromRepository(String packageName) {

        if ( Window.confirm( "Export the package?" ) ) {
            LoadingPopup.showMessage( "Exporting package, please wait, as this could take some time..." );

            Window.open( GWT.getModuleBaseURL() + "backup?" + HTMLFileManagerFields.FORM_FIELD_REPOSITORY + "=true&packageName=true&" + packageName,
                         "downloading",
                         "resizable=no,scrollbars=yes,status=no" );

            LoadingPopup.close();
        }
    }

}