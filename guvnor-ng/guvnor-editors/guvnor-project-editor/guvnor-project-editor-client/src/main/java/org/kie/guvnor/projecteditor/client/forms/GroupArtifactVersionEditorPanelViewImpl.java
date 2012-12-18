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

package org.kie.guvnor.projecteditor.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class GroupArtifactVersionEditorPanelViewImpl
        extends Composite
        implements GroupArtifactVersionEditorPanelView {


    private Presenter presenter;
    private InlineLabel tabTitleLabel = new InlineLabel(ProjectEditorConstants.INSTANCE.ProjectModel());

    interface GroupArtifactVersionEditorPanelViewImplBinder
            extends
            UiBinder<Widget, GroupArtifactVersionEditorPanelViewImpl> {

    }

    private static GroupArtifactVersionEditorPanelViewImplBinder uiBinder = GWT.create(GroupArtifactVersionEditorPanelViewImplBinder.class);

    private final Event<NotificationEvent> notificationEvent;

    @UiField
    TextBox groupIdTextBox;

    @UiField
    TextBox artifactIdTextBox;

    @UiField
    TextBox versionIdTextBox;

    @Inject
    public GroupArtifactVersionEditorPanelViewImpl(Event<NotificationEvent> notificationEvent) {
        initWidget(uiBinder.createAndBindUi(this));
        this.notificationEvent = notificationEvent;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setGroupId(String id) {
        groupIdTextBox.setText(id);
    }

    @UiHandler("groupIdTextBox")
    public void onGroupIdChange(KeyUpEvent event) {
        presenter.onGroupIdChange(groupIdTextBox.getText());
    }

    @Override
    public void setArtifactId(String id) {
        artifactIdTextBox.setText(id);
    }

    @UiHandler("artifactIdTextBox")
    public void onArtifactIdChange(KeyUpEvent event) {
        presenter.onArtifactIdChange(artifactIdTextBox.getText());
    }

    @Override
    public void setVersionId(String versionId) {
        versionIdTextBox.setText(versionId);
    }

    @UiHandler("versionIdTextBox")
    public void onVersionIdChange(KeyUpEvent event) {
        presenter.onVersionIdChange(versionIdTextBox.getText());
    }

    @Override
    public void showSaveSuccessful(String fileName) {
        notificationEvent.fire(new NotificationEvent(ProjectEditorConstants.INSTANCE.SaveSuccessful(fileName)));
    }

    @Override
    public IsWidget getTitleWidget() {
        return tabTitleLabel;
    }

    @Override
    public void setTitleText(String titleText) {
        tabTitleLabel.setText(titleText);
    }
}
