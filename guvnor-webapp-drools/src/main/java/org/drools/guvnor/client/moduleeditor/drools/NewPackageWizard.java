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

package org.drools.guvnor.client.moduleeditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.ModuleNameValidator;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEvent;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;

/**
 * This is the wizard used when creating new packages or importing them.
 */
public class NewPackageWizard extends FormStylePopup {

    private TextBox nameBox;
    private TextBox descBox;
    private final FormStyleLayout importLayout = new FormStyleLayout();
    private final FormStyleLayout newPackageLayout = new FormStyleLayout();
    private final EventBus eventBus;
    private final ClientFactory clientFactory;

    public NewPackageWizard(ClientFactory clientFactory, EventBus eventBus) {
        super(getImage(),
                Constants.INSTANCE.CreateANewPackage());
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        nameBox = new TextBox();
        descBox = new TextBox();

        newPackageLayout.addAttribute(Constants.INSTANCE.NameColon(),
                nameBox);
        newPackageLayout.addAttribute(Constants.INSTANCE.DescriptionColon(),
                descBox);

        nameBox.setTitle(Constants.INSTANCE.PackageNameTip());

        RadioButton newPackage = new RadioButton("action",
                Constants.INSTANCE.CreateNewPackageRadio());
        RadioButton importPackage = new RadioButton("action",
                Constants.INSTANCE.ImportFromDrlRadio());

        newPackage.setValue(true);
        newPackageLayout.setVisible(true);

        newPackage.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                newPackageLayout.setVisible(true);
                importLayout.setVisible(false);
            }
        });

        this.setAfterShow(new Command() {
            public void execute() {
                nameBox.setFocus(true);
            }
        });

        importLayout.setVisible(false);
        importPackage.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                newPackageLayout.setVisible(false);
                importLayout.setVisible(true);
            }
        });

        VerticalPanel ab = new VerticalPanel();
        ab.add(newPackage);
        ab.add(importPackage);
        addAttribute("",
                ab);

        addRow(newPackageLayout);
        addRow(importLayout);

        importLayout.addAttribute(Constants.INSTANCE.DRLFileToImport(),
                newImportWidget(this));

        importLayout.addRow(new HTML("<br/><b>" + Constants.INSTANCE.NoteNewPackageDrlImportWarning() + "</b>"));
        importLayout.addRow(new HTML(Constants.INSTANCE.ImportDRLDesc1()));
        importLayout.addRow(new HTML(Constants.INSTANCE.ImportDRLDesc2()));
        importLayout.addRow(new HTML(Constants.INSTANCE.ImportDRLDesc3()));

        HorizontalPanel hp = new HorizontalPanel();
        Button create = new Button(Constants.INSTANCE.CreatePackage());
        create.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if (ModuleNameValidator.validatePackageName(nameBox.getText())) {
                    createPackageAction(nameBox.getText(),
                            descBox.getText());
                    hide();
                } else {
                    nameBox.setText("");
                    Window.alert(Constants.INSTANCE.PackageNameCorrectHint());
                }
            }
        });
        hp.add(create);

        Button cancel = new Button(Constants.INSTANCE.Cancel());
        cancel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                hide();
                LoadingPopup.close();
            }
        });
        hp.add(cancel);

        newPackageLayout.addAttribute("",
                hp);
        
        setAfterCloseEvent(new Command() {
            @Override
            public void execute() {
                LoadingPopup.close();                
            }});
    }

    private static Image getImage() {
        Image image = new Image(DroolsGuvnorImages.INSTANCE.newexWiz());
        image.setAltText(Constants.INSTANCE.Wizard());
        return image;
    }

    private void createPackageAction(final String name,
                                     final String descr) {
        LoadingPopup.showMessage(Constants.INSTANCE.CreatingPackagePleaseWait());
        clientFactory.getModuleService().createModule(name,
                                    descr, "package",
                                    new GenericCallback<java.lang.String>() {
                                        public void onSuccess(String uuid) {
                                            RulePackageSelector.currentlySelectedPackage = name;
                                            LoadingPopup.close();
                                            eventBus.fireEvent(new RefreshModuleListEvent());
                                        }
                                    });
    }

    private Widget newImportWidget(final FormStylePopup parent) {

        final FormPanel uploadFormPanel = new FormPanel();
        uploadFormPanel.setAction(GWT.getModuleBaseURL() + "package");
        uploadFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadFormPanel.setMethod(FormPanel.METHOD_POST);

        VerticalPanel panel = new VerticalPanel();
        uploadFormPanel.setWidget(panel);

        final FileUpload upload = new FileUpload();
        upload.setName(HTMLFileManagerFields.CLASSIC_DRL_IMPORT);
        panel.add(upload);


        HorizontalPanel hp = new HorizontalPanel();
        Button create = new Button(Constants.INSTANCE.Import());
        ClickHandler okClickHandler = new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if (Window.confirm(Constants.INSTANCE.ImportMergeWarning())) {
                    uploadFormPanel.submit();
                }
            }
        };
        create.addClickHandler(okClickHandler);
        hp.add(create);

        Button cancel = new Button(Constants.INSTANCE.Cancel());
        cancel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                parent.hide();
                LoadingPopup.close();
            }
        });
        hp.add(cancel);
        panel.add(hp);


        Image image = new Image(DroolsGuvnorImages.INSTANCE.packageLarge());
        image.setAltText(Constants.INSTANCE.Package());
        final FormStylePopup packageNamePopup = new FormStylePopup(image,
                Constants.INSTANCE.PackageName());
        HorizontalPanel packageNamePanel = new HorizontalPanel();
        packageNamePopup.addRow(new Label(Constants.INSTANCE.ImportedDRLContainsNoNameForThePackage()));

        final TextBox packageName = new TextBox();
        packageNamePanel.add(new Label(Constants.INSTANCE.PackageName() + ":"));
        packageNamePanel.add(packageName);
        Button uploadWithNameButton = new Button(Constants.INSTANCE.OK());
        uploadWithNameButton.addClickHandler(okClickHandler);

        packageNamePanel.add(uploadWithNameButton);
        packageNamePopup.addRow(packageNamePanel);
        uploadFormPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
            public void onSubmitComplete(SubmitCompleteEvent event) {
                if (event.getResults().indexOf("OK") > -1) { //NON-NLS
                    LoadingPopup.close();
                    Window.alert(Constants.INSTANCE.PackageWasImportedSuccessfully());

                    eventBus.fireEvent(new RefreshModuleListEvent());
                    parent.hide();
                    if (packageNamePopup != null) {
                        packageNamePopup.hide();
                    }
                } else if (event.getResults().indexOf("Missing package name.") > -1) {
                    LoadingPopup.close();
                    packageNamePopup.show();
                } else {
                    ErrorPopup.showMessage(Constants.INSTANCE.UnableToImportIntoThePackage0(event.getResults()));
                }
                LoadingPopup.close();
            }
        });

        uploadFormPanel.addSubmitHandler(new SubmitHandler() {
            public void onSubmit(SubmitEvent event) {
                if (upload.getFilename().length() == 0) {
                    LoadingPopup.close();
                    Window.alert(Constants.INSTANCE.YouDidNotChooseADrlFileToImport());
                    event.cancel();
                } else if (!upload.getFilename().endsWith(".drl")) { //NON-NLS
                    LoadingPopup.close();
                    Window.alert(Constants.INSTANCE.YouCanOnlyImportDrlFiles());
                    event.cancel();
                } else if (packageName.getText() != null && !packageName.getText().equals("")) {
                    LoadingPopup.showMessage(Constants.INSTANCE.ImportingDRLPleaseWait(), true);
                    uploadFormPanel.setAction(uploadFormPanel.getAction() + "?packageName=" + packageName.getText());
                } else {
                    LoadingPopup.showMessage(Constants.INSTANCE.CreatingPackagePleaseWait(), true);
                }
            }
        });

        return uploadFormPanel;
    }

}
