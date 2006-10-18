package org.drools.brms.client.decisiontable;

import org.drools.brms.client.decisiontable.EditableDTGrid.RowClickListener;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This shows the widgets for editing a row.
 * @author Michael Neale
 */
public class EditActions extends Composite {

    private HorizontalPanel panel = new HorizontalPanel();
    private Image edit;
    private Image ok;
	private int row;
    

    
    
    /**
     * Pass in the click listener delegates for when the respective action is clicked
     * @param editClickListener
     * @param okClickListener
     */
    public EditActions(final int currentRow,
    				   final RowClickListener editClickListener, 
                       final RowClickListener okClickListener) {
        row = currentRow;
        edit = new Image("images/edit.gif");
        edit.setTitle( "Edit this row" );
        edit.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                makeEditable();
                editClickListener.onClick( w, row );
            }
            
        });
        
        ok = new Image("images/tick.gif");
        ok.setTitle( "Apply the edit changes to this row." );
        ok.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                makeReadOnly();
                okClickListener.onClick( w, row );                
            }
            
        });
        
        panel.add( edit );
        panel.add( ok );
        ok.setVisible( false );
        
        initWidget( panel );
    }
    
    public void makeEditable() {
       edit.setVisible( false );
       ok.setVisible( true );

    }
    
    public void makeReadOnly() {
        edit.setVisible( true );
        ok.setVisible( false );
    }

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
    
    
    
}
