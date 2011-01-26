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
package org.drools.guvnor.client.widgets.decoratedgrid;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Vertical implementation of DecoratedGridWidget.
 * 
 * @param <T>
 *            The type of domain columns represented
 * 
 * @author manstis
 * 
 */
public class VerticalDecoratedGridWidget<T> extends DecoratedGridWidget<T> {

    /**
     * BodyPanel is a VerticalPanel in which Header and Grid are inserted
     */
    @Override
    public Panel getBodyPanel() {
        if ( this.bodyPanel == null ) {
            this.bodyPanel = new VerticalPanel();
        }
        return this.bodyPanel;
    }

    /**
     * Grid is a vertical table
     */
    @Override
    public MergableGridWidget<T> getGridWidget() {
        if ( this.gridWidget == null ) {
            this.gridWidget = new VerticalMergableGridWidget<T>( this,
                                                                 data,
                                                                 columns );
        }
        return this.gridWidget;
    }

    /**
     * MainPanel is a HorizontalPanel in which the Sidebar and BodyPanel are
     * inserted
     */
    @Override
    public Panel getMainPanel() {
        if ( this.mainPanel == null ) {
            this.mainPanel = new HorizontalPanel();
        }
        return this.mainPanel;
    }

    /**
     * Return a ScrollHandler to ensure the Header and Sidebar are repositioned
     * according to the position of the scroll bars surrounding the GridWidget
     */
    @Override
    public ScrollHandler getScrollHandler() {
        return new ScrollHandler() {

            public void onScroll(ScrollEvent event) {
                headerWidget.setScrollPosition( scrollPanel
                        .getHorizontalScrollPosition() );
                sidebarWidget
                        .setScrollPosition( scrollPanel.getScrollPosition() );
            }

        };
    }

    /**
     * Set the Header Widget and attach resize handlers to GridWidget to support
     * column resizing and to resize GridWidget's ScrollPanel when header
     * resizes.
     */
    public void setHeaderWidget(DecoratedGridHeaderWidget<T> headerWidget) {
        if ( headerWidget == null ) {
            throw new IllegalArgumentException( "headerWidget cannot be null" );
        }
        this.headerWidget = headerWidget;
        headerWidget.addColumnResizeHandler( new ColumnResizeHandler() {

            @Override
            public void onColumnResize(ColumnResizeEvent event) {
                gridWidget.resizeColumn( event.getColumn(),
                                         event.getWidth() );
            }

        } );
        this.headerWidget.addResizeHandler( new ResizeHandler() {

            public void onResize(ResizeEvent event) {
                scrollPanel.setHeight( (height - event.getHeight())
                                       + "px" );
                assertDimensions();
            }
        } );

        bodyPanel.add( headerWidget );
        bodyPanel.add( scrollPanel );
    }

    /**
     * Set the Sidebar Widget and attach resize handlers to SidebarWidget to
     * resize the Sidebar when header resizes.
     */
    public void setSidebarWidget(final DecoratedGridSidebarWidget<T> sidebarWidget) {
        if ( sidebarWidget == null ) {
            throw new IllegalArgumentException( "sidebarWidget cannot be null" );
        }
        this.sidebarWidget = sidebarWidget;
        this.headerWidget.addResizeHandler( new ResizeHandler() {

            public void onResize(ResizeEvent event) {
                sidebarWidget.resizeSidebar( event.getHeight() );
            }

        } );

        this.mainPanel.add( sidebarWidget );
        this.mainPanel.add( bodyPanel );
    }

}
