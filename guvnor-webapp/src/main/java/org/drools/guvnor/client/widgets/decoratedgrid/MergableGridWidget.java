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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * An abstract grid of data. Implementations can choose the orientation to
 * render "rows" and "columns" (e.g. some may transpose the normal meaning to
 * provide a horizontal implementation of normally vertical tabular data)
 */
public abstract class MergableGridWidget<T> extends Widget
    implements
    ValueUpdater<Object>,
    HasSelectedCellChangeHandlers {

    /**
     * Add a handler for SelectedCellChangeEvents
     */
    public HandlerRegistration addSelectedCellChangeHandler(SelectedCellChangeHandler handler) {
        return addHandler( handler,
                           SelectedCellChangeEvent.getType() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.ValueUpdater#update(java.lang.Object)
     */
    public void update(Object value) {

        // Update underlying data
        for ( CellValue< ? extends Comparable< ? >> cell : getSelectedCells() ) {
            Coordinate c = cell.getCoordinate();
            if ( !columns.get( c.getCol() ).isSystemControlled() ) {
                data.set( c,
                          value );
            }
        }

        // Partial redraw
        assertModelMerging();
        int baseRowIndex = getSelectedCells().first().getPhysicalCoordinate()
                .getRow();
        int minRedrawRow = findMinRedrawRow( baseRowIndex );
        int maxRedrawRow = findMaxRedrawRow( baseRowIndex );

        // When merged cells become unmerged (if their value is
        // cleared need to ensure the re-draw range is at least
        // as large as the selection range
        if ( maxRedrawRow < getSelectedCells().last().getPhysicalCoordinate()
                .getRow() ) {
            maxRedrawRow = getSelectedCells().last().getPhysicalCoordinate()
                    .getRow();
        }
        redrawRows( minRedrawRow,
                               maxRedrawRow );

        //Re-select applicable cells, following change to merge
        selectRange( getSelectedCells().first(),
                                getSelectedCells().last() );

    }
    
    /**
     * Container for a cell's extents
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

    // Enum to support keyboard navigation
    public enum MOVE_DIRECTION {
        LEFT, RIGHT, UP, DOWN, NONE
    }

    //GWT disable text selection in an HTMLTable. 
    //event.stopPropogation() doesn't prevent text selection
    private native static void disableTextSelectInternal(Element e,
                                                         boolean disable)/*-{
        if (disable) {
        e.ondrag = function () { return false; };
        e.onselectstart = function () { return false; };
        e.style.MozUserSelect="none"
        } else {
        e.ondrag = null;
        e.onselectstart = null;
        e.style.MozUserSelect="text"
        }
    }-*/;

    // Selections store the actual grid data selected (irrespective of
    // merged cells). So a merged cell spanning 2 rows is stored as 2
    // selections. Selections are ordered by row number so we can
    // iterate top to bottom.
    protected TreeSet<CellValue< ? extends Comparable< ? >>> selections           = new TreeSet<CellValue< ? extends Comparable< ? >>>(
                                                                                                                                        new Comparator<CellValue< ? extends Comparable< ? >>>() {

                                                                                                                                            public int compare(CellValue< ? extends Comparable< ? >> o1,
                                                                                                                                                               CellValue< ? extends Comparable< ? >> o2) {
                                                                                                                                                return o1.getPhysicalCoordinate().getRow()
                                                                                                                                                       - o2.getPhysicalCoordinate().getRow();
                                                                                                                                            }

                                                                                                                                        } );

    // TABLE elements
    protected TableElement                                   table;
    protected TableSectionElement                            tbody;

    // Resources
    protected static final DecisionTableResources            resource             = GWT.create( DecisionTableResources.class );
    protected static final DecisionTableStyle                style                = resource.cellTableStyle();

    // Data and columns to render
    protected List<DynamicColumn<T>>                         columns              = new ArrayList<DynamicColumn<T>>();
    protected DynamicData                                    data                 = new DynamicData();

    //Properties for multi-cell selection
    protected CellValue< ? >                                 rangeOriginCell;
    protected CellValue< ? >                                 rangeExtentCell;
    protected MOVE_DIRECTION                                 rangeDirection       = MOVE_DIRECTION.NONE;

    protected boolean                                        bDragOperationPrimed = false;
    protected boolean                                        isMerged             = false;

    /**
     * A grid of cells.
     */
    public MergableGridWidget() {
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
                    | Event.getTypeInt( "mousedown" )
                    | Event.getTypeInt( "mouseup" )
                    | Event.getTypeInt( "mousemove" )
                    | Event.getTypeInt( "mouseout" )
                    | Event.getTypeInt( "change" )
                    | Event.getTypeInt( "keypress" )
                    | Event.getTypeInt( "keydown" ) );

        //Prevent text selection
        disableTextSelectInternal( table,
                                   true );

    }

    /**
     * Ensure indexes in the model are correct
     */
    // Here lays a can of worms! Each cell in the Decision Table has three
    // coordinates: (1) The physical coordinate, (2) The coordinate relating to
    // the HTML table element and (3) The coordinate mapping a HTML table
    // element back to the physical coordinate. For example a cell could have
    // the (1) physical coordinate (0,0) which equates to (2) HTML element (0,1)
    // in which case the cell at physical coordinate (0,1) would have a (3)
    // mapping back to (0,0).
    public void assertModelIndexes() {

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            int colCount = 0;
            for ( int iCol = 0; iCol < row.size(); iCol++ ) {

                int newRow = iRow;
                int newCol = colCount;
                CellValue< ? extends Comparable< ? >> indexCell = row.get( iCol );

                // Don't index hidden columns; indexing is used to
                // map between HTML elements and the data behind
                DynamicColumn<T> column = columns.get( iCol );
                if ( column.isVisible() ) {

                    if ( indexCell.getRowSpan() != 0 ) {
                        newRow = iRow;
                        newCol = colCount++;

                        CellValue< ? extends Comparable< ? >> cell = data.get(
                                                                               newRow ).get( newCol );
                        cell.setPhysicalCoordinate( new Coordinate( iRow,
                                                                    iCol ) );

                    } else {
                        DynamicDataRow priorRow = data.get( iRow - 1 );
                        CellValue< ? extends Comparable< ? >> priorCell = priorRow
                                .get( iCol );
                        Coordinate priorHtmlCoordinate = priorCell
                                .getHtmlCoordinate();
                        newRow = priorHtmlCoordinate.getRow();
                        newCol = priorHtmlCoordinate.getCol();
                    }
                }
                indexCell.setCoordinate( new Coordinate( iRow,
                                                         iCol ) );
                indexCell.setHtmlCoordinate( new Coordinate( newRow,
                                                             newCol ) );
            }
        }
    }

    /**
     * Ensure merging is reflected in the entire model
     */
    public void assertModelMerging() {

        //Remove merging first as it initialises all coordinates
        removeModelMerging();

        final int minRowIndex = 0;
        final int maxRowIndex = data.size() - 1;

        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            for ( int iRow = minRowIndex; iRow <= maxRowIndex; iRow++ ) {

                int rowSpan = 1;
                CellValue< ? > cell1 = data.get( iRow ).get( iCol );
                if ( iRow
                     + rowSpan < data.size() ) {

                    CellValue< ? > cell2 = data.get( iRow
                                                     + rowSpan ).get( iCol );

                    // Don't merge empty cells
                    if ( isMerged
                         && !cell1.isEmpty()
                         && !cell2.isEmpty() ) {
                        while ( cell1.getValue().equals( cell2.getValue() )
                                && iRow
                                   + rowSpan < maxRowIndex ) {
                            cell2.setRowSpan( 0 );
                            rowSpan++;
                            cell2 = data.get( iRow
                                              + rowSpan ).get( iCol );
                        }
                        if ( cell1.getValue().equals( cell2.getValue() ) ) {
                            cell2.setRowSpan( 0 );
                            rowSpan++;
                        }
                    }
                    cell1.setRowSpan( rowSpan );
                    iRow = iRow
                           + rowSpan
                           - 1;
                } else {
                    cell1.setRowSpan( rowSpan );
                }
            }
        }

        // Set indexes after merging has been corrected
        assertModelIndexes();
    }

    /**
     * Clear all selections
     */
    public void clearSelection() {
        // De-select any previously selected cells
        for ( CellValue< ? extends Comparable< ? >> cell : this.selections ) {
            cell.setSelected( false );
            deselectCell( cell );
        }

        // Clear collection
        selections.clear();
        rangeDirection = MOVE_DIRECTION.NONE;
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
     * Extend selection from the first cell selected to the cell specified
     * 
     * @param end
     *            Extent of selection
     */
    public void extendSelection(Coordinate end) {
        if ( rangeOriginCell == null ) {
            throw new IllegalArgumentException( "origin has not been set. Unable to extend selection" );
        }
        if ( end == null ) {
            throw new IllegalArgumentException( "end cannot be null" );
        }
        clearSelection();
        CellValue< ? > endCell = data.get( end );
        selectRange( rangeOriginCell,
                     endCell );
        if ( rangeOriginCell.getCoordinate().getRow() > endCell.getCoordinate().getRow() ) {
            rangeExtentCell = selections.first();
            rangeDirection = MOVE_DIRECTION.UP;
        } else {
            rangeExtentCell = selections.last();
            rangeDirection = MOVE_DIRECTION.DOWN;
        }
    }

    /**
     * Extend selection in the specified direction
     * 
     * @param dir
     *            Direction to extend the selection
     */
    public void extendSelection(MOVE_DIRECTION dir) {
        if ( selections.size() > 0 ) {
            CellValue< ? > activeCell = (rangeExtentCell == null ? rangeOriginCell : rangeExtentCell);
            Coordinate nc = getNextCell( activeCell.getCoordinate(),
                                         dir );
            if ( nc != null ) {
                clearSelection();
                rangeDirection = dir;
                rangeExtentCell = data.get( nc );
                selectRange( rangeOriginCell,
                             rangeExtentCell );
            }
        }
    }

    /**
     * Return grid's columns
     * 
     * @return columns
     */
    public List<DynamicColumn<T>> getColumns() {
        return columns;
    }

    /**
     * Return grid's data
     * 
     * @return data
     */
    public DynamicData getData() {
        return data;
    }

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
     * Return a set of selected cells
     * 
     * @return The selected cells
     */
    public TreeSet<CellValue< ? extends Comparable< ? >>> getSelectedCells() {
        return this.selections;
    }

    /**
     * Retrieve the selected cells
     * 
     * @return The selected cells
     */
    public TreeSet<CellValue< ? extends Comparable< ? >>> getSelections() {
        return this.selections;
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
     * Return the state of merging
     * 
     * @return
     */
    public boolean isMerged() {
        return isMerged;
    }

    /**
     * Move the selected cell
     * 
     * @param dir
     *            Direction to move the selection
     * @return Dimensions of the newly selected cell
     */
    public CellExtents moveSelection(MOVE_DIRECTION dir) {
        CellExtents ce = null;
        if ( selections.size() > 0 ) {
            CellValue< ? > activeCell = (rangeExtentCell == null ? rangeOriginCell : rangeExtentCell);
            Coordinate nc = getNextCell( activeCell.getCoordinate(),
                                         dir );
            if ( nc == null ) {
                nc = activeCell.getCoordinate();
            }
            startSelecting( nc );
            rangeDirection = dir;
            ce = getSelectedCellExtents( data.get( nc ) );
        }
        return ce;
    }

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
     * Resize a column
     * 
     * @param col
     * @param width
     */
    public abstract void resizeColumn(DynamicColumn< ? > col,
                                      int width);

    /**
     * Add styling to cell to indicate a selected state
     * 
     * @param cell
     */
    public abstract void selectCell(CellValue< ? extends Comparable< ? >> cell);

    /**
     * Select a range of cells
     * 
     * @param startCell
     *            The first cell to select
     * @param endCell
     *            The last cell to select
     */
    public void selectRange(CellValue< ? > startCell,
                             CellValue< ? > endCell) {
        int col = startCell.getCoordinate().getCol();

        //Ensure startCell precedes endCell
        if ( startCell.getCoordinate().getRow() > endCell.getCoordinate().getRow() ) {
            CellValue< ? > swap = startCell;
            startCell = endCell;
            endCell = swap;
        }

        //Ensure startCell is at the top of a merged cell
        while ( startCell.getRowSpan() == 0 ) {
            startCell = data.get( startCell.getCoordinate().getRow() - 1 ).get( col );
        }

        //Ensure endCell is at the bottom of a merged cell
        while ( endCell.getRowSpan() == 0 ) {
            endCell = data.get( endCell.getCoordinate().getRow() - 1 ).get( col );
        }
        endCell = data.get( endCell.getCoordinate().getRow() + endCell.getRowSpan() - 1 ).get( col );

        //Select range
        for ( int iRow = startCell.getCoordinate().getRow(); iRow <= endCell
                .getCoordinate().getRow(); iRow++ ) {
            CellValue< ? > cell = data.get( iRow ).get( col );
            selections.add( cell );

            // Redraw selected cell
            cell.setSelected( true );
            selectCell( cell );
        }

        //Set extent of selected range according to the direction of selection
        switch ( rangeDirection ) {
            case DOWN :
                this.rangeExtentCell = this.selections.last();
                break;
            case UP :
                this.rangeExtentCell = this.selections.first();
                break;
        }
    }

    /**
     * Show a column
     */
    public abstract void showColumn(int index);

    /**
     * Select a single cell. If the cell is merged the selection is extended to
     * include all merged cells.
     * 
     * @param start
     *            The physical coordinate of the cell
     */
    public void startSelecting(Coordinate start) {
        if ( start == null ) {
            throw new IllegalArgumentException( "start cannot be null" );
        }
        clearSelection();
        CellValue< ? > startCell = data.get( start );
        selectRange( startCell,
                     startCell );
        rangeOriginCell = startCell;
        rangeExtentCell = null;
    }

    /**
     * Toggle the state of DecoratedGridWidget merging.
     * 
     * @return The state of merging after completing this call
     */
    public boolean toggleMerging() {
        if ( !isMerged ) {
            isMerged = true;
            clearSelection();
            assertModelMerging();
            redraw();
        } else {
            isMerged = false;
            clearSelection();
            removeModelMerging();
            redraw();
        }
        return isMerged;
    }

    //Get the next cell when selection moves in the specified direction
    private Coordinate getNextCell(Coordinate c,
                                   MOVE_DIRECTION dir) {
        int step = 0;
        Coordinate nc = null;
        switch ( dir ) {
            case LEFT :

                // Move left
                step = c.getCol() > 0 ? 1 : 0;
                if ( step > 0 ) {
                    nc = new Coordinate( c.getRow(),
                                         c.getCol()
                                                 - step );

                    // Skip hidden columns
                    while ( nc.getCol() > 0
                            && !columns.get( nc.getCol() ).isVisible() ) {
                        nc = new Coordinate( c.getRow(),
                                             nc.getCol()
                                                     - step );
                    }

                    //Move to top of a merged cells
                    CellValue< ? > newCell = data.get( nc );
                    while ( newCell.getRowSpan() == 0 ) {
                        nc = new Coordinate( nc.getRow() - 1,
                                             nc.getCol() );
                        newCell = data.get( nc );
                    }

                }
                break;
            case RIGHT :

                // Move right
                step = c.getCol() < columns.size() - 1 ? 1 : 0;
                if ( step > 0 ) {
                    nc = new Coordinate( c.getRow(),
                                         c.getCol()
                                                 + step );

                    // Skip hidden columns
                    while ( nc.getCol() < columns.size() - 2
                            && !columns.get( nc.getCol() ).isVisible() ) {
                        nc = new Coordinate( c.getRow(),
                                             nc.getCol()
                                                     + step );
                    }

                    //Move to top of a merged cells
                    CellValue< ? > newCell = data.get( nc );
                    while ( newCell.getRowSpan() == 0 ) {
                        nc = new Coordinate( nc.getRow() - 1,
                                             nc.getCol() );
                        newCell = data.get( nc );
                    }

                }
                break;
            case UP :

                // Move up
                step = c.getRow() > 0 ? 1 : 0;
                if ( step > 0 ) {
                    nc = new Coordinate( c.getRow()
                                                 - step,
                                         c.getCol() );

                    //Move to top of a merged cells
                    CellValue< ? > newCell = data.get( nc );
                    while ( newCell.getRowSpan() == 0 ) {
                        nc = new Coordinate( nc.getRow() - step,
                                             nc.getCol() );
                        newCell = data.get( nc );
                    }

                }
                break;
            case DOWN :

                // Move down
                step = c.getRow() < data.size() - 1 ? 1 : 0;
                if ( step > 0 ) {
                    nc = new Coordinate( c.getRow()
                                                 + step,
                                         c.getCol() );

                    //Move to top of a merged cells
                    CellValue< ? > newCell = data.get( nc );
                    while ( newCell.getRowSpan() == 0 && nc.getRow() < data.size() - 1 ) {
                        nc = new Coordinate( nc.getRow() + step,
                                             nc.getCol() );
                        newCell = data.get( nc );
                    }

                }
        }
        return nc;
    }

    //Remove merging from model
    private void removeModelMerging() {

        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                CellValue< ? > cell = data.get( iRow ).get( iCol );
                Coordinate c = new Coordinate( iRow,
                                               iCol );
                cell.setCoordinate( c );
                cell.setHtmlCoordinate( c );
                cell.setPhysicalCoordinate( c );
                cell.setRowSpan( 1 );
            }
        }

        // Set indexes after merging has been corrected
        assertModelIndexes();
    }
    
    // Given a base row find the maximum row that needs to be re-rendered based
    // upon each columns merged cells; where each merged cell passes through the
    // base row
    private int findMaxRedrawRow(int baseRowIndex) {

        if ( data.size() == 0 ) {
            return 0;
        }

        // These should never happen, but it's a safe-guard
        if ( baseRowIndex < 0 ) {
            baseRowIndex = 0;
        }
        if ( baseRowIndex > data.size() - 1 ) {
            baseRowIndex = data.size() - 1;
        }

        int maxRedrawRow = baseRowIndex;
        DynamicDataRow baseRow = data.get( baseRowIndex );
        for ( int iCol = 0; iCol < baseRow.size(); iCol++ ) {
            int iRow = baseRowIndex;
            CellValue< ? extends Comparable< ? >> cell = baseRow.get( iCol );
            while ( cell.getRowSpan() != 1
                    && iRow < data.size() - 1 ) {
                iRow++;
                DynamicDataRow row = data.get( iRow );
                cell = row.get( iCol );
            }
            maxRedrawRow = (iRow > maxRedrawRow ? iRow : maxRedrawRow);
        }
        return maxRedrawRow;
    }

    // Given a base row find the minimum row that needs to be re-rendered based
    // upon each columns merged cells; where each merged cell passes through the
    // base row
    private int findMinRedrawRow(int baseRowIndex) {

        if ( data.size() == 0 ) {
            return 0;
        }

        // These should never happen, but it's a safe-guard
        if ( baseRowIndex < 0 ) {
            baseRowIndex = 0;
        }
        if ( baseRowIndex > data.size() - 1 ) {
            baseRowIndex = data.size() - 1;
        }

        int minRedrawRow = baseRowIndex;
        DynamicDataRow baseRow = data.get( baseRowIndex );
        for ( int iCol = 0; iCol < baseRow.size(); iCol++ ) {
            int iRow = baseRowIndex;
            CellValue< ? extends Comparable< ? >> cell = baseRow.get( iCol );
            while ( cell.getRowSpan() != 1
                    && iRow > 0 ) {
                iRow--;
                DynamicDataRow row = data.get( iRow );
                cell = row.get( iCol );
            }
            minRedrawRow = (iRow < minRedrawRow ? iRow : minRedrawRow);
        }
        return minRedrawRow;
    }


}
