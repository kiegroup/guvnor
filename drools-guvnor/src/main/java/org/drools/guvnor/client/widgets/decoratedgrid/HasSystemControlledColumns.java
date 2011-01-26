package org.drools.guvnor.client.widgets.decoratedgrid;

/**
 * Callback for consumers of DecoratedGridWidget to update cell values following
 * operations controlled by DecordatedGridWidget (such as sorting)
 * 
 * @author manstis
 */
public interface HasSystemControlledColumns {

    public abstract void updateSystemControlledColumnValues();

}