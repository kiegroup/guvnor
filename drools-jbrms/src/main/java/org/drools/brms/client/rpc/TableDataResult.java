package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This contains the results returned to populate a table/grid.
 * This will be enhanced to provide pagination data shortly.
 * @author Michael Neale
 */
public class TableDataResult
    implements
    IsSerializable {
    
    public TableDataRow[] data;
    
}
