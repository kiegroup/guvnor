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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;
import org.drools.guvnor.client.widgets.decoratedgrid.MergableGridWidget.CellExtents;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Abstract grid, decorated with DecoratedGridHeaderWidget and
 * DecoratedGridSidebarWidget encapsulating basic operation: keyboard navigation
 * and column resizing.
 * 
 * @param <T>
 *            The type of domain columns represented by the Grid
 */
public abstract class DecoratedGridWidget<T> extends Composite
    implements
        ValueUpdater<Object> {

    // Enum to support keyboard navigation
    public enum MOVE_DIRECTION {
        LEFT, RIGHT, UP, DOWN
    }

    // Widgets for UI
    protected Panel                                        mainPanel;
    protected Panel                                        bodyPanel;
    protected FocusPanel                                   mainFocusPanel;
    protected ScrollPanel                                  scrollPanel;
    protected MergableGridWidget<T>                        gridWidget;
    protected DecoratedGridHeaderWidget<T>                 headerWidget;
    protected DecoratedGridSidebarWidget<T>                sidebarWidget;
    protected HasSystemControlledColumns                   hasSystemControlledColumns;
    protected boolean                                      isMerged   = false;

    // Decision Table data
    protected DynamicData                                  data       = new DynamicData();
    protected List<DynamicColumn<T>>                       columns    = new ArrayList<DynamicColumn<T>>();
    protected int                                          height;
    protected int                                          width;

    // Resources
    protected static final DecisionTableResources          resource   = GWT
                                                                              .create( DecisionTableResources.class );
    protected static final DecisionTableStyle              style      = resource.cellTableStyle();

    // Selections store the actual grid data selected (irrespective of
    // merged cells). So a merged cell spanning 2 rows is stored as 2
    // selections. Selections are ordered by row number so we can
    // iterate top to bottom.
    private TreeSet<CellValue< ? extends Comparable< ? >>> selections = new TreeSet<CellValue< ? extends Comparable< ? >>>(
                                                                                                                            new Comparator<CellValue< ? extends Comparable< ? >>>() {

                                                                                                                                public int compare(CellValue< ? extends Comparable< ? >> o1,
                                                                                                                                                   CellValue< ? extends Comparable< ? >> o2) {
                                                                                                                                    return o1.getPhysicalCoordinate().getRow()
                                                                                                                                           - o2.getPhysicalCoordinate().getRow();
                                                                                                                                }

                                                                                                                            } );

    /**
     * Construct at empty DecoratedGridWidget, without DecoratedGridHeaderWidget
     * or DecoratedGridSidebarWidget These should be set before the grid is
     * displayed using setHeaderWidget and setSidebarWidget respectively.
     */
    public DecoratedGridWidget() {

        mainPanel = getMainPanel();
        bodyPanel = getBodyPanel();
        gridWidget = getGridWidget();
        if ( mainPanel == null ) {
            throw new IllegalArgumentException( "mainPanel cannot be null" );
        }
        if ( bodyPanel == null ) {
            throw new IllegalArgumentException( "bodyPanel cannot be null" );
        }
        if ( gridWidget == null ) {
            throw new IllegalArgumentException( "gridWidget cannot be null" );
        }

        scrollPanel = new ScrollPanel();
        scrollPanel.add( gridWidget );
        scrollPanel.addScrollHandler( getScrollHandler() );

        mainFocusPanel = new FocusPanel( mainPanel );
        initWidget( mainFocusPanel );
    }

    /**
     * Append a column to the end of the column list
     * 
     * @param column
     * @param columnData
     * @param bRedraw
     *            Redraw the grid after the column has been appended
     */
    public void appendColumn(DynamicColumn<T> column,
                             List<CellValue< ? >> columnData,
                             boolean bRedraw) {
        if ( column == null ) {
            throw new IllegalArgumentException(
                                                "Column cannot be null." );
        }
        if ( columnData == null ) {
            throw new IllegalArgumentException( "columnData cannot be null" );
        }
        if ( columnData.size() != data.size() ) {
            throw new IllegalArgumentException( "columnData contains a different number of rows to the grid" );
        }
        insertColumnBefore( null,
                            column,
                            columnData,
                            bRedraw );
    }

    /**
     * Append a row to the end of the grid
     * 
     * @param row
     */
    public void appendRow(DynamicDataRow row) {
        if ( row == null ) {
            throw new IllegalArgumentException( "row cannot be null" );
        }
        clearSelection();
        insertRowBefore( null,
                         row );
    }

    /**
     * Resize the DecoratedGridHeaderWidget and DecoratedGridSidebarWidget when
     * DecoratedGridWidget shows scrollbars
     */
    public void assertDimensions() {
        headerWidget.setWidth( scrollPanel.getElement().getClientWidth()
                               + "px" );
        sidebarWidget.setHeight( scrollPanel.getElement().getClientHeight()
                                 + "px" );
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
     * Delete the given column
     * 
     * @param column
     */
    public void deleteColumn(DynamicColumn<T> column) {
        if ( column == null ) {
            throw new IllegalArgumentException(
                                                "Column cannot be null." );
        }
        deleteColumn( column,
                      true );
    }

    /**
     * Delete the given row
     * 
     * @param row
     */
    public void deleteRow(DynamicDataRow row) {
        if ( row == null ) {
            throw new IllegalArgumentException( "row cannot be null" );
        }
        int index = data.indexOf( row );
        if ( index == -1 ) {
            throw new IllegalArgumentException(
                                                "DynamicDataRow does not exist in table data." );
        }
        clearSelection();

        data.remove( index );

        // Partial redraw
        if ( !isMerged ) {
            // Single row when not merged
            gridWidget.deleteRow( index );
            assertModelIndexes();
        } else {
            // Affected rows when merged
            gridWidget.deleteRow( index );

            if ( data.size() > 0 ) {
                assertModelMerging();
                int minRedrawRow = findMinRedrawRow( index - 1 );
                int maxRedrawRow = findMaxRedrawRow( index - 1 ) + 1;
                if ( maxRedrawRow > data.size() - 1 ) {
                    maxRedrawRow = data.size() - 1;
                }
                gridWidget.redrawRows( minRedrawRow,
                                       maxRedrawRow );
            }
        }

        assertDimensions();

    }

    /**
     * Get the DecoratedGridWidget inner panel to which the
     * DecoratedGridHeaderWidget will be added. This allows subclasses to have
     * some control over the internal layout of the grid.
     * 
     * @return
     */
    public abstract Panel getBodyPanel();

    /**
     * Return the DecoratedGridWidget columns
     * 
     * @return The columns
     */
    public List<DynamicColumn<T>> getColumns() {
        return this.columns;
    }

    /**
     * Return the DecoratedGridWidget data
     * 
     * @return The grid data
     */
    public DynamicData getData() {
        return this.data;
    }

    /**
     * Return the Widget responsible for rendering the DecoratedGridWidget
     * "grid".
     * 
     * @return
     */
    public abstract MergableGridWidget<T> getGridWidget();

    /**
     * Return the Widget responsible for rendering the DecoratedGridWidget
     * "header".
     * 
     * @return
     */
    public DecoratedGridHeaderWidget<T> getHeaderWidget() {
        return headerWidget;
    }

    /**
     * Return the DecoratedGridWidget outer most panel to which all child
     * widgets is added. This allows subclasses to have some control over the
     * internal layout of the grid.
     * 
     * @return
     */
    public abstract Panel getMainPanel();

    /**
     * Return the ScrollPanel in which the DecoratedGridWidget "grid" is nested.
     * This allows ScrollEvents to be hooked up to other defendant controls
     * (e.g. the Header).
     * 
     * @return
     */
    public abstract ScrollHandler getScrollHandler();

    /**
     * Retrieve the selected cells
     * 
     * @return The selected cells
     */
    public TreeSet<CellValue< ? extends Comparable< ? >>> getSelections() {
        return this.selections;
    }

    /**
     * Return the Widget responsible for rendering the DecoratedGridWidget
     * "sidebar".
     * 
     * @return
     */
    public DecoratedGridSidebarWidget<T> getSidebarWidget() {
        return sidebarWidget;
    }

    /**
     * Insert a column before that specified
     * 
     * @param columnBefore
     * @param newColumn
     * @param columnData
     * @param bRedraw
     *            Redraw the grid after the column has been inserted
     */
    public void insertColumnBefore(DynamicColumn<T> columnBefore,
                                   DynamicColumn<T> newColumn,
                                   List<CellValue< ? >> columnData,
                                   boolean bRedraw) {

        int index = columns.size();
        if ( columnBefore != null ) {
            index = columns.indexOf( columnBefore );
            if ( index == -1 ) {
                throw new IllegalArgumentException(
                                                    "columnBefore does not exist in table data." );
            }
            index++;
        }
        if ( newColumn == null ) {
            throw new IllegalArgumentException( "newColumn cannot be null" );
        }
        if ( columnData == null ) {
            throw new IllegalArgumentException( "columnData cannot be null" );
        }
        if ( columnData.size() != data.size() ) {
            throw new IllegalArgumentException( "columnData contains a different number of rows to the grid" );
        }

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
            gridWidget.redrawColumns( index,
                                      columns.size() - 1 );
            headerWidget.redraw();
            assertDimensions();
        }

    }

    /**
     * Insert a row before that specified
     * 
     * @param rowBefore
     * @param newRow
     */
    public void insertRowBefore(DynamicDataRow rowBefore,
                                DynamicDataRow newRow) {
        int index = data.size();
        if ( rowBefore != null ) {
            index = data.indexOf( rowBefore );
            if ( index == -1 ) {
                throw new IllegalArgumentException(
                                                    "rowBefore does not exist in table data." );
            }
        }
        if ( newRow == null ) {
            throw new IllegalArgumentException( "newRow cannot be null" );
        }
        if ( newRow.size() != columns.size() ) {
            throw new IllegalArgumentException( "newRow contains a different number of columns to the grid" );
        }

        // Find rows that need to be (re)drawn
        int minRedrawRow = index;
        int maxRedrawRow = index;
        if ( isMerged ) {
            if ( index < data.size() ) {
                minRedrawRow = findMinRedrawRow( index );
                maxRedrawRow = findMaxRedrawRow( index ) + 1;
            } else {
                minRedrawRow = findMinRedrawRow( (index > 0 ? index - 1 : index) );
                maxRedrawRow = index;
            }
        }

        data.add( index,
                  newRow );

        // Partial redraw
        if ( !isMerged ) {
            // Only new row when not merged
            assertModelIndexes();
            gridWidget.insertRowBefore( index,
                                        newRow );
        } else {
            // Affected rows when merged
            assertModelMerging();

            // This row is overwritten by the call to redrawRows()
            gridWidget.insertRowBefore( index,
                                        newRow );
            gridWidget.redrawRows( minRedrawRow,
                                   maxRedrawRow );
        }

        assertDimensions();

    }

    /**
     * Return the state of merging
     * 
     * @return
     */
    public boolean isMerged() {
        return isMerged;
    }

    private Coordinate getNextCell(MOVE_DIRECTION dir) {
        int step = 0;
        Coordinate nc = null;
        CellExtents ce = null;
        Coordinate c = selections.first().getCoordinate();
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

                    // Ensure cell is visible
                    ce = gridWidget.getSelectedCellExtents( selections.first() );
                    if ( ce.getOffsetX() < scrollPanel
                            .getHorizontalScrollPosition() ) {
                        scrollPanel
                                .setHorizontalScrollPosition( ce.getOffsetX() );
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

                    // Ensure cell is visible
                    ce = gridWidget.getSelectedCellExtents( selections.first() );
                    int scrollWidth = scrollPanel.getElement().getClientWidth();
                    if ( ce.getOffsetX()
                         + ce.getWidth() > scrollWidth
                                                           + scrollPanel.getHorizontalScrollPosition() ) {
                        int delta = ce.getOffsetX()
                                    + ce.getWidth()
                                    - scrollPanel.getHorizontalScrollPosition()
                                    - scrollWidth;
                        scrollPanel.setHorizontalScrollPosition( scrollPanel
                                .getHorizontalScrollPosition()
                                                                 + delta );
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
                    CellValue< ? > newCell = data.get( c );
                    while ( newCell.getRowSpan() == 0 ) {
                        nc = new Coordinate( c.getRow()
                                                     - step,
                                             c.getCol() );
                        newCell = data.get( c );
                    }

                    // Ensure cell is visible
                    ce = gridWidget.getSelectedCellExtents( selections.first() );
                    if ( ce.getOffsetY() < scrollPanel.getScrollPosition() ) {
                        scrollPanel.setScrollPosition( ce.getOffsetY() );
                    }
                }
                break;
            case DOWN :

                // Move down
                CellValue< ? > currentCell = data.get( c );
                step = c.getRow() < data.size() - 1 ? currentCell.getRowSpan() : 0;
                if ( step > 0 ) {
                    nc = new Coordinate( c.getRow()
                                                 + step,
                                         c.getCol() );

                    // Ensure cell is visible
                    ce = gridWidget.getSelectedCellExtents( selections.first() );
                    int scrollHeight = scrollPanel.getElement()
                            .getClientHeight();
                    if ( ce.getOffsetY()
                         + ce.getHeight() > scrollHeight
                                                            + scrollPanel.getScrollPosition() ) {
                        int delta = ce.getOffsetY()
                                    + ce.getHeight()
                                    - scrollPanel.getScrollPosition()
                                    - scrollHeight;
                        scrollPanel.setScrollPosition( scrollPanel
                                .getScrollPosition()
                                                       + delta );
                    }
                }
        }
        return nc;
    }

    /**
     * Move the selected cell
     * 
     * @param dir
     *            Direction to move the selection
     */
    public void moveSelection(MOVE_DIRECTION dir) {
        if ( selections.size() > 0 ) {
            Coordinate nc = getNextCell( dir );
            startSelecting( nc );
        }
    }

    /**
     * Redraw any columns that have their values programmatically manipulated
     */
    public void redrawSystemControlledColumns() {
        for ( DynamicColumn< ? > col : columns ) {
            if ( col.isSystemControlled() ) {
                gridWidget.redrawColumn( col.getColumnIndex() );
            }
        }
    }

    /**
     * Set the visibility of a column
     * 
     * @param index
     *            The index of the column to hide
     * @param isVisible
     *            true if the column is to be visible
     */
    public void setColumnVisibility(int index,
                                    boolean isVisible) {
        if ( index < 0
             || index > columns.size() ) {
            throw new IllegalArgumentException(
                                                "Column index must be greater than zero and less than then number of declared columns." );
        }

        if ( isVisible
             && !columns.get( index ).isVisible() ) {
            columns.get( index ).setVisible( isVisible );
            assertModelIndexes();
            gridWidget.showColumn( index );
            headerWidget.redraw();
        } else if ( !isVisible
                    && columns.get( index ).isVisible() ) {
            columns.get( index ).setVisible( isVisible );
            assertModelIndexes();
            gridWidget.hideColumn( index );
            headerWidget.redraw();
        }
    }

    /**
     * Some implementations may require the values of cells within the
     * DecoratedGridWidget to be programmatically manipulated (such as
     * "Row Number", which has to be recalculated after a sort operation). Such
     * implementations can register themselves here to receive requests to
     * update cell values when necessary (currently only after a sort).
     * 
     * @param hasSystemControlledColumns
     */
    public void setHasSystemControlledColumns(HasSystemControlledColumns hasSystemControlledColumns) {
        this.hasSystemControlledColumns = hasSystemControlledColumns;
    }

    /**
     * Set the "Header" for the DecoratedGridWidget and perform any
     * initialisation, such as registering event handlers.
     * 
     * @param headerWidget
     */
    public abstract void setHeaderWidget(DecoratedGridHeaderWidget<T> headerWidget);

    /**
     * This should be used instead of setHeight(String) and setWidth(String) as
     * various child Widgets of the DecisionTable need to have their sizes set
     * relative to the outermost Widget (i.e. this).
     */
    @Override
    public void setPixelSize(int width,
                             int height) {
        if ( width < 0 ) {
            throw new IllegalArgumentException( "width cannot be less than zero" );
        }
        if ( height < 0 ) {
            throw new IllegalArgumentException( "height cannot be less than zero" );
        }
        super.setPixelSize( width,
                            height );
        this.height = height;
        setHeight( height );
        setWidth( width );
    }

    /**
     * Set the "Sidebar" for the DecoratedGridWidget and perform any
     * initialisation, such as registering event handlers.
     * 
     * @param sidebarWidget
     */
    public abstract void setSidebarWidget(final DecoratedGridSidebarWidget<T> sidebarWidget);

    /**
     * Sort data based upon information stored in Columns
     */
    public void sort() {
        final DynamicColumn< ? >[] sortOrderList = new DynamicColumn[columns.size()];
        int index = 0;
        for ( DynamicColumn<T> column : columns ) {
            int sortIndex = column.getSortIndex();
            if ( sortIndex != -1 ) {
                sortOrderList[sortIndex] = column;
                index++;
            }
        }
        final int sortedColumnCount = index;

        Collections.sort( data,
                          new Comparator<DynamicDataRow>() {

                              @SuppressWarnings({"rawtypes", "unchecked"})
                              public int compare(DynamicDataRow leftRow,
                                                 DynamicDataRow rightRow) {
                                  int comparison = 0;
                                  for ( int index = 0; index < sortedColumnCount; index++ ) {
                                      DynamicColumn sortableHeader = sortOrderList[index];
                                      Comparable leftColumnValue = leftRow.get( sortableHeader
                                              .getColumnIndex() );
                                      Comparable rightColumnValue = rightRow.get( sortableHeader
                                              .getColumnIndex() );
                                      comparison = (leftColumnValue == rightColumnValue) ? 0
                                          : (leftColumnValue == null) ? -1
                                              : (rightColumnValue == null) ? 1
                                                  : leftColumnValue
                                                          .compareTo( rightColumnValue );
                                      if ( comparison != 0 ) {
                                          switch ( sortableHeader.getSortDirection() ) {
                                              case ASCENDING :
                            break;
                        case DESCENDING :
                            comparison = -comparison;
                            break;
                        default :
                            throw new IllegalStateException(
                                                             "Sorting can only be enabled for ASCENDING or"
                                                                     + " DESCENDING, not sortDirection ("
                                                                     + sortableHeader.getSortDirection()
                                                                     + ") ." );
                    }
                    return comparison;
                }
            }
            return comparison;
        }
                          } );

        removeModelMerging();
        assertModelMerging();

        // Request dependent children update cell values accordingly
        if ( hasSystemControlledColumns != null ) {
            hasSystemControlledColumns.updateSystemControlledColumnValues();
        }
        gridWidget.redraw();

    }

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
        extendSelection( startCell.getCoordinate() );
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
            gridWidget.redraw();
        } else {
            isMerged = false;
            clearSelection();
            removeModelMerging();
            gridWidget.redraw();
        }
        return isMerged;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.ValueUpdater#update(java.lang.Object)
     */
    public void update(Object value) {

        // Update underlying data
        for ( CellValue< ? extends Comparable< ? >> cell : this.selections ) {
            Coordinate c = cell.getCoordinate();
            if ( !columns.get( c.getCol() ).isSystemControlled() ) {
                data.set( c,
                          value );
            }
        }

        // Partial redraw
        assertModelMerging();
        int baseRowIndex = this.selections.first().getPhysicalCoordinate()
                .getRow();
        int minRedrawRow = findMinRedrawRow( baseRowIndex );
        int maxRedrawRow = findMaxRedrawRow( baseRowIndex );

        // When merged cells become unmerged (if their value is
        // cleared need to ensure the re-draw range is at least
        // as large as the selection range
        if ( maxRedrawRow < this.selections.last().getPhysicalCoordinate()
                .getRow() ) {
            maxRedrawRow = this.selections.last().getPhysicalCoordinate()
                    .getRow();
        }
        gridWidget.redrawRows( minRedrawRow,
                               maxRedrawRow );
        gridWidget.selectCell( selections.first() );
    }

    // Ensure merging is reflected in the entire model
    private void assertModelMerging() {

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

    // Clear and selection.
    private void clearSelection() {
        // De-select any previously selected cells
        for ( CellValue< ? extends Comparable< ? >> cell : this.selections ) {
            cell.setSelected( false );
            gridWidget.deselectCell( cell );
        }

        // Clear collection
        selections.clear();
    }

    // Delete column from table with optional redraw
    private void deleteColumn(DynamicColumn<T> column,
                              boolean bRedraw) {

        // Lookup UI column
        int index = columns.indexOf( column );
        if ( index == -1 ) {
            throw new IllegalArgumentException(
                                                "Column not found in declared columns." );
        }

        // Clear any selections
        clearSelection();

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
            gridWidget.redraw();
            headerWidget.redraw();
        }

    }

    // Ensure Coordinates are the extents of merged cell
    private void extendSelection(Coordinate coordinate) {
        CellValue< ? > startCell = data.get( coordinate );
        CellValue< ? > endCell = startCell;
        while ( startCell.getRowSpan() == 0 ) {
            startCell = data.get( startCell.getCoordinate().getRow() - 1 ).get(
                                                                                startCell.getCoordinate().getCol() );
        }

        if ( startCell.getRowSpan() > 1 ) {
            endCell = data.get(
                                startCell.getCoordinate().getRow()
                                        + startCell.getRowSpan()
                                        - 1 ).get( startCell.getCoordinate().getCol() );
        }
        selectRange( startCell,
                     endCell );
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

    // Re-index columns
    private void reindexColumns() {
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<T> col = columns.get( iCol );
            col.setColumnIndex( iCol );
        }
    }

    // Remove merging from model
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

    // Select a range of cells between the two coordinates.
    private void selectRange(CellValue< ? > startCell,
                             CellValue< ? > endCell) {
        int col = startCell.getCoordinate().getCol();
        for ( int iRow = startCell.getCoordinate().getRow(); iRow <= endCell
                .getCoordinate().getRow(); iRow++ ) {
            CellValue< ? > cell = data.get( iRow ).get( col );
            selections.add( cell );

            // Redraw selected cell
            cell.setSelected( true );
            gridWidget.selectCell( cell );
        }
    }

    // Set height of outer most Widget and related children
    private void setHeight(final int height) {
        mainPanel.setHeight( height
                             + "px" );
        mainFocusPanel.setHeight( height
                                  + "px" );

        // The Sidebar and Header sizes are derived from the ScrollPanel
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    // Set width of outer most Widget and related children
    private void setWidth(int width) {
        mainPanel.setWidth( width
                            + "px" );
        scrollPanel.setWidth( (width - style.sidebarWidth())
                              + "px" );
        mainFocusPanel.setWidth( width
                                 + "px" );

        // The Sidebar and Header sizes are derived from the ScrollPanel
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

}
