package org.drools.brms.client.decisiontable;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This shows the widget for moving a row up or down.
 * @author Steven Williams
 */
public class Toolbar extends Composite {

    private HorizontalPanel toolbar = new HorizontalPanel();
    private int row;
    
    public Toolbar() {
    	initWidget(toolbar);
    }
    /**
     * Pass in the click listener delegate for when the respective action is clicked and the
     * direction to move the row
     * @param currentRow
     * @param clickListener
     * @param direction
     */
    public Toolbar(final EditableDTGrid grid) {
		Image insert = new Image("images/new_item.gif");
		insert.addClickListener(new ClickListener() {

			public void onClick(Widget w) {
				grid.insertRow();
			}
		});
		toolbar.add(insert);
        Image delete = new Image("images/clear_item.gif");
        delete.setTitle( "Delete row" );
		delete.addClickListener(new ClickListener() {
			
			public void onClick(Widget w) {
				grid.deleteRow();
			}
		});
		toolbar.add(delete);
		Image moveUp = new Image("images/shuffle_up.gif");
		moveUp.addClickListener(new ClickListener() {
			
			public void onClick(Widget w) {
				grid.moveUp();
			}
		});
		toolbar.add(moveUp);
		Image moveDown = new Image("images/shuffle_down.gif");
		toolbar.add(moveDown);
		moveDown.addClickListener(new ClickListener() {

			public void onClick(Widget w) {
				grid.moveDown();
			}});
		toolbar.setStyleName("dt-editor-Toolbar");
        
        initWidget( toolbar );
    }
    
    public void setRow(final int row) {
    	this.row = row;
    }
    
    public int getRow() {
    	return row;
    }
    
}
