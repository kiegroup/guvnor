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

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.NumericIntegerTextBox;
import org.drools.ide.common.shared.workitems.PortableIntegerParameterDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Widget to display a Work Item Integer parameter
 */
public class WorkItemIntegerParameterWidget extends WorkItemParameterWidget {

    interface WorkItemIntegerParameterWidgetBinder
        extends
        UiBinder<HorizontalPanel, WorkItemIntegerParameterWidget> {
    }

    @UiField
    Label                                               parameterName;

    @UiField
    NumericIntegerTextBox                               parameterEditor;

    @UiField
    ListBox                                             lstAvailableBindings;

    private static WorkItemIntegerParameterWidgetBinder uiBinder = GWT.create( WorkItemIntegerParameterWidgetBinder.class );

    public WorkItemIntegerParameterWidget(PortableIntegerParameterDefinition ppd) {
        super( ppd );
        this.parameterName.setText( ppd.getName() );
        if ( ppd.getValue() != null ) {
            this.parameterEditor.setText( Integer.toString( ppd.getValue() ) );
        }
    }

    @Override
    protected Widget getWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("parameterEditor")
    void parameterEditorOnChange(ChangeEvent event) {
        try {
            ((PortableIntegerParameterDefinition) ppd).setValue( Integer.parseInt( parameterEditor.getText() ) );
        } catch ( NumberFormatException nfe ) {
            ((PortableIntegerParameterDefinition) ppd).setValue( null );
        }
    }

}
