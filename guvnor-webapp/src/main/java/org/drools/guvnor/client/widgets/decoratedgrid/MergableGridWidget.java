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

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue.GroupedCellValue;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

/**
 * An abstract grid of data. Implementations can choose the orientation to
 * render "rows" and "columns" (e.g. some may transpose the normal meaning to
 * provide a horizontal implementation of normally vertical tabular data)
 */
public abstract class MergableGridWidget<T> extends Widget
    implements
    ValueUpdater<Object>,
    HasSelectedCellChangeHandlers,
    HasRowGroupingChangeHandlers {

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
    protected TreeSet<CellValue< ? extends Comparable< ? >>> selections                 = new TreeSet<CellValue< ? extends Comparable< ? >>>(
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
    protected static final Constants                         messages                   = GWT.create( Constants.class );
    protected static final DecisionTableResources            resource                   = GWT.create( DecisionTableResources.class );
    protected static final DecisionTableStyle                style                      = resource.cellTableStyle();

    private static final ImageResource                       selectorGroupedCells       = resource.selectorDelete();
    private static final ImageResource                       selectorUngroupedCells     = resource.selectorAdd();
    protected static final String                            selectorGroupedCellsHtml   = makeImageHtml( selectorGroupedCells );
    protected static final String                            selectorUngroupedCellsHtml = makeImageHtml( selectorUngroupedCells );

    private static String makeImageHtml(ImageResource image) {
        return AbstractImagePrototype.create( image ).getHTML();
    }

    // Data and columns to render
    protected List<DynamicColumn<T>> columns              = new ArrayList<DynamicColumn<T>>();
    protected DynamicData            data                 = new DynamicData();

    //Properties for multi-cell selection
    protected CellValue< ? >         rangeOriginCell;
    protected CellValue< ? >         rangeExtentCell;

    protected MOVE_DIRECTION         rangeDirection       = MOVE_DIRECTION.NONE;
    protected boolean                bDragOperationPrimed = false;
    protected boolean                isMerged             = false;

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
     * Add a handler for RowGroupingChangeEvents
     */
    public HandlerRegistration addRowGroupingChangeHandler(RowGroupingChangeHandler handler) {
        return addHandler( handler,
                           RowGroupingChangeEvent.getType() );
    }

    /**
     * Add a handler for SelectedCellChangeEvents
     */
    public HandlerRegistration addSelectedCellChangeHandler(SelectedCellChangeHandler handler) {
        return addHandler( handler,
                           SelectedCellChangeEvent.getType() );
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

                        CellValue< ? extends Comparable< ? >> cell = data.get( newRow ).get( newCol );
                        cell.setPhysicalCoordinate( new Coordinate( iRow,
                                                                    iCol ) );

                    } else {
                        DynamicDataRow priorRow = data.get( iRow - 1 );
                        CellValue< ? extends Comparable< ? >> priorCell = priorRow.get( iCol );
                        Coordinate priorHtmlCoordinate = priorCell.getHtmlCoordinate();
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
     * Delete a column
     * 
     * @param column
     *            Column to delete
     * @param bRedraw
     *            Should grid be redrawn
     */
    public void deleteColumn(DynamicColumn<T> column,
                             boolean bRedraw) {

        //Find index of column
        int index = columns.indexOf( column );
        if ( index == -1 ) {
            throw new IllegalArgumentException(
                                                "Column not found in declared columns." );
        }

        // Clear any selections
        clearSelection();

        //Expand any merged cells in colum
        boolean bRedrawSidebar = false;
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            CellValue< ? > cv = data.get( iRow ).get( index );
            if ( cv.isGrouped() ) {
                removeModelGrouping( cv,
                                     false );
                bRedrawSidebar = true;
            }
        }

        // Delete column data
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            row.remove( index );
        }

        // Delete column from grid
        columns.remove( index );
        reindexColumns();

        // Redraw
        if ( bRedraw ) {
            assertModelIndexes();
            redraw();
            if ( bRedrawSidebar ) {
                RowGroupingChangeEvent.fire( this );
            }
        }

    }

    /**
     * Delete the given row. Partial redraw.
     * 
     * @param row
     */
    public void deleteRow(DynamicDataRow row) {

        //Find index of row
        int index = data.indexOf( row );
        if ( index == -1 ) {
            throw new IllegalArgumentException(
                                                "DynamicDataRow does not exist in table data." );
        }

        // Clear any selections
        clearSelection();

        //Delete row data
        data.remove( index );

        // Partial redraw
        if ( !isMerged() ) {
            // Single row when not merged
            removeRowElement( index );
            assertModelIndexes();
        } else {
            // Affected rows when merged
            removeRowElement( index );

            if ( data.size() > 0 ) {
                assertModelMerging();
                int minRedrawRow = findMinRedrawRow( index - 1 );
                int maxRedrawRow = findMaxRedrawRow( index - 1 ) + 1;
                if ( maxRedrawRow > data.size() - 1 ) {
                    maxRedrawRow = data.size() - 1;
                }
                redrawRows( minRedrawRow,
                                       maxRedrawRow );
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
     * Insert a column before another
     * 
     * @param columnBefore
     *            The column before which the new column should be inserted
     * @param newColumn
     *            Column definition
     * @param columnData
     *            Data for column
     * @param bRedraw
     *            Should grid be redrawn
     */
    public void insertColumnBefore(DynamicColumn<T> columnBefore,
                                   DynamicColumn<T> newColumn,
                                   List<CellValue< ? >> columnData,
                                   boolean bRedraw) {

        if ( newColumn == null ) {
            throw new IllegalArgumentException( "newColumn cannot be null" );
        }
        if ( columnData == null ) {
            throw new IllegalArgumentException( "columnData cannot be null" );
        }
        if ( columnData.size() != data.size() ) {
            throw new IllegalArgumentException( "columnData contains a different number of rows to the grid" );
        }

        //Find index of new column
        int index = columns.size();
        if ( columnBefore != null ) {
            index = columns.indexOf( columnBefore );
            if ( index == -1 ) {
                throw new IllegalArgumentException(
                                                    "columnBefore does not exist in table data." );
            }
            index++;
        }

        // Clear any selections
        clearSelection();

        // Add column definition
        columns.add( index,
                     newColumn );
        reindexColumns();

        // Add column data
        for ( int iRow = 0; iRow < columnData.size(); iRow++ ) {
            CellValue< ? > cv = columnData.get( iRow );
            data.get( iRow ).add( index,
                                  cv );
        }
        assertModelIndexes();

        // Redraw
        if ( bRedraw ) {
            redrawColumns( index,
                           columns.size() - 1 );
        }

    }

    /**
     * Insert the given row before the provided index. Partial redraw.
     * 
     * @param rowBefore
     *            The row before which the new row should be inserted
     * @param rowData
     *            The row of data to insert
     */
    public void insertRowBefore(DynamicDataRow rowBefore,
                                         DynamicDataRow rowData) {

        if ( rowData == null ) {
            throw new IllegalArgumentException( "Row data cannot be null" );
        }
        if ( rowData.size() != columns.size() ) {
            throw new IllegalArgumentException( "rowData contains a different number of columns to the grid" );
        }

        //Find index of row
        int index = data.size();
        if ( rowBefore != null ) {
            index = data.indexOf( rowBefore );
            if ( index == -1 ) {
                throw new IllegalArgumentException(
                                                    "rowBefore does not exist in table data." );
            }
        }

        // Clear any selections
        clearSelection();

        // Find rows that need to be (re)drawn
        int minRedrawRow = index;
        int maxRedrawRow = index;
        if ( isMerged() ) {
            if ( index < data.size() ) {
                minRedrawRow = findMinRedrawRow( index );
                maxRedrawRow = findMaxRedrawRow( index ) + 1;
            } else {
                minRedrawRow = findMinRedrawRow( (index > 0 ? index - 1 : index) );
                maxRedrawRow = index;
            }
        }

        data.add( index,
                  rowData );

        // Partial redraw
        if ( !isMerged() ) {
            // Only new row when not merged
            assertModelIndexes();
            createRowElement( index,
                              rowData );
        } else {
            // Affected rows when merged
            assertModelMerging();
            createEmptyRowElement( index );
            redrawRows( minRedrawRow,
                        maxRedrawRow );
        }

    }

    /**
     * Return the state of merging
     * 
     * @return
     */
    public boolean isMerged() {
        return isMerged;
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
            removeModelGrouping();
            removeModelMerging();
            redraw();
            RowGroupingChangeEvent.fire( this );
        }
        return isMerged;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.ValueUpdater#update(java.lang.Object)
     */
    public void update(Object value) {

        boolean bUngroupCells = false;
        TreeSet<CellValue< ? extends Comparable< ? >>> selections = getSelectedCells();

        //If selections span multiple cells, any of which are grouped we should ungroup them
        if ( selections.size() > 1 ) {
            for ( CellValue< ? extends Comparable< ? >> cell : selections ) {
                if ( cell instanceof GroupedCellValue ) {
                    bUngroupCells = true;
                    break;
                }
            }
        }

        // Update underlying data (update before ungrouping as selections would need to be expanded too)
        for ( CellValue< ? extends Comparable< ? >> cell : selections ) {
            Coordinate c = cell.getCoordinate();
            if ( !columns.get( c.getCol() ).isSystemControlled() ) {
                data.set( c,
                          value );
            }
        }

        //Ungroup if applicable
        if ( bUngroupCells ) {
            for ( CellValue< ? extends Comparable< ? >> cell : selections ) {
                if ( cell instanceof GroupedCellValue ) {
                    removeModelGrouping( cell,
                                         false );
                }
            }
        }

        // Partial redraw
        assertModelMerging();
        int baseRowIndex = selections.first().getCoordinate().getRow();
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
        Coordinate selection = selections.first().getCoordinate();
        clearSelection();
        //redrawRows( minRedrawRow,
        //            maxRedrawRow );
        redraw();

        //Re-select applicable cells, following change to merge
        startSelecting( selection );
    }

    //Apply grouping by collapsing applicable rows
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void applyModelGrouping(CellValue< ? > startCell,
                                    boolean bRedraw) {

        int startRowIndex = startCell.getCoordinate().getRow();
        int endRowIndex = findMergedCellExtent( startCell.getCoordinate() ).getRow();
        int colIndex = startCell.getCoordinate().getCol();

        GroupedCellValue groupedCell;
        DynamicDataRow row = data.get( startRowIndex );
        GroupedDynamicDataRow groupedRow = new GroupedDynamicDataRow();
        for ( int iCol = 0; iCol < row.size(); iCol++ ) {
            if ( iCol == colIndex || hasMultipleValues( startRowIndex,
                                                        endRowIndex,
                                                        iCol ) ) {
                groupedCell = row.get( iCol ).convertToGroupedCell();
                groupedCell.setGrouped( (iCol == colIndex) );
                groupedRow.add( groupedCell );
            } else {
                groupedRow.add( row.get( iCol ) );
            }
        }
        for ( int iRow = startRowIndex; iRow <= endRowIndex; iRow++ ) {
            DynamicDataRow childRow = data.get( startRowIndex );
            groupedRow.addChildRow( childRow );
            data.remove( childRow );
        }
        data.remove( row );
        data.add( startRowIndex,
                  groupedRow );

        assertModelMerging();

        if ( bRedraw ) {
            redraw();
            RowGroupingChangeEvent.fire( this );
        }

    }

    //Clear all selections
    private void clearSelection() {
        // De-select any previously selected cells
        for ( CellValue< ? extends Comparable< ? >> cell : this.selections ) {
            cell.setSelected( false );
            deselectCell( cell );
        }

        // Clear collection
        selections.clear();
        rangeDirection = MOVE_DIRECTION.NONE;
    }

    //Check whether two values are equal or both null
    private boolean equalOrNull(Object o1,
                                Object o2) {
        if ( o1 == null && o2 == null ) {
            return true;
        }
        if ( o1 != null && o2 == null ) {
            return false;
        }
        if ( o1 == null && o2 != null ) {
            return false;
        }
        return o1.equals( o2 );
    }

    //Expand a grouped row and return a list of expanded rows
    private List<DynamicDataRow> expandGroupedRow(DynamicDataRow row,
                                                   boolean bRecursive) {

        List<DynamicDataRow> ungroupedRows = new ArrayList<DynamicDataRow>();

        if ( row instanceof GroupedDynamicDataRow ) {

            GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;
            for ( int iChildRow = 0; iChildRow < groupedRow.getChildRows().size(); iChildRow++ ) {
                DynamicDataRow childRow = groupedRow.getChildRows().get( iChildRow );

                if ( bRecursive ) {
                    if ( childRow instanceof GroupedDynamicDataRow ) {
                        List<DynamicDataRow> expandedRow = expandGroupedRow( childRow,
                                                                              bRecursive );
                        ungroupedRows.addAll( expandedRow );
                    } else {
                        ungroupCells( childRow );
                        ungroupedRows.add( childRow );
                    }
                } else {
                    ungroupedRows.add( childRow );
                }
            }
        } else {
            ungroupCells( row );
            ungroupedRows.add( row );
        }

        return ungroupedRows;
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

    //Find the bottom coordinate of a merged cell
    private Coordinate findMergedCellExtent(Coordinate c) {
        if ( c.getRow() == data.size() - 1 ) {
            return c;
        }
        Coordinate nc = new Coordinate( c.getRow() + 1,
                                        c.getCol() );
        CellValue< ? > newCell = data.get( nc );
        while ( newCell.getRowSpan() == 0 && nc.getRow() < data.size() - 1 ) {
            nc = new Coordinate( nc.getRow() + 1,
                                     nc.getCol() );
            newCell = data.get( nc );
        }
        if ( newCell.getRowSpan() != 0 ) {
            nc = new Coordinate( nc.getRow() - 1,
                                     nc.getCol() );
            newCell = data.get( nc );
        }
        return nc;
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

    //Get the next cell when selection moves in the specified direction
    private Coordinate getNextCell(Coordinate c,
                                    MOVE_DIRECTION dir) {

        int step = 0;
        Coordinate nc = c;

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
                    if ( newCell.getRowSpan() == 0 && nc.getRow() == data.size() - 1 ) {
                        nc = c;
                    }

                }
        }
        return nc;
    }

    private boolean hasMultipleValues(int startRowIndex,
                                      int endRowIndex,
                                      int colIndex) {
        Object value1 = data.get( startRowIndex ).get( colIndex ).getValue();
        for ( int iRow = startRowIndex + 1; iRow <= endRowIndex; iRow++ ) {
            Object value2 = data.get( iRow ).get( colIndex ).getValue();
            if ( !equalOrNull( value1,
                               value2 ) ) {
                return true;
            }
        }
        return false;
    }

    //Merge between the two provided cells
    private void mergeCells(CellValue< ? > cell1,
                            CellValue< ? > cell2) {
        int iStartRowIndex = cell1.getCoordinate().getRow();
        int iEndRowIndex = cell2.getCoordinate().getRow();
        int iColIndex = cell1.getCoordinate().getCol();

        //Any rows that are grouped need row span of zero
        for ( int iRow = iStartRowIndex; iRow < iEndRowIndex; iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            row.get( iColIndex ).setRowSpan( 0 );
        }
        cell1.setRowSpan( iEndRowIndex - iStartRowIndex );

    }

    // Re-index columns
    private void reindexColumns() {
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<T> col = columns.get( iCol );
            col.setColumnIndex( iCol );
        }
    }

    //Remove all grouping throughout the model
    private void removeModelGrouping() {

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            if ( row instanceof GroupedDynamicDataRow ) {
                List<DynamicDataRow> expandedRow = expandGroupedRow( row,
                                                                      true );
                data.remove( iRow );
                data.addAll( iRow,
                             expandedRow );
                iRow = iRow + expandedRow.size() - 1;
            }
        }

    }

    //Remove grouping by expanding applicable rows
    @SuppressWarnings("rawtypes")
    private void removeModelGrouping(CellValue< ? > startCell,
                                     boolean bRedraw) {

        int startRow = startCell.getCoordinate().getRow();

        startCell.setGrouped( false );

        boolean bRecursive = true;
        DynamicDataRow row = data.get( startRow );

        for ( int iCol = 0; iCol < row.size(); iCol++ ) {
            CellValue< ? > cv = row.get( iCol );
            if ( cv instanceof GroupedCellValue ) {
                bRecursive = !(bRecursive ^ ((GroupedCellValue) cv).hasMultipleValues());
            }
        }

        List<DynamicDataRow> expandedRow = expandGroupedRow( row,
                                                             bRecursive );
        data.remove( startRow );
        data.addAll( startRow,
                     expandedRow );

        assertModelMerging();
        if ( bRedraw ) {
            redraw();
            RowGroupingChangeEvent.fire( this );
        }
    }

    //Remove merging from model
    private void removeModelMerging() {

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );

            for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
                CellValue< ? > cell = row.get( iCol );
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

    //Initialise cell parameters when ungrouped
    private void ungroupCells(DynamicDataRow row) {
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            CellValue< ? > cell = row.get( iCol );
            cell.setGrouped( false );
        }
    }

    protected abstract void createEmptyRowElement(int index);

    protected abstract void createRowElement(int index,
                                             DynamicDataRow rowData);

    //Check whether "Grouping" widget has been clicked
    protected boolean isGroupWidgetClicked(Event event,
                                           Element target) {
        String eventType = event.getType();
        if ( eventType.equals( "mousedown" ) ) {
            String tagName = target.getTagName();
            if ( "img".equalsIgnoreCase( tagName ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Redraw table rows. Partial redraw
     * 
     * @param startRedrawIndex
     *            Start row index (inclusive)
     * @param endRedrawIndex
     *            End row index (inclusive)
     */
    protected abstract void redrawRows(int startRedrawIndex,
                                       int endRedrawIndex);

    protected abstract void removeRowElement(int index);

    /**
     * Ensure merging is reflected in the entire model
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    void assertModelMerging() {

        //Remove merging first as it initialises all coordinates
        removeModelMerging();

        //Only apply merging if merged
        if ( isMerged ) {

            int minRowIndex = 0;
            int maxRowIndex = data.size();

            //Add an empty row to the end of the data to simplify detection of merged cells that run to the end of the table
            DynamicDataRow blankRow = new DynamicDataRow();
            for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
                CellValue cv = new CellValue( null,
                                              maxRowIndex,
                                              iCol );
                blankRow.add( cv );
            }
            data.add( blankRow );
            maxRowIndex++;

            //Look in columns for cells with identical values
            for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
                CellValue< ? > cell1 = data.get( minRowIndex ).get( iCol );
                CellValue< ? > cell2 = null;
                for ( int iRow = minRowIndex + 1; iRow < maxRowIndex; iRow++ ) {
                    cell1.setRowSpan( 1 );
                    cell2 = data.get( iRow ).get( iCol );

                    //Don't merge if either cell is empty
                    boolean bMerge = false;
                    boolean cell1HasMultipleValues = false;
                    boolean cell2HasMultipleValues = false;
                    boolean cell1IsGrouped = false;
                    boolean cell2IsGrouped = false;

                    if ( cell1 instanceof GroupedCellValue ) {
                        cell1HasMultipleValues = ((GroupedCellValue) cell1).hasMultipleValues();
                        cell1IsGrouped = true;
                    }
                    if ( cell2 instanceof GroupedCellValue ) {
                        cell2HasMultipleValues = ((GroupedCellValue) cell2).hasMultipleValues();
                        cell2IsGrouped = true;
                    }
                    if ( !cell1HasMultipleValues && cell2HasMultipleValues ) {
                        bMerge = true;
                    }
                    if ( cell1HasMultipleValues && !cell2HasMultipleValues ) {
                        bMerge = true;
                    }
                    if ( cell1IsGrouped && cell2IsGrouped ) {
                        bMerge = true;
                    }
                    if ( !cell1HasMultipleValues && !cell2HasMultipleValues ) {
                        if ( cell1IsGrouped && !cell2IsGrouped ) {
                            bMerge = true;
                        } else if ( !cell1IsGrouped && cell2IsGrouped ) {
                            bMerge = true;
                        }
                    }
                    if ( cell1.isEmpty() && !cell2.isEmpty() ) {
                        bMerge = true;
                    } else if ( !cell1.isEmpty() && cell2.isEmpty() ) {
                        bMerge = true;
                    } else if ( cell1.isEmpty() && cell2.isEmpty() ) {
                        cell1 = cell2;
                    } else if ( !cell1.getValue().equals( cell2.getValue() ) ) {
                        bMerge = true;
                    }
                    if ( bMerge ) {
                        mergeCells( cell1,
                                    cell2 );
                        cell1 = cell2;
                    }

                }
            }

            //Remove dummy blank row
            data.remove( blankRow );

        }

        // Set indexes after merging has been corrected
        assertModelIndexes();

    }

    /**
     * Remove styling indicating a selected state
     * 
     * @param cell
     */
    abstract void deselectCell(CellValue< ? extends Comparable< ? >> cell);

    /**
     * Extend selection from the first cell selected to the cell specified
     * 
     * @param end
     *            Extent of selection
     */
    void extendSelection(Coordinate end) {
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
    void extendSelection(MOVE_DIRECTION dir) {
        if ( selections.size() > 0 ) {
            CellValue< ? > activeCell = (rangeExtentCell == null ? rangeOriginCell : rangeExtentCell);
            Coordinate nc = getNextCell( activeCell.getCoordinate(),
                                         dir );
            clearSelection();
            rangeDirection = dir;
            rangeExtentCell = data.get( nc );
            selectRange( rangeOriginCell,
                             rangeExtentCell );

        }
    }

    /**
     * Retrieve the extents of a cell
     * 
     * @param cv
     *            The cell for which to retrieve the extents
     * @return
     */
    CellExtents getSelectedCellExtents(
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
    TreeSet<CellValue< ? extends Comparable< ? >>> getSelectedCells() {
        return this.selections;
    }

    /**
     * Group a merged cell. If the cell is not merged across at least two rows
     * or the cell is not the top of the merged range no action is taken.
     * 
     * @param start
     *            Coordinate of top of merged group.
     */
    void groupCells(Coordinate start) {
        if ( start == null ) {
            throw new IllegalArgumentException( "start cannot be null" );
        }
        CellValue< ? > startCell = data.get( start );

        //Start cell needs to be top of a merged range
        if ( startCell.getRowSpan() <= 1 && !startCell.isGrouped() ) {
            return;
        }

        clearSelection();
        if ( startCell.isGrouped() ) {
            removeModelGrouping( startCell,
                                 true );
        } else {
            applyModelGrouping( startCell,
                                true );
        }

    }

    /**
     * Hide a column
     */
    abstract void hideColumn(int index);

    /**
     * Move the selected cell
     * 
     * @param dir
     *            Direction to move the selection
     * @return Dimensions of the newly selected cell
     */
    CellExtents moveSelection(MOVE_DIRECTION dir) {
        CellExtents ce = null;
        if ( selections.size() > 0 ) {
            CellValue< ? > activeCell = (rangeExtentCell == null ? rangeOriginCell : rangeExtentCell);
            Coordinate nc = getNextCell( activeCell.getCoordinate(),
                                         dir );
            startSelecting( nc );
            rangeDirection = dir;
            ce = getSelectedCellExtents( data.get( nc ) );
        }
        return ce;
    }

    /**
     * Resize a column
     * 
     * @param col
     * @param width
     */
    abstract void resizeColumn(DynamicColumn< ? > col,
                                      int width);

    /**
     * Add styling to cell to indicate a selected state
     * 
     * @param cell
     */
    abstract void selectCell(CellValue< ? extends Comparable< ? >> cell);

    /**
     * Select a range of cells
     * 
     * @param startCell
     *            The first cell to select
     * @param endCell
     *            The last cell to select
     */
    void selectRange(CellValue< ? > startCell,
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
        Coordinate nc = findMergedCellExtent( endCell.getCoordinate() );
        endCell = data.get( nc );

        //Select range
        for ( int iRow = startCell.getCoordinate().getRow(); iRow <= endCell.getCoordinate().getRow(); iRow++ ) {
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
    abstract void showColumn(int index);

    /**
     * Select a single cell. If the cell is merged the selection is extended to
     * include all merged cells.
     * 
     * @param start
     *            The physical coordinate of the cell
     */
    void startSelecting(Coordinate start) {
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

}
