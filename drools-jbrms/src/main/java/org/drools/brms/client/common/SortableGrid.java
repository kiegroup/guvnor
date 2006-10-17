package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * This is the grid widget wrapper.
 * Main features are that it works off a data model, and 
 *
 */
public class SortableGrid extends Composite {

    ScrollPanel panel;
    FlexTable   table = new FlexTable();
    
    
    /**
     * @param data The first column is the key. The rest are the displayed data.
     * @param header This is obviously the header !
     */
    public SortableGrid(String[][] data, String[] header) {
        panel = new ScrollPanel();
        header( header );
        
        panel.setHeight( "50%" );
        data( data );
        
        panel.setScrollPosition( 50 );
        panel.setAlwaysShowScrollBars( true );
        panel.add( table );
        table.setWidth( "100%" );
        initWidget( panel );        
    }


    private void data(String[][] data) {
        for ( int i = 0; i < data.length; i++ ) {
            String[] row = data[i];
            int rowNumber = i + 1;
            table.setText( rowNumber, 0, row[0] );
            for ( int j = 1; j < row.length; j++ ) {
                table.setText( rowNumber, j, row[j] );
            }
        }
    }


    private void header(String[] header) {
        for ( int i = 0; i < header.length; i++ ) {
            Label head = new Label(header[i]);           
            table.setWidget( 0, i, head );          
        }
    }
    
    
    
}
