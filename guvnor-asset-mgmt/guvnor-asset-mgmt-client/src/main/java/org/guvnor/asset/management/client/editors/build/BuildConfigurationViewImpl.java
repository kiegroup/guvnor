/*
 * Copyright 2012 JBoss Inc
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
package org.guvnor.asset.management.client.editors.build;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.guvnor.asset.management.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "BuildConfigurationViewImpl.html")
public class BuildConfigurationViewImpl extends Composite implements BuildConfigurationPresenter.BuildConfigurationView {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private BuildConfigurationPresenter presenter;

    @Inject
    @DataField
    public Label accordionLabel;
    
    @Inject
    @DataField
    public Label chooseRepositoryLabel;
    
//    @Inject
//    @DataField
//    public ListBox chooseRepositoryBox;
    
    @Inject
    @DataField
    public TextBox chooseRepositoryBox;
    
    @Inject
    @DataField
    public Label chooseBranchLabel;
    
//    @Inject
//    @DataField
//    public ListBox chooseBranchBox;
    
    @Inject
    @DataField
    public TextBox chooseBranchBox;
    
    @Inject
    @DataField
    public Label chooseProjectLabel;
    
//    @Inject
//    @DataField
//    public ListBox chooseProjectBox;
    
    @Inject
    @DataField
    public TextBox chooseProjectBox;
    
    @Inject
    @DataField
    public Button buildButton;

    @Inject
    @DataField
    public Label userNameLabel;
    
    @Inject
    @DataField
    public TextBox userNameText;
    
    @Inject
    @DataField
    public Label passwordLabel;
    
    @Inject
    @DataField
    public TextBox passwordText;
    
    @Inject
    @DataField
    public Label serverURLLabel;
    
    @Inject
    @DataField
    public TextBox serverURLText;
    
    
    @Inject
    @DataField
    public Label deployToRuntimeLabel;
    
    @Inject
    @DataField
    public CheckBox deployToRuntimeCheck;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(BuildConfigurationPresenter presenter) {
        this.presenter = presenter;
        accordionLabel.setText(constants.Build_Configuration());
        chooseRepositoryLabel.setText(constants.Choose_Repository());
        chooseBranchLabel.setText(constants.Choose_Branch());
        chooseProjectLabel.setText(constants.Choose_Project());
        userNameLabel.setText(constants.User_Name());
        passwordLabel.setText(constants.Password());
        serverURLLabel.setText(constants.Server_URL());
        deployToRuntimeLabel.setText(constants.Deploy_To_Runtime());
        buildButton.setText(constants.Build_Project());
    }

    

    @EventHandler("buildButton")
    public void buildButton(ClickEvent e) {
        presenter.buildProject(chooseRepositoryBox.getText(), chooseBranchBox.getText(), chooseProjectBox.getText(),
                            userNameText.getText(), passwordText.getText(), serverURLText.getText(), deployToRuntimeCheck.getValue());
       
    }

   

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

//    public ListBox getChooseRepositoryBox() {
//        return chooseRepositoryBox;
//    }

   
    @Override
    public TextBox getChooseRepositoryBox() {
        return chooseRepositoryBox;
    }

    public Button getBuildButton() {
        return buildButton;
    }

    
   

}
