package org.drools.brms.client.common;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class DirtableFlexTable extends FlexTable {
    
    private int length; 
    private ArrayList list = new ArrayList();

    public boolean hasDirty() {
        
        //Pair coordinates;
        DirtableComposite element;
        
        for ( Object coordinates : list ) {
            element = (DirtableComposite) getWidget( ( (Pair) coordinates).getRow(), ( (Pair) coordinates).getColumn() );
            if ( element.isDirty() ) return true;
        }
        return false;
    }
    
    public void setWidget(int row, int column , Widget arg2) {
        super.setWidget( row, column, arg2 );
        
        if ( arg2 instanceof DirtableComposite ) {
            list.add( length++, new Pair(row ,column) );
            
        }
    }
}

class Pair {
    private int row;
    private int column;
    
    public Pair(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
    
    
    
}