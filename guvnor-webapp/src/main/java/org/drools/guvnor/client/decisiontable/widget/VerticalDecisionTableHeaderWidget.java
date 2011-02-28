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
package org.drools.guvnor.client.decisiontable.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.widgets.decoratedgrid.ColumnResizeEvent;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridHeaderWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.decoratedgrid.SortConfiguration;
import org.drools.guvnor.client.widgets.tables.SortDirection;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.DescriptionCol;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

import com.google.gwt.animation.client.Animation;
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
public class VerticalDecisionTableHeaderWidget extends
        DecoratedGridHeaderWidget<DTColumnConfig> {

    /**
     * This is the guts of the widget.
     */
    private class HeaderWidget extends CellPanel {

        /**
         * A Widget to display sort order
         */
        private class HeaderSorter extends FocusPanel {

            private final HorizontalPanel               hp = new HorizontalPanel();
            private final DynamicColumn<DTColumnConfig> col;

            private HeaderSorter(final DynamicColumn<DTColumnConfig> col) {
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

        /**
         * A Widget to split Conditions section
         */
        private class HeaderSplitter extends FocusPanel {

            /**
             * Animation to change the height of a row
             */
            private class HeaderRowAnimation extends Animation {

                private TableRowElement tre;
                private int             startHeight;
                private int             endHeight;

                private HeaderRowAnimation(TableRowElement tre,
                                           int startHeight,
                                           int endHeight) {
                    this.tre = tre;
                    this.startHeight = startHeight;
                    this.endHeight = endHeight;
                }

                @Override
                protected void onComplete() {
                    super.onComplete();
                    setHeight( endHeight );
                }

                @Override
                protected void onUpdate(double progress) {
                    int height = (int) (startHeight + (progress * (endHeight - startHeight)));
                    setHeight( height );
                }

                // Set row height by setting height of children
                private void setHeight(int height) {
                    for ( int i = 0; i < tre.getChildCount(); i++ ) {
                        tre.getChild( i ).getFirstChild().<DivElement> cast()
                                .getStyle().setHeight( height,
                                                       Unit.PX );
                    }

                    // Decision Table and Sidebar need to know of new height
                    ResizeEvent.fire( VerticalDecisionTableHeaderWidget.this,
                                      getBody().getClientWidth(),
                                      getBody()
                                              .getClientHeight() );
                }

            }

            private Element[]             rowHeaders;
            private final HorizontalPanel hp          = new HorizontalPanel();
            private final Image           icon        = new Image();
            private boolean               isCollapsed = true;

            private HeaderSplitter() {
                hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
                hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
                hp.setWidth( "100%" );
                setIconImage();
                hp.add( icon );
                add( hp );

                // Handle action
                addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        if ( isCollapsed ) {
                            showRow( 2 );
                            showRow( 3 );
                        } else {
                            hideRow( 2 );
                            hideRow( 3 );
                        }
                        isCollapsed = !isCollapsed;
                        setIconImage();
                    }

                } );
            }

            // Hide a row using our animation
            private void hideRow(int iRow) {
                if ( rowHeaders == null
                     || (rowHeaders.length - 1) < iRow ) {
                    return;
                }
                TableRowElement tre = rowHeaders[iRow].<TableRowElement> cast();
                HeaderRowAnimation anim = new HeaderRowAnimation( tre,
                                                                  style.rowHeaderHeight(),
                                                                  0 );
                anim.run( 250 );
            }

            // Set icon's resource accordingly
            private void setIconImage() {
                if ( isCollapsed ) {
                    icon.setResource( resource.smallUpArrow() );
                } else {
                    icon.setResource( resource.smallDownArrow() );
                }
            }

            // Set rows to animate
            private void setRowHeaders(Element[] rowHeaders) {
                this.rowHeaders = rowHeaders;
            }

            // Show a row using our animation
            private void showRow(int iRow) {
                if ( rowHeaders == null
                     || (rowHeaders.length - 1) < iRow ) {
                    return;
                }
                TableRowElement tre = rowHeaders[iRow].<TableRowElement> cast();
                HeaderRowAnimation anim = new HeaderRowAnimation( tre,
                                                                  0,
                                                                  style.rowHeaderHeight() );
                anim.run( 250 );
            }

        }

        // Child Widgets used in this Widget
        private List<HeaderSorter>                  sorters              = new ArrayList<HeaderSorter>();
        private HeaderSplitter                      splitter             = new HeaderSplitter();

        // UI Components
        private Element[]                           rowHeaders           = new Element[5];

        private List<DynamicColumn<DTColumnConfig>> visibleCols          = new ArrayList<DynamicColumn<DTColumnConfig>>();
        private List<DynamicColumn<DTColumnConfig>> visibleConditionCols = new ArrayList<DynamicColumn<DTColumnConfig>>();

        // Constructor
        private HeaderWidget() {
            for ( int iRow = 0; iRow < rowHeaders.length; iRow++ ) {
                rowHeaders[iRow] = DOM.createTR();
                getBody().appendChild( rowHeaders[iRow] );
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
        private void populateTableCellElement(DynamicColumn<DTColumnConfig> col,
                                              Element tce) {

            DTColumnConfig modelCol = col.getModelColumn();
            if ( modelCol instanceof RowNumberCol ) {
                tce.appendChild( makeLabel( "#",
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            } else if ( modelCol instanceof DescriptionCol ) {
                tce.appendChild( makeLabel( constants.Description(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            } else if ( modelCol instanceof MetadataCol ) {
                tce.appendChild( makeLabel( ((MetadataCol) modelCol).getMetadata(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            } else if ( modelCol instanceof AttributeCol ) {
                tce.appendChild( makeLabel( ((AttributeCol) modelCol).getAttribute(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            } else if ( modelCol instanceof ConditionCol ) {
                tce.appendChild( makeLabel(
                                            ((ConditionCol) modelCol).getHeader(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
            } else if ( modelCol instanceof ActionCol ) {
                tce.appendChild( makeLabel( ((ActionCol) modelCol).getHeader(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            }

        }

        // Redraw entire header
        private void redraw() {

            // Remove existing widgets from the DOM hierarchy
            if ( splitter != null ) {
                remove( splitter );
            }
            for ( HeaderSorter sorter : sorters ) {
                remove( sorter );
            }
            sorters.clear();

            // Extracting visible columns makes life easier
            visibleCols.clear();
            visibleConditionCols.clear();
            for ( int iCol = 0; iCol < grid.getGridWidget().getColumns().size(); iCol++ ) {
                DynamicColumn<DTColumnConfig> col = grid.getGridWidget().getColumns().get( iCol );
                if ( col.isVisible() ) {
                    visibleCols.add( col );
                    DTColumnConfig modelCol = col.getModelColumn();
                    if ( modelCol instanceof ConditionCol ) {
                        visibleConditionCols.add( col );
                    }
                }
            }

            // Draw rows
            for ( int iRow = 0; iRow < rowHeaders.length; iRow++ ) {
                redrawHeaderRow( iRow );
            }

            // Schedule resize event after header has been drawn
            Scheduler.get().scheduleDeferred( new ScheduledCommand() {
                public void execute() {
                    ResizeEvent.fire( VerticalDecisionTableHeaderWidget.this,
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
                    // General row, all visible cells included
                    for ( DynamicColumn<DTColumnConfig> col : grid.getGridWidget().getColumns() ) {
                        if ( col.isVisible() ) {
                            tce = DOM.createTD();
                            tce.addClassName( style.headerText() );
                            tre.appendChild( tce );
                            populateTableCellElement( col,
                                                      tce );
                        }
                    }
                    break;

                case 1 :
                    // Splitter between "general" and "technical" condition
                    // details
                    if ( visibleConditionCols.size() > 0 ) {
                        splitter.setRowHeaders( rowHeaders );
                        tce = DOM.createTD();
                        tce.<TableCellElement> cast().setColSpan(
                                                                  visibleConditionCols.size() );
                        tce.addClassName( style.headerSplitter() );
                        tre.appendChild( tce );
                        add( splitter,
                             tce );
                    }
                    break;

                case 2 :
                    // Condition FactType, merged between identical
                    for ( int iCol = 0; iCol < visibleConditionCols.size(); iCol++ ) {
                        tce = DOM.createTD();
                        tce.addClassName( style.headerText() );
                        tre.appendChild( tce );

                        DynamicColumn<DTColumnConfig> col = visibleConditionCols.get( iCol );
                        ConditionCol cc = (ConditionCol) col.getModelColumn();

                        // Merging
                        int colSpan = 1;
                        int width = col.getWidth();
                        while ( iCol
                                + colSpan < visibleConditionCols.size() ) {
                            DynamicColumn<DTColumnConfig> mergeCol = visibleConditionCols.get( iCol
                                                                                               + colSpan );
                            ConditionCol mergeCondCol = (ConditionCol) mergeCol
                                    .getModelColumn();

                            if ( mergeCondCol.getFactType().equals( cc.getFactType() )
                                 && mergeCondCol.getBoundName().equals(
                                                                        cc.getBoundName() ) ) {
                                width = width
                                        + mergeCol.getWidth();
                                colSpan++;
                            } else {
                                break;
                            }
                        }

                        // Make cell
                        iCol = iCol
                               + colSpan
                               - 1;
                        tce.addClassName( style.headerRowIntermediate() );
                        tce.appendChild( makeLabel(
                                                    cc.getFactType()
                                                            + " ["
                                                            + cc.getBoundName()
                                                            + "]",
                                                    width,
                                                    (splitter.isCollapsed ? 0 : style.rowHeaderHeight()) ) );
                        tce.<TableCellElement> cast().setColSpan( colSpan );

                    }
                    break;

                case 3 :
                    // Condition FactField
                    for ( DynamicColumn<DTColumnConfig> col : visibleConditionCols ) {
                        tce = DOM.createTD();
                        tce.addClassName( style.headerText() );
                        tce.addClassName( style.headerRowIntermediate() );
                        tre.appendChild( tce );
                        ConditionCol cc = (ConditionCol) col.getModelColumn();
                        tce.appendChild( makeLabel( cc.getFactField()
                                                            + " ["
                                                            + cc.getOperator()
                                                            + "]",
                                                    col.getWidth(),
                                                    (splitter.isCollapsed ? 0 : style.rowHeaderHeight()) ) );
                    }
                    break;

                case 4 :
                    // Sorters
                    for ( DynamicColumn<DTColumnConfig> col : grid.getGridWidget().getColumns() ) {
                        if ( col.isVisible() ) {
                            final HeaderSorter shp = new HeaderSorter( col );
                            final DynamicColumn<DTColumnConfig> sortableColumn = col;
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
                    }
                    break;
            }

            getBody().replaceChild( tre,
                                    rowHeaders[iRow] );
            rowHeaders[iRow] = tre;
        }

        // Update sort order. The column clicked becomes the primary sort column
        // and the other, previously sorted, columns degrade in priority
        private void updateSortOrder(DynamicColumn<DTColumnConfig> column) {
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
                for ( DynamicColumn<DTColumnConfig> sortableColumn : grid.getGridWidget().getColumns() ) {
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
     * Construct a "Header" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public VerticalDecisionTableHeaderWidget(final DecoratedGridWidget<DTColumnConfig> grid) {
        super( grid );
    }

    @Override
    protected Widget getHeaderWidget() {
        if ( this.widget == null ) {
            this.widget = new HeaderWidget();
        }
        return widget;
    }

    @Override
    protected ResizerInformation getResizerInformation(int mx) {
        boolean isPrimed = false;
        ResizerInformation resizerInfo = new ResizerInformation();
        for ( int iCol = 0; iCol < widget.rowHeaders[0].getChildCount(); iCol++ ) {
            TableCellElement tce = widget.rowHeaders[0].getChild(
                                                                  iCol ).<TableCellElement> cast();
            int cx = tce.getAbsoluteRight();
            if ( Math.abs( mx
                           - cx ) <= 5 ) {
                isPrimed = true;
                resizerInfo.setResizePrimed( isPrimed );
                resizerInfo.setResizeColumn( widget.visibleCols.get( iCol ) );
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

    @Override
    public void redraw() {
        widget.redraw();
    }

    // Resize the inner DIV in each table cell
    protected void resizeColumn(DynamicColumn<DTColumnConfig> resizeColumn,
                                int resizeColumnWidth) {
        DivElement div;
        TableCellElement tce;
        int conditionColsWidth = 0;

        // This is also set in the ColumnResizeEvent handler, however it makes
        // resizing columns in the header more simple too
        resizeColumn.setWidth( resizeColumnWidth );
        int resizeColumnIndex = widget.visibleCols.indexOf( resizeColumn );

        // Row 0 (General\Fact Type)
        tce = widget.rowHeaders[0].getChild( resizeColumnIndex )
                .<TableCellElement> cast();
        div = tce.getFirstChild().<DivElement> cast();
        div.getStyle().setWidth( resizeColumnWidth,
                                 Unit.PX );

        // Row 3 (Fact Fields). This column is resized before row 1
        // as it depends on the total width of all Condition Columns
        for ( int iCol = 0; iCol < widget.visibleConditionCols.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig> col = widget.visibleConditionCols.get( iCol );
            int colWidth = col.getWidth();
            conditionColsWidth = conditionColsWidth
                                 + colWidth;
            tce = widget.rowHeaders[3].getChild( iCol ).<TableCellElement> cast();
            div = tce.getFirstChild().<DivElement> cast();
            div.getStyle().setWidth( colWidth,
                                     Unit.PX );
        }

        // Row 2 (Fact Types)
        int iColColumn = 0;
        for ( int iCol = 0; iCol < widget.visibleConditionCols.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig> col = widget.visibleConditionCols.get( iCol );
            ConditionCol cc = (ConditionCol) col.getModelColumn();

            // Merging
            int colSpan = 1;
            int width = col.getWidth();
            while ( iCol
                    + colSpan < widget.visibleConditionCols.size() ) {
                DynamicColumn<DTColumnConfig> mergeCol = widget.visibleConditionCols.get( iCol
                                                                                          + colSpan );
                ConditionCol mergeCondCol = (ConditionCol) mergeCol
                        .getModelColumn();

                if ( mergeCondCol.getFactType().equals( cc.getFactType() )
                        && mergeCondCol.getBoundName()
                                .equals( cc.getBoundName() ) ) {
                    width = width
                            + mergeCol.getWidth();
                    colSpan++;
                } else {
                    break;
                }
            }

            // Make cell
            iCol = iCol
                   + colSpan
                   - 1;
            tce = widget.rowHeaders[2].getChild( iColColumn )
                    .<TableCellElement> cast();
            div = tce.getFirstChild().<DivElement> cast();
            div.getStyle().setWidth( width,
                                     Unit.PX );
            iColColumn++;
        }

        // Row 4 (Sorters)
        tce = widget.rowHeaders[4].getChild( resizeColumnIndex )
                .<TableCellElement> cast();
        div = tce.getFirstChild().<DivElement> cast();
        div.getStyle().setWidth( resizeColumnWidth,
                                 Unit.PX );

        // Fire event to any interested consumers
        ColumnResizeEvent.fire( this,
                                widget.visibleCols.get( resizeColumnIndex ),
                                resizeColumnWidth );
    }

    // Set the cursor type for all cells on the table as
    // we only use rowHeader[0] to check which column
    // needs resizing however the mouse could be over any
    // row
    private void setCursorType(Cursor cursor) {
        for ( int iRow = 0; iRow < widget.rowHeaders.length; iRow++ ) {
            TableRowElement tre = widget.rowHeaders[iRow]
                        .<TableRowElement> cast();
            for ( int iCol = 0; iCol < tre.getCells().getLength(); iCol++ ) {
                TableCellElement tce = tre.getCells().getItem( iCol );
                tce.getStyle().setCursor( cursor );
            }
        }

    }

    @Override
    public void setScrollPosition(int position) {
        if ( position < 0 ) {
            throw new IllegalArgumentException( "position cannot be null" );
        }

        ((ScrollPanel) this.panel).setHorizontalScrollPosition( position );
    }

}
