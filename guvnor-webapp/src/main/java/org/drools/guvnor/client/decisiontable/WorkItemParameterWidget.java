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
package org.drools.guvnor.client.decisiontable;

import org.drools.ide.common.shared.workitems.PortableParameterDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A Widget to display a Work Item parameter
 */
public class WorkItemParameterWidget extends Composite {

    interface WorkItemParameterWidgetBinder
        extends
        UiBinder<HorizontalPanel, WorkItemParameterWidget> {
    }

    @UiField
    Label                                        parameterName;

    @UiField
    TextBox                                      parameterValue;

    @UiField
    CheckBox                                     chkBindValue;

    @UiField
    ListBox                                      lstAvailableBindings;

    private static WorkItemParameterWidgetBinder uiBinder = GWT.create( WorkItemParameterWidgetBinder.class );

    private PortableParameterDefinition          ppd;

    public WorkItemParameterWidget(PortableParameterDefinition ppd) {
        this.ppd = ppd;
        initWidget( uiBinder.createAndBindUi( this ) );
        this.parameterName.setText( ppd.getName() );
    }

}
