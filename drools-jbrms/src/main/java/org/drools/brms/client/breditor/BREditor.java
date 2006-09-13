package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the editor for "business" rules via a DSL.
 */
public class BREditor extends Composite {

    final int DESC_COLUMN = 0;
    final int CONTENT_COLUMN = 1;
    final int ACTION_COLUMN = 2;
    
    
    private Panel panel;
    private List lhs = new ArrayList();
    private List rhs = new ArrayList();
    private FlexTable table = null;
    
    public BREditor() {
        
        panel = new VerticalPanel();
        
        initData();
        populateTable();
        
        
        
        initWidget( panel );
    }

    /** This will populate and refresh the table */
    private void populateTable() {
        refreshTableOnPanel();
        table.setText( 0, DESC_COLUMN, "IF" );
        
        final ChoiceList listPopup = new ChoiceList(new ClickListener() {
            public void onClick(Widget popup) {
                //need up add to the LHS list
                ChoiceList c = (ChoiceList) popup;
                lhs.add( new Label(c.getSelectedItem()) );
                populateTable();
            }            
        });
        
        Button addLhs = new Button("+");
        addLhs.addClickListener( new ClickListener() {
            public void onClick(Widget sender) {
                
                
                int left = sender.getAbsoluteLeft() + 10;
                int top = sender.getAbsoluteTop() + 10;
                listPopup.setPopupPosition( left,
                                    top );
                listPopup.show();                
            }            
        });
        
        table.setWidget( 0, ACTION_COLUMN, addLhs );
        
        
        int rowOffset = 1;
        
        //setup LHS
        populateContent( 
                         rowOffset, lhs );
        
        rowOffset = lhs.size() + 1;
        
        table.setText( rowOffset, DESC_COLUMN, "THEN" );
        
       
        table.setWidget( rowOffset, ACTION_COLUMN, new Button("+") );
        
        rowOffset++;
        
        //setup RHS
        populateContent( rowOffset, rhs );


    }

    private void populateContent(int rowOffset, final List dataList) {
        final BREditor editor = this;
        for ( int i = 0; i < dataList.size(); i++ ) {
            Widget w = (Widget) dataList.get( i );
            int row = i + rowOffset;
            table.setWidget( row, CONTENT_COLUMN, w );            
            Button removeButton = new Button("-");
            final int idx = i;
            removeButton.addClickListener( new ClickListener() {

                public void onClick(Widget but) {
                    dataList.remove( idx );
                    editor.populateTable();
                }
                
            });            
            table.setWidget( row, ACTION_COLUMN, removeButton );
        }
    }

    private void refreshTableOnPanel() {
        //remove old if refreshing
        if (table != null) {
            panel.remove( table );
        }
        
        //now add the new
        table = new FlexTable();
        table.setStyleName( "rule-breditor-Table" );
        panel.add( table );
    }

    private void initData() {
        
        lhs.add( new Label("Hello this is {foo}"));
        lhs.add( new Label("Hello this is {foo}"));
        rhs.add(new Label("panic all is lost") );
    }
    
}
