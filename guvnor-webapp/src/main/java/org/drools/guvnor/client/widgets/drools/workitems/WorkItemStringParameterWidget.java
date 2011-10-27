/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.widgets.drools.workitems;

import org.drools.ide.common.shared.workitems.PortableStringParameterDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Widget to display a Work Item String parameter
 */
public class WorkItemStringParameterWidget extends WorkItemParameterWidget {

    interface WorkItemStringParameterWidgetBinder
        extends
        UiBinder<HorizontalPanel, WorkItemStringParameterWidget> {
    }

    @UiField
    Label                                              parameterName;

    @UiField
    TextBox                                            parameterEditor;

    @UiField
    ListBox                                            lstAvailableBindings;

    private static WorkItemStringParameterWidgetBinder uiBinder = GWT.create( WorkItemStringParameterWidgetBinder.class );

    public WorkItemStringParameterWidget(PortableStringParameterDefinition ppd) {
        super( ppd );
        this.parameterName.setText( ppd.getName() );
        if ( ppd.getValue() != null ) {
            this.parameterEditor.setText( ppd.getValue() );
        }
    }

    @Override
    protected Widget getWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("parameterEditor")
    void parameterEditorOnChange(ChangeEvent event) {
        ((PortableStringParameterDefinition) ppd).setValue( parameterEditor.getText() );
    }

}
