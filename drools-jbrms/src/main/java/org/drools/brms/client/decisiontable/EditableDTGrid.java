package org.drools.brms.client.decisiontable;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableDTGrid extends Composite {

    private Grid table = new Grid();

    public EditableDTGrid() {

        //for if I switch to a Grid
        table.resizeColumns( numCols() + 1 );
        table.resizeRows( numRows() );

        for ( int row = 0; row < numRows(); row++ ) {
            int column = 0;
            for ( ; column < numCols(); column++ ) {
                table.setText( row,
                               column,
                               "boo " + column );
            }
            final int currentRow = row;
            Image editButton = new Image("images/edit.gif");
            editButton.addClickListener( new ClickListener() {

                public void onClick(Widget w) {
                    editRow(currentRow);
                    
                }

                
            }) ;          
            table.setWidget( row, column, editButton );
            
        }

        initWidget( table );

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
        return 12;
    }

    private int numRows() {
        return 70;
    }

}
