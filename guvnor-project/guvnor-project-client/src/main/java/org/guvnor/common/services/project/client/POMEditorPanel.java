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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.POM;
import org.uberfire.client.mvp.PlaceManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;

@Dependent
public class POMEditorPanel
        implements POMEditorPanelView.Presenter,
        IsWidget {

    private ArrayList<NameChangeHandler> nameChangeHandlers = new ArrayList<NameChangeHandler>();
    private POMEditorPanelView view;
    private PlaceManager placeManager;
    private POM model;

    public POMEditorPanel() {
    }

    @Inject
    public POMEditorPanel(final POMEditorPanelView view,
                          final PlaceManager placeManager) {
        this.view = view;
        this.placeManager = placeManager;
        view.setPresenter(this);
    }

    public void setPOM(POM model,
                       boolean isReadOnly) {
        if (isReadOnly) {
            view.setReadOnly();
        }

        this.model = model;

        view.setName(model.getName());
        view.setDescription(model.getDescription());
        if (model.hasParent()) {
            view.setParentGAV(model.getParent());
            view.showParentGAV();
            view.disableGroupID("");
            view.disableVersion("");
        } else {
            view.hideParentGAV();
            view.enableGroupID();
            view.enableVersion();
        }
        view.setGAV(model.getGav());
        view.addArtifactIdChangeHandler(new ArtifactIdChangeHandler() {
            @Override
            public void onChange(String newArtifactId) {
                setTitle(newArtifactId);
            }
        });
        setTitle(model.getGav().getArtifactId());
    }

    private void setTitle(final String titleText) {
        if (titleText == null || titleText.isEmpty()) {
            view.setProjectModelTitleText();
        } else {
            view.setTitleText(titleText);
        }
    }

    @Override
    public void addNameChangeHandler(NameChangeHandler changeHandler) {
        nameChangeHandlers.add(changeHandler);
    }

    @Override
    public void addGroupIdChangeHandler(GroupIdChangeHandler changeHandler) {
        this.view.addGroupIdChangeHandler(changeHandler);
    }

    @Override
    public void addArtifactIdChangeHandler(ArtifactIdChangeHandler changeHandler) {
        this.view.addArtifactIdChangeHandler(changeHandler);
    }

    @Override
    public void addVersionChangeHandler(VersionChangeHandler changeHandler) {
        this.view.addVersionChangeHandler(changeHandler);
    }

    @Override
    public void onNameChange(String name) {
        this.model.setName(name);
        for (NameChangeHandler changeHandler : nameChangeHandlers) {
            changeHandler.onChange(name);
        }
    }

    @Override
    public void onDescriptionChange(String description) {
        this.model.setDescription(description);
    }

    @Override
    public void onOpenProjectContext() {
        placeManager.goTo("repositoryStructureScreen");
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public boolean isDirty() {
        return false;
    }

    public void disableGroupID(String reason) {
        view.disableGroupID(reason);
    }

    public void disableVersion(String reason) {
        view.disableVersion(reason);
    }

    public POM getPom() {
        return model;
    }
}