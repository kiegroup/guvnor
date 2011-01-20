package org.drools.guvnor.client.decisiontable.widget;

import java.util.Set;

import org.drools.guvnor.client.decisiontable.widget.DecisionTableWidget.MOVE_DIRECTION;
import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * A minimal CellTable replacement that renders merged cells and handles basic
 * events. No keyboard navigation implemented.
 * 
 * @author manstis
 * 
 */
public abstract class MergableGridWidget extends Widget {

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

    // The DecisionTable to which this grid belongs
    protected DecisionTableWidget                 dtable;

    /**
     * A grid of possibly merged cells.
     * 
     * @param dtable
     *            DecisionTable to which the grid is a child
     * @param resource
     *            ClientBundle for the grid
     */
    public MergableGridWidget(DecisionTableWidget dtable) {

        this.dtable = dtable;

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
     * Retrieve the extents of a cell
     * 
     * @param cv
     *            The cell for which to retrieve the extents
     * @return
     */
    public CellExtents getSelectedCellExtents(
                                              CellValue< ? extends Comparable< ? >> cv) {

        if ( cv == null ) {
            throw new IllegalArgumentException( "CellValue cannot be null" );
        }

        // Cells in hidden columns do not have extents
        if ( !dtable.getColumns().get( cv.getCoordinate().getCol() )
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user
     * .client.Event)
     */
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
        DynamicDataRow htmlRowData = dtable.getData().get( htmlRow );
        CellValue< ? extends Comparable< ? >> htmlCell = htmlRowData.get( htmlCol );
        Coordinate c = htmlCell.getPhysicalCoordinate();
        CellValue< ? extends Comparable< ? >> physicalCell = dtable.getData()
                .get( c.getRow() ).get( c.getCol() );

        // Select range
        if ( eventType.equals( "click" ) ) {
            dtable.startSelecting( c );
        }

        // Keyboard navigation
        if ( eventType.equals( "keypress" ) ) {
            if ( event.getKeyCode() == KeyCodes.KEY_DELETE ) {
                dtable.update( null );
            } else if ( event.getKeyCode() == KeyCodes.KEY_RIGHT
                        || (event.getKeyCode() == KeyCodes.KEY_TAB && !event
                                .getShiftKey()) ) {
                dtable.moveSelection( MOVE_DIRECTION.RIGHT );
                event.preventDefault();
            } else if ( event.getKeyCode() == KeyCodes.KEY_LEFT
                        || (event.getKeyCode() == KeyCodes.KEY_TAB && event
                                .getShiftKey()) ) {
                dtable.moveSelection( MOVE_DIRECTION.LEFT );
                event.preventDefault();
            } else if ( event.getKeyCode() == KeyCodes.KEY_UP ) {
                dtable.moveSelection( MOVE_DIRECTION.UP );
                event.preventDefault();
            } else if ( event.getKeyCode() == KeyCodes.KEY_DOWN ) {
                dtable.moveSelection( MOVE_DIRECTION.DOWN );
                event.preventDefault();
            }
        }
        // Enter key is a special case; as the selected cell needs to be
        // sent events and not the cell that GWT deemed the target for
        // events.
        if ( eventType.equals( "keydown" ) ) {
            if ( event.getKeyCode() == KeyCodes.KEY_ENTER ) {

                physicalCell = dtable.getSelections().first();
                c = physicalCell.getCoordinate();
                tableCell = tbody.getRows()
                        .getItem( physicalCell.getHtmlCoordinate().getRow() )
                        .getCells()
                        .getItem( physicalCell.getHtmlCoordinate().getCol() );
            }
        }

        // Pass event and physical cell to Cell Widget for handling
        Cell<CellValue< ? extends Comparable< ? >>> cellWidget = dtable
                .getColumns().get( c.getCol() ).getCell();

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
     * Redraw table columns from index to the end. Partial redraw
     * 
     * @param startRedrawIndex
     *            Start column index (inclusive)
     * @param endRedrawIndex
     *            End column index (inclusive)
     */
    public abstract void redrawColumns(int startRedrawIndex,
                                       int endRedrawIndex);

    /**
     * Redraw a section of the table. Partial redraw
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

}
