package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the editor for "business" rules via a DSL.
 * This uses the EditableLine widget.
 */
public class BREditor extends Composite {

    final int DESC_COLUMN = 0; // this column in the layout if for descriptions
    final int CONTENT_COLUMN = 1; //this displays the rule contents
    final int ACTION_COLUMN = 2; //this contains "action" buttons
    
    
    private Panel panel;
    private List lhs = new ArrayList(); //these will be populated with EditableLine widget
    private List rhs = new ArrayList();
    private FlexTable table = null;
    
    public BREditor() {
        panel = new VerticalPanel();
        
        initData();
        refreshLayoutTable();
        
        initWidget( panel );
    }

    /** This will populate and refresh the overall layout table. */
    private void refreshLayoutTable() {
        resetTableWidget();
        table.setText( 0, DESC_COLUMN, "IF" );
        
        //DSL suggestions/pick list
        final ChoiceList listPopup = new ChoiceList(new ClickListener() {
            public void onClick(Widget popup) {
                //need up add to the LHS list
                ChoiceList c = (ChoiceList) popup;
                lhs.add( new Label(c.getSelectedItem()) );
                refreshLayoutTable();
            }            
        });
        
        //button to add a new item (using the choice list).
        Image addLhs = new Image("images/new_item.gif");
        addLhs.addClickListener( new ClickListener() {
            public void onClick(Widget sender) {
                int left = sender.getAbsoluteLeft() + 10;
                int top = sender.getAbsoluteTop() + 10;
                listPopup.setPopupPosition( left, top );
                listPopup.show();                
            }            
        });
        
        //button to toggle edit mode
        Image edit = new Image("images/edit.gif");
        edit.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                switchModes();
            }
            
        });
        
        
        
        table.setWidget( 0, ACTION_COLUMN, addLhs );
        table.setWidget( 0, ACTION_COLUMN + 1, edit );
        
        
        int rowOffset = 1;
        
        //setup LHS
        displayEditorWidgets( 
                         rowOffset, lhs );
        
        rowOffset = lhs.size() + 1;
        
        table.setText( rowOffset, DESC_COLUMN, "THEN" );
        
       
        table.setWidget( rowOffset, ACTION_COLUMN, new Image("images/new_item.gif") );
        
        rowOffset++;
        
        //setup RHS
        displayEditorWidgets( rowOffset, rhs );


    }
    
    private void switchModes() {
        
    }

    /** This processes the individual LHS or RHS items */
    private void displayEditorWidgets(int rowOffset, final List dataList) {
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
                    editor.refreshLayoutTable();
                }
                
            });            
            table.setWidget( row, ACTION_COLUMN, removeButton );
        }
    }

    private void resetTableWidget() {
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
        
        lhs.add( new EditableLine(new Label[] {new Label("Hello this is {foo}")}));
        lhs.add( new EditableLine(new Label[] {new Label("Hello this is {foo}")}));
        rhs.add( new EditableLine(new Label[] {new Label("Hello this is {foo}")}));
    }
    
}
