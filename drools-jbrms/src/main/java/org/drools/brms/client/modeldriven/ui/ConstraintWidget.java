package org.drools.brms.client.modeldriven.ui;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This contains all the constraints that are placed on a fact class.
 * 
 * @author Michael Neale
 */
public class ConstraintWidget extends Composite {

    private FlexTable outer;
    public FlexTable layout;
    private List rowData = new ArrayList();
    private final SuggestionCompletionEngine completions;
    private String factType;
    
    
    /**
     * 
     * @param factType The type of the fact (needed for lists)
     * @param com The suggestion completion.
     * @param constraints A list of ConstraintAtom's, which apply to the fact.
     */
    public ConstraintWidget(String factType, SuggestionCompletionEngine com, List constraints) {
        this.completions = com;
        this.factType = factType;
        this.rowData = constraints;
        outer = new FlexTable();
        doTable();
        
        
        
        initWidget( outer );
    }
    
    
    
    private void doTable() {
        layout = new FlexTable();
        outer.setWidget( 0, 0, layout);
        
        //firstly to add a row
        Image add = new Image("images/new_item.gif");
        add.setTitle( "Add a new constraint on this fact." );
        add.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                rowData.add( new ConstraintAtom() );
                doTable();                
            }            
        });
        
        int addRow = rowData.size() + 1;
        layout.setWidget( addRow, 0, add );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setColSpan( addRow, 0, 5 );
        formatter.setHorizontalAlignment( addRow, 0, HasHorizontalAlignment.ALIGN_CENTER );
        
        
        //now to do the rows:
        for ( int i = 0; i < rowData.size(); i++ ) {
            ConstraintAtom row = (ConstraintAtom) rowData.get( i );
            layout.setWidget( i, 0, doFieldDropDown(row, i));
            layout.setWidget( i, 1, doOperatorDropDown(row) );
            layout.setWidget( i, 2, doValueEditor(row) );
            //TODO: add in connectives here...
            
            Image remove = new Image("images/clear_item.gif");
            final int idx = i;
            remove.addClickListener( new ClickListener() {

                public void onClick(Widget w) {
                    rowData.remove( idx );
                    doTable();
                }
                
            });
            layout.setWidget( i, 4, remove );
        }
        
        
    }



    private Widget doValueEditor(final ConstraintAtom row) {
        final TextBox text = new TextBox();
        text.setText( row.value );
        text.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                row.value = text.getText();
            }            
        });
        return text;
    }



    /**
     * Builds the list of valid operators based on the field selected.
     */
    private ListBox doOperatorDropDown(final ConstraintAtom row) {
        final ListBox box = new ListBox();
        String[] oprs = completions.getOperatorCompletions( factType, row.field.field );
        if (oprs != null) {
            for ( int i = 0; i < oprs.length; i++ ) {
                box.addItem( oprs[i] );
                if (oprs[i].equals( row.operator )) {
                    box.setSelectedIndex( i );
                }

            }
            
            box.addChangeListener( new ChangeListener() {
                public void onChange(Widget w) {
                    row.operator = box.getItemText( box.getSelectedIndex() );                
                }            
            });
        
        }
        
        if (row.operator == null) {
            box.addItem( "..." );  
            
        }
        
        return box;
    }



    /** 
     * Creates the field list drop down, and selects the item.
     */
    private ListBox doFieldDropDown(final ConstraintAtom row, final int rowNumberInLayout) {
        
        final ListBox box = new ListBox();
        
        String[] com = completions.getFieldCompletions( factType );
        for ( int i = 0; i < com.length; i++ ) {
            box.addItem( com[i] );
            if (com[i].equals( row.field.field )) {
                box.setSelectedIndex( i );
            }

        }
        
        box.addChangeListener( new ChangeListener() {

            public void onChange(Widget w) {
                row.field.field = box.getItemText( box.getSelectedIndex() );    
                layout.setWidget( rowNumberInLayout, 1, doOperatorDropDown( row ) );
            }
            
        });
        
        if (row.field.field == null) {
            box.addItem( "..." );
        }
        
        return box;
        
    }


    /**
     * These classes are for containing the values.
     */
    class ConstraintAtom {
        public ConstraintField field = new ConstraintField();
        public String operator;
        public String value;
        public ConnectiveConstraint[] connectives;         
    }
    
    class ConstraintField {
        public String field;
        public String boundVariableName;
    }
    
    class ConnectiveConstraint {
        public String operator;
        public String value;
    }
    
}
