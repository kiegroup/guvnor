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

package org.guvnor.common.services.project.client;

import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.client.resources.ProjectResources;
import org.guvnor.common.services.project.model.GAV;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class POMEditorPanelViewImpl
        extends Composite
        implements POMEditorPanelView {

    private String tabTitleLabel = ProjectResources.CONSTANTS.ProjectModel();

    interface GroupArtifactVersionEditorPanelViewImplBinder
            extends
            UiBinder<Widget, POMEditorPanelViewImpl> {

    }

    private static GroupArtifactVersionEditorPanelViewImplBinder uiBinder = GWT.create(GroupArtifactVersionEditorPanelViewImplBinder.class);

    private Event<NotificationEvent> notificationEvent;

    @UiField
    TextBox pomNameTextBox;

    @UiField
    TextArea pomDescriptionTextArea;

    @UiField(provided = true)
    GAVEditor gavEditor;

    @UiField(provided = true)
    GAVEditor parentGavEditor;

    @UiField
    Fieldset parentGavEditorFieldSet;

    private Presenter presenter;

    public POMEditorPanelViewImpl() {
    }

    @Inject
    public POMEditorPanelViewImpl(Event<NotificationEvent> notificationEvent,
                                  GAVEditor parentGavEditor,
                                  GAVEditor gavEditor) {
        this.parentGavEditor = parentGavEditor;
        this.gavEditor = gavEditor;
        initWidget(uiBinder.createAndBindUi(this));
        this.notificationEvent = notificationEvent;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showSaveSuccessful(String fileName) {
        notificationEvent.fire(new NotificationEvent(ProjectResources.CONSTANTS.SaveSuccessful(fileName)));
    }

    @Override
    public String getTitleWidget() {
        return tabTitleLabel;
    }

    @Override
    public void setName(String projectName) {
        pomNameTextBox.setText(projectName);
    }

    @Override
    public void setDescription(String projectDescription) {
        pomDescriptionTextArea.setText(projectDescription);
    }

    @Override
    public void showParentGAV() {
        parentGavEditorFieldSet.setVisible(true);
    }

    @Override
    public void hideParentGAV() {
        parentGavEditorFieldSet.setVisible(false);
    }

    @Override
    public void setParentGAV(GAV gav) {
        parentGavEditor.setGAV(gav);
    }

    @Override
    public void setGAV(GAV gav) {
        gavEditor.setGAV(gav);
    }

    @Override
    public void addGroupIdChangeHandler(GroupIdChangeHandler changeHandler) {
        gavEditor.addGroupIdChangeHandler(changeHandler);
    }

    @Override
    public void addArtifactIdChangeHandler(ArtifactIdChangeHandler changeHandler) {
        gavEditor.addArtifactIdChangeHandler(changeHandler);
    }

    @Override
    public void addVersionChangeHandler(VersionChangeHandler changeHandler) {
        gavEditor.addVersionChangeHandler(changeHandler);
    }

    @Override
    public void setReadOnly() {
        gavEditor.setReadOnly();
    }

    @Override
    public void disableGroupID(String reason) {
        gavEditor.disableGroupID(reason);
    }

    @Override
    public void disableVersion(String reason) {
        gavEditor.disableVersion(reason);
    }

    @Override
    public void enableGroupID() {
        gavEditor.enableGroupID();
    }

    @Override
    public void enableVersion() {
        gavEditor.enableVersion();
    }

    @Override
    public void setTitleText(String titleText) {
        tabTitleLabel = titleText;
    }

    @Override
    public void setProjectModelTitleText() {
        tabTitleLabel = ProjectResources.CONSTANTS.ProjectModel();
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @UiHandler("pomNameTextBox")
    //Use KeyUpEvent as ValueChangeEvent is only fired when the focus is lost
    public void onNameChange(KeyUpEvent event) {
        presenter.onNameChange(pomNameTextBox.getText());
    }

    @UiHandler("openProjectContext")
    public void onOpenProjectContext(ClickEvent event) {
        presenter.onOpenProjectContext();
    }

    @UiHandler("pomDescriptionTextArea")
    public void onDescriptionChange(ValueChangeEvent<String> event) {
        presenter.onDescriptionChange(pomDescriptionTextArea.getText());
    }
}
