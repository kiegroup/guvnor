/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.SetFormParamsEvent;

@Dependent
public class ReviewViewImpl extends Composite implements ReviewPresenter.ReviewView {

    @Override
    public void displayNotification( String text ) {

    }

    interface Binder
            extends UiBinder<Widget, ReviewViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    private PlaceManager placeManager;

    private ReviewPresenter presenter;

    private boolean readOnly;

    @UiField
    FormGroup showCommitsGroup;

    @UiField
    TextArea showCommitsBox;

    @UiField
    FormGroup requestorGroup;

    @UiField
    TextBox requestorTextBox;

    @UiField
    FormGroup repositoryGroup;

    @UiField
    TextBox repositoryTextBox;

    @UiField
    FormGroup approvedGroup;

    @UiField
    CheckBox approvedCheckBox;

    @UiField
    FormGroup commentsGroup;

    @UiField
    TextArea commentsBox;

    @Inject
    private Event<GetFormParamsEvent> getFormParamsEvent;

    public ReviewViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        showCommitsBox.setEnabled( false );
    }

    public void getOutputMap( @Observes RequestFormParamsEvent event ) {
        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put( "out_reviewed", approvedCheckBox.getValue() );
        outputMap.put( "out_comment", commentsBox.getText() );
        getFormParamsEvent.fire( new GetFormParamsEvent( event.getAction(), outputMap ) );
    }

    @Override
    public void init( ReviewPresenter presenter ) {
        this.presenter = presenter;

    }

    public void setInputMap( @Observes SetFormParamsEvent event ) {
        requestorTextBox.setText( event.getParams().get( "in_requestor" ) );
        repositoryTextBox.setText( event.getParams().get( "in_repository" ) );
        showCommitsBox.setText( event.getParams().get( "in_commits" ) );
        setReadOnly( event.isReadOnly() );
    }

    public void setReadOnly( boolean b ) {
        this.readOnly = b;
        requestorTextBox.setEnabled( !readOnly );
        repositoryTextBox.setEnabled( !readOnly );
        approvedCheckBox.setEnabled( !readOnly );
        commentsBox.setEnabled( !readOnly );
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

}
