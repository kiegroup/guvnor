package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This contains the results returned to populate a table/grid.
 * @author Michael Neale
 */
public class TableDataResult
    implements
    IsSerializable {

    public int numberOfRows;
    
    public TableDataRow[] data;
    
}
