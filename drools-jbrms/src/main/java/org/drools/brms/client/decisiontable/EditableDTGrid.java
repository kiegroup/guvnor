package org.drools.brms.client.decisiontable;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * The decision table viewer and editor.
 * @author Michael Neale
 * @author Stephen Williams
 * 
 * TODO: add editors for header stuff,
 * and ability to add rows/cols and shift rows around
 * This probably can be done from a seperate "editor" such that it is re-rendered 
 * when you need to add/move a col or row.
 * 
 * Should be able to add/shift stuff around by entering a row number to deal with.
 * 
 */
public class EditableDTGrid extends Composite {

    private static final int START_DATA_ROW = 1;
    private FlexTable table = new FlexTable();

    public EditableDTGrid(String dtName) {

//for if I switch to a Grid
//        table.resizeColumns( numCols() + 1 );
//        table.resizeRows( numRows() );

        VerticalPanel vert = new VerticalPanel();
        
        Label title = new Label(dtName);
        title.setStyleName( "dt-editor-Title" );
        
        HorizontalPanel header = new HorizontalPanel();
        header.add( new Image("images/decision_table.gif") );
        header.add( title );
        
        vert.add( header );
        vert.add( table );
        
        FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
        
        table.setStyleName( "dt-editor-Grid" );

        //set up the header
        populateHeader( cellFormatter );
        
        //and the data follows
        populateDataGrid( cellFormatter );
        
        //and this is how you span/merge things, FYI
        //table.getFlexCellFormatter().setColSpan( 2, 3, 4 );

        //needed for Composite
        initWidget( vert );
    }


    private void populateHeader(FlexCellFormatter cellFormatter) {
        
        //for the count column
        cellFormatter.setStyleName( 0, 0, "dt-editor-DescriptionCell" );
        
        for (int col = 1; col < numCols() + 1; col++) {
            table.setText( 0, col, "some header " + col );
            cellFormatter.setStyleName( 0, col, "dt-editor-DescriptionCell" );
        }
    }


    /**
     * This populates the "data" part of the decision table (not the header bits).
     * It starts at the row offset. 
     * @param cellFormatter So it can set the style of each cell that is created.
     */
    private void populateDataGrid(FlexCellFormatter cellFormatter) {

        for ( int i = 0; i < numRows(); i++ ) {
            
            int rowCount = i + 1;
            
            int column = 1;
            int row = i + START_DATA_ROW;
            
            //now do the count column
            table.setText( row, 0, Integer.toString( rowCount) );
            cellFormatter.setStyleName( row, 0, "dt-editor-CountColumn" );
            for ( ; column < numCols() + 1; column++ ) {
                table.setText( row,
                               column,
                               "boo " + column );
                cellFormatter.setStyleName( row, column, "dt-editor-Cell" );
            }
                        
            final int currentRow = row;
            
            //the action magic
            final EditActions actions = new EditActions(new ClickListener() {
                    public void onClick(Widget w) {editRow( currentRow );}               
                },
                new ClickListener() {
                    public void onClick(Widget w) {updateRow( currentRow );}                
                });
            table.setWidget( currentRow, column, actions );
            
        }
    }
    
    /**
     * Apply the changes to the row.
     */
    private void updateRow(int row) {
        for (int column = 1; column < numCols() + 1; column++) {
            TextBox text = (TextBox) table.getWidget( row, column );
            table.setText( row, column, text.getText() );                
        }
    }


    /**
     * This switches the given row into edit mode.
     * @param row
     */
    private void editRow(int row) {
        for (int column = 1; column < numCols() + 1; column++) {
            String text = table.getText( row, column );
                TextBox box = new TextBox();
                box.setText( text );
                box.setStyleName( "dsl-field-TextBox" );
                box.setVisibleLength( 3 );                
                table.setWidget( row, column, box );   
                
        }
    }

    private int numCols() {
        return 6;
    }

    private int numRows() {
        return 14;
    }

}
