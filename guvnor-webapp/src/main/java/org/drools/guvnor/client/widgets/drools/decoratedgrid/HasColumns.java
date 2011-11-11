package org.drools.guvnor.client.widgets.drools.decoratedgrid;

/**
 * Column operations for consumers of DecoratedGridWidget
 * 
 * @param <T>
 *            The type of domain columns represented
 */
public interface HasColumns<T> {

    public abstract void addColumn(T modelColumn);

    public abstract void setColumnVisibility(T modelColumn,
                                             boolean isVisible);

    public abstract void deleteColumn(T modelColumn);

}
