package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.RowMapper;

/**
 * Row operations for consumers of MergableGridWidget
 */
public interface HasGroupedRows<T> extends HasRows<T> {
    
    public abstract RowMapper getRowMapper();

}
