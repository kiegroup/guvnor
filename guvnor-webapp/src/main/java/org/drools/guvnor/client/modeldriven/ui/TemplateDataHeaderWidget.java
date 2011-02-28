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
package org.drools.guvnor.client.modeldriven.ui;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.widgets.decoratedgrid.ColumnResizeEvent;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridHeaderWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.decoratedgrid.SortConfiguration;
import org.drools.guvnor.client.widgets.tables.SortDirection;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Header for a Vertical Decision Table
 */
public class TemplateDataHeaderWidget extends
        DecoratedGridHeaderWidget<TemplateDataColumn> {

    /**
     * This is the guts of the widget.
     */
    private class HeaderWidget extends CellPanel {

        /**
         * A Widget to display sort order
         */
        private class HeaderSorter extends FocusPanel {

            private final HorizontalPanel                   hp = new HorizontalPanel();
            private final DynamicColumn<TemplateDataColumn> col;

            private HeaderSorter(final DynamicColumn<TemplateDataColumn> col) {
                this.col = col;
                hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
                hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
                hp.setHeight( style.rowHeaderSorterHeight()
                              + "px" );
                hp.setWidth( "100%" );
                setIconImage();
                add( hp );

                // Ensure our icon is updated when the SortDirection changes
                col.addValueChangeHandler( new ValueChangeHandler<SortConfiguration>() {

                    public void onValueChange(
                                              ValueChangeEvent<SortConfiguration> event) {
                        setIconImage();
                    }

                } );
            }

            // Set icon's resource accordingly
            private void setIconImage() {
                hp.clear();
                switch ( col.getSortDirection() ) {
                    case ASCENDING :
                        switch ( col.getSortIndex() ) {
                            case 0 :
                                hp.add( new Image( resource.upArrow() ) );
                                break;
                            default :
                                hp.add( new Image( resource.smallUpArrow() ) );
                        }
                        break;
                    case DESCENDING :
                        switch ( col.getSortIndex() ) {
                            case 0 :
                                hp.add( new Image( resource.downArrow() ) );
                                break;
                            default :
                                hp.add( new Image( resource.smallDownArrow() ) );
                        }
                        break;
                }
            }

        }

        // Child Widgets used in this Widget
        private List<HeaderSorter>                      sorters       = new ArrayList<HeaderSorter>();

        // UI Components
        private Element[]                               headerRows    = new Element[2];
        private List<DynamicColumn<TemplateDataColumn>> headerColumns = new ArrayList<DynamicColumn<TemplateDataColumn>>();

        // Constructor
        private HeaderWidget() {
            for ( int iRow = 0; iRow < headerRows.length; iRow++ ) {
                headerRows[iRow] = DOM.createTR();
                getBody().appendChild( headerRows[iRow] );
            }
            getBody().getParentElement().<TableElement> cast()
                    .setCellSpacing( 0 );
            getBody().getParentElement().<TableElement> cast()
                    .setCellPadding( 0 );
        }

        // Make default header label
        private Element makeLabel(String text,
                                  int width,
                                  int height) {
            Element div = DOM.createDiv();
            div.getStyle().setWidth( width,
                                     Unit.PX );
            div.getStyle().setHeight( height,
                                      Unit.PX );
            div.getStyle().setOverflow( Overflow.HIDDEN );
            div.setInnerText( text );
            return div;
        }

        // Populate a default header element
        private void populateTableCellElement(DynamicColumn<TemplateDataColumn> col,
                                              Element tce) {

            TemplateDataColumn modelCol = col.getModelColumn();
            tce.appendChild( makeLabel( modelCol.getTemplateVar(),
                                        col.getWidth(),
                                        style.rowHeaderHeight() ) );
            tce.addClassName( style.headerRowIntermediate() );
        }

        // Redraw entire header
        private void redraw() {

            // Remove existing widgets from the DOM hierarchy
            for ( HeaderSorter sorter : sorters ) {
                remove( sorter );
            }
            sorters.clear();

            // Extracting visible columns makes life easier
            headerColumns.clear();
            final List<DynamicColumn<TemplateDataColumn>> columns = grid.getGridWidget().getColumns();
            for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
                DynamicColumn<TemplateDataColumn> col = columns.get( iCol );
                headerColumns.add( col );
            }

            // Draw rows
            for ( int iRow = 0; iRow < headerRows.length; iRow++ ) {
                redrawHeaderRow( iRow );
            }

            // Schedule resize event after header has been drawn
            Scheduler.get().scheduleDeferred( new ScheduledCommand() {
                public void execute() {
                    ResizeEvent.fire( TemplateDataHeaderWidget.this,
                                      getBody().getClientWidth(),
                                      getBody()
                                              .getClientHeight() );
                }
            } );

        }

        // Redraw a single row obviously
        private void redrawHeaderRow(int iRow) {
            Element tce = null;
            Element tre = DOM.createTR();
            switch ( iRow ) {
                case 0 :
                    for ( DynamicColumn<TemplateDataColumn> col : headerColumns ) {
                        tce = DOM.createTD();
                        tce.addClassName( style.headerText() );
                        tre.appendChild( tce );
                        populateTableCellElement( col,
                                                      tce );
                    }
                    break;

                case 1 :
                    // Sorters
                    for ( DynamicColumn<TemplateDataColumn> col : headerColumns ) {
                        final HeaderSorter shp = new HeaderSorter( col );
                        final DynamicColumn<TemplateDataColumn> sortableColumn = col;
                        shp.addClickHandler( new ClickHandler() {

                            public void onClick(ClickEvent event) {
                                if ( sortableColumn.isSortable() ) {
                                    updateSortOrder( sortableColumn );
                                    grid.sort();
                                }
                            }

                        } );
                        sorters.add( shp );

                        tce = DOM.createTD();
                        tce.addClassName( style.headerRowBottom() );
                        tre.appendChild( tce );
                        add( shp,
                                 tce );
                    }
                    break;

            }

            getBody().replaceChild( tre,
                                    headerRows[iRow] );
            headerRows[iRow] = tre;
        }

        // Update sort order. The column clicked becomes the primary sort column
        // and the other, previously sorted, columns degrade in priority
        private void updateSortOrder(DynamicColumn<TemplateDataColumn> column) {
            if ( column.getSortIndex() == 0 ) {
                if ( column.getSortDirection() != SortDirection.ASCENDING ) {
                    column.setSortDirection( SortDirection.ASCENDING );
                } else {
                    column.setSortDirection( SortDirection.DESCENDING );
                }
            } else {
                column.setSortIndex( 0 );
                column.setSortDirection( SortDirection.ASCENDING );
                int sortIndex = 1;
                final List<DynamicColumn<TemplateDataColumn>> columns = grid.getGridWidget().getColumns();
                for ( DynamicColumn<TemplateDataColumn> sortableColumn : columns ) {
                    if ( !sortableColumn.equals( column ) ) {
                        if ( sortableColumn.getSortDirection() != SortDirection.NONE ) {
                            sortableColumn.setSortIndex( sortIndex );
                            sortIndex++;
                        }
                    }
                }
            }
        }

    }

    // UI Components
    private HeaderWidget widget;

    /**
     * Construct a "Header" for the provided DecoratedGridWidget
     * 
     * @param grid
     */
    public TemplateDataHeaderWidget(final DecoratedGridWidget<TemplateDataColumn> grid) {
        super( grid );
    }

    @Override
    public void redraw() {
        widget.redraw();
    }

    @Override
    public void setScrollPosition(int position) {
        if ( position < 0 ) {
            throw new IllegalArgumentException( "position cannot be null" );
        }

        ((ScrollPanel) this.panel).setHorizontalScrollPosition( position );
    }

    // Resize the inner DIV in each table cell
    protected void resizeColumn(DynamicColumn<TemplateDataColumn> resizeColumn,
                                int resizeColumnWidth) {
        DivElement div;
        TableCellElement tce;

        // This is also set in the ColumnResizeEvent handler, however it makes
        // resizing columns in the header more simple too
        resizeColumn.setWidth( resizeColumnWidth );
        int resizeColumnIndex = widget.headerColumns.indexOf( resizeColumn );

        // Row 0 (General\Fact Type)
        tce = widget.headerRows[0].getChild( resizeColumnIndex )
                .<TableCellElement> cast();
        div = tce.getFirstChild().<DivElement> cast();
        div.getStyle().setWidth( resizeColumnWidth,
                                 Unit.PX );

        // Row 1 (Sorters)
        tce = widget.headerRows[1].getChild( resizeColumnIndex )
                .<TableCellElement> cast();
        div = tce.getFirstChild().<DivElement> cast();
        div.getStyle().setWidth( resizeColumnWidth,
                                 Unit.PX );

        // Fire event to any interested consumers
        ColumnResizeEvent.fire( this,
                                widget.headerColumns.get( resizeColumnIndex ),
                                resizeColumnWidth );
    }

    @Override
    protected Widget getHeaderWidget() {
        if ( this.widget == null ) {
            this.widget = new HeaderWidget();
        }
        return this.widget;
    }

    @Override
    protected ResizerInformation getResizerInformation(int mx) {
        boolean isPrimed = false;
        ResizerInformation resizerInfo = new ResizerInformation();
        for ( int iCol = 0; iCol < widget.headerRows[0].getChildCount(); iCol++ ) {
            TableCellElement tce = widget.headerRows[0].getChild(
                                                                  iCol ).<TableCellElement> cast();
            int cx = tce.getAbsoluteRight();
            if ( Math.abs( mx
                           - cx ) <= 5 ) {
                isPrimed = true;
                resizerInfo.setResizePrimed( isPrimed );
                resizerInfo.setResizeColumn( widget.headerColumns.get( iCol ) );
                resizerInfo.setResizeColumnLeft( tce.getAbsoluteLeft() );
                break;
            }
        }
        if ( isPrimed ) {
            setCursorType( Cursor.COL_RESIZE );
        } else {
            setCursorType( Cursor.DEFAULT );
        }

        return resizerInfo;
    }

    // Set the cursor type for all cells on the table as
    // we only use rowHeader[0] to check which column
    // needs resizing however the mouse could be over any
    // row
    private void setCursorType(Cursor cursor) {
        for ( int iRow = 0; iRow < widget.headerRows.length; iRow++ ) {
            TableRowElement tre = widget.headerRows[iRow]
                        .<TableRowElement> cast();
            for ( int iCol = 0; iCol < tre.getCells().getLength(); iCol++ ) {
                TableCellElement tce = tre.getCells().getItem( iCol );
                tce.getStyle().setCursor( cursor );
            }
        }
    }

}
