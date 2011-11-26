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

package org.drools.guvnor.client.moduleeditor.soa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEvent;
import org.drools.guvnor.client.moduleeditor.drools.PackageNameValidator;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

/**
 * This is the wizard used when creating new SOA service.
 */
public class NewSOAServiceWizard extends FormStylePopup {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    private TextBox nameBox;
    private TextBox descBox;
    private EventBus eventBus;

    public NewSOAServiceWizard(ClientFactory clientFactory, EventBus eventBus) {
        super(images.newexWiz(),
                "Create New SOA service");
        this.eventBus = eventBus;
        nameBox = new TextBox();
        descBox = new TextBox();
        addAttribute(constants.NameColon(),
                nameBox);
        addAttribute(constants.DescriptionColon(),
                descBox);

        nameBox.setTitle(constants.PackageNameTip());

        this.setAfterShow(new Command() {
            public void execute() {
                nameBox.setFocus(true);
            }
        });

        HorizontalPanel hp = new HorizontalPanel();
        Button create = new Button("Create SOA Service");
        create.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if (PackageNameValidator.validatePackageName(nameBox.getText())) {
                    createSOAServiceAction(nameBox.getText(),
                            descBox.getText());
                    hide();
                } else {
                    nameBox.setText("");
                    Window.alert(constants.PackageNameCorrectHint());
                }
            }
        });
        hp.add(create);

        Button cancel = new Button(constants.Cancel());
        cancel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                hide();
            }
        });
        hp.add(cancel);
        addAttribute("",
                hp);
    }

    private void createSOAServiceAction(final String name,
                                        final String descr) {
        LoadingPopup.showMessage(constants.CreatingPackagePleaseWait());
        RepositoryServiceFactory.getPackageService().createPackage(name,
                descr, "soaservice",
                new GenericCallback<java.lang.String>() {
                    public void onSuccess(String uuid) {
                        RulePackageSelector.currentlySelectedPackage = name;
                        LoadingPopup.close();
                        eventBus.fireEvent(new RefreshModuleListEvent());
                    }
                });
    }

}
