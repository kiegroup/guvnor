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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NewProjectPopupViewImpl
        extends Composite
        implements NewProjectPopupView {


    interface NewFolderPopupViewImplBinder
            extends
            UiBinder<Widget, NewProjectPopupViewImpl> {

    }

    private static NewFolderPopupViewImplBinder uiBinder = GWT.create(NewFolderPopupViewImplBinder.class);


    @UiField
    TextBox nameTextBox;

    private Presenter presenter;


    public NewProjectPopupViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("nameTextBox")
    public void onNameChange(KeyUpEvent e) {
        presenter.onNameChange(nameTextBox.getText());
    }

    @UiHandler("okButton")
    public void onOk(ClickEvent e) {
        presenter.onOk();
    }

    @UiHandler("cancelButton")
    public void onCancel(ClickEvent e) {
        presenter.onCancel();
    }
}
