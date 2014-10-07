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
package org.guvnor.asset.management.client.editors.forms.error;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.editors.forms.promote.SelectAssetsToPromotePresenter;
import org.kie.uberfire.client.forms.FormDisplayerView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class DisplayErrorViewImpl extends Composite implements DisplayErrorPresenter.DisplayErrorView, FormDisplayerView {

    @Override
    public void displayNotification(String text) {

    }

    interface Binder
            extends UiBinder<Widget, DisplayErrorViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);
    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private DisplayErrorPresenter presenter;

    private boolean readOnly;

    @UiField
    ControlGroup showErrorGroup;

    @UiField
    TextArea errorBox;

    @UiField
    ControlGroup processNameGroup;

    @UiField
    TextBox processNameTextBox;

    public DisplayErrorViewImpl() {

        initWidget(uiBinder.createAndBindUi(this));

    }

    public Map<String, Object> getOutputMap() {
        Map<String, Object> outputMap = new HashMap<String, Object>();

        return outputMap;
    }

    @Override
    public void init(DisplayErrorPresenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void setInputMap(Map<String, String> params) {
        processNameTextBox.setText((String) params.get("ProcessName"));
        errorBox.setText((String) params.get("Error"));
    }

    @Override
    public void setReadOnly(boolean b) {
        this.readOnly = b;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

}
