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

import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.DecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.SelectedCellChangeEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.SelectedCellChangeHandler;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.VerticalDecoratedGridSidebarWidget;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.VerticalDecoratedGridWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A Vertical Decision Table composed of a VerticalDecoratedGridWidget
 */
public class VerticalDecisionTableWidget extends AbstractDecisionTableWidget {

    private VerticalDecisionTableHeaderWidget header;

    public VerticalDecisionTableWidget(DecisionTableControlsWidget ctrls,
                                       SuggestionCompletionEngine sce,
                                       EventBus eventBus) {
        super( ctrls,
               sce, 
               eventBus );

        VerticalPanel vp = new VerticalPanel();

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedGridWidget<DTColumnConfig52>( resources );
        header = new VerticalDecisionTableHeaderWidget( resources,
                                                        widget );
        DecoratedGridSidebarWidget<DTColumnConfig52> sidebar = new VerticalDecoratedGridSidebarWidget<DTColumnConfig52>( resources,
                                                                                                                         widget,
                                                                                                                         this );
        widget.setHeaderWidget( header );
        widget.setSidebarWidget( sidebar );
        widget.setHasSystemControlledColumns( this );

        widget.addSelectedCellChangeHandler( new SelectedCellChangeHandler() {

            public void onSelectedCellChange(SelectedCellChangeEvent event) {

                CellValue< ? > cell = widget.getData().get( event.getCellSelectionDetail().getCoordinate() );
                dtableCtrls.getOtherwiseButton().setEnabled( canAcceptOtherwiseValues( cell ) );
            }

        } );

        vp.add( widget );
        vp.add( ctrls );
        initWidget( vp );
    }

    /**
     * Set the Decision Table's data and hook-up the Header
     * 
     * @param data
     */
    @Override
    public void setModel(GuidedDecisionTable52 model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }

        header.setModel( model );
        super.setModel( model );
    }

}
