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
import java.util.TreeMap;

import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.widgets.decoratedgrid.ColumnResizeEvent;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridHeaderWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.DynamicColumn;
import org.drools.guvnor.client.widgets.decoratedgrid.SortConfiguration;
import org.drools.guvnor.client.widgets.tables.SortDirection;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;
import org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;

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
import com.google.gwt.i18n.client.DateTimeFormat;
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
        DecoratedGridHeaderWidget<DTColumnConfig52> {

    private static final String         DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    private static final DateTimeFormat format      = DateTimeFormat.getFormat( DATE_FORMAT );

    /**
     * This is the guts of the widget.
     */
    private class HeaderWidget extends CellPanel {

        /**
         * A Widget to display sort order
         */
        private class HeaderSorter extends FocusPanel {

            private final HorizontalPanel                 hp = new HorizontalPanel();
            private final DynamicColumn<DTColumnConfig52> col;

            private HeaderSorter(final DynamicColumn<DTColumnConfig52> col) {
                this.col = col;
                hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
                hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
                hp.setHeight( style.rowHeaderSorterHeight() + "px" );
                hp.setWidth( "100%" );
                setIconImage();
                add( hp );

                // Ensure our icon is updated when the SortDirection changes
                col.addValueChangeHandler( new ValueChangeHandler<SortConfiguration>() {

                    public void onValueChange(ValueChangeEvent<SortConfiguration> event) {
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
                    default :
                        hp.add( new Image( resource.emptyArrow() ) );
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

                // Set row height by setting height of children
                private void setHeight(int height) {
                    for ( int i = 0; i < tre.getChildCount(); i++ ) {
                        tre.getChild( i ).getFirstChild().<DivElement> cast().getStyle().setHeight( height,
                                                                                                    Unit.PX );
                    }
                    fireResizeEvent();
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
                if ( rowHeaders == null || (rowHeaders.length - 1) < iRow ) {
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
        private List<HeaderSorter>                    sorters              = new ArrayList<HeaderSorter>();
        private HeaderSplitter                        splitter             = new HeaderSplitter();

        // UI Components
        private Element[]                             rowHeaders           = new Element[5];

        private List<DynamicColumn<DTColumnConfig52>> visibleCols          = new ArrayList<DynamicColumn<DTColumnConfig52>>();
        private List<DynamicColumn<DTColumnConfig52>> visibleConditionCols = new ArrayList<DynamicColumn<DTColumnConfig52>>();

        // Constructor
        private HeaderWidget() {
            for ( int iRow = 0; iRow < rowHeaders.length; iRow++ ) {
                rowHeaders[iRow] = DOM.createTR();
                getBody().appendChild( rowHeaders[iRow] );
            }
            getBody().getParentElement().<TableElement> cast().setCellSpacing( 0 );
            getBody().getParentElement().<TableElement> cast().setCellPadding( 0 );
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
        private void populateTableCellElement(DynamicColumn<DTColumnConfig52> col,
                                              Element tce) {

            DTColumnConfig52 modelCol = col.getModelColumn();
            if ( modelCol instanceof RowNumberCol52 ) {
                tce.appendChild( makeLabel( "#",
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            } else if ( modelCol instanceof DescriptionCol52 ) {
                tce.appendChild( makeLabel( constants.Description(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            } else if ( modelCol instanceof MetadataCol52 ) {
                tce.appendChild( makeLabel( ((MetadataCol52) modelCol).getMetadata(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            } else if ( modelCol instanceof AttributeCol52 ) {
                tce.appendChild( makeLabel( ((AttributeCol52) modelCol).getAttribute(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
                tce.<TableCellElement> cast().setRowSpan( 4 );
                tce.addClassName( style.headerRowIntermediate() );
            } else if ( modelCol instanceof ConditionCol52 ) {
                tce.appendChild( makeLabel( ((ConditionCol52) modelCol).getHeader(),
                                            col.getWidth(),
                                            style.rowHeaderHeight() ) );
            } else if ( modelCol instanceof ActionCol52 ) {
                tce.appendChild( makeLabel( ((ActionCol52) modelCol).getHeader(),
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
                DynamicColumn<DTColumnConfig52> col = grid.getGridWidget().getColumns().get( iCol );
                if ( col.isVisible() ) {
                    visibleCols.add( col );
                    DTColumnConfig52 modelCol = col.getModelColumn();
                    if ( modelCol instanceof ConditionCol52 ) {
                        visibleConditionCols.add( col );
                    }
                }
            }

            // Draw rows
            for ( int iRow = 0; iRow < rowHeaders.length; iRow++ ) {
                redrawHeaderRow( iRow );
            }

            fireResizeEvent();
        }

        // Redraw a single row obviously
        private void redrawHeaderRow(int iRow) {
            Element tce = null;
            Element tre = DOM.createTR();
            switch ( iRow ) {
                case 0 :
                    // General row, all visible cells included
                    for ( DynamicColumn<DTColumnConfig52> col : grid.getGridWidget().getColumns() ) {
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
                    // Splitter between "general" and "technical" condition details
                    if ( visibleConditionCols.size() > 0 ) {
                        splitter.setRowHeaders( rowHeaders );
                        tce = DOM.createTD();
                        tce.<TableCellElement> cast().setColSpan( visibleConditionCols.size() );
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

                        DynamicColumn<DTColumnConfig52> col = visibleConditionCols.get( iCol );
                        ConditionCol52 cc = (ConditionCol52) col.getModelColumn();
                        Pattern52 ccPattern = model.getPattern( cc );

                        // Merging
                        int colSpan = 1;
                        int width = col.getWidth();
                        while ( iCol + colSpan < visibleConditionCols.size() ) {
                            DynamicColumn<DTColumnConfig52> mergeCol = visibleConditionCols.get( iCol + colSpan );
                            ConditionCol52 mergeCondCol = (ConditionCol52) mergeCol.getModelColumn();
                            Pattern52 mergeCondColPattern = model.getPattern( mergeCondCol );

                            //Only merge columns if FactType and BoundName are identical
                            if ( mergeCondColPattern.getFactType() == null || mergeCondColPattern.getFactType().length() == 0 ) {
                                break;
                            }
                            if ( !mergeCondColPattern.getFactType().equals( ccPattern.getFactType() )
                                 || !mergeCondColPattern.getBoundName().equals( ccPattern.getBoundName() ) ) {
                                break;
                            }

                            width = width + mergeCol.getWidth();
                            colSpan++;
                        }

                        // Make cell
                        iCol = iCol + colSpan - 1;
                        tce.addClassName( style.headerRowIntermediate() );
                        StringBuilder label = new StringBuilder();
                        String factType = ccPattern.getFactType();
                        if ( factType != null && factType.length() > 0 ) {
                            label.append( (ccPattern.isNegated() ? constants.negatedPattern() + " " : "") );
                            label.append( ccPattern.getFactType() );
                            label.append( " [" + ccPattern.getBoundName() + "]" );
                        }
                        tce.appendChild( makeLabel( label.toString(),
                                                    width,
                                                    (splitter.isCollapsed ? 0 : style.rowHeaderHeight()) ) );
                        tce.<TableCellElement> cast().setColSpan( colSpan );

                    }
                    break;

                case 3 :
                    // Condition FactField
                    for ( DynamicColumn<DTColumnConfig52> col : visibleConditionCols ) {
                        tce = DOM.createTD();
                        tce.addClassName( style.headerText() );
                        tce.addClassName( style.headerRowIntermediate() );
                        tre.appendChild( tce );
                        ConditionCol52 cc = (ConditionCol52) col.getModelColumn();
                        StringBuilder label = new StringBuilder();
                        String factField = cc.getFactField();
                        if ( factField != null && factField.length() > 0 ) {
                            label.append( factField );
                        }
                        if ( cc.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_PREDICATE ) {
                            label.append( " [" );
                            label.append( cc.getOperator() );
                            label.append( getLimitedEntryValue( cc ) );
                            label.append( "]" );
                        }
                        tce.appendChild( makeLabel( label.toString(),
                                                    col.getWidth(),
                                                    (splitter.isCollapsed ? 0 : style.rowHeaderHeight()) ) );
                    }
                    break;

                case 4 :
                    // Sorters
                    for ( DynamicColumn<DTColumnConfig52> col : grid.getGridWidget().getColumns() ) {
                        if ( col.isVisible() ) {
                            final HeaderSorter shp = new HeaderSorter( col );
                            final DynamicColumn<DTColumnConfig52> sortableColumn = col;
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

        private String getLimitedEntryValue(ConditionCol52 c) {
            if ( !(c instanceof LimitedEntryCol) ) {
                return "";
            }
            LimitedEntryCol lec = (LimitedEntryCol) c;
            DTCellValue52 cv = lec.getValue();
            DTDataTypes52 type = cv.getDataType();
            switch ( type ) {
                case BOOLEAN :
                    return cv.getBooleanValue().toString();
                case NUMERIC :
                    return cv.getNumericValue().toPlainString();
                case DATE :
                    return format.format( cv.getDateValue() );
                default :
                    return cv.getStringValue();
            }
        }

        // Update sort order. The column clicked becomes the primary sort column
        // and the other, previously sorted, columns degrade in priority
        private void updateSortOrder(DynamicColumn<DTColumnConfig52> column) {

            int sortIndex;
            TreeMap<Integer, DynamicColumn<DTColumnConfig52>> sortedColumns = new TreeMap<Integer, DynamicColumn<DTColumnConfig52>>();
            switch ( column.getSortIndex() ) {
                case -1 :

                    //A new column is added to the sort group
                    for ( DynamicColumn<DTColumnConfig52> sortedColumn : grid.getGridWidget().getColumns() ) {
                        if ( sortedColumn.getSortDirection() != SortDirection.NONE ) {
                            sortedColumns.put( sortedColumn.getSortIndex(),
                                               sortedColumn );
                        }
                    }
                    sortIndex = 1;
                    for ( DynamicColumn<DTColumnConfig52> sortedColumn : sortedColumns.values() ) {
                        sortedColumn.setSortIndex( sortIndex );
                        sortIndex++;
                    }
                    column.setSortIndex( 0 );
                    column.setSortDirection( SortDirection.ASCENDING );
                    break;

                case 0 :

                    //The existing "lead" column's sort direction is changed
                    if ( column.getSortDirection() == SortDirection.ASCENDING ) {
                        column.setSortDirection( SortDirection.DESCENDING );
                    } else if ( column.getSortDirection() == SortDirection.DESCENDING ) {
                        column.setSortDirection( SortDirection.NONE );
                        column.clearSortIndex();
                        for ( DynamicColumn<DTColumnConfig52> sortedColumn : grid.getGridWidget().getColumns() ) {
                            if ( sortedColumn.getSortDirection() != SortDirection.NONE ) {
                                sortedColumns.put( sortedColumn.getSortIndex(),
                                                   sortedColumn );
                            }
                        }
                        sortIndex = 0;
                        for ( DynamicColumn<DTColumnConfig52> sortedColumn : sortedColumns.values() ) {
                            sortedColumn.setSortIndex( sortIndex );
                            sortIndex++;
                        }
                    }
                    break;

                default :

                    //An existing column is promoted to "lead"
                    for ( DynamicColumn<DTColumnConfig52> sortedColumn : grid.getGridWidget().getColumns() ) {
                        if ( sortedColumn.getSortDirection() != SortDirection.NONE ) {
                            if ( !sortedColumn.equals( column ) ) {
                                sortedColumns.put( sortedColumn.getSortIndex() + 1,
                                                   sortedColumn );
                            }
                        }
                    }
                    column.setSortIndex( 0 );
                    sortIndex = 1;
                    for ( DynamicColumn<DTColumnConfig52> sortedColumn : sortedColumns.values() ) {
                        sortedColumn.setSortIndex( sortIndex );
                        sortIndex++;
                    }
                    break;
            }
        }

    }

    // UI Components
    private HeaderWidget          widget;

    private GuidedDecisionTable52 model;

    /**
     * Construct a "Header" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public VerticalDecisionTableHeaderWidget(final DecoratedGridWidget<DTColumnConfig52> grid) {
        super( grid );
    }

    /**
     * Inject the model
     * 
     * @param model
     */
    void setModel(GuidedDecisionTable52 model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        this.model = model;
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

    // Schedule resize event after header has been drawn or resized
    private void fireResizeEvent() {
        Scheduler.get().scheduleFinally( new ScheduledCommand() {
            public void execute() {
                ResizeEvent.fire( VerticalDecisionTableHeaderWidget.this,
                                  getBody().getOffsetWidth(),
                                  getBody().getOffsetHeight() );
            }
        } );

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
            TableCellElement tce = widget.rowHeaders[0].getChild( iCol ).<TableCellElement> cast();
            int cx = tce.getAbsoluteRight();
            if ( Math.abs( mx - cx ) <= 5 ) {
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

    // Resize the inner DIV in each table cell
    protected void resizeColumn(DynamicColumn<DTColumnConfig52> resizeColumn,
                                int resizeColumnWidth) {
        DivElement div;
        TableCellElement tce;
        int conditionColsWidth = 0;

        // This is also set in the ColumnResizeEvent handler, however it makes
        // resizing columns in the header more simple too
        resizeColumn.setWidth( resizeColumnWidth );
        int resizeColumnIndex = widget.visibleCols.indexOf( resizeColumn );

        // Row 0 (General\Fact Type)
        tce = widget.rowHeaders[0].getChild( resizeColumnIndex ).<TableCellElement> cast();
        div = tce.getFirstChild().<DivElement> cast();
        div.getStyle().setWidth( resizeColumnWidth,
                                 Unit.PX );

        // Row 3 (Fact Fields). This column is resized before row 1
        // as it depends on the total width of all Condition Columns
        for ( int iCol = 0; iCol < widget.visibleConditionCols.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig52> col = widget.visibleConditionCols.get( iCol );
            int colWidth = col.getWidth();
            conditionColsWidth = conditionColsWidth + colWidth;
            tce = widget.rowHeaders[3].getChild( iCol ).<TableCellElement> cast();
            div = tce.getFirstChild().<DivElement> cast();
            div.getStyle().setWidth( colWidth,
                                     Unit.PX );
        }

        // Row 2 (Fact Types)
        int iColColumn = 0;
        for ( int iCol = 0; iCol < widget.visibleConditionCols.size(); iCol++ ) {
            DynamicColumn<DTColumnConfig52> col = widget.visibleConditionCols.get( iCol );
            ConditionCol52 cc = (ConditionCol52) col.getModelColumn();
            Pattern52 ccPattern = model.getPattern( cc );

            // Merging
            int colSpan = 1;
            int width = col.getWidth();
            while ( iCol + colSpan < widget.visibleConditionCols.size() ) {
                DynamicColumn<DTColumnConfig52> mergeCol = widget.visibleConditionCols.get( iCol + colSpan );
                ConditionCol52 mergeCondCol = (ConditionCol52) mergeCol.getModelColumn();
                Pattern52 mergeCondColPattern = model.getPattern( mergeCondCol );

                //Only merge columns if FactType and BoundName are identical
                if ( mergeCondColPattern.getFactType() == null || mergeCondColPattern.getFactType().length() == 0 ) {
                    break;
                }
                if ( !mergeCondColPattern.getFactType().equals( ccPattern.getFactType() )
                     || !mergeCondColPattern.getBoundName().equals( ccPattern.getBoundName() ) ) {
                    break;
                }

                width = width + mergeCol.getWidth();
                colSpan++;
            }

            // Make cell
            iCol = iCol + colSpan - 1;
            tce = widget.rowHeaders[2].getChild( iColColumn ).<TableCellElement> cast();
            div = tce.getFirstChild().<DivElement> cast();
            div.getStyle().setWidth( width,
                                     Unit.PX );
            iColColumn++;
        }

        // Row 4 (Sorters)
        tce = widget.rowHeaders[4].getChild( resizeColumnIndex ).<TableCellElement> cast();
        div = tce.getFirstChild().<DivElement> cast();
        div.getStyle().setWidth( resizeColumnWidth,
                                 Unit.PX );

        // Fire event to any interested consumers
        ColumnResizeEvent.fire( this,
                                widget.visibleCols.get( resizeColumnIndex ),
                                resizeColumnWidth );
    }

}
