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

package org.drools.guvnor.client.widgets.tables;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

/**
 * A custom Pager that maintains a set page size and displays page numbers and
 * total pages more elegantly. SimplePager will ensure <code>pageSize</code>
 * rows are always rendered even if the "last" page has less than
 * <code>pageSize</code> rows remain.
 * 
 *
 */
public class GuvnorSimplePager extends SimplePager {

    //Page size is normally derieved from the visibleRange
    private int pageSize = 10;

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        super.setPageSize( pageSize );
    }

    // We want pageSize to remain constant
    @Override
    public int getPageSize() {
        return pageSize;
    }

    // Page forward by an exact size rather than the number of visible
    // rows as is in the norm in the underlying implementation
    @Override
    public void nextPage() {
        if ( getDisplay() != null ) {
            Range range = getDisplay().getVisibleRange();
            setPageStart( range.getStart()
                          + getPageSize() );
        }
    }

    // Page back by an exact size rather than the number of visible rows
    // as is in the norm in the underlying implementation
    @Override
    public void previousPage() {
        if ( getDisplay() != null ) {
            Range range = getDisplay().getVisibleRange();
            setPageStart( range.getStart()
                          - getPageSize() );
        }
    }

    // Override so the last page is shown with a number of rows less
    // than the pageSize rather than always showing the pageSize number
    // of rows and possibly repeating rows on the last and penultimate
    // page
    @Override
    public void setPageStart(int index) {
        if ( getDisplay() != null ) {
            Range range = getDisplay().getVisibleRange();
            int displayPageSize = getPageSize();
            if ( isRangeLimited()
                 && getDisplay().isRowCountExact() ) {
                displayPageSize = Math.min( getPageSize(),
                                            getDisplay().getRowCount()
                                                    - index );
            }
            index = Math.max( 0,
                              index );
            if ( index != range.getStart() ) {
                getDisplay().setVisibleRange( index,
                                              displayPageSize );
            }
        }
    }

    // Override to display "0 of 0" when there are no records (otherwise
    // you get "1-1 of 0") and "1 of 1" when there is only one record
    // (otherwise you get "1-1 of 1"). Not internationalised (but
    // neither is SimplePager)
    protected String createText() {
        NumberFormat formatter = NumberFormat.getFormat( "#,###" );
        HasRows display = getDisplay();
        Range range = display.getVisibleRange();
        int pageStart = range.getStart() + 1;
        int pageSize = range.getLength();
        int dataSize = display.getRowCount();
        int endIndex = Math.min( dataSize,
                                 pageStart
                                         + pageSize
                                         - 1 );
        endIndex = Math.max( pageStart,
                             endIndex );
        boolean exact = display.isRowCountExact();
        if ( dataSize == 0 ) {
            return "0 of 0";
        } else if ( pageStart == endIndex ) {
            return formatter.format( pageStart )
                   + " of "
                   + formatter.format( dataSize );
        }
        return formatter.format( pageStart )
               + "-"
               + formatter.format( endIndex )
               + (exact ? " of " : " of over ")
               + formatter.format( dataSize );
    }

}
