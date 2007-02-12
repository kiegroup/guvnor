package org.drools.brms.client.rulelist;

/**
 * This is used by the list view to "open" an item.
 * @author Michael Neale
 */
public interface EditItemEvent {
    
    /**
     * @param key - the UUID to open.
     * @param type - the resource type.
     */
    public void open(String key);
    
}
