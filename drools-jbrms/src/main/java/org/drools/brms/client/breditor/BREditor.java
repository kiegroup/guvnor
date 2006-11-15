package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A basic DSL based BUSINESS rule editor.
 * 
 * This is the editor for "business" rules via a DSL.
 * This uses the EditableLine widget.
 */
public class BREditor extends Composite {

    final int DESC_COLUMN = 0; // this column in the layout if for descriptions
    final int CONTENT_COLUMN = 1; //this displays the rule contents
    final int ACTION_COLUMN = 2; //this contains "action" buttons
    
    
    private final Panel panel;
    
    /** these lists contain the guts of the rule */
    private List lhs = new ArrayList(); //these will be populated with EditableLine widget
    private List rhs = new ArrayList();
    
    /** these lists contain the "popup data" for the content assistance */
    private List lhsSuggestions = new ArrayList();
    private List rhsSuggestions = new ArrayList();
    
    
    private FlexTable table = null;
    private boolean editMode = false;
    private ChoiceList lhsSuggestionPopup;
    private ChoiceList rhsSuggestionPopup;
    private Image addLhsPopupButton;
    private Image editButton;
    private Image addRhsPopupButton;
    
    private MetaData meta;
    
    
    public BREditor(RuleAsset asset) {
        this.meta = asset.metaData;
        panel = new VerticalPanel();
        
        
        initData();
        initEditorActions();        
        refreshLayoutTable();
        initWidget( panel );
        setWidth( "100%" );
        
    }

    private void initEditorActions() {
        //DSL suggestions/pick list
        lhsSuggestionPopup = getLHSChoiceList();
        rhsSuggestionPopup = getRhsChoiceList();

        
        //button to add a new item for lhs (using the choice list).
        addLhsPopupButton = new Image("images/new_item.gif");
        addLhsPopupButton.addClickListener( new ClickListener() {
            public void onClick(Widget sender) {
                int left = sender.getAbsoluteLeft() - 40;
                int top = sender.getAbsoluteTop() + 10;
                lhsSuggestionPopup.setPopupPosition( left, top );
                lhsSuggestionPopup.show();                
            }            
        });
        addLhsPopupButton.setVisible( false );
        
        //button to toggle edit mode
        editButton = new Image("images/edit.gif");
        editButton.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                switchModes(lhs, editMode);
                switchModes(rhs, editMode);
                showHideLineEditorWidgets( editMode );                
                editMode = !editMode;
                meta.dirty = true;
            }            
        });        
        
        //the new button for the RHS
        addRhsPopupButton = new Image("images/new_item.gif");
        addRhsPopupButton.addClickListener( new ClickListener() {

            public void onClick(Widget sender) {
                int left = sender.getAbsoluteLeft() - 40;
                int top = sender.getAbsoluteTop() + 10;
                rhsSuggestionPopup.setPopupPosition( left, top );
                rhsSuggestionPopup.show();              
            }
            
        });
        addRhsPopupButton.setVisible( false );
    }

    /** This will populate and refresh the overall layout table. */
    private void refreshLayoutTable() {
        
        resetTableWidget();
        table.setText( 0, DESC_COLUMN, "IF" );
        
        table.setWidget( 0, ACTION_COLUMN, addLhsPopupButton );
        table.setWidget( 0, ACTION_COLUMN + 1, editButton );
        
        int rowOffset = 1;
        
        //setup LHS
        displayEditorWidgets( rowOffset, lhs );        
        rowOffset = lhs.size() + 1;        
        table.setText( rowOffset, DESC_COLUMN, "THEN" );
        

        table.setWidget( rowOffset, ACTION_COLUMN, addRhsPopupButton );
        
        rowOffset++;
        
        //setup RHS
        displayEditorWidgets( rowOffset, rhs );
    }

    private ChoiceList getRhsChoiceList() {
        final ChoiceList rhsSuggestionPopup = new ChoiceList(rhsSuggestions);
        rhsSuggestionPopup.setOKClickListener( new ClickListener() {
            public void onClick(Widget popup) {
                //need up add to the LHS list
                ChoiceList c = (ChoiceList) popup;
                EditableLine editLine = new EditableLine(c.getSelectedItem());
                if (editMode) editLine.makeEditable();
                
                rhs.add( editLine );
                refreshLayoutTable();
            }            
        } );
        return rhsSuggestionPopup;
    }

    private ChoiceList getLHSChoiceList() {
        final ChoiceList lhsSuggestionPopup = new ChoiceList(lhsSuggestions);
        lhsSuggestionPopup.setOKClickListener( new ClickListener() {
            public void onClick(Widget popup) {
                //need up add to the LHS list
                ChoiceList c = (ChoiceList) popup;
                EditableLine editLine = new EditableLine(c.getSelectedItem());
                if (editMode) editLine.makeEditable();
                
                lhs.add( editLine );
                refreshLayoutTable();
            }            
        } );
        return lhsSuggestionPopup;
    }
    

    private void switchModes(List list, boolean readOnly) {  
        
        
        for ( int i = 0; i < list.size(); i++ ) {
            EditableLine line = (EditableLine) list.get( i );
            if (readOnly) {                
                line.makeReadOnly();
            } else { 
                line.makeEditable();            
            }
        }
        
    }

    /** Switch all the line editor widgets on or off */
    private void showHideLineEditorWidgets(boolean readOnly) {
        
        this.addLhsPopupButton.setVisible(!readOnly);
        this.addRhsPopupButton.setVisible( !readOnly );
        
        for (int row = 1; row <= lhs.size(); row++ ) {
            showHideLineEditorWidget( readOnly, row );
        }
        int rhsStart = lhs.size() + 2;
        int rhsEnd = rhs.size() + rhsStart;
        for (int row = rhsStart; row < rhsEnd; row++ ) {
            showHideLineEditorWidget( readOnly, row );
        }        
    }

    /** Show or hide all the widgets for a line */
    private void showHideLineEditorWidget(boolean readOnly, int row) {
        Image img = (Image) table.getWidget( row, ACTION_COLUMN );
        img.setVisible( !readOnly );
        img = (Image) table.getWidget( row, ACTION_COLUMN + 1 );
        img.setVisible( !readOnly );
        img = (Image) table.getWidget( row, ACTION_COLUMN + 2 );
        img.setVisible( !readOnly );
    }

    /** 
     * This processes the individual LHS or RHS items. 
     */
    private void displayEditorWidgets(int rowOffset, final List dataList) {
        final BREditor editor = this;
        for ( int i = 0; i < dataList.size(); i++ ) {
            EditableLine w = (EditableLine) dataList.get( i );
            int row = i + rowOffset;
            
            table.setWidget( row, CONTENT_COLUMN, w );  
            
            Image removeButton = new Image("images/clear_item.gif");
            Image shuffleUpButton = new Image("images/shuffle_up.gif");
            Image shuffleDownButton = new Image("images/shuffle_down.gif");
            removeButton.setVisible( editMode);
            shuffleUpButton.setVisible( editMode );
            shuffleDownButton.setVisible( editMode );
            table.setWidget( row, ACTION_COLUMN, removeButton );
            table.setWidget( row, ACTION_COLUMN + 1, shuffleUpButton);
            table.setWidget( row, ACTION_COLUMN + 2, shuffleDownButton );
            final int idx = i;

            

            removeButton.addClickListener( new ClickListener()  {
                public void onClick(Widget wid) {
                  dataList.remove( idx );
                  editor.refreshLayoutTable();
                }
                
            });
            
            //setup shuffle button up
            shuffleUpButton.addClickListener( new ClickListener() {
                public void onClick(Widget wid) {
                    shuffle( dataList, idx, true );
                    editor.refreshLayoutTable();
                }
            });

            //setup shuffle button down
            shuffleDownButton.addClickListener( new ClickListener() {
                public void onClick(Widget wid) {
                    shuffle( dataList, idx, false );
                    editor.refreshLayoutTable();
                }
            });
            
            
        }
    }
 

    private void resetTableWidget() {
        //remove old if refreshing
        if (table != null) {
            panel.remove( table );
        }
        
        //now add the new
        table = new FlexTable();
        table.setWidth( "100%" );
        
        table.setStyleName( "rule-breditor-Table" );
        panel.add( table );
    }

    /**
     * This will setup the data
     * TODO: this is only mockup data.
     * TODO: When populading the lhs and rhs lists, need to have "{" stuffed in there
     * so that the text fields will be created for you. When reading the value out of the 
     * EditableLine, they will be removed (as they won't actually show up on screen, 
     * they are just place holders).
     */
    private void initData() {

        //the suggestion data
        lhsSuggestions.add("There is a Driver");
        lhsSuggestions.add("- age less than {age} years old");
        lhsSuggestions.add("- age greater than {age} years old");
        lhsSuggestions.add("- has had more than {number} prior claims");
        lhsSuggestions.add("- has a location risk profile of '{risk}'");
        lhsSuggestions.add("- age is at least {age}");
        lhsSuggestions.add("- age is between {lower} and {upper} years old");
        lhsSuggestions.add("- has had exactly {number} prior claims");
        lhsSuggestions.add("Policy type is '{type}'");
        lhsSuggestions.add("Policy has not been rejected");
        
        rhsSuggestions.add("Reject Policy with explanation : '{reason}'");        
        rhsSuggestions.add("Approve Policy with the reason : '{reason}'");                
        rhsSuggestions.add( "Log '{message}'" );
        
        //now the actual data, which in reality would have to be parsed out
//        lhs.add( new EditableLine("this is a {bam}"));
//        lhs.add( new EditableLine("this is a {bam}"));
//        rhs.add( new EditableLine("this is a {bam}"));
        
    }

    /** Adjust items up and down in a list.
     * 
     * @param lst The list to adjust.
     * @param idx The item to move.
     * @param up The direction to move (true == up, false == down ).
     */
    public static void shuffle(List lst,
                              int idx, boolean up) {        
        int targetIdx;
        if (up) {
            targetIdx = idx - 1;
        } else {
            targetIdx = idx + 1;
        }        
        
        if (targetIdx < 0 || targetIdx >= lst.size()) {
            return;
        }
        Object target = lst.get( targetIdx );
        Object source = lst.get( idx );
        
        lst.set(  targetIdx, source );
        lst.set( idx, target );        
    }


    
}
