package org.drools.brms.client.rulelist;

import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.table.SortableTable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This is a viewer for viewing a list of rules for editing/selection.
 */
public class NewView extends Composite {
    
    private FlexTable outer = new FlexTable();
    private SortableTable table;
    private TableConfig config;
    
    public NewView(TableConfig conf) {
    
        FlexCellFormatter formatter = outer.getFlexCellFormatter();
        this.config = conf;
        //outer.setStyleName( SortableTable.styleList );
        outer.setWidth( "100%" );
        
        outer.setWidget( 0, 0, new Label("left") );        
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE );
        
        outer.setWidget( 0, 1, new Label("right") );
        formatter.setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE );
                
        
        table = new SortableTable(50, conf.headers.length + 1);
        table.setHiddenColumn( 0 );
        table.addColumnHeader( "", 0 );
        table.setWidth( "100%" );
        
        for ( int i = 0; i < conf.headers.length; i++ ) {
            table.addColumnHeader( conf.headers[i], i + 1 );
        }

        ScrollPanel scroll = new ScrollPanel();
        
        scroll.setScrollPosition( 500 );
        scroll.add( table );
        
        outer.setWidget( 1, 0, scroll );
        formatter.setColSpan( 1, 0, 2 );
       
        initWidget( outer );
        
    }

}
