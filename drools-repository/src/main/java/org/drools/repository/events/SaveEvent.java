package org.drools.repository.events;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

/**
 * This will be called as content is saved to the repository - you can hook in and also store content in an external store.
 * Content can be text or binary.
 *
 * To install an implementation of this, create an instance of SaveEvent, make it available on the classpath
 * and set the system property 'guvnor.saveEventListener' with the full name of the class. 
 *
 * @author Michael Neale
 */
public interface SaveEvent {

    /**
     * When the content of the asset changes, or some meta data. This will also be called when it is new.
     */
    public void onAssetCheckin(AssetItem item);

    /**
     * When it is hard deleted. A soft delete is just a checkin with the archive flag set.
     */
    public void onAssetDelete(AssetItem item);


    /**
     * Called once, when a package is created.
     */
    public void onPackageCreate(PackageItem item);

}
