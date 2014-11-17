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
package org.guvnor.asset.management.client.editors.forms.review;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.SetFormParamsEvent;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class ReviewViewImpl extends Composite implements ReviewPresenter.ReviewView {

    @Override
    public void displayNotification(String text) {

    }

    interface Binder
            extends UiBinder<Widget, ReviewViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @Inject
    private PlaceManager placeManager;

    private ReviewPresenter presenter;

    private boolean readOnly;

    @UiField
    ControlGroup showCommitsGroup;

    @UiField
    TextArea showCommitsBox;


    @UiField
    ControlGroup requestorGroup;

    @UiField
    TextBox requestorTextBox;

    @UiField
    ControlGroup repositoryGroup;

    @UiField
    TextBox repositoryTextBox;

    @UiField
    ControlGroup approvedGroup;

    @UiField
    CheckBox approvedCheckBox;

    @UiField
    ControlGroup commentsGroup;

    @UiField
    TextArea commentsBox;
    
    @Inject
    private Event<GetFormParamsEvent> getFormParamsEvent;


    public ReviewViewImpl() {

        initWidget(uiBinder.createAndBindUi(this));

    }

    public void getOutputMap(@Observes RequestFormParamsEvent event) {
        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("out_reviewed", approvedCheckBox.getValue());
        outputMap.put("out_comment", commentsBox.getText());
        getFormParamsEvent.fire(new GetFormParamsEvent(event.getAction(), outputMap));
    }

    @Override
    public void init(ReviewPresenter presenter) {
        this.presenter = presenter;

    }

    
    public void setInputMap(@Observes SetFormParamsEvent event) {
        requestorTextBox.setText(event.getParams().get("in_requestor"));
        repositoryTextBox.setText( event.getParams().get("in_repository"));
        showCommitsBox.setText(event.getParams().get("in_commits"));
        setReadOnly(event.isReadOnly());
    }

    
    public void setReadOnly(boolean b) {
        this.readOnly = b;
    }

    
    public boolean isReadOnly() {
        return this.readOnly;
    }

}
