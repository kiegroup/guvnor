/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.decisiontable.widget;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A Vertical implementation of MergableGridWidget, that renders columns as erm,
 * columns and rows as rows. Supports merging of cells between rows.
 * 
 * @author manstis
 * 
 */
public class VerticalMergableGridWidget extends MergableGridWidget {

    public VerticalMergableGridWidget(final DecisionTableWidget dtable) {
        super( dtable );

        // Resize columns when header signals the need
        dtable.getHeaderWidget().addColumnResizeHandler(
                                                         new ColumnResizeHandler() {

                                                             public void onColumnResize(ColumnResizeEvent event) {
                                                                 DynamicColumn col = event.getColumn();
                                                                 int iCol = col.getColumnIndex();
                                                                 int width = event.getWidth();
                                                                 col.setWidth( width );
                                                                 for ( DynamicDataRow row : dtable.getData() ) {
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

                                                                 // Resizing
                                                                 // columns can
                                                                 // cause the
                                                                 // scrollbar to
                                                                 // appear
                                                                 dtable.assertDimensions();
                                                             }

                                                         } );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#deleteRow
     * (int)
     */
    @Override
    public void deleteRow(int index) {
        if ( index < 0 ) {
            throw new IllegalArgumentException(
                                                "Index cannot be less than zero." );
        }
        if ( index > dtable.getData().size() ) {
            throw new IllegalArgumentException(
                                                "Index cannot be greater than the number of rows." );
        }

        dtable.getSidebarWidget().deleteSelector( index );
        tbody.deleteRow( index );
        fixRowStyles( index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.client.decisiontable.widget.MergableGridWidget#deselectCell
     * (org.drools.guvnor.client.decisiontable.widget.CellValue)
     */
    @Override
    public void deselectCell(CellValue< ? extends Comparable< ? >> cell) {
        Coordinate hc = cell.getHtmlCoordinate();
        TableRowElement tre = tbody.getRows().getItem( hc.getRow() )
                .<TableRowElement> cast();
        TableCellElement tce = tre.getCells().getItem( hc.getCol() )
                .<TableCellElement> cast();

        String cellSelectedStyle = style.cellTableCellSelected();
        tce.removeClassName( cellSelectedStyle );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.client.decisiontable.widget.MergableGridWidget#hideColumn
     * (int)
     */
    @Override
    public void hideColumn(int index) {
        for ( int iRow = 0; iRow < dtable.getData().size(); iRow++ ) {
            DynamicDataRow rowData = dtable.getData().get( iRow );
            CellValue< ? extends Comparable< ? >> cell = rowData.get( index );

            if ( cell.getRowSpan() > 0 ) {
                Coordinate hc = cell.getHtmlCoordinate();
                TableRowElement tre = tbody.getRows().getItem( hc.getRow() );
                TableCellElement tce = tre.getCells().getItem( hc.getCol() );
                tre.removeChild( tce );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#
     * insertRowBefore(int)
     */
    @Override
    public void insertRowBefore(int index,
                                DynamicDataRow rowData) {
        if ( index < 0 ) {
            throw new IllegalArgumentException(
                                                "Index cannot be less than zero." );
        }
        if ( index > dtable.getData().size() ) {
            throw new IllegalArgumentException(
                                                "Index cannot be greater than the number of rows." );
        }
        if ( rowData == null ) {
            throw new IllegalArgumentException( "Row data cannot be null" );
        }

        dtable.getSidebarWidget().insertSelectorBefore( rowData,
                                                        index );
        TableRowElement newRow = tbody.insertRow( index );
        populateTableRowElement( newRow,
                                 rowData );
        fixRowStyles( index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#redraw()
     */
    @Override
    public void redraw() {

        // Prepare sidebar
        dtable.getSidebarWidget().initialise();

        TableSectionElement nbody = Document.get().createTBodyElement();

        for ( int iRow = 0; iRow < dtable.getData().size(); iRow++ ) {

            // Add a selector for each row
            DynamicDataRow rowData = dtable.getData().get( iRow );
            dtable.getSidebarWidget().appendSelector( rowData );

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.client.decisiontable.widget.MergableGridWidget#redrawColumn
     * (int)
     */
    @Override
    public void redrawColumn(int index) {
        if ( index < 0 ) {
            throw new IllegalArgumentException(
                                                "Column index cannot be less than zero." );
        }
        if ( index > dtable.getColumns().size() - 1 ) {
            throw new IllegalArgumentException(
                                                "Column index cannot be greater than the number of defined columns." );
        }

        for ( int iRow = 0; iRow < dtable.getData().size(); iRow++ ) {
            TableRowElement tre = tbody.getRows().getItem( iRow );
            DynamicDataRow rowData = dtable.getData().get( iRow );
            redrawTableRowElement( rowData,
                                   tre,
                                   index,
                                   index );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.guvnor.client.decisiontable.widget.MergableGridWidget#
     * redrawColumns(int, int)
     */
    @Override
    public void redrawColumns(int startRedrawIndex,
                              int endRedrawIndex) {
        if ( startRedrawIndex < 0 ) {
            throw new IllegalArgumentException(
                                                "Start Column index cannot be less than zero." );
        }
        if ( startRedrawIndex > dtable.getColumns().size() - 1 ) {
            throw new IllegalArgumentException(
                                                "Start Column index cannot be greater than the number of defined columns." );
        }
        if ( endRedrawIndex < 0 ) {
            throw new IllegalArgumentException(
                                                "End Column index cannot be less than zero." );
        }
        if ( endRedrawIndex > dtable.getColumns().size() - 1 ) {
            throw new IllegalArgumentException(
                                                "End Column index cannot be greater than the number of defined columns." );
        }
        if ( startRedrawIndex > endRedrawIndex ) {
            throw new IllegalArgumentException(
                                                "Start Column index cannot be greater than End Column index." );
        }

        for ( int iRow = 0; iRow < dtable.getData().size(); iRow++ ) {
            TableRowElement tre = tbody.getRows().getItem( iRow );
            DynamicDataRow rowData = dtable.getData().get( iRow );
            redrawTableRowElement( rowData,
                                   tre,
                                   startRedrawIndex,
                                   endRedrawIndex );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#redrawRows
     * (int, int)
     */
    @Override
    public void redrawRows(int startRedrawIndex,
                           int endRedrawIndex) {
        if ( startRedrawIndex < 0 ) {
            throw new IllegalArgumentException(
                                                "Start Row index cannot be less than zero." );
        }
        if ( startRedrawIndex > dtable.getData().size() - 1 ) {
            throw new IllegalArgumentException(
                                                "Start Row index cannot be greater than the number of rows in the table." );
        }
        if ( endRedrawIndex < 0 ) {
            throw new IllegalArgumentException(
                                                "End Row index cannot be less than zero." );
        }
        if ( endRedrawIndex > dtable.getData().size() - 1 ) {
            throw new IllegalArgumentException(
                                                "End Row index cannot be greater than the number of rows in the table." );
        }
        if ( endRedrawIndex < startRedrawIndex ) {
            throw new IllegalArgumentException(
                                                "End Row index cannot be greater than Start Row index." );
        }

        for ( int iRow = startRedrawIndex; iRow <= endRedrawIndex; iRow++ ) {
            TableRowElement newRow = Document.get().createTRElement();
            DynamicDataRow rowData = dtable.getData().get( iRow );
            populateTableRowElement( newRow,
                                     rowData );
            tbody.replaceChild( newRow,
                                tbody.getChild( iRow ) );
        }
        fixRowStyles( startRedrawIndex );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.client.decisiontable.widget.MergableGridWidget#selectCell
     * (org.drools.guvnor.client.decisiontable.widget.CellValue)
     */
    @Override
    public void selectCell(CellValue< ? extends Comparable< ? >> cell) {
        Coordinate hc = cell.getHtmlCoordinate();
        TableRowElement tre = tbody.getRows().getItem( hc.getRow() )
                .<TableRowElement> cast();
        TableCellElement tce = tre.getCells().getItem( hc.getCol() )
                .<TableCellElement> cast();

        String cellSelectedStyle = style.cellTableCellSelected();
        tce.addClassName( cellSelectedStyle );
        tce.focus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.client.decisiontable.widget.MergableGridWidget#showColumn
     * (int)
     */
    @Override
    public void showColumn(int index) {
        for ( int iRow = 0; iRow < dtable.getData().size(); iRow++ ) {
            DynamicDataRow rowData = dtable.getData().get( iRow );
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
        DynamicColumn column = dtable.getColumns().get( iCol );

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
            div.getStyle().setHeight( Math.floor( style.rowHeight() * 0.95 ),
                                      Unit.PX );
            tce.getStyle().setHeight( style.rowHeight()
                                              * rowSpan,
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

        for ( int iCol = 0; iCol < dtable.getColumns().size(); iCol++ ) {
            if ( dtable.getColumns().get( iCol ).isVisible() ) {
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
            DynamicColumn column = dtable.getColumns().get( iCol );
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
