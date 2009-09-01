package org.drools.repository.events;

import org.drools.repository.AssetItem;

/**
 * To be called after an asset is checked in. 
 * @author Michael Neale
 */
public interface CheckinEvent {
    public void afterCheckin(AssetItem item);
}
