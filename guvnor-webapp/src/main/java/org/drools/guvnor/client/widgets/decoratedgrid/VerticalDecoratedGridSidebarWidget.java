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

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A sidebar for a VericalDecisionTable. This provides a vertical list of
 * controls to add and remove the associated row from the DecisionTable.
 */
public class VerticalDecoratedGridSidebarWidget<T> extends
        DecoratedGridSidebarWidget<T> {

    /**
     * Widget to render selectors beside rows. Two selectors are provided per
     * row: (1) A "add new row (above selected)" and (2) "delete row".
     */
    private class VerticalSelectorWidget extends CellPanel {

        // Widgets (selectors) created (so they can be removed later)
        private ArrayList<Widget> widgets = new ArrayList<Widget>();

        private VerticalSelectorWidget() {
            getBody().getParentElement().<TableElement> cast()
                    .setCellSpacing( 0 );
            getBody().getParentElement().<TableElement> cast()
                    .setCellPadding( 0 );
            sinkEvents( Event.getTypeInt( "click" ) );
        }

        // Delete a row at the given index
        private void deleteSelector(int index) {
            Widget widget = widgets.get( index );
            remove( widget );
            getBody().<TableSectionElement> cast().deleteRow( index );
            widgets.remove( index );
            fixStyles( index );
        }

        // Row styles need to be re-applied after inserting and deleting rows
        private void fixStyles(int iRow) {
            while ( iRow < getBody().getChildCount() ) {
                TableRowElement tre = getBody().getChild( iRow )
                        .<TableRowElement> cast();
                tre.setClassName( getRowStyle( iRow ) );
                iRow++;
            }
        }

        // Get style applicable to row
        private String getRowStyle(int iRow) {
            boolean isEven = iRow % 2 == 0;
            String trClasses = isEven ? style.cellTableEvenRow() : style
                    .cellTableOddRow();
            return trClasses;
        }

        // Insert a new row before the given index
        private void insertSelector(DynamicDataRow row,
                                    int index) {

            Element tre = DOM.createTR();
            Element tce = DOM.createTD();
            tre.setClassName( getRowStyle( widgets.size() ) );
            tre.getStyle().setHeight( style.rowHeight(),
                                      Unit.PX );
            tce.addClassName( style.selectorCell() );
            DOM.insertChild( getBody(),
                             tre,
                             index );
            tre.appendChild( tce );

            Widget widget = makeRowWidget( row );
            add( widget,
                 tce );

            widgets.add( index,
                         widget );
            fixStyles( index );
        }

        // Make the selector Widget
        private Widget makeRowWidget(final DynamicDataRow row) {

            HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
            hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
            hp.setWidth( "100%" );

            FocusPanel fp;
            fp = new FocusPanel();
            fp.setHeight( "100%" );
            fp.setWidth( "50%" );
            fp.add( new Image( resource.selectorAdd() ) );
            fp.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    hasRows.insertRowBefore( row );
                }

            } );
            hp.add( fp );

            fp = new FocusPanel();
            fp.setHeight( "100%" );
            fp.setWidth( "50%" );
            fp.add( new Image( resource.selectorDelete() ) );
            fp.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    hasRows.deleteRow( row );
                }

            } );
            hp.add( fp );
            return hp;
        }

        // Redraw entire sidebar
        private void redraw() {
            //Remove existing
            int totalRows = widgets.size();
            for ( int iRow = 0; iRow < totalRows; iRow++ ) {
                deleteSelector( 0 );
            }
            //Add selector for each row
            for ( DynamicDataRow row : grid.getGridWidget().getData() ) {
                insertSelector( row,
                                widgets.size() );
            }

        }

    }

    /**
     * Simple spacer to ensure scrollable part of sidebar aligns with grid.
     */
    private class VerticalSideBarSpacerWidget extends CellPanel {

        private Image icon = new Image();

        private VerticalSideBarSpacerWidget() {
            // Widget stuff
            FocusPanel fp = new FocusPanel();
            HorizontalPanel hp = new HorizontalPanel();
            hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
            hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
            setIconImage( grid.getGridWidget().isMerged() );
            hp.add( icon );
            hp.setWidth( "100%" );
            hp.setHeight( "100%" );
            fp.add( hp );

            // DOM stuff (put Widget in HTML cell so we can fix the width)
            getBody().getParentElement().<TableElement> cast()
                    .setCellSpacing( 0 );
            getBody().getParentElement().<TableElement> cast()
                    .setCellPadding( 0 );
            Element tre = DOM.createTR();
            Element tce = DOM.createTD();
            tre.appendChild( tce );
            getBody().appendChild( tre );
            tce.addClassName( style.selectorSpacer() );
            add( fp,
                 tce );

            // Setup event handling
            fp.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    setIconImage( grid.getGridWidget().toggleMerging() );
                }

            } );

            sinkEvents( Event.getTypeInt( "click" ) );
        }

        // Set the icon's image accordingly
        private void setIconImage(boolean isMerged) {
            if ( isMerged ) {
                icon.setResource( resource.toggleUnmerge() );
            } else {
                icon.setResource( resource.toggleMerge() );
            }
        }
    }

    // UI Elements
    private ScrollPanel                       scrollPanel;
    private VerticalPanel                     container;
    private VerticalSelectorWidget            selectors;
    private final VerticalSideBarSpacerWidget spacer = new VerticalSideBarSpacerWidget();

    /**
     * Construct a "Sidebar" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public VerticalDecoratedGridSidebarWidget(DecoratedGridWidget<T> grid,
                                              HasRows hasRows) {
        // Argument validation performed in the superclass constructor
        super( grid,
               hasRows );

        // Construct the Widget
        scrollPanel = new ScrollPanel();
        container = new VerticalPanel();
        selectors = new VerticalSelectorWidget();

        container.add( spacer );
        container.add( scrollPanel );
        scrollPanel.add( selectors );

        // We don't want scroll bars on the Sidebar
        scrollPanel.getElement().getStyle().setOverflow( Overflow.HIDDEN );

        initWidget( container );

    }

    @Override
    public void deleteSelector(DynamicDataRow row) {
        int index = grid.getGridWidget().getData().indexOf( row );
        if ( index == -1 ) {
            throw new IllegalArgumentException( "row does not exist in table data." );
        }
        selectors.deleteSelector( index );
    }

    @Override
    public void insertSelector(DynamicDataRow row) {
        if ( row == null ) {
            throw new IllegalArgumentException( "row cannot be null" );
        }
        int index = grid.getGridWidget().getData().indexOf( row );
        if ( index == -1 ) {
            throw new IllegalArgumentException( "row does not exist in table data." );
        }
        selectors.insertSelector( row,
                                  index );
    }

    @Override
    public void redraw() {
        selectors.redraw();
    }

    @Override
    public void resizeSidebar(int height) {
        if ( height < 0 ) {
            throw new IllegalArgumentException( "height cannot be less than zero" );
        }
        spacer.setHeight( height
                          + "px" );
    }

    @Override
    public void setHeight(String height) {
        if ( height == null ) {
            throw new IllegalArgumentException( "height cannot be null" );
        }
        this.scrollPanel.setHeight( height );
    }

    @Override
    public void setScrollPosition(int position) {
        if ( position < 0 ) {
            throw new IllegalArgumentException( "position cannot be less than zero" );
        }
        this.scrollPanel.setScrollPosition( position );
    }

}
