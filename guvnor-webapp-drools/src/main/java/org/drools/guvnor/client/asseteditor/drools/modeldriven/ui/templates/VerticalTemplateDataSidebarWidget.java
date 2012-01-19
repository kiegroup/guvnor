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
package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates.events.SetInternalTemplateDataModelEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractVerticalDecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CopyPasteContextMenu;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.RowMapper;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetInternalModelEvent;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;

import com.google.gwt.event.shared.EventBus;

/**
 * A "sidebar" for a vertical Template Data editor
 */
public class VerticalTemplateDataSidebarWidget extends AbstractVerticalDecoratedGridSidebarWidget<TemplateModel, TemplateDataColumn> {

    private CopyPasteContextMenu contextMenu;

    /**
     * Construct a "sidebar" for a vertical Template Data editor
     */
    public VerticalTemplateDataSidebarWidget(ResourcesProvider<TemplateDataColumn> resources,
                                                           EventBus eventBus) {
        // Argument validation performed in the superclass constructor
        super( resources,
               eventBus );

        contextMenu = new CopyPasteContextMenu( eventBus );

        //Wire-up event handlers
        eventBus.addHandler( SetInternalTemplateDataModelEvent.TYPE,
                             this );
    }

    public void onSetInternalModel(SetInternalModelEvent<TemplateModel, TemplateDataColumn> event) {
        this.data = event.getData();
        this.rowMapper = new RowMapper( this.data );
        this.redraw();
    }

    @Override
    public void showContextMenu(int index,
                                int clientX,
                                int clientY) {
        contextMenu.setContextRows( rowMapper.mapToAllAbsoluteRows( index ) );
        contextMenu.setPopupPosition( clientX,
                                      clientY );
        contextMenu.show();
    }

}
