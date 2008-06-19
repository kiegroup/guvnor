package org.drools.guvnor.client.packages;
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
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the wizard used when creating new packages or importing them.
 *
 * @author Michael Neale
 */
public class NewPackageWizard extends FormStylePopup {

    private TextBox nameBox;
    private TextArea descBox;
    private final FormStyleLayout importLayout = new FormStyleLayout();
    private final FormStyleLayout newPackageLayout = new FormStyleLayout();


    public NewPackageWizard(final Command afterCreatedEvent) {
        super( "images/new_wiz.gif", "Create a new package" );
        nameBox = new TextBox();
        descBox = new TextArea();

        newPackageLayout.addRow( new HTML("<i><small>Create a new package in the BRMS</small></i>") );
        importLayout.addRow( new HTML("<i><small>Importing a package from an existing DRL will create the package in the BRMS if it " +
                "does not already exist. If it does exist, any new rules found will be merged into the BRMS package.</small></i>") );
        importLayout.addRow( new HTML("<i><small>Any new rules created will not have any categories assigned initially, " +
                "but rules and functions will be stored individually (ie normalised). Queries, imports etc will show up in the package configuration.</small></i>") );
        importLayout.addRow( new HTML("<i><small>Any DSLs or models required by the imported package will need to be uploaded seperately.</small></i>") );

        newPackageLayout.addAttribute( "Name:", nameBox );
        newPackageLayout.addAttribute( "Description:", descBox );

        nameBox.setTitle( "The name of the package. Avoid spaces, use underscore instead." );

        RadioButton newPackage = new RadioButton("action", "Create new package");
        RadioButton importPackage = new RadioButton("action", "Import from drl file");
        newPackage.setChecked( true );
        newPackageLayout.setVisible( true );

        newPackage.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                newPackageLayout.setVisible( true );
                importLayout.setVisible( false );
            }
        });

        importLayout.setVisible( false );

        importPackage.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                newPackageLayout.setVisible( false );
                importLayout.setVisible( true );
            }
        });
        AbsolutePanel ab = new AbsolutePanel();
        ab.add( newPackage );
        ab.add( importPackage );
        addRow( ab );

        addRow(newPackageLayout);
        addRow(importLayout);

        importLayout.addAttribute( "DRL file to import:", newImportWidget(afterCreatedEvent, this) );


        Button create = new Button("Create package");
        create.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	if ( PackageNameValidator.validatePackageName(nameBox.getText()) ) {
            		createPackageAction(nameBox.getText(), descBox.getText(), afterCreatedEvent);
            		hide();
            	} else {
            		nameBox.setText("");
            		Window.alert("Invalid package name, use java-style package name");
            	}
            }
        });

        newPackageLayout.addAttribute( "", create );




    }



    private void createPackageAction(final String name, final String descr, final Command refresh) {
        LoadingPopup.showMessage( "Creating package - please wait..." );
        RepositoryServiceFactory.getService().createPackage( name, descr, new GenericCallback() {
            public void onSuccess(Object data) {
                LoadingPopup.close();
                refresh.execute();
            }
        });
    }

    public static Widget newImportWidget(final Command afterCreatedEvent, final FormStylePopup parent) {

        final FormPanel uploadFormPanel = new FormPanel();
        uploadFormPanel.setAction( GWT.getModuleBaseURL() + "package" );
        uploadFormPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
        uploadFormPanel.setMethod( FormPanel.METHOD_POST );

        HorizontalPanel panel = new HorizontalPanel();
        uploadFormPanel.setWidget( panel );

        final FileUpload upload = new FileUpload();
        upload.setName( HTMLFileManagerFields.CLASSIC_DRL_IMPORT );
        panel.add( upload );


        panel.add(new Label("upload:"));
        ImageButton ok = new ImageButton("images/upload.gif", "Import");
        ok.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                if (Window.confirm( "Are you sure you want to import this package? If the package already exists in the BRMS it will be merged." )) {
                    LoadingPopup.showMessage( "Importing drl package, please wait, as this could take some time..." );
                    uploadFormPanel.submit();
                }
            }

        });

        panel.add( ok );

        uploadFormPanel.addFormHandler( new FormHandler() {
            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                if (event.getResults().indexOf( "OK" ) > -1) {
                    Window.alert( "Package was imported successfully. ");
                    afterCreatedEvent.execute();
                    parent.hide();
                } else {
                    ErrorPopup.showMessage( "Unable to import into the package. [" + event.getResults() + "]"  );
                }
                LoadingPopup.close();
            }

            public void onSubmit(FormSubmitEvent event) {
                if ( upload.getFilename().length() == 0 ) {
                    Window.alert( "You did not choose a drl file to import !" );
                    event.setCancelled( true );
                } else if ( !upload.getFilename().endsWith( ".drl" ) ) {
                    Window.alert( "You can only import '.drl' files." );
                    event.setCancelled( true );
                }

            }
        } );

        return uploadFormPanel;
    }


}