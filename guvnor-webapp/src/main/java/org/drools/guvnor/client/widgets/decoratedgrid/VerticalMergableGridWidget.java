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

import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * A Vertical implementation of MergableGridWidget, that renders columns as erm,
 * columns and rows as rows. Supports merging of cells between rows.
 */
public class VerticalMergableGridWidget<T> extends MergableGridWidget<T> {

    public VerticalMergableGridWidget(final DecoratedGridWidget<T> grid) {
        super( grid );
    }

    @Override
    public void deleteRow(int index) {
        if ( index < 0 ) {
            throw new IllegalArgumentException(
                                                "Index cannot be less than zero." );
        }
        if ( index > data.size() ) {
            throw new IllegalArgumentException(
                                                "Index cannot be greater than the number of rows." );
        }

        grid.getSidebarWidget().deleteSelector( index );
        tbody.deleteRow( index );
        fixRowStyles( index );
    }

    @Override
    public void deselectCell(CellValue< ? extends Comparable< ? >> cell) {
        if ( cell == null ) {
            throw new IllegalArgumentException( "cell cannot be null" );
        }

        Coordinate hc = cell.getHtmlCoordinate();
        TableRowElement tre = tbody.getRows().getItem( hc.getRow() )
                .<TableRowElement> cast();
        TableCellElement tce = tre.getCells().getItem( hc.getCol() )
                .<TableCellElement> cast();

        String cellSelectedStyle = style.cellTableCellSelected();
        tce.removeClassName( cellSelectedStyle );
    }

    @Override
    public void hideColumn(int index) {
        if ( index < 0 ) {
            throw new IllegalArgumentException( "index cannot be less than zero" );
        }
        if ( index > columns.size() ) {
            throw new IllegalArgumentException( "index cannot be greater than the number of rows" );
        }

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow rowData = data.get( iRow );
            CellValue< ? extends Comparable< ? >> cell = rowData.get( index );

            if ( cell.getRowSpan() > 0 ) {
                Coordinate hc = cell.getHtmlCoordinate();
                TableRowElement tre = tbody.getRows().getItem( hc.getRow() );
                TableCellElement tce = tre.getCells().getItem( hc.getCol() );
                tre.removeChild( tce );
            }
        }
    }

    @Override
    public void insertRowBefore(int index,
                                DynamicDataRow rowData) {
        if ( index < 0 ) {
            throw new IllegalArgumentException(
                                                "Index cannot be less than zero." );
        }
        if ( index > data.size() ) {
            throw new IllegalArgumentException(
                                                "Index cannot be greater than the number of rows." );
        }
        if ( rowData == null ) {
            throw new IllegalArgumentException( "Row data cannot be null" );
        }
        if ( rowData.size() != columns.size() ) {
            throw new IllegalArgumentException( "rowData contains a different number of columns to the grid" );
        }

        grid.getSidebarWidget().insertSelectorBefore( rowData,
                                                        index );
        TableRowElement newRow = tbody.insertRow( index );
        populateTableRowElement( newRow,
                                 rowData );
        fixRowStyles( index );
    }

    @Override
    public void onBrowserEvent(Event event) {

        String eventType = event.getType();
        // Get the event target.
        EventTarget eventTarget = event.getEventTarget();
        if ( !Element.is( eventTarget ) ) {
            return;
        }
        Element target = event.getEventTarget().cast();

        // Find the cell where the event occurred.
        TableCellElement tableCell = findNearestParentCell( target );
        if ( tableCell == null ) {
            return;
        }
        int htmlCol = tableCell.getCellIndex();

        Element trElem = tableCell.getParentElement();
        if ( trElem == null ) {
            return;
        }
        TableRowElement tr = TableRowElement.as( trElem );
        int htmlRow = tr.getSectionRowIndex();

        // Convert HTML coordinates to physical coordinates
        DynamicDataRow htmlRowData = data.get( htmlRow );
        CellValue< ? extends Comparable< ? >> htmlCell = htmlRowData.get( htmlCol );
        Coordinate c = htmlCell.getPhysicalCoordinate();
        CellValue< ? extends Comparable< ? >> physicalCell = data.get( c.getRow() ).get( c.getCol() );

        // Select range
        if ( eventType.equals( "mousedown" ) ) {
            if ( event.getShiftKey() ) {
                extendSelection( c );
                return;
            } else {
                startSelecting( c );
                bDragOperationPrimed = true;
                return;
            }
        } else if ( bDragOperationPrimed && eventType.equals( "mousemove" ) ) {
            extendSelection( c );
            return;
        } else if ( eventType.equals( "mouseup" ) ) {
            bDragOperationPrimed = false;
            return;
        }

        // Keyboard navigation
        if ( eventType.equals( "keydown" ) ) {
            if ( event.getKeyCode() == KeyCodes.KEY_DELETE ) {
                grid.update( null );
            } else if ( event.getKeyCode() == KeyCodes.KEY_RIGHT
                        || (event.getKeyCode() == KeyCodes.KEY_TAB && !event
                                .getShiftKey()) ) {
                CellExtents ce = moveSelection( MOVE_DIRECTION.RIGHT );
                SelectedCellChangeEvent.fire( grid,
                                              ce );
                event.preventDefault();
            } else if ( event.getKeyCode() == KeyCodes.KEY_LEFT
                        || (event.getKeyCode() == KeyCodes.KEY_TAB && event
                                .getShiftKey()) ) {
                CellExtents ce = moveSelection( MOVE_DIRECTION.LEFT );
                SelectedCellChangeEvent.fire( grid,
                                              ce );
                event.preventDefault();
            } else if ( event.getKeyCode() == KeyCodes.KEY_UP ) {
                if ( event.getShiftKey() ) {
                    extendSelection( MOVE_DIRECTION.UP );
                } else {
                    CellExtents ce = moveSelection( MOVE_DIRECTION.UP );
                    SelectedCellChangeEvent.fire( grid,
                                                  ce );
                }
                event.preventDefault();
            } else if ( event.getKeyCode() == KeyCodes.KEY_DOWN ) {
                if ( event.getShiftKey() ) {
                    extendSelection( MOVE_DIRECTION.DOWN );
                } else {
                    CellExtents ce = moveSelection( MOVE_DIRECTION.DOWN );
                    SelectedCellChangeEvent.fire( grid,
                                                  ce );
                }
                event.preventDefault();
            }
        }
        // Enter key is a special case; as the selected cell needs to be
        // sent events and not the cell that GWT deemed the target for
        // events.
        if ( eventType.equals( "keydown" ) ) {
            if ( event.getKeyCode() == KeyCodes.KEY_ENTER ) {

                physicalCell = getSelections().first();
                c = physicalCell.getCoordinate();
                tableCell = tbody.getRows()
                        .getItem( physicalCell.getHtmlCoordinate().getRow() )
                        .getCells()
                        .getItem( physicalCell.getHtmlCoordinate().getCol() );
            }
        }

        // Pass event and physical cell to Cell Widget for handling
        Cell<CellValue< ? extends Comparable< ? >>> cellWidget = columns.get( c.getCol() ).getCell();

        // Implementations of AbstractCell aren't forced to initialise
        // consumed events
        Set<String> consumedEvents = cellWidget.getConsumedEvents();
        if ( consumedEvents != null
             && consumedEvents.contains( eventType ) ) {
            Context context = new Context( c.getRow(),
                                           c.getCol(),
                                           c );
            cellWidget.onBrowserEvent( context,
                                       tableCell.getFirstChildElement(),
                                       physicalCell,
                                       event,
                                       null );
        }
    }

    @Override
    public void redraw() {

        // Prepare sidebar
        grid.getSidebarWidget().initialise();

        TableSectionElement nbody = Document.get().createTBodyElement();

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {

            // Add a selector for each row
            DynamicDataRow rowData = data.get( iRow );
            grid.getSidebarWidget().appendSelector( rowData );

            TableRowElement tre = Document.get().createTRElement();
            tre.setClassName( getRowStyle( iRow ) );

            populateTableRowElement( tre,
                                     rowData );
            nbody.appendChild( tre );

        }

        // Update table to DOM
        table.replaceChild( nbody,
                            tbody );
        tbody = nbody;

    }

    @Override
    public void redrawColumn(int index) {
        if ( index < 0 ) {
            throw new IllegalArgumentException(
                                                "index cannot be less than zero." );
        }
        if ( index > columns.size() ) {
            throw new IllegalArgumentException(
                                                "index cannot be greater than the number of defined columns." );
        }

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            TableRowElement tre = tbody.getRows().getItem( iRow );
            DynamicDataRow rowData = data.get( iRow );
            redrawTableRowElement( rowData,
                                   tre,
                                   index,
                                   index );
        }

    }

    @Override
    public void redrawColumns(int startRedrawIndex,
                              int endRedrawIndex) {
        if ( startRedrawIndex < 0 ) {
            throw new IllegalArgumentException(
                                                "startRedrawIndex cannot be less than zero." );
        }
        if ( startRedrawIndex > columns.size() ) {
            throw new IllegalArgumentException(
                                                "startRedrawIndex cannot be greater than the number of defined columns." );
        }
        if ( endRedrawIndex < 0 ) {
            throw new IllegalArgumentException(
                                                "endRedrawIndex cannot be less than zero." );
        }
        if ( endRedrawIndex > columns.size() ) {
            throw new IllegalArgumentException(
                                                "endRedrawIndex cannot be greater than the number of defined columns." );
        }
        if ( startRedrawIndex > endRedrawIndex ) {
            throw new IllegalArgumentException(
                                                "startRedrawIndex cannot be greater than endRedrawIndex." );
        }

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            TableRowElement tre = tbody.getRows().getItem( iRow );
            DynamicDataRow rowData = data.get( iRow );
            redrawTableRowElement( rowData,
                                   tre,
                                   startRedrawIndex,
                                   endRedrawIndex );
        }
    }

    @Override
    public void redrawRows(int startRedrawIndex,
                           int endRedrawIndex) {
        if ( startRedrawIndex < 0 ) {
            throw new IllegalArgumentException(
                                                "startRedrawIndex cannot be less than zero." );
        }
        if ( startRedrawIndex > data.size() ) {
            throw new IllegalArgumentException(
                                                "startRedrawIndex cannot be greater than the number of rows in the table." );
        }
        if ( endRedrawIndex < 0 ) {
            throw new IllegalArgumentException(
                                                "endRedrawIndex cannot be less than zero." );
        }
        if ( endRedrawIndex > data.size() ) {
            throw new IllegalArgumentException(
                                                "endRedrawIndex cannot be greater than the number of rows in the table." );
        }
        if ( startRedrawIndex > endRedrawIndex ) {
            throw new IllegalArgumentException(
                                                "startRedrawIndex cannot be greater than endRedrawIndex." );
        }

        for ( int iRow = startRedrawIndex; iRow <= endRedrawIndex; iRow++ ) {
            TableRowElement newRow = Document.get().createTRElement();
            DynamicDataRow rowData = data.get( iRow );
            populateTableRowElement( newRow,
                                     rowData );
            tbody.replaceChild( newRow,
                                tbody.getChild( iRow ) );
        }
        fixRowStyles( startRedrawIndex );
    }

    @Override
    public void resizeColumn(DynamicColumn< ? > col,
                             int width) {
        if ( col == null ) {
            throw new IllegalArgumentException( "col cannot be null" );
        }
        if ( width < 0 ) {
            throw new IllegalArgumentException( "width cannot be less than zero" );
        }

        col.setWidth( width );
        int iCol = col.getColumnIndex();
        for ( DynamicDataRow row : data ) {
            CellValue< ? extends Comparable< ? >> cell = row
                    .get( iCol );
            Coordinate c = cell.getHtmlCoordinate();
            TableRowElement tre = tbody.getRows().getItem(
                                                           c.getRow() );
            TableCellElement tce = tre.getCells().getItem(
                                                           c.getCol() );
            tce.getFirstChild().<DivElement> cast().getStyle()
                    .setWidth( width,
                               Unit.PX );
        }

        // Resizing columns can cause the scrollbar to appear
        grid.assertDimensions();

    }

    @Override
    public void selectCell(CellValue< ? extends Comparable< ? >> cell) {
        if ( cell == null ) {
            throw new IllegalArgumentException( "cell cannot be null" );
        }

        Coordinate hc = cell.getHtmlCoordinate();
        TableRowElement tre = tbody.getRows().getItem( hc.getRow() )
                .<TableRowElement> cast();
        TableCellElement tce = tre.getCells().getItem( hc.getCol() )
                .<TableCellElement> cast();

        String cellSelectedStyle = style.cellTableCellSelected();
        tce.addClassName( cellSelectedStyle );
        tce.focus();
    }

    @Override
    public void showColumn(int index) {
        if ( index < 0 ) {
            throw new IllegalArgumentException( "index cannot be less than zero" );
        }
        if ( index > columns.size() ) {
            throw new IllegalArgumentException( "index cannot be greater than the number of rows" );
        }

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow rowData = data.get( iRow );
            TableCellElement tce = makeTableCellElement( index,
                                                         rowData );
            if ( tce != null ) {

                CellValue< ? extends Comparable< ? >> cell = rowData.get( index );
                Coordinate hc = cell.getHtmlCoordinate();

                TableRowElement tre = tbody.getRows().getItem( hc.getRow() );
                TableCellElement ntce = tre.insertCell( hc.getCol() );
                tre.replaceChild( tce,
                                  ntce );
            }
        }
    }

    // Find the cell that contains the element. Note that the TD element is not
    // the parent. The parent is the div inside the TD cell.
    private TableCellElement findNearestParentCell(Element elem) {
        while ( (elem != null)
                && (elem != table) ) {
            String tagName = elem.getTagName();
            if ( "td".equalsIgnoreCase( tagName )
                    || "th".equalsIgnoreCase( tagName ) ) {
                return elem.cast();
            }
            elem = elem.getParentElement();
        }
        return null;
    }

    // Row styles need to be re-applied after inserting and deleting rows
    private void fixRowStyles(int iRow) {
        while ( iRow < tbody.getChildCount() ) {
            Element e = Element.as( tbody.getChild( iRow ) );
            TableRowElement tre = TableRowElement.as( e );
            tre.setClassName( getRowStyle( iRow ) );
            iRow++;
        }
    }

    // Get style applicable to row
    private String getRowStyle(int iRow) {
        String evenRowStyle = style.cellTableEvenRow();
        String oddRowStyle = style.cellTableOddRow();
        boolean isEven = iRow % 2 == 0;
        String trClasses = isEven ? evenRowStyle : oddRowStyle;
        return trClasses;
    }

    // Build a TableCellElement
    private TableCellElement makeTableCellElement(int iCol,
                                                  DynamicDataRow rowData) {

        String cellStyle = style.cellTableCell();
        String divStyle = style.cellTableCellDiv();
        String cellSelectedStyle = style.cellTableCellSelected();
        TableCellElement tce = null;

        // Column to render the column
        DynamicColumn< ? > column = columns.get( iCol );

        CellValue< ? extends Comparable< ? >> cellData = rowData.get( iCol );
        int rowSpan = cellData.getRowSpan();
        if ( rowSpan > 0 ) {

            // Use Elements rather than Templates as it's easier to set
            // attributes that need to be dynamic
            tce = Document.get().createTDElement();
            DivElement div = Document.get().createDivElement();
            if ( cellData.isSelected() ) {
                tce.addClassName( cellSelectedStyle );
            }
            tce.addClassName( cellStyle );
            div.setClassName( divStyle );

            // Dynamic attributes!
            div.getStyle().setWidth( column.getWidth(),
                                     Unit.PX );
            tce.setRowSpan( rowSpan );

            // Render the cell and set inner HTML
            Coordinate c = cellData.getCoordinate();
            Context context = new Context( c.getRow(),
                                           c.getCol(),
                                           c );
            SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
            column.render( context,
                           rowData,
                           cellBuilder );
            div.setInnerHTML( cellBuilder.toSafeHtml().asString() );

            // Construct the table
            tce.appendChild( div );
            tce.setTabIndex( 0 );
        }
        return tce;

    }

    // Populate the content of a TableRowElement. This is used to populate
    // new, empty, TableRowElements with complete rows for insertion into an
    // HTML table based upon visible columns
    private TableRowElement populateTableRowElement(TableRowElement tre,
                                                    DynamicDataRow rowData) {

        tre.getStyle().setHeight( style.rowHeight(),
                                  Unit.PX );
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            if ( columns.get( iCol ).isVisible() ) {
                TableCellElement tce = makeTableCellElement( iCol,
                                                             rowData );
                if ( tce != null ) {
                    tre.appendChild( tce );
                }
            }
        }

        return tre;

    }

    // Redraw a row adding new cells if necessary. This is used to populate part
    // of a row from the given index onwards, when a new column has been
    // inserted. It is important the indexes on the underlying data have
    // been set correctly before calling as they are used to determine the
    // correct HTML element in which to render a cell.
    private void redrawTableRowElement(DynamicDataRow rowData,
                                       TableRowElement tre,
                                       int startColIndex,
                                       int endColIndex) {

        for ( int iCol = startColIndex; iCol <= endColIndex; iCol++ ) {

            // Only redraw visible columns
            DynamicColumn< ? > column = columns.get( iCol );
            if ( column.isVisible() ) {

                int maxColumnIndex = tre.getCells().getLength() - 1;
                int requiredColumnIndex = rowData.get( iCol ).getHtmlCoordinate()
                        .getCol();
                if ( requiredColumnIndex > maxColumnIndex ) {

                    // Make a new TD element
                    TableCellElement newCell = makeTableCellElement( iCol,
                                                                     rowData );
                    if ( newCell != null ) {
                        tre.appendChild( newCell );
                    }

                } else {

                    // Reuse an existing TD element
                    TableCellElement newCell = makeTableCellElement( iCol,
                                                                     rowData );
                    if ( newCell != null ) {
                        TableCellElement oldCell = tre.getCells().getItem(
                                                                           requiredColumnIndex );
                        tre.replaceChild( newCell,
                                          oldCell );
                    }
                }
            }
        }

    }

}
