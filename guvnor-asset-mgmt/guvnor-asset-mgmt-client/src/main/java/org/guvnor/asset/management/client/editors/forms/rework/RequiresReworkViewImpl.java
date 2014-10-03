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
package org.guvnor.asset.management.client.editors.forms.rework;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.client.forms.FormDisplayerView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

@Dependent
public class RequiresReworkViewImpl extends Composite implements RequiresReworkPresenter.RequiresReworkView, FormDisplayerView {

    @Override
    public void displayNotification(String text) {

    }

    interface Binder
            extends UiBinder<Widget, RequiresReworkViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);
    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private RequiresReworkPresenter presenter;

    private boolean readOnly;

    @UiField
    ControlGroup reviewCommentGroup;

    @UiField
    TextArea reviewCommentBox;


    public RequiresReworkViewImpl() {

        initWidget(uiBinder.createAndBindUi(this));

    }

    public Map<String, Object> getOutputMap() {
        Map<String, Object> outputMap = new HashMap<String, Object>();

        return outputMap;
    }

    @Override
    public void init(RequiresReworkPresenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void setInputMap(Map<String, Object> params) {
        reviewCommentBox.setText((String) params.get("ReviewComment"));

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
