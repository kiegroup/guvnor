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
package org.drools.guvnor.client.widgets.decoratedgrid.data;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.widgets.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue.CellState;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue.GroupedCellValue;

/**
 * A simple container for rows of data.
 */
public class DynamicData<T> extends ArrayList<DynamicDataRow> {

    private boolean           isMerged         = false;

    private List<Boolean>     visibleColumns   = new ArrayList<Boolean>();

    private static final long serialVersionUID = 5061393855340039472L;

    /**
     * Add column to data
     * 
     * @param index
     * @param columnData
     */
    public void addColumn(int index,
                          List<CellValue< ? >> columnData,
                          boolean isVisible) {
        for ( int iRow = 0; iRow < columnData.size(); iRow++ ) {
            CellValue< ? > cv = columnData.get( iRow );
            get( iRow ).add( index,
                                  cv );
        }
        visibleColumns.add( index,
                            isVisible );
        assertModelIndexes();
    }

    /**
     * Add an empty row of data to the end of the table
     * 
     * @return DynamicDataRow The newly created row
     */
    public DynamicDataRow addRow() {
        DynamicDataRow row = new DynamicDataRow();
        add(row);
        return row;
    }

    /**
     * Add a row of data at the specified index
     * 
     * @param index
     * @param rowData
     * @return DynamicDataRow The newly created row
     */
    public DynamicDataRow addRow(int index,
                                 List<CellValue< ? extends Comparable< ? >>> rowData) {
        DynamicDataRow row = new DynamicDataRow();
        for ( CellValue< ? extends Comparable< ? >> cell : rowData ) {
            row.add( cell );
        }
        add( index,
             row );
        return row;
    }

    /**
     * Add a row of data at the end of the table
     * 
     * @param rowData
     * @return DynamicDataRow The newly created row
     */
    public DynamicDataRow addRow(List<CellValue< ? extends Comparable< ? >>> rowData) {
        DynamicDataRow row = new DynamicDataRow();
        for ( CellValue< ? extends Comparable< ? >> cell : rowData ) {
            row.add( cell );
        }
        add( row );
        return row;
    }

    /**
     * Apply grouping by collapsing applicable rows
     * 
     * @param startCell
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void applyModelGrouping(CellValue< ? > startCell) {

        int startRowIndex = startCell.getCoordinate().getRow();
        int endRowIndex = findMergedCellExtent( startCell.getCoordinate() ).getRow();
        int colIndex = startCell.getCoordinate().getCol();

        //Delete grouped rows replacing with a single "grouped" row
        GroupedCellValue groupedCell;
        DynamicDataRow row = get( startRowIndex );
        GroupedDynamicDataRow groupedRow = new GroupedDynamicDataRow();
        for ( int iCol = 0; iCol < row.size(); iCol++ ) {
            groupedCell = row.get( iCol ).convertToGroupedCell();
            if ( iCol == colIndex ) {
                groupedCell.addState( CellState.GROUPED );
            } else {
                groupedCell.removeState( CellState.GROUPED );
            }
            groupedRow.add( groupedCell );
        }

        //Add individual cells to "grouped" row
        for ( int iRow = startRowIndex; iRow <= endRowIndex; iRow++ ) {
            DynamicDataRow childRow = get( startRowIndex );
            groupedRow.addChildRow( childRow );
            remove( childRow );
        }
        remove( row );
        add( startRowIndex,
                  groupedRow );

        assertModelMerging();
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

        if ( size() == 0 ) {
            return;
        }

        for ( int iRow = 0; iRow < size(); iRow++ ) {
            DynamicDataRow row = get( iRow );

            int colCount = 0;
            for ( int iCol = 0; iCol < row.size(); iCol++ ) {

                int newRow = iRow;
                int newCol = colCount;
                CellValue< ? extends Comparable< ? >> indexCell = row.get( iCol );

                // Don't index hidden columns; indexing is used to
                // map between HTML elements and the data behind
                if ( visibleColumns.get( iCol ) ) {

                    if ( indexCell.getRowSpan() != 0 ) {
                        newRow = iRow;
                        newCol = colCount++;

                        CellValue< ? extends Comparable< ? >> cell = get( newRow ).get( newCol );
                        cell.setPhysicalCoordinate( new Coordinate( iRow,
                                                                    iCol ) );

                    } else {
                        DynamicDataRow priorRow = get( iRow - 1 );
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
     * Ensure merging is reflected in the entire model
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void assertModelMerging() {

        if ( size() == 0 ) {
            return;
        }

        //Remove merging first as it initialises all coordinates
        removeModelMerging();

        final int COLUMNS = get( 0 ).size();

        //Only apply merging if merged
        if ( isMerged ) {

            int minRowIndex = 0;
            int maxRowIndex = size();

            //Add an empty row to the end of the data to simplify detection of merged cells that run to the end of the table
            DynamicDataRow blankRow = new DynamicDataRow();
            for ( int iCol = 0; iCol < COLUMNS; iCol++ ) {
                CellValue cv = new CellValue( null,
                                              maxRowIndex,
                                              iCol );
                blankRow.add( cv );
            }
            add( blankRow );
            maxRowIndex++;

            //Look in columns for cells with identical values
            for ( int iCol = 0; iCol < COLUMNS; iCol++ ) {
                CellValue< ? > cell1 = get( minRowIndex ).get( iCol );
                CellValue< ? > cell2 = null;
                for ( int iRow = minRowIndex + 1; iRow < maxRowIndex; iRow++ ) {
                    cell1.setRowSpan( 1 );
                    cell2 = get( iRow ).get( iCol );

                    //Merge if both cells contain the same value and neither is grouped
                    boolean bSplit = true;
                    if ( !cell1.isEmpty() && !cell2.isEmpty() ) {
                        if ( cell1.getValue().equals( cell2.getValue() ) ) {
                            bSplit = false;
                            if ( cell1 instanceof GroupedCellValue ) {
                                GroupedCellValue gcv = (GroupedCellValue) cell1;
                                if ( gcv.hasMultipleValues() ) {
                                    bSplit = true;
                                }
                            }
                            if ( cell2 instanceof GroupedCellValue ) {
                                GroupedCellValue gcv = (GroupedCellValue) cell2;
                                if ( gcv.hasMultipleValues() ) {
                                    bSplit = true;
                                }
                            }
                        }
                    }

                    if ( bSplit ) {
                        mergeCells( cell1,
                                    cell2 );
                        cell1 = cell2;
                    }

                }
            }

            //Remove dummy blank row
            remove( blankRow );

        }

        // Set indexes after merging has been corrected
        assertModelIndexes();

    }

    /**
     * Delete column data
     * 
     * @param index
     */
    public void deleteColumn(int index) {
        for ( int iRow = 0; iRow < size(); iRow++ ) {
            DynamicDataRow row = get( iRow );
            row.remove( index );
        }
        visibleColumns.remove( index );
        assertModelIndexes();
    }

    /**
     * Get the CellValue at the given coordinate
     * 
     * @param c
     * @return
     */
    public CellValue< ? extends Comparable< ? >> get(Coordinate c) {
        return get( c.getRow() ).get( c.getCol() );
    }

    /**
     * Return grid's data. Grouping in the data will be expanded and can
     * therefore can be used prior to populate the underlying data structures
     * prior to persisting.
     * 
     * @return data
     */
    public DynamicData<T> getFlattenedData() {
        DynamicData<T> dataClone = new DynamicData<T>();
        for ( int iRow = 0; iRow < size(); iRow++ ) {
            DynamicDataRow row = get( iRow );
            if ( row instanceof GroupedDynamicDataRow ) {
                List<DynamicDataRow> expandedRow = expandGroupedRow( row,
                                                                      true );
                dataClone.addAll( expandedRow );
            } else {
                dataClone.add( row );
            }
        }
        return dataClone;
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
     * Remove grouping by expanding applicable rows
     * 
     * @param startCell
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List<DynamicDataRow> removeModelGrouping(CellValue< ? > startCell) {

        int startRowIndex = startCell.getCoordinate().getRow();

        startCell.removeState( CellState.GROUPED );

        //Check if rows need to be recursively expanded
        boolean bRecursive = true;
        DynamicDataRow row = get( startRowIndex );
        for ( int iCol = 0; iCol < row.size(); iCol++ ) {
            CellValue< ? > cv = row.get( iCol );
            if ( cv instanceof GroupedCellValue ) {
                bRecursive = !(bRecursive ^ ((GroupedCellValue) cv).hasMultipleValues());
            }
        }

        //Delete "grouped" row and replace with individual rows
        List<DynamicDataRow> expandedRow = expandGroupedRow( row,
                                                             bRecursive );
        remove( startRowIndex );
        addAll( startRowIndex,
                     expandedRow );

        assertModelMerging();

        //If the row is replaced with another grouped row ensure the row can be expanded
        row = get( startRowIndex );
        boolean hasCellToExpand = false;
        for ( CellValue< ? > cell : row ) {
            if ( cell instanceof GroupedCellValue ) {
                if ( cell.isGrouped() && cell.getRowSpan() > 0 ) {
                    hasCellToExpand = true;
                    break;
                }
            }
        }
        if ( !hasCellToExpand ) {
            for ( CellValue< ? > cell : row ) {
                if ( cell instanceof GroupedCellValue && cell.getRowSpan() == 1 ) {
                    cell.addState( CellState.GROUPED );
                }
            }
        }
        return expandedRow;
    }

    /**
     * Set the value at the specified coordinate
     * 
     * @param c
     * @param value
     */
    public void set(Coordinate c,
                    Object value) {
        if ( c == null ) {
            throw new IllegalArgumentException( "c cannot be null" );
        }
        get( c.getRow() ).get( c.getCol() ).setValue( value );
    }

    /**
     * Set whether a columns is Visible
     * 
     * @param index
     *            index of column
     * @param isVisible
     *            True if the column is visible
     */
    public void setColumnVisibility(int index,
                                    boolean isVisible) {
        this.visibleColumns.set( index,
                                 isVisible );
    }

    /**
     * Set whether the grid's data is merged or not. Clearing merging within the
     * data also clears grouping
     * 
     * @param isMerged
     */
    public void setMerged(boolean isMerged) {
        this.isMerged = isMerged;
        if ( isMerged ) {
            assertModelMerging();
        } else {
            removeModelGrouping();
            removeModelMerging();
        }
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

    //Find the bottom coordinate of a merged cell
    private Coordinate findMergedCellExtent(Coordinate c) {
        if ( c.getRow() == size() - 1 ) {
            return c;
        }
        Coordinate nc = new Coordinate( c.getRow() + 1,
                                        c.getCol() );
        CellValue< ? > newCell = get( nc );
        while ( newCell.getRowSpan() == 0 && nc.getRow() < size() - 1 ) {
            nc = new Coordinate( nc.getRow() + 1,
                                     nc.getCol() );
            newCell = get( nc );
        }
        if ( newCell.getRowSpan() != 0 ) {
            nc = new Coordinate( nc.getRow() - 1,
                                     nc.getCol() );
            newCell = get( nc );
        }
        return nc;
    }

    //Merge between the two provided cells
    private void mergeCells(CellValue< ? > cell1,
                            CellValue< ? > cell2) {
        int iStartRowIndex = cell1.getCoordinate().getRow();
        int iEndRowIndex = cell2.getCoordinate().getRow();
        int iColIndex = cell1.getCoordinate().getCol();

        //Any rows that are grouped need row span of zero
        for ( int iRow = iStartRowIndex; iRow < iEndRowIndex; iRow++ ) {
            DynamicDataRow row = get( iRow );
            row.get( iColIndex ).setRowSpan( 0 );
        }
        cell1.setRowSpan( iEndRowIndex - iStartRowIndex );

    }

    //Initialise cell parameters when ungrouped
    private void ungroupCells(DynamicDataRow row) {
        for ( int iCol = 0; iCol < row.size(); iCol++ ) {
            CellValue< ? > cell = row.get( iCol );
            cell.removeState( CellState.GROUPED );
        }
    }

    /**
     * Remove all grouping throughout the model
     */
    void removeModelGrouping() {

        for ( int iRow = 0; iRow < size(); iRow++ ) {
            DynamicDataRow row = get( iRow );
            if ( row instanceof GroupedDynamicDataRow ) {
                List<DynamicDataRow> expandedRow = expandGroupedRow( row,
                                                                      true );
                remove( iRow );
                addAll( iRow,
                             expandedRow );
                iRow = iRow + expandedRow.size() - 1;
            }
        }

    }

    /**
     * Remove merging from model
     */
    void removeModelMerging() {

        for ( int iRow = 0; iRow < size(); iRow++ ) {
            DynamicDataRow row = get( iRow );

            for ( int iCol = 0; iCol < row.size(); iCol++ ) {
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

}