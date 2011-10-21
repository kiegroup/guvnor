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
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An abstract "Header" widget to decorate a <code>DecoratedGridWidget</code>
 * 
 * @param <T>
 *            The type of domain columns represented by the Header
 */
public abstract class DecoratedGridHeaderWidget<T> extends CellPanel
    implements
    HasResizeHandlers,
    HasColumnResizeHandlers {

    /**
     * Container class for information relating to re-size operations
     */
    public class ResizerInformation {

        private boolean          isResizePrimed    = false;
        private boolean          isResizing        = false;
        private int              resizeColumnLeft  = 0;
        private DynamicColumn<T> resizeColumn      = null;
        private int              resizeColumnWidth = 0;

        /**
         * @param resizeColumn
         *            the resizeColumn to set
         */
        public void setResizeColumn(DynamicColumn<T> resizeColumn) {
            this.resizeColumn = resizeColumn;
        }

        /**
         * @param resizeColumnLeft
         *            the resizeColumnLeft to set
         */
        public void setResizeColumnLeft(int resizeColumnLeft) {
            this.resizeColumnLeft = resizeColumnLeft;
        }

        /**
         * @param isResizePrimed
         *            the bResizePrimed to set
         */
        public void setResizePrimed(boolean isResizePrimed) {
            this.isResizePrimed = isResizePrimed;
        }

    }

    private static final int         MIN_COLUMN_WIDTH = 16;

    protected Panel                  panel;
    protected DecoratedGridWidget<T> grid;

    // Resources
    protected static final Constants constants        = GWT.create( Constants.class );
    protected ResourcesProvider<T>   resources;

    // Column resizing
    private ResizerInformation       resizerInfo      = new ResizerInformation();
    private DivElement               resizer;

    /**
     * Construct a "Header" for the provided DecoratedGridWidget
     * 
     * @param grid
     * @param styleProvider
     */
    public DecoratedGridHeaderWidget(ResourcesProvider<T> resources,
                                     DecoratedGridWidget<T> grid) {
        if ( resources == null ) {
            throw new IllegalArgumentException( "resources cannot be null" );
        }
        this.resources = resources;
        if ( grid == null ) {
            throw new IllegalArgumentException( "grid cannot be null" );
        }
        this.grid = grid;

        // Container DIV in which the components will live
        Element tre = DOM.createTR();
        Element tce = DOM.createTD();
        Element div = DOM.createDiv();
        div.getStyle().setPosition( Position.RELATIVE );
        getBody().getParentElement().<TableElement> cast().setCellSpacing( 0 );
        getBody().getParentElement().<TableElement> cast().setCellPadding( 0 );
        tce.appendChild( div );
        tre.appendChild( tce );
        getBody().appendChild( tre );

        // Widgets within the container
        panel = new ScrollPanel();

        // We don't want scroll bars on the ScrollPanel so hide any overflow
        panel.getElement().getStyle().setOverflow( Overflow.HIDDEN );
        panel.add( getHeaderWidget() );
        add( panel,
             div );

        // Column resizing
        resizer = DOM.createDiv().<DivElement> cast();
        resizer.addClassName( resources.headerResizer() );
        resizer.getStyle().setTop( 0,
                                   Unit.PX );

        // Add the resizer to the outer most container, otherwise it gets
        // truncated by the ScrollPanel as it hides any overflow
        div.appendChild( resizer );

        addDomHandler( new MouseMoveHandler() {

                           public void onMouseMove(MouseMoveEvent event) {
                               int mx = event.getClientX();
                               if ( resizerInfo.isResizing ) {
                                   if ( mx - resizerInfo.resizeColumnLeft < MIN_COLUMN_WIDTH ) {
                                       event.preventDefault();
                                       return;
                                   }
                                   setResizerDimensions( event.getX() );
                                   resizerInfo.resizeColumnWidth = mx - resizerInfo.resizeColumnLeft;
                                   resizeColumn( resizerInfo.resizeColumn,
                                                 resizerInfo.resizeColumnWidth );

                                   // Second call to set dimensions as a column
                                   // resize can add (or remove) a scroll bar to
                                   // (or from) the Decision Table and our
                                   // resizer needs to be redrawn accordingly.
                                   // Just having the call to set dimensions
                                   // after the column has been resized added
                                   // excess flicker to movement of the resizer.
                                   setResizerDimensions( event.getX() );
                                   event.preventDefault();
                               } else {
                                   resizerInfo = getResizerInformation( mx );
                               }
                           }

                       },
                       MouseMoveEvent.getType() );

        addDomHandler( new MouseDownHandler() {

                           public void onMouseDown(MouseDownEvent event) {
                               if ( !resizerInfo.isResizePrimed ) {
                                   return;
                               }
                               int mx = event.getX();
                               resizerInfo.isResizing = true;
                               setResizerDimensions( mx );
                               resizer.getStyle().setVisibility( Visibility.VISIBLE );
                               event.preventDefault();
                           }

                       },
                       MouseDownEvent.getType() );

        addDomHandler( new MouseUpHandler() {

                           public void onMouseUp(MouseUpEvent event) {
                               if ( !resizerInfo.isResizing ) {
                                   return;
                               }
                               resizerInfo.isResizing = false;
                               resizerInfo.isResizePrimed = false;
                               resizer.getStyle().setVisibility( Visibility.HIDDEN );
                               event.preventDefault();
                           }

                       },
                       MouseUpEvent.getType() );

        addDomHandler( new MouseOutHandler() {

                           public void onMouseOut(MouseOutEvent event) {
                               if ( !resizerInfo.isResizing ) {
                                   return;
                               }
                               resizerInfo.isResizing = false;
                               resizerInfo.isResizePrimed = false;
                               resizer.getStyle().setVisibility( Visibility.HIDDEN );
                               event.preventDefault();
                           }

                       },
                       MouseOutEvent.getType() );

    }

    public HandlerRegistration addColumnResizeHandler(
                                                      ColumnResizeHandler handler) {
        if ( handler == null ) {
            throw new IllegalArgumentException( "handler cannot be null" );
        }

        return addHandler( handler,
                           ColumnResizeEvent.getType() );
    }

    public HandlerRegistration addResizeHandler(ResizeHandler handler) {
        if ( handler == null ) {
            throw new IllegalArgumentException( "handler cannot be null" );
        }

        return addHandler( handler,
                           ResizeEvent.getType() );
    }

    /**
     * Redraw entire header
     */
    public abstract void redraw();

    /**
     * Set scroll position to enable some degree of synchronisation between
     * DecisionTable and DecisionTableHeader
     * 
     * @param position
     */
    public abstract void setScrollPosition(int position);

    @Override
    public void setWidth(String width) {
        // Set the width of our ScrollPanel too; to prevent the containing
        // DIV from extending it's width to accommodate the increase in size
        super.setWidth( width );
        panel.setWidth( width );
    }

    // Bit of a hack to ensure the resizer is the correct size. The
    // Decision Table itself could be contained in an outer most DIV
    // that hides any overflow however the horizontal scrollbar
    // would be rendered inside the DIV and hence still be covered
    // by the resizer.
    private void setResizerDimensions(int position) {
        resizer.getStyle().setHeight(
                                      grid.getSidebarWidget().getOffsetHeight(),
                                      Unit.PX );
        resizer.getStyle().setLeft( position,
                                    Unit.PX );
    }

    /**
     * Get the Widget that should be wrapped by the scroll panel and resize
     * handlers. The widget renders the actual "header" embedded within the
     * decorations provided by this class: scroll-bars and resizing support.
     * 
     * @return
     */
    protected abstract Widget getHeaderWidget();

    /**
     * Given the X-coordinate check whether resizing of any column should be
     * enabled. The ResizerInformation return value contains necessary
     * information for this decorating class to perform column-resizing.
     * 
     * @param mx
     *            the MouseMoveEvent.event.getClientX() coordinate
     * @return
     */
    protected abstract ResizerInformation getResizerInformation(int mx);

    /**
     * Resize the Header column
     * 
     * @param resizeColumn
     * @param resizeColumnWidth
     */
    protected abstract void resizeColumn(DynamicColumn<T> resizeColumn,
                                         int resizeColumnWidth);

}
