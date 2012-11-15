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

package org.kie.projecteditor.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import javax.inject.Inject;

public class ProjectEditorScreenViewImpl
        extends Composite
        implements ProjectEditorScreenView {


    private Presenter presenter;

    interface ProjectModelScreenViewImplBinder
            extends
            UiBinder<Widget, ProjectEditorScreenViewImpl> {

    }

    private static ProjectModelScreenViewImplBinder uiBinder = GWT.create(ProjectModelScreenViewImplBinder.class);

    @UiField(provided = true)
    KnowledgeBaseConfigurationForm form;

    @UiField
    ListBox kbaseList;

    @Inject
    public ProjectEditorScreenViewImpl(KnowledgeBaseConfigurationForm form) {
        this.form = form;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addKnowledgeBaseConfiguration(String kbaseName) {
        kbaseList.addItem(kbaseName);
    }

    @UiHandler("kbaseList")
    public void handleChange(ChangeEvent event) {
        presenter.onKBaseSelection(kbaseList.getValue(kbaseList.getSelectedIndex()));
    }

    @Override
    public void showForm(KnowledgeBaseConfiguration knowledgeBaseConfiguration) {
        form.setConfig(knowledgeBaseConfiguration);
    }
}
