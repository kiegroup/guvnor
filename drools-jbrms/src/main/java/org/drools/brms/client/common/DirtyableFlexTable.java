package org.drools.brms.client.common;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class DirtyableFlexTable extends FlexTable {
    
    private int length; 
    private ArrayList list = new ArrayList();

    public boolean hasDirty() {
        
        Pair coordinates;
        DirtableComposite element;
        
        for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
            coordinates = (Pair) iter.next();
            element = (DirtableComposite) getWidget( coordinates.getRow(), coordinates.getColumn() );
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