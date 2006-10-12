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

/**
 * The decision table viewer and editor.
 * @author Michael Neale
 * @author Stephen Williams
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
        
        //and this is how you span things, FYI
        //table.getFlexCellFormatter().setColSpan( 2, 3, 4 );

        
        
        initWidget( vert );

    }


    private void populateHeader(FlexCellFormatter cellFormatter) {
        for (int col = 0; col < numCols(); col++) {
            table.setText( 0, col, "some header " + col );
            cellFormatter.setStyleName( 0, col, "dt-editor-DescriptionCell" );
        }
    }


    private void populateDataGrid(FlexCellFormatter cellFormatter) {
        for ( int i = 0; i < numRows(); i++ ) {
            int column = 0;
            int row = i + START_DATA_ROW;
            for ( ; column < numCols(); column++ ) {
                table.setText( row,
                               column,
                               "boo " + column );
                cellFormatter.setStyleName( row, column, "dt-editor-Cell" );
            }
            
            final int currentRow = row;
            Image editButton = new Image("images/edit.gif");
            editButton.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    editRow(currentRow);
                    
                }
            });   
            
            
            table.setWidget( row, column, editButton );
            
        }
    }
    
    
    private void editRow(int row) {
        for (int column = 0; column < numCols(); column++) {
            String text = table.getText( row, column );
            Widget w = table.getWidget( row, column );
            if (w == null) {
                TextBox box = new TextBox();
                box.setText( text );
                box.setStyleName( "dsl-field-TextBox" );
                box.setVisibleLength( 3 );
                
                table.setWidget( row, column, box );
                
            } else {
                table.setText( row, column, ((TextBox ) w).getText());
            }
        }
        
        
    }

    private int numCols() {
        return 7;
    }

    private int numRows() {
        return 12;
    }

}
