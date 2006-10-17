package org.drools.brms.client.categorynav;

/**
 * This represents an event of a category being selected.
 * This means the category widget can be used in several different places.
 * @author Michael Neale
 */
public interface CategorySelectHandler {

    /**
     * When a category is selected.
     */
    public void selected(String selectedPath);
    
}
