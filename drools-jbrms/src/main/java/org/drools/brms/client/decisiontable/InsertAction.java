package org.drools.brms.client.decisiontable;

import org.drools.brms.client.decisiontable.EditableDTGrid.RowClickListener;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This shows the widget for inserting a row.
 * @author Steven Williams
 */
public class InsertAction extends Composite {

    private HorizontalPanel panel = new HorizontalPanel();
    private Image insert;
    private int row;
    

    
    
    /**
     * Pass in the click listener delegate for when the respective action is clicked
     * @param clickListener
     * @param okClickListener
     */
    public InsertAction(final int currentRow, final RowClickListener clickListener) {
        row = currentRow;
        insert = new Image("images/new_item.gif");
        insert.setTitle( "Insert row after row " + row );
        insert.addClickListener( new ClickListener() {
			public void onClick(final Widget w) {
				clickListener.onClick(w, row);
			}
        });
        
        panel.add( insert);
        
        initWidget( panel );
    }
    
    public void setRow(final int row) {
    	this.row = row;
    	insert.setTitle( "Insert row after row " + row );
    	
    }
    
    public int getRow() {
    	return row;
    }
    
}
