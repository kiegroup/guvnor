package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * This is a viewer/editor for categories.
 * It will show a list of categories currently applicable, and allow you to 
 * remove/add to them.
 * @author Michael Neale
 */
public class CategoryViewer extends Composite {

    private MetaData data;
    private HorizontalPanel panel = new HorizontalPanel();
    
    public CategoryViewer(MetaData d) {
        this.data = d;
        ListBox box = new ListBox();
        
        box.setMultipleSelect( true );
        loadData( box );
        
        initWidget( panel );
        
    }

    private void loadData(ListBox box) {
        for ( int i = 0; i < data.categories.length; i++ ) {
            box.addItem( data.categories[i] );
        }
    }
    
}
