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
package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractVerticalDecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.RowMapper;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetInternalDecisionTableModelEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetInternalModelEvent;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.event.shared.EventBus;

/**
 * A "sidebar" for a vertical Decision Table
 */
public class VerticalDecisionTableSidebarWidget extends AbstractVerticalDecoratedGridSidebarWidget<GuidedDecisionTable52, DTColumnConfig52> {

    /**
     * Construct a "sidebar" for a vertical Decision Table
     */
    public VerticalDecisionTableSidebarWidget(ResourcesProvider<DTColumnConfig52> resources,
                                                           EventBus eventBus) {
        // Argument validation performed in the superclass constructor
        super( resources,
               eventBus );

        //Wire-up event handlers
        eventBus.addHandler( SetInternalDecisionTableModelEvent.TYPE,
                             this );
    }

    public void onSetInternalModel(SetInternalModelEvent<GuidedDecisionTable52, DTColumnConfig52> event) {
        this.data = event.getData();
        this.rowMapper = new RowMapper( this.data );
        this.redraw();
    }

}
