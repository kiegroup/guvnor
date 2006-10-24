package org.drools.brms.client.decisiontable;

import org.drools.brms.client.decisiontable.EditableDTGrid.RowClickListener;

/**
 * This shows the widget for moving a row up.
 * @author Steven Williams
 */
public class ShuffleUpAction extends ShuffleAction {

    /**
     * Pass in the click listener delegate for when the respective action is clicked
     * @param clickListener
     * @param okClickListener
     */
    public ShuffleUpAction(final int currentRow, final RowClickListener clickListener) {
    	super(currentRow, clickListener, "up");
    }
    
}
