package org.drools.guvnor.client.decisiontable.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A Cell that renders it's corresponding row index number only
 * 
 * @author manstis
 * 
 */
public class RowNumberCell extends AbstractCell<Integer> {

    public RowNumberCell() {
        // Good citizen: AbstractCell does not initialise an empty set of
        // consumed events
        super( "" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client
     * .Cell.Context, java.lang.Object,
     * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
     */
    @Override
    public void render(Context context,
                       Integer value,
                       SafeHtmlBuilder sb) {
        sb.append( value );
    }

}
