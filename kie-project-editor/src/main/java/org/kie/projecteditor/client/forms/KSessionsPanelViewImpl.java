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

package org.kie.projecteditor.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.kie.projecteditor.shared.model.KSessionModel;

import javax.inject.Inject;

public class KSessionsPanelViewImpl
        extends Composite
        implements KSessionsPanelView {

    private Presenter presenter;

    interface KSessionsPanelViewImplBinder
            extends
            UiBinder<Widget, KSessionsPanelViewImpl> {

    }

    private static KSessionsPanelViewImplBinder uiBinder = GWT.create(KSessionsPanelViewImplBinder.class);

    @UiField
    ListBox kSessionsList;

    @UiField(provided = true)
    KSessionForm kSessionForm;

    @Inject
    public KSessionsPanelViewImpl(KSessionForm kSessionForm) {
        this.kSessionForm = kSessionForm;
        initWidget(uiBinder.createAndBindUi(this));
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addKSessionModel(KSessionModel model) {
        kSessionsList.addItem(model.getFullName());
    }

    @Override
    public void clearList() {
        kSessionsList.clear();
    }

    @Override
    public void setSelectedSession(KSessionModel kSessionModel) {
        kSessionForm.setModel(kSessionModel);
    }

    @UiHandler("kSessionsList")
    public void handleChange(ChangeEvent event) {
        presenter.selectKSession(kSessionsList.getValue(kSessionsList.getSelectedIndex()));
    }

}
