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

import java.util.List;

import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * An abstract grid of data. Implementations can choose the orientation to
 * render "rows" and "columns" (e.g. some may transpose the normal meaning to
 * provide a horizontal implementation of normally vertical tabular data)
 * 
 * @author manstis
 * 
 */
public abstract class MergableGridWidget<T> extends Widget {

    /**
     * Container for a cell's extents
     * 
     * @author manstis
     * 
     */
    public static class CellExtents {
        private int offsetX;
        private int offsetY;
        private int height;
        private int width;

        CellExtents(int offsetX,
                    int offsetY,
                    int height,
                    int width) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.height = height;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public int getWidth() {
            return width;
        }

    }

    // TABLE elements
    protected TableElement                        table;
    protected TableSectionElement                 tbody;

    // Resources
    protected static final DecisionTableResources resource = GWT
                                                                   .create( DecisionTableResources.class );

    protected static final DecisionTableStyle     style    = resource.cellTableStyle();

    // Data and columns to render
    protected DynamicData                         data;
    protected List<DynamicColumn<T>>              columns;

    /**
     * A grid of cells.
     * 
     * @param data
     * @param columns
     */
    public MergableGridWidget(final DynamicData data,
                              final List<DynamicColumn<T>> columns) {
        if ( data == null ) {
            throw new IllegalArgumentException( "data cannot be null" );
        }
        if ( columns == null ) {
            throw new IllegalArgumentException( "columns cannot be null" );
        }

        this.data = data;
        this.columns = columns;

        style.ensureInjected();

        // Create some elements to contain the grid
        table = Document.get().createTableElement();
        tbody = Document.get().createTBodyElement();
        table.setClassName( style.cellTable() );
        table.setCellPadding( 0 );
        table.setCellSpacing( 0 );
        setElement( table );

        table.appendChild( tbody );

        // Events in which we're interested (note, if a Cell<?> appears not to
        // work I've probably forgotten some events. Might be a better way of
        // doing this, but I copied CellTable<?, ?>'s lead
        sinkEvents( Event.getTypeInt( "click" )
                    | Event.getTypeInt( "dblclick" )
                    | Event.getTypeInt( "mouseover" )
                    | Event.getTypeInt( "mouseout" )
                    | Event.getTypeInt( "change" )
                    | Event.getTypeInt( "keypress" )
                    | Event.getTypeInt( "keydown" ) );
    }

    /**
     * Delete the row at the given index. Partial redraw.
     * 
     * @param index
     */
    public abstract void deleteRow(int index);

    /**
     * Remove styling indicating a selected state
     * 
     * @param cell
     */
    public abstract void deselectCell(CellValue< ? extends Comparable< ? >> cell);

    /**
     * Resize a column
     * 
     * @param col
     * @param width
     */
    public abstract void resizeColumn(DynamicColumn< ? > col,
                                      int width);

    /**
     * Retrieve the extents of a cell
     * 
     * @param cv
     *            The cell for which to retrieve the extents
     * @return
     */
    public CellExtents getSelectedCellExtents(
                                              CellValue< ? extends Comparable< ? >> cv) {

        if ( cv == null ) {
            throw new IllegalArgumentException( "cv cannot be null" );
        }

        // Cells in hidden columns do not have extents
        if ( !columns.get( cv.getCoordinate().getCol() )
                .isVisible() ) {
            return null;
        }

        Coordinate hc = cv.getHtmlCoordinate();
        TableRowElement tre = tbody.getRows().getItem( hc.getRow() )
                .<TableRowElement> cast();
        TableCellElement tce = tre.getCells().getItem( hc.getCol() )
                .<TableCellElement> cast();
        int offsetX = tce.getOffsetLeft();
        int offsetY = tce.getOffsetTop();
        int w = tce.getOffsetWidth();
        int h = tce.getOffsetHeight();
        CellExtents e = new CellExtents( offsetX,
                                         offsetY,
                                         h,
                                         w );
        return e;
    }

    /**
     * Hide a column
     */
    public abstract void hideColumn(int index);

    /**
     * Insert the given row before the provided index. Partial redraw.
     * 
     * @param index
     *            The index of the row before which the new row should be
     *            inserted
     * @param rowData
     *            The row of data to insert
     */
    public abstract void insertRowBefore(int index,
                                         DynamicDataRow rowData);

    /**
     * Redraw the whole table
     */
    public abstract void redraw();

    /**
     * Redraw table column. Partial redraw
     * 
     * @param index
     *            Start column index (inclusive)
     */
    public abstract void redrawColumn(int index);

    /**
     * Redraw table columns. Partial redraw
     * 
     * @param startRedrawIndex
     *            Start column index (inclusive)
     * @param endRedrawIndex
     *            End column index (inclusive)
     */
    public abstract void redrawColumns(int startRedrawIndex,
                                       int endRedrawIndex);

    /**
     * Redraw table rows. Partial redraw
     * 
     * @param startRedrawIndex
     *            Start row index (inclusive)
     * @param endRedrawIndex
     *            End row index (inclusive)
     */
    public abstract void redrawRows(int startRedrawIndex,
                                    int endRedrawIndex);

    /**
     * Add styling to cell to indicate a selected state
     * 
     * @param cell
     */
    public abstract void selectCell(CellValue< ? extends Comparable< ? >> cell);

    /**
     * Show a column
     */
    public abstract void showColumn(int index);

}
