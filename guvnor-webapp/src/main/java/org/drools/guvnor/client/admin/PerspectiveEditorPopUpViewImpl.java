/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.Popup;
import org.drools.guvnor.client.messages.Constants;

public class PerspectiveEditorPopUpViewImpl extends Popup implements PerspectiveEditorPopUpView {

    private Constants constants = GWT.create( Constants.class );

    interface PerspectiveEditorPopUpViewImplBinder
            extends
            UiBinder<Widget, PerspectiveEditorPopUpViewImpl> {

    }

    private static PerspectiveEditorPopUpViewImplBinder uiBinder = GWT.create(PerspectiveEditorPopUpViewImplBinder.class);

    private Presenter presenter;

    private Widget content;

    @UiField
    TextBox nameTextBox;

    @UiField
    TextBox urlTextBox;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    public PerspectiveEditorPopUpViewImpl() {
        setTitle(constants.PerspectivesConfiguration());
        content = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget getContent() {
        return content;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setName(String name) {
        nameTextBox.setText(name);
    }

    public String getName() {
        return nameTextBox.getText();
    }

    public void setUrl(String url) {
        urlTextBox.setText(url);
    }

    public String getUrl() {
        return urlTextBox.getText();
    }

    public void showNameCanNotBeEmptyWarning() {
        ErrorPopup.showMessage(constants.NameCanNotBeEmpty());
    }

    public void showUrlCanNotBeEmptyWarning() {
        ErrorPopup.showMessage(constants.UrlCanNotBeEmpty());
    }

    @UiHandler("saveButton")
    public void okClick(ClickEvent event) {
        presenter.onSave();
    }

    @UiHandler("cancelButton")
    public void handleClick(ClickEvent event) {
        presenter.onCancel();
    }
}
