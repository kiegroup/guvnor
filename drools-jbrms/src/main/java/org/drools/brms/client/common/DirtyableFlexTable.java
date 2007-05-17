package org.drools.brms.client.common;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class DirtyableFlexTable extends FlexTable implements DirtyableContainer {
    
    private int length; 
    private ArrayList list = new ArrayList();

    public boolean hasDirty() {
        
        Pair coordinates;
        Widget element;
        
        for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
            coordinates = (Pair) iter.next();
            element =  (Widget) getWidget( coordinates.getRow(), coordinates.getColumn() );
            if (element instanceof DirtyableWidget) 
                if ( ((DirtyableWidget) element).isDirty() ) return true;
            if (element instanceof DirtyableContainer)
                if ( ((DirtyableContainer) element).hasDirty()) return true;
        }
        return false;
    }
    
    public void setWidget(int row, int column , Widget arg2) {
        super.setWidget( row, column, arg2 );
        
        if (( arg2 instanceof IDirtyable ))  {
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