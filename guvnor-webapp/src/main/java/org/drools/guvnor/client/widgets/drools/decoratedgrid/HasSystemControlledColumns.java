package org.drools.guvnor.client.widgets.drools.decoratedgrid;

/**
 * Callback for consumers of DecoratedGridWidget to update cell values following
 * operations controlled by DecordatedGridWidget (such as sorting)
 */
public interface HasSystemControlledColumns {

    public abstract void updateSystemControlledColumnValues();

}
