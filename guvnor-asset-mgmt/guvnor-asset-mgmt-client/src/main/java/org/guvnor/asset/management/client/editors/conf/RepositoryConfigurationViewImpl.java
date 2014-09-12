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
package org.guvnor.asset.management.client.editors.conf;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
@Templated(value = "RepositoryConfigurationViewImpl.html")
public class RepositoryConfigurationViewImpl extends Composite implements RepositoryConfigurationPresenter.RepositoryConfigurationView {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private RepositoryConfigurationPresenter presenter;

    @Inject
    @DataField
    public Label accordionLabel;

    @Inject
    @DataField
    public Label chooseRepositoryLabel;

    @Inject
    @DataField
    public ListBox chooseRepositoryBox;

    @Inject
    @DataField
    public Button configureButton;

    @Inject
    @DataField
    public Label sourceBranchLabel;

    @Inject
    @DataField
    public TextBox sourceBranchText;

    @Inject
    @DataField
    public Label releaseBranchLabel;

    @Inject
    @DataField
    public TextBox releaseBranchText;

    @Inject
    @DataField
    public Label devBranchLabel;

    @Inject
    @DataField
    public TextBox devBranchText;

    @Inject
    @DataField
    public Label versionLabel;

    @Inject
    @DataField
    public TextBox versionText;

    @Inject
    @DataField
    public Label currentVersionLabel;

    @Inject
    @DataField
    public TextBox currentVersionText;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(final RepositoryConfigurationPresenter presenter) {
        this.presenter = presenter;
        accordionLabel.setText(constants.Repository_Configuration());
        chooseRepositoryLabel.setText(constants.Choose_Repository());
        sourceBranchLabel.setText(constants.Source_Branch());
        releaseBranchLabel.setText(constants.Release_Branch());
        devBranchLabel.setText(constants.Dev_Branch());
        configureButton.setText(constants.Configure_Repository());
        versionLabel.setText(constants.Version());
        currentVersionLabel.setText(constants.Current_Version());
        sourceBranchText.setText("master");
        devBranchText.setText("dev");
        releaseBranchText.setText("release");
        currentVersionText.setReadOnly(true);
        chooseRepositoryBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                String value = chooseRepositoryBox.getValue();
                GWT.log(value);

                presenter.loadRepositoryProjectStructure(value);
            }
        });
        presenter.loadRepositories();
    }

    @EventHandler("configureButton")
    public void configureButton(ClickEvent e) {

        presenter.configureRepository(chooseRepositoryBox.getValue(), sourceBranchText.getText(), devBranchText.getText(), releaseBranchText.getText(), versionText.getText());

    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public Button getConfigureButton() {
        return configureButton;
    }

    @Override
    public TextBox getReleaseBranchText() {
        return releaseBranchText;
    }

    @Override
    public TextBox getDevBranchText() {
        return devBranchText;
    }

    @Override
    public ListBox getChooseRepositoryBox() {
        return chooseRepositoryBox;
    }

    @Override
    public TextBox getCurrentVersionText() {
        return currentVersionText;
    }

    @Override
    public TextBox getVersionText() {
        return versionText;
    }

}
