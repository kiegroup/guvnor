package org.drools.brms.client.decisiontable;

import org.drools.brms.client.decisiontable.EditableDTGrid.RowClickListener;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This shows the widget for deleting a row.
 * @author Steven Williams
 */
public class DeleteAction extends Composite {

    private HorizontalPanel panel = new HorizontalPanel();
    private Image delete;
    private int row;
    

    
    
    /**
     * Pass in the click listener delegate for when the respective action is clicked
     * @param clickListener
     * @param okClickListener
     */
    public DeleteAction(final int currentRow, final RowClickListener clickListener) {
        row = currentRow;
        delete = new Image("images/clear_item.gif");
        delete.setTitle( "Delete this row" );
        delete.addClickListener( new ClickListener() {
			public void onClick(final Widget w) {
				clickListener.onClick(w, row);
			}
        });
        
        panel.add( delete);
        
        initWidget( panel );
    }
    
    public void setRow(final int row) {
    	this.row = row;
    }
    
    public int getRow() {
    	return row;
    }
    
}
