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



import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.util.Format;

/**
 * This is the wizard used when creating new packages or importing them.
 *
 * @author Michael Neale
 */
public class NewPackageWizard extends FormStylePopup {

    private TextBox nameBox;
    private TextBox descBox;
    private final FormStyleLayout importLayout = new FormStyleLayout();
    private final FormStyleLayout newPackageLayout = new FormStyleLayout();
    private static Constants constants = ((Constants) GWT.create(Constants.class));


    public NewPackageWizard(final Command afterCreatedEvent) {
        super( "images/new_wiz.gif", constants.CreateANewPackage());  //NON-NLS
        nameBox = new TextBox();
        descBox = new TextBox();

        //newPackageLayout.addRow( new HTML(constants.CreateNewPackage()) );


        newPackageLayout.addAttribute(constants.NameColon(), nameBox );
        newPackageLayout.addAttribute(constants.DescriptionColon(), descBox );



        nameBox.setTitle(constants.PackageNameTip());

        RadioButton newPackage = new RadioButton("action", constants.CreateNewPackageRadio());     //NON-NLS
        RadioButton importPackage = new RadioButton("action", constants.ImportFromDrlRadio());     //NON-NLS
        newPackage.setChecked( true );
        newPackageLayout.setVisible( true );

        newPackage.addClickListener( new ClickListener() {        
            public void onClick(Widget w) {
                newPackageLayout.setVisible( true );
                importLayout.setVisible( false );
            }
        });

        this.setAfterShow(new Command() {
           public void execute() {
            nameBox.setFocus(true);
            }
        } );

        importLayout.setVisible( false );

        importPackage.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                newPackageLayout.setVisible( false );
                importLayout.setVisible( true );
            }
        });
        VerticalPanel ab = new VerticalPanel();
        ab.add( newPackage );
        ab.add( importPackage );
        addAttribute("",  ab );

        addRow(newPackageLayout);
        addRow(importLayout);

        importLayout.addAttribute(constants.DRLFileToImport(), newImportWidget(afterCreatedEvent, this) );

        importLayout.addRow(new HTML("<br/><b>" + constants.NoteNewPackageDrlImportWarning() + "</b>"));
        importLayout.addRow( new HTML(constants.ImportDRLDesc1()) );
        importLayout.addRow( new HTML(constants.ImportDRLDesc2()) );
        importLayout.addRow( new HTML(constants.ImportDRLDesc3()) );


        Button create = new Button(constants.CreatePackage());
        create.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	if ( PackageNameValidator.validatePackageName(nameBox.getText()) ) {
            		createPackageAction(nameBox.getText(), descBox.getText(), afterCreatedEvent);
            		hide();
            	} else {
            		nameBox.setText("");
            		Window.alert(constants.PackageNameCorrectHint());
            	}
            }
        });

        newPackageLayout.addAttribute( "", create );




    }





    private void createPackageAction(final String name, final String descr, final Command refresh) {
        LoadingPopup.showMessage(constants.CreatingPackagePleaseWait());
        RepositoryServiceFactory.getService().createPackage( name, descr, new GenericCallback() {
            public void onSuccess(Object data) {
                RulePackageSelector.currentlySelectedPackage = name;
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


        panel.add(new Label(constants.upload()));
        ImageButton ok = new ImageButton("images/upload.gif", constants.Import()); //NON-NLS
        ok.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                if (Window.confirm(constants.ImportMergeWarning())) {
                    LoadingPopup.showMessage(constants.ImportingDRLPleaseWait());
                    uploadFormPanel.submit();
                }
            }

        });

        panel.add( ok );

        uploadFormPanel.addFormHandler( new FormHandler() {
            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                if (event.getResults().indexOf( "OK" ) > -1) {  //NON-NLS
                    Window.alert(constants.PackageWasImportedSuccessfully());
                    afterCreatedEvent.execute();
                    parent.hide();
                } else {
                    ErrorPopup.showMessage(Format.format(constants.UnableToImportIntoThePackage0(), event.getResults()));
                }
                LoadingPopup.close();
            }

            public void onSubmit(FormSubmitEvent event) {
                if ( upload.getFilename().length() == 0 ) {
                    Window.alert(constants.YouDidNotChooseADrlFileToImport());
                    event.setCancelled( true );
                } else if ( !upload.getFilename().endsWith( ".drl" ) ) { //NON-NLS
                    Window.alert(constants.YouCanOnlyImportDrlFiles());
                    event.setCancelled( true );
                }

            }
        } );

        return uploadFormPanel;
    }


}