package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a row of data from a table.
 * @author michael neale
 *
 */
public class TableDataRow
    implements
    IsSerializable {

    public String key;
    public String[] values;
    
}
