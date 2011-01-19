package org.drools.guvnor.client.decisiontable.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.drools.guvnor.client.decisiontable.cells.CellFactory;
import org.drools.guvnor.client.decisiontable.cells.CellValueFactory;
import org.drools.guvnor.client.decisiontable.widget.MergableGridWidget.CellExtents;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;
import org.drools.guvnor.client.table.SortDirection;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.DescriptionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Abstract Decision Table encapsulating basic operation.
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableWidget extends Composite
    implements
        ValueUpdater<Object> {

    public enum MOVE_DIRECTION {
        LEFT, RIGHT, UP, DOWN
    }

    // Widgets for UI
    protected Panel                                        mainPanel;
    protected Panel                                        bodyPanel;
    protected FocusPanel                                   mainFocusPanel;
    protected ScrollPanel                                  scrollPanel;
    protected MergableGridWidget                           gridWidget;
    protected DecisionTableHeaderWidget                    headerWidget;
    protected DecisionTableSidebarWidget                   sidebarWidget;
    protected boolean                                      isMerged   = false;

    protected SuggestionCompletionEngine                   sce;

    // Decision Table data
    protected GuidedDecisionTable                          model;
    protected DynamicData                                  data       = new DynamicData();
    protected List<DynamicColumn>                          columns    = new ArrayList<DynamicColumn>();

    private int                                            height;

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
     * Construct at empty Decision Table
     */
    public DecisionTableWidget(SuggestionCompletionEngine sce) {

        this.sce = sce;

        mainPanel = getMainPanel();
        bodyPanel = getBodyPanel();
        gridWidget = getGridWidget();
        headerWidget = getHeaderWidget();
        headerWidget.addResizeHandler( new ResizeHandler() {

            public void onResize(ResizeEvent event) {
                scrollPanel.setHeight( (height - event.getHeight()) + "px" );
                assertDimensions();
            }
        } );
        sidebarWidget = getSidebarWidget();

        scrollPanel = new ScrollPanel();
        scrollPanel.add( gridWidget );
        scrollPanel.addScrollHandler( getScrollHandler() );

        bodyPanel.add( headerWidget );
        bodyPanel.add( scrollPanel );
        mainPanel.add( sidebarWidget );
        mainPanel.add( bodyPanel );

        mainFocusPanel = new FocusPanel( mainPanel );
        initWidget( mainFocusPanel );
    }

    /**
     * Add a new column to the table at the appropriate position. Columns are
     * grouped thus: RowNumberCol, DescriptionCol, MetadataCols, AttributeCols,
     * ConditionCols (sub-grouped by pattern), ActionCols.
     * 
     * @param modelColumn
     *            The Model column to add
     */
    public void addColumn(DTColumnConfig modelColumn) {
        int index = 0;
        if ( modelColumn instanceof MetadataCol ) {
            index = findMetadataColumnIndex();
        } else if ( modelColumn instanceof AttributeCol ) {
            index = findAttributeColumnIndex();
        } else if ( modelColumn instanceof ConditionCol ) {
            index = findConditionColumnIndex( (ConditionCol) modelColumn );
        } else if ( modelColumn instanceof ActionCol ) {
            index = findActionColumnIndex();
        }
        insertColumnBefore( modelColumn,
                            index + 1 );
    }

    /**
     * Append a new row to the bottom of the table
     */
    public void appendRow() {
        clearSelection();
        insertRowBefore( null );
    }

    /**
     * Delete a column
     * 
     * @param column
     *            The Model column to delete
     */
    public void deleteColumn(DTColumnConfig column) {
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            if ( columns.get( iCol ).getModelColumn().equals( column ) ) {
                deleteColumn( iCol );
                break;
            }
        }
    }

    /**
     * Delete a column
     * 
     * @param index
     *            The index of the column to delete
     */
    public void deleteColumn(int index) {
        if ( index < 0 || index > columns.size() - 1 ) {
            throw new IllegalArgumentException(
                                                "Column index must be greater than zero and less than then number of declared columns." );
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
        assertModelIndexes();
        gridWidget.redraw();
        headerWidget.redraw();

    }

    /**
     * Delete a row
     * 
     * @param row
     *            The row to delete
     */
    public void deleteRow(DynamicDataRow row) {
        int index = data.indexOf( row );
        if ( index == -1 ) {
            throw new IllegalArgumentException(
                                                "DynamicDataRow does not exist in table data." );
        }
        clearSelection();

        data.remove( index );
        updateSystemControlledColumnValues();

        // Partial redraw
        if ( !isMerged ) {
            // Single row when not merged
            gridWidget.deleteRow( index );
            assertModelIndexes();
        } else {
            // Affected rows when merged
            gridWidget.deleteRow( index );

            if ( data.size() > 0 ) {
                updateSystemControlledColumnValues();
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

        redrawSystemControlledColumns();
        assertDimensions();

    }

    /**
     * Return the Decision Tables columns
     * 
     * @return The columns
     */
    public List<DynamicColumn> getColumns() {
        return this.columns;
    }

    /**
     * Return the Decision Tables data
     * 
     * @return The grid data
     */
    public DynamicData getData() {
        return this.data;
    }

    /**
     * Return the model
     * 
     * @return The DecisionTable data model
     */
    public GuidedDecisionTable getModel() {
        return this.model;
    }

    /**
     * Return the SCE associated with this Decision Table
     * 
     * @return
     */
    public SuggestionCompletionEngine getSCE() {
        return this.sce;
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
     * Insert a new row before the specified row.
     * 
     * @param row
     *            The row before which the new row should be added. A
     *            <code>null</code> value appends the row to the end of the
     *            existing collection
     */
    public void insertRowBefore(DynamicDataRow row) {
        int index = data.size();
        if ( row != null ) {
            index = data.indexOf( row );
            if ( index == -1 ) {
                throw new IllegalArgumentException(
                                                    "DynamicDataRow does not exist in table data." );
            }
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

        // Add row to data
        DynamicDataRow newRow = new DynamicDataRow();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DTColumnConfig column = columns.get( iCol ).getModelColumn();
            CellValue< ? extends Comparable< ? >> data = CellValueFactory
                    .getInstance().getCellValue( column,
                                                 index,
                                                 iCol,
                                                 column.getDefaultValue(),
                                                 this );
            newRow.add( data );
        }
        data.add( index,
                  newRow );
        updateSystemControlledColumnValues();

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

        redrawSystemControlledColumns();
        assertDimensions();

    }

    /**
     * Move the selected cell
     * 
     * @param dir
     *            Direction to move the selection
     */
    public void moveSelection(MOVE_DIRECTION dir) {
        if ( selections.size() > 0 ) {
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
                                             c.getCol() - step );

                        // Skip hidden columns
                        while ( nc.getCol() > 0
                                && !columns.get( nc.getCol() ).isVisible() ) {
                            nc = new Coordinate( c.getRow(),
                                                 nc.getCol() - step );
                        }
                        startSelecting( nc );

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
                                             c.getCol() + step );

                        // Skip hidden columns
                        while ( nc.getCol() < columns.size() - 2
                                && !columns.get( nc.getCol() ).isVisible() ) {
                            nc = new Coordinate( c.getRow(),
                                                 nc.getCol() + step );
                        }
                        startSelecting( nc );

                        // Ensure cell is visible
                        ce = gridWidget.getSelectedCellExtents( selections.first() );
                        int scrollWidth = scrollPanel.getElement().getClientWidth();
                        if ( ce.getOffsetX() + ce.getWidth() > scrollWidth
                                                               + scrollPanel.getHorizontalScrollPosition() ) {
                            int delta = ce.getOffsetX() + ce.getWidth()
                                        - scrollPanel.getHorizontalScrollPosition()
                                        - scrollWidth;
                            scrollPanel.setHorizontalScrollPosition( scrollPanel
                                    .getHorizontalScrollPosition() + delta );
                        }
                    }
                    break;
                case UP :

                    // Move up
                    step = c.getRow() > 0 ? 1 : 0;
                    if ( step > 0 ) {
                        nc = new Coordinate( c.getRow() - step,
                                             c.getCol() );
                        startSelecting( nc );

                        // Ensure cell is visible
                        ce = gridWidget.getSelectedCellExtents( selections.first() );
                        if ( ce.getOffsetY() < scrollPanel.getScrollPosition() ) {
                            scrollPanel.setScrollPosition( ce.getOffsetY() );
                        }
                    }
                    break;
                case DOWN :

                    // Move down
                    step = c.getRow() < data.size() - 1 ? 1 : 0;
                    if ( step > 0 ) {
                        nc = new Coordinate( c.getRow() + step,
                                             c.getCol() );
                        startSelecting( nc );

                        // Ensure cell is visible
                        ce = gridWidget.getSelectedCellExtents( selections.first() );
                        int scrollHeight = scrollPanel.getElement()
                                .getClientHeight();
                        if ( ce.getOffsetY() + ce.getHeight() > scrollHeight
                                                                + scrollPanel.getScrollPosition() ) {
                            int delta = ce.getOffsetY() + ce.getHeight()
                                        - scrollPanel.getScrollPosition()
                                        - scrollHeight;
                            scrollPanel.setScrollPosition( scrollPanel
                                    .getScrollPosition() + delta );
                        }
                    }
            }
        }
    }

    /**
     * Redraw "static" columns (e.g. row number and salience)
     */
    public void redrawSystemControlledColumns() {

        for ( DynamicColumn col : columns ) {

            // Redraw if applicable
            if ( col.isSystemControlled() ) {
                gridWidget.redrawColumn( col.getColumnIndex() );
            }
        }
    }

    /**
     * Update the Decision Table model with the columns contained in the grid.
     * The Decision Table controls indexing of new columns to preserve grouping
     * of column types. If the order of columns is important to client-code this
     * can be called to ensure columns within the model are synchronised with
     * the Decision Table.
     */
    public void scrapeColumns() {

        // Clear existing definition
        model.getMetadataCols().clear();
        model.getAttributeCols().clear();
        model.getConditionCols().clear();
        model.getActionCols().clear();

        RowNumberCol rnCol = null;
        DescriptionCol descCol = null;

        // Extract column information
        for ( DynamicColumn column : columns ) {
            DTColumnConfig modelCol = column.getModelColumn();
            if ( modelCol instanceof RowNumberCol ) {
                rnCol = (RowNumberCol) modelCol;
                model.setRowNumberCol( rnCol );

            } else if ( modelCol instanceof DescriptionCol ) {
                descCol = (DescriptionCol) modelCol;
                model.setDescriptionCol( descCol );

            } else if ( modelCol instanceof MetadataCol ) {
                MetadataCol tc = (MetadataCol) modelCol;
                model.getMetadataCols().add( tc );

            } else if ( modelCol instanceof AttributeCol ) {
                AttributeCol tc = (AttributeCol) modelCol;
                model.getAttributeCols().add( tc );

            } else if ( modelCol instanceof ConditionCol ) {
                ConditionCol tc = (ConditionCol) modelCol;
                model.getConditionCols().add( tc );

            } else if ( modelCol instanceof ActionCol ) {
                ActionCol tc = (ActionCol) modelCol;
                model.getActionCols().add( tc );

            }
        }
    }

    /**
     * Update the Decision Table model with the data contained in the grid. The
     * Decision Table does not synchronise model data with UI data during user
     * interaction with the UI. Consequentially this should be called to refresh
     * the Model with the UI when needed.
     */
    public void scrapeData() {

        // Copy data
        final int GRID_ROWS = data.size();
        String[][] grid = new String[GRID_ROWS][];
        for ( int iRow = 0; iRow < GRID_ROWS; iRow++ ) {
            DynamicDataRow dataRow = data.get( iRow );
            String[] row = new String[dataRow.size()];
            for ( int iCol = 0; iCol < dataRow.size(); iCol++ ) {
                Object value = dataRow.get( iCol ).getValue();
                row[iCol] = (value == null ? null : value.toString());
            }
            grid[iRow] = row;
        }
        this.model.setData( grid );
    }

    /**
     * Set the visibility of a column
     * 
     * @param column
     *            The Model column to hide
     * @param isVisible
     *            true if the column is to be visible
     */
    public void setColumnVisibility(DTColumnConfig column,
                                    boolean isVisible) {
        if ( column == null ) {
            throw new IllegalArgumentException( "Column cannot be null" );
        }

        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            if ( columns.get( iCol ).getModelColumn().equals( column ) ) {
                setColumnVisibility( iCol,
                                     isVisible );
                break;
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
        if ( index < 0 || index > columns.size() - 1 ) {
            throw new IllegalArgumentException(
                                                "Column index must be greater than zero and less than then number of declared columns." );
        }

        if ( isVisible && !columns.get( index ).isVisible() ) {
            columns.get( index ).setVisible( isVisible );
            assertModelIndexes();
            gridWidget.showColumn( index );
            headerWidget.redraw();
        } else if ( !isVisible && columns.get( index ).isVisible() ) {
            columns.get( index ).setVisible( isVisible );
            assertModelIndexes();
            gridWidget.hideColumn( index );
            headerWidget.redraw();
        }
    }

    /**
     * Set the Decision Table's data. This removes all existing columns from the
     * Decision Table and re-creates them based upon the provided data.
     * 
     * @param data
     */
    public void setModel(GuidedDecisionTable model) {

        this.model = model;

        columns.clear();

        // Static columns, Row#
        int iCol = 0;
        DTColumnConfig colStatic;
        DynamicColumn columnStatic;
        colStatic = model.getRowNumberCol();
        columnStatic = new DynamicColumn( colStatic,
                                          CellFactory.getInstance()
                                                  .getCell( colStatic,
                                                            this ),
                                          iCol,
                                          true,
                                          false );
        columns.add( columnStatic );
        iCol++;

        // Static columns, Description
        colStatic = model.getDescriptionCol();
        columnStatic = new DynamicColumn( colStatic,
                                          CellFactory.getInstance()
                                                  .getCell( colStatic,
                                                            this ),
                                          iCol );
        columns.add( columnStatic );
        iCol++;

        // Initialise CellTable's Metadata columns
        for ( DTColumnConfig col : model.getMetadataCols() ) {
            DynamicColumn column = new DynamicColumn( col,
                                                      CellFactory
                                                              .getInstance().getCell( col,
                                                                                      this ),
                                                      iCol );
            column.setVisible( !col.isHideColumn() );
            columns.add( column );
            iCol++;
        }

        // Initialise CellTable's Attribute columns
        for ( DTColumnConfig col : model.getAttributeCols() ) {
            DynamicColumn column = new DynamicColumn( col,
                                                      CellFactory
                                                              .getInstance().getCell( col,
                                                                                      this ),
                                                      iCol );
            column.setVisible( !col.isHideColumn() );
            column.setSystemControlled( col.isUseRowNumber() );
            column.setSortable( !col.isUseRowNumber() );
            columns.add( column );
            iCol++;
        }

        // Initialise CellTable's Condition columns
        assertConditionColumnGrouping( model );
        for ( DTColumnConfig col : model.getConditionCols() ) {
            DynamicColumn column = new DynamicColumn( col,
                                                      CellFactory
                                                              .getInstance().getCell( col,
                                                                                      this ),
                                                      iCol );
            column.setVisible( !col.isHideColumn() );
            columns.add( column );
            iCol++;
        }

        // Initialise CellTable's Action columns
        for ( DTColumnConfig col : model.getActionCols() ) {
            DynamicColumn column = new DynamicColumn( col,
                                                      CellFactory
                                                              .getInstance().getCell( col,
                                                                                      this ),
                                                      iCol );
            column.setVisible( !col.isHideColumn() );
            columns.add( column );
            iCol++;
        }

        // Ensure columns are correctly indexed
        reindexColumns();

        // Setup data
        int dataSize = model.getData().length;
        if ( dataSize > 0 ) {
            for ( int iRow = 0; iRow < dataSize; iRow++ ) {
                String[] row = model.getData()[iRow];
                DynamicDataRow cellRow = new DynamicDataRow();
                for ( iCol = 0; iCol < columns.size(); iCol++ ) {
                    DTColumnConfig column = columns.get( iCol ).getModelColumn();
                    CellValue< ? extends Comparable< ? >> cv = CellValueFactory
                            .getInstance().getCellValue( column,
                                                         iRow,
                                                         iCol,
                                                         row[iCol],
                                                         this );
                    cellRow.add( cv );
                }
                this.data.add( cellRow );
            }
        }

        // Draw header first as the size of child Elements depends upon it
        headerWidget.redraw();

        // Schedule redraw of grid after sizes of child Elements have been set
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            public void execute() {
                gridWidget.redraw();
            }

        } );
    }

    /**
     * This should be used instead of setHeight(String) and setWidth(String) as
     * various child Widgets of the DecisionTable need to have their sizes set
     * relative to the outermost Widget (i.e. this).
     */
    @Override
    public void setPixelSize(int width,
                             int height) {
        super.setPixelSize( width,
                            height );
        this.height = height;
        setHeight( height );
        setWidth( width );
    }

    /**
     * Sort data based upon information stored in Columns
     */
    public void sort() {
        final DynamicColumn[] sortOrderList = new DynamicColumn[columns.size()];
        int index = 0;
        for ( DynamicColumn column : columns ) {
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
        updateSystemControlledColumnValues();
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
        clearSelection();
        CellValue< ? > startCell = data.get( start );
        extendSelection( startCell.getCoordinate() );
    }

    /**
     * Toggle the state of Decision Table merging.
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

    /**
     * Update a column
     * 
     * @param col
     */
    public void updateColumn(DTColumnConfig col) {

        int iCol = getDynamicColumnIndex( col );
        columns.get( iCol ).setCell( CellFactory.getInstance().getCell( col,
                                                                        this ) );

        boolean redraw = false;
        List<String> vals = Arrays.asList( model.getValueList( col,
                                                               sce ) );
        if ( vals.size() > 0 ) {
            for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                DynamicDataRow row = data.get( iRow );
                if ( !vals.contains( row.get( iCol ).getValue() ) ) {
                    row.get( iCol ).setValue( null );
                    redraw = true;
                }
            }
        }
        if ( redraw ) {
            gridWidget.redrawColumn( iCol );
        }

    }

    /**
     * Update values of "static" columns (e.g. row number and salience)
     */
    public void updateSystemControlledColumnValues() {

        for ( DynamicColumn col : columns ) {

            DTColumnConfig modelColumn = col.getModelColumn();

            if ( modelColumn instanceof RowNumberCol ) {

                // Update Row Number column values
                for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                    data.get( iRow ).get( col.getColumnIndex() ).setValue( iRow + 1 );
                }

            } else if ( modelColumn instanceof AttributeCol ) {

                // Update Salience values
                AttributeCol attrCol = (AttributeCol) modelColumn;
                if ( attrCol.attr.equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                    if ( attrCol.isUseRowNumber() ) {
                        col.setSortDirection( SortDirection.NONE );
                        final int MAX_ROWS = data.size();
                        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                            int salience = iRow + 1;
                            if ( attrCol.isReverseOrder() ) {
                                salience = Math.abs( iRow - MAX_ROWS );
                            }
                            data.get( iRow ).get( col.getColumnIndex() )
                                    .setValue( salience );
                        }
                    }
                    // Ensure Salience cells are rendered with the correct Cell
                    col.setCell( CellFactory.getInstance()
                            .getCell( attrCol,
                                      this ) );
                    col.setSystemControlled( attrCol.isUseRowNumber() );
                    col.setSortable( !attrCol.isUseRowNumber() );
                }
            }
        }
    }

    // Ensure Condition columns are grouped by pattern (as we merge equal
    // patterns in the UI). This operates on the original Model data and
    // therefore should be called before the Decision Table's internal data
    // representation (i.e. DynamicData, DynamicDataRow and CellValue) is
    // populated
    private void assertConditionColumnGrouping(GuidedDecisionTable model) {

        class ConditionColData {
            ConditionCol col;
            String[]     data;
        }

        // Offset into Model's data array
        final int DATA_COLUMN_OFFSET = model.getMetadataCols().size() + model.getAttributeCols().size() + 2;
        Map<String, List<ConditionColData>> groups = new HashMap<String, List<ConditionColData>>();
        final int DATA_ROWS = model.getData().length;

        // Copy conditions and related data into temporary groups
        for ( int iCol = 0; iCol < model.getConditionCols().size(); iCol++ ) {

            ConditionCol col = model.getConditionCols().get( iCol );
            String pattern = col.getBoundName();
            if ( !groups.containsKey( pattern ) ) {
                List<ConditionColData> groupCols = new ArrayList<ConditionColData>();
                groups.put( pattern,
                            groupCols );
            }
            List<ConditionColData> groupCols = groups.get( pattern );

            // Make a ConditionColData object
            ConditionColData ccd = new ConditionColData();
            int colIndex = DATA_COLUMN_OFFSET + iCol;
            ccd.data = new String[DATA_ROWS];
            for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                ccd.data[iRow] = model.getData()[iRow][colIndex];
            }
            ccd.col = col;
            groupCols.add( ccd );

        }

        // Copy temporary groups back into the model
        int iCol = 0;
        model.getConditionCols().clear();
        for ( Map.Entry<String, List<ConditionColData>> me : groups.entrySet() ) {
            for ( ConditionColData ccd : me.getValue() ) {
                model.getConditionCols().add( ccd.col );
                int colIndex = DATA_COLUMN_OFFSET + iCol;
                for ( int iRow = 0; iRow < DATA_ROWS; iRow++ ) {
                    model.getData()[iRow][colIndex] = ccd.data[iRow];
                }
                iCol++;
            }
        }

    }

    // Here lays a can of worms! Each cell in the Decision Table
    // has three coordinates: (1) The physical coordinate, (2) The
    // coordinate relating to the HTML table element and (3) The
    // coordinate mapping a HTML table element back to the physical
    // coordinate. For example a cell could have the (1) physical
    // coordinate (0,0) which equates to (2) HTML element (0,1) in
    // which case the cell at physical coordinate (0,1) would
    // have a (3) mapping back to (0,0).
    private void assertModelIndexes() {

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            int colCount = 0;
            for ( int iCol = 0; iCol < row.size(); iCol++ ) {

                int newRow = iRow;
                int newCol = colCount;
                CellValue< ? extends Comparable< ? >> indexCell = row.get( iCol );

                // Don't index hidden columns; indexing is used to
                // map between HTML elements and the data behind
                DynamicColumn column = columns.get( iCol );
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

    // Ensure merging is reflected in the entire model
    private void assertModelMerging() {

        final int minRowIndex = 0;
        final int maxRowIndex = data.size() - 1;

        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            for ( int iRow = minRowIndex; iRow <= maxRowIndex; iRow++ ) {

                int rowSpan = 1;
                CellValue< ? > cell1 = data.get( iRow ).get( iCol );
                if ( iRow + rowSpan < data.size() ) {

                    CellValue< ? > cell2 = data.get( iRow + rowSpan ).get( iCol );

                    // Don't merge empty cells
                    if ( isMerged && !cell1.isEmpty() && !cell2.isEmpty() ) {
                        while ( cell1.getValue().equals( cell2.getValue() )
                                && iRow + rowSpan < maxRowIndex ) {
                            cell2.setRowSpan( 0 );
                            rowSpan++;
                            cell2 = data.get( iRow + rowSpan ).get( iCol );
                        }
                        if ( cell1.getValue().equals( cell2.getValue() ) ) {
                            cell2.setRowSpan( 0 );
                            rowSpan++;
                        }
                    }
                    cell1.setRowSpan( rowSpan );
                    iRow = iRow + rowSpan - 1;
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
                                startCell.getCoordinate().getRow() + startCell.getRowSpan()
                                        - 1 ).get( startCell.getCoordinate().getCol() );
        }
        selectRange( startCell,
                     endCell );
    }

    // Find the right-most index for an Action column
    private int findActionColumnIndex() {
        int index = columns.size() - 1;
        return index;
    }

    // Find the right-most index for a Attribute column
    private int findAttributeColumnIndex() {
        int index = 0;
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn column = columns.get( iCol );
            DTColumnConfig modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol ) {
                index = iCol;
            } else if ( modelColumn instanceof AttributeCol ) {
                index = iCol;
            }
        }
        return index;
    }

    // Find the right-most index for a Condition column
    private int findConditionColumnIndex(ConditionCol col) {
        int index = 0;
        boolean bMatched = false;
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn column = columns.get( iCol );
            DTColumnConfig modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol ) {
                index = iCol;
            } else if ( modelColumn instanceof AttributeCol ) {
                index = iCol;
            } else if ( modelColumn instanceof ConditionCol ) {
                if ( isEquivalentConditionColumn( (ConditionCol) modelColumn,
                                                  col ) ) {
                    index = iCol;
                    bMatched = true;
                } else if ( !bMatched ) {
                    index = iCol;
                }
            }
        }
        return index;
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
            while ( cell.getRowSpan() != 1 && iRow < data.size() - 1 ) {
                iRow++;
                DynamicDataRow row = data.get( iRow );
                cell = row.get( iCol );
            }
            maxRedrawRow = (iRow > maxRedrawRow ? iRow : maxRedrawRow);
        }
        return maxRedrawRow;
    }

    // Find the right-most index for a Metadata column
    private int findMetadataColumnIndex() {
        int index = 0;
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn column = columns.get( iCol );
            DTColumnConfig modelColumn = column.getModelColumn();
            if ( modelColumn instanceof RowNumberCol ) {
                index = iCol;
            } else if ( modelColumn instanceof DescriptionCol ) {
                index = iCol;
            } else if ( modelColumn instanceof MetadataCol ) {
                index = iCol;
            }
        }
        return index;
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
            while ( cell.getRowSpan() != 1 && iRow > 0 ) {
                iRow--;
                DynamicDataRow row = data.get( iRow );
                cell = row.get( iCol );
            }
            minRedrawRow = (iRow < minRedrawRow ? iRow : minRedrawRow);
        }
        return minRedrawRow;
    }

    // Retrieves the index of a DynamicColumn or -1 if it cannot be found
    private int getDynamicColumnIndex(DTColumnConfig col) {
        int index = -1;
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            if ( columns.get( iCol ).getModelColumn().equals( col ) ) {
                index = iCol;
                break;
            }
        }
        return index;
    }

    // Insert a new model column at the specified index
    private void insertColumnBefore(DTColumnConfig modelColumn,
                                    int index) {

        // Add column to data
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            CellValue< ? > cell = CellValueFactory.getInstance().getCellValue(
                                                                               modelColumn,
                                                                               iRow,
                                                                               index,
                                                                               modelColumn.getDefaultValue(),
                                                                               this );
            data.get( iRow ).add( index,
                                  cell );
        }

        // Create new column for grid
        DynamicColumn column = new DynamicColumn( modelColumn,
                                                  CellFactory
                                                          .getInstance().getCell( modelColumn,
                                                                                  this ),
                                                  index );
        column.setVisible( !modelColumn.isHideColumn() );
        columns.add( index,
                     column );
        reindexColumns();

        // Redraw
        assertModelIndexes();
        gridWidget.redrawColumns( index,
                                  columns.size() - 1 );
        headerWidget.redraw();
        assertDimensions();

    }

    // Check whether two Strings are equal or both null
    private boolean isEqualOrNull(String s1,
                                  String s2) {
        if ( s1 == null && s2 == null ) {
            return true;
        } else if ( s1 != null && s2 != null && s1.equals( s2 ) ) {
            return true;
        }
        return false;
    }

    // Check whether two ConditionCols are equivalent
    private boolean isEquivalentConditionColumn(ConditionCol c1,
                                                ConditionCol c2) {
        if ( isEqualOrNull( c1.getFactType(),
                            c2.getFactType() )
                && isEqualOrNull( c1.getBoundName(),
                                  c2.getBoundName() ) ) {
            return true;
        }
        return false;
    }

    // Re-index columns
    private void reindexColumns() {
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn col = columns.get( iCol );
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
        mainPanel.setHeight( height + "px" );
        mainFocusPanel.setHeight( height + "px" );

        // The Sidebar and Header sizes are derived from the ScrollPanel
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    // Set width of outer most Widget and related children
    private void setWidth(int width) {
        mainPanel.setWidth( width + "px" );
        scrollPanel.setWidth( (width - style.sidebarWidth()) + "px" );
        mainFocusPanel.setWidth( width + "px" );

        // The Sidebar and Header sizes are derived from the ScrollPanel
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    // The DecisionTableHeaderWidget and DecisionTableSidebarWidget need to be
    // resized when MergableGridWidget has scrollbars
    protected void assertDimensions() {
        headerWidget.setWidth( scrollPanel.getElement().getClientWidth() + "px" );
        sidebarWidget.setHeight( scrollPanel.getElement().getClientHeight()
                                 + "px" );
    }

    /**
     * Gets the Widgets inner panel to which the DecisionTable and Header will
     * be added. This allows subclasses to have some control over the internal
     * layout of the Decision Table.
     * 
     * @return
     */
    protected abstract Panel getBodyPanel();

    /**
     * Gets the Widget responsible for rendering the DecisionTables "grid".
     * 
     * @return
     */
    protected abstract MergableGridWidget getGridWidget();

    /**
     * Gets the Widget responsible for rendering the DecisionTables "header".
     * 
     * @return
     */
    protected abstract DecisionTableHeaderWidget getHeaderWidget();

    /**
     * Gets the Widget's outer most panel to which other content will be added.
     * This allows subclasses to have some control over the general layout of
     * the Decision Table.
     * 
     * @return
     */
    protected abstract Panel getMainPanel();

    /**
     * The DecisionTable is nested inside a ScrollPanel. This allows
     * ScrollEvents to be hooked up to other defendant controls (e.g. the
     * Header).
     * 
     * @return
     */
    protected abstract ScrollHandler getScrollHandler();

    /**
     * Gets the Widget responsible for rendering the DecisionTables "side-bar".
     * 
     * @return
     */
    protected abstract DecisionTableSidebarWidget getSidebarWidget();

}
