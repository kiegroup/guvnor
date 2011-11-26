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

import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractDecoratedGridHeaderWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractDecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractDecoratedGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractMergableGridWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetModelEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetTemplateDataEvent;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Panel;

/**
 * A Decorated Grid for Template Data
 */
public abstract class AbstractDecoratedTemplateDataGridWidget extends AbstractDecoratedGridWidget<TemplateModel, TemplateDataColumn, String> {

    //Factories to create new data elements
    protected final TemplateDataCellFactory      cellFactory;
    protected final TemplateDataCellValueFactory cellValueFactory;

    public AbstractDecoratedTemplateDataGridWidget(ResourcesProvider<TemplateDataColumn> resources,
                                                   TemplateDataCellFactory cellFactory,
                                                   TemplateDataCellValueFactory cellValueFactory,
                                                   EventBus eventBus,
                                                   Panel mainPanel,
                                                   Panel bodyPanel,
                                                   AbstractMergableGridWidget<TemplateModel, TemplateDataColumn> gridWidget,
                                                   AbstractDecoratedGridHeaderWidget<TemplateModel, TemplateDataColumn> headerWidget,
                                                   AbstractDecoratedGridSidebarWidget<TemplateModel, TemplateDataColumn> sidebarWidget) {
        super( resources,
               eventBus,
               mainPanel,
               bodyPanel,
               gridWidget,
               headerWidget,
               sidebarWidget );
        if ( cellFactory == null ) {
            throw new IllegalArgumentException( "cellFactory cannot be null" );
        }
        if ( cellValueFactory == null ) {
            throw new IllegalArgumentException( "cellValueFactory cannot be null" );
        }
        this.cellFactory = cellFactory;
        this.cellValueFactory = cellValueFactory;

        //Wire-up event handlers
        eventBus.addHandler( SetTemplateDataEvent.TYPE,
                             this );
    }

    public void onSetModel(SetModelEvent<TemplateModel> event) {
        //TODO {manstis} Add the code!
    }

    public void onInsertColumn(InsertColumnEvent<TemplateDataColumn, String> event) {
        //TODO {manstis} Add the code!
    }

}
