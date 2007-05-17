package org.drools.brms.client.table;

import com.google.gwt.user.client.ui.Widget;

/**
 * An optional interface to provide widgets for the body of the grid.
 * @author Michael Neale
 *
 */
public interface DataModel {

    
    /**
     * Must always provide a value. This is used for sorting (and display possibly).
     */
    public Comparable getValue(int row, int col);
    
    /**
     * optionally return a widget to display instead of the text. If null, then the text will be rendered.
     */
    public Widget getWidget(int row, int col);
    
    
    public int getNumberOfRows();
    
    public String getRowId(int row);
    
}
