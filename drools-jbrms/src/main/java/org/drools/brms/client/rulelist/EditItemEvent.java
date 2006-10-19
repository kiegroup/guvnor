package org.drools.brms.client.rulelist;

/**
 * This is used by the list view to "open" an item.
 * @author Michael Neale
 */
public interface EditItemEvent {

    public void open(String key);
    
}
