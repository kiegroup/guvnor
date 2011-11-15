package org.drools.guvnor.client.widgets.drools.decoratedgrid;

/**
 * Row operations for consumers of DecoratedGridWidget
 */
public interface HasRows<T> {

    public abstract void appendRow(T data);

    public abstract void insertRowBefore(int index, T data);

    public abstract void deleteRow(int index);
    
    public abstract int rowCount();

}
