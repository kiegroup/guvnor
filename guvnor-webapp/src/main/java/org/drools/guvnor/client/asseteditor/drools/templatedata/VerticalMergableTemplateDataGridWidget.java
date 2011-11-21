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
package org.drools.guvnor.client.asseteditor.drools.templatedata;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractVerticalMergableGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetInternalTemplateDataModelEvent;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel;

import com.google.gwt.event.shared.EventBus;

/**
 * A Vertical implementation of MergableGridWidget, that renders columns as erm,
 * columns and rows as rows. Supports merging of cells between rows.
 */
public class VerticalMergableTemplateDataGridWidget extends AbstractVerticalMergableGridWidget<TemplateModel, TemplateDataColumn> {

    public VerticalMergableTemplateDataGridWidget(ResourcesProvider<TemplateDataColumn> resources,
                                                  TemplateDataCellValueFactory cellValueFactory,
                                                  EventBus eventBus) {
        super( resources,
               cellValueFactory,
               eventBus );

        //Wire-up event handlers
        eventBus.addHandler( SetInternalTemplateDataModelEvent.TYPE,
                             this );
    }

}
