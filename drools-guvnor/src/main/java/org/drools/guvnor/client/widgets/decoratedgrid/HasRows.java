package org.drools.guvnor.client.widgets.decoratedgrid;

/**
 * Row operations for consumers of DecoratedGridWidget
 * 
 */
public interface HasRows {

    public abstract void insertRowBefore(DynamicDataRow rowBefore);

    public abstract void appendRow();

    public abstract void deleteRow(DynamicDataRow row);

}
