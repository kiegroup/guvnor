package org.drools.repository.events;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

/**
 * @author Michael Neale
 */
public class MockSaveEvent implements SaveEvent {
    boolean checkinCalled = false;

    public void onAssetCheckin(AssetItem item) {
        this.checkinCalled = true;
    }

    public void onAssetDelete(AssetItem item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onPackageCreate(PackageItem item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
